package entity;

import java.util.ArrayList;
import java.util.List;

public class ResultInfo {

    private String hostname; // 主机名称

    private List<JvmInfo> jvmInfoList = new ArrayList<JvmInfo>(); // 存放结果

    public List<JvmInfo> getJvmInfoList() {
        return jvmInfoList;
    }

    public void setJvmInfoList(List<JvmInfo> jvmInfoList) {
        this.jvmInfoList = jvmInfoList;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
