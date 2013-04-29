This Maven project builds an Alfresco Share WAR file ready for deployment to an existing Alfresco Enterprise 3.1 installation.

We are assuming that $ALF31_INSTALL is the installation location of Alfresco 3.1 Enterprise.  Usually this is ~/Alfresco

   To build the war:

	mvn clean package

  To deploy it to the Alfresco:

  If necessary, look for the running process and kill it first:

	ps guax|grep java|grep $ALF31_INSTALL
	kill -9 XYZ

  Deploy the WAR:

	rm -rf  $ALF31_INSTALL/tomcat/webapps/share-cx
	cp target/alfresco-share-3.1-Enterprise-BRANCH-cx.war  $ALF31_INSTALL/tomcat/webapps/share-cx.war

  Start Alfresco. 

	cd $ALF31_INSTALL
	./alfresco.sh start

  Monitor progress:

	tail -f tomcat/logs/catalina.out

  You should have a working Alfresco Share web application, here:

     http://localhost:8080/share-cx
