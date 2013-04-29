<div id="acnVersionWrapper" class="cstudio-view admin-delete-view">

    <style type="text/css">
        .admin-delete-view .item.sub-item {
            padding-left: 0;
        }
        .admin-delete-view .item.sub-item input {
            margin-left: 15px;
        }
        .admin-delete-view table.dependencies-table-head .item,
        .admin-delete-view table.dependencies-table .item,
        .admin-delete-view table.dependencies-table-head .item-desc,
        .admin-delete-view table.dependencies-table .item-desc,
        .admin-delete-view table.dependencies-table-head .item-sch,
        .admin-delete-view table.dependencies-table .item-sch {
            white-space: nowrap;
            overflow:hidden;
        }
        .admin-delete-view table.dependencies-table-head .item,
        .admin-delete-view table.dependencies-table .item {
            width: 175px;
        }
        .admin-delete-view table.dependencies-table-head .item-desc,
        .admin-delete-view table.dependencies-table .item-desc {
            width: 235px;
        }
        .admin-delete-view table.dependencies-table-head .item-sch,
        .admin-delete-view table.dependencies-table .item-sch {
            width: 105px;
            text-align: right;
        }
        .admin-delete-view table.dependencies-table .item-sch a.when {
            margin-right:5px;
        }
    </style>

    <h3>Delete</h3>
    <div class="view-caption">
        <span>The following checked items will be deleted:</span>
        <a class="set-all-now" href="javascript:" style="float:right">Set everything to "Now"</a>
    </div>

    <div class="view-square-wrp">
        <div class="head">
            <table class="dependencies-table-head">
                <tr>
                    <td><div class="item"><span style="padding-left:15px">Page</span></div></td>
                    <td><div class="item-desc">&nbsp;</div></td>
                    <td><div class="item-sch">Delete</div></td>
                </tr>
            </table>
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
    <div class="schedule-overlay" style="display:none;border:1px #7f9db9 solid;background-color:#F2F2F2;">
        <div class="bd">
            <div style="margin-bottom:5px;padding-top:1px">
                <input type="radio" name="when-to-delete" class="now" checked /> Now
                <a href="javascript:" class="overlay-close" style="float:right">Done</a>
            </div>
            <div>
                <input id="settime" type="radio" name="when-to-delete" class="scheduled-delete" />
                <input id="datepicker" class="date-picker water-marked" value="Date..." default="Date..." />
                <input id="timepicker" class="time-picker water-marked" value="Time..." default="Time..." />
                <div class="timeButtonContainer" style="float:right">
                <input id="timeIncrementButton" type="submit" value=""/>
                <input id="timeDecrementButton" type="submit" value=""/>
                </div>
            </div>
        </div>
    </div>

    <div class="acnSubmitButtons">
        <input type="submit" class="do-delete" value="Delete" />
        <input type="submit" class="cancel" value="Cancel" />
    </div>

</div>
