package cn.yaman.entity;

import java.io.Serializable;

/**
 * @author timpkins
 */
public class SelectDeviceEntity implements Serializable {

    /**
     * id : 1
     * model : A1
     * color : 红色
     * name : null
     * macAdress : 00:50:56:C0:00:01
     * type : 2
     * simageUrl : http://210.22.8.76:9068/facemaster-app-dev/re/产品图01a.png
     */

    private int id;
    private String model;
    private String color;
    private String name;
    private String macAdress;
    private int type;
    private String simageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAdress() {
        return macAdress;
    }

    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
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
}
