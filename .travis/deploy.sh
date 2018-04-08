#!/bin/bash

echo "initiate deployment"


ls -al

cd ./androidProjects

ls -al 

./gradlew clean assembleDebug --stacktrace --daemon