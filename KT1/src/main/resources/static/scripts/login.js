var input_email;
var input_password;
var button_login;

var p_log;

$(document).ready(function(e){
  //localStorage.removeItem('jwt');

  input_email = $('#id_email');
  input_password = $('#id_password');
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
        localStorage.setItem('jwt', jwt.accessToken);
        window.location.href = "/html/certificates.html";
        }
      },
      error: function(){
        p_log.text('Wrong credentials');
      }
    });
    
    
  });

  function sanatize(input) {
    var output = input.replace(/<script[^>]*?>.*?<\/script>/gi, '').
           replace(/<[\/\!]*?[^<>]*?>/gi, '').
           replace(/<style[^>]*?>.*?<\/style>/gi, '').
           replace(/<![\s\S]*?--[ \t\n\r]*>/gi, '');
      return output;
  };

});