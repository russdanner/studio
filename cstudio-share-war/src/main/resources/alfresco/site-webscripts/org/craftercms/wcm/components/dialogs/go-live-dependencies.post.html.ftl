<#if args.baseUrl?exists>
    <#assign urlBase = args.baseUrl >
<#else>
    <#assign urlBase = url.context >
</#if>

<script>
   ${jsonDependencies}
</script>

<table class="liveTable acnLiveTable" cellpadding="0" cellspacing="0"  border="0">
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
            <#if item.submitted == true && item.scheduled == true && item.submittedForDeletion != true >
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
            <#if item.submitted == true && item.scheduled == true && item.submittedForDeletion!=true>
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
            <#if item.submitted == true && item.scheduled == true && item.submittedForDeletion!=true>
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
            <#if item.submitted == true && item.scheduled == true && item.submittedForDeletion!=true>
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
    <td title=${item.browserUri} class="acnLiveTableFileURI acn-width140 alignLeft">
    <#if (item.browserUri?length > 22) >
        ...${item.browserUri?substring(0,20)}...
    <#else>
        ...${item.browserUri}
    </#if>
    </td>
    <#if 0 != item.scheduledDate?length>
        <#assign scheduledTime = item.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
        <td class="acnLiveTableRight"><div class="acnGoLiveSetTime"><a href='#' onclick='CStudioAuthoring.Dialogs.DialogGoLive.instance.openSchedulePopup(event, this, "${item.browserUri}"); return false;' class='scheduledDate' title="scheduledDate">${scheduledTime}</a></div></td>
    <#else>
        <td class="acnLiveTableRight"><div class="acnGoLiveSetTime"><a href='#' onclick='CStudioAuthoring.Dialogs.DialogGoLive.instance.openSchedulePopup(event, this, "${item.browserUri}"); return false;' class='scheduledDate' title="scheduledDate">Now</a></div></td>
    </#if>
</tr>
    <#if 0 != item.children?size>
        <@getChildren item.children item.browserUri />
    </#if>
</#list>
</table>

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
                <input id="${childNode.uri}" class="child-${parentBrowserUri}"
                       value="${childNode.browserUri}" type="checkbox"
                       checked />
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
            ...${childNode.browserUri?substring(0,20)}...
        <#else>
            ...${childNode.browserUri}
        </#if>
        </td>
        <#if 0 != childNode.scheduledDate?length>
            <#assign scheduledTime1 = childNode.scheduledDate?datetime("yyyy-MM-dd'T'HH:mm:ss")?string("MM/dd hh:mm a")>
            <td class="acnLiveTableRight"><div class="acnGoLiveSetTime"><a href='#' onclick='CStudioAuthoring.Dialogs.DialogGoLive.instance.openSchedulePopup(event, this, "${childNode.browserUri}"); return false;' class='scheduledDate' title="scheduledDate">${scheduledTime1}</a></div></td>
        <#else>
            <td class="acnLiveTableRight"><div class="acnGoLiveSetTime"><a href='#' onclick='CStudioAuthoring.Dialogs.DialogGoLive.instance.openSchedulePopup(event, this, "${childNode.browserUri}"); return false;' class='scheduledDate' title="scheduledDate">Now</a></div></td>
        </#if>
    </tr>
    </#foreach>

</#macro>

<div id="acn-submission-comment-hidden-div" style="display:none">${submissionComments}</div>
