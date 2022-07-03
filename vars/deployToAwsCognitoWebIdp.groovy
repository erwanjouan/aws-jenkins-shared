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
                steps {
                    git branch: 'main', url: 'https://github.com/erwanjouan/' + infraProjectName + '.git'
                }
            }
            stage('Deploy Infrastructure') {
                steps {
                    script{
                        def gitCommitId = params.DEV_COMMIT_ID
                        sh """
                            aws cloudformation deploy \
                                --capabilities CAPABILITY_NAMED_IAM \
                                --template-file ./infra.yaml \
                                --stack-name ${infraProjectName} \
                                --parameter-overrides \
                                    ProjectName=${infraProjectName}
                        """
                    }
                }
            }
        }
    }
}
