package org.peter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


//https://javadoc.io/doc/com.auth0/java-jwt/latest/index.html

/**
 * interface that holds the default method
 * <a href="https://javadoc.io/static/com.auth0/java-jwt/4.2.1/com/auth0/jwt/interfaces/Claim.html">Claim</a>
 * <a href="https://javadoc.io/static/com.auth0/java-jwt/4.2.1/com/auth0/jwt/interfaces/Payload.html">Payload</a>
 * <a href="https://javadoc.io/static/com.auth0/java-jwt/4.2.1/com/auth0/jwt/interfaces/Verification.html">Verification</a>
 */


/**
 * HeaderParams
 * <p>
 <<<<<<< HEAD
 * Contains constants representing the JWT header parameter names.
 * JWT
 * <p>
 * Exposes all the JWT functionalities.
 * JWTCreator
 * <p>
 * The JWTCreator class holds the sign method to generate a complete JWT (with Signature) from a given Header and Payload content.
 * JWTCreator.Builder
 * <p>
 * The Builder class holds the Claims that defines the JWT to be created.
 =======
 * `only constants`
 * Contains constants representing the JWT header parameter names.
 * <p>
 * JWT
 * <p>
 * `entry`
 * Exposes all the JWT functionalities.
 * <p>
 * JWTCreator
 * <p>
 * `no usage for API`
 * The JWTCreator class holds the sign method to generate a complete JWT (with Signature) from a given Header and Payload content.
 * <p>
 * JWTCreator.Builder
 * <p>
 * The Builder class holds the Claims that defines the JWT to be created.
 * <p>
 >>>>>>> 3046c59 (jjwt3)
 * JWTVerifier
 * <p>
 * The JWTVerifier class holds the verify method to assert that a given Token has not only a proper JWT format, but also its signature matches.
 * JWTVerifier.BaseVerification
 * <p>
 <<<<<<< HEAD
 * Verification implementation that accepts all the expected Claim values for verification, and builds a JWTVerifier used to verify a JWT's signature and expected claims.
 * RegisteredClaims
 * <p>
 * Contains constants representing the name of the Registered Claim Names as defined in Section 4.1 of RFC 7529
 * <p>
 * Algorithm
 * <p>
 =======
 * <p>
 * Verification implementation that accepts all the expected Claim values for verification, and builds a JWTVerifier used to verify a JWT's signature and expected claims.
 * RegisteredClaims
 * <p>
 * only constants for default usage
 * Contains constants representing the name of the Registered Claim Names as defined in Section 4.1 of RFC 7529
 * <p>
 * <p>
 * Algorithm
 * <p>
 * `entry`
 >>>>>>> 3046c59 (jjwt3)
 * The Algorithm class represents an algorithm to be used in the Signing or Verification process of a Token.
 */
public class Main {

    String s = "";

    public static void main(String[] args) {
        var app = new Main();
        app.createToken();
        app.checkToken();
    }


    public void createToken() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, 900);

        Map<String, Object> map = new HashMap<>();

        System.out.println(Calendar.getInstance().getTime());
        System.out.println(instance.getTime());

        // 生成令牌
        String token = JWT.create()
                .withHeader(map) // 设置Header 可以不写
                .withClaim("userId", 21) // 设置payload
                .withClaim("username", "张三") // 自定义用户名
                .withExpiresAt(instance.getTime()) // 过期时间
                .sign(Algorithm.HMAC256("!##$d34jd*)#"));// 设置签名

        s = token;
        System.out.println(token);
    }

    public void checkToken() {
        // 创建验证Token的对象

//        JWTVerifier verifier = JWTVerifier.init(Algorithm.RSA256(publicKey, privateKey)
//                .withIssuer("auth0")
//                .build();
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("!##$d34jd*)#")).build();
        DecodedJWT verify = jwtVerifier.verify(s);
        System.out.println("用户Id: " + verify.getClaim("userId").asInt());
        System.out.println("用户名: " + verify.getClaim("username").asString());
    }

}