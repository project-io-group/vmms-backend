#!/bin/bash

cd sourcecode

#dockerd --host 127.0.0.1:54321
#
#export DOCKER_HOST=127.0.0.1:54321

docker run -v /var/run/docker.sock:/var/run/docker.sock & docker-compose up -d

./gradlew clean build

echo "Build artifacts: "
ls -la ./build

docker-compose down
