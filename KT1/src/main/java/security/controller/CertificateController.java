package security.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import security.model.StringResponse;
import security.model.User;
import security.security.auth.JwtAuthenticationRequest;
import security.service.CertificateService;
import security.service.UserService;

// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

	@Autowired
	private UserService userService;
	
	 @Autowired
	 private CertificateService certificateService;

	@GetMapping("/getAll")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<String>> getAll() {
		return ResponseEntity.ok(this.certificateService.getAllCertificates());
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
	

	
}
