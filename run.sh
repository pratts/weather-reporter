#!/bin/sh

source .env.sh

./gradlew clean build

java -jar build/libs/weather-reporter-1.0.0.jar
