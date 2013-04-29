<script>
   ${jsonDependencies}
</script>

<div class="acnBox" id="acnVersionWrapper" style="width: 825px; height:415px;">
	<h3>Submit to Go Live</h3>
	<p>When would you like the checked item(s) to Go Live?</p>
	<br/>
	<div class="formRow radio">
		<div class="field"><input type="radio" name="now" id="now" checked="checked" class="radiobutton"/></div>
		<label>As soon as possible</label>
	</div>
	<div class="formRow radio">
		<div class="field"><input type="radio" name="now" id="settime" class="radiobutton1" id="settime"/></div>
    <label>At the requested time:</label>
    <div class="textField">
    <div style="float: left;">
    	<input type="text" class="submitdate submitdtext datePickerInput" id="datepicker" value="Date..."/>
    </div>
    <div style="position: relative; float: left;">
      	<input type="text" class="submittime submitdtext" id="timepicker" value="Time..."/>
     </div>
     <div class="timeButtonContainer">
      	<input id="timeIncrementButton" type="submit" value=""/>
      	<input id="timeDecrementButton" type="submit" value=""/>
      </div>
      <div style="float: left; margin-top: 3px"><span id="timeZone">EST</span> (<a id="schedulePolicy" href="#">Scheduling Policy</a>)</div>
    </div>
	</div>

  <div class="formRow radio padTop padBottom">
  	<div class="field"><input type="checkbox" checked="checked" name="email" id="email" class="radiobutton1"/></div>
    <label>Email me when my items go live</label>
  </div>

  <div class="acnScroll acnScrollPadTop">
    <h5>
        <span class="left">Page</span>
        <span class="right">Original Time</span>
    </h5>
    <div id="acnScrollBoxDiv" class="acnScrollBox" style="height:174px">
    	<table class="liveTable acnLiveTable">
      	<#list dependencies.items as item>
			<#if item.disabled == true>
				<#assign disabledCss = "strike" >
			<#else>
				<#assign disabledCss = "" >
			</#if>
        <tr>
		<td class="acnLiveTableFileName">
			<div class="acnLiveTableCheckbox"><input id="${item.uri}" class="parent-${item.browserUri}" value="${item.browserUri}" type="checkbox" checked="checked"/></div>
				<#if item.document == true>
					<#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true>
						<div class="documentSubmittedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true && item.scheduled == true >
						<div class="documentDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true>
						<div class="documentDeleted icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true && item.scheduled == true>
						<div class="documentDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true>
						<div class="documentDeleted icon ${disabledCss}"/>
					<#elseif item.scheduled == true>
						<div class="documentScheduled icon ${disabledCss}"/>
					<#elseif item.inProgress == true>
						<div class="documentInProgress icon ${disabledCss}"/>
					<#elseif item.submitted == true>
						<div class="documentSubmitted icon ${disabledCss}"/>
					<#else>
						<div class="madeLiveDocument icon ${disabledCss}"/>
					</#if>
				<#elseif item.floating == true>
					<#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true>
						<div class="ttFloatingSubmittedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true && item.scheduled == true>
						<div class="ttFloatingDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true>
						<div class="ttFloatingDeleted icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true && item.scheduled == true>
						<div class="ttFloatingDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true>
						<div class="ttFloatingDeleted icon ${disabledCss}"/>
					<#elseif item.scheduled == true>
						<div class="ttFloatingScheduled icon ${disabledCss}"/>
					<#elseif item.inProgress == true>
						<div class="ttFloatingInProgress icon ${disabledCss}"/>
					<#elseif item.submitted == true>
						<div class="ttFloatingSubmitted icon ${disabledCss}"/>
					<#else>
						<div class="ttFloating icon ${disabledCss}"/>
					</#if>
				<#elseif item.component == true>
					<#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true>
						<div class="componentSubmittedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true && item.scheduled == true>
						<div class="componentDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true>
						<div class="componentDeleted icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true && item.scheduled == true>
						<div class="componentDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true>
						<div class="componentDeleted icon ${disabledCss}"/>
					<#elseif item.scheduled == true>
						<div class="componentScheduled icon ${disabledCss}"/>
					<#elseif item.inProgress == true>
						<div class="componentInProgress icon ${disabledCss}"/>
					<#elseif item.submitted == true>
						<div class="componentSubmitted icon ${disabledCss}"/>
					<#else>
						<div class="madeLiveComponent icon ${disabledCss}"/>
					</#if>
				<#else>
					<#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true>
						<div class="ttSubmittedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true && item.scheduled == true>
						<div class="ttDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.deleted == true>
						<div class="ttDeleted icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true && item.scheduled == true>
						<div class="ttDeletedScheduled icon ${disabledCss}"/>
					<#elseif item.submittedForDeletion == true>
						<div class="ttDeleted icon ${disabledCss}"/>
					<#elseif item.scheduled == true>
						<div class="ttScheduled icon ${disabledCss}"/>
					<#elseif item.inProgress == true>
						<div class="ttInProgress icon ${disabledCss}"/>
					<#elseif item.submitted == true>
						<div class="ttSubmitted icon ${disabledCss}"/>
					<#else>
						<div class="navPage icon ${disabledCss}"/>
					</#if>
				</#if>
			<div title="${item.internalName}" class="liveItemName liveFirstCol12 ${disabledCss}">
				<#if (item.internalName?length > 22) >
					<#if item.newFile == true >
						${item.internalName?substring(0,20)}...*
					<#else>
						${item.internalName?substring(0,20)}...
					</#if>
				<#else>
					<#if item.newFile == true >
						${item.internalName}*
					<#else>
						${item.internalName}
					</#if>
				</#if>
			</div>
		</td>
		<td title=${item.browserUri} class="acnLiveTableFileURI width330 alignLeft">
		  <#if (item.browserUri?length > 22) >
			...${item.browserUri?substring(0,18)}...
		  <#else>
			...${item.browserUri}
		  </#if>
		</td>
                <#if 0 != item.scheduledDate?length>
                    <#assign scheduledTime = item.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
                    <td class="acnLiveTableRight">${scheduledTime}</td>
                <#else>
                    <td class="acnLiveTableRight">&nbsp;</td>
                </#if>
	</tr>
				<#if 0 != item.children?size>
					<@getChildren item.children item.browserUri />
					<#assign hasDependencies=true>
        		</#if>

       </#list>
     </table>

    </div>


  <div class="acnSubmitButtons">
    <input id="golivesubmitButton" type="submit" value="Submit" />
    <input id="golivecancelButton" type="submit" value="Cancel" class="livecancelButton" />
  </div>

  </div>

	<#if hasDependencies??>
  		<div id="dependenciesWarning" style="margin-bottom:10px; text-align:center; color: black;">Dependencies must be checked before you can Submit.</div>
	</#if>

         <div class="publishing-channels" style="position: absolute; display: inline-block; left: 575px" >
            <h3>Submission Comment</h3>
            <div class="pub-status">
                <div class="pub-msg" >
                    <textarea id="acn-submission-comment" name="acn-submission-comment"></textarea>
                </div>
            </div>
        </div>
        
