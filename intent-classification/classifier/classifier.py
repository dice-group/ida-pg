import os

import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import SGDClassifier
from sklearn.preprocessing import LabelEncoder

from classifier import helper
from settings import APP_STATIC


class ChatIntentClassifier:
	__threshold = 0.6
	__training_data_file = None
	__df = None
	classifier = None
	label_encoder = None
	tfidf_vectorizer = None
	id_to_label = None

	def __init__(self, training_data_file, text_column_name, label_column_name, delimiter):
		self.label_encoder = LabelEncoder()
		self.tfidf_vectorizer = TfidfVectorizer(ngram_range=(1, 2), max_df=.95)
		self.__training_data_file = os.path.join(APP_STATIC, training_data_file)
		self.__read_file(self.__training_data_file, text_column_name, label_column_name, delimiter)
		self.__train()

	def __read_file(self, train_data_file, text_column_name, label_column_name, delimiter):
		# read file & make dataframe
		data = open(train_data_file, "r")
		headers = data.readline().rstrip().split(delimiter)
		text_column_index = headers.index(text_column_name)
		label_column_index = headers.index(label_column_name)
		intent_labels, texts = [], []

		for line in data.readlines():
			splitted = line.split(delimiter)
			intent_labels.append(splitted[label_column_index])
			texts.append(helper.preprocess_text(splitted[text_column_index].rstrip()))

		df = pd.DataFrame([intent_labels, texts]).T
		df.columns = ['class_label', 'text']
		self.__df = df

	def __train(self):
		# encode string labels into numbers
		self.__df['class_id'] = self.label_encoder.fit_transform(self.__df['class_label'])
		self.id_to_label = self.label_encoder.classes_

		# Creating the features (tf-idf weights) for the processed text
		X = self.tfidf_vectorizer.fit_transform(self.__df['text']).toarray()  # features
		y = self.__df['class_id'].values  # target

		self.classifier = SGDClassifier(random_state=3, loss='log')
		self.classifier.fit(X, y)

	def predict(self, text, n=2):
		to_predict = self.tfidf_vectorizer.transform([helper.preprocess_text(text)]).toarray()
		predictions = list(self.classifier.predict_proba(to_predict)[0])
		sorted_predictions = sorted(predictions, reverse=True)

		intent_scores = []
		for i in range(n):
			if sorted_predictions[i] >= self.__threshold:
				intent_scores.append({'intent': self.label_encoder.classes_[predictions.index(sorted_predictions[i])],
									  'score': sorted_predictions[i]})

		return intent_scores
