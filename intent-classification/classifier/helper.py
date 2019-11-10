import re

import nltk
from nltk import WordNetLemmatizer
from nltk.corpus import wordnet
from sklearn.feature_extraction.stop_words import ENGLISH_STOP_WORDS

stopwords = {"a", "an", "the", "for", "to", "and", "of", "you", "me", "in", "on", "i", "please", "this", "that",
             "there"}


def preprocess_text(text: str):
    non_alphanumeric_filter = re.compile("[^a-zA-Z0-9]")
    # stopword_filter = re.compile("(\\s+|^)(a|an|the|for|to|and|of|you|me|in|on|i|please)(\\s+|$)")
    space_trimmer = re.compile("\\s+")

    # cleaned_text = space_trimmer.sub(
    #     " ", stopword_filter.sub(
    #         " ", stopword_filter.sub(
    #             " ", space_trimmer.sub(
    #                 " ", non_alphanumeric_filter.sub(
    #                     " ", string.lower()))))).strip()
    cleaned_text = space_trimmer.sub(" ", non_alphanumeric_filter.sub(" ", text.lower())).strip()

    cleaned = cleaned_text.split(" ")
    tagged = nltk.pos_tag(cleaned)
    lemmatized = lemma_wordnet(tagged)
    no_stopwords = [w for w in lemmatized if w not in stopwords]

    return no_stopwords


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

# print(preprocess_text("use ... to open ? i the geo-spatial the for the Graph me"))
# print(preprocess_text("go back"))
