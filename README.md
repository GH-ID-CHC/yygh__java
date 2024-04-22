## 医院上传接口

![医院长传流程](images/医院长传流程.png)

## nacos

地址：http://192.168.114.128:8848/nacos

命名空间：yygh

![image-20240401213902056](images/image-20240401213902056.png)

### 远程调用

1. 通过maven模块的引入，将`service_client`模块引入到`serivce_hosp`（调用方）中

![image-20240401220914649](images/image-20240401220914649.png)

2. 通过服务名称在nacos注册中心获取数据，然后拼接下面的url可以获取到接口路径

   ![image-20240401221055426](images/image-20240401221055426.png)

## 网关解决跨域

没有配置网关之前使用`@CrossOrigin`进行解决

```java
/**
 * 解决跨域问题
 * @program: yygh
 * @author:
 * @create: 2024-04-10 21:39
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
```

> 需要再次去除`@CrossOrigin`注解

![image-20240410214615332](images/image-20240410214615332.png)

## 状态码

```java
package com.chai.yygh.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    PARAM_ERROR( 202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    DATA_UPDATE_ERROR(205, "数据版本异常"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),

    CODE_ERROR(210, "验证码错误"),
//    LOGIN_MOBLE_ERROR(211, "账号不正确"),
    LOGIN_DISABLED_ERROR(212, "改用户已被禁用"),
    REGISTER_MOBLE_ERROR(213, "手机号已被使用"),
    LOGIN_AURH(214, "需要登录"),
    LOGIN_ACL(215, "没有权限"),

    URL_ENCODE_ERROR( 216, "URL编码失败"),
    ILLEGAL_CALLBACK_REQUEST_ERROR( 217, "非法回调请求"),
    FETCH_ACCESSTOKEN_FAILD( 218, "获取accessToken失败"),
    FETCH_USERINFO_ERROR( 219, "获取用户信息失败"),
    //LOGIN_ERROR( 23005, "登录失败"),

    PAY_RUN(220, "支付中"),
    CANCEL_ORDER_FAIL(225, "取消订单失败"),
    CANCEL_ORDER_NO(225, "不能取消预约"),

    HOSCODE_EXIST(230, "医院编号已经存在"),
    NUMBER_NO(240, "可预约号不足"),
    TIME_NO(250, "当前时间不可以预约"),

    SIGN_ERROR(300, "签名错误"),
    HOSPITAL_OPEN(310, "医院未开通，暂时不能访问"),
    HOSPITAL_LOCK(320, "医院被锁定，暂时不能访问"),
    ;

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

## 登录

![image-20240416214913361](images/image-20240416214913361.png)

（1）手机号码+手机验证码

（2）微信扫描

1，无注册界面，第一次登录根据手机号判断系统是否存在，如果不存在则自动注册

2，微信扫描登录成功必须绑定手机号码，即：第一次扫描成功后绑定手机号，以后登录扫描直接登录成功

## Token（JWT生成）

JWT最重要的作用就是对 token信息的**防伪**作用。

JWT的原理， 

一个JWT由三个部分组成：公共部分、私有部分、签名部分。最后由这三者组合进行base64编码得到JWT。

![image-20240416223357960](images/image-20240416223357960.png)

1. 添加依赖

   ```xml
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt</artifactId>
   </dependency>
   ```

2. 添加工具类

```java
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
```

3. 生成token

   ```java
   String token = JwtHelper.createToken(userInfo.getId(), name);
   ```

## 阿里云短信服务

![image-20240417212226003](images/image-20240417212226003.png)

![image-20240417213024722](images/image-20240417213024722.png)

![image-20240417213029677](images/image-20240417213029677.png)

![image-20240417213036552](images/image-20240417213036552.png)

![image-20240417213042335](images/image-20240417213042335.png)

## 验证码

将生成的验证码放在redis中并设置过期的时间

```
key:手机号

value:验证码
```

```java
/**
 * 生成验证码
 * @program: yygh
 * @author:
 * @create: 2024-04-17 21:40
 */
public class RandomUtil {

    private static final Random random = new Random();

