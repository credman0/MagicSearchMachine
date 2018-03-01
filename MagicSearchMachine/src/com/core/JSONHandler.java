package com.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.querys.ManaQuery;
import com.querys.QueryParser;
import com.querys.SearchQuery;

public class JSONHandler {
	public static final String CARDJSON_NAME = "AllCards-x.json";
	public static final String SETJSON_NAME = "AllSets.json";
	protected QueryParser queryParser = new QueryParser();
	protected Map<String, Card> cards = new HashMap<String, Card>();

	public void initilizeCardMap() {
		cards = loadCardsFromJSON(CARDJSON_NAME, SETJSON_NAME);

	}

	protected Map<String, Card> loadCardsFromJSON(String cardsFileName, String setsFileName) {
		Map<String, Card> cards = new HashMap<String, Card>();
		JSONObject cardJson = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(cardsFileName));
			StringBuilder jsonBuilder = new StringBuilder();
			String jsonString = br.readLine();
			jsonBuilder.append(jsonString);
			while (br.ready()) {
				jsonString = br.readLine();
				jsonBuilder.append(jsonString);
			}
			cardJson = new JSONObject(jsonBuilder.toString());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject setJson = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(setsFileName));
			StringBuilder jsonBuilder = new StringBuilder();
			String jsonString = br.readLine();
			jsonBuilder.append(jsonString);
			while (br.ready()) {
				jsonString = br.readLine();
				jsonBuilder.append(jsonString);
			}
			setJson = new JSONObject(jsonBuilder.toString());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String cardName : JSONObject.getNames(cardJson)) {
			JSONObject cardObject = cardJson.getJSONObject(cardName);
			JSONArray printingsJSON = null;
			String[] printings = null;
			if (cardObject.has("printings")) {
				printingsJSON = cardObject.getJSONArray("printings");
				printings = new String[printingsJSON.length()];
				for (int i = 0; i < printingsJSON.length(); i++) {
					printings[i] = printingsJSON.getString(i);
				}
			}

			Character[] colorIdentity;
			if (cardObject.has("colorIdentity")) {
				JSONArray identityJson = cardObject.getJSONArray("colorIdentity");
				colorIdentity = new Character[identityJson.length()];
				for (int i = 0; i < colorIdentity.length; i++) {
					colorIdentity[i] = identityJson.getString(i).toLowerCase().charAt(0);
				}
				Arrays.sort(colorIdentity, ManaQuery.ORDER_COMPARATOR);
			} else {
				colorIdentity = new Character[] { 'c' };
			}

			String cardType;
			if (cardObject.has("type")) {
				cardType = cardObject.getString("type");
			} else {
				cardType = null;
			}

			String cardText;
			if (cardObject.has("text")) {
				cardText = cardObject.getString("text");
			} else {
				cardText = null;
			}

			String power;
			if (cardObject.has("power")) {
				power = cardObject.getString("power");
			} else {
				power = "-1";
			}

			String toughness;
			if (cardObject.has("toughness")) {
				toughness = cardObject.getString("toughness");
			} else {
				toughness = "-1";
			}

			String manaCost;
			if (cardObject.has("manaCost")) {
				manaCost = cardObject.getString("manaCost");
			} else {
				manaCost = "";
			}
			// get the multiverse id
			int multiverseID = -1;
			String lastPrinting = printingsJSON.getString(printingsJSON.length() - 1);
			JSONArray printingsCardsArray = setJson.getJSONObject(lastPrinting).getJSONArray("cards");
			for (int i = 0; i < printingsCardsArray.length(); i++) {
				JSONObject card = printingsCardsArray.getJSONObject(i);
				if (card.get("name").equals(cardName)) {
					if (card.has("multiverseid")) {
						multiverseID = card.getInt("multiverseid");
						break;
					}
				}
			} // if the id was not found, it will still be -1

			JSONArray legalitiesJSON = null;
			Map<String, Boolean> legalities = null;
			if (cardObject.has("legalities")) {
				legalitiesJSON = cardObject.getJSONArray("legalities");

				legalities = new HashMap<String, Boolean>();
				for (int i = 0; i < legalitiesJSON.length(); i++) {
					legalities.put(legalitiesJSON.getJSONObject(i).getString("format"),
							legalitiesJSON.getJSONObject(i).getString("legality").equals("Legal"));
				}
			}

			Card card = new Card(cardName, cardObject.getInt("cmc"), power, toughness, cardType, cardText, printings,
					colorIdentity, multiverseID, manaCost, legalities);
			cards.put(cardName, card);
		}

		return cards;
	}

	public int getMultiverseID(String cardName) {
		return cards.get(cardName).getMultiverseID();
	}

	public Card getCard(String cardName) {
		return cards.get(cardName);
	}

	public String getCardText(String cardName) {
		return cards.get(cardName).getText();
	}

	public String getCardMana(String cardName) {
		return cards.get(cardName).getManaCost();
	}

	public Vector<String> getSearchResultList(String query) {
		String regex = "(\\S+)\"([^\"]*)\"|\"([^\"]*)\"|(\\S+)";
		Matcher queryMatcher = Pattern.compile(regex).matcher(query);

		ArrayList<SearchQuery> queryList = new ArrayList<SearchQuery>();

		// iterates through all the query tokens in the command from the user
		while (queryMatcher.find()) {
			SearchQuery searchQuery = queryParser.evaluate(queryMatcher.group());
			queryList.add(searchQuery);
		}

		Vector<String> resultList = new Vector<String>();

		for (String cardName : cards.keySet()) {
			Card card = cards.get(cardName);
			boolean queryTestFailed = false;
			for (SearchQuery searchQuery : queryList) {
				if (searchQuery != null)
					if (!searchQuery.matchesQuery(card)) {
						queryTestFailed = true;
						break;
					}
			}

			if (!queryTestFailed) {
				resultList.add(cardName);
			}

		}
		return resultList;
	}

}
