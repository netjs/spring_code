package com.netjstech.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netjstech.dto.AuthResponseDto;
import com.netjstech.dto.SignupRequestDto;
import com.netjstech.entity.User;
import com.netjstech.service.AuthService;
import com.netjstech.service.UserService;


@RestController
@RequestMapping("/auth")
public class AuthController {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	private final UserService userService;
	private final AuthService authService;
	public AuthController(UserService userService, AuthService authService){
		this.userService = userService;
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> userLogin(@Validated @RequestBody User user) {
		System.out.println("AuthController111 -- userLogin");
		AuthResponseDto authResponseDto = authService.userLogin(user);
		return ResponseEntity.ok(authResponseDto);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<String> userSignup(@Validated @RequestBody SignupRequestDto signupRequestDto) {
		System.out.println("in signup");
		try {
			if(userService.isUserExists(signupRequestDto.getUserName())){
				return ResponseEntity.badRequest().body("Username is already taken");
			}
			if(userService.isEmailTaken(signupRequestDto.getEmail())){
				return ResponseEntity.badRequest().body("Email is already taken");
			}
			authService.userSignup(signupRequestDto);
			return ResponseEntity.ok("User signed up successfully");
		}catch(Exception e) {
			logger.error("Failed to register User " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register User");
	    }
			
	}
}
