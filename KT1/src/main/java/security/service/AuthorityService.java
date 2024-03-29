package security.service;

import java.util.List;

import security.model.Authority;

public interface AuthorityService {
	List<Authority> findById(Long id);
	List<Authority> findByname(String name);
}
