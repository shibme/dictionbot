package me.shib.java.lib.dictionary.service;

import java.util.ArrayList;

public class DictionWord {

    private String word;
    private ArrayList<DictionDesc> descriptions;
    private ArrayList<String> hyponyms;

    public DictionWord(String word) {
        this.word = word;
        descriptions = new ArrayList<>();
        hyponyms = new ArrayList<>();
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

    public String toString() {
        StringBuilder dictionBuilder = new StringBuilder();
        if (descriptions.size() > 0) {
            dictionBuilder.append("*").append(word).append(":\n\nDescription:\n*");
            for (DictionDesc description : descriptions) {
                dictionBuilder.append(description.wordType).append(" - ").append(description.description).append("\n");
            }
        }
        if (hyponyms.size() > 0) {
            dictionBuilder.append("*\nRelated to \"").append(word).append("\":\n*");
            for (int i = 0; i < hyponyms.size(); i++) {
                dictionBuilder.append(hyponyms.get(i));
                if (i < (hyponyms.size() - 1)) {
                    dictionBuilder.append(" - ");
                }
            }
        }
        if (dictionBuilder.toString().isEmpty()) {
            return null;
        }
        return dictionBuilder.toString();
    }

    public class DictionDesc {
        private String wordType;
        private String description;

        private DictionDesc(String wordType, String description) {
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

}
