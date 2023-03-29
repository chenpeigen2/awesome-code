package featuretest;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import javax.crypto.Cipher;
import javax.crypto.spec.PBEKeySpec;

public class RSA {
    public static void main(String[] args) throws Exception {
        // 明文:
        byte[] plain = "Hello, encrypt use RSA".getBytes("UTF-8");
        // 创建公钥／私钥对:
        Person alice = new Person("Alice");
        // 用Alice的公钥加密:
        byte[] pk = alice.getPublicKey();
        System.out.println(String.format("public key: %x", new BigInteger(1, pk)));
        byte[] encrypted = alice.encrypt(plain);
        System.out.println(String.format("encrypted: %x", new BigInteger(1, encrypted)));
        // 用Alice的私钥解密:
        byte[] sk = alice.getPrivateKey();
        System.out.println(String.format("private key: %x", new BigInteger(1, sk)));
        byte[] decrypted = alice.decrypt(encrypted);
        System.out.println(new String(decrypted, "UTF-8"));

        equals();
    }

    public static void equals() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);//RSA keys must be at least 512 bits long
        KeyPair key = keyGen.generateKeyPair();
        byte[] pubBytes = null;
        byte[] priBytes = null;

        PublicKey pubKey = key.getPublic();
        pubBytes = pubKey.getEncoded();
        // Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys
        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(pubBytes);

        PrivateKey priKey = key.getPrivate();
        priBytes = priKey.getEncoded();
        // Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(priBytes);

        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pubkey2 = factory.generatePublic(pubX509);
        PrivateKey prikey2 = factory.generatePrivate(priPKCS8);
        System.out.println(pubKey.equals(pubkey2));
        System.out.println(priKey.equals(prikey2));
    }

    public static void e1() {
    }
}

class Person {
    String name;
    // 私钥:
    PrivateKey sk;
    // 公钥:
    PublicKey pk;

    public Person(String name) throws GeneralSecurityException {
        this.name = name;
        // 生成公钥／私钥对:
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(1024);
        KeyPair kp = kpGen.generateKeyPair();
        this.sk = kp.getPrivate();
        this.pk = kp.getPublic();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        var as = keyFactory.getKeySpec(pk, RSAPublicKeySpec.class);
        var as1 = keyFactory.getKeySpec(sk, RSAPrivateKeySpec.class);


        BigInteger modulus = as.getModulus();
        BigInteger pubExp = as.getPublicExponent();
        BigInteger priExp = as1.getPrivateExponent();

//        priExp = new BigInteger(priExp.toString().replaceAll("0","1")); // should be spec

        var pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
        var privKeySpec = new RSAPrivateKeySpec(modulus, priExp);

        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privKeySpec);

        this.pk = pubKey;
        this.sk = privKey;
    }

    // 把私钥导出为字节
    public byte[] getPrivateKey() {
        return this.sk.getEncoded();
    }

    // 把公钥导出为字节
    public byte[] getPublicKey() {
        return this.pk.getEncoded();
    }

    // 用公钥加密:
    public byte[] encrypt(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.pk);
        return cipher.doFinal(message);
    }

    // 用私钥解密:
    public byte[] decrypt(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.sk);
        return cipher.doFinal(input);
    }
}
