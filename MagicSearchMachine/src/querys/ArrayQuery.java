package querys;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArrayQuery extends SearchQuery {

	String attribute;
	String value;
	boolean positive;

	public ArrayQuery(String attribute, String value, boolean positive) {
		this.attribute = attribute;
		this.value = value;
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has(attribute)) {
			JSONArray cardTestArray = card.getJSONArray(attribute);
			for (int i = 0; i < cardTestArray.length(); i++){
				if (cardTestArray.getString(i).equalsIgnoreCase(value))
				return positive;
			}

			return !positive;

		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}

}
