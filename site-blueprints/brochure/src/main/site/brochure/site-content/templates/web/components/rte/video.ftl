<#if model['../componentId']??>
	<#assign componentId = model['../componentId'] />
<#else>
	<#assign componentId = model['file-name']?substring(0, model['file-name']?length - 4) />
</#if>

<#if RequestParameters['preview']??>
	<#assign containerClass = 'crComponent' />	
<#else>
	<#assign containerClass = '' />
</#if>

<div id="o_${componentId}" class="${containerClass}">

	<#if model['//video/videos'] == 'series'>
		<#assign videoMetaData = model['video'] />
	<#else>
		<#assign videoMetaData = model['video']  />
	</#if>
	<#if model['//video/alignment'] == 'inline-block'>
		<#assign format = 'display' />
	<#else>
		<#if model['//video/alignment'] == 'center'>
			<#assign format = 'text-align' />
		<#else>
			<#assign format = 'float' />
		</#if>
	</#if>


	<#assign cstudioComponent = 'crComponent' />

	<div style="${format}:${model['//video/alignment']};<#if model['//video/alignment'] == 'center'>'clear:both;'<#else>''</#if>;padding-left:${model['//video/paddingLeft']}px;padding-right:${model['//video/paddingRight']}px;padding-top:${model['//video/paddingTop']}px;padding-bottom:${model['//video/paddingBottom']}px">
		<#if model['//video/videoType'] == 'in page'>
			<#if model['//video/overrideHeight'] == ''>
				<#assign height = 270 />
			<#else>
				<#assign height = model['//video/overrideHeight'] />
			</#if>
			<#if model['//video/overrideWidth'] == ''>
				<#assign width = 450 />
			<#else>
				<#assign width = model['//video/overrideWidth'] />
			</#if>
			<#if model['//video/videos'] == 'single'>
				<div id="playerComponent" class="videoCont formatingVideo"
						style="<#if model['//video/alignment'] == 'center'>'margin: 0 auto;text-align:left;'<#else>''</#if>width:${width + 29}px">
							<div class="playerTopLeft">
								<div class="playerTopRight">
								    <div class="playerTopCenter"></div>
								</div>
							</div>
							<div class="playerCenterLeft">
								<div class="playerCenterRight">
								    <div class="quoteCenterContent">
									<div class="quoteCenterContent clearfix playerStdPddng">
										<div style="width:${width}px;height:${height}px;border: 1px solid #E1E1E1;text-align:left">
										<#if  RequestParameters['preview']??>
											
											<img src="http://img.youtube.com/vi/${model['//video/videoId']}/0.jpg" style="height:${height}px;width:${width}px;" ></img>
										<#else>
