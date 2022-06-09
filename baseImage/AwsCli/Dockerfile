FROM jenkins/slave

USER root

RUN apt-get update && \
	apt-get -y install python python-pip jq && \
	pip install awscli

