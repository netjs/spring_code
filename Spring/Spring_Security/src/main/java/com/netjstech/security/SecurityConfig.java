package com.netjstech.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.netjstech.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
	private final UserDetailsServiceImpl userDetailsService;
	//private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final JwtTokenFilter jwtTokenFilter;
	
	SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtTokenFilter jwtTokenFilter){
		this.userDetailsService = userDetailsService;
		//this.authenticationEntryPoint = authenticationEntryPoint;
		this.jwtTokenFilter = jwtTokenFilter;
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf((csrf) -> csrf.disable())
			.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/user/allusers").permitAll()
					.anyRequest().authenticated())
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
			//.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint));
			
			return http.build();
	}
	
	@Bean 
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
    /* 
     * Configure to use DaoAuthenticationProvider
     * also set PasswordEncoder to encode password before persisting
     */
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
	

//	@Override
//	protected void configure(HttpSecurity httpSecurity) throws Exception {
//		// We don't need CSRF for this example
//		httpSecurity.cors().and().csrf().disable()
//					.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
//					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//					.authorizeRequests().antMatchers("/auth/**").permitAll()
//					.antMatchers(HttpMethod.GET, "/user/allusers").permitAll()
//					.anyRequest().authenticated();
//		
//		httpSecurity.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);					
//	}
}
