package com.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.querys.CMCQuery;
import com.querys.FormatQuery;
import com.querys.IdentityQuery;
import com.querys.ManaQuery;
import com.querys.NameQuery;
import com.querys.PowerQuery;
import com.querys.PrintingsQuery;
import com.querys.SearchQuery;
import com.querys.TextQuery;
import com.querys.ToughnessQuery;
import com.querys.TypeQuery;

public class QueryParser {
	private Hashtable<String, String[]> loadedFormats = new Hashtable<String, String[]>();
	
	public QueryParser() {
		loadCustomFormats();
	}
	
	public void loadCustomFormats() {
		File formatFolder = new File("FormatLists");
		File[] formatList = formatFolder.listFiles();
		for (File file : formatList) {
			ArrayList<String> cardList = new ArrayList<String>();
			try {
				Scanner scan = new Scanner(file);

				while (scan.hasNextLine()) {
					String cardName = scan.nextLine();
					cardList.add(cardName.toLowerCase());
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
	
	public SearchQuery evaluate(String queryToken) {
		boolean positive = true;
		// check if the query is a negative (starts with '-')
		if (queryToken.charAt(0) == '-') {
			positive = false;
			// then remove it
			queryToken = queryToken.substring(1);
		}

		// remove any quotation marks from the query token and convert to lowercase
		queryToken = queryToken.replace("\"", "").toLowerCase();

		String[] commandSplit = queryToken.split(":");

		/*
		 * if the size of the array split on colons is 0, we have something with
		 * no specific command ie: it's a name query OR a numerical query
		 */
		if (commandSplit.length == 1) {
			String commandToken = commandSplit[0];

			/*
			 * check if the token contains a comparison operator indicating a
			 * numerical query
			 */
			Matcher comparisonMatcher = Pattern.compile("<=|>=|<|=|>").matcher(commandToken);
			if (!comparisonMatcher.find()) {
				/*
				 * in this case there were no comparison operators - it is a
				 * name query
				 */
				return new NameQuery(commandToken, positive);
			} else {
				if (!commandToken.substring(comparisonMatcher.end()).matches("^(0|[1-9][0-9]*)$"))
					return null;

				// note that the numberSplitMatcher has already run once above
				switch (commandToken.substring(0, comparisonMatcher.start())) {
				case "cmc":
					return new CMCQuery(Integer.parseInt(commandToken.substring(comparisonMatcher.end())),
							comparisonMatcher.group(), positive);
				case "power":
					return new PowerQuery(
							Integer.parseInt(commandToken.substring(comparisonMatcher.end())),
							comparisonMatcher.group(), positive);
				case "toughness":
					return new ToughnessQuery(
							Integer.parseInt(commandToken.substring(comparisonMatcher.end())),
							comparisonMatcher.group(), positive);
				default:
					System.out
							.println("Unknown search command: " + commandToken.substring(0, comparisonMatcher.start()));
					return null;
				}

			}

			/*
			 * otherwise the query contained a colon; in this case we check for
			 * the various commands that include a colon in the syntax
			 */
		} else {
			switch (commandSplit[0]) {
			case "t":
				return new TypeQuery(commandSplit[1], positive);
			case "o":
				return new TextQuery(commandSplit[1], positive);
			case "f":
				return new FormatQuery(commandSplit[1], positive, loadedFormats);
			case "s":
				return new PrintingsQuery(commandSplit[1], positive);
			case "id":
				return new IdentityQuery(commandSplit[1], positive);
			case "m":
				return new ManaQuery(commandSplit[1], positive);

			default:
				System.out.println("Unknown search command: " + commandSplit[0]);
				return null;
			}
		}
	}
}
