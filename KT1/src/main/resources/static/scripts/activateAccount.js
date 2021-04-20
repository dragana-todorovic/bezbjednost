$(document).ready(function(){
	
	 $('#btnActivate').on('click', function(e){
		 customAjax({
		      url: '/auth/activateAccount',
		      method: 'POST',
		      data:localStorage.getItem('obj'),
			  contentType: 'application/json',
			        success: function(){
			        	alert("Success registration!")
					},
				      error: function(){
				       	p_log.text('User with that email already exists');
				      }
		    });
	    });
	    
});