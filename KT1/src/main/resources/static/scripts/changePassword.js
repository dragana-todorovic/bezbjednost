$(document).ready(function(){
  	input_password = $('#id_password');
	input_passwordConf = $('#id_passwordConf');
  	var btnChange = document.getElementById("btnChange")
	console.log(btnChange)
  	btnChange.disabled = true
  
  input_password.keyup(function () {
	  	if(!validatePassword(input_password.val())){
	  		btnChange.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_password').css('border-color', 'red');
	  		$("#errorPassword").text("Password must have at least 8 characters, lower case, upper case, digit, special character!")
	  		$('#errorPassword').css('color', 'red');
	  	}else {
	  		$(this).removeClass(`alert-danger`);
	  		$('#id_password').css('border-color', '');
	  		$("#errorPassword").text("")
	  	}
  });	
	input_passwordConf.keyup(function () {
	  	if(input_password.val()!=input_passwordConf.val()){
	  		btnChange.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_passwordConf').css('border-color', 'red');
	  		$("#errorPasswordConf").text("Passwords must match!")
	  		$('#errorPasswordConf').css('color', 'red');
	  	}else {
		
	  		$(this).removeClass(`alert-danger`);
	  		$('#id_passwordConf').css('border-color', '');
	  		$("#errorPasswordConf").text("")
			btnChange.disabled = false;
	  	}
  });
	$('#btnChange').click(function() {	
		var newPassword = $('#id_password').val()
		var confirmPassword = $('#id_passwordConf').val()
		var email = localStorage.getItem('email')
		obj = JSON.stringify({email:email,newPass:newPassword,confirmPass:confirmPassword});
		customAjax({
	        method:'POST',
	        url:'/auth/changePassword',
	        data : obj,
	        contentType: 'application/json',
	        success: function(){
				localStorage.removeItem('email');
	        	alert("Success changed password!")
			},
			error: function(){
				localStorage.removeItem('email');
				alert("User with that email doesn't exist")
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