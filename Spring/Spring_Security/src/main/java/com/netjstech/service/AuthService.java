package com.netjstech.service;

import com.netjstech.dto.AuthResponseDto;
import com.netjstech.dto.SignupRequestDto;
import com.netjstech.entity.User;

public interface AuthService {
	void userSignup(SignupRequestDto signupRequestDto);
	AuthResponseDto userLogin(User user);
}
