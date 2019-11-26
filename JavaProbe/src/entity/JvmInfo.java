package entity;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JvmInfo {

    private String jvmName; // 虚拟机名称

    private String version; // jvm版本

    private String jvmHome; // jvm安装目录

    private String classPath; // classpath

    private String libraryPath; // libraryPath

    private String tmpdir; // 临时目录

    private String extdirs; // 拓展目录

    private String userName; // 当前用户的名称

    private String userDir; // 用户当前的工作目录

    private String exceTime; // 执行时间

    private List<String> jarFileList = new ArrayList<String>();; // jar文件列表

    private List<String> classFileList = new ArrayList<String>();; // 加载的class列表

    private List<String> errorList = new ArrayList<String>(); // 为了方便错误排查 进行的错误收集

    private Map<String,String> jarPathMap = new HashMap<String, String>(); // 存放可能存在jar的路径呀

    private List<DependencyInfo> dependencyInfoList = new ArrayList<DependencyInfo>(); // 存放jar包依赖，用于生成依赖文件，方便对整个应用进行漏洞跟踪

    public List<DependencyInfo> getDependencyInfoList() {
        return dependencyInfoList;
    }

    public void setDependencyInfoList(List<DependencyInfo> dependencyInfoList) {
        this.dependencyInfoList = dependencyInfoList;
    }

    public String getExceTime() {
        return exceTime;
    }

    public void setExceTime(String exceTime) {
        this.exceTime = exceTime;
    }

    public Map<String, String> getJarPathMap() {
        return jarPathMap;
    }

    public void setJarPathMap(Map<String, String> jarPathMap) {
        this.jarPathMap = jarPathMap;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }


    public List<String> getJarFileList() {
        return jarFileList;
    }

    public void setJarFileList(List<String> jarFileList) {
        this.jarFileList = jarFileList;
    }

    public List<String> getClassFileList() {
        return classFileList;
    }

    public void setClassFileList(List<String> classFileList) {
        this.classFileList = classFileList;
    }

    public String getJvmName() {
        return jvmName;
    }

    public void setJvmName(String jvmName) {
        this.jvmName = jvmName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJvmHome() {
        return jvmHome;
    }

    public void setJvmHome(String jvmHome) {
        this.jvmHome = jvmHome;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getLibraryPath() {
        return libraryPath;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    public String getTmpdir() {
        return tmpdir;
    }

    public void setTmpdir(String tmpdir) {
        this.tmpdir = tmpdir;
    }

    public String getExtdirs() {
        return extdirs;
    }

    public void setExtdirs(String extdirs) {
        this.extdirs = extdirs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }
}
