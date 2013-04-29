<?xml version='1.0' ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" >
 
<xsl:template match="fr:accordionEx/fr:accordionEx-section/node()">
	<xsl:if test="@id = '{fieldId}'">
		<xsl:copy>
        	<xsl:apply-templates select="@*|node()"/>
    	</xsl:copy>
	</xsl:if>
</xsl:template>

<xsl:template match="@*|node()">
    <xsl:choose>
		<xsl:when test="name() = 'fr:accordionEx'">
			<xsl:apply-templates />
		</xsl:when>

		<xsl:when test="name() = 'fr:accordionEx-section'">
			<xsl:apply-templates />
		</xsl:when>
		
		<xsl:otherwise>
			 <xsl:copy>
		        <xsl:apply-templates select="@*|node()"/>
		    </xsl:copy>
		</xsl:otherwise>
	</xsl:choose>
		 
</xsl:template>
</xsl:stylesheet>