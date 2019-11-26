package maven;
import common.CommonUtil;
import entity.DependencyInfo;
import org.springframework.boot.loader.jar.JarFile; // 偷懒 直接使用springboot的
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;

/**
 * @author fate
 * @date 2019-11-22 上午11:38
 * 用于处理fat jar资源的获取
 */
public class FatJarHandle {

    /**
     * fat jar 依赖文件的获取，多用于处理springboot打包的jar 传入的path是这样的 jar:file:/home/q/system/java/live/build/libs/live-33541.a12ed7cc.jar!/BOOT-INF/classes!/
     * @param jarpath
     * @param dependencyInfoList
     * @return
     */
    public static List<DependencyInfo> getDependencyInfo(String jarpath, List<DependencyInfo> dependencyInfoList) {

        try {

            JarFile jarFile = new JarFile(new File(getROOTJar(jarpath)));

            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

            while (jarEntryEnumeration.hasMoreElements()) {

                JarEntry jarEntry = jarEntryEnumeration.nextElement();

                if (jarEntry.getName().endsWith(".jar")) { // 这里就暂时不匹配BOOT-INF/lib，考虑通用性

                    JarFile inJarFile = jarFile.getNestedJarFile(jarEntry);
                    DependencyInfo dependencyInfo = getJarInJardependcyInfo(inJarFile); // 获取资源

                    if (dependencyInfo != null) dependencyInfoList.add(dependencyInfo);

                }
            }

        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","getDependencyInfo:\t" + e.getMessage());
        }

        return dependencyInfoList;
    }

    /**
     * 获取Jarinjar中的资源
     * @param jarFile
     * @return
     */
    public static DependencyInfo getJarInJardependcyInfo(JarFile jarFile) {

        try {

            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();

            while (jarEntryEnumeration.hasMoreElements()) {

                JarEntry jarEntry= jarEntryEnumeration.nextElement();

                if (jarEntry.getName().endsWith("/pom.properties")) {

                    Properties prop = new Properties();
                    prop.load(jarFile.getInputStream(jarEntry));

                    DependencyInfo dependencyInfo = new DependencyInfo(); // 存放依赖信息
                    dependencyInfo.setArtifactId(prop.getProperty("artifactId"));
                    dependencyInfo.setGroupId(prop.getProperty("groupId"));
                    dependencyInfo.setVersion(prop.getProperty("version"));

                    return dependencyInfo;
                }
            }

        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","getJarInJardependcyInfo:\t" + e.getMessage());
        }

        return null;

    }

    /**
     * 获取rootjar资源路径
     * @param jarPath
     * @return
     */
    public static String getROOTJar(String jarPath) {

        jarPath = jarPath.split(".jar!/")[0].replace("jar:file:","");

        return jarPath + ".jar";
    }

}