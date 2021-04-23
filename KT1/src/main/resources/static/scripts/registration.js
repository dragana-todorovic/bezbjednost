var input_email;
var input_first_name;
var input_last_name;
var input_password;
var input_password_repeat;
var button_register;

var p_log;

$(document).ready(function(e){
  //localStorage.removeItem('jwt');
  input_first_name=$('#id_first_name');
  input_last_name=$('#id_last_name');
  input_email = $('#id_email');
  input_password = $('#id_password');
  input_password_repeat = $('#id_password_repeat');
  var btnRegister = document.getElementById("id_button")
  btnRegister.disabled = true

  input_last_name.keyup(function () {
	  	if(validateEmail(input_email.val()) && validatePassword(input_password.val()) && validateName(input_first_name.val())  && validateName(input_last_name.val())) {
	  		btnRegister.disabled = false
	  	}
	  	if(!validateName(input_last_name.val())){
	  		btnRegister.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_last_name').css('border-color', 'red');
	  		$("#errorLastName").text("You can only use letters for first and last name!")
	  		$('#errorLastName').css('color', 'red');
	  	}else {
	  		$(this).removeClass(`alert-danger`);
	  		$('#id_last_name').css('border-color', '');
	  		$("#errorLastName").text("")
	  	}
  });

  input_first_name.keyup(function () {
	  	if(validateEmail(input_email.val()) && validatePassword(input_password.val()) && validateName(input_first_name.val())  && validateName(input_last_name.val())) {
	  		btnRegister.disabled = false
	  	}
	  	if(!validateName(input_first_name.val())){
	  		btnRegister.disabled = true
			$(this).addClass(`alert-danger`);
	  		$('#id_first_name').css('border-color', 'red');
	  		$("#errorFirstName").text("You can only use letters for first and last name!")
	  		$('#errorFirstName').css('color', 'red');
	  	}else {
	  		$(this).removeClass(`alert-danger`);
	  		$('#id_first_name').css('border-color', '');
	  		$("#errorFirstName").text("")
	  	}
  });
  
  input_email.keyup(function () {
	  	if(validateEmail(input_email.val()) && validatePassword(input_password.val()) && validateName(input_first_name.val()) && validateName(input_last_name.val())) {
	  		btnRegister.disabled = false
	  	}
	  	if(!validateEmail(input_email.val())){
	  		btnRegister.disabled = true
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
	  if(validateEmail(input_email.val()) && validatePassword(input_password.val()) && validateName(input_first_name.val()) && validateName(input_last_name.val())) {
	  		btnRegister.disabled = false
	  	}
		if(!validatePassword(input_password.val())) {
			btnRegister.disabled = true
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
	input_password_repeat.keyup(function () {
		if(input_password.val()!=input_password_repeat.val()){
			btnRegister.disabled = true
			$("#errorPasswordRepeat").text("Passwords do not match!")
			$(this).addClass(`alert-danger`);
	  		$('#id_password_repeat').css('border-color', 'red');
			$('#errorPasswordRepeat').css('color', 'red');
		}
		else {
			btnRegister.disabled = false
			$(this).removeClass(`alert-danger`);
	  		$('#id_password_repeat').css('border-color', '');
	  		$("#errorPasswordRepeat").text("")  		
		}
		
	 
	});  		
  button_register = $('#id_button');
  p_log = $('#id_p_log');

  button_register.on('click', function(e){
	
	var first_name=input_first_name.val();
	var last_name=input_last_name.val();
    var email = input_email.val();
    var password = input_password.val();
	obj = JSON.stringify({
		firstname:first_name,
		lastname:last_name,
		email:email,
		password:password});
	
    customAjax({
      url: '/auth/register',
      method: 'POST',
      data:obj,
	  contentType: 'application/json',
	        success: function(){
			localStorage.setItem('obj', obj);
		  	p_log.text('')
	        	alert("Check your email to configure your registration.")
			},
		      error: function(){
		       	p_log.text('User with that email already exists');
		      }
    });
    
   
  });
  
  //[a-zA-Z]+
 function validateName(name) {
	    const re = /[a-zA-Z]+/;
	    return re.test(String(name));
}
  
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