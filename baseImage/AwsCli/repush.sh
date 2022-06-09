#!/bin/sh
docker login
docker build -t rone56/jenkins-aws-cli:latest .
docker push rone56/jenkins-aws-cli:latest
