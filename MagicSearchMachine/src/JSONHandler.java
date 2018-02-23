import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import querys.QueryParser;
import querys.SearchQuery;

public class JSONHandler {
	public static final String CARDJSON_NAME = "AllCards-x.json";
	public static final String SETJSON_NAME = "AllSets-x.json";
	private JSONObject cardJson;
	private JSONObject setJson;
	private QueryParser queryParser = new QueryParser();
	
	public void initilizeCardJSON(){
		cardJson = loadJson(CARDJSON_NAME);
		setJson = loadJson(SETJSON_NAME);
	}

	private JSONObject loadJson(String fileName) {
		JSONObject json = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuilder jsonBuilder = new StringBuilder();
			String jsonString = br.readLine();
			jsonBuilder.append(jsonString);
			while (br.ready()){
				jsonString = br.readLine();
				jsonBuilder.append(jsonString);
			}
			json = new JSONObject(jsonBuilder.toString());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	public int getMultiverseID(String cardName){
		JSONObject cardJSON = getCardJSON(cardName);
		
		JSONArray printingsArray = cardJSON.getJSONArray("printings");
		String lastPrinting = printingsArray.getString(printingsArray.length()-1);

		JSONArray cardsArray = setJson.getJSONObject(lastPrinting).getJSONArray("cards");
		for (int i = 0; i < cardsArray.length(); i++){
			JSONObject card = cardsArray.getJSONObject(i);
			if (card.get("name").equals(cardName)){
				return card.getInt("multiverseid");
			}
		}
		return -1;
	}

	public JSONObject getCardJSON(String cardName) {
		return cardJson.getJSONObject(cardName);
	}

	public String getCardText(String cardName) {
		if (getCardJSON(cardName).has("text")) {
			return cardJson.getJSONObject(cardName).getString("text");
		} else {
			return "";
		}
	}

	public String getCardMana(String cardName) {
		if (getCardJSON(cardName).has("manaCost")) {
			return cardJson.getJSONObject(cardName).getString("manaCost");
		} else {
			return "";
		}
	}

	public JSONObject getSearchResultList(String query) {
		String regex = "(\\S+)\"([^\"]*)\"|\"([^\"]*)\"|(\\S+)";
		Matcher queryMatcher = Pattern.compile(regex).matcher(query);

		ArrayList<SearchQuery> queryList = new ArrayList<SearchQuery>();

		// iterates through all the query tokens in the command from the user
		while (queryMatcher.find()) {
			SearchQuery searchQuery = queryParser.evaluate(queryMatcher.group());
			queryList.add(searchQuery);
		}

		JSONObject resultJson = new JSONObject();

		for (String cardName : JSONObject.getNames(cardJson)) {
			JSONObject testObject = cardJson.getJSONObject(cardName);
			boolean queryTestFailed = false;
			for (SearchQuery searchQuery : queryList) {
				if (searchQuery != null)
					if (!searchQuery.matchesQuery(testObject)) {
						queryTestFailed = true;
						break;
					}
			}

			if (!queryTestFailed) {
				resultJson.put(cardName, testObject);
			}

		}
		return resultJson;
	}

}
