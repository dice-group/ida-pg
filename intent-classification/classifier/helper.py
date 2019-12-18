import os
import re

import joblib
import nltk
from nltk import WordNetLemmatizer
from nltk.corpus import wordnet

import settings
from classifier.classifier import ChatIntentClassifier

stopwords = {"a", "an", "the", "for", "to", "and", "of", "you", "me", "in", "on", "i", "please", "this", "that",
			 "there"}


def preprocess_text(text: str):
	non_alphanumeric_filter = re.compile("[^a-zA-Z0-9]")
	space_trimmer = re.compile("\\s+")

	cleaned_text = space_trimmer.sub(" ", non_alphanumeric_filter.sub(" ", text.lower())).strip()

	cleaned = cleaned_text.split(" ")
	tagged = nltk.pos_tag(cleaned)
	lemmatized = lemma_wordnet(tagged)
	no_stopwords = [w for w in lemmatized if w not in stopwords]

	return " ".join(no_stopwords)


def lemma_wordnet(tagged_text):
	lemmatizer = WordNetLemmatizer()
	final = []
	for word, tag in tagged_text:
		wordnet_tag = get_word_net_pos(tag)
		if wordnet_tag is None:
			final.append(lemmatizer.lemmatize(word))
		else:
			final.append(lemmatizer.lemmatize(word, pos=wordnet_tag))
	return final


def get_word_net_pos(treebank_tag):
	if treebank_tag.startswith('J'):
		return wordnet.ADJ
	elif treebank_tag.startswith('V'):
		return wordnet.VERB
	elif treebank_tag.startswith('N'):
		return wordnet.NOUN
	elif treebank_tag.startswith('R'):
		return wordnet.ADV
	else:
		return None


def load_model():
	model_filename = os.path.join(settings.APP_STATIC, settings.get_property('model_file'))
	exists = os.path.exists(model_filename)

	if exists:
		classifier = joblib.load(model_filename)
	else:
		classifier = ChatIntentClassifier(settings.get_property('training_data_file'),
										  settings.get_property('text_field'),
										  settings.get_property('label_field'), settings.get_property('delimiter'))
		save_model(classifier)

	return classifier


def save_model(classifier):
	joblib.dump(classifier, os.path.join(settings.APP_STATIC, settings.get_property('model_file')))
