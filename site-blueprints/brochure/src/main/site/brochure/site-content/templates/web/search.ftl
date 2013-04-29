<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#include "/templates/system/common/cstudio-support.ftl" />

<html xmlns="http://www.w3.org/1999/html">
<head>
    <script type="text/javascript" src="/static-assets/js/jquery.core.js"></script>
    <#include "/templates/web/components/common-head.ftl">
</head>
<body>
<div id="wrap">
    <div class="top_corner"></div>

    <div id="main_container">

        <#include "/templates/web/components/common-header.ftl" />

        <div class="center_content_pages">
            <#assign query = searchService.createQuery()>
            <#assign searchFields = model.searchFields>
            <#assign highlightFields = model.highlightFields>
            <#assign highlightSnippets = model.highlightSnippets?number>
            <#assign highlightSnippetSize = model.highlightSnippetSize?number>
            <#if RequestParameters["q"]??>
                <#assign q = RequestParameters["q"]>
            </#if>
            <#assign start = 0>
            <#if RequestParameters["start"]??>
                <#assign start = RequestParameters["start"]?number>
            </#if>
            <#assign rows = 10>
            <#if RequestParameters["rows"]??>
                <#assign rows = RequestParameters["rows"]?number>
            </#if>

            <#if q??>
                <#assign query = query.setFieldsToReturn(searchFields)>
                <#assign query = query.setHighlightFields(highlightFields)>
                <#assign query = query.setHighlightSnippets(highlightSnippets)>
                <#assign query = query.setHighlightSnippetSize(highlightSnippetSize)>
                <#assign query = query.setQuery("crafterSite:${siteName} AND content-type:page AND (${q})")>
                <#assign query = query.setStart(start).setRows(rows)>

                <#assign results = searchService.search(query)>
                <#assign numFound = results.response.numFound>
                <#assign matches = results.response.documents>
                <#assign highlighting = results.highlighting>
            </#if>

            <!-- SEARCH RESULTS MARKUP -->
            <div id="search-wrapper">
                <div class="header">
                    <form id="search" action="/search">
                        <div>
                            <input type="text" name="q" class="search_input" value="${q!}"/>
                            <input type="submit" name="" class="search_submit" value="Search"/>
                            <input type="hidden" name="rows" value="${rows}"/>
                        </div>
                    </form>
                    <h1>Search Results</h1>
                    <#if matches??>
                        <#if (matches?size > 0)>
                            <div class="pref-results">
                                <span>Results per Page:</span>
                                <#assign searchUrl = "/search?q=${q}&start=${start}&rows=" />
                                <ul>
                                    <li>
                                        <#if rows == 10>10<#else><a href="${searchUrl}10">10</a></#if>
                                    </li>
                                    <li>
                                        <#if rows == 25>25<#else><a href="${searchUrl}25">25</a></#if>
                                    </li>
                                    <li>
                                        <#if rows == 50>50<#else><a href="${searchUrl}50">50</a></#if>
                                    </li>
                                    <li>
                                        <#if rows == 100>100<#else><a href="${searchUrl}100">100</a></#if>
                                    </li>
                                </ul>
                            </div>
                            <span class="num-results">Showing ${1 + start} - <#if ((numFound - start) >= rows)>${start + rows}<#else>${numFound - start}</#if> of ${numFound}:</span>
                        <#else>
                            <span class="no-results"><b>There are no results matching the term(s): <em>${q}</em></b><br/>Please refine your search and try again.</span>
                        </#if>
                    </#if>
                </div>
                <div class="matches">
                <#if matches??>
                    <#list matches as match>
                        <div>
                            <#assign matchHighlighting = highlighting[match.id]>
                            <#if matchHighlighting.title??>
                                <#assign title = matchHighlighting.title?first>
                            <#else>
                                <#assign title = match.title>
                            </#if>
                            <#assign storeUrl = match.id?substring(match.id?index_of(":") + 1)>
                            <#assign renderUrl = urlTransformationService.transform('storeUrlToRenderUrl', storeUrl)>

                            <a href="${renderUrl}">${title}</a>

                            <div>
                                <#assign highlightingStr = "">
                                <#list matchHighlighting?keys as highlightField>
                                    <#if highlightField != "title">
                                        <#list matchHighlighting[highlightField] as highlight>
                                            <#if highlightingStr != "">
                                                <#assign highlightingStr = "${highlightingStr}<br />${highlight}">
                                            <#else>
                                                <#assign highlightingStr = "${highlight}">
                                            </#if>
                                        </#list>
                                    </#if>
                                </#list>
                                <#if highlightingStr != "">${highlightingStr}<#elseif match.body_html??>${match.body_html?substring(0, highlightSnippetSize?number)}</#if>
                            </div>
                        </div>
                    </#list>
                    </#if>
                </div>
                <#if matches?? && (matches?size > 0)>
                    <div class="view-more">
                            <span>View more results:</span>
                            <ul>
                                <#assign numPages = (numFound?float / rows?float)?ceiling>
                                <#assign currPage = (start / rows) + 1>

                                <#if (currPage > 1)>
                                    <li class="prev"><a href="/search?q=${q}&start=${start - rows}&rows=${rows}">Prev <span class="smaller">&lt;&lt;</span></a></li>
                                </#if>

                                <#list 1..numPages as page>
                                    <#if page == currPage>
                                        <li>${page}</li>
                                    <#else>
                                        <li><a href="/search?q=${q}&start=${(page - 1) * rows}&rows=${rows}">${page}</a></li>
                                    </#if>
                                </#list>

                                <#if (currPage < numPages)>
                                    <li class="next"><a href="/search?q=${q}&start=${start + rows}&rows=${rows}">Next <span class="smaller">&gt;&gt;</span></a></li>
                                </#if>
                            </ul>
                    </div>
                </#if>
            </div>
            <!-- END OF SEARCH RESULTS -->

        <#include "/templates/web/components/common-footer.ftl" />
        </div>
    </div>
</div>

<#include "/templates/web/components/sch.ftl" />

</body>
</html>