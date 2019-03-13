package cn.yaman.entity;

import java.io.Serializable;

/**
 * @author timpkins
 */
public class SchemeEntity implements Serializable {

    /**
     * id : 2
     * name : 极致导入
     * explain : 通过保湿与冷敷共同作用，活性化面部组织，导入护肤液中的营养元素
     * duration : 6
     * imageUrl : http://210.22.8.76:9068/facemaster-app-dev/re/步骤提拉瘦脸-2.png
     * prepareIurl : null
     * prepareName : null
     * prepareExplain : null
     * tip : null
     * createTime : 1548645587000
     * updateTime : 1550906692000
     */

    private int id;
    private String name;
    private String explain;
    private String duration;
    private String imageUrl;
    private Object prepareIurl;
    private Object prepareName;
    private Object prepareExplain;
    private Object tip;
    private long createTime;
    private long updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getPrepareIurl() {
        return prepareIurl;
    }

    public void setPrepareIurl(Object prepareIurl) {
        this.prepareIurl = prepareIurl;
    }

    public Object getPrepareName() {
        return prepareName;
    }

    public void setPrepareName(Object prepareName) {
        this.prepareName = prepareName;
    }

    public Object getPrepareExplain() {
        return prepareExplain;
    }

    public void setPrepareExplain(Object prepareExplain) {
        this.prepareExplain = prepareExplain;
    }

    public Object getTip() {
        return tip;
    }

    public void setTip(Object tip) {
        this.tip = tip;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
