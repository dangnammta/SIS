/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author namdv
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class);

    private static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validate email with regular expression
     *
     * @param email for validation
     * @return true valid email, false invalid email
     */
    public static boolean validateEmail(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public static String encryptMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] checksum(byte[] data, MessageDigest digest) {
        try {
            digest.update(data);
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printLogSystem(String className, String message) {
        System.out.println(className + "." + message);
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < arrayBytes.length; i++) {
            String hex = Long.toHexString(0xff & arrayBytes[i]);
            if (hex.length() == 1) {
                stringBuffer.append('0');
            }
            stringBuffer.append(hex);
//            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
//                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static String toHex(byte[] input, MessageDigest digest) {
        return convertByteArrayToHexString(checksum(input, digest));
    }

    public static String listStringToString(List<String> strList) {
        String result = "";
        for (int i = 0; i < strList.size(); i++) {
            String str = strList.get(i);
            if (str != null && str.length() > 0) {
                if (result.length() == 0) {
                    result = str;
                } else {
                    result = result + "," + str;
                }
            }
        }
        return result;
    }
}
