package entity;

/**
 * @author fate
 * @date 2019-11-12 下午6:30
 * 依赖信息实体
 */
public class DependencyInfo {

    private String version;

    private String groupId;

    private String artifactId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
}
