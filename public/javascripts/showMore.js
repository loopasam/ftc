$(document).ready(function() {

	$('.showMore').click(function(){
		var method = $(this).attr('id');
		var ftcClassId = $('#classId').html();
		var currentNumber = $('#currentNumber' + method).html();

		$.ajax({
			url: "/" + method + "/" + ftcClassId + "/" + currentNumber,
			type: "GET",
			// callback handler that will be called on success
			success: function(newElements, textStatus, jqXHR){
				currentNumber = parseInt(currentNumber) + newElements.length;
				$('#currentNumber' + method).html(currentNumber);
				if(currentNumber >= parseInt($('#totalNumber' + method).html())){
					$('#' + method).fadeOut('slow');
				}

				$.each(newElements, function(){
					var elementType;
					var classes;
					if(method == 'moreIndirectAgents'){
						elementType = "agent/";
						classes = "inferred";
					}else if(method == 'moreDirectAgents'){
						elementType = "agent/";
						classes = "direct";
					}else if(method == 'moreSuperclasses'){
						elementType = "";
						classes = "direct";
					}else if(method == 'moreSubclasses'){
						elementType = "";
						classes = "direct";
					}

					var newElement = '<a href="/' + elementType + '"' + this.drugBankId + '>' +
					'<li class="transition ' + classes + '">' + this.drugBankId + '-' + this.label + '</li></a>';
					$('.' + method + 'Wrap > ul').append(newElement);
				});



			},
			// callback handler that will be called on error
			error: function(jqXHR, textStatus, errorThrown){
				//TODO handle the error
				console.log(
						"The following error occured: "+
						textStatus, errorThrown
				);
			}
		});


	});
});
