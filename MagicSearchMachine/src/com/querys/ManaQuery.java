package com.querys;

import java.util.Arrays;

import com.core.Card;

public class ManaQuery extends SearchQuery {

	String manaQuery;
	boolean positive;

	public ManaQuery(String value, boolean positive) {
		this.positive = positive;
		manaQuery = value.replaceAll("\\{|\\}", "");
		
		// we do all this so we can sort with a custom comparator
		Character[] costArray = new Character[manaQuery.length()];
		for (int i  = 0; i < manaQuery.length(); i++) {
			costArray[i] = manaQuery.charAt(i);
		}
		Arrays.sort(costArray, IdentityQuery.ORDER_COMPARATOR);
		char[] cCostArray = new char[costArray.length];
		for (int i  = 0; i < costArray.length; i++) {
			cCostArray[i] = costArray[i];
		}
		manaQuery = new String (cCostArray);
	}

	@Override
	public boolean matchesQuery(Card card) {
		if (card.getType()!=null) {
			if (positive)
				return card.getManaCost().contains(manaQuery);
			else
				return !card.getManaCost().contains(manaQuery);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means
			 * we return false
			 */
			return !positive;
		}
	}

}
