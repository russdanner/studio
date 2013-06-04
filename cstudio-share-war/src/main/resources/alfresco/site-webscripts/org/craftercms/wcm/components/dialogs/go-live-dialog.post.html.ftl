<#if args.baseUrl?exists>
    <#assign urlBase = args.baseUrl >
<#else>
    <#assign urlBase = url.context >
</#if>

<div id="acnVersionWrapper" class="acnBox go-live" style="height: auto;">
    <div id="goLivePopWrapper" style='position:absolute; left:257px; top:117px; width:288px; display:none;'>
        <div class="goLivePopRadio"><input name='setSchedule' id='now' type="radio" checked="checked"  /></div>
        <div class="goLivePopTextTop">Now</div>
        <div class="goLiveTopRight"><a href="#" onclick="CStudioAuthoring.Dialogs.DialogGoLive.instance.onDone();return false;">Done</a></div>
        <div class="clear"></div>
        <div class="goLivePopRadio"><input name='setSchedule' id='settime' type='radio'  /></div>
        <input class="goLivePopDate goLivePopText submitdate submitdtext datePickerInput" id="datepicker" style="background-image:url(${urlBase}/themes/cstudioTheme/images/icons/icon_calendar.gif); background-position:right center; background-repeat:no-repeat;width:100px;" value="Date..."/>
        <input class="goLivePopTime goLivePopText submittime submitdtext" id="timepicker"" style="width:100px;" value="Time..."/>
        <div class="timeButtonContainer">
            <input id="timeIncrementButton" type="submit" value=""/>
            <input id="timeDecrementButton" type="submit" value=""/>
        </div>
        <span style="line-height: 20px; font-size:11px;" id="timeZone">EST</span>
    </div>

    <h3>Go Live</h3>
    <div class="acnBoxFloat" style="margin-bottom: 13px;">
        <div class="acnBoxFloatLeft">
            <span>The following checked items will Go Live</span>
        </div>
        <div class="acnBoxFloatRight"><a href="#" id="globalSetToNow">Set everything to &lsquo;Now&rsquo;</a></div>
    </div>

    <style>
        #acnScrollBoxDiv { height: 418px; }
        #acnScrollBoxDiv .spinner {
            width: 50%;
            margin: 10% auto;
            text-align: center;
        }
        #acnScrollBoxDiv .spinner img {
            float: left;
            margin-right: 5px;
        }
    </style>

    <div class="dialog-main">
        <div class="acnScroll acnScrollPadTop">
            <h5>
                <span class="left">Page</span>
                <span class="right">Go Live</span>
            </h5>
            <div id="acnScrollBoxDiv" class="acnScrollBox">

            </div>
            <div class="comment">
                <label for="acn-submission-comment">Submission Comment</label>
                <textarea id="acn-submission-comment" name="acn-submission-comment"></textarea>
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
        <input id="golivesubmitButton" type="submit" value="Go Live" disabled="disabled" />
        <input id="golivecancelButton" type="submit" value="Cancel" class="livecancelButton" disabled="disabled" />
    </div>
</div>


