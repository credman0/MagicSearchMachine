package com.querys;

public abstract class NumericalQuery extends SearchQuery {
	static final int LESS_THAN = 0;
	static final int LESS_THAN_EQUAL = 1;
	static final int EQUAL = 2;
	static final int GREATER_THAN = 3;
	static final int GREATER_THAN_EQUAL = 4;
	
	protected int value;
	protected int comparator;
	protected boolean positive;

	public NumericalQuery(int value, String comparison, boolean positive) {
		this.value = value;
		this.comparator = parseComparator(comparison);
		this.positive = positive;
	}

	
	protected boolean compareInts(int i1, int comparison, int i2) {
		switch (comparison) {
		case (LESS_THAN):
			return i1 < i2;
		case (LESS_THAN_EQUAL):
			return i1 <= i2;
		case (EQUAL):
			return i1 == i2;
		case (GREATER_THAN):
			return i1 > i2;
		case (GREATER_THAN_EQUAL):
			return i1 >= i2;
		default:
			throw new IllegalStateException("Comparison improperly set");
		}

	}
	
	protected int parseComparator(String comparison) {
		switch (comparison) {
		case "<":
			return LESS_THAN;
		case "<=":
			return LESS_THAN_EQUAL;
		case "=":
			return EQUAL;
		case ">":
			return GREATER_THAN;
		case ">=":
			return GREATER_THAN_EQUAL;
		default:
			throw new IllegalArgumentException("Unknown comparison operator: " + comparison);
		}
	}
}
