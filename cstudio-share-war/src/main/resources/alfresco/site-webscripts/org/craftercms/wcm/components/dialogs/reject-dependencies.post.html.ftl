<script>
   ${jsonDependencies}
</script>

<div id="acnVersionWrapper" class="acnBox">
	<h3>Reject</h3>
	<p>The following checked item(s) will be rejected.</p>

	<div class="acnScroll">
  	<h5>
			<span class="left">Page</span>
			<span class="right">Submitted By</span>
		</h5>
    <div class="acnScrollBox" style="height:100px">
			<table class="acnLiveTable liveTable">
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
						    <div title="${item.internalName}" class="liveItemName ${disabledCss}">
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
              <td title=${item.browserUri} class="width270 rejectItemLayoutMargin">
				<#if (item.browserUri?length > 22) >
					...${item.browserUri?substring(0,20)}...
				<#else>
					...${item.browserUri}
				</#if>
              </td>
							<td title="${item.userFirstName} ${item.userLastName}" class="acnLiveTableRight">${item.userFirstName} ${item.userLastName}</td>
						</tr>
						<#if 0 != item.children?size>
           	<@getChildren item.children item.browserUri />
            </#if>
					</#list>
       </table>
		</div>
	</div>

	<div class="formRow padTop">
		<label>Rejection Reason:</label>
		<div class="field">
			<select id="rejectReasonDropDown" class="rejectReasonDropDown">
				 <option>Select a Reason</option>
				 <#list rejectionMessagesList.messages as reasonMessage>
					 <option>${reasonMessage.title?trim}</option>
				 </#list>
			</select>
		</div>
	</div>

	<div class="formRow">
		<textarea id="rejectMessageArea" class="rejectBottomBox rejectTextarea"></textarea>
	</div>

	<div class="acnSubmitButtons">
		<span><input id="golivesubmitButton" type="submit" value="Send Rejection" class="rejectSend"/></span>
		<span><input id="golivecancelButton" type="submit" value="Cancel" class="rejectCancel"/></span>
	</div>
	<div id="rejectReasonJson" style="display:none;">${jsonRejectionMessagesList}</div>
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
		  	<div class="acnLiveTableCheckbox acnLiveCellIndented"><input id="${childNode.uri}" class="child-${parentBrowserUri}" value="${childNode.browserUri}" type="checkbox" checked="checked"/></div>
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
     <td title=${childNode.browserUri} class="width270 rejectItemLayoutMargin">
     	<#if (childNode.browserUri?length > 22) >
			...${childNode.browserUri?substring(0,20)}...
		<#else>
			...${childNode.browserUri}
		</#if>
     </td>
	   <td title="${childNode.userFirstName} ${childNode.userLastName}" class="acnLiveTableRight">${childNode.userFirstName} ${childNode.userLastName}</td>
	</tr>
  </#foreach>
</#macro>

