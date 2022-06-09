FROM jenkins/slave

USER root

# Docker install on debian
# https://docs.docker.com/engine/install/debian/ 

RUN apt-get update && \
	apt-get install -y \
    		ca-certificates \
    		curl \
    		gnupg \
    		lsb-release
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg 
RUN echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

RUN apt-get update && \
	apt-get install -y docker-ce docker-ce-cli containerd.io
RUN systemctl enable docker
