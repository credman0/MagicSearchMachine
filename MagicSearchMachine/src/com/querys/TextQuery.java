package com.querys;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			String testValue = value.replaceAll("~", card.getName().toLowerCase());
			String uncheckedText = card.getText();
			
			// split on stars so we can go through and skip that text
			while (testValue.contains("*")) {
				int starIndex = testValue.indexOf('*');
				
				String testToken = testValue.substring(0,starIndex).trim();
				
				// the index in the remaining card text that contains a string between stars from the query text (-1 if none)
				// ie we check if one string token is found in the card text
				int textIndex = uncheckedText.indexOf(testToken);
				
				if (textIndex==-1) {
					// the card does not contain the correct string
					return !positive;
				}else {
					// reduce the size of the unchecked text so we do not check the same part twice
					uncheckedText = uncheckedText.substring(textIndex + testToken.length());
					
					// same for the test value, so we can progress through the string
					testValue = testValue.substring(starIndex+1).trim();
				}
			}
			// check the remaining string
			if (positive)
				return uncheckedText.contains(testValue);
			else
				return !uncheckedText.contains(testValue);
		} else {
			/*
			 * if we are a positive condition, then lacking the attribute means we return
			 * false
			 */
			return !positive;
		}
	}

}
