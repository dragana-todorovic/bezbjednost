var input_email;
var input_password;
var button_login;

var p_log;

$(document).ready(function(e){

  input_email = $('#id_email');
  input_password = $('#id_password');
  var btnLogin = document.getElementById("id_button")
  btnLogin.disabled = true
  
  input_email.keyup(function () {
	  	if(validateEmail(input_email.val()) && validatePassword(input_password.val())) {
	  		btnLogin.disabled = false
	  	}
	  	if(!validateEmail(input_email.val())){
	  		btnLogin.disabled = true
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
  
  input_password.keyup(function () {
	  if(validateEmail(input_email.val()) && validatePassword(input_password.val())) {
	  		btnLogin.disabled = false
	  	}
		if(!validatePassword(input_password.val())) {
			//btnLogin.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_password').css('border-color', 'red');
	  		$("#errorPassword").text("Password must have at least 8 characters, lower case, upper case, digit, special character!")
	  		$('#errorPassword').css('color', 'red');
		} else {
			$(this).removeClass(`alert-danger`);
	  		$('#id_password').css('border-color', '');
	  		$("#errorPassword").text("")
	  		
		}
	});
	  	
		
  button_login = $('#id_button');
  p_log = $('#id_p_log');

  button_login.on('click', function(e){
	
    var email = input_email.val();
    var password = input_password.val();

    customAjax({
      url: '/auth/login',
      method: 'POST',
      data: { email: email, password: password },
      success: function(jwt, status, xhr){
	        if(xhr.status == 200){
	        	localStorage.setItem('email', email);
	        localStorage.setItem('jwt', jwt.accessToken);
	        window.location.href = "/html/certificates.html";
	
    	}
      },
      error: function(){
        p_log.text('Wrong credentials');
      }
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

  function sanatize(input) {
    var output = input.replace(/<script[^>]*?>.*?<\/script>/gi, '').
           replace(/<[\/\!]*?[^<>]*?>/gi, '').
           replace(/<style[^>]*?>.*?<\/style>/gi, '').
           replace(/<![\s\S]*?--[ \t\n\r]*>/gi, '');
      return output;
  };

});