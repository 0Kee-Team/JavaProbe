package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储运行用户的信息
 */
public class RunUserInfo {

    private String userName; // 用户名称

    private List<String> userPid = new ArrayList<String>(); // 正在运行的java应用pid

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getUserPid() {
        return userPid;
    }

    public void setUserPid(List<String> userPid) {
        this.userPid = userPid;
    }
}
