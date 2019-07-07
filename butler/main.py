import fasttext
import flask
from flask import request
from config_loader import config_loader

app = flask.Flask(__name__)

print('Loading intent configuration file.. ', end='')
intent_config = config_loader('intent_config.json')
print('Done')

print('Initializing classifier.. ', end='')
corpus = 'data.db'
classifier = fasttext.supervised(corpus, 'model_chat', label_prefix='__label__')
print('Done')

# Api to classify the user message.
@app.route('/predict', methods=['GET'])
def index():
    # Getting the user message from url parameter.
    msg = request.args.get('msg')
    if msg is None:
        return flask.jsonify({
            'error': 'No query parameter'
        })

    # creating a response object
    # storing the model's prediction in the object
    response = {}

    # Intent classification of message
    cls = classifier.predict([msg])[0][0]
    print(cls)

    if cls in intent_config:
        if 'parameters' in intent_config.get(cls):
            parameters = intent_config.get(cls).get('parameters')

            if 'param' not in request.args:
                response['param'] = 0
            else:
                response['param'] += 1

            param = parameters[response.get('param')]
            for k in param:
                response['reply'] = param.get(k)

            response['complete'] = 0
            response['intent'] = cls
        else:
            response['complete'] = 1
            if cls == 'greet':
                response['reply'] = 'Hello'
            elif cls == 'help':
                response['reply'] = 'I can load datasets for you. \n I can visualise data too.'
            elif cls == 'dataset':
                response['reply'] = 'Dataset loaded.'
            elif cls == 'barchart':
                response['reply'] = 'Barchart drawn.'
            print('parameters missing')

    # returning the response object as json
    return flask.jsonify(response)


print('Starting server')
app.run()