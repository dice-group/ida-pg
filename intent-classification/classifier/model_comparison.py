import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import SGDClassifier
from sklearn.naive_bayes import GaussianNB, MultinomialNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import LinearSVC
import numpy as np

from classifier import helper

# read file & make dataframe
data = open("scikit.data.train.txt", "r")
headers = data.readline().split(";")
class_labels, texts = [], []

for line in data.readlines():
	splitted = line.split(";")
	class_labels.append(splitted[0].replace("__label__", ""))
	texts.append(helper.preprocess_text(splitted[1].rstrip()))

df = pd.DataFrame([class_labels, texts]).T
df.columns = ['class_label', 'text']

# encode string labels into numbers
LE = LabelEncoder()
df['class_id'] = LE.fit_transform(df['class_label'])

# Creating the features (tf-idf weights) for the processed text
tfidf_vectorizer = TfidfVectorizer(ngram_range=(1, 2),
								   # min_df = 2,
								   max_df=.95)

X = tfidf_vectorizer.fit_transform(df['text']).toarray()  # features
y = df['class_id'].values  # target

to_predict = tfidf_vectorizer.transform(df['text']).toarray()

nb_model = GaussianNB()
sgd_model = SGDClassifier(random_state=3, loss='log')
knn_model = KNeighborsClassifier()
mnb_model = MultinomialNB()
rf_model = RandomForestClassifier(n_estimators=200, max_depth=3, random_state=0)
lsvc_model = LinearSVC()
# lr_model = LogisticRegression(random_state=0),

models = [nb_model, sgd_model, knn_model, mnb_model, rf_model, lsvc_model]
model_names = ['gaussian_naive_bayes', 'stochastic_grad_descent', 'knn', 'multinomial_naive_bayes', 'random_forrest',
			   'linear_svc']

results = []
results_encoded = []
for model in models:
	model.fit(X, y)
	predictions = model.predict(to_predict)
	results.append(LE.inverse_transform(predictions))
	results_encoded.append(predictions)

accuracies = []
np_class_ids = np.array(df['class_id'])
total = np_class_ids.size

for i in range(6):
	df[model_names[i]] = results[i]
	accuracies.append((np.count_nonzero(np_class_ids == results_encoded[i])/total)*100)

pd.set_option('display.max_columns', 500)
pd.set_option('display.max_rows', 500)
pd.set_option('display.width', 1000)

# print('\ntext')
# print("\n".join(list(df['text'])))
# print('\nclass_label')
# print("\n".join(list(df['class_label'])))
# print('\nclass_id')
# print("\n".join(list(df['class_id'].astype('str'))))
# print('\ngaussian_naive_bayes')
# print("\n".join(list(df['gaussian_naive_bayes'])))
# print('\nstochastic_grad_descent')
# print("\n".join(list(df['stochastic_grad_descent'])))
# print('\nknn')
# print("\n".join(list(df['knn'])))
# print('\nmultinomial_naive_bayes')
# print("\n".join(list(df['multinomial_naive_bayes'])))
# print('\nrandom_forrest')
# print("\n".join(list(df['random_forrest'])))
# print('\nlinear_svc')
# print("\n".join(list(df['linear_svc'])))


print('')
for i in range(6):
	print(model_names[i] + ": " + str(accuracies[i]))
