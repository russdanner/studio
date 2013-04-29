<#include "/templates/system/common/cstudio-support.ftl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<#include "/templates/web/components/common-head.ftl">
	</head>
	<body>
		<div id="wrap">
			<div class="top_corner"></div>
			
			<div id="main_container">
				
				<#include "/templates/web/components/common-header.ftl" />
				
				<div class="center_content_pages">
					<table>
						<tr>
							<td width="10xp"> </td>
							<td valign="top">

                           <form action="">
								<table>
								
                           			<#list model.formFields.item as field>
                               			<tr>
                               				<td>${field.fieldTitle}</td><td>
                               				<#if field.fieldType == "state">
                               				
                               				   <select name="state" id="state">
    <option value="AL">Alabama</option>
    <option value="AK">Alaska</option>
    <option value="AZ">Arizona</option>
    <option value="AR">Arkansas</option>
    <option value="CA">California</option>
    <option value="CO">Colorado</option>
    <option value="CT">Connecticut</option>
    <option value="DE">Delaware</option>
    <option value="DC">District of Columbia</option>
    <option value="FL">Florida</option>
    <option value="GA">Georgia</option>
    <option value="HI">Hawaii</option>
    <option value="ID">Idaho</option>
    <option value="IL">Illinois</option>
    <option value="IN">Indiana</option>
    <option value="IA">Iowa</option>
    <option value="KS">Kansas</option>
    <option value="KY">Kentucky</option>
    <option value="LA">Louisiana</option>
    <option value="ME">Maine</option>
    <option value="MD">Maryland</option>
    <option value="MA">Massachusetts</option>
    <option value="MI">Michigan</option>
    <option value="MN">Minnesota</option>
    <option value="MS">Mississippi</option>
    <option value="MO">Missouri</option>
    <option value="MT">Montana</option>
    <option value="NE">Nebraska</option>
    <option value="NV">Nevada</option>
    <option value="NH">New Hampshire</option>
    <option value="NJ">New Jersey</option>
    <option value="NM">New Mexico</option>
    <option value="NY">New York</option>
    <option value="NC">North Carolina</option>
    <option value="ND">North Dakota</option>
    <option value="OH">Ohio</option>
    <option value="OK">Oklahoma</option>
    <option value="OR">Oregon</option>
    <option value="PA">Pennsylvania</option>
    <option value="RI">Rhode Island</option>
    <option value="SC">South Carolina</option>
    <option value="SD">South Dakota</option>
    <option value="TN">Tennessee</option>
    <option value="TX">Texas</option>
    <option value="UT">Utah</option>
    <option value="VT">Vermont</option>
    <option value="VA">Virginia</option>
    <option value="WA">Washington</option>
    <option value="WV">West Virginia</option>
    <option value="WI">Wisconsin</option>
    <option value="WY">Wyoming</option>
    </select>
<#elseif field.fieldType="boolean">
<input type="checkbox">
<#else>
<input>
</#if>
</td>
    
                               		    </tr>
		                           </#list>
		                        </table>
		                    </form>
</td>
							<td width="10xp"> </td>
						</tr>
					</table>
					<#include "/templates/web/components/common-footer.ftl" />
				</div>
			</div>
		</div>
        <#include "/templates/web/components/sch.ftl" />
	</body>
</html>
