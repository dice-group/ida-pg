import fasttext
import flask
from flask import request, render_template
from config_loader import config_loader


app = flask.Flask(__name__)

print('Loading intent configuration file.. ', end='')
intent_config = config_loader('intent_config.json')
print('Done')

print('Initializing classifier.. ', end='')
corpus = 'data.db'
classifier = fasttext.supervised(corpus, 'model_chat', label_prefix='__label__')
print('Done')


@app.route('/')
def home():
    return render_template('index.html')

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
    if 'complete' in request.args:
        cls = request.args.get('intent')
        for k in request.args:
            if k != 'reply' and k != 'msg':
                response[k] = request.args.get(k)
    else:
        cls = classifier.predict([msg])[0][0]

    print(cls)

    if cls in intent_config:
        if 'parameters' in intent_config.get(cls):
            parameters = intent_config.get(cls).get('parameters')

            if 'param' not in request.args:
                response['param'] = 0
            else:
                paramN = int(request.args.get('param'))
                param = parameters[paramN]
                for k in param:
                    response[k] = msg
                response['param'] =  paramN + 1

            if response['param'] <= len(parameters)-1:
                param = parameters[response.get('param')]
                for k in param:
                    response['reply'] = param.get(k)

                response['complete'] = 0
            else:
                response['complete'] = 1

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