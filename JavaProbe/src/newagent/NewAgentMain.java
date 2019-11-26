package newagent;

import com.google.gson.Gson;
import common.CommonUtil;
import entity.JvmInfo;
import entity.RunUserInfo;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import entity.ResultInfo;
import maven.MavenHandle;
import org.apache.commons.io.FileUtils;
import runuser.RunHandle;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 用于加载jvm列表并且hook，包含一部分回传数据的工作
 */
public class NewAgentMain {

    public static void main(String[] args) throws InterruptedException {

        RunHandle.checkOS();

        String runUser = System.getProperty("user.name");

        // 自动获取用户 进行jvm检测
        if (args.length == 0) {

            List<RunUserInfo> runUserList = RunHandle.getUser();

            if (!"root".equals(runUser.toLowerCase().trim())) {

                System.out.println("在没有指定运行用户的模式下,必须使用root用户运行!"); // 样子货
                System.exit(0);
            }

            CommonUtil.writeStr("/tmp/jvm_start.txt","start");

            for (RunUserInfo runUserInfo : runUserList) {

                if (runUserInfo.getUserPid().size() < 1) {

                    continue;
                }
                System.out.println("HOOKING:" + runUserInfo.getUserName());
                RunHandle.runHookCommand(runUserInfo.getUserName());
            }

            Thread.sleep(5000);

            getResultFile(); // 读取结果
        }
        else {

            // 单用户jvm检测
            if (!runUser.equals(args[0].trim())) {

                System.out.println("此模块只能检测您当前权限下的JVM!");
            }

            hookIng();
        }
    }

    /**
     * 获取所有的结果 进行发送
     */
    public static String getResultFile() {

        String resultStr = null;
        MavenHandle mavenHandle = new MavenHandle(); // 处理依赖
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setHostname(getHostName());

        try {

            CommonUtil.deleteRepeat("/tmp/javainfo_result.txt");

            File tmpFile = new File("/tmp/");

            // 列出所有结果文件 进行整合
            for (File resultFile : tmpFile.listFiles()) {

                if (resultFile.getName().startsWith("jvminfo_")) {

                    List<String> jvmLine = FileUtils.readLines(resultFile, Charset.defaultCharset());

                    for (String tmpLine : jvmLine) {

                        if (!"".equals(tmpLine.trim())) {

                            JvmInfo jvmInfot =  getJsontoObj(tmpLine);

                            if (jvmInfot != null) {

                                // 处理springboot的方法
                                springBootHandle(jvmInfot);
                                // 清理掉所有的class数据,如果还需要收集的话可以注释下面的代码，这个一般是为了在没有jar的情况下去确定组件、应急排查rce加载的恶意class，判断高风险class
                                //jvmInfot.getClassFileList().clear();
                                // 把异常都抛掉
                                jvmInfot.getErrorList().clear();
                                // 获取依赖内容
                                mavenHandle.getMavenResult(jvmInfot);
                                // 添加到结果
                                resultInfo.getJvmInfoList().add(jvmInfot);

                            }
                        }
                    }
                }
            }

            if (resultInfo.getJvmInfoList() != null & resultInfo.getJvmInfoList().size() != 0) {

                resultStr = new Gson().toJson(resultInfo);
            }

            CommonUtil.writeStr("/tmp/javainfo_result.txt",resultStr);

            // 写完结果 再删除全部临时文件
            for (File resultFile : tmpFile.listFiles()) {

                if (resultFile.getName().startsWith("jvminfo_")) {

                    resultFile.delete();
                }
            }
        }
        catch (Exception e) {
            CommonUtil.writeStr("/tmp/jvm_error.txt","aaa\t" + e.getMessage());
            System.out.println(e.getMessage());
        }

        return resultStr;
    }

    /**
     * 开始hook
     */
    public static void hookIng() {

            try {

                String agentFile = System.getProperty("user.dir") + "/JavaProbe.jar";

                // 获取运行jvm
                List<VirtualMachineDescriptor> startVmList = VirtualMachine.list();

                    // 遍历jvm进行hook
                for (VirtualMachineDescriptor vminfo : startVmList) {

                    if (vminfo.displayName().indexOf("JavaProbe") > -1) {

                        continue; //不hook自己
                    }

                    try {

                        VirtualMachine vm = VirtualMachine.attach(vminfo.id());

                        System.out.println("即将hook" + vminfo.displayName() + "！\tagent路径:" + agentFile);

                        vm.loadAgent(agentFile, vminfo.displayName());

                        Thread.sleep(5000); // 沉睡5秒钟 等待agent里面的业务执行完毕，这里也可以去处理jvminfo.txt里面的数据来进行判断，但是怕出现异常导致文件对比始终不一致

                        vm.detach();
                    }
                    catch (Exception e) {
                        CommonUtil.writeStr("/tmp/jvm_error.txt","bbb\t" + e.getMessage());
                        continue; // hook错误的话 基本是权限问题 直接下次循环就好了
                    }
                }
        }
        catch(Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","ccc\t" + e.getMessage());

            System.out.println(e.getMessage()); // 这里的异常直接抛出了
        }

    }
    /**
     * 获取hostname
     * @return
     */
    public static String getHostName() {

        try {

            return InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","ddd\t" + e.getMessage());
            return "nothostname";
        }
    }

    /**
     * 转换json字符串 为对象
     * @param jsonstr
     * @return
     */
    public static JvmInfo getJsontoObj(String jsonstr) {

        try {

            JvmInfo jvmInfo = new Gson().fromJson(jsonstr, JvmInfo.class);

            return jvmInfo;
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","xxxx\t" + e.getMessage());
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * 处理可能是springboot的应用
     * @param jvmInfo
     */
    public static void springBootHandle(JvmInfo jvmInfo) {

        try {

            for (String keyStr : jvmInfo.getJarPathMap().keySet()) {

                if (keyStr.indexOf(".jar!/") > -1) {

                    String jarPath = keyStr.replace("jar:file:","");
                    springBootLibget(jvmInfo, jarPath.substring(0,jarPath.indexOf("jar!")+3));
                }
            }
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","jqjq\t" + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * 获取springboot应用的lib
     * @param jvmInfo
     */
    public static void springBootLibget(JvmInfo jvmInfo,String springbootAppFile) {

        try {

            JarFile jarFile = new JarFile(springbootAppFile);

            if ("org.springframework.boot.loader.JarLauncher".equals(jarFile.getManifest().getMainAttributes().getValue("Main-Class"))) {

                // 可以用清单中的Spring-Boot-Lib
               Enumeration<JarEntry> entryEnumeration = jarFile.entries();

               String jarPath = "";

               while (entryEnumeration.hasMoreElements()) {

                   if ((jarPath = entryEnumeration.nextElement().toString()).endsWith(".jar")) {

                       jvmInfo.getJarFileList().add(jarPath);
                   }
               }
            }

            jarFile.close();
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","qjqj\t" + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

}
