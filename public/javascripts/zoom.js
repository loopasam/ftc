$(document).ready(function() {
	
	$('#map').css("width", $(window).width());

	$('#zoom-in').click(function(){
		var width = $('#map').width();
		$('#map').width(width*2);
	});

	$('#zoom-out').click(function(){
		var width = $('#map').width();
		if(width > 600){
			$('#map').width(width/2);
		}
	});

});
