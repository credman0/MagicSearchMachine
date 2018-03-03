package com.querys;

import com.core.Card;

public class TypeQuery extends SearchQuery {

	String value;
	boolean positive;

	public TypeQuery(String value, boolean positive) {
		this.value = value;
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(Card card) {
		if (card.getType()!=null) {
			String testValue = value.replaceAll( "~", card.getName());
			if (positive)
				return card.getType().contains(testValue);
			else
				return !card.getType().contains(testValue);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}

}
