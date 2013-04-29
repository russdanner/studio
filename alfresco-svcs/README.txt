1. do mvn package. To run test cases properly, open alfresco-svcs.properties and update properties to match your local settings
2. copy craftercms-alfresco-svcs-x.x.x.amp to ${alfresco}/amps
3. run apply_amps.sh in ${alfresco}/amps
4. copy files at /resources/alfresco/extension to ${tomcat}/shared/classes/alfresco/extension folder. Update properties if needed.
5. upload files at /resources/alfresco/content/config/services/ to Company Home/cstudio/config/services/

Related documents
http://1wiki.craftercms.net/display/EMO/Alfresco+Service+Catalog
http://1wiki.craftercms.net/display/EMO/KC+Deployment

