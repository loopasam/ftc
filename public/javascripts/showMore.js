$(document).ready(function() {
		
	$('#showMoreIndirectAgents').click(function(){
		
		//TODO has to be specifc to the method - store the value in a value field or something
		//Or give a better id
		var ftcClassId = $('#classId').html();
		var currentNumber = $('#currentNumber').html();
		
	    $.ajax({
	        url: "/moreIndirectAgents/" + ftcClassId + "/" + currentNumber,
	        type: "GET",
	        // callback handler that will be called on success
	        success: function(indirectAgents, textStatus, jqXHR){
	            // log a message to the console
	            console.log(indirectAgents);
	            currentNumber = parseInt(currentNumber) + indirectAgents.length;
	            $('#currentNumber').html(currentNumber);
	            if(currentNumber >= parseInt($('#totalNumber').html())){
	            	$('#showMoreIndirectAgents').fadeOut('slow');
	            }
	            
	            $.each(indirectAgents, function(){
	            	var indirectAgentElement = '<a href="/agent/" + this.drugBankId>' +
	                		'<li class="transition inferred">' + this.drugBankId + '-' + this.label + '</li></a>';
	            
	            	$('#indirectAgents > ul').append(indirectAgentElement);
	            });

	            
	            
	        },
	        // callback handler that will be called on error
	        error: function(jqXHR, textStatus, errorThrown){
	            // log the error to the console
	            console.log(
	                "The following error occured: "+
	                textStatus, errorThrown
	            );
	        }
	    });
		
		
	});
});
