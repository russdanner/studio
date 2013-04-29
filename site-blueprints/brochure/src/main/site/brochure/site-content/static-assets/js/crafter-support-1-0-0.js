var Crafter = Crafter || {};

Crafter.Components = {
	render: function(){
	    var elems = jQuery.makeArray(jQuery(".crComponent"));
	    var length = elems.length;
		
	    for(var i=0; i < length; ++i){
			var o_elem = document.getElementById("o_" + elems[i].id);
			if(document.getElementById(elems[i].id) && o_elem){
				document.getElementById(elems[i].id).innerHTML = o_elem.innerHTML;
			}
		} // For
	}
};

$(document).ready(function() {
	Crafter.Components.render();
});