# UET

本项目可实现将写在excel中的测试用例数据导入到testlink系统中。

## Getting Started
https://img.shields.io/github/v/release/crazyone2one/uet?style=plastic

### 依赖环境

* java 1.8+
* maven
* git

### 配置

* 登录testlink系统，创建Personal API access key

* ![image-20210621145029900](C:\Users\jingll\AppData\Roaming\Typora\typora-user-images\image-20210621145029900.png)

* clone the repo

  ~~~bas
  git@github.com:crazyone2one/uet.git
  ~~~

* 更改application.properties文件中参数

  ~~~txt
  server.port=8088
  #开启 multipart 上传功能
  spring.servlet.multipart.enabled=true
  #最大文件大小
  spring.servlet.multipart.max-file-size=200MB
  # 修改testlink的url和key
  testlink.url=http://192.168.4.51/testlink/lib/api/xmlrpc/v1/xmlrpc.php
  testlink.key=cc858594b55ab99efce47d4cc39d1c37
  ~~~

## 使用

* 编译打包项目

  ~~~maven
  mvn clean package
  ~~~

* 运行项目

  ~~~maven
  java -jar xx.jar
  ~~~

* 使用docker运行

 > mvn clean package
 >
 > docker build -t [镜像名称] .
 >
 > docker run -d --name [容器名称] -p 8088:8088 [镜像名称]

## Contributing

## License

Distributed under the MIT License. See [LICENSE](https://github.com/crazyone2one/uet/blob/master/LICENSE) for more information.

