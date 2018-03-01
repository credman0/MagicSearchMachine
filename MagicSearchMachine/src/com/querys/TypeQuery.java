package com.querys;

import com.core.Card;

public class TypeQuery extends SearchQuery {

	String value;
	boolean positive;

	public TypeQuery(String value, boolean positive) {
		this.value = value.toLowerCase();
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(Card card) {
		if (card.getType()!=null) {
			String testValue = value.toLowerCase().replaceAll( "~", card.getName().toLowerCase());
			if (positive)
				return card.getType().toLowerCase().contains(testValue);
			else
				return !card.getType().toLowerCase().contains(testValue);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}

}
