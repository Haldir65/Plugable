#!/bin/bash

echo "initiate deployment"


ls -al

cd ./androidProjects

ls -al 

which gradle

./gradlew wordsToEnumDebug

# ./gradlew clean assembleDebug --stacktrace --daemon