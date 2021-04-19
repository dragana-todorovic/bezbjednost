$(document).ready(function(){

	input_email = $('#id_email');
	let email;
	var btnForgot = document.getElementById("btnForgot")
  	btnForgot.disabled = true
  
  	input_email.keyup(function () {
	  	if(validateEmail(input_email.val())) {
	  		btnForgot.disabled = false
	  	}
	  	if(!validateEmail(input_email.val())){
	  		btnForgot.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_email').css('border-color', 'red');
	  		$("#errorEmail").text("Email is in wrong format!")
	  		$('#errorEmail').css('color', 'red');
	  	}else {
	  		$(this).removeClass(`alert-danger`);
	  		$('#id_email').css('border-color', '');
	  		$("#errorEmail").text("")
	  	}
  });
	$('#btnForgot').click(function() {
		email = $('#id_email').val()
		customAjax({
	        method:'GET',
	        url:'/auth/forgotPassword',
	        data: {email : email},
	        contentType: 'application/json',
	        success: function(){
	        	localStorage.setItem('email', email);
	        	alert("Success sent email!")
			},
			error: function(){
				alert("Neuspjesno")
			}
	            });

		});
  	
	
	});	
	  function validateEmail(email) {
	    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(String(email).toLowerCase());
}
  
  function validatePassword(password) {
	  
	  var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");
	  	if(password.match(strongRegex)) {
	  		return true;
	  	}
	  	else {
	  		return false;
	  	}
}