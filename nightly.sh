#!/bin/bash
for f in alfresco-svcs* cstudio-share-war cstudio-publishing-receiver load-tester; do
  cd $f;
  mvn -q -DskipTests=true $MAVEN_PROFILE install
  if (($?)); then exit $?; fi
  cd ..
done
