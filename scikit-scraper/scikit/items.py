# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class MainItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    libName = scrapy.Field() # library name
    funcName = scrapy.Field() # function/class name
    funcDesc = scrapy.Field() # function/class desc.
    notes = scrapy.Field() #special notes about the class/function
    allFuncParams = scrapy.Field() # list of parameters accepted by the class/ func
    funcParamBody = scrapy.Field() # contains entire col. body associated with "Parameters: " 
    #funcParamName = scrapy.Field() # parameter name 
    #funcParamDesc = scrapy.Field() # description of the parameter
    allFuncAttributes = scrapy.Field() # attributes of the parameter
    funcAttrBody = scrapy.Field() # contains entire col. body associated with "Attributes: " 
    #funcAttrName = scrapy.Field()
    #funcAttrDesc = scrapy.Field() 
    allReturnParams = scrapy.Field()
    funcReturnBody = scrapy.Field() # contains entire col. body associated with "Returns: " 
    #returnName = scrapy.Field()
    #returnDesc = scrapy.Field()
    methods = scrapy.Field() # methods associated with the class/func


class MethodItem(scrapy.Item):
    methodName = scrapy.Field() #method associated to the function
    methodDesc = scrapy.Field() # it's description
    methodParams = scrapy.Field() # a list of parameters of the method
    methodParamsBody = scrapy.Field() #description associated with the parameters
    methodReturns = scrapy.Field() # return type of the method
    methodReturnsBody = scrapy.Field() #description associated with method's 'Returns'

