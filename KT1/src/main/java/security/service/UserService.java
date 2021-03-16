package security.service;

import java.util.List;

import javassist.NotFoundException;
import security.model.User;

public interface UserService {
	User findOneByEmailAndPassword(String email, String password) throws NotFoundException;
	User findOneByEmail(String email);
}
