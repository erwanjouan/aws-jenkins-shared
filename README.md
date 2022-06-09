# Jenkins

## Source

https://www.youtube.com/watch?v=jaaMNK0Df8U

1. run the jenkins master controller
```
docker-compose up
```

2. create a new user (jenkins:jenkins) and install Docker plugin

3. Manage Jenkins > Configure System > Cloud > Add a new Cloud : Docker > Docker cloud details

run docker-proxy image

```
make run-docker-proxy
```

docker run -p 3375:2375 -v /var/run/docker.sock:/var/run/docker.sock -d shipyard/docker-proxy
 
Docker HOST URI
tcp://docker.for.mac.localhost:3375
and test it

4. Add Docker Agent templates

Name: docker-agent-1
Docker Image: jenkins/slave

Registry authentication
add docker hub user & password
to allow jenkins master to download jenkins slave

instance capacity: 2

Apply / Save

5. Disable master node executors

Master provides 2 executors by defaut, we need to disable them.

Manage Jenkins / Manage Node / Master / configure / 
set 0 to # of executors

We do not see build executors anymore

## Add a new Base Image

1. Create a public repo on DockerHub

2. Make a base image from Jenkins slave image. E.g. Dockerfile for maven base image

```Dockerfile
FROM jenkins/slave

USER root

RUN apt-get update && \
	apt-get -y install maven
```

and push it to DockerHub

3. Add the Docker image and maven repo cache

- Build executor status (Etat du lanceur de compilations)
- Configure Clouds
- Docker agent templates
- Add Docker templates
	- Label : maven
	- Name : maven
	- Docker image : rone56/jenkins-maven
	- Instance capacity : 2
	- Remote File System root : /home/jenkins
	- (Container Settings)
		- Mounts: type=bind,source=/Users/erwanjouan/.m2,destination=/root/.m2

## Add new Build project

Create a new "Pipeline Multibranch" item in Jenkins, Jenkinsfile at the root of the project.
Configure the project to retrieve the project from Github:
- Credentials : none
- URL : https with Personal access token
	- https://<personal_access_token>@github.com/erwanjouan/aws-dev-spring-boot-jar.git
## Moving docker-compose 

Moving this file in another folder can make the persistent data to be lost.
Docker takes the parent folder as project name and expects it to be the same.

https://www.reddit.com/r/docker/comments/uvalnt/how_to_move_dockercompose_files_without_losing/

