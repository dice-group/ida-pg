import configparser
import os

APP_ROOT = os.path.dirname(os.path.abspath(__file__))  # refers to application_top
APP_STATIC = os.path.join(APP_ROOT, 'static')

CONFIG = None

if CONFIG is None:
	CONFIG = configparser.ConfigParser()
	CONFIG.read(os.path.join(APP_STATIC, 'properties.ini'))


def get_property(key, section='properties'):
	properties = CONFIG[section]
	return properties.get(key)
