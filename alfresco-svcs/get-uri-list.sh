#!/bin/bash
#
# This script prints out a JSON object string that represents all content items
# with keys: 'uri', 'children' and 'deep', which can be used to submit
# Copy/Paste commands. To use this script:
#
#  1) Copy/mount the work-area folder of a site via WebDAV to the local machine.
#
#  2) While in that local work-area folder, run this script like this:
#
#       ~/cstudio-2-2-x/alfresco-svcs/get-uri-list.sh > ~/uri-list.json
#
#  3) To paste all content into a site's sub-folder "/temp":
#
#       curl -d @../uri-list.json -u admin:admin -H "Content-Type: application/json" "http://localhost:8080/alfresco/service/cstudio/wcm/clipboard/paste?site=test-site&destination=/temp&cut=false"
#
#     Be sure to specify the matching site name after site= in the URI. Also, don't paste
#     into a sub-folder in the list. It will cause an infinite copy loop and fail. 
#
#     Note that paste does not require you to call copy first, which can be done like this:
#
#       curl -d @$HOME/uri-list.json -u admin:admin -H "Content-Type: application/json" http://localhost:8080/share/service/cstudio/services/clipboard/copy?site=test-site
#
#  4) You can use the -1 parameter to print just the top level files and folders with
#     the "deep":true property to Copy/Paste the entire site content tree:
#
#       ~/cstudio-2-2-x/alfresco-svcs/get-uri-list.sh -1 > ~/uri-list.json
#
echo -n '{"item":['

IFS=$'\n'
unset comma

if [[ "-$1" == "--1" ]] ; then deep=1; shift; else deep=0; fi

if [[ $# == 0 ]]; then top=*; else top=$@; fi

for f in `find $top -depth 0 -type f`; do # Top level files
  echo $comma
  echo ' {'
  echo "  \"uri\":\"/${f}\""
  echo -n ' }'
  comma=,
done

if [ $deep == 1 ] ; then # Single level with deep=true

for f in `find $top -depth 0 -type d`; do
  echo $comma
  echo ' {'
  echo '  "deep":true,'
  echo "  \"uri\":\"/${f}\""
  echo -n ' }'
  comma=,
done

else # Full depth tree using children=[...]

function go_deep() # $1:parent, $2:indent, $3:comma
{
  local f comma= parent=$1
  if [ -f $1/index.xml ]; then
    parent=$1/index.xml
  fi
  echo $3
  echo "$2{"
  echo "$2 \"uri\":\"/${parent}\","
  echo -n "$2 \"children\":["
  for f in `find $1 -depth 1 -type f`; do # Current level files
    if [[ "$f" == "$parent" ]]; then
      continue
    fi
    echo $comma
    echo "$2  {"
    echo "$2   \"uri\":\"/${f}\""
    echo -n "$2  }"
    comma=,
  done
  for f in `find $1 -depth 1 -type d`; do # Current level folders
    go_deep $f "$2  " $comma
    comma=,
  done
  echo ']'
  echo -n "$2}"
}

for f in `find $top -depth 0 -type d`; do
  go_deep $f " " $comma
  comma=,
done

fi

echo ']}'
