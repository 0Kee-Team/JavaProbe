package newagent;

import common.CommonUtil;
import com.google.gson.Gson;
import entity.JvmInfo;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HookMain {

    /**
     * 加载的框架
     * @param args
     * @param inst
     */
    public static void agentmain(String args, Instrumentation inst) {

        JvmInfo jvmInfo = new JvmInfo();

        jvmInfo.setJvmName(args);

        jvmInfoinit(jvmInfo);

        getAllLoadedClass(jvmInfo,inst);

        getJarFileList(jvmInfo);

        //CommonUtil.deleteRepeat("/tmp/jvminfo_" + jvmInfo.getUserName().trim() + ".txt"); // 存在历史文件的话 进行删除

        CommonUtil.writeStr("/tmp/jvminfo_" + jvmInfo.getUserName().trim() + ".txt",new Gson().toJson(jvmInfo).toString()+"\n");
    }

    /**
     * 获取引入的jar文件
     * @param jvmInfo
     */
    public static void getJarFileList(JvmInfo jvmInfo) {

        try {

            String[] classjar = jvmInfo.getClassPath().split(":");

            // 对classpath中的jar的一些处理
            for (String jartmp : classjar) {

                if (!jartmp.endsWith(".jar")) {

                    continue;
                }

                jvmInfo.getJarFileList().add(jartmp);
            }

            // 处理loader中class拉到的路径
            for (String jardirtmp : jvmInfo.getJarPathMap().keySet()) {

                jardirtmp = jardirtmp.replace("file:","");

                if (jardirtmp.toString().endsWith("WEB-INF/classes/")) {

                    jardirtmp = jardirtmp.replace("WEB-INF/classes/", "WEB-INF/lib/");
                }

                File jartmpdir = new File(jardirtmp);

                if (!jartmpdir.exists() || !jartmpdir.isDirectory()) {

                    continue;
                }

               for (File jarrtmpfile : jartmpdir.listFiles()) {

                   if (!jarrtmpfile.getName().endsWith(".jar")) {

                       continue;
                   }

                   jvmInfo.getJarFileList().add(jarrtmpfile.toString());
               }
            }
        }
        catch (Exception e) {

            jvmInfo.getErrorList().add("getJarFileList:\t" + e.toString());
        }

    }
    /**
     *
     * @param jvmInfo
     * @param inst
     * @return
     */
    public static void getAllLoadedClass(JvmInfo jvmInfo, Instrumentation inst) {

        for (Class classz : inst.getAllLoadedClasses()) {

            try {

                jvmInfo.getClassFileList().add(classz.getName());
                jvmInfo.getJarPathMap().put(classz.getResource("/").toString(),"");
            }
            catch (Exception e) {

                jvmInfo.getErrorList().add("getAllLoadedClass:\t" + e.toString());
            }
        }

    }

    /**
     * 获取一些基本的jvm信息 使用System.getProperty避免安全管理器带来的问题
     * @param jvmInfo
     * @return
     */
    public static void jvmInfoinit(JvmInfo jvmInfo) {

        try {

            jvmInfo.setVersion(System.getProperty("java.version"));
            jvmInfo.setJvmHome(System.getProperty("java.home"));
            jvmInfo.setLibraryPath(System.getProperty("java.library.path"));
            jvmInfo.setClassPath(System.getProperty("java.class.path"));
            jvmInfo.setTmpdir(System.getProperty("java.io.tmpdir"));
            jvmInfo.setExtdirs(System.getProperty("java.ext.dirs"));
            jvmInfo.setUserName(System.getProperty("user.name"));
            jvmInfo.setUserDir(System.getProperty("user.dir"));
            jvmInfo.setExceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        }
        catch (Exception e) {

            jvmInfo.getErrorList().add("jvmInfoinit:\t" + e.toString());
        }
    }
}
