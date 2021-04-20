	console.log(localStorage.getItem('jwt'))
	if(localStorage.getItem('jwt') == null) {
		window.location.href = "login.html"
	}
