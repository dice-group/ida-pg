from flask import Flask
from flask import request

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/classify')
def classify():
    text = str(request.args.get('text'))
    return 'Hello World! - ' + text


if __name__ == '__main__':
    app.run()
