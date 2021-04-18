$(document).ready(function(){


	let email;
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
	        	alert("Success changed password!")
			},
			error: function(){
				alert("Neuspjesno")
			}
	            });

		});
	
	});	