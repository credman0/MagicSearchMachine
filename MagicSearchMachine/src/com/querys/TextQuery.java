package com.querys;

import com.core.Card;

public class TextQuery extends SearchQuery {

	String value;
	boolean positive;

	public TextQuery(String value, boolean positive) {
		this.value = value.toLowerCase();
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(Card card) {
		if (card.getText() != null) {
			String testValue = value.toLowerCase().replaceAll("~", card.getName().toLowerCase());
			if (positive)
				return card.getText().toLowerCase().contains(testValue);
			else
				return !card.getText().toLowerCase().contains(testValue);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means we return
			 * false
			 */
			return !positive;
		}
	}

}
