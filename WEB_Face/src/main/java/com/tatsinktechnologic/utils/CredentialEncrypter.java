/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.utils;

/**
 *
 * @author olivier
 */
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;


public class CredentialEncrypter {

    private static RandomNumberGenerator rng = new SecureRandomNumberGenerator();

    /**
     * hash + salt
     * 
     * @param plainTextPassword
     * @return
     */
    public static TwoTuple<String, String> saltedHash(String plainTextPassword) {
        if (StringUtils.isEmpty(plainTextPassword)) {
            throw new IllegalArgumentException("plainTextPassword must not be null");
        }

        Object salt = rng.nextBytes();
        String hash = new Sha256Hash(plainTextPassword, salt, 1024).toBase64();
        return new TwoTuple<>(hash, salt.toString());
    }

}