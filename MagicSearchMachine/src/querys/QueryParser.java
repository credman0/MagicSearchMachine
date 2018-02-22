package querys;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {
	public static SearchQuery evaluate(String queryToken) {
		boolean positive = true;
		// check if the query is a negative (starts with '-')
		if (queryToken.charAt(0) == '-') {
			positive = false;
			// then remove it
			queryToken = queryToken.substring(1);
		}

		// remove any quotation marks from the query token
		queryToken = queryToken.replace("\"", "");

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
				return new StringQuery("name", commandToken, positive);
			} else {
				if (!commandToken.substring(comparisonMatcher.end()).matches("^(0|[1-9][0-9]*)$"))
					return null;

				// note that the numberSplitMatcher has already run once above
				switch (commandToken.substring(0, comparisonMatcher.start())) {
				case "cmc":
					return new NumericalQuery("cmc", Integer.parseInt(commandToken.substring(comparisonMatcher.end())),
							comparisonMatcher.group(), positive);
				case "power":
					return new NumericalQuery("power",
							Integer.parseInt(commandToken.substring(comparisonMatcher.end())),
							comparisonMatcher.group(), positive);
				case "toughness":
					return new NumericalQuery("toughness",
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
				return new StringQuery("type", commandSplit[1], positive);
			case "o":
				return new StringQuery("text", commandSplit[1], positive);
			case "f":
				return new FormatQuery(commandSplit[1], positive);
			case "s":
				return new ArrayQuery("printings",commandSplit[1], positive);
			case "c":
				return new ArrayQuery("colorIdentity",commandSplit[1], positive);

			default:
				System.out.println("Unknown search command: " + commandSplit[0]);
				return null;
			}
		}
	}
}
