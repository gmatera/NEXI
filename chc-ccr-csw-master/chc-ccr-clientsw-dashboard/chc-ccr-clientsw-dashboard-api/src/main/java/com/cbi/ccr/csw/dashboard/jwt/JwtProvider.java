package com.cbi.ccr.csw.dashboard.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;



@Component
@Slf4j
public class JwtProvider {

	
	@Value ("${jwt_secret}")
    private String jwtSecret;

  	@Value ("${jwt_expiration}")
    private Integer jwtExpiration;

	
    public String generateTokenFromUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
  	
  	
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (MalformedJwtException e) {
        	log.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
        	log.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
        	log.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
        	log.error("JWT claims string is empty -> Message: {}", e);
        }
        
        return false;
    }
    
    public Date getTokenExpiryFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }
    
    public long getExpiryDuration() {
        return jwtExpiration;
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
			                .setSigningKey(jwtSecret)
			                .parseClaimsJws(token)
			                .getBody().getSubject();
    }
}
