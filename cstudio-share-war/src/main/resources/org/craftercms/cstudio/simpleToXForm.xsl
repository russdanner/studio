<?xml version='1.0' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xforms="http://www.w3.org/2002/xforms"
 				xmlns:f="http://orbeon.org/oxf/xml/formatting" xmlns:xhtml="http://www.w3.org/1999/xhtml"
 				xmlns:xxforms="http://orbeon.org/oxf/xml/xforms" xmlns:xi="http://www.w3.org/2001/XInclude"
 				xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" xmlns:xs="http://www.w3.org/2001/XMLSchema"
 				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:widget="http://orbeon.org/oxf/xml/widget"
 				xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
 				xmlns:xbl="http://www.w3.org/ns/xbl" xmlns:fr="http://orbeon.org/oxf/xml/form-runner">
 

<xsl:template match="/form"> 
 	<xhtml:html xmlns:xforms="http://www.w3.org/2002/xforms"
 				xmlns:f="http://orbeon.org/oxf/xml/formatting" xmlns:xhtml="http://www.w3.org/1999/xhtml"
 				xmlns:xxforms="http://orbeon.org/oxf/xml/xforms" xmlns:xi="http://www.w3.org/2001/XInclude"
 				xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" xmlns:xs="http://www.w3.org/2001/XMLSchema"
 				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:widget="http://orbeon.org/oxf/xml/widget"
 				xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
 				xmlns:xbl="http://www.w3.org/ns/xbl" xmlns:fr="http://orbeon.org/oxf/xml/form-runner">
    
		<xhtml:head>
        	<xforms:model 	id="main" 
        					xxforms:session-heartbeat="true" 
                			xxforms:show-error-dialog="false"
                        	xxforms:external-events="submit-save submit-preview submit-cancel">
				
				<xforms:instance id="instance">
					<dynamic></dynamic>
				</xforms:instance>

				<xforms:bind nodeset="instance('instance')" readonly="instance('cs-formcontrol')/renderParams/readonly='true'">
					<xsl:for-each select="//field">
						<xforms:bind>
							<xsl:attribute name="nodeset">
								<xsl:value-of select="./@ref"/>
							</xsl:attribute>						
							<xsl:if test="./@default">
								<xsl:attribute name="default">
									<xsl:value-of select="./@default"/><xsl:text>()</xsl:text>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="./@xformType">
								<xsl:attribute name="type">
									<xsl:value-of select="./@xformType"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="./@required">
								<xsl:attribute name="required">
									<xsl:value-of select="./@required"/><xsl:text>()</xsl:text>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="./@constraint">
								<xsl:attribute name="constraint">
									<xsl:value-of select="./@constraint"/>
								</xsl:attribute>
							</xsl:if>
						</xforms:bind>
					</xsl:for-each>
				</xforms:bind>

            	{standardRepoSubmissionHanders} 
            
        	</xforms:model>
    	</xhtml:head>

    	<xhtml:body class="body">
	        <fr:entity-avm id="entityId">
	        	<xsl:attribute name="ref">
					<xsl:value-of select="./@fileNameRef"/>
				</xsl:attribute>
	        </fr:entity-avm>

    	    <fr:page-metadata ref="instance('instance')" />

	        <fr:accordionEx>
			    <xforms:label><xsl:value-of select="./@label"/></xforms:label>
	        	<xsl:apply-templates select="section"/>
	        </fr:accordionEx>
        
	        {xml-inspector-widget} 
	
	        <fr:formctrl-wcm>
				<xsl:if test="./@enablePreview">
					<xsl:attribute name="enablePreview">
						<xsl:value-of select="./@enablePreview"/>
					</xsl:attribute>
				</xsl:if>
	        </fr:formctrl-wcm>

		</xhtml:body>
	</xhtml:html>

</xsl:template>

<xsl:template match="section"> 
	<fr:accordionEx-section>
		<xforms:label><xsl:value-of select="./@label"/></xforms:label>
		<xsl:apply-templates select="field"/>
	</fr:accordionEx-section>
</xsl:template>

<xsl:template match="field[@type='input']"> 
	<div class="cstudio-xforms-widget-wrapper">
		<fr:input-counted>
			<xsl:attribute name="id">
				<xsl:value-of select="./@id"/>
			</xsl:attribute>
			<xsl:attribute name="ref">
				<xsl:value-of select="./@ref"/>
			</xsl:attribute>

			<xsl:if test="./@max">
				<xsl:attribute name="max">
					<xsl:value-of select="./@max"/>
				</xsl:attribute>
			</xsl:if>
			<xforms:label><xsl:value-of select="./@label"/></xforms:label>
			<xsl:if test="./alert">
				<xforms:alert><xsl:value-of select="./alert"/></xforms:alert>				
			</xsl:if>
			<xsl:if test="./hint">
				<xforms:hint><xsl:value-of select="./hint"/></xforms:hint>				
			</xsl:if>
	
		</fr:input-counted>                         
	</div>							
</xsl:template>

<xsl:template match="field"> 
	<div class="cstudio-xforms-widget-wrapper">
		<xsl:value-of select="./@label"/>
	</div>							
</xsl:template>


</xsl:stylesheet>