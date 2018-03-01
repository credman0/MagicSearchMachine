package querys;

import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManaQuery extends SearchQuery {
	
	Character[] colors;
	boolean positive;
	final static char[] COLOR_ORDER = {'w', 'u', 'b', 'r', 'g'}; 	

	public ManaQuery(String value, boolean positive) {
		// possibilitiy for multiple single letter colors
		colors = new Character[value.length()];
		for (int i  = 0; i < value.length(); i++) {
			colors[i] = value.charAt(i);
		}
		Arrays.sort(colors, new Comparator<Character>() {

			@Override
			public int compare(Character o1, Character o2) {
				// we want to align the colors WUBRG
				for (char c:COLOR_ORDER) {
					if (o1==c) {
						if (o2==c) {
							// they are the same
							return 0;
						}else {
							//o1 came first
							return -1;
						}
					}else if (o2==c) {
						// o1 was not here, o2 came second
						return 1;
					}
				}
				return -1;
			}
			
		});
		this.positive = positive;
	}
	
	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has("colorIdentity")) {
			JSONArray cardTestArray = card.getJSONArray("colorIdentity");
			if (cardTestArray.length()!=colors.length) {
				return !positive;
			}
			// we trust that the colors are sorted WUBRG
			for (int i = 0; i < colors.length; i++){
				if (cardTestArray.getString(i).toLowerCase().charAt(0)!=colors[i]) {
					return !positive;
				}			
			}
			return positive;

		} else {
			if (colors[0]=='c') {
				return positive;
			}
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}

}
