@author Ritesh Trivedi
Module for building alfresco.war file with all the AMPs applied. This is to avoid using MMT tool outside of the build process.
To add additional AMP files to be applied to the war, add them as dependencies similar to already existing AMP.

Add all the extension files under resources/alfresco/extension folder to be copied over to the WEB-INF/classes/alfresco/extension
folder on the resulting alfresco.war file

modified
