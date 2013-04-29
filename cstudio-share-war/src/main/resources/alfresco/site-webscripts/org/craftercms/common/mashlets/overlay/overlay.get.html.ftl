<#macro renderGroup itemGroup>
    <#if itemGroup.menuItem?is_sequence>
        <#list itemGroup.menuItem as menuitem >
            <#if menuitem.modulehook?exists >
                <#include "*/modules/mod_${menuitem.modulehook}_hook.ftl">
            </#if>
        </#list>
    <#else>
        <#if itemGroup.menuItem?is_hash && itemGroup.menuItem.item.modulehook?exists >
            <#include "*/modules/mod_${itemGroup.menuItem.item.modulehook}_hook.ftl">
        </#if>
    </#if>
<div id="contextual_nav_menu_items">
    <#if itemGroup.label?exists >
        <#if itemGroup.id?exists >
            <#assign grpId="itemGroup"/>
        <#else>
            <#assign grpId=""/>
        </#if>

        <div id="${grpId}" class="acn-default-navTitle">${itemGroup.label}</div>
    </#if>
    <#if itemGroup.menuItem?is_sequence>
        <#list itemGroup.menuItem as menuitem >
            <#if menuitem.script?exists >
                <script type="text/javascript">
				${menuitem.script}
		    </script>
            <#elseif menuitem.modulehook?exists >

            <#elseif menuitem.image?exists >
                <div id="acn-image">
                    <img src='${menuitem.image}' />
                </div>
            <#else>
                <div class="acn-link" id="acn-link-${menuitem.label?lower_case?replace(" ", "")}">
                    <a href='${menuitem.href}'>${menuitem.label}</a>
                </div>
                <div id="acn-render">
                    <#if menuitem_index &lt; (itemGroup.menuItem?size -1) >
                        <li> | </li>
                    </#if>
                </div>
            </#if>
        </#list>
    <#else>
        <#if itemGroup.menuItem?is_hash>
            <#if itemGroup.menuItem.item.script?exists >
                <script type="text/javascript">
                    ${itemGroup.menuItem.item.script}
                </script>
            <#elseif itemGroup.menuItem.item.modulehook?exists >

            <#elseif itemGroup.menuItem.item.image?exists >
                <div id="acn-image">
                    <img src='${itemGroup.menuItem.item.image}' />
                </div>
            <#else>
                <div class="acn-link" id="acn-link-${itemGroup.menuItem.item.label?lower_case?replace(" ", "")}">
                    <a href='${itemGroup.menuItem.item.href}'>${itemGroup.menuItem.item.label}</a>
                </div>
                <div id="acn-render">
                    <#if menuitem_index &lt; (itemGroup.menuItem?size -1) >
                        <li> | </li>
                    </#if>
                </div>
            </#if>
        </#if>
    </#if>
</div>
</#macro>

<div id="acn-wrapper">
    <!-- transparent curtain on contextual nav, when pop-up is shown - the div below has a z-index of 99999, so curtain needs to be more than that -->
    <div id='curtain' class='curtain-style'></div>

    <div id="authoringContextNavHeader">
        <div id="acn-bar">
            <div id="acn-group">
            <@renderGroup  contextNavModel.left />
            </div>
            <div id="acn-right">
            <@renderGroup  contextNavModel.right />
            </div>
        </div> <!-- close bar -->
    </div>

</div>
