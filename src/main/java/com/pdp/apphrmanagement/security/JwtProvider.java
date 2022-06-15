package com.pdp.apphrmanagement.security;

import com.pdp.apphrmanagement.utils.enums.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

@Component
public class JwtProvider {

    public static final long expireMill=1000*60*60*24;
    SecretKey secretKey= SecretKey.KEY;
    public String generateToken(String username, Collection<? extends GrantedAuthority> roleSet){
        return Jwts.
                builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMill))
                .claim("roles",  roleSet)
                .signWith( SignatureAlgorithm.HS512, secretKey.key )
                .compact();

    }

    public String getUsername(String token){

        try{
            return Jwts
                    .parser()
                    .setSigningKey(secretKey.key)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
        catch (Exception e){
            return null;
        }


    }
}
