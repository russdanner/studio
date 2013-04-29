<#macro renderNavItem currLevel maxLevel itemIndex itemCount item active = false>
<li <#if active>class="current"</#if>><a href="${mainNavFunctions.getNavItemUrl(item)}">${mainNavFunctions.getNavItemName(item)}</a></li>
</#macro>