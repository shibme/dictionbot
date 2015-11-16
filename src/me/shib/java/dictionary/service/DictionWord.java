package me.shib.java.dictionary.service;

import java.util.ArrayList;

public class DictionWord {
	
	public class DictionDesc {
		private String wordType;
		private String description;
		private DictionDesc(String wordType,String description) {
			this.wordType = wordType;
			this.description = description;
		}
		public String getWordType() {
			return wordType;
		}
		public String getDescription() {
			return description;
		}
	}
	
	private String word;
	private ArrayList<DictionDesc> descriptions;
	private ArrayList<String> hyponyms;
	
	public DictionWord(String word) {
		this.word = word;
		descriptions = new ArrayList<DictionDesc>();
		hyponyms = new ArrayList<String>();
	}

	protected void addDescription(String wordType, String description) {
		descriptions.add(new DictionDesc(wordType, description));
	}

	protected void addHyponym(String hyponym) {
		hyponyms.add(hyponym);
	}

	public String getWord() {
		return word;
	}

	public ArrayList<DictionDesc> getDescriptions() {
		return descriptions;
	}

	public ArrayList<String> getHyponyms() {
		return hyponyms;
	}
	
	public String toString() {
		StringBuilder dictionBuilder = new StringBuilder();
		if(descriptions.size() > 0) {
			dictionBuilder.append(word + ":\n\nDescription:\n");
			for(int i = 0; i < descriptions.size(); i++) {
				dictionBuilder.append(descriptions.get(i).wordType + " - " + descriptions.get(i).description + "\n");
			}
		}
		if(hyponyms.size() > 0) {
			dictionBuilder.append("\nRelated to \"" + word + "\":\n");
			for(int i = 0; i < hyponyms.size(); i++) {
				dictionBuilder.append(hyponyms.get(i));
				if(i < (hyponyms.size() - 1)) {
					dictionBuilder.append(" - ");
				}
			}
		}
		if(dictionBuilder.toString().isEmpty()) {
			return null;
		}
		return dictionBuilder.toString();
	}
	
}
