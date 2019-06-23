import fasttext
import flask
from flask import request

app = flask.Flask(__name__)

corpus = 'data.db'

classifier = fasttext.supervised(corpus, 'model_chat', label_prefix='__label__')

# Api to classify the user message.
@app.route('/predict', methods=['GET'])
def index():
    # Getting the user message from url parameter.
    query = request.args.get('msg')
    if query is None:
        return flask.jsonify({
            'error': 'No query parameter'
        })

    # creating a response object
    # storing the model's prediction in the object
    response = {}

    # Intent classification of message
    cls = classifier.predict([query])
    cls = cls[0][0]

    # set the reply based on the class
    if cls == 'greet':
        response['message'] = 'Hello'
    elif cls == 'help':
        response['message'] = 'I can load datasets for you. \n I can visualise data too.'
    elif cls == 'dataset':
        response['message'] = 'Dataset loaded.'
    elif cls == 'barchart':
        response['message'] = 'Barchart drawn.'

    # returning the response object as json
    return flask.jsonify(response)


app.run()