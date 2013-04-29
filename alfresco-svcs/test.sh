#!/bin/bash

PROJ=`dirname $0`
if [[ $PROJ == "." ]]; then PROJ=$PWD; fi

if [[ _$1 == "_-u" ]]; then
  shift
else
  mvn test-compile
fi
if [[ _$1 == "_-d" ]]; then
  shift
  DEBUG=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
fi
if [[ _$1 == "_-s" ]]; then
  shift
  DEBUG=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=y
fi

if (($#)); then
  TESTS="$@"
else
  cd src/test/java
  echo Found tests:
  for t in `find * -type f -name \*Test.java`; do
    t=${t//\//.}
    t=${t%.java}
    echo $t
    TESTS="$TESTS $t"
  done
  echo .
fi

if [[ "_$ALF_SDK" == "_" ]]; then
  echo This test script requires the ALF_SDK environment variable to point to the Alfresco SDK folder.
  exit 1
fi

cd $ALF_SDK
global=lib/server/config/alfresco-global.properties
if [ ! -f $global ]; then
  echo 'dir.root=./alf_data' >> $global
  echo 'index.recovery.mode=AUTO' >> $global
  echo 'hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect' >> $global
  echo 'db.name=alfresco_test' >> $global
  echo 'db.username=alfresco' >> $global
  echo 'db.password=alfresco' >> $global
  echo 'db.driver=org.gjt.mm.mysql.Driver' >> $global
  echo 'db.url=jdbc:mysql://localhost:3306/${db.name}?useUnicode=yes&characterEncoding=UTF-8' >> $global
fi
if [ ! -d alf_data ]; then mkdir alf_data; fi

JARS=\
~/.m2/repository/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar:\
~/.m2/repository/org/apache/ibatis/ibatis-sqlmap/2.3.4.726/ibatis-sqlmap-2.3.4.726.jar:\
~/.m2/repository/javolution/javolution/5.5.0/javolution-5.5.0.jar:\
~/.m2/repository/net/sf/json-lib/json-lib/2.2.3/json-lib-2.2.3.jar

for f in `ls lib/server/alfresco-*.jar`; do JARS=${JARS}:$f; done
for f in `find lib/server/dependencies -name \*.jar`; do JARS=${JARS}:$f; done

java -XX:MaxPermSize=512m -Xms128m -Xmx768m -Dfile.encoding=UTF-8 -cp $PROJ/target/test-classes:$PROJ/target/classes:lib/server/source:${JARS}:lib/server/config $DEBUG org.junit.runner.JUnitCore $TESTS
