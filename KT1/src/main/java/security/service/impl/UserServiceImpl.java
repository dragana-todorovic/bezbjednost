package security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import security.model.Authority;
import security.model.User;
import security.repository.UserRepository;
import security.service.AuthorityService;
import security.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthorityService authService;


	public User findOneByEmailAndPassword(String email, String password) throws NotFoundException {
		User user = this.userRepository.findOneByEmail(email);
        if (!this.passwordEncoder.matches(password, user.getPassword()))
            throw new NotFoundException("Not existing user");

        return user;
    }

	@Override
	public User findOneByEmail(String email) {
		User u = userRepository.findOneByEmail(email);
		return u;
	}

	

}
