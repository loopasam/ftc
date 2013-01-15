$(document).ready(function() {
	$('.togglable').click(function() {
		$(this).parent().find('.togglable').toggle();
		console.log(this);
		$(this).parent().parent().find('#members').toggle('fast', function() {});
	});
});
