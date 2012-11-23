$(document).ready(function() {

	$('#zoom-in').click(function(){
		var width = $('#map').width();
		console.log(width);
		$('#map').width(width*2);
	});

	$('#zoom-out').click(function(){
		var width = $('#map').width();
		if(width > 300){
			$('#map').width(width/2);
		}
	});

	$(function() {
		$("map").draggable();
	});

});