// iPhone Scale Bug Fix, read this when using http://www.blog.highub.com/mobile-2/a-fix-for-iphone-viewport-scale-bug/
MBP.scaleFix();

$(document).ready( function() {
	
	// .menuTab : iphone site menu
	var menuTab = $('.menu-tab');
		
	var toggleMenu = function(open) {
		
		if (menuTab.hasClass('active')){
			menuTab.removeClass('active');
		} else {
			if (open) {
				menuTab.addClass('active');
			}
		}
	};
	
	if (menuTab) {
		// applies only if the menuTab structure exists
		$('a.toggle-menu').bind('tap', function(e){		
			toggleMenu(true);	
		});
		
		$(document).bind( "pagechange", function( event, data ){
			toggleMenu(false);
		});
	}
});