<iframe width="${width}" height="${height}" src="http://www.youtube.com/embed/${model['//video/videoId']}" frameborder="0" allowfullscreen="true"></iframe>
										</#if>

										</div>
									
								</div>
							    </div>
							</div>
						    </div>
						    <div class="playerBottomLeft">
							<div class="playerBottomRight">
							    <div class="playerBottomCenter"></div>
							</div>
						    </div>
						</div>	
			<#elseif model['//video/videos'] == 'series'>
				<#assign height = 324 />
				<#assign width = 578 />
					 		<div id="playerThumbs" 
							class="clearfix formatingVideo videoCont" style="<#if model['//video/alignment'] == 'center'>'margin: 0 auto;text-align:left;'<#else>''</#if>">
							    <div class="playerTopLeft">
								<div class="playerTopRight">
								    <div class="playerTopCenter"></div>
								</div>
							    </div>
							    <div class="playerCenterLeft">
								<div class="playerCenterRight">
								    <div class="playerCenterContent clearfix">

									<div class="clear"></div>
									<div class="playerContentLeft clearfix"  rendered="${videoMetaData.thumbnailUrl != null}">
									
									<#if  RequestParameters['preview']??>
										<img src="${videoMetaData.thumbnailUrl}" style="height:#{height}px;width:#{width}px;" ></img>
										<div style="<#if videoMetaData.thumbnailUrl == null>''<#else>'display:none'</#if>">Invalid Player ID</div>
									<#else>
										<div id="videooseriesinpage" class="videoSeries">
											<object  id='CustomCTVPlayerSeries${video.videoId}' 
											codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,47,0'  
											allowScriptAccess='always'  
											height='#{height}'  
											width='#{width}'
											classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'>  
											<param value='http://cstudio.com/tv/s/tv/players/ctv_viral_1_0.swf?ctv=${videoMetaData.videos[0].id}&amp;playerId=111868803001&amp;autoStart=false&amp;server=http://cstudio.com/tv&amp;width=${width}&amp;height=${height}' 
											name='movie' /> 
											<param value='high' name='quality' /> 
											<param value='#ffffff' name='bgcolor' />
											<param value='always' name='allowScriptAccess' /> 
											<param value='true' name='allowFullScreen' /> 
											<param name='wmode' value='opaque'  /> 
											<embed 	name='CustomCTVPlayerSeries${video.videoId}' 	height='#{height}' 	width='#{width}' 	wmode='opaque' 	align='middle' 	
											pluginspage='http://www.adobe.com/go/getflashplayer' 	type='application/x-shockwave-flash' 
											allowScriptAccess='always' 	allowFullScreen='true' bgcolor='#ffffff' quality='high' 
											src='http://cstudio.com/tv/s/tv/players/ctv_viral_1_0.swf?ctv=${videoMetaData.videos[0].id}&amp;playerId=111868803001&amp;autoStart=false&amp;server=http://cstudio.com/tv&amp;width=${width}&amp;height=${height}'>
											</embed></object>
										</div>
										<input type="hidden" id="video${video.videoId}" value="${video.videoType}"/>
									</#if>
									    <ul class="playerRight clearfix">
										<li class="share"><a href="javascript:void(0);" class="share_btn">Share<span class="urlToShare hide">http://cstudio.com/tv/video/${videoMetaData.videos[0].id}</span></a></li>
									    </ul>
									</div>
									<div class="playerContentRight" rendered="${videoMetaData.thumbnailUrl != null}">
										<#list videoMetaData.videos as seriesVideos>
										<div class="playerImageRight">
											<a class="hide" onclick="playSeriesVideo('${seriesVideos.id}','CustomCTVPlayerSeries${model['//video/videoId']}','${height}','${width}', '${model['//video/videos']}', '${model['//video/videoType']}')"
											href="javascript:void(0);" />
											<div class="videoThumbnailSeries" style="background:url('${seriesVideos.thumbnailUrl}')">
												<div class="videoPlaySeries"></div>
											</div>
											<div class="title">${seriesVideos.title}</div>
											<p>${seriesVideos.description}</p>
										</div>
									    </#list>
									</div>
									<div style="text-align:center" rendered="${videoMetaData.thumbnailUrl == null}">
									Invalid Video ID
									</div>
								    </div>
								</div>
							    </div>
							    <div class="playerBottomLeft">
								<div class="playerBottomRight">
								    <div class="playerBottomCenter"></div>
								</div>
							    </div>
							</div>
				</#if>
		<#elseif model['//video/videoType'] == 'link'>
				<#if model['//video/overrideHeight'] == ''>
					<#assign height = 324 />
				<#else>
					<#assign height = model['//video/overrideHeight'] />
				</#if>
				<#if model['//video/overrideWidth'] == ''>
					<#assign width = 581 />
				<#else>
					<#assign width = model['//video/overrideWidth'] />
				</#if>
			<div class="formatingVideo" style="<#if model['//video/alignment'] == 'center'>'margin: 0 auto;text-align:center;'<#else>''</#if>">
			<a href="javascript:void(0);" class="videoList"
			onclick="m2lCallback('${videoMetaData.leadFormUrl}','CustomCTVPlayer${model['//video/videoId']}','${model['//video/videoId']}', 'lightbox','${model['//video/videos']}',#{videoUtils.getVideoSeriesDetailString(model['//video/videoId'])})"
			title="${model['//video/linkAltTag']}">
			<#if model['//video/videos'] == 'series'>
				<span>${model['//video/linkName']} - ${videoMetaData.numVideos} videos</span>
			<#else>
				<span>${model['//video/linkName']} - ${videoMetaData.duration}</span>
			</#if>
			</a>
			<input type="hidden" id="video${model['//video/videoId']}" value="${model['//video/videoType']}"></input>
			</div>
		</#if>


	</div>
</div>
