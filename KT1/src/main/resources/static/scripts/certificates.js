$(document).ready(function() {
	
	$('#addCertificate').on('click', function(e){
	customAjax({
        method:'GET',
        url:'/certificate/addNew',
        contentType: 'application/json',
        success: function(data, status, xhr) {
        	
            $("#success").html(`Uspjesno`)
                },
        
            });
	});
})