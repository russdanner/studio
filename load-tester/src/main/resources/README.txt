This project is run by Maven test phase:

	cd load-tester
	mvn test

Errors are logged in the target/surefire-reports folder. To run a single
test case, use command:

	mvn -Dtest=org.craftercms.cstudio.loadtesting.tests.RunAll test

The default configuration file is load-tester.properties. To override its
settings, create this file in your home directory:

	~/.loadtesting/load-tester.properties

You can further override settings by specifying configuration file in the
command line like this (no space character between the 2 equal signs):

	mvn -DargLine="-Dloadtesting.config=/opt/loadtest/test2.properties" test

Tests will load the default first, then the one in your home directory, and
finally the one in command line, so you don't have to duplicate all settings,
just the ones you want to change.

You can also package everything to run the tests without Maven. To build the
package file "target/load-tester.zip", do this:

	mvn -Dmaven.test.skip package

The archive can then be unzipped elsewhere and run with only Java like this:

	./run.sh org.craftercms.cstudio.loadtesting.tests.RunAll

You can specify multiple test case class names separated by spaces. If no
test case is specified, it will first look for ~/.loadtesting/testcases.txt,
then the testcases.txt file that comes with the archive. This file contains
test class names separated by new-line or white-spaces.

This script also accept additional JVM parameters before test case names:

	./run.sh -Dloadtesting.config=/opt/loadtest/test2.properties  org.craftercms.cstudio.loadtesting.tests.RunAllTimed

You can also run this script in the build environment after the package has
been built.
