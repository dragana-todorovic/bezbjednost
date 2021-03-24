package security.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import security.model.CertificateForAdding;
import security.model.StringResponse;
import security.model.User;
import security.pki.keystores.KeyStoreReader;
import security.security.auth.JwtAuthenticationRequest;
import security.service.CertificateService;
import security.service.UserService;

// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

	@Autowired
	private UserService userService;
	KeyStoreReader keyStoreReader = new KeyStoreReader();
	
	 @Autowired
	 private CertificateService certificateService;

	@GetMapping("/getAll")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<String>> getAll() {
		return ResponseEntity.ok(this.certificateService.getAllCertificates());
	}
	@GetMapping("/successLoad")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> sucessLoad() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/pullCertificate/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> pull(@PathVariable String id) throws NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		String uid = id.substring(7);
		this.certificateService.pullCertificate(uid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@PostMapping("/checkCertificate/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Boolean> check(@PathVariable String id) throws NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		String uid = id.substring(8);
		return new ResponseEntity<Boolean>(this.certificateService.checkCertificate(uid),HttpStatus.OK);
	}
	
	@PostMapping("/downloadCertificate/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StringResponse> download(@PathVariable String id) throws NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		String uid = id.substring(11);
		return new ResponseEntity<StringResponse>(this.certificateService.downloadCertificate(uid),HttpStatus.OK);
	}
	
	
	@PostMapping("/addCertificate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> addCertificate(@RequestBody CertificateForAdding certificate) throws CertIOException, KeyStoreException, NoSuchProviderException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException  {
		this.certificateService.addCertificate(certificate);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/getIssuerUids/{validFrom}/{validTo}/{speciality}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<String>> getIssuerUids(@PathVariable(name="validFrom") String validFrom,@PathVariable(name="validTo") String validTo,@PathVariable(name="speciality") String speciality) throws NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, ParseException {
		List<String> result = new ArrayList<String>();
		//valid from
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		String ds2 = sdf2.format(sdf1.parse(validFrom));
		Date validFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(ds2);
		//valid to
		String ds3 = sdf2.format(sdf1.parse(validTo));
		Date validToDate = new SimpleDateFormat("dd/MM/yyyy").parse(ds3);
		
;		if(speciality.equals("ca")) {
			List<X509Certificate> rootCertificates = keyStoreReader.getX509Certificates("./src/main/resources/keystores/root.jks", "12345");
			for(X509Certificate c : rootCertificates) {
				String pomocna = c.getSubjectX500Principal().toString().split(",")[0].split("=")[1];
				if(validFromDate.after(c.getNotBefore()) && validToDate.before(c.getNotAfter())) {
					if(!result.contains(pomocna)) {
						result.add(pomocna);
					}
				}
				
			}
		}
		if(speciality.equals("endEntity")) {
			List<X509Certificate> intermediateCertificates = keyStoreReader.getX509Certificates("./src/main/resources/keystores/intermediate.jks", "12345");

			for(X509Certificate c : intermediateCertificates) {
				String pomocna = c.getSubjectX500Principal().toString().split(",")[0].split("=")[1];
				if(validFromDate.after(c.getNotBefore()) && validToDate.before(c.getNotAfter())) {
					if(!result.contains(pomocna)) {
						result.add(pomocna);
					}
				}
				
			}
		}
 		return new ResponseEntity<List<String>>(result,HttpStatus.OK);
	}

	
}
