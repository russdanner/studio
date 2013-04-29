<#include "/templates/web/navigation/navigation.ftl">

<nav id="menu">
    <ul>
        <@renderNavigation "/site/website", 1 />
    </ul>

    <#if !(requestContext.requestUri?contains("/search"))>
    <form id="search" action="/search">
        <div>
            <input type="text" name="q" class="search_input" />
            <input type="submit" name="" class="search_submit" value="Search" />
        </div>
    </form>
    </#if>
</nav>