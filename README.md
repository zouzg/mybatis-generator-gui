mybatis-generator-gui
==============

mybatis-generator-gui是基于[mybatis generator](http://www.mybatis.org/generator/index.html)开发一款界面工具, 本工具可以使你非常容易及快速生成Mybatis的Java POJO文件及数据库Mapping文件。

![image](https://raw.githubusercontent.com/slankka/mybatis-generator-gui/slankka-pic/mybatisGeneratorUI_20180603025329.png)

### 核心特性
* 按照界面步骤轻松生成代码，省去XML繁琐的学习与配置过程
* 保存数据库连接与Generator配置，每次代码生成轻松搞定
* 内置常用插件，比如分页插件
* 把数据库中表列的注释生成为Java实体的注释，生成的实体清晰明了
* 可选的去除掉对版本管理不友好的注释，这样新增或删除字段重新生成的文件比较过来清楚
* 目前已经支持Mysql、Mysql8、Oracle、PostgreSQL与SQL Server，暂不对其他非主流数据库提供支持。

### 要求
本工具由于使用了Java 8的众多特性，所以要求JDK <strong>1.8.0.60</strong>以上版本，另外<strong>JDK 1.9</strong>暂时还不支持。

### 下载
你可以从本链接下载本工具: https://github.com/astarring/mybatis-generator-gui/releases

### 启动本软件

* 方法一: 自助构建

```bash
    git clone https://github.com/astarring/mybatis-generator-gui
    cd mybatis-generator-gui
    mvn jfx:jar
    cd target/jfx/app/
    java -jar mybatis-generator-gui.jar
```

* 方法二: IDE中运行

Eclipse or IntelliJ IDEA中启动, 找到```com.zzg.mybatis.generator.MainUI```类并运行就可以了

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
    
### QQ群
鉴于有的同学可能有一些特殊情况不能使用，我建了一个QQ群供大家交流，QQ群号：613911142（已满），608454894

### 赞助
本工具纯属个人业余时间开发与维护，如果你觉得软件对你有用，欢迎赞助，金额随意 :)

<img src="https://user-images.githubusercontent.com/3505708/38162358-423d2d18-3512-11e8-9b52-c188bf37671d.png" width="200"> <img src="https://user-images.githubusercontent.com/3505708/38162365-56f8f55c-3512-11e8-8fd8-b8ae4f0ad3c8.png" width="200">

- - -
Licensed under the Apache 2.0 License

Copyright 2017 by Owen Zou
