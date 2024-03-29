package security.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.mail.MailException;

import javassist.NotFoundException;
import security.model.User;
import security.model.UserRequest;

public interface UserService {
	User findOneByEmailAndPassword(String email, String password) ;
	User findOneByEmail(String email) ;
	void sendEmailForRecoveryOfAccount(String email) throws MailException, MessagingException;
	void sendEmailForConfirmingRegistration(String email) throws MailException, MessagingException;
	Boolean changePassword(String email, String password);
	User save(UserRequest userRequest);
}
