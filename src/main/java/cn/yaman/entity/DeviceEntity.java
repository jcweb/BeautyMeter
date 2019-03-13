package cn.yaman.entity;

import java.io.Serializable;

/**
 * @author timpkins
 */
public class DeviceEntity implements Serializable {

    /**
     * id : 1
     * name : 美容仪
     * model : A1
     * bname :
     * introduction :
     * type : 2
     * simageUrl : http://localhost/re/edede6d3产品图01a.png
     * dimageUrl : http://localhost/re/1b2ab479产品图01b.jpg
     * createTime : 1550732091000
     * updateTime : 1551149704000
     */

    private int id;
    private String name;
    private String model;
    private String bname;
    private String introduction;
    private int type;
    private String simageUrl;
    private String dimageUrl;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSimageUrl() {
        return simageUrl;
    }

    public void setSimageUrl(String simageUrl) {
        this.simageUrl = simageUrl;
    }

    public String getDimageUrl() {
        return dimageUrl;
    }

    public void setDimageUrl(String dimageUrl) {
        this.dimageUrl = dimageUrl;
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
