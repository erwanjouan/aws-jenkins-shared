def call(String infraProjectName, String devProjectName){
    pipeline{
        agent { label 'aws-cli' }
        environment {
            AWS_ACCESS_KEY_ID = credentials('aws_access_key_id')
            AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
            AWS_DEFAULT_REGION='eu-west-1'
        }
        stages{
            stage('Checkout Source') {
                when { expression { return !params.DESTROY } }
                steps {
                    git branch: 'main', url: 'https://github.com/erwanjouan/' + infraProjectName + '.git'
                }
            }
            stage('Deploy Infrastructure') {
                when { expression { return !params.DESTROY } }
                steps {
                    sh """
                        aws cloudformation deploy \
                            --capabilities CAPABILITY_NAMED_IAM \
                            --template-file ./infra.yaml \
                            --stack-name ${infraProjectName} \
                            --parameter-overrides \
                                ProjectName=${infraProjectName} \
                                ProjectVersion=${PROJECT_VERSION}
                    """
                }
            }
            stage('Trigger CodeDeploy') {
                when { expression { return !params.DESTROY } }
                steps {
                    sh """
                        aws deploy create-deployment \
                            --application-name ${infraProjectName} \
                            --deployment-group-name ${infraProjectName} \
                            --s3-location bucket=${devProjectName}-output,key=${devProjectName}/revision.zip,bundleType=zip
                    """
                }
            }
            stage('Destroy') {
                when { expression { return params.DESTROY } }
                steps {
                    sh "aws cloudformation delete-stack --stack-name ${infraProjectName}"
                }
            }
        }
    }
}
