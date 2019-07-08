import json

def config_loader(config_file_path):
    config = open(config_file_path, 'r')
    return json.loads(config.read())