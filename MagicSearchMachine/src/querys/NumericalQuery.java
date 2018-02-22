package querys;

import org.json.JSONObject;

public class NumericalQuery extends SearchQuery {
	static final int LESS_THAN = 0;
	static final int LESS_THAN_EQUAL = 1;
	static final int EQUAL = 2;
	static final int GREATER_THAN = 3;
	static final int GREATER_THAN_EQUAL = 4;

	String attribute;
	int value;
	int comparator;
	boolean positive;

	public NumericalQuery(String attribute, int value, String comparison, boolean positive) {
		this.attribute = attribute;
		this.value = value;
		this.comparator = parseComparator(comparison);
		this.positive = positive;
	}

	

	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has(attribute)) {
			int cardValue;
			if (card.get(attribute).toString().contains("*")) {
				cardValue = 0;
			} else if (card.get(attribute).toString().contains(".")) {
				// some un-set cards have decimal stats - we don't bother
				cardValue = 0;
			}else{
				// we have to always parse it because sometimes it is a string
				cardValue = Integer.parseInt(card.get(attribute).toString());
			}
			if (positive)
				return compareInts(cardValue, comparator, value);
			else
				return !compareInts(cardValue, comparator, value);
		} else
			return !positive;
	}

	
	private boolean compareInts(int i1, int comparison, int i2) {
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
	
	private int parseComparator(String comparison) {
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
