package com.chai.yygh.common.helper;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Jwt工具类
 * @program: yygh
 * @author:
 * @create: 2024-04-16 22:36
 */
public class JwtHelper {
    /** 令牌过期时间 */
    private static long tokenExpiration = 24*60*60*1000;
    /** 令牌签名密钥 */
    private static String tokenSignKey = "123456";

    /**
     * 根据参数生成token
     *
     * @param userId   用户id
     * @param userName 用户名
     * @return {@link String}
     */
    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("YYGH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /**
     * 根据token获取用户的id
     *
     * @param token 令牌
     * @return {@link Long}
     */
    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
    }

    /**
     * 获取用户名称
     *
     * @param token 令牌
     * @return {@link String}
     */
    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }
    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "55");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUserName(token));
    }
}
