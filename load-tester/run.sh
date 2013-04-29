#!/bin/bash

top=${0%/*}

if [[ -f $top/target/load-tester.zip ]]; then # Running in build environment
  if [[ ! -d $top/target/classes/lib ]]; then
    unzip $top/target/load-tester.zip lib/\* -d $top/target/classes
  fi
  top=$top/target/classes
fi

while [[ "-$1" == --* ]]; do
  OPT="$OPT $1"
  shift
done

if (($#)); then
  TESTS="$@"
elif [[ -f ~/.loadtesting/testcases.txt ]]; then
  TESTS=`cat ~/.loadtesting/testcases.txt`
else
  TESTS=`cat $top/testcases.txt`
fi

java -cp "$top/lib/*:$top" $OPT org.junit.runner.JUnitCore $TESTS
