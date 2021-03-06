def call(String infraProjectName){
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
            stage('Deploy Eb Application') {
                when { expression { return !params.DESTROY } }
                steps {
                    sh """
                         aws cloudformation deploy \
                            --capabilities CAPABILITY_NAMED_IAM \
                            --template-file ./infra.yaml \
                            --stack-name ${infraProjectName} \
                            --parameter-overrides \
                                ProjectName=${infraProjectName} \
                                MavenProjectName=${DEV_PROJECT_NAME} \
                                ArtifactOutputBucket=${DEV_PROJECT_NAME}-output \
                                ImageTag=${DOCKER_TAG}
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
