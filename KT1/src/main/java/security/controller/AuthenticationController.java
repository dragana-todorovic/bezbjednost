package security.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javassist.NotFoundException;
import security.exception.ResourceConflictException;
import security.model.CertificateForAdding;
import security.model.ChangePassword;
import security.model.StringResponse;
import security.model.User;
import security.model.UserTokenState;
import security.security.TokenUtils;
import security.security.auth.JwtAuthenticationRequest;
import security.service.UserService;
import security.service.impl.CustomUserDetailsService;

//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;

	// Prvi endpoint koji pogadja korisnik kada se loguje.
	// Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
	@PostMapping("/login")
	public ResponseEntity<UserTokenState> createAuthenticationToken(JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response) {
		String regexEmail = "^([_a-zA-Z0-9-]+)@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6})?$";
		Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcherEmail = patternEmail.matcher(authenticationRequest.getEmail());
        
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern patternPassword = Pattern.compile(regexPassword);
        Matcher matcherPassword = patternPassword.matcher(authenticationRequest.getPassword());
        
        if(matcherEmail.matches() && matcherPassword.matches()) {
        	
        
			try {User u = userService.findOneByEmailAndPassword(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        	
			} catch(NullPointerException e) {
				System.out.println("USAO U ERROR ZA NULL");
        		return ResponseEntity.notFound().build();
			}
       
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
							authenticationRequest.getPassword()));
	
			// Ubaci korisnika u trenutni security kontekst
			SecurityContextHolder.getContext().setAuthentication(authentication);
	
			// Kreiraj token za tog korisnika
			User user = (User) authentication.getPrincipal();
			String jwt = tokenUtils.generateToken(user.getUsername());
			int expiresIn = tokenUtils.getExpiredIn();
	
			// Vrati token kao odgovor na uspesnu autentifikaciju
			return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
        } else {
        	return ResponseEntity.badRequest().body(null);
        }
        
	}
	
	@GetMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(String email) throws MailException, MessagingException{
		userService.sendEmailForRecoveryOfAccount(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<?> chandePassword(@RequestBody ChangePassword change) {
		Boolean result = userService.changePassword(change.getEmail(), change.getNewPass());
		System.out.println(result);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	
}