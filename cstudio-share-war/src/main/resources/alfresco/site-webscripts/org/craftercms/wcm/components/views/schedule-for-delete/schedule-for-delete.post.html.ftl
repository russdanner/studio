<#if args.baseUrl?exists>
	<#assign urlBase = args.baseUrl >
<#else>
	<#assign urlBase = url.context >
</#if>
<div class="cstudio-view delete-view">

    <style>
        .delete-view .dependencies-listing .item-desc {
            white-space: nowrap;
            overflow: hidden;
            width: 310px;
        }
    </style>

    <h1 class="view-title">Submit for deletion</h1>
    <div class="view-caption">
        <span>The following checked items will be deleted:</span>
    </div>

    <div class="view-block">
        <input class="email-notify" type="checkbox" checked /> Email me when my items are deleted
    </div>

    <div class="view-square-wrp">
        <div class="head">
            <div style="margin-left:5px">Page</div>
        </div>
        <div class="body">
            <div class="dependencies-listing">
                <table class="dependencies-table">

                </table>
            </div>
        </div>
    </div>
    <div class="view-block items-feedback" style="text-align:center;margin-top:-10px">
        Dependencies must be checked before you can submit
    </div>

    <div class="action-wrapper">
        <button class="schedule-for-delete">Submit</button>
        <button class="cancel">Cancel</button>
    </div>

</div>
