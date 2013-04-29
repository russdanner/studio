<#--
  This component requires the FTL variable 'model', which must be defined as
  the data model of the over-all web page. This should be done as follows in
  the FTL file that includes this component:

    <#asssign model = page.dom['/model']>

  where '/model' is the XPath for the root XML element in the model-prototype.
  Refer to the article.ftl and article.xml pair as an example.
--><#t>
<#if (model.showAboutAuthor!) == "true" &&
     ((model.authorName!)?trim?length) gt 0 &&
     ((model.authorDescription!)?trim?length) gt 0 >
	<table>
		<tr>
			<td width="10px"> </td>
			<td colspan="2"><b>About The Author</b></td>
			<td width="10px"> </td>
		</tr>
		<tr>
			<td width="10px"> </td>
			<td valign="top" class="cstudio-ice">${model.authorName}</td>
			<td valign="top"><img src="${model.authorImage}" border="0" /></td>
			<td width="10px"> </td>
		</tr>
		<tr>
			<td width="10px"> </td>
			<td valign="top" class="cstudio-ice">${model.authorDescription}</td>
			<td width="10px"> </td>
		</tr>
	</table>
</#if>
