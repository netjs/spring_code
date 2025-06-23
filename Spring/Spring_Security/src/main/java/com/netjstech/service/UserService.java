package com.netjstech.service;

import com.netjstech.entity.User;

public interface UserService {
	boolean isUserExists(String userName);
	boolean isEmailTaken(String email);
	User save(User user);
	
}
