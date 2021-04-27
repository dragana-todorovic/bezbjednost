package security.service.impl;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import security.model.Authority;
import security.model.User;
import security.model.UserRequest;
import security.repository.UserRepository;
import security.service.AuthorityService;
import security.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	// @Autowired
	// private JavaMailSender javaMailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorityService authService;


   public User findOneByEmailAndPassword(String email, String password) {
		
		User user = this.userRepository.findOneByEmail(email);
		if(user == null)
			return null;
	
        if (!BCrypt.checkpw(password, user.getPassword())) {
           user = null;
        }
        return user;
    }

	@Override
	public User findOneByEmail(String email)  {
		User u = userRepository.findOneByEmail(email);
		return u;
	}

	@Override
	public void sendEmailForRecoveryOfAccount(String email) throws MailException, MessagingException {
		
		Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", "smtp.gmail.com");
	      props.put("mail.smtp.port", "587");
	      Session session = Session.getInstance(props,
	    	         new javax.mail.Authenticator() {
	    	            protected PasswordAuthentication getPasswordAuthentication() {
	    	               return new PasswordAuthentication("notificationsnotifications22@gmail.com", "Admin123#");
	    	            }
	    		});
	//	Session session = Session.getDefaultInstance(System.getProperties());  
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("notificationsnotifications22@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,
	              InternetAddress.parse(email));

		   // Set Subject: header field
		   message.setSubject("Change password");

		   // Send the actual HTML message, as big as you like
		   message.setContent(
	              "<a href='"+ "https://localhost:8443/html/changePassword.html" + "'>Change your password</a>",
	             "text/html");

		   // Send message
		   Transport.send(message);
	}

	@Override
	public Boolean changePassword(String email, String password) {
		User u = findOneByEmail(email);
		System.out.println(u);
		if(u == null) {
			return false;
		}
		u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
		this.userRepository.save(u);
		return true;
		
		
	}


	@Override
	public User save(UserRequest userRequest) {
		User user = this.userRepository.findOneByEmail(userRequest.getEmail());
		if(user ==null) {
			User u = new User();
			u.setEmail(userRequest.getEmail());
			// pre nego sto postavimo lozinku u atribut hesiramo je
			u.setPassword(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt(12)));
			//u.setPassword(passwordEncoder.encode(userRequest.getPassword()));
			u.setFirstName(userRequest.getFirstname());
			u.setLastName(userRequest.getLastname());
			u.setEnabled(true);
			
			List<Authority> auth = authService.findByname("ROLE_USER");
			// u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
			u.setAuthorities(auth);
			
			u = this.userRepository.save(u);
		return u;
		}
		return null;
	}

	@Override
	public void sendEmailForConfirmingRegistration(String email) throws MailException, MessagingException {
		Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", "smtp.gmail.com");
	      props.put("mail.smtp.port", "587");

	      Session session = Session.getInstance(props,
	    	         new javax.mail.Authenticator() {
	    	            protected PasswordAuthentication getPasswordAuthentication() {
	    	               return new PasswordAuthentication("notificationsnotifications22@gmail.com", "Admin123#");
	    	            }
	    		});
	//	Session session = Session.getDefaultInstance(System.getProperties());  
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("notificationsnotifications22@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,
	              InternetAddress.parse(email));

		   // Set Subject: header field
		   message.setSubject("Activation");

		   // Send the actual HTML message, as big as you like
		   message.setContent(
	              "<a href='"+ "https://localhost:8443/html/activateAccount.html" + "'>Activate your account!</a>",
	             "text/html");

		   // Send message
		   Transport.send(message);
		
	}
		
}

	


