FROM jenkins/inbound-agent:jdk17
USER root
RUN mkdir -p /usr/share/man/man1

# install maven
RUN apt-get update && \
        apt-get -y install maven

# permissions for mounting a volume that contains  maven repo
RUN mkdir /home/jenkins/.m2
RUN chown jenkins:jenkins /home/jenkins/.m2

USER jenkins