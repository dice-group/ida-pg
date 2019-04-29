#!/bin/bash

cd "${BASH_SOURCE%/*}" || exit

version=$(cat VERSION)
registry=${REGISTRY:-localhost:5000}

function build_container() {
    tag=$registry/ida/$1:$version
    echo "Building $1: $tag..."
    if docker build ${@:2} -t $tag; then
        echo "Successfully built $tag."
        if docker push $tag; then
            echo "Successfully pushed $tag."
        fi
    fi
}

build_container nginx .. -f nginx/Dockerfile
build_container ida-ws .. -f ida-ws/Dockerfile

export REGISTRY=$registry
export VERSION=$version
docker stack deploy --compose-file docker-compose.yml ida-stack
