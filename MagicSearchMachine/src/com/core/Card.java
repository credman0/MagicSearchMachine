package com.core;

import java.util.Map;

public class Card {
	protected String name;
	protected int cmc;
	protected String power;
	protected String toughness;
	protected String type;
	protected String text;
	protected String[] printings;
	protected Character[] identity;
	protected int multiverseID;
	protected String manaCost;
	protected Map<String, Boolean> legalities;
	
	public Card(String name, int cmc, String power, String toughness, String type, String text, String[] printings,
			Character[] identity, int multiverseID, String manaCost, Map<String, Boolean> legalities) {
		this.name = name;
		this.cmc = cmc;
		this.power = power;
		this.toughness = toughness;
		this.type = type;
		this.text = text;
		this.printings = printings;
		this.identity = identity;
		this.multiverseID = multiverseID;
		this.manaCost = manaCost;
		this.legalities = legalities;
	}

	public Map<String, Boolean> getLegalities() {
		return legalities;
	}

	public String getName() {
		return name;
	}

	public int getCmc() {
		return cmc;
	}

	public String getPower() {
		return power;
	}

	public String getToughness() {
		return toughness;
	}

	public String getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public String[] getPrintings() {
		return printings;
	}

	public Character[] getIdentity() {
		return identity;
	}
	
	public int getMultiverseID() {
		return multiverseID;
	}

	public String getManaCost() {
		return manaCost;
	}
}
