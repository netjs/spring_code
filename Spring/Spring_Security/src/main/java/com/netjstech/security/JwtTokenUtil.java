package com.netjstech.security;

import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
	@Value("${jwttoken.secret}")
	private String secretKey;
	@Value("${jwttoken.expiration}")
	private long jwtTokenExpiration;
	
	public String generateJwtToken(String userName) {
		//CustomUserBean userPrincipal = (CustomUserBean)authentication.getPrincipal();
		return Jwts.builder()
				   .subject(userName)
				   .issuedAt(new Date(System.currentTimeMillis()))
				   .expiration(new Date(System.currentTimeMillis() + jwtTokenExpiration))
				   .signWith(getSignInKey())
				   .compact();
	}
	
	public boolean validateJwtToken(String token) {
		logger.info("Enter validateJwtToken");
		boolean flag = false;
		try {
			Jwts.parser()
				.verifyWith((SecretKey) getSignInKey())
				.build()
				.parseSignedClaims(token);
			flag = true;
			//return true;
		}catch(UnsupportedJwtException exp) {
			System.out.println("claimsJws argument does not represent Claims JWS " + exp.getMessage());
			throw new JwtException("claimsJws argument does not represent Claims JWS " + exp.getMessage());
		}catch(MalformedJwtException exp) {
			System.out.println("claimsJws string is not a valid JWS " + exp.getMessage());
			throw new JwtException("claimsJws string is not a valid JWS " + exp.getMessage());
		}catch(ExpiredJwtException exp) {
			System.out.println("Claims has an expiration time before the method is invoked " + exp.getMessage());
			throw new JwtException("Claims has an expiration time before the method is invoked " + exp.getMessage());
		}catch(IllegalArgumentException exp) {
			System.out.println("claimsJws string is null or empty or only whitespace " + exp.getMessage());
			throw new JwtException("claimsJws string is null or empty or only whitespace " + exp.getMessage());
		}
		logger.info("Exit validateJwtToken");
		return flag;
	}
	
	public String getUserNameFromJwtToken(String token) throws JwtException {
		logger.info("In getUserNameFromJwtToken");
		Claims claims = Jwts.parser()
					.verifyWith((SecretKey) getSignInKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
		return claims.getSubject();
		
	}
	
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
