#!/bin/bash -ex

chmod +x sourcecode/gradlew

cd sourcecode

export TERM=xterm

./gradlew clean build

echo "Build artifacts: "
ls -la ./build
ls -la ./build/libs

cp ./build/* ../build
ls -la ../build
