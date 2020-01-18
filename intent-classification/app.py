from flask import Flask
from flask import jsonify
from flask import request

from classifier import helper

app = Flask(__name__)
classifier = helper.load_model()


@app.route('/')
def hello_world():
	return 'use /classify endpoint'


@app.route('/classify')
def classify():
	text = str(request.args.get('text'))
	n = request.args.get('n')

	if text:
		if n:
			predictions = classifier.predict(text, n=int(n))
		else:
			predictions = classifier.predict(text)

		return jsonify(dict(text=text, intents=predictions))
	else:
		return "Please supply text as a url param e.g. /classify?text=Hello"


if __name__ == '__main__':
	app.run(port=5001)
