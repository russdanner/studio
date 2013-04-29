For MAC OSX 10.5, the following will ensure that your development 
and Tomcat runtime will use the proper version of the JDK (1.6):

Use the env.mac.sh file to set your JAVA_HOME environment variable in the shell in which you compile:

$ . env.mac.sh
$ echo $JAVA_HOME
/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home

In addition, you will need to edit the $ALF31_INSTALL/alfresco.sh file.  Comment out the following line (put the # at the beginning of the line):

#export JAVA_HOME=""