</div>

<#macro getChildren node parentBrowserUri>
 <#foreach childNode in node>
  	<#if childNode.disabled == true>
		<#assign childNodeDisabledCss = "strike" >
	<#else>
		<#assign childNodeDisabledCss = "" >
	</#if>
 	<tr>
		<td class="acnLiveTableFileName">
		  <div class="acnLiveTableCheckbox acnLiveCellIndented">
		    <input id="${childNode.uri}" class="child-${parentBrowserUri}" value="${childNode.browserUri}" type="checkbox" checked="checked"/>
	    </div>
		<#if childNode.document == true>
			<#if childNode.submitted == true && childNode.scheduled == true && childNode.submittedForDeletion != true>
				<div class="documentSubmittedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true && childNode.scheduled == true>
				<div class="documentDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true>
				<div class="documentDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true && childNode.scheduled == true>
				<div class="documentDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true>
				<div class="documentDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.scheduled == true>
				<div class="documentScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.inProgress == true>
				<div class="documentInProgress icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submitted == true>
				<div class="documentSubmitted icon ${childNodeDisabledCss}"/>
			<#else>
				<div class="madeLiveDocument icon ${childNodeDisabledCss}"/>
			</#if>
		<#elseif childNode.floating == true>
			<#if childNode.submitted == true && childNode.scheduled == true && childNode.submittedForDeletion != true>
				<div class="ttFloatingSubmittedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true && childNode.scheduled == true>
				<div class="ttFloatingDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true>
				<div class="ttFloatingDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true && childNode.scheduled == true>
				<div class="ttFloatingDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true>
				<div class="ttFloatingDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.scheduled == true>
				<div class="ttFloatingScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.inProgress == true>
				<div class="ttFloatingInProgress icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submitted == true>
				<div class="ttFloatingSubmitted icon ${childNodeDisabledCss}"/>
			<#else>
				<div class="ttFloating icon ${childNodeDisabledCss}"/>
			</#if>
		<#elseif childNode.component == true>
			<#if childNode.submitted == true && childNode.scheduled == true && childNode.submittedForDeletion != true>
				<div class="componentSubmittedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true && childNode.scheduled == true>
				<div class="componentDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true>
				<div class="componentDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true && childNode.scheduled == true>
				<div class="componentDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true>
				<div class="componentDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.scheduled == true>
				<div class="componentScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.inProgress == true>
				<div class="componentInProgress icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submitted == true>
				<div class="componentSubmitted icon ${childNodeDisabledCss}"/>
			<#else>
				<div class="madeLiveComponent icon ${childNodeDisabledCss}"/>
			</#if>
		<#else>
			<#if childNode.submitted == true && childNode.scheduled == true && childNode.submittedForDeletion != true>
				<div class="ttSubmittedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true && childNode.scheduled == true>
				<div class="ttDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.deleted == true>
				<div class="ttDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true && childNode.scheduled == true>
				<div class="ttDeletedScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submittedForDeletion == true>
				<div class="ttDeleted icon ${childNodeDisabledCss}"/>
			<#elseif childNode.scheduled == true>
				<div class="ttScheduled icon ${childNodeDisabledCss}"/>
			<#elseif childNode.inProgress == true>
				<div class="ttInProgress icon ${childNodeDisabledCss}"/>
			<#elseif childNode.submitted == true>
				<div class="ttSubmitted icon ${childNodeDisabledCss}"/>
			<#else>
				<div class="navPage icon ${childNodeDisabledCss}"/>
			</#if>
		</#if>
		  <div title="${childNode.internalName}" class="acnLiveItemName liveItemName liveFirstCol12 ${childNodeDisabledCss}">
				<#if (childNode.internalName?length > 20) >
					<#if childNode.newFile == true >
						${childNode.internalName?substring(0,18)}...*
					<#else>
						${childNode.internalName?substring(0,18)}...
					</#if>
				<#else>
					<#if childNode.newFile == true >
						${childNode.internalName}*
					<#else>
						${childNode.internalName}
					</#if>
				</#if>
		  </div>
		</td>
		<td title=${childNode.browserUri} class="acnLiveTableFileURI width330 ellipsis ellipsisblock330">
    		<#if (childNode.browserUri?length > 22) >
				...${childNode.browserUri?substring(0,18)}...
			<#else>
				...${childNode.browserUri}
			</#if>
		</td>
                <#if 0 != childNode.scheduledDate?length>
                    <#assign scheduledTime = childNode.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
                    <td class="acnLiveTableRight">${scheduledTime}</td>
                <#else>
                    <td class="acnLiveTableRight">&nbsp;</td>
                </#if>
	</tr>
	<#if 0 != childNode.children?size>
       <@getChildren node.children/>
    </#if>
  </#foreach>
</#macro>
