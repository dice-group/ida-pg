FROM ubuntu:18.04

RUN apt-get update && apt-get install \
  -y --no-install-recommends python3 python3-pip python3-setuptools python3-virtualenv python3-wheel


WORKDIR /app
COPY intent-classification/requirements.txt intent-classification/requirements.txt
RUN pip3 install -r intent-classification/requirements.txt
RUN pip3 install gunicorn

WORKDIR /app/intent-classification
RUN python3 -m nltk.downloader averaged_perceptron_tagger
RUN python3 -m nltk.downloader wordnet

EXPOSE 5001
ENTRYPOINT ["/app/intent-classification/deploy-ic.sh"]
