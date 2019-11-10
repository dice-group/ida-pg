import fasttext

model = fasttext.train_supervised('data.train.txt')

# print(model.words)
# print(model.labels)

# print(model.get_words(True))

print(model.predict("geo spatial graph", k=2))