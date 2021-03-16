package security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByEmail(String email);
}

