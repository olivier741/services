/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.utils;

import java.util.Arrays;
import java.util.List;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

/**
 *
 * @author olivier.tatsinkou
 */
public class PasswordGenerators {
    
    private StringBuilder Password_Message;

    public PasswordGenerators() {
    }

    public StringBuilder getPassword_Message() {
        return Password_Message;
    }

    public void setPassword_Message(StringBuilder Password_Message) {
        this.Password_Message = Password_Message;
    }

   
    
    
    
    public static String generateRandomPassword() {

		List rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, 1),
                                           new CharacterRule(EnglishCharacterData.LowerCase, 1), 
                                           new CharacterRule(EnglishCharacterData.Digit, 1),
                                           new CharacterRule(EnglishCharacterData.Alphabetical));
		PasswordGenerator generator = new PasswordGenerator();
		String password = generator.generatePassword(8, rules);
		return password;
	}
    
    public boolean isPasswordValid(String password) {
		boolean isPasswordValid = false;
		PasswordValidator validator = new PasswordValidator( Arrays.asList( new LengthRule(8, 32),
                                                                                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                                                                                    new CharacterRule(EnglishCharacterData.LowerCase, 1), 
                                                                                    new CharacterRule(EnglishCharacterData.Digit, 1),
                                                                                    new CharacterRule(EnglishCharacterData.Alphabetical), 
                                                                                    new WhitespaceRule()
                                                                                  )
                                                                    );

		RuleResult result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			isPasswordValid = true;
		} else {
			isPasswordValid = false;
			Password_Message = new StringBuilder();
			validator.getMessages(result).stream().forEach(Password_Message::append);
		}
		return isPasswordValid;
	}
    
}
