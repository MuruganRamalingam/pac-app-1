#!/bin/sh

mkdir -p build
docker run --rm --entrypoint cat pac-app  /home/application/function.zip > build/function.zip

sam local start-api -t sam.yaml -p 3000 --docker-network host

aws lambda create-function --function-name pac-app --zip-file fileb://build/function.zip --handler function.handler --runtime provided --role arn:aws:iam::536824749084:role/spring-native-PACFunctionRole-1538SC6AM9GDN

