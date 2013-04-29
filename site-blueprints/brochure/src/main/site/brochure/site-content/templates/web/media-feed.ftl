<#include "/templates/system/common/cstudio-support.ftl" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<#include "/templates/web/components/common-head.ftl">
	</head>
	<body>
		<div id="wrap">
			<div class="top_corner"></div>
			
			<div id="main_container">
				
				<#include "/templates/web/components/common-header.ftl" />
				
				<div class="center_content_pages">
					<table>
						<#list model.mediaFeeds as feed>
							<tr>
                                <td>
                                    <#-- Reference:
                                            https://twitter.com/about/resources/widgets/widget_search
                                    --><#t>
                                    <script charset="utf-8" src="http://widgets.twimg.com/j/2/widget.js"></script>
                                    <script>
                                    new TWTR.Widget({
                                      version: 2,
                                      type: 'search',
                                      search: '${feed.hashtag}',
                                      interval: 30000,
                                      title: '${feed.title}',
                                      subject: '${feed.hashtag}',
                                      width: 250,
                                      height: 300,
                                      theme: {
                                        shell: {
                                          background: '#8ec1da',
                                          color: '#ffffff'
                                        },
                                        tweets: {
                                          background: '#ffffff',
                                          color: '#444444',
                                          links: '#1985b5'
                                        }
                                      },
                                      features: {
                                        scrollbar: false,
                                        loop: true,
                                        live: true,
                                        behavior: 'default'
                                      }
                                    }).render().start();
                                    </script>
							    </td>
                            </tr>
						</#list>
					</table>
					<#include "/templates/web/components/common-footer.ftl" />
				</div>
			</div>
		</div>
        <#include "/templates/web/components/sch.ftl" />
        <script>
        CStudioAuthoring.InContextEdit.initializeEditRegion(
                                    'wide-ice',
                                    '/site/website'+ document.location.pathname + '/index.xml',
                                    'body');

        CStudioAuthoring.InContextEdit.initializeEditRegion(
                                    'rightrail-ice',
                                    '/site/website'+ document.location.pathname + '/index.xml',
                                    'rightRail');
        </script>
	</body>
</html>

