#### 关于JavaProbe:

在甲方的安全建设当中，应用数据收集无疑是非常重要的工作，在所有的应用资产中，Java应用应该是属于问题较多、漏洞较为严重的一种。我写了个简单的工具来收集这些信息，他还有很多问题，开源之后希望和大家可以一起完善下。

做一个Java运行信息收集工具，我思考了很多方案。其中包括了比较边缘的办法，比如全量扫描主机jar文件，扫描pom文件，解析JavaWeb容器的配置文件等，但是感觉这些操作有一些缺点，比如操作比较复杂，对主机的性能操作可能也有影响、获取的数据维度较少后期数据运营分析不方便等等，后来综合考虑，使用了Java Attach API来进行信息的获取(相关的技术文章附在了结尾链接)。


#### JavaProbe使用说明:
    
* 运行环境要求:    

        1. >= jdk1.6,linux主机,安装了runuser(解决用户之间权限问题),root用户运行
        2. 用之前自己做好测试


* 使用方法:

        1.切换至root权限,给JavaProbe.jar文件设置下权限，保证其他用户权限运行的JVM实例可以加载(在/tmp/目录运行最省事了)
        2.执行 java -jar JavaProbe.jar
        3.结果会生成在/tmp/javainfo_result.txt当中,是json格式，根据所需解析存储。
        
* 数据示例


    ```json
    
    {
        "hostname": "xxxxx.net",
        "jvmInfoList": [{
            "jvmName": "org.apache.catalina.startup.Bootstrap start",
            "version": "1.8.0_171",
            "jvmHome": "/usr/java/jdk1.8.0_171/jre",
            "classPath": "/usr/local/tomcat/bin/bootstrap.jar:/usr/local/tomcat/bin/tomcat-juli.jar",
            "libraryPath": "/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib",
            "tmpdir": "/usr/local/tomcat/temp",
            "extdirs": "/usr/java/jdk1.8.0_171/jre/lib/ext:/usr/java/packages/lib/ext",
            "userName": "root",
            "userDir": "/data/tomcat",
            "exceTime": "2019-09-25 10:57:25",
            "jarFileList": ["/usr/local/tomcat/bin/bootstrap.jar", "/usr/local/tomca/webapps/webscan/WEB-INF/lib/fastjson-1.2.58.jar", "....."],
            "classFileList": ["sun.reflect.GeneratedMethodAccessor126", "....."],
            "errorList": [],
            "jarPathMap": {
                "file:/usr/local/tomcat/lib/": "",
                "file:/usr/local/tomcat/webapps/webscan/WEB-INF/classes/": ""
            },
        "dependencyInfoList": [{
                "version": "4.5.2",
                "groupId": "org.apache.httpcomponents",
                "artifactId": "httpclient"
            }, {
                "version": "3.1.0",
                "groupId": "javax.servlet",
                "artifactId": "javax.servlet-api"
            },
            {"...."}]
        }]
    }
        
    ``` 
    
* TODO:

        1.agent class卸载

#### 问题:
    
    问:JavaProbe你有在生产环境部署过吗？ 
    答:2017年开始在公司部署了9万+机器,没有对生产环境造成影响。
    Agent类逻辑简单，理论上不会影响正常的业务,但是会在HOOK的JVM实例里面残留Agent的class，目前在解决这个问题。

    问:JavaProbe目前都支持哪些形式开发的Java应用？
    答:常见的都支持比如war部署和springboot打jar包部署等等。

    问:JavaProbe都有啥数据啊？  
    答:虚拟机名称,jdk版本,classpath,运行目录，临时目录，运行用户，导入的jar包，加载的class等等。

    问:JavaProbe为啥要用root啊？
    答:因为不同用户权限的JVM之间是有隔离的，并不能跨权限HOOK，root用户是为了使用runuser来用不同的用户权限hook用户自己的JVM实例。

    问:我还是不太明白JavaProbe可以用来干啥？
    答:
        1.可以排查存使用了存在漏洞版本的第三方组件的Java应用(应急响应)
        2.统计内网Java应用状态(指纹识别)
        3.处理遗留旧漏洞(定时炸弹) + 开发引用了高风险的服务(比如jolokia)(安全基线)


#### 数据使用:
![](https://i.bmp.ovh/imgs/2019/09/df7b2af94f8da804.png)

#### 链接:
https://www.ibm.com/developerworks/cn/java/j-lo-jse61/index.html 《Java Instrumentation》

