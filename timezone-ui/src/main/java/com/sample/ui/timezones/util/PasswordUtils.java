package com.sample.ui.timezones.util;

import java.security.MessageDigest;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;

public class PasswordUtils {

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";

    private static final String ENCRYPTION_ENCODING = "UTF-8";

    private static final String ENCRYPTION_HASHING = "SHA";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static String hashPassword(String in) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ENCRYPTION_HASHING);
            md.update(in.getBytes(ENCRYPTION_ENCODING));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte raw[] = md.digest();

        String hash = new String(Base64.encodeBase64(raw));
        return hash;
    }

    /**
     * Validate password with regular expression
     * 
     * @param password
     *            password for validation
     * @return true valid password, false invalid password
     */
    public static boolean validatePassword(final String password) {
        return pattern.matcher(password).matches();
    }

}