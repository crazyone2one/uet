# UET

本项目可实现将写在excel中的测试用例数据导入到testlink系统中。

## Getting Started

### Prerequisites

* java 1.8+
* maven
* git

### Installation

* 创建api key

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

## Usage

* 编译项目

  ~~~bash
  mvn clean
  ~~~

* 打包项目

  ~~~bash
  mvn package
  ~~~

* 运行项目

  ~~~bash
  java -jar xx.jar
  ~~~

  

## Contributing

## License

Distributed under the MIT License. See [LICENSE](https://github.com/crazyone2one/uet/blob/master/LICENSE) for more information.

