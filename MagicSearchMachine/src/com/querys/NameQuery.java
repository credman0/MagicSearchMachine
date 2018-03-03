package com.querys;

import com.core.Card;

public class NameQuery extends SearchQuery {

	String value;
	boolean positive;

	public NameQuery(String value, boolean positive) {
		this.value = value.toLowerCase();
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(Card card) {
		if (card.getName() != null) {
			if (positive)
				return card.getName().contains(value);
			else
				return !card.getName().contains(value);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means we return
			 * false
			 */
			return !positive;
		}
	}

}
