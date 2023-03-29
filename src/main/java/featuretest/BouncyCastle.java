package featuretest;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Security;

public class BouncyCastle {
//    public static void main(String[] args) throws Exception {
//        // 注册BouncyCastle:
//        Security.addProvider(new BouncyCastleProvider());
//        // 按名称正常调用:
//        MessageDigest md = MessageDigest.getInstance("RipeMD160");
//        md.update("HelloWorld".getBytes("UTF-8"));
//        byte[] result = md.digest();
//        System.out.println(new BigInteger(1, result).toString(16));
//    }


    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
        SecretKey key = keyGen.generateKey();
        // 打印随机生成的key:
        byte[] skey = key.getEncoded();
        System.out.println(new BigInteger(1, skey).toString(16));
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        mac.update("HelloWorld".getBytes("UTF-8"));
        byte[] result = mac.doFinal();
        System.out.println(new BigInteger(1, result).toString(16));
    }
}
