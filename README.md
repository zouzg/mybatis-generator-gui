mybatis-generator-gui
==============

mybatis-generator-gui是基于[mybatis generator](http://www.mybatis.org/generator/index.html)开发一款界面工具, 本工具可以使你非常容易及快速生成Mybatis的Java POJO文件及数据库Mapping文件。

![image](https://user-images.githubusercontent.com/3505708/49334784-1a42c980-f619-11e8-914d-9ea85db9cec3.png)


![basic](https://user-images.githubusercontent.com/3505708/51911610-45754980-240d-11e9-85ad-643e55cafab2.png)


![overSSH](https://user-images.githubusercontent.com/3505708/51911646-5920b000-240d-11e9-9048-738306a56d14.png)

### 核心特性
* 按照界面步骤轻松生成代码，省去XML繁琐的学习与配置过程
* 保存数据库连接与Generator配置，每次代码生成轻松搞定
* 内置常用插件，比如分页插件
* 支持OverSSH 方式，通过SSH隧道连接至公司内网访问数据库
* 把数据库中表列的注释生成为Java实体的注释，生成的实体清晰明了
* 可选的去除掉对版本管理不友好的注释，这样新增或删除字段重新生成的文件比较过来清楚
* 目前已经支持Mysql、Mysql8、Oracle、PostgreSQL与SQL Server，暂不对其他非主流数据库提供支持。(MySQL支持的比较好，其他数据库有什么问题可以在issue中反馈)

### 要求
本工具由于使用了Java 8的众多特性，所以要求JDK <strong>1.8.0.60</strong>以上版本，另外<strong>JDK 1.9</strong>暂时还不支持。

### 下载
你可以从本链接下载本工具: http://tools.mingzhi.ink


### 启动本软件

* 方法一：下载
```bash
    cd /your_download_folder
    java -jar mybatis-generator-gui.jar
```
* 方法二: 自助构建

```bash
    git clone https://github.com/zouzg/mybatis-generator-gui
    cd mybatis-generator-gui
    mvn jfx:jar
    cd target/jfx/app/
    java -jar mybatis-generator-gui.jar
```

* 方法三: IDE中运行

Eclipse or IntelliJ IDEA中启动, 找到```com.zzg.mybatis.generator.MainUI```类并运行就可以了（主要你的IED运行的jdk版本是否符合要求）

- 方法三：打包为本地原生应用，双击快捷方式即可启动，方便快捷

  如果不想打包后的安装包logo为Java的灰色的茶杯，需要在pom文件里将对应操作系统平台的图标注释放开

```bash
	#<icon>${project.basedir}/package/windows/mybatis-generator-gui.ico</icon>为windows
	#<icon>${project.basedir}/package/macosx/mybatis-generator-gui.icns</icon>为mac
	mvn jfx:native
```

​	另外需要注意，windows系统打包成exe的话需要安装WiXToolset3+的环境；由于打包后会把jre打入安装包，两个平台均100M左右，体积较大请自行打包；打包后的安装包在target/jfx/native目录下

### 注意事项
* 本自动生成代码工具只适合生成单表的增删改查，对于需要做数据库联合查询的，请自行写新的XML与Mapper；
* 部分系统在中文输入方法时输入框中无法输入文字，请切换成英文输入法；
* 如果不明白对应字段或选项是什么意思的时候，把光标放在对应字段或Label上停留一会然后如果有解释会出现解释；


### 文档
更多详细文档请参考本库的Wiki
* [Usage](https://github.com/astarring/mybatis-generator-gui/wiki/Usage-Guide)


### 贡献
目前本工具只是本人项目人使用到了并且觉得非常有用所以把它开源，如果你觉得有用并且想改进本软件，你可以：
* 对于你认为有用的功能，你可以在Issue提，我可以开发的尽量满足
* 对于有Bug的地方，请按如下方式在Issue中提bug
    * 如何重现你的bug，包括你使用的系统，JDK版本，数据库类型及版本
    * 如果有任何的错误截图会更好
    * 如果你是一些常见的数据库连接、软件启动不了等问题，请先仔细阅读上面的文档，再解决不了在下面的QQ群中问（问问题的时候尽量把各种信息都提供好，否则只是几行文字是没有人愿意为你解答的）。
    
### QQ群
鉴于有的同学可能有一些特殊情况不能使用，我建了一个QQ群供大家交流，QQ群号：613911142（已满），608454894

- - -
Licensed under the Apache 2.0 License

Copyright 2017 by Owen Zou
