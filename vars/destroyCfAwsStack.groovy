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
            
            stage('Check deletion') {
                steps{
                    writeFile file:'deleteStack.sh', text:libraryResource("sh/deleteStack.sh")
                    sh """
                        set +x
                        chmod +x deleteStack.sh
                        ./deleteStack.sh ${infraProjectName} ${AWS_DEFAULT_REGION}
                    """
                }
            }
        }
    }
}
