/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.captcha;

/**
 *
 * @author olivier.tatsinkou
 */
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Locale;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class CaptchaServiceSingleton {

    private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService();
    private static final int MAX_CACHE_SIZE = 200;
    private static HashMap<String, BufferedImage> captchaImgCache = new HashMap<String, BufferedImage>();

    public static ImageCaptchaService getInstance(){
        return instance;
    }

    public static BufferedImage getImageChallengeForID(String id, Locale locale) {
        if (captchaImgCache.containsKey(id)) {
            return captchaImgCache.get(id);
        } else {
            BufferedImage bImage = instance.getImageChallengeForID(id, locale);

            // if limit reached reset captcha cache
            if (captchaImgCache.size() > MAX_CACHE_SIZE) {
                captchaImgCache = new HashMap<String, BufferedImage>();
            }

            captchaImgCache.put(id, bImage);
            return bImage;
        }
    }

    public static void resetImageChallengeForID(String id) {        
        if (captchaImgCache.containsKey(id)) {      
            captchaImgCache.remove(id);
        }               
    }

}