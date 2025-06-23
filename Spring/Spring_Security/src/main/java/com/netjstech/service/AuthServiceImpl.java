package com.netjstech.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.netjstech.dao.RoleRepository;
import com.netjstech.dto.AuthResponseDto;
import com.netjstech.dto.SignupRequestDto;
import com.netjstech.entity.CustomUserBean;
import com.netjstech.entity.Role;
import com.netjstech.entity.Roles;
import com.netjstech.entity.User;
import com.netjstech.exception.RoleNotFoundException;
import com.netjstech.security.JwtTokenUtil;

@Service
public class AuthServiceImpl implements AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	private final RoleRepository roleRepository;
	private final UserService userService;
	private final PasswordEncoder encoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenUtil jwtTokenUtil;
	public AuthServiceImpl(RoleRepository roleRepository, UserService userService, 
			PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil){
		this.roleRepository = roleRepository;
		this.userService = userService;
		this.encoder = encoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}
	@Override
	public void userSignup(SignupRequestDto signupRequestDto) {
		logger.info("Entering userSignup");
		User user = new User();
		Set<Role> roles = new HashSet<>();
		user.setUserName(signupRequestDto.getUserName());
		user.setEmail(signupRequestDto.getEmail());
		user.setPassword(encoder.encode(signupRequestDto.getPassword()));
		//System.out.println("Encoded password--- " + user.getPassword());
		String[] roleArr = signupRequestDto.getRoles();
		
		// give User role by default
		if(roleArr == null) {
			roles.add(roleRepository.findByRoleName(Roles.ROLE_USER).get());
		}
		// 
		for(String role: roleArr) {
			switch(role.toLowerCase()) {
				case "admin":
					roles.add(roleRepository.findByRoleName(Roles.ROLE_ADMIN).get());
					break;
				case "user":
					roles.add(roleRepository.findByRoleName(Roles.ROLE_USER).get());
					break;	
				default:
					throw new RoleNotFoundException("Specified role not found");
					//return ResponseEntity.badRequest().body("Specified role not found");
			}
		}
		user.setRoles(roles);
		userService.save(user);
		logger.info("Exiting userSignup");
	}

	@Override
	public AuthResponseDto userLogin(User user) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
	
		//SecurityContextHolder.getContext().setAuthentication(authentication);
		CustomUserBean userBean = (CustomUserBean) authentication.getPrincipal();
		String token = jwtTokenUtil.generateJwtToken(userBean.getUsername());
		List<String> roles = userBean.getAuthorities().stream()
									 .map(GrantedAuthority::getAuthority)
									 .collect(Collectors.toList());
		AuthResponseDto authResponseDto = new AuthResponseDto();
		authResponseDto.setToken(token);
		authResponseDto.setRoles(roles);
		return authResponseDto;
		
	}

}
