def call(String codeBuildProjectName, String gitHubProjectName){
    pipeline {
        agent any
        stages{
            stage('Build in CodeBuild') {
                steps {
                    script{
                        awsCodeBuild \
                            projectName: codeBuildProjectName, \
                            cacheLocationOverride: "${codeBuildProjectName}-output/cache", \
                            cacheTypeOverride: 'S3', \
                            region: 'eu-west-1', \
                            credentialsType: 'jenkins', \
                            credentialsId: 'codebuild-credentials', \
                            gitCloneDepthOverride: '1', \
                            sourceControlType: 'project', \
                            sourceLocationOverride: "https://github.com/erwanjouan/${gitHubProjectName}.git", \
                            buildSpecFile: getBuildSpecContent()
                    }
                }
            }
        }
    }
}

String getBuildSpecContent(){
    def path = pwd()
    def filePath = path + "/buildspec.yml"
    def buildSpecContent = readFile filePath
    return buildSpecContent
}