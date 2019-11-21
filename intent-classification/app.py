from flask import Flask
from flask import jsonify
from flask import request

from classifier.classifier import ChatIntentClassifier

flask_app = Flask(__name__)
classifier = ChatIntentClassifier("clean.data.train.txt", "text", "class", ";")


@flask_app.route('/')
def hello_world():
	return 'Hello World!'


@flask_app.route('/classify')
def classify():
	text = str(request.args.get('text'))

	if text:
		predictions = classifier.predict(text, n=3)
		return jsonify(predictions)
	else:
		return "Please supply text as a url param e.g. http://127.0.0.1:5000/classify?text=Hello"


if __name__ == '__main__':
	flask_app.run()
