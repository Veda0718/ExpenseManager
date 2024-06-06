package com.user.pems.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.pems.dto.User;

public interface UserRepository extends MongoRepository<User, String>{
	Optional<User> findByUsername(String username);
}
