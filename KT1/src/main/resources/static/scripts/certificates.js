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
	$('#logout').on('click', function(e){
		location.href = "login.html";
		});
		
	$('#addCertificate').on('click', function(e){
		customAjax({
        method:'GET',
        url:'/certificate/successLoad',
        contentType: 'application/json',
        success: function(status, xhr) {
			dodajCertifikat();
			
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
			<tr ><td>FOR:</td><td>`+pomocna[0].split("(")[0].substring(0,pomocna[0].split("(")[0].length-3)+`</td></tr>
			<tr ><td>FROM:</td><td>`+pomocna[0].split(")")[1]+`</td></tr>
			<tr><td>VALID FROM:</td><td>`+pomocna[1].split(" ")[1] +("-")+ pomocna[1].split(" ")[2]+("-")+pomocna[1].split(" ")[5] +`</td></tr>
			<tr><td>VALID TO:</td><td>`+pomocna[2].split(" ")[1] +("-")+ pomocna[2].split(" ")[2]+("-")+pomocna[2].split(" ")[5]+`</td></tr>
			<tr><td>SPECIALITY:</td><td>`+pomocna[3]+`</td></tr>
			<tr><td>UID:</td><td>`+pomocna[0].split("(")[1].split(")")[0].split("=")[1]+`</td></tr></table>
			`;
			temp+=`<br><table><tr><td>
		         <input name="pull" id="btnPull` + pomocna[0].split("(")[1].split(")")[0].split("=")[1] + `" class="btn btn-dark" type="button" value="PULL"></br>
			                </td><td>
		         <input name="check" id="btnCheck` + pomocna[0].split("(")[1].split(")")[0].split("=")[1] + `" class="btn btn-dark" type="button" value="CHECK"></br>
			                </td>
			                <td>
			       <input name="download" id="btnDownload` + pomocna[0].split("(")[1].split(")")[0].split("=")[1] + `" class="btn btn-dark" type="button" value="DOWNLOAD"></br>
			                </td></tr>
			
				<tr><td></td><td style="text-align:center" ><br><p style="text-align:center"  id="isValid` + pomocna[0].split("(")[1].split(")")[0].split("=")[1] + `"></p></td></tr>
				</table>
				
				</div>`;
			
		
		}
	    
		$('#showData').html(temp);	
		
		$("input:button[name=pull]").click(function () {
			console.log(this.id)
			customAjax({
		        method:'POST',
		        url:'/certificate/pullCertificate/' + this.id,
		        contentType: 'application/json',
		        success: function(){
					location.href = "certificates.html";
				},
				error: function(message){
					alert("Neuspjesno")
				}
		            });

			});
			$("input:button[name=check]").click(function () {
			id = this.id;
			id = id.substring(8);
			var text = "isValid"+id;
			customAjax({
		        method:'POST',
		        url:'/certificate/checkCertificate/' + this.id,
		        contentType: 'application/json',
		        success: function(data){
				var pom = document.getElementById(text);
				
					if(data==true){		
						pom.innerHTML = "Valid";	
						pom.style.color = '#7CFC00';
													
					}
					else{
						pom.innerHTML = "Not valid";
						pom.style.color = '#ff0000';	
					}
					
				},
				error: function(message){
					alert("Neuspjesno")
				}
		            });

			});
			$("input:button[name=download]").click(function () {
				id = this.id;
				customAjax({
			        method:'POST',
			        url:'/certificate/downloadCertificate/' + id,
			        contentType: 'application/json',
			        success: function(data){
			        	console.log(data.reposnse)
			        	const element = document.createElement("a");
		                const file = new Blob([data.response],    
		                            {type: 'text/plain;charset=utf-8'});
		                element.href = URL.createObjectURL(file);
		                element.download = "certificate.crt";
		                document.body.appendChild(element);
		                element.click();
					},
					error: function(message){
						alert("Neuspjesno")
					}
			            });

				});

};
let dodajCertifikat = function() {
$("#showData").html(`
<table  style="width:30%;height:500px;float:center;margin-left:auto;margin-right:auto">
        <thead>
             <tr >
                <th colspan="2"  style= "text-align:center;"   >
					<h3 style="color:white">
                   ADD CERTIFICATE
					</h3>
                </th>
            </tr>
        </thead>
        <tbody>
        	 <tr id="upozorenje" class="hidden">
                <td colspan="2"><strong></strong></td>
            </tr>  				
            <tr>
                <td style="color:white">FULL NAME:</td>
                <td>
                    <input type="text" name="txtFullName" id="txtFullName" placeholder="Full name..." />
                </td>
            </tr>
              <tr>
                <td style="color:white">SURNAME:</td>
                <td>
                    <input type="text" name="txtSurname" id="txtSurname" placeholder="Surname..." />
                </td>
            </tr>
				<tr>
                <td style="color:white">GIVEN NAME:</td>
                <td>
                    <input type="text" name="txtGivenName" id="txtGivenName" placeholder="Given name..." />
                </td>
            </tr>
			<tr>
                <td style="color:white">EMAIL:</td>
                <td>
                    <input type="text" name="txtEmail" id="txtEmail" placeholder="Email..." />
                </td>
            </tr>
			<tr>
			<td style="color:white">SPECIALITY:</td>
			<td>
			<select name="speciality" id="speciality">
  			<option value="root">ROOT</option>
  			<option value="ca">CA</option>
			<option value="endEntity">END ENTITY</option>
			</select>
			</td>
			</tr>
				<tr>
                <td style="color:white">UID:</td>
                <td>
                    <input type="text" name="txtUid" id="txtUid" placeholder="Uid..." />
                </td>
            </tr>
				<tr>
                <td style="color:white">VALID FROM:</td>
                <td>
                    <input type="date" name="txtFrom" id="txtFrom"  />
                </td>
            </tr>	
			<tr>
                <td style="color:white">VALID TO:</td>
                <td>
                    <input type="date" name="txtTo" id="txtTo"  />
                </td>
            </tr>
			<tr>
                <td style="color:white">ALIAS:</td>
                <td>
                    <input type="text" name="txtAlias" id="txtAlias" placeholder="Alias..."  />
                </td>
            </tr>								
 	
            <tr class="success">
                <td colspan="2" style = "text-align:center;">
                    <input id="btnDodaj" class="btn btn-primary pull-center" type="button"
                           value="ADD CERTIFICATE" />
                </td>
            </tr>
            
        </tbody>
    </table>
       `);
		$('#btnDodaj').on('click', function(e){
					
		var fullName = $('#txtFullName').val();
		var surname = $('#txtSurname').val();
		var givenName = $('#txtGivenName').val();
		var email = $('#txtEmail').val();
		var e = document.getElementById("speciality");
		var speciality = e.value;
		var uid = $('#txtUid').val();
		var validFrom = document.getElementById("txtFrom").value;
		var validTo= document.getElementById("txtTo").value;		
		var alias = $('#txtAlias').val();	
		console.log(JSON.stringify({fullName:fullName,surname:surname,givenName:givenName,email:email,speciality:speciality,uid:uid,validFrom:validFrom,validTo:validTo,alias:alias}))	
	customAjax({
        method:'POST',
        url:'/certificate/addCertificate',
		data: JSON.stringify({fullName:fullName,surname:surname,givenName:givenName,email:email,speciality:speciality,uid:uid,validFrom:validFrom,validTo:validTo,alias:alias}),
        contentType: 'application/json',
        success: function(data,status, xhr) {
			location.href = "certificates.html";
			
        }
            });

	});
}