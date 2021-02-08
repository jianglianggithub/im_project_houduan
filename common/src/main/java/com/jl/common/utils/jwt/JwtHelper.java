package com.jl.common.utils.jwt;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Author: Helon
 * @Description: JWT工具类
 * 参考官网：https://jwt.io/
 * JWT的数据结构为：A.B.C三部分数据，由字符点"."分割成三部分数据
 * A-header头信息
 * B-payload 有效负荷 一般包括：已注册信息（registered claims），公开数据(public claims)，私有数据(private claims)
 * C-signature 签名信息 是将header和payload进行加密生成的
 * @Data: Created in 2018/7/19 14:11
 * @Modified By:
 */
@Slf4j
public class JwtHelper {



    /**
     * @Author: Helon
     * @Description: 生成JWT字符串
     * 格式：A.B.C
     * A-header头信息
     * B-payload 有效负荷
     * C-signature 签名信息 是将header和payload进行加密生成的
     * @param userId - 用户编号

     * @param roles - 角色
     * @Data: 2018/7/28 19:26
     * @Modified By:
     */
    public static String generateJWT(String userId, String phone, String roles) {
        //签名算法，选择SHA-256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        //Date now = new Date(nowTimeMillis);
        //将BASE64SECRET常量字符串使用base64解码成字节数组
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecretConstant.BASE64SECRET);
        //使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>();
        /*
            Header
            {
              "alg": "HS256",
              "typ": "JWT"
            }
         */
        headMap.put("alg", SignatureAlgorithm.HS256.getValue());
        headMap.put("typ", "JWT");
        JwtBuilder builder = Jwts.builder().setHeader(headMap)
                /*
                    Payload
                    {
                      "userId": "1234567890",
                      "userName": "John Doe",
                    }
                 */
                //加密后的客户编号
               // .claim("userId", AESSecretUtil.encryptToStr(userId, SecretConstant.DATAKEY))
                .claim("userId", userId)
                //手机号
                .claim("phone", phone)
                //客户端浏览器信息
                .claim("roles", roles)
                //Signature
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (SecretConstant.EXPIRESSECOND >= 0) {
            long expMillis = nowTimeMillis + SecretConstant.EXPIRESSECOND;
            Date expDate = new Date(expMillis);
            builder.setExpiration(expDate);
        }
        return builder.compact();
    }

    /**
     * @Author: Helon
     * @Description: 解析JWT
     * 返回Claims对象
     * @param jsonWebToken - JWT
     * @Data: 2018/7/28 19:25
     * @Modified By:
     */
    public static Claims parseJWT(String jsonWebToken) {
        Claims claims = null;
        try {
            if (StringUtils.isNotBlank(jsonWebToken)) {
                //解析jwt
                claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SecretConstant.BASE64SECRET))
                        .parseClaimsJws(jsonWebToken).getBody();
            }else {
                log.warn("[JWTHelper]-json web token 为空");
            }
        } catch (Exception e) {
            log.error("[JWTHelper]-JWT解析异常：可能因为token已经超时或非法token");
        }
        return claims;
    }

    /**
     * @Author: Helon
     * @Description: 校验JWT是否有效
     * 返回json字符串的demo:
     * {"freshToken":"A.B.C","userName":"Judy","userId":"123", "userAgent":"xxxx"}
     * freshToken-刷新后的jwt
     * userName-客户名称
     * userId-客户编号
     * userAgent-客户端浏览器信息
     * @param jsonWebToken - JWT
     * @Data: 2018/7/24 15:28
     * @Modified By:
     */
    public static Map<String,String> validateLogin(String jsonWebToken) {
        Map<String, String> retMap = null;
        Claims claims = parseJWT(jsonWebToken);
        if (claims != null) {
            //解密客户编号
            //String decryptUserId = AESSecretUtil.decryptToStr((String)claims.get("userId"), SecretConstant.DATAKEY);
            retMap = new HashMap<>();
            //加密后的客户编号
            retMap.put("userId", (String) claims.get("userId"));
            //客户名称
            retMap.put("phone", (String) claims.get("phone"));
            //客户端浏览器信息
            retMap.put("roles", (String) claims.get("roles"));
            //刷新JWT
            //retMap.put("freshToken", generateJWT(decryptUserId, (String)claims.get("userName"), (String)claims.get("userAgent"), (String)claims.get("domainName")));
        }else {
            log.warn("[JWTHelper]-JWT解析出claims为空");
        }
        return retMap;
    }

    public static void main(String[] args) throws InterruptedException {

        String jsonWebKey = generateJWT("8", "13145202630",
                "ROLE_3");
        System.out.println(jsonWebKey);

        System.out.println(parseJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxMyIsInBob25lIjoiMTMxNDUyMDI2MzAiLCJyb2xlcyI6IlJPTEVfMyIsImV4cCI6MTU5MDY1MzkxMH0.wWPC4chTyhktStdQt52x8l_ks1H1YKs9GY6uHmGDJEk"));
//        System.out.println(jsonWebKey);
//        Claims claims =  parseJWT(jsonWebKey);
//        System.out.println(claims);
//        System.out.println(validateLogin(jsonWebKey));
    }
}
