def call(String gitHubProjectName, String deploymentName, String buildSpecFilePath){
    pipeline {
        agent any
        stages{
            stage('CodeBuild') {
                steps {
                    script{
                        def codebuildBaseProject = 'codebuild-jenkins-slave'
                        def projectDeploymentName = "${gitHubProjectName}-${deploymentName}"
                        awsCodeBuild \
                            projectName: codebuildBaseProject, \
                            buildSpecFile: "${deploymentName}/${buildSpecFilePath}", \
                            region: 'eu-west-1', \
                            credentialsType: 'jenkins', \
                            credentialsId: 'codebuild-credentials', \
                            gitCloneDepthOverride: '1', \
                            sourceControlType: 'project', \
                            sourceLocationOverride: "https://github.com/erwanjouan/${gitHubProjectName}.git", \
                            envVariables: "[{PROJECT_NAME, ${gitHubProjectName}},{DEPLOYMENT_NAME, ${deploymentName}},{PROJECT_DEPLOYMENT_NAME, ${projectDeploymentName}}]"
                    }
                }
            }
        }
    }
}