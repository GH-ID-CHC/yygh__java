# nacos

地址：http://192.168.114.128:8848/nacos

命名空间：yygh

![image-20240401213902056](images/image-20240401213902056.png)

## 远程调用

1. 通过maven模块的引入，将`service_client`模块引入到`serivce_hosp`（调用方）中

![image-20240401220914649](images/image-20240401220914649.png)

2. 通过服务名称在nacos注册中心获取数据，然后拼接下面的sql可以获取到接口路径

   ![image-20240401221055426](images/image-20240401221055426.png)