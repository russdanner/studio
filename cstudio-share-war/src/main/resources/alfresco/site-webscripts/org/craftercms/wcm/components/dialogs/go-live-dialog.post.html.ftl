<#if args.baseUrl?exists>
    <#assign urlBase = args.baseUrl >
<#else>
    <#assign urlBase = url.context >
</#if>

<div id="acnVersionWrapper" class="acnBox go-live">

    <style>
        #acnVersionWrapper { height: auto; }
        div.acnBox td.acnLiveTableRight div.acnGoLiveSetTime { width: auto }
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
        #acnVersionWrapper .mb5 {
            margin-bottom: 5px;
        }
        #acnVersionWrapper .mb10 {
            margin-bottom: 10px;
        }
        #acnVersionWrapper .mb15 {
            margin-bottom: 15px;
        }
        #acnVersionWrapper .mb20 {
            margin-bottom: 20px;
        }
        #acnVersionWrapper fieldset {
            margin-bottom: 10px;
        }
        #acnVersionWrapper fieldset legend {
            font-size: 18px;
            border-bottom: 1px solid #7F9DB9;
            margin-bottom: 10px;
            display: block;
            width: 100%;
        }
        #acnVersionWrapper fieldset label {
            display: block;
            margin-bottom: 5px;
        }
        #acnVersionWrapper label.pointer,
        #acnVersionWrapper fieldset label {
            cursor: pointer;
        }
        #acnVersionWrapper #datepicker {
            background-image: url(${urlBase}/themes/cstudioTheme/images/icons/icon_calendar.gif);
            background-position: right center;
            background-repeat: no-repeat;
        }
        #acnVersionWrapper input#datepicker,
        #acnVersionWrapper input#timepicker {
            width: 90px;
            padding: 5px;
            border: 1px solid rgb(1, 118, 177);
            color: rgb(0, 0, 0);
        }
        #acnVersionWrapper #timeZone {
            line-height: 25px;
        }
        #acnVersionWrapper .scheduling-selection {
            width: 255px;
            margin-left: 15px;
        }
        #acnVersionWrapper .pull-right {
            float: right;
        }
        #acnVersionWrapper .thead,
        #acnVersionWrapper .thead *,
        #acnVersionWrapper .thead span {
            color: #fff;
        }
        #timeIncrementButton {
            margin-top: 2px;
        }
        #timeDecrementButton {
            margin-top: 1px;
        }
        #acnVersionWrapper .dialog-main {
            margin-top: 20px
        }
        #acnVersionWrapper #calendarWrapper {
            margin-top: 0;
        }
        #acnVersionWrapper .modified-placeholder span {
            color: red;
            font-weight: bold;
            cursor: pointer;
        }
        #acnVersionWrapper input[type="button"][disabled],
        #acnVersionWrapper input[type="submit"][disabled],
        #acnVersionWrapper input[type="button"][disabled]:hover,
        #acnVersionWrapper input[type="submit"][disabled]:hover {
            background-color: #C0C0C0;
            border-color: grey;
            color: grey;
        }
        #acnVersionWrapper .warning-overlay {
            position: absolute;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
            opacity: 0.8;
            background-color: #000;
            z-index: 1;
        }
        #acnVersionWrapper .warning-overlay-dialog {
            background-color: #f89406;
            color: #c67605;
            padding: 20px 25px;
            width: 400px;
            margin-left: -200px;
            position: absolute;
            top: 30%;
            left: 50%;
            z-index: 1;
        }
        #acnVersionWrapper .warning-dialog {
            background-color: #f89406;
            border: 1px solid #c67605;
            margin-bottom: 10px;
            padding: 5px 10px;
            float: right;
            color: #fff;
            width: 40%;
        }
        #acnVersionWrapper .warning-dialog label,
        #acnVersionWrapper .warning-dialog a {
            font-weight: bold;
            text-decoration: underline;
            color: #fff;
        }
        #dependenciesNotice {
            text-align: center;
        }
        #acnVersionWrapper.acnBox table.acnLiveTable td.acnLiveTableFileName {
            width: 250px !important;
        }
        #acnVersionWrapper.acnBox table.acnLiveTable td.acnLiveTableFileURI {
            width: 215px !important;
        }
        #acnVersionWrapper.acnBox table.acnLiveTable td.acnLiveTableRight {
            width: 115px;
        }
        #schedulingSelection {
            position: relative;
        }
        #schedulingSelection .overlay,
        #schedulingSelection #schedulingSelectionDatepickerOverlay,
        #schedulingSelection #schedulingSelectionTimepickerOverlay {
            position: absolute;
            top: 0;
            bottom: 0;
            width: 100px;
        }
        #schedulingSelection #schedulingSelectionDatepickerOverlay {
            left: 0;
        }
        #schedulingSelection #schedulingSelectionTimepickerOverlay {
            left: 105px;
        }
    </style>

    <div id="warningDialog" class="warning-dialog" style="display: none">
        <p>
            You have selected items with mixed schedules. Moving forward
            with this action will launch all checked items on the same schedule.
        </p>
    </div>

    <div id="goLivePopWrapper" style='position:absolute; left:257px; top:117px; width:288px; display:none;'>
        <div class="goLivePopRadio">
            <input name='setSchedule' id='now' type="radio" checked="checked"  />
        </div>
        <div class="goLivePopTextTop">Now</div>
        <div class="goLiveTopRight">
            <a onclick="CStudioAuthoring.Dialogs.DialogGoLive.instance.onDone();return false;" href="#">
                Done
            </a>
        </div>
        <div class="clear"></div>
        <div class="goLivePopRadio">
            <input name='setSchedule' id='settime' type='radio'  />
        </div>
    </div>

    <h3>Approve for Publish</h3>
    <p>The following checked items will Go Live</p>

    <div class="dialog-main">
        <fieldset class="pull-right">
            <legend>Selected Item Scheduling</legend>
            <label>
                <input type="radio" name="go-live-scheduling-global" value="now" id="globalSetToNow" />
                Set all items to go live &lsquo;Now&rsquo;
            </label>
            <label>
                <input type="radio" name="go-live-scheduling-global" value="datetime" id="globalSetToDateTime" />
                Set all items to go live on a specific date &amp; time:
            </label>
            <div class="scheduling-selection" id="schedulingSelection">

                <div id="timeZone" class="pull-right">EST</div>

                <div class="timeButtonContainer pull-right">
                    <input id="timeIncrementButton" type="submit" value=""/>
                    <input id="timeDecrementButton" type="submit" value=""/>
                </div>

                <input class="datePickerInput" id="datepicker" value="Date..." readonly disabled />
                <input id="timepicker" value="Time..." disabled />

                <div class="overlay" id="schedulingSelectionDatepickerOverlay" data-click-target="datepicker"></div>
                <div class="overlay" id="schedulingSelectionTimepickerOverlay" data-click-target="timepicker"></div>
            </div>
        </fieldset>

        <div class="acnScroll acnScrollPadTop">
            <h5 class="thead">
                <span class="left">Page</span>
                <span class="right">Original Schedule</span>
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

    <div id="dependenciesNotice"></div>

    <div class="acnSubmitButtons">
        <input id="golivesubmitButton" type="submit"
               value="Go Live" disabled />
        <input id="golivecancelButton" type="button"
               value="Cancel" class="livecancelButton"
               disabled />
    </div>

</div>


