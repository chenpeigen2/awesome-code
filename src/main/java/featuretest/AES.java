package featuretest;


import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {


    public static void main(String[] args) throws Exception {
//        // 原文:
//        String message = "Hello, world!";
//        System.out.println("Message: " + message);
//        // 128位密钥 = 16 bytes Key:
//
//        byte[] key = MD5Utils.md5("99309cd5c24a653ed80948d0672ad157c37ceffeda821c8cc15d7c440378f52b1ca5bf6ac04ef153b49cd89edf981622e63b4d906a7affc1a4c43ff27fae414f").getBytes("UTF-8");
//        // 加密:
//        byte[] data = message.getBytes("UTF-8");
//        byte[] encrypted = encrypt(key, data);
//        System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(encrypted));
//        // 解密:
//        byte[] decrypted = decrypt(key, encrypted);
//        System.out.println("Decrypted: " + new String(decrypted, "UTF-8"));


        var a = encrypt("xxxxx");
        var b = decrypt(a);
        System.out.println(b);
    }

    // 加密:
    public static byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    // 解密:
    public static byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }


    private static final String ALGORITHM_NAME = "AES";
    private static final String DEFAULT_ENCRYPT_RULE = "AES/CBC/PKCS5Padding";
    private static final String RANDOM_KEY_ALGORITHM = "SHA1PRNG";
    private static final String RANDOM_KEY_ALGORITHM_PROVIDER = "SUN";

    /**
     * AES加密
     *
     * @param content 待加密的内容，为空时为回空
     * @return 加密后的base64格式的结果，出现异常时返回null
     */
    public static String encrypt(String content) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_NAME);
            SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_KEY_ALGORITHM, RANDOM_KEY_ALGORITHM_PROVIDER);
            secureRandom.setSeed(DEFAULT_ENCRYPT_RULE.getBytes());
            keyGenerator.init(128, secureRandom);
//            s9uOeMg8oSpLxe05ac0VimeBjcnQQpEwms7WswGDkQM=
//            s9uOeMg8oSpLxe05ac0Vig
            SecretKey originalKey = keyGenerator.generateKey();
            System.out.println(Base64.getEncoder().encodeToString(originalKey.getEncoded()));
            SecretKey secretKey = new SecretKeySpec(originalKey.getEncoded(), ALGORITHM_NAME);
            System.out.println(originalKey.getEncoded().length);
            System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
            String result = new String(Base64.getEncoder().encodeToString(encrypted));
            return result;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 解密
     *
     * @param encrypted 加密后的base64格式的密文
     * @return 解密后的原文，出现异常时返回null
     */
    public static String decrypt(String encrypted) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_NAME);
            SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_KEY_ALGORITHM, RANDOM_KEY_ALGORITHM_PROVIDER);
            secureRandom.setSeed(DEFAULT_ENCRYPT_RULE.getBytes());
            keyGenerator.init(128, secureRandom);
            SecretKey originalKey = keyGenerator.generateKey();
            SecretKeySpec secretKey = new SecretKeySpec(originalKey.getEncoded(), ALGORITHM_NAME);
            System.out.println(new String(originalKey.getEncoded(),"UTF-8"));
            Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted, "utf-8");
        } catch (Exception e) {
            return null;
        }
    }


}
