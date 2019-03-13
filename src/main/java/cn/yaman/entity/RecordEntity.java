package cn.yaman.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author timpkins
 */
public class RecordEntity implements Serializable {

    /**
     * id : 9
     * schemeId : 2
     * userId : 1
     * deviceId : 2
     * happenTime : 1551166033000
     * second : 144
     * atp : null
     * collagen : null
     * elastin : null
     * microcirculation : null
     * moisture : null
     * divisor : null
     * repair : null
     * vitality : null
     * burning : null
     * createTime : 1551166032000
     * updateTime : 1551166032000
     * imageUrl : http://210.22.8.76:9068/facemaster-app-dev/re/buzhoutilashoulian2.png
     * sname : 极致导入
     * ntime : null
     * detailList : null
     */

    private int id;
    private int schemeId;
    private int userId;
    private int deviceId;
    private long happenTime;
    private int second;
    private String atp;
    private String collagen;
    private String elastin;
    private String microcirculation;
    private String moisture;
    private String divisor;
    private String repair;
    private String vitality;
    private String burning;
    private long createTime;
    private long updateTime;
    private String imageUrl;
    private String sname;
    private Object ntime;
    private List<DetailList> detailList;

    public class DetailList {
        private String id;
        private String second;
        private String name;
        private String imageUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(int schemeId) {
        this.schemeId = schemeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public long getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(long happenTime) {
        this.happenTime = happenTime;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public Object getAtp() {
        return atp;
    }

    public void setAtp(String atp) {
        this.atp = atp;
    }

    public Object getCollagen() {
        return collagen;
    }

    public void setCollagen(String collagen) {
        this.collagen = collagen;
    }

    public Object getElastin() {
        return elastin;
    }

    public void setElastin(String elastin) {
        this.elastin = elastin;
    }

    public Object getMicrocirculation() {
        return microcirculation;
    }

    public void setMicrocirculation(String microcirculation) {
        this.microcirculation = microcirculation;
    }

    public Object getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public Object getDivisor() {
        return divisor;
    }

    public void setDivisor(String divisor) {
        this.divisor = divisor;
    }

    public Object getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public Object getVitality() {
        return vitality;
    }

    public void setVitality(String vitality) {
        this.vitality = vitality;
    }

    public Object getBurning() {
        return burning;
    }

    public void setBurning(String burning) {
        this.burning = burning;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Object getNtime() {
        return ntime;
    }

    public void setNtime(Object ntime) {
        this.ntime = ntime;
    }

    public List<DetailList> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailList> detailList) {
        this.detailList = detailList;
    }
}
