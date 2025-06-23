package com.netjstech.service;

import org.springframework.stereotype.Service;
import com.netjstech.dao.UserRepository;
import com.netjstech.entity.User;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	UserServiceImpl(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	@Override
	public boolean isUserExists(String userName) {
		return userRepository.existsByUserName(userName);
	}

	@Override
	public boolean isEmailTaken(String email) {
		// TODO Auto-generated method stub
		return userRepository.existsByEmail(email);
	}
	@Override
	public User save(User user) {
		// TODO Auto-generated method stub
		return userRepository.save(user);
	}

}
