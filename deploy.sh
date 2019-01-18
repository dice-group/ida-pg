#!/bin/bash

version=`cat VERSION`
registry=${1:-localhost:5000}

function build_container() {
    echo "Building $1:"
    tag=$registry/ida/$1:$version
    echo "Building $tag..."
    if docker build ${@:2} -t $tag; then
        echo "Successfully built $tag."
        if docker push $tag; then
            echo "Successfully pushed $tag."
        fi
    fi
}

build_container nginx . -f Dockerfile-nginx
build_container ida-ws ida-ws

export REGISTRY=$registry
export VERSION=$version
docker stack deploy --compose-file docker-compose.yml ida-stack
