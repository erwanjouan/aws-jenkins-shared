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
                        //aws cloudformation wait stack-delete-complete \
                        //    --stack-name ${infraProjectName}

                        local stack=${infraProjectName}
                        local lastEvent
                        local lastEventId
                        local stackStatus=$(aws cloudformation describe-stacks --stack-name $stack | jq -c -r .Stacks[0].StackStatus)

                        until \
                            [ "$stackStatus" = "CREATE_COMPLETE" ] \
                            || [ "$stackStatus" = "CREATE_FAILED" ] \
                            || [ "$stackStatus" = "DELETE_COMPLETE" ] \
                            || [ "$stackStatus" = "DELETE_FAILED" ] \
                            || [ "$stackStatus" = "ROLLBACK_COMPLETE" ] \
                            || [ "$stackStatus" = "ROLLBACK_FAILED" ] \
                            || [ "$stackStatus" = "UPDATE_COMPLETE" ] \
                            || [ "$stackStatus" = "UPDATE_ROLLBACK_COMPLETE" ] \
                            || [ "$stackStatus" = "UPDATE_ROLLBACK_FAILED" ]; do
                            
                            #[[ $stackStatus == *""* ]] || [[ $stackStatus == *"CREATE_FAILED"* ]] || [[ $stackStatus == *"COMPLETE"* ]]; do
                            lastEvent=$(aws cloudformation describe-stack-events --stack $stack --query 'StackEvents[].{ EventId: EventId, LogicalResourceId:LogicalResourceId, ResourceType:ResourceType, ResourceStatus:ResourceStatus, Timestamp: Timestamp }' --max-items 1 | jq .[0])
                            eventId=$(echo "$lastEvent" | jq -r .EventId)
                            if [ "$eventId" != "$lastEventId" ]
                            then
                                lastEventId=$eventId
                                echo $(echo $lastEvent | jq -r '.Timestamp + "\t-\t" + .ResourceType + "\t-\t" + .LogicalResourceId + "\t-\t" + .ResourceStatus')
                            fi
                            sleep 3
                            stackStatus=$(aws cloudformation describe-stacks --stack-name $stack | jq -c -r .Stacks[0].StackStatus)
                        done

                        echo "Stack Status: $stackStatus"
                    """
                }
            }
        }
    }
}
