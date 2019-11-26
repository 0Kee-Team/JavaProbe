package maven;

import common.CommonUtil;
import entity.DependencyInfo;
import entity.JvmInfo;
import org.springframework.boot.loader.jar.Handler;

import java.util.*;

/**
 * @author fate
 * @date 2019-11-08 上午12:31
 * 从jvm实例中构建pom.xml
 */
public class MavenHandle extends Handler {

    /**
     * 获取jar读取到的依赖 用于针对于应用的漏洞(风险)管理
     * @param jvmInfo
     * @return
     */
    public JvmInfo getMavenResult(JvmInfo jvmInfo) {

        try {

            List<DependencyInfo> dependencyInfos = new ArrayList<DependencyInfo>();

            for(Map.Entry<String, String> entry : jvmInfo.getJarPathMap().entrySet()){

                String targetJar = entry.getKey().trim();

                if (targetJar.endsWith("!/")) {

                    FatJarHandle.getDependencyInfo(targetJar,dependencyInfos);
                    //System.out.println("胖头鱼走起");
                }
                else {

                    EasyJarHandle.getDependencyInfo(targetJar,dependencyInfos);
                    //System.out.println("easyjar 走起");
                }
            }

            jvmInfo.setDependencyInfoList(dependencyInfos);
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","getMavenResult:\t" + e.getMessage());
        }

        return jvmInfo;
    }
}

