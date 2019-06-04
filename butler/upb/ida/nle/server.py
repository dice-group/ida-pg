import  pickle
import flask
from flask import request
from flask_cors import CORS

app = flask.Flask(__name__)
CORS(app)

# Extract the dumped model from pickle file
model = pickle.load(open("model.pkl", "rb"))
# extract the dumped tokenizer for pre-processing the user message.
tokenizer = pickle.load(open('tokenizer.pk', 'rb'))

# Extract the token frequency transformer from dumped pickle file.
frequency_transformer = pickle.load(open('frquencytransformer.pk', 'rb'))


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

    # tokenizing the user input using the existing tokenizer.
    test_counts = tokenizer.transform([query])
    # converting the tokens into frequencies.
    test_frequencies = frequency_transformer.transform(test_counts)
    cls = model.predict(test_frequencies)

    # set the reply based on the class
    if cls == 'greet':
        response['predictions'] = 'Hello'
    elif cls == 'help':
        response['predictions'] = 'I can load datasets for you. \n I can visualise data too.'
    elif cls == 'dataset':
        response['predictions'] = 'Dataset loaded.'
    elif cls == 'barchart':
        response['predictions'] = 'Barchart drawn.'

    # returning the response object as json
    return flask.jsonify(response)


app.run()
