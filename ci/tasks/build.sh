#!/bin/bash -ex

chmod +x sourcecode/gradlew

cd sourcecode

export TERM=xterm

./gradlew clean build

echo "Build artifacts: "
ls -la ./build
