package com.querys;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.core.Card;

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
	public boolean matchesQuery(Card card) {
		if (card.getLegalities() != null) {
			Map<String, Boolean> legalities = card.getLegalities();

			if (loadedFormats.containsKey(format)) {
				String[] formatList = loadedFormats.get(format);
				String cardName = card.getName();
				if (Arrays.binarySearch(formatList, cardName) >= 0)
					return positive;
			}

			if (legalities.containsKey(format)) {
				if (positive)
					return legalities.get(format);
				else
					return !legalities.get(format);
			}

			return !positive;
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means we return
			 * false
			 */
			return !positive;
		}
	}
}
