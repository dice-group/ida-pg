from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.naive_bayes import MultinomialNB
import pickle

training_data = ["Hello", "Hello how are you", "Hi", "Hey, whats up?", "Hey", "What you can do for me?",
                 "How can you help me?", "What can you do?", "What are you capable of doing?", "Load $city database",
                 "Open $city dataset", "Please load $city dataset", "can you please load $city dataset",
                 "Load dataset $city data for me", "Can you draw me bar chart for this table?",
                 "Draw me a bar chart for random table", "Go ahead and give me a bar chart",
                 "Create me bar chart of nava database"]
# training_target = ["0", "0", "0", "0", "0", "1", "1", "1", "1", "2", "2", "2", "2", "2", "3", "3", "3", "3"]
training_classes = ["greet", "greet", "greet", "greet", "greet", "help", "help", "help", "help", "dataset", "dataset",
                   "dataset", "dataset", "dataset", "barchart", "barchart", "barchart", "barchart"]
classes = ['greet', 'help', 'dataset', 'barchart']

tokenizer = CountVectorizer()
training_counts = tokenizer.fit_transform(training_data)
frequency_transformer = TfidfTransformer()
training_frequencies = frequency_transformer.fit_transform(training_counts)
classifier = MultinomialNB().partial_fit(training_frequencies, training_classes, classes=classes)

pickle.dump(classifier, open("model.pkl", "wb"))
with open('tokenizer.pk', 'wb') as fin:
    pickle.dump(tokenizer, fin)
with open('frquencytransformer.pk', 'wb') as fin:
    pickle.dump(frequency_transformer, fin)


test_counts = tokenizer.transform(['load movie data'])
test_frequencies = frequency_transformer.transform(test_counts)
ans = classifier.predict(test_frequencies)
print(ans)
