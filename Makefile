DOCKER_API_VERSION:=v1.41

update-aws:
	docker build -t rone56/jenkins-aws-cli:latest baseImage/AwsCli/ && \
	docker push rone56/jenkins-aws-cli:latest
get-api-version:
	docker version | grep API
check:	
	docker exec jenkins curl http://docker.for.mac.localhost:3375/$(DOCKER_API_VERSION)/info

start:
	docker-compose up
