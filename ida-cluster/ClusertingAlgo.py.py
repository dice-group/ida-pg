
# coding: utf-8

# In[76]:


import pandas as pd
import numpy as np
import sklearn
import scipy
import sklearn.metrics as sm 
import matplotlib.pyplot as plot
import sklearn.cluster as cluster
from sklearn.preprocessing import scale 


from sklearn import datasets 
from sklearn.metrics import confusion_matrix,classification_report 
from pandas import DataFrame 
from sklearn import preprocessing 
from sklearn.preprocessing import scale 


# In[83]:


X = np.array([[5,3],[12,4],[5,8],[9,12],[3,8],[7,5],[8,9],[10,8],[9,12],[8,4]])

def intake(data,algoname,params):
    toexe =  getattr(cluster,algoname)
    clusobj = toexe(**params);  #** doesnt work with arrays   *works with arrays
    clusobj.fit(X);
    return clusobj.labels_;
paramDict = {'n_clusters':5 };
intake(X, 'KMeans', paramDict)

