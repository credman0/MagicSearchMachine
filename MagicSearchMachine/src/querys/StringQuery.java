package querys;

import org.json.JSONObject;

public class StringQuery extends SearchQuery {

	String attribute;
	String value;
	boolean positive;

	public StringQuery(String attribute, String value, boolean positive) {
		this.attribute = attribute;
		this.value = value;
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has(attribute)) {
			String cardValue = card.getString(attribute);
			String testValue = value.toLowerCase().replaceAll( "~", card.getString("name").toLowerCase());
			if (positive)
				return cardValue.toLowerCase().contains(testValue);
			else
				return !cardValue.toLowerCase().contains(testValue);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}
}
