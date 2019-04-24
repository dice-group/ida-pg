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
*   bash
*   git
*   Docker >= 18.09
*   Java JDK >= 1.8
*   Maven >= 3.6
*   Node.js >= 10

Docker is not directly supported by Windows 10 Home and Windows <= 8.1.
Use [Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/) if you are using an unsupported OS.

IDA is set up via bash-scripts. If you are using Windows you will have to install bash, e.g. by installing [cmder](https://cmder.net/), by installing [Cygwin](https://www.cygwin.com/) or via [WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10).

To get everything up and running follow these steps:
1. Setting up Docker:
	1. Start a local registry: `docker run -d -p 5000:5000 --name registry registry:2`
	2. Create a local single-node Docker swarm: `docker swarm init --advertise-addr 127.0.0.1`
2. Starting the IDA stack: `./services/deploy-dev.sh`.
3. The IDA web interface should now be available at `http://127.0.0.1:3080/`.
4. The development stack uses `ng serve` and `spring-boot-devtools` internally. Updates to the sources (`.class`-files in case of Spring) should be reflected automatically.
5. To stop and remove the running development stack run `docker stack rm ida-stack-dev`.

## Production setup

IDA does not yet support distributed environments and is therefore not ready for big production use-cases.
To run IDA in a single-node production environment you need:
*   bash
*   git
*   Docker >= 18.09

To start the production stack:
1. Setting up Docker:
	1. Start a local registry: `docker run -d -p 5000:5000 --name registry registry:2`
	2. Create a single-node Docker swarm: `docker swarm init --advertise-addr [SERVER_IP]` where `[SERVER_IP]` is the IP on which IDA should be accessible.
2. Starting the IDA stack: `./services/deploy.sh`
3. The IDA web interface should now be available at `http://[SERVER_IP]/`.
4. To stop and remove the running production stack run `docker stack rm ida-stack`.
