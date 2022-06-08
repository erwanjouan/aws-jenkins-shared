# aws-jenkins-shared

https://www.lambdatest.com/blog/use-jenkins-shared-libraries-in-a-jenkins-pipeline/

## deployToAws.groovy

Job Parameters :

- (String) DOCKER_TAG
- (String) ECR_REPOSITORY
- (String) APPLICATION_PORT
- (Boolean) DESTROY

Pipeline Content:

```groovy
@Library('aws-jenkins-shared') _
deployToAws 'aws-deploy-alb-fargate'
```

Global library 'aws-jenkins-shared' declared in :
- Manage Jenkins
- Global pipeline libraries