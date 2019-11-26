
#### About JavaProbe:

A Java runtime information-gathering tool which uses the Java Attach API for information acquisition (the related technical articles links are attached to the end of this article).


#### Instructions:

* Environment:    

        1. jdk version >= 1.6, linux, runuser (used to solve privilege issues between different users), and root privilege
        2. Make your own tests before using it


* Usage:

        1.Switch to root permissions, give a suitable permission to the JavaProbe.jar, and ensure the JVM instance can load it (the easiest way is run in /tmp/ directory)        
        2.Execute java -jar JavaProbe.jar
        3.Json as result will be generated in /tmp/javainfo_result.txt
    
* Data example:


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
            "jarFileList": ["/usr/local/tomcat/bin/bootstrap.jar", "/usr/local/tomca/webapps/webscan/WEB-INF/lib/fastjson-1.2.58.jar", ".. ..."],
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
		}]
        }]
    }
        
    ``` 

* TODO:

        1. Unload agent class

#### FAQ:

    Q: Have you deployed JavaProbe in a production environment? 
    A: Since 2017, it has been deployed on 90,000+ machines without any collateral damage.
    The agent class logic is simple, and it will not damage the common business theoretically, but the JVM instance which will be hooked include agent class as residual, which is the current problem waiting to be solved. 
    
    Q: What Java applications are supported by JavaProbe?
    A: Common applications are supported, such as .war deployment and springboot .jar deployment, and so on.
    
    Q: What data can we get with JavaProbe?  
    A: virtual machine name, jdk version, classpath, run directory, temporary directory, running user, imported jar package, loaded class, etc.
    
    Q: Why dose JavaProbe need the "root" privilege?
    A: Because the JVMs with different user privilege are isolated and cannot be cross-permissions HOOKED, but root can use the runuser to run as different users to hook the user's own JVM instance.
    
    Q: I still don't understand what can JavaProbe do?
    A: 1.Check Java applications that use third-party components vulnerable (emergency response)
       2.Statistics of intranet Java applications status (fingerprint identification)
       3.Check legacy old vulnerabilities (time bombs) and development, which include high-risk services (like jolokia) (security baseline)

#### Example:
![](https://i.bmp.ovh/imgs/2019/09/df7b2af94f8da804.png)

#### References:
Https://www.ibm.com/developerworks/cn/java/j-lo-jse61/index.html Java Instrumentation

