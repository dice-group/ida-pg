#!/bin/bash

version=`cat VERSION`
registry=${1:-localhost:5000}

function build_container() {
    echo "Building $1:"
    tag=$registry/ida/$1:$version
    echo "Building $tag..."
    docker build . -f $2 -t $tag
    echo "Successfully built $tag."
    docker push $tag
    echo "Successfully pushed $tag."
}

build_container nginx ./Dockerfile-nginx
build_container ida-ws ida-ws/Dockerfile

export REGISTRY=$registry
export VERSION=$version
docker stack deploy --compose-file docker-compose.yml ida-stack
