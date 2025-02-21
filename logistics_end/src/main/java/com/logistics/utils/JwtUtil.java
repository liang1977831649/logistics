package com.logistics.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "student";

    //接收业务数据,生成token并返回
    public static String getTokenForMap(Map<String, Object> claims) {
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12 * 5 ))
                .sign(Algorithm.HMAC256(KEY));
    }
    public static String getTokenForString(String str) {
        return JWT.create()
                .withClaim("claims", str)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12 * 5 ))
                .sign(Algorithm.HMAC256(KEY));
    }

    //接收token,验证token,并返回业务数据
    public static Map<String, Object> parseTokenForMap(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
    public static String parseTokenForString(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asString();
    }
}
