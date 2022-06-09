def call(String projectName, String devProjectName){
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
                    git branch: 'main', url: 'https://github.com/erwanjouan/' + projectName + '.git'
                }
            }
            stage('Deploy Infrastructure') {
                when { expression { return !params.DESTROY } }
                steps {
                    sh """
                        aws cloudformation deploy \
                            --capabilities CAPABILITY_NAMED_IAM \
                            --template-file ./infra.yaml \
                            --stack-name ${projectName} \
                            --parameter-overrides \
                                ProjectName=${projectName} \
                                MavenProjectName=${devProjectName} \
                                ApplicationPort=${APPLICATION_PORT} \
                                ImageTag=${DOCKER_TAG}
                    """
                }
            }
            stage('Destroy') {
                when { expression { return params.DESTROY } }
                steps {
                    sh "aws cloudformation delete-stack --stack-name ${projectName}"
                }
            }
        }
    }
}
