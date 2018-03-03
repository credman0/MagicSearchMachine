package com.querys;

import java.util.Arrays;
import java.util.Comparator;

import com.core.Card;

public class IdentityQuery extends SearchQuery {
	
	Character[] colors;
	boolean positive;
	final static char[] COLOR_ORDER = {'w', 'u', 'b', 'r', 'g'}; 	
	public final static Comparator<Character> ORDER_COMPARATOR = new Comparator<Character>() {

		@Override
		public int compare(Character o1, Character o2) {
			// we want to align the colors WUBRG
			for (char c:COLOR_ORDER) {
				if (o1==c) {
					if (o2==c) {
						// they are the same
						return 0;
					}else {
						//o1 came first
						return -1;
					}
				}else if (o2==c) {
					// o1 was not here, o2 came second
					return 1;
				}
			}
			return -1;
		}
		
	};

	public IdentityQuery(String value, boolean positive) {
		// possibilitiy for multiple single letter colors
		colors = new Character[value.length()];
		for (int i  = 0; i < value.length(); i++) {
			colors[i] = value.charAt(i);
		}
		Arrays.sort(colors, ORDER_COMPARATOR);
		this.positive = positive;
	}
	
	@Override
	public boolean matchesQuery(Card card) {
		Character[] colorIdentity = card.getIdentity();
		if (colorIdentity[0]!='c') {
			if (colorIdentity.length!=colors.length) {
				return !positive;
			}
			// we trust that the colors are sorted WUBRG
			for (int i = 0; i < colors.length; i++){
				if (colorIdentity[i]!=colors[i]) {
					return !positive;
				}			
			}
			return positive;

		} else {
			// colorless card
			if (colors[0]=='c') {
				return positive;
			}else {
				return !positive;
			}
		}
	}

}
