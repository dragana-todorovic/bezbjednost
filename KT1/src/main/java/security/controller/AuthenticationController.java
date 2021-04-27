package security.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
import security.model.EmailDTO;
import security.model.StringResponse;
import security.model.User;
import security.model.UserDTO;
import security.model.UserRequest;
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
        User u = userService.findOneByEmailAndPassword(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        if(u==null) {
        	System.out.println("USAO");
        	return ResponseEntity.notFound().build();
        }
			/*try {User u = userService.findOneByEmailAndPassword(authenticationRequest.getEmail(), authenticationRequest.getPassword());
		
			} catch(NullPointerException e) {
				System.out.println("USAO U ERROR ZA NULL");
        		return ResponseEntity.notFound().build();
			}*/
       
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
        	System.out.println("Usao u else");
        	return ResponseEntity.badRequest().body(null);
        }
        
	}
	@PostMapping("/register")
	public ResponseEntity<User> addUser(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) throws MailException, MessagingException {
		
		User existUser = this.userService.findOneByEmail(userRequest.getEmail());

		if (existUser != null) {
			throw new ResourceConflictException(userRequest.getId(), "Username already exists");
		} 
		
		String regexName = "[a-zA-Z]+\\.?";
		Pattern patternName = Pattern.compile(regexName);
        Matcher matcherFirstName = patternName.matcher(userRequest.getFirstname());
        Matcher matcherLastName = patternName.matcher(userRequest.getLastname());

		
		String regexEmail = "^([_a-zA-Z0-9-]+)@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6})?$";
		Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcherEmail = patternEmail.matcher(userRequest.getEmail());
        
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern patternPassword = Pattern.compile(regexPassword);
        Matcher matcherPassword = patternPassword.matcher(userRequest.getPassword());
       
        if(userRequest.getFirstname().equals("") || userRequest.getLastname().equals("") || userRequest.getEmail().equals("") || userRequest.getPassword().equals("")) {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(matcherEmail.matches() && matcherPassword.matches() && matcherFirstName.matches() && matcherLastName.matches()) {
        	//SLANJE MEJLA ZA POTVRDU REGISTRACIJE
        	userService.sendEmailForConfirmingRegistration(userRequest.getEmail());
        	return new ResponseEntity<>(HttpStatus.OK);
		}
        else {
        	System.out.println(matcherEmail.matches() + "#" + matcherPassword.matches() + "#" + matcherFirstName.matches() + "#" + matcherLastName.matches());
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        //OVO ZAKOMENTARISANO JE CUVANJE U BAZI,TREBALO BI DA SE POZOVE KADA SE KLIKNE 
        //NA LINK ZA POTVRDU REGISTRACIJE SA MEJLA,NE TREBA DA STOJI OVDE RANIJE JE STAJALO U IF-U
	/*	User user = this.userService.save(userRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/user/{userId}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<>(user, HttpStatus.CREATED);*/
	}
	
	@PostMapping("/activateAccount")
	public ResponseEntity<User> activateAccount(@RequestBody UserRequest userRequest) {
	
        //OVO ZAKOMENTARISANO JE CUVANJE U BAZI,TREBALO BI DA SE POZOVE KADA SE KLIKNE 
        //NA LINK ZA POTVRDU REGISTRACIJE SA MEJLA,NE TREBA DA STOJI OVDE RANIJE JE STAJALO U IF-U
		User user = this.userService.save(userRequest);
		if(user !=null) {
		//HttpHeaders headers = new HttpHeaders();
		//headers.setLocation(ucBuilder.path("/api/user/{userId}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<>(user, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/forgotPassword")
	public ResponseEntity<?> forgotPassword(String email) throws MailException, MessagingException{
		String regexEmail = "^([_a-zA-Z0-9-]+)@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6})?$";
		Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcherEmail = patternEmail.matcher(email);
        if(matcherEmail.matches()) {
        	userService.sendEmailForRecoveryOfAccount(email);
        	return new ResponseEntity<>(HttpStatus.OK);
		}
        else {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
	}
	@RequestMapping(value = "/changePassword" , method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> changePassword(@RequestBody ChangePassword change) {
		System.out.println(change);
		String regexEmail = "^([_a-zA-Z0-9-]+)@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6})?$";
		Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcherEmail = patternEmail.matcher(change.getEmail());
        
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern patternPassword = Pattern.compile(regexPassword);
        Matcher matcherPassword = patternPassword.matcher(change.getNewPass());
        
        if(matcherEmail.matches() && matcherPassword.matches() && change.getNewPass().equals(change.getConfirmPass())) {
		Boolean result = userService.changePassword(change.getEmail(), change.getNewPass());
		if(result==false) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	else {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);		
	}	
	}
	

	@PostMapping("/prevent")
	 public UserDTO xssPrevent(@RequestBody EmailDTO emaill) throws SQLException {

		String sql = "select "
	                + "first_name,last_name,email "
	                + "from users where email = '"
	                + emaill.getEmaill()
	                + "'";

	        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
	        dataSourceBuilder.driverClassName("org.postgresql.Driver");
	        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/postgres");
	        dataSourceBuilder.username("postgres");
	        dataSourceBuilder.password("super");
	        
	        DataSource dataSource = dataSourceBuilder.build();
	        Connection c = dataSource.getConnection();
	        ResultSet rs = c.createStatement().executeQuery(sql);

	        String firstName = "";
	        String lastName = "";
	        String email = "";
	        int i = 0;
	        while (rs.next()) {
	        	System.out.println(i++);
	            firstName = rs.getString("first_name");
	            lastName = rs.getString("last_name");
	            email = rs.getString("email");
	        }

	        System.out.println(firstName + " lastic  + " + lastName );
	        firstName = StringEscapeUtils.escapeHtml4(firstName);
	        lastName = StringEscapeUtils.escapeHtml4(lastName);
	        email = StringEscapeUtils.escapeHtml4(email);

	        UserDTO u = new UserDTO();
	        u.setEmail(email);
	        u.setFirstName(firstName);
	        u.setLastName(lastName);

	        return u;
	    }
}