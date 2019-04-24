# Intelligent Data Assistant (IDA)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ffb33228db8a49919b15063ee05eca70)](https://www.codacy.com/app/nikit91/dice-ida?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nikit91/dice-ida&amp;utm_campaign=Badge_Grade)
[![Codecov Badge](https://codecov.io/gh/dice-group/ida/branch/master/graph/badge.svg)](https://codecov.io/gh/dice-group/ida)

IDA aims to become an all-in-one natural language based interface for Data Science Workflows.
It's core purpose is to ease the task of a Data Scientist by providing a chat interface. At the moment, IDA is capable of handling the following tasks:
*   Load and display the files of a csv based dataset
*   Display Bar Graph Visualizations
*   Display Force-Directed Graph Visualization
*   Suggest and execute scikit's implementation of clustering algorithms

**Here's a video demonstration of IDA:**
[https://youtu.be/7FQTiFdqyDE](https://youtu.be/7FQTiFdqyDE)

**Coming soon:**
*   Link to the deployed instance
*   Directions on deploying locally

## Development quick start

IDA currently consists of three main components:
*   `ida-ws`: A Java Spring server that provides the IDA REST-API.
*   `ida-chatbot`: The Angular webclient for IDA.
*   `librarian`: A general purpose toolkit to programmatically work with software libraries.

You will need at least the following tools:
*   git
*   Docker >= 18.09

To get everything up and running follow these steps:
1. Setting up Docker:
	1. Start a local registry: `docker run -d -p 5000:5000 --name registry registry:2`
	2. Create a local single-node Docker swarm: `docker swarm init --advertise-addr 127.0.0.1`
2. Starting the IDA stack: `./services/deploy.sh`
3. The IDA web interface should now be available at `http://127.0.0.1/`.
