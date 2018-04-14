#!/bin/bash

cd sourcecode

./gradlew clean build

echo "Build artifacts: "
ls -la ./build
