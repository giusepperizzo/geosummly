# CLI output

java -cp /opt/Program/elki.jar de.lmu.ifi.dbs.elki.application.KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in elki_subclu.txt -dbscan.epsilon 20 -dbscan.minpts 10 > /tmp/out
java -cp /opt/Program/elki.jar de.lmu.ifi.dbs.elki.application.KDDCLIApplication -algorithm clustering.subspace.SUBCLU -dbc.in elki_subclu.txt -subclu.epsilon 20 -subclu.minpts 10 > /tmp/out


# GUI
java -jar /opt/Program/elki.jar then play with it