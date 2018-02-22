package querys;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class FormatQuery extends SearchQuery {

	String format;
	boolean positive;
	private static boolean formatsLoaded = false;
	private static Hashtable<String, String[]> loadedFormats = new Hashtable<String, String[]>();

	public FormatQuery(String format, boolean positive) {
		if (!formatsLoaded) {
			formatsLoaded = true;
			File formatFolder = new File("FormatLists");
			File[] formatList = formatFolder.listFiles();
			for (File file : formatList) {
				ArrayList<String> cardList = new ArrayList<String>();
				try {
					Scanner scan = new Scanner(file);

					while (scan.hasNextLine()) {
						String cardName = scan.nextLine();
						cardList.add(cardName);
					}
					
					scan.close();
					
					String[] cardsArray = cardList.toArray(new String[cardList.size()]);
					Arrays.sort(cardsArray);
					// name of the format in our program excludes extension
					loadedFormats.put(file.getName().substring(0, file.getName().length()-4), cardsArray);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.format = format;
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(JSONObject card) {
		if (card.has("legalities")) {
			JSONArray array = card.getJSONArray("legalities");

			// check for the existence of the attribute we want to find
			JSONObject testAttribute = null;
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("format").equalsIgnoreCase(format)) {
					testAttribute = array.getJSONObject(i);
				}
			}

			if (loadedFormats.containsKey(format)) {
				String [] formatList = loadedFormats.get(format);
				String cardName = card.getString("name");
				if(Arrays.binarySearch(formatList, cardName)>=0)
					return positive;
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
