#!/usr/bin/env bash

cd "${BASH_SOURCE%/*}" || exit

version=$(cat VERSION)
registry=${REGISTRY:-localhost:5000}

fuseki_pw=${FUSEKI_PW:-$(cat FUSEKI_PW)}

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

build_container nginx-dev .. -f nginx/Dockerfile.nginx-dev
build_container frontend-dev .. -f nginx/Dockerfile.frontend-dev
build_container ida-ws-dev .. -f ida-ws/Dockerfile.dev

export REGISTRY=$registry
export VERSION=$version
export FUSEKI_PW=$fuseki_pw
docker stack deploy --compose-file docker-compose-dev.yml ida-stack-dev
