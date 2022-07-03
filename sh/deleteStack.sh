#!/bin/sh
cloudformation_tail() {
  local stack="$1"
  local region="$2"
  local lastEvent
  local lastEventId
  local stackStatus=$(aws cloudformation describe-stacks --region $region --stack-name $stack | jq -c -r .Stacks[0].StackStatus)

  until \
	[ "$stackStatus" = "ACREATE_COMPLETE" ] \
	|| [ "$stackStatus" = "CREATE_FAILED" ] \
	|| [ "$stackStatus" = "DELETE_COMPLETE" ] \
	|| [ "$stackStatus" = "DELETE_FAILED" ] \
	|| [ "$stackStatus" = "ROLLBACK_COMPLETE" ] \
	|| [ "$stackStatus" = "ROLLBACK_FAILED" ] \
	|| [ "$stackStatus" = "UPDATE_COMPLETE" ] \
	|| [ "$stackStatus" = "UPDATE_ROLLBACK_COMPLETE" ] \
	|| [ "$stackStatus" = "UPDATE_ROLLBACK_FAILED" ] \
	|| [ "$stackStatus" = "" ]; do
	
	aws cloudformation describe-stack-events --region $region --stack $stack --query 'StackEvents[].{ Timestamp: Timestamp, ResourceStatus:ResourceStatus, ResourceType:ResourceType, LogicalResourceId:LogicalResourceId }' | jq -r '(.[0] | keys_unsorted) as $keys | $keys, map([.[ $keys[] ]])[] | @csv' | grep DELETE
	sleep 3
	stackStatus=$(aws cloudformation describe-stacks --region $region --stack-name $stack | jq -c -r .Stacks[0].StackStatus)
  done

  echo "Stack Status: $stackStatus"
}
cloudformation_tail $1 $2