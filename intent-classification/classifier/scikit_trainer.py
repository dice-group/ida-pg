from sklearn.linear_model import SGDClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.neighbors import KNeighborsClassifier

from classifier import helper
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.decomposition import TruncatedSVD
from sklearn.preprocessing import label_binarize
import pandas as pd


def preprocess_data():
    raw_data = open("scikit.data.train.txt", "r")
    cleaned_data = open("clean.data.train.txt", "w")
    print(raw_data.name)
    cleaned_data.write('class;text\n')

    headers = raw_data.readline()

    for line in raw_data.readlines():
        class_label, text = line.split(";")
        cleaned_text = helper.preprocess_text(text)
        # print(class_label + " " + cleaned_text)
        cleaned_data.write(class_label + ";" + cleaned_text + "\n")

    raw_data.close()
    cleaned_data.close()


# read file, make dataframe
data = open("scikit.data.train.txt", "r")
headers = data.readline().split(";")
class_labels, texts = [], []

for line in data.readlines():
    splitted = line.split(";")
    class_labels.append(splitted[0].replace("__label__", ""))
    texts.append(helper.preprocess_text(splitted[1].rstrip()))

df = pd.DataFrame([class_labels, texts]).T
df.columns = ['class_label', 'text']

# pd.set_option('display.max_rows', None)
# for x in df['text']:
#     print(x)

# Preparing the dataframes
df_holdout = []
df_train = []
for class_label in df['class_label'].unique():
    df_holdout.append(df.loc[df['class_label'] == class_label][:1])
    df_train.append(df.loc[df['class_label'] == class_label][1:])

df_holdout = pd.concat(df_holdout)
df = pd.concat(df_train)

# Turning the labels into numbers
LE = LabelEncoder()
df['class_id'] = LE.fit_transform(df['class_label'])
# print(df)

# find word count - unigram, bigram

# Creating the features (tf-idf weights) for the processed text
textss = df['text'].astype('str')

tfidf_vectorizer = TfidfVectorizer(ngram_range=(1, 2),
                                   # min_df = 2,
                                   max_df=.95)

X = tfidf_vectorizer.fit_transform(textss)  # features
y = df['class_id'].values  # target

print(X.shape)
print(y.shape)


nb_model = GaussianNB()
sgd_model = SGDClassifier(random_state=3, loss='log')
knn_model: KNeighborsClassifier()

models = [nb_model, sgd_model, knn_model]
for model in models:
    model.fit(X, y)


