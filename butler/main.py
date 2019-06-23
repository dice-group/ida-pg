import fasttext

corpus = 'data.db'

classifier = fasttext.supervised(corpus, 'model_chat', label_prefix='__label__')

msg = input('Enter text> ')
while msg != 'q':
    print(classifier.predict([msg]))
    msg = input('Enter text> ')