    private static final DecimalFormat fourdf = new DecimalFormat("0000");

    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    public static String getFourBitRandom() {
        return fourdf.format(random.nextInt(10000));
    }

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }

    /**
     * 给定数组，抽取n个数据
     * @param list
     * @param n
     * @return
     */
    public static ArrayList getRandom(List list, int n) {

        Random random = new Random();

        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

// 生成随机数字并存入HashMap
        for (int i = 0; i < list.size(); i++) {

            int number = random.nextInt(100) + 1;

            hashMap.put(number, i);
        }

// 从HashMap导入数组
        Object[] robjs = hashMap.values().toArray();

        ArrayList r = new ArrayList();

// 遍历数组并打印数据
        for (int i = 0; i < n; i++) {
            r.add(list.get((int) robjs[i]));
            System.out.print(list.get((int) robjs[i]) + "\t");
        }
        System.out.print("\n");
        return r;
    }
}
```

> 生成6为验证码：`RandomUtil.getSixBitRandom();`

## 用户权限认证

1. 所有请求都会经过服务网关，服务网关对外暴露服务，在网关进行统一用户认证；
2. 既然要在网关进行用户认证，网关得知道对哪些url进行认证，所以我们得对ur制定规则
3. Api接口异步请求的，我们采取url规则匹配，如：/api/**/auth/**，如凡是满足该规则的都必须用户认证



> 使用过滤器进行实现，非法操作进行过滤

## OAuth2

1. 开放系统之间的授权问题

   ![image-20240420145252966](images/image-20240420145252966.png)

   > 使用令牌的方式进行解决

2. 单点登录

如：登录百度贴吧，访问百度其它账号则 不需要再次登录百度账号

![image-20240420150233333](images/image-20240420150233333.png)

## 微信登录

![image-20240420150527316](images/image-20240420150527316.png)

![img](images/D0wkkHSbtC6VUSHX4WsjP5ssg5mdnEmXO8NGVGF34dxS9N1WCcq6wvquR4K_Hcut.png)

1. 生成二维码

![image-20240420161621308](images/image-20240420161621308.png)

https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html

## httpclient

![image-20240422213352579](images/image-20240422213352579.png)

> 拼接请求地址

```java
public class HttpClientUtils {

    public static final int connTimeout=10000;
    public static final int readTimeout=10000;
    public static final String charset="UTF-8";
    private static HttpClient client = null;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }

    public static String postParameters(String url, String parameterStr,String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }

    public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    public static String postParameters(String url, Map<String, String> params, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    public static String get(String url) throws Exception {
        return get(url, charset, null, null);
    }

    public static String get(String url, String charset) throws Exception {
        return get(url, charset, connTimeout, readTimeout);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param body RequestBody
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset 编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException  响应超时
     * @throws Exception
     */
    public static String post(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout)
            throws ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }


    /**
     * 提交form表单
     *
     * @param url
     * @param params
     * @param connTimeout
     * @param readTimeout
     * @return
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     * @throws Exception
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 发送一个 GET 请求
     */
    public static String get(String url, String charset, Integer connTimeout,Integer readTimeout)
            throws ConnectTimeoutException,SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(get);
            }

            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }

    /**
     * 从 response 里获取 charset
     */
    @SuppressWarnings("unused")
    private static String getCharsetFromResponse(HttpResponse ressponse) {
        // Content-Type:text/html; charset=GBK
        if (ressponse.getEntity() != null  && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {
            String contentType = ressponse.getEntity().getContentType().getValue();
            if (contentType.contains("charset=")) {
                return contentType.substring(contentType.indexOf("charset=") + 8);
            }
        }
        return null;
    }

    /**
     * 创建 SSL连接
     * @return
     * @throws GeneralSecurityException
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } catch (GeneralSecurityException e) {
            throw e;
        }
    }
}
```

> httpclient工具类

![image-20240422213458306](images/image-20240422213458306.png)

## 阿里OSS服务

文件服务系统

用户认证需要上传证件图片、首页轮播也需要上传图片，因此我们要做文件服务，阿里云oss是一个很好的分布式文件服务系统，所以我们需要集成阿里云oss即可

![image-20240422222004728](images/image-20240422222004728.png)

1. 开通服务

2. 创建bucket列表

   ![image-20240422222530369](images/image-20240422222530369.png)

   ![image-20240422222801779](images/image-20240422222801779.png)

3. 
