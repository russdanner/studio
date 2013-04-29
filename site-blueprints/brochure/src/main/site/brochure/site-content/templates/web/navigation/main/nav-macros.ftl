<#macro renderNavItem item active = false>
<li <#if active>class="current"</#if>><a href="${navFunctions.getNavItemUrl(item)}">${navFunctions.getNavItemName(item)}</a></li>
</#macro>

<#macro renderNavItemWithSubItems item active = false>
<li <#if active>class="dropdown current"<#else>class="dropdown"</#if>>
    <a class="dropdown-toggle" data-toggle="dropdown" href="${navFunctions.getNavItemUrl(item)}">${navFunctions.getNavItemName(item)}</a>
    <ul class="dropdown-menu">
        <#nested>
    </ul>
</li>
</#macro>

<#macro renderNavSubItem item active = false>
<li <#if active>class="current"</#if>><a href="${navFunctions.getNavItemUrl(item)}">${navFunctions.getNavItemName(item)}</a></li>
</#macro>

<#macro renderNavSubItemWithSubItems item active = false>
<li class="dropdown-submenu">
    <a href="${navFunctions.getNavItemUrl(item)}">${navFunctions.getNavItemName(item)}</a>
    <ul class="dropdown-menu">
        <#nested>
    </ul>
</li>
</#macro>