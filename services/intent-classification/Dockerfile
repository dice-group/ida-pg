FROM ubuntu:18.04

RUN apt-get update && apt-get install \
  -y --no-install-recommends python3=3.6.7-1~18.04 python3-pip=9.0.1-2.3~ubuntu1.18.04.1 python3-setuptools=39.0.1-2 python3-virtualenv=15.1.0+ds-1.1 python3-wheel=0.30.0-0.2 \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*


WORKDIR /app
COPY intent-classification/requirements.txt intent-classification/requirements.txt
RUN pip3 install -r intent-classification/requirements.txt

WORKDIR /app/intent-classification
RUN python3 -m nltk.downloader averaged_perceptron_tagger
RUN python3 -m nltk.downloader wordnet

EXPOSE 5001
ENTRYPOINT ["/app/intent-classification/deploy-ic.sh"]
