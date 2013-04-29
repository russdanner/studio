<article class="intro">
	<header>
		<img src="${urlTransformationService.transform('toWebAppRelativeUrl', model.thumb)}" alt="" widt="63" height="59" />
		<hgroup>
			<h1>${model.heading}</h1>
			<h2>${model.subhead}</h2>
		</hgroup>
	</header>
	<div class="content">
		<img src="${urlTransformationService.transform('toWebAppRelativeUrl', model.image)}" alt="" width="265" height="77" />
		<div>
			<p>${model.body_html}</p>
			<a class="more" href="${model.link}" data-role="button" data-theme="b">Learn more</a>
		</div>
	</div>
</article>