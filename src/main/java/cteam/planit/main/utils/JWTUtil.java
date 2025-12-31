package cteam.planit.main.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil
{
	@Value("${spring.security.jwt.secret}")
	private String secret;

	@Value("${spring.security.jwt.expires}")
	private int expires;

	public String createToken(String userId)
	{
		return Jwts.builder()
		.subject(userId)
		.issuedAt(Date.from(Instant.now()))
		.expiration(Date.from(Instant.now().plus(expires,ChronoUnit.SECONDS)))
		.signWith(Keys.hmacShaKeyFor(secret.getBytes()))
		.compact();
	}
}