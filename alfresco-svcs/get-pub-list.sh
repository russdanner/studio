#!/bin/bash
#
# This script create a flat list of all content items, then use that list to
# construct a JSON object string for publishing. To use this script:
#
#  1) Copy the work-area folder of a site via WebDAV to the local machine.
#
#  2) While in that local work-area folder, run this script like this:
#
#       ~/cstudio-2-2-x/alfresco-svcs/get-pub-list.sh > ~/pub-list.json
#
#  3) Then submit the JSON object string for publishing:
#
#       curl -d @$HOME/pub-list.json -u admin:admin -H "Content-Type: application/json" http://localhost:8080/alfresco/service/cstudio/wcm/workflow/go-live?site=test-site
#
#     Be sure to specify the matching site name at the very end of the URI.
#
echo '{"items":['

IFS=$'\n'
for f in `find * -type f`; do
  echo ' {'
  for s in assets children components documents deletedItems renderingTemplates levelDescriptors; do
    echo "  \"${s}\":[],"
  done
  echo '  "deleted":false,'
  echo '  "now":false,'
  echo '  "scheduledDate":"",'
  echo '  "submittedForDeletion":false,'
  echo '  "submitted":false,'
  echo '  "inProgress":true,'
  echo '  "reference":false,'
  echo "  \"uri\":\"/${f}\","
  echo '  "user":""'
  echo ' },'
done

echo '],'
echo '"submissionComment":"",'
echo '"publishChannel":{"index":"0","name":"Sample Group"},'
echo '"status":{"channels":[],"message":""},'
echo '"now":"true",'
echo '"scheduledDate":""}'
