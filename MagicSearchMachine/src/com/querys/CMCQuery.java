package com.querys;

import com.core.Card;

public class CMCQuery extends NumericalQuery {

	public CMCQuery(int value, String comparison, boolean positive) {
		super(value, comparison, positive);
	}
	
	@Override
	public boolean matchesQuery(Card card) {
		if (card.getCmc()!=-1) {
			int cardValue = card.getCmc();
			
			if (positive)
				return compareInts(cardValue, comparator, value);
			else
				return !compareInts(cardValue, comparator, value);
		} else
			return !positive;
	}

}
