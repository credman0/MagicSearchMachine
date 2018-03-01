package com.querys;

import com.core.Card;

public class PowerQuery extends NumericalQuery {

	public PowerQuery(int value, String comparison, boolean positive) {
		super(value, comparison, positive);
	}
	
	@Override
	public boolean matchesQuery(Card card) {
		if (!card.getPower().equals("-1")) {
			int cardValue;
			if (card.getPower().contains("*")) {
				cardValue = 0;
			} else if (card.getPower().contains(".")) {
				// some un-set cards have decimal stats - we don't bother
				cardValue = 0;
			}else{
				// we have to always parse it because sometimes it is a string
				cardValue = Integer.parseInt(card.getPower());
			}
			if (positive)
				return compareInts(cardValue, comparator, value);
			else
				return !compareInts(cardValue, comparator, value);
		} else
			return !positive;
	}

}
