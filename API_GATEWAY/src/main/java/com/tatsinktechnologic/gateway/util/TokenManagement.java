/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.util;

//import com.auth0.jwt.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.UUID;
import org.apache.log4j.Logger;


/**
 *
 * @author olivier.tatsinkou
 */
public class TokenManagement {
    
    private static Logger logger = Logger.getLogger(TokenManagement.class);
    
    public static String getTokenHS256(String user_name,String tokenSecret,int duration){
      String result = null;
      Date expireDate = new Date(System.currentTimeMillis() + (duration * 1000));
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
            result = JWT.create()
                .withIssuer(user_name)
                .withClaim("user_name", user_name)
                .withClaim("random", String.valueOf(UUID.randomUUID()))
                .withExpiresAt(expireDate)
                .sign(algorithm);
          logger.info("Token Generate : "+result);
          logger.info("Token Expire date : "+expireDate);
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            logger.error("Token Creation Error : "+exception);
        }catch (Exception exception) {
            logger.error("Token Creation Error : "+exception); 
        }
        return result;
    }
    
    
    public static void checkTokenHS256(String token,String tokenSecret,String user_name) throws Exception{
       
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(user_name)
            .withClaim("user_name", user_name)
            .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
    }
}
