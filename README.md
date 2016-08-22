mybatis-generator-gui
==============

mybatis-generator-gui是基于[mybatis generator](http://www.mybatis.org/generator/index.html)开发一款界面工具, 本工具可以使你非常容易及快速生成Mybatis的Java POJO文件及数据库Mapping文件。

![MainUI](https://cloud.githubusercontent.com/assets/3505708/17854987/ab4ed078-68a8-11e6-824e-ada64d0b4ad1.png)

### 核心特性
* 按照界面步骤轻松生成代码，省去XML繁琐的学习与配置过程
* 保存数据库连接与Generator配置，每次代码生成轻松搞定
* 内置常用插件，比如offset
* 可选的去除掉对版本管理不友好的注释，这样新增或删除字段重新生成的文件比较过来清楚
* 目前已经支持Mysql、Oracle与PostgreSQL，如果有对其它数据库有需求的请提Issue我会跟进。

### 要求
本工具由于使用了Java 8的众多特性，所以要求 JRE或者JDK 8.0以上版本，对于JDK版本还没有升级的童鞋表示歉意。

### 下载
你可以从本链接下载本工具: https://github.com/astarring/mybatis-generator-gui/releases

### 自助构建
    git clone https://github.com/astarring/mybatis-generator-gui
    cd mybatis-generator-gui
    mvn jfx:jar
### 启动本软件
* 方法一: 下载的zip包

解压zip包，如果安装好了JRE或者JDK 8，直接cd至软件目录运行

    java -jar mybatis-generator-gui.jar

* 方法二: 自助构建


    cd target/jfx/app/
    java -jar mybatis-generator-gui.jar

* 方法三: IDE中运行

Eclipse or IntelliJ IDEA中启动, 找到MainUI类并运行就可以了

### 文档
更多详细文档请参考本库的Wiki
* [Usage](https://github.com/astarring/mybatis-generator-gui/wiki/Usage-Guide)


### 贡献
目前本工具只是本人项目人使用到了并且觉得非常有用所以把它开源，如果你觉得有用并且想改进本软件，你可以：
* 对于你认为有用的功能，你可以在Issue提，我可以开发的尽量满足
* 对于有Bug的地方，请按如下方式在Issue中提bug
    * 如何重现你的bug，包括你使用的系统，JDK版本，数据库类型及版本
    * 如果有任何的错误截图会更好


------
Licensed under the Apache 2.0 License

Copyright 2016 by Owen Zou
