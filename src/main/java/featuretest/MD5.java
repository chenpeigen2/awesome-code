package featuretest;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;

public class MD5 {
    public static void main(String[] args) throws Exception {
        // 创建一个MessageDigest实例:
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        // 反复调用update输入数据:
//        md.update("Hello".getBytes("UTF-8"));
//        md.update("World".getBytes("UTF-8"));
//        byte[] result = md.digest(); // 16 bytes: 68e109f0f40ca72a15e05cc22786f8e6
//        System.out.println(new BigInteger(1, result).toString(16));


        // 创建一个MessageDigest实例:
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        // 反复调用update输入数据:
        md.update("Hello".getBytes("UTF-8"));
        md.update("World".getBytes("UTF-8"));
        byte[] result = md.digest(); // 20 bytes: db8ac1c259eb89d4a131b253bacfca5f319d54f2
        System.out.println(new BigInteger(1, result).toString(16));
    }
}


class MD5Utils {
    // 不带秘钥加密
    public static String md5(String text) {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text);
        System.out.println("MD52加密后的字符串为:" + md5str + "\t长度：" + md5str.length());
        return md5str;
    }

    /**
     * MD5验证方法
     *
     * @param text明文
     * @param key密钥
     * @param md5密文
     */
    // 根据传入的密钥进行验证
    public static boolean verify(String text, String md5) {
        String md5str = md5(text);
        if (md5str.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    // 测试
    public static void main(String[] args) {
        // String str =
        // "181115.041650.10.88.168.148.2665.2419425653_1";181115.040908.10.88.181.118.3013.1655327821_1
        String str = "181115.040908.10.88.181.118.3013.1655327821_1";
        System.out.println("加密的字符串：" + str + "\t长度：" + str.length());
        MD5Utils.md5(str);
    }
}

