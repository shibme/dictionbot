package dictionbot

import (
	"regexp"
	"strings"

	"github.com/shibme/dictionbot/wordnet"
)

var maxRelatedWords = 20
var maxWordsPerText = 10
var wn, _ = wordnet.New()
var spacesRegex = regexp.MustCompile(`\s+`)
var validWordRegex = regexp.MustCompile(`^[A-Za-z0-9]+$`)
var posMap = map[string]string{
	"n": "noun",
	"a": "adjective",
	"v": "verb",
	"r": "adverb",
}

func GetDictions(text string) (dictions []Diction, limtExceeded bool) {
	parts := spacesRegex.Split(text, -1)
	words := make(map[string]bool)
	for _, w := range parts {
		words[w] = true
	}
	if len(parts) > maxWordsPerText {
		limtExceeded = true
		return
	}
	for word := range words {
		dictions = append(dictions, GetDiction(word))
	}
	return
}

func GetDiction(word string) (diction Diction) {
	diction.word = word
	diction.valid = validWordRegex.MatchString(word)
	if diction.valid {
		diction.process()
	}
	return
}

type Diction struct {
	word         string
	valid        bool
	descriptions []DictionDescription
	hyponyms     []string
	hypernyms    []string
}

func (diction *Diction) process() {
	result := wn.Search(strings.ToLower(diction.word))
	for posKey, synsetList := range result {
		pos := posMap[posKey]
		for _, synset := range synsetList {
			diction.descriptions = append(diction.descriptions, DictionDescription{
				pos:         pos,
				description: synset.Gloss,
			})
		}
	}
}

type DictionDescription struct {
	pos         string
	description string
}
