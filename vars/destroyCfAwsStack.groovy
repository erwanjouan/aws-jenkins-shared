def call(String infraProjectName){
    pipeline{
        agent { label 'aws-cli' }
        environment {
            AWS_ACCESS_KEY_ID = credentials('aws_access_key_id')
            AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
            AWS_DEFAULT_REGION='eu-west-1'
        }
        stages{
            stage('Destroy Infrastructure') {
                steps {
                    sh """
                        aws cloudformation delete-stack \
                            --stack-name ${infraProjectName}
                    """
                }
            }
            stage('Wait for destroy completion') {
                steps {
                    sh """
                        aws cloudformation wait stack-delete-complete \
                            --stack-name ${infraProjectName}
                    """
                }
            }
        }
    }
}
