package it.unito.geosummly.experiments;

import java.util.Comparator;

public enum RecordComparator implements Comparator<Record> {
    LAT {
        public int compare(Record o1, Record o2) {
            return o1.getLat().compareTo(o2.getLat());
        }},
    LNG {
        public int compare(Record o1, Record o2) {
            return o1.getLng().compareTo(o2.getLng());
        }};

    public static Comparator<Record> descending(final Comparator<Record> other) {
        return new Comparator<Record>() {
			public int compare(Record o1, Record o2) {
				return -1 * other.compare(o1, o2);
			}
        };
    }
    public static Comparator<Record> ascending(final Comparator<Record> other) {
        return new Comparator<Record>() {
			public int compare(Record o1, Record o2) {
				return other.compare(o1, o2);
			}
        };
    }
    
    public static Comparator<Record> getComparator(final RecordComparator... multipleOptions) {
        return new Comparator<Record>() {
            public int compare(Record o1, Record o2) {
                for (RecordComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

}
