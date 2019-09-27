package runuser;

import common.CommonUtil;
import entity.RunUserInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用于多用户的处理
 */
public class RunHandle {

    /**
     * 获取运行的结果 封装了处理多路径的情况
     * @return
     */
    public static List<RunUserInfo> getUser() {

        // 实测发现有的linux版本获取的临时目录不会加/
        String usetmp = System.getProperty("java.io.tmpdir").endsWith("/") ? System.getProperty("java.io.tmpdir") : System.getProperty("java.io.tmpdir")+"/";

        List<RunUserInfo> runUserList = new ArrayList<RunUserInfo>();

        if (!"/tmp/".equals(usetmp)) {

            getRunUserlist(usetmp,runUserList);
        }

        getRunUserlist("/tmp/",runUserList);

        return runUserList;
    }

    /**
     * jvm通常都是多权限运行的,为了解决权限不同的隔离问题，按照linux下面大部分的特性，在/tmp/目录寻找运行过jvm的用户
     * 可能会有偶尔的状况更改了tmp目录，在此不做操作默认为/tmp/(主要是每个用户的tmp可能 不好获取，但是基本没人改这个)
     * 2019年08月27日19:20:25 打脸了，我的mac就不在这个目录，所以改成了原始执行用户的use目录 + /tmp/ (方便兼容)
     */
    public static void getRunUserlist(String path, List<RunUserInfo> runUserList) {

        try {

            File jvmTmpDir = new File(path);

            for (File tmpfile :  jvmTmpDir.listFiles()) {

                if (tmpfile.getName().startsWith("hsperfdata_")) {

                    RunUserInfo runUserInfo = new RunUserInfo();
                    runUserInfo.setUserName(tmpfile.getName().replace("hsperfdata_",""));

                    for (File usertmp :  tmpfile.listFiles()) {

                        runUserInfo.getUserPid().add(usertmp.getName());
                    }

                    runUserList.add(runUserInfo);
                }
            }
        }
        catch (Exception e) {
            CommonUtil.writeStr("/tmp/jvm_error.txt","zzz\t" + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * 垃圾的监测 windwos没测 直接干死
     */
    public static void checkOS() {

        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {

            System.out.println("不支持Windows系统!");
            System.exit(0);
        }
    }

    /**
     * 执行分用户hook  其实应该做一个runuser的监测 但是我懒，并且我司内网主机架构比较统一基本都安装了
     * @param user
     */
    public static void runHookCommand(String user) {

        try {

            String[] commd = { "runuser", "-c", "java -jar JavaProbe.jar " + user, user};

            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(commd);

            InputStreamReader isr = new InputStreamReader(proc.getErrorStream());

            BufferedReader br = new BufferedReader(isr);

            String line = null;

            System.out.println("RunUser:\t" + user);

            while ((line = br.readLine()) != null) {

                System.out.println(line);
            }

            br.close();
            isr.close();
        }
        catch (Exception e) {
            CommonUtil.writeStr("/tmp/jvm_error.txt","fff\t" + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

}