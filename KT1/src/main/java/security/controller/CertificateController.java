package security.controller;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import security.model.User;
import security.security.auth.JwtAuthenticationRequest;
import security.service.UserService;

// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateController {

	@Autowired
	private UserService userService;

	@GetMapping("/addNew")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<User> user(Principal user) {
		User u = this.userService.findOneByEmail(user.getName());
		return ResponseEntity.ok(u);
	}
	
}
