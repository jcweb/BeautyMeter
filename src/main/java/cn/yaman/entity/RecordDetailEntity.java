package cn.yaman.entity;

import java.util.List;

/**
 * @author timpkins
 */
public class RecordDetailEntity {
    /**
     * id : 1
     * schemeId : 1
     * userId : 1
     * deviceId : 1
     * happenTime : 1550800308000
     * second : 138
     * atp : 56
     * collagen : 53
     * elastin : 60
     * microcirculation : 40
     * moisture : 66
     * divisor : 88
     * repair : 98
     * vitality : 51
     * burning : 88
     * createTime : 1550800309000
     * updateTime : 1551257404000
     * imageUrl : http://210.22.8.76:9068/facemaster-app-dev/re/buzhoudaorubingsuojinjinghua.png
     * sname : 全面护理
     * ntime : 66
     * detailList : [{"id":21,"recordId":1,"operationId":1,"second":60,"createTime":1551257444000,"updateTime":1551257444000,"name":"瘦脸与提拉","imageUrl":"http://210.22.8.76:9068/facemaster-app-dev/re/buzhoudaorubingsuojinjinghua.png"},{"id":22,"recordId":1,"operationId":2,"second":89,"createTime":1551257454000,"updateTime":1551257454000,"name":"祛皱与修复","imageUrl":"http://210.22.8.76:9068/facemaster-app-dev/re/buzhoudaorubingsuojinjinghua.png"}]
     */

    private int id;
    private int schemeId;
    private int userId;
    private int deviceId;
    private long happenTime;
    private int second;
    private int atp;
    private int collagen;
    private int elastin;
    private int microcirculation;
    private int moisture;
    private int divisor;
    private int repair;
    private int vitality;
    private int burning;
    private long createTime;
    private long updateTime;
    private String imageUrl;
    private String sname;
    private int ntime;
    private List<DetailListBean> detailList;

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

    public int getAtp() {
        return atp;
    }

    public void setAtp(int atp) {
        this.atp = atp;
    }

    public int getCollagen() {
        return collagen;
    }

    public void setCollagen(int collagen) {
        this.collagen = collagen;
    }

    public int getElastin() {
        return elastin;
    }

    public void setElastin(int elastin) {
        this.elastin = elastin;
    }

    public int getMicrocirculation() {
        return microcirculation;
    }

    public void setMicrocirculation(int microcirculation) {
        this.microcirculation = microcirculation;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }

    public int getDivisor() {
        return divisor;
    }

    public void setDivisor(int divisor) {
        this.divisor = divisor;
    }

    public int getRepair() {
        return repair;
    }

    public void setRepair(int repair) {
        this.repair = repair;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public int getBurning() {
        return burning;
    }

    public void setBurning(int burning) {
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

    public int getNtime() {
        return ntime;
    }

    public void setNtime(int ntime) {
        this.ntime = ntime;
    }

    public List<DetailListBean> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailListBean> detailList) {
        this.detailList = detailList;
    }

    public static class DetailListBean {
        /**
         * id : 21
         * recordId : 1
         * operationId : 1
         * second : 60
         * createTime : 1551257444000
         * updateTime : 1551257444000
         * name : 瘦脸与提拉
         * imageUrl : http://210.22.8.76:9068/facemaster-app-dev/re/buzhoudaorubingsuojinjinghua.png
         */

        private int id;
        private int recordId;
        private int operationId;
        private int second;
        private long createTime;
        private long updateTime;
        private String name;
        private String imageUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRecordId() {
            return recordId;
        }

        public void setRecordId(int recordId) {
            this.recordId = recordId;
        }

        public int getOperationId() {
            return operationId;
        }

        public void setOperationId(int operationId) {
            this.operationId = operationId;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
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
}
