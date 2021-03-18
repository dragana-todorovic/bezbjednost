$(document).ready(function() {
	
	$('#showAll').on('click', function(e){
	customAjax({
        method:'GET',
        url:'/certificate/getAll',
        contentType: 'application/json',
        success: function(data, status, xhr) {
			iscrtajCertifikate(data);
        }
            });

	});
})
let iscrtajCertifikate = function(data) {
	let temp='';
	   
	
		for (i in data){
			let pomocna = data[i].split("+");
			
			//"Pera Peric(UID=0001)+Sun Oct 10 00:00:00 CEST 2021+Thu Nov 11 00:00:00 CET 2021+Root"
			temp +=`<div style="display:inline-block; top:0; bottom:0; border:2px solid; border-radius:10px;  text-align:center; height:250px; width:300px; margin:1rem 3rem"><table style="color:white; font-family:Arial; font-style: oblique; font-size: 17px; font-weight:bold " >
			<tr ><td>FOR:<td><td>`+pomocna[0].split("(")[0]+`</td></tr>
			<tr><td>VALID FROM:<td><td>`+pomocna[1].split(" ")[1] +("-")+ pomocna[1].split(" ")[2]+("-")+pomocna[1].split(" ")[5] +`</td></tr>
			<tr><td>VALID TO:<td><td>`+pomocna[2].split(" ")[1] +("-")+ pomocna[2].split(" ")[2]+("-")+pomocna[2].split(" ")[5]+`</td></tr>
			<tr><td>SPECIALITY:<td><td>`+pomocna[3]+`</td></tr>
			<tr><td>UID:<td><td>`+pomocna[0].split("(")[1].split(")")[0].split("=")[1]+`</td></tr>
			`;
			temp+=`<tr><td style = "text-align:center;">
		         <input name="pull" id="btnPull` + pomocna[0].split("(")[1].split(")")[0].split("=")[1] + `" class="btn btn-dark" type="button" value="PULL"></br>
			                </td></tr></table></div>`;
			
		
		}
	    
		$('#showData').html(temp);	
		
		$("input:button[name=pull]").click(function () {
			console.log(this.id)
			customAjax({
		        method:'POST',
		        url:'/certificate/pullCertificate/' + this.id,
		        contentType: 'application/json',
		        success: function(){
					//location.href = "certificates.html";
				},
				error: function(message){
					alert("Neuspjesno")
				}
		            });

			});
		

};
