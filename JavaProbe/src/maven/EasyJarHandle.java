package maven;

import common.CommonUtil;
import entity.DependencyInfo;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author fate
 * @date 2019-11-22 下午12:05
 */
public class EasyJarHandle {

    /**
     * 获取依赖信息
     * @param jarpath jar文件路径
     * @param dependencyInfoList 存放依赖包数据的list
     * @return
     */
    public static List<DependencyInfo> getDependencyInfo(String jarpath, List<DependencyInfo> dependencyInfoList) {

        try {

            File jarDict = new File(jarpath.replace("file:","").replace("WEB-INF/classes/", "WEB-INF/lib/"));

            for (File file : jarDict.listFiles()) {

                if (file.isFile() && file.getName().endsWith(".jar")) {

                    JarFile jarFile = new JarFile(file);

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

                            dependencyInfoList.add(dependencyInfo);
                        }
                    }
                }
            }
        }
        catch (Exception e) {

            CommonUtil.writeStr("/tmp/jvm_error.txt","getDependencyInfo_byeasy:\t" + e.getMessage());
        }

        return dependencyInfoList;
    }

}
