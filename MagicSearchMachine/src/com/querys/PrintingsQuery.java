package com.querys;

import com.core.Card;

public class PrintingsQuery extends SearchQuery {
	String value;
	boolean positive;

	public PrintingsQuery(String value, boolean positive) {
		this.value = value;
		this.positive = positive;
	}

	@Override
	public boolean matchesQuery(Card card) {
		String[] printings = card.getPrintings();
		for (int i = 0; i < printings.length; i++) {
			if (printings[i].equalsIgnoreCase(value))
				return positive;
		}

		return !positive;
	}
}
