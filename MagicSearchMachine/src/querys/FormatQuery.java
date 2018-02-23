package querys;

import java.util.Arrays;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

public class FormatQuery extends SearchQuery {

	String format;
	boolean positive;
	private Hashtable<String, String[]> loadedFormats;

	public FormatQuery(String format, boolean positive, Hashtable<String, String[]> loadedFormats) {
		this.format = format;
		this.positive = positive;
		this.loadedFormats = loadedFormats;
	}

	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has("legalities")) {
			JSONArray array = card.getJSONArray("legalities");

			if (loadedFormats.containsKey(format)) {
				String [] formatList = loadedFormats.get(format);
				String cardName = card.getString("name");
				if(Arrays.binarySearch(formatList, cardName)>=0)
					return positive;
			}

			// check for the existence of the attribute we want to find
			JSONObject testAttribute = null;
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("format").equalsIgnoreCase(format)) {
					testAttribute = array.getJSONObject(i);
					break;
				}
			}

			if (testAttribute == null) {
				return !positive;
			}

			if (positive)
				return testAttribute.getString("legality").equals("Legal");
			else
				return !testAttribute.getString("legality").equals("Legal");
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}
}
