package com.codehooks.auth.config.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.jwtSecret}")
	private String jwtSecret;
	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMillis;
	
	public String generateToken(Authentication authentication) {
		System.out.println("Generating Token");
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMillis);
		return Jwts
					.builder()
					.setSubject(Long.toString(userPrincipal.getId()))
					.setIssuedAt(new Date())
					.setExpiration(expiryDate)
					.signWith(SignatureAlgorithm.HS512, jwtSecret)
					.compact();
	}
	
	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts
							.parser()
							.setSigningKey(jwtSecret)
							.parseClaimsJws(token)
							.getBody();
		return Long.parseLong(claims.getSubject());
	}
	
	public boolean validateToken(String token) {
		
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid Signature");
		}catch (MalformedJwtException  e) {
			logger.error("Invalid JWT Token");
		}catch (ExpiredJwtException  e) {
			logger.error("Expired JWT Token");
		}catch (UnsupportedJwtException  e) {
			logger.error("Unsupported JWT Token");
		}catch (IllegalArgumentException  e) {
			logger.error("JWT Claims String is Empty");
		}
		
		return false;
	}
}
