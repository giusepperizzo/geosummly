package it.unito.geosummly.io;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

class WKTType extends BaseDatatype {
    public static final String theTypeURI = "http://www.opengis.net/ont/geosparql#wktLiteral";
    public static final RDFDatatype type = new WKTType();

    /** private constructor - single global instance */
    private WKTType() {
        super(theTypeURI);
    }

    /**
     * Convert a value of this datatype out
     * to lexical form.
     */
    public String unparse(Object value) {
        return value.toString();
    }

    /**
     * Parse a lexical form of this datatype to a value
     * @throws DatatypeFormatException if the lexical form is not legal
     */
    public Object parse(String lexicalForm) throws DatatypeFormatException {
        return lexicalForm;
    }

    /**
     * Compares two instances of values of the given datatype.
     * This does not allow rationals to be compared to other number
     * formats, Lang tag is not significant.
     */
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        return value1.getDatatype() == value2.getDatatype()
             && value1.getValue().equals(value2.getValue());
    }
}