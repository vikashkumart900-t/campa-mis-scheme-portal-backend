package com.campa.mis.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration-ms}")
	private long expirationTime;

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(UserDetails userDetails, String tokenId) {

		Date issuedAt = new Date();

		Date expiration = new Date(issuedAt.getTime() + expirationTime);

		return Jwts.builder().id(tokenId) // JWT ID (jti)
				.subject(userDetails.getUsername()).issuedAt(issuedAt).expiration(expiration).signWith(getSigningKey())
				.compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractTokenId(String token) {
		return extractAllClaims(token).getId();
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);

		return username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}
}