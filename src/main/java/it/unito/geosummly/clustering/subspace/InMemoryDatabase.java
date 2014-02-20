package it.unito.geosummly.clustering.subspace;

import java.util.BitSet;
import java.util.Collection;

import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.AbstractDatabase;
import de.lmu.ifi.dbs.elki.database.ids.ArrayDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.relation.DBIDView;
import de.lmu.ifi.dbs.elki.database.relation.MaterializedRelation;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.bundle.MultipleObjectsBundle;
import de.lmu.ifi.dbs.elki.datasource.bundle.ObjectBundle;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.index.IndexFactory;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.statistics.Duration;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;

public class InMemoryDatabase extends AbstractDatabase implements Parameterizable{

    /**
     * Our logger
     */
    private static final Logging LOG = Logging.getLogger(InMemoryDatabase.class);

    /**
     * IDs of this database
     */
    private ArrayDBIDs ids;

    /**
     * The DBID representation we use
     */
    private DBIDView idrep;

    /**
     * The data source we get the initial data from.
     */
    protected DatabaseConnection databaseConnection;

    
    public InMemoryDatabase(DatabaseConnection databaseConnection, Collection<IndexFactory<?, ?>> indexFactories) {
        super();
        this.databaseConnection = databaseConnection;
        this.ids = null;
        this.idrep = null;

        // Add indexes.
        if (indexFactories != null) {
          this.indexFactories.addAll(indexFactories);
        }
      }

    
    /**
     * Initialize the database by getting the initial data from the database
     * connection.
     */
    @Override
    public void initialize() {
      if (databaseConnection != null) {
        if (LOG.isDebugging()) {
          LOG.debugFine("Loading data from database connection.");
        }
        MultipleObjectsBundle objpackages = databaseConnection.loadData();
        // Run at most once.
        databaseConnection = null;
       
        // Find DBID column
        int idrepnr = findDBIDColumn(objpackages);
        // Build DBID array
        if (idrepnr == -1) {
          this.ids = DBIDUtil.generateStaticDBIDRange(objpackages.dataLength());
        } else {
          final ArrayModifiableDBIDs newids = DBIDUtil.newArray(objpackages.dataLength());
          for (int j = 0; j < objpackages.dataLength(); j++) {
            DBID newid = (DBID) objpackages.data(j, idrepnr);
            newids.add(newid);
          }
          this.ids = newids;
        }
        // Replace id representation.
        // TODO: this is an ugly hack
        this.idrep = new DBIDView(this, this.ids);
        relations.add(this.idrep);
        getHierarchy().add(this, idrep);

        // insert into db - note: DBIDs should have been prepared before this!
        Relation<?>[] targets = alignColumns(objpackages);

        DBIDIter newid = ids.iter();
        for (int j = 0; j < objpackages.dataLength(); j++, newid.advance()) {
          // insert object
          for (int i = 0; i < targets.length; i++) {
            // DBIDs were handled above.
            if (i == idrepnr) {
              continue;
            }
            @SuppressWarnings("unchecked")
            final Relation<Object> relation = (Relation<Object>) targets[i];
            relation.set(newid, objpackages.data(j, i));
          }
        }

        for (Relation<?> relation : relations) {
          SimpleTypeInformation<?> meta = relation.getDataTypeInformation();
          // Try to add indexes where appropriate
          for (IndexFactory<?, ?> factory : indexFactories) {
            if (factory.getInputTypeRestriction().isAssignableFromType(meta)) {
              @SuppressWarnings("unchecked")
              final IndexFactory<Object, ?> ofact = (IndexFactory<Object, ?>) factory;
              @SuppressWarnings("unchecked")
              final Relation<Object> orep = (Relation<Object>) relation;
              final Index index = ofact.instantiate(orep);
              Duration duration = LOG.isStatistics() ? LOG.newDuration(index.getClass().getName() + ".construction") : null;
              if (duration != null) {
                duration.begin();
              }
              index.initialize();
              if (duration != null) {
                duration.end();
                LOG.statistics(duration);
              }
              addIndex(index);
            }
          }
        }

        // fire insertion event
        eventManager.fireObjectsInserted(ids);
      }
    }
    
    @Override
    public void addIndex(Index index) {
      if (LOG.isDebuggingFiner()) {
        LOG.debugFine("Adding index: " + index);
      }
      this.indexes.add(index);
      // TODO: actually add index to the representation used?
      this.addChildResult(index);
    }

    /**
     * Find an DBID column.
     * 
     * @param pack Package to process
     * @return DBID column
     */
    protected int findDBIDColumn(ObjectBundle pack) {
      for (int i = 0; i < pack.metaLength(); i++) {
        SimpleTypeInformation<?> meta = pack.meta(i);
        if (TypeUtil.DBID.isAssignableFromType(meta)) {
          return i;
        }
      }
      return -1;
    }

    /**
     * Find a mapping from package columns to database columns, eventually adding
     * new database columns when needed.
     * 
     * @param pack Package to process
     * @return Column mapping
     */
    protected Relation<?>[] alignColumns(ObjectBundle pack) {
      // align representations.
      Relation<?>[] targets = new Relation<?>[pack.metaLength()];
      BitSet used = new BitSet(relations.size());
      for (int i = 0; i < targets.length; i++) {
        SimpleTypeInformation<?> meta = pack.meta(i);
        // TODO: aggressively try to match exact metas first?
        // Try to match unused representations only
        for (int j = used.nextClearBit(0); j >= 0 && j < relations.size(); j = used.nextClearBit(j + 1)) {
          Relation<?> relation = relations.get(j);
          if (relation.getDataTypeInformation().isAssignableFromType(meta)) {
            targets[i] = relation;
            used.set(j);
            break;
          }
        }
        if (targets[i] == null) {
          targets[i] = addNewRelation(meta);
          used.set(relations.size() - 1);
        }
      }
      return targets;
    }

    /**
     * Add a new representation for the given meta.
     * 
     * @param meta meta data
     * @return new representation
     */
    private Relation<?> addNewRelation(SimpleTypeInformation<?> meta) {
      @SuppressWarnings("unchecked")
      SimpleTypeInformation<Object> ometa = (SimpleTypeInformation<Object>) meta;
      Relation<?> relation = new MaterializedRelation<>(this, ometa, ids);
      relations.add(relation);
      getHierarchy().add(this, relation);
      return relation;
    }
    

    @Override
    protected Logging getLogger() {
        return LOG;
    }

}
