$(document).ready(function() {
	$('.togglable').click(function() {
		$(this).parent().find('.togglable').toggle();
		$(this).parent().parent().find('ul').toggle('fast', function() {});
	});
});
