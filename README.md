# GraalVM + Lambda

The `Dockerfile` contains the build to build the native image and it can be built with:

```bash
$ docker rmi pac-app
$ docker build . -t pac-app
$ mkdir -p build
$ docker run --rm --entrypoint cat pac-app  /home/application/function.zip > build/function.zip
```

Which will add the function deployment ZIP file to `build/function.zip`. You can run function locally using [SAM](https://github.com/awslabs/aws-sam-cli/)

```bash
$ ./sam-local.sh
```

Or you can deploy it to AWS via the console or CLI:

```bash
aws lambda delete-function --function-name pac-app  
aws lambda create-function --function-name pac-app-test --zip-file fileb://build/function.zip \
--handler function.handler --runtime provided.al2 \
--role arn:aws:iam::536824749084:role/spring-native-PACFunctionRole-1538SC6AM9GDN
```

Thes function can be invoked by sending an API Gateway Proxy request. For example:

```bash
aws lambda invoke --function-name pac-app --payload '{"resource": "/{proxy+}", "path": "/", "httpMethod": "GET"}' build/response.txt
cat build/response.txt
```

and response should be something like:

```json
{"statusCode":200,"multiValueHeaders":{},"body":"test ok","isBase64Encoded":false}
```

Reference:
```txt
https://www.tabnine.com/code/java/methods/io.micronaut.http.annotation.Controller/%3Cinit%3E
```
