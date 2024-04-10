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
