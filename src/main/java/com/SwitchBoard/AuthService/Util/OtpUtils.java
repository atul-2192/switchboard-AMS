package com.SwitchBoard.AuthService.Util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;

public class OtpUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public static String hashOtp(String otp) {
        return DigestUtils.sha256Hex(otp);
    }
}
