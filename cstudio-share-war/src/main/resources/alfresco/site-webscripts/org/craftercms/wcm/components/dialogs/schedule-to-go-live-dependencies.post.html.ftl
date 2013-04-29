<#if args.baseUrl?exists>
	<#assign urlBase = args.baseUrl >
<#else>
	<#assign urlBase = url.context >
</#if>
<script>
   ${jsonDependencies}
</script>

<div id="acnVersionWrapper" class="acnBox schedule" style="height:auto">
	<h3>Schedule To Go Live</h3>
	<div class="acnBoxFloat">
		<div class="acnBoxFloatLeft">
			<div>When would you like the checked item(s) to Go Live?</div>
			<div class="schedule-date">
				<input name='setSchedule' id='settime' type='radio'  checked="checked" style="display:none;"/>
				<input class="goLivePopDate goLivePopText submitdate submitdtext datePickerInput" id="datepicker" style="background-image:url(${urlBase}/themes/cstudioTheme/images/icons/icon_calendar.gif); background-position:right center; background-repeat:no-repeat; width:100px; margin-left:0" value="Date..."/>
				<input class="goLivePopTime goLivePopText submittime submitdtext" id="timepicker" style=" width:100px;" value="Time..."/>
	     		<div class="timeButtonContainer">
	      			<input id="timeIncrementButton" type="submit" value=""/>
	      			<input id="timeDecrementButton" type="submit" value=""/>
	      		</div>
				<div style="font-size:11px; float:left;padding-top:3px;" id="timeZone">EST</div>
			</div>
		</div>
	</div>
	<div class="dialog-main">
        <div class="acnScroll acnScrollPadTop">
            <h5>
                <span class="left">Page</span>
                <span class="right">Original Time</span>
            </h5>
            <div id="acnScrollBoxDiv" class="acnScrollBox"  style="height: 418px; margin-bottom: 3px;">
                <table class="liveTable acnLiveTable">
                    <tr>
	                    <#assign submissionComments= "" >
                        <#list dependencies.items as item>
                          <#if submissionComments?contains(item.submissionComment)>
                          <#else>
                              <#if submissionComments != "">
                                <#assign submissionComments = submissionComments + ", ">
                              </#if>
                              <#assign submissionComments = submissionComments + item.submissionComment>
                          </#if>

                            <#if item.disabled == true>
                                <#assign disabledCss = "strike" >
                            <#else>
                                <#assign disabledCss = "" >
                            </#if>
                        <td class="acnLiveTableFileName">
                            <div class="acnLiveTableCheckbox"><input id="${item.uri}" class="parent-${item.browserUri}" value="${item.browserUri}" type="checkbox" checked="checked"/></div>
                            <#if item.document == true>
                                <#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true>
                                    <div class="documentSubmittedScheduled icon ${disabledCss}"/>
                                <#elseif item.deleted == true && item.scheduled == true>
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
                            <#if (item.browserUri?length > 25) >
                                ...${item.browserUri?substring(0,22)}...
                            <#else>
                                ...${item.browserUri}
                            </#if>
                        </td>
                        <#if 0 != item.scheduledDate?length>
                    <#assign scheduledTime = item.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
                    <td class="acnLiveTableRight"><a href='#' onclick='return false;' class='scheduledDate' title="scheduledDate" style="cursor:default;color:black;">${scheduledTime}</a></td>
                    <#else>
                    <td class="acnLiveTableRight">&nbsp;</td>
                    </#if>
                    </tr>
                    <#if 0 != item.children?size>
                        <@getChildren item.children item.browserUri />
                    </#if>
            </#list>
            </table>
            </div>
            <div class="comment">
                <label for="acn-submission-comment">Submission Comment</label>
                <textarea id="acn-submission-comment" name="acn-submission-comment">${submissionComments}</textarea>
            </div>
        </div>

        <div class="publishing-channels">
            <h3>Publish Content</h3>
            <div class="pub-channel">
                <label for="go-pub-channel">
                    <span>Publishing options</span>
                </label>
                <select id="go-pub-channel">
                    <#list channels.availablePublishChannels as channel>
                        <option value="channel-${channel_index}">${channel.name}</option>
                    </#list>
                </select>
            </div>
            <div class="pub-status">
                <h4>Broadcast content update</h4>

                <#list channels.availableUpdateStatusChannels as channel>
                    <label for="pub-status-${channel_index}">
                        <input id="pub-status-${channel_index}" value="${channel.name?lower_case}" type="checkbox" />
                        <span>${channel.name}</span>
                    </label>
                </#list>

                <div class="pub-msg">
                    <label for="go-status-msg">
                        <span>Message to broadcast</span>
                    </label>
                    <span class="counter hidden"><b>140</b><span> characters available</span></span>
                    <textarea id="go-status-msg" name="go-status-msg"></textarea>
                </div>
            </div>
        </div>

    </div>

	<div class="acnSubmitButtons">
  	    <input id="golivesubmitButton" type="submit" value="Approve & Schedule to Go Live" />
        <input id="golivecancelButton" type="submit" value="Cancel" class="livecancelButton" />
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
    	<#if (childNode.browserUri?length > 25) >
			...${childNode.browserUri?substring(0,22)}...
		<#else>
			...${childNode.browserUri}
		</#if>
    </td>
    <#if 0 != childNode.scheduledDate?length>
    <#assign scheduledTime1 = childNode.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
    <td class="acnLiveTableRight"><a href='#' onclick='return false;' class='scheduledDate' title="scheduledDate" style="cursor:default;color:black;">${scheduledTime1}</a></td>
	  <#else>
		<td class="acnLiveTableRight">&nbsp;</td>
		</#if>
	</tr>
  </#foreach>
</#macro>