# Jupyter Kernel Gateway

## Overview

Jupyter Kernel Gateway is a web server that provides headless access to
Jupyter kernels. Your application communicates with the kernels remotely,
through REST calls and Websockets rather than ZeroMQ messages. 

## Prerequisites

1. python 3 or better
2. Miniconda
3. GNU make
4. jupyter notebook ```pip install jupyter```

## Installation steps

### Step-1

#### Clone the repo

Clone [this](https://github.com/jupyter/kernel_gateway).
 repository in a local directory

```
## clone this repo
git clone https://github.com/jupyter/kernel_gateway.git
```

### Step-2
Replace kernel-gateway\Makefile with [this](https://github.com/dice-group/ida/tree/master/jupyter-notebook/windows/Makefile) for windows and [this](https://github.com/dice-group/ida/tree/master/jupyter-notebook/linux/Makefile) for linux.

### Step-3
Install kernel gateway

```
## Install using pip
pip install jupyter_kernel_gateway

OR

## Install using conda
conda install -c conda-forge jupyter_kernel_gateway
```

### Step-4
#### Build a conda environment
Build a Python 3 conda environment containing the necessary dependencies for running the kernel gateway

```
## from kernel_gateway\
make env
```
Next....

```
## Install kernel_gateway into the environment
conda install --name kernel-gateway-dev jupyter_kernel_gateway
```
### Step-5
#### Adding clustering notebook to our kernel gateway

Copy [this](https://github.com/dice-group/ida/blob/master/jupyter-notebook/clustering.ipynb) notebook to \kernel_gateway\etc\api_examples\

### Step-6
Install missing packages to the kernel-gateway-dev conda environment.

```
## Install pandas
conda install --name kernel-gateway-dev pandas
```
```
## Install scikit
conda install --name kernel-gateway-dev scikit_learn
```
```
## Install matplotlib
conda install --name kernel-gateway-dev matplotlib
````
Install any missing notebook dependencies into the environment.

### Step-7
#### Run kernel gateway

```
#run kernel as notebook-http mode
make dev-http
```
