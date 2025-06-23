package com.netjstech.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.netjstech.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	//@Query(Select u from User where u.username = :username)
	public Optional<User> findByUserName(String userName);
	public boolean existsByEmail(String email);
	public boolean existsByUserName(String userName);
}
