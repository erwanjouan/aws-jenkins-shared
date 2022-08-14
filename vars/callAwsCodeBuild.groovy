def call(String gitHubProjectName, String deploymentName, String buildSpecFilePath){
    pipeline {
        agent any
        stages{
            stage('CodeBuild') {
                steps {
                    script{
                        def codebuildBaseProject = 'codebuild-jenkins-slave'
                        def bucketName = "${gitHubProjectName}-${deploymentName}"
                        def bucketCacheName = "${codebuildBaseProject}-output"
                        awsCodeBuild \
                            projectName: codebuildBaseProject, \
                            cacheLocationOverride: "${bucketCacheName}/cache", \
                            cacheTypeOverride: 'S3', \
                            buildSpecFile: "${deploymentName}/${buildSpecFilePath}", \
                            region: 'eu-west-1', \
                            credentialsType: 'jenkins', \
                            credentialsId: 'codebuild-credentials', \
                            gitCloneDepthOverride: '1', \
                            sourceControlType: 'project', \
                            sourceLocationOverride: "https://github.com/erwanjouan/${gitHubProjectName}.git", \
                            envVariables: "[{PROJECT_NAME, ${gitHubProjectName}},{BUCKET_NAME, ${bucketName}}]"
                    }
                }
            }
        }
    }
}