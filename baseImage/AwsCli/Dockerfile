FROM jenkins/slave

USER root

# AWS cli

RUN apt-get update && \
	apt-get -y install python python-pip jq python3 python3-venv virtualenv python3-virtualenv && \
	pip install awscli

# EB cli 

RUN git clone https://github.com/aws/aws-elastic-beanstalk-cli-setup.git

RUN python3 ./aws-elastic-beanstalk-cli-setup/scripts/ebcli_installer.py

