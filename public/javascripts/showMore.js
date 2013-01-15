$(document).ready(function() {

	$('.showMore').click(function(){
		var loader = $(this).find('img');
		var showMore = $(this).find('.textShowMore');
		loader.show();
		showMore.hide();
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
					var newElement;
					if(method == 'moreIndirectAgents'){
						newElement = '<a style="display:none" href="/agent/' + this.drugBankId + '">' +
						'<li class="transition inferred">' + this.drugBankId + '-' + this.label + '</li></a>';
					}else if(method == 'moreDirectAgents'){
						newElement = '<a style="display:none" href="/agent/' + this.drugBankId + '">' +
						'<li class="transition direct">' + this.drugBankId + '-' + this.label + '</li></a>';
					}else if(method == 'moreSuperclasses'){
						var hasDrug = "";
						if(this.hasDrug == true){
							hasDrug = "has-drug";
						}
						newElement = '<a style="display:none" href="/' + this.ftcId + '">' +
						'<li class="transition direct ' + hasDrug + '">' + this.ftcId + '-' + this.label + '</li></a>';
					}else if(method == 'moreSubclasses'){
						var hasDrug = "";
						if(this.hasDrug == true){
							hasDrug = "has-drug";
						}
						newElement = '<a style="display:none" href="/' + this.ftcId + '">' +
						'<li class="transition direct ' + hasDrug + '">' + this.ftcId + '-' + this.label + '</li></a>';
					}

					//$('.' + method + 'Wrap > div > ul').append(newElement);
					$(newElement).appendTo('.' + method + 'Wrap > div > ul').fadeIn('slow');
					loader.hide();
					showMore.show();
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
