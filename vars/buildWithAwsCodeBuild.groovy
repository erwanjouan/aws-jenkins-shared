def call(String gitHubProjectName){
    pipeline {
        agent any
        stages{
            stage('Build in CodeBuild') {
                steps {
                    script{
                        def codebuildBaseProject = 'codebuild-jenkins-slave'
                        awsCodeBuild \
                            projectName: codebuildBaseProject, \
                            cacheLocationOverride: "${codebuildBaseProject}-output/cache", \
                            cacheTypeOverride: 'S3', \
                            region: 'eu-west-1', \
                            credentialsType: 'jenkins', \
                            credentialsId: 'codebuild-credentials', \
                            gitCloneDepthOverride: '1', \
                            sourceControlType: 'project', \
                            sourceLocationOverride: "https://github.com/erwanjouan/${gitHubProjectName}.git", \
                            envVariables: "[{PROJECT_NAME, ${gitHubProjectName}}]"
                    }
                }
            }
        }
    }
}