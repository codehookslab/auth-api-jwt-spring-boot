package com.codehooks.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codehooks.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	Optional<User> findByUsernameOrEmail(String username, String email);
	List<User> findByIdIn(List<Long> userIds);
	Optional<User> findByUsername(String username);
	Boolean existsByEmail(String email);
	Boolean existsByUsername(String username);
}
