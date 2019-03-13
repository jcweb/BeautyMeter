package cn.yaman.entity;

import java.util.List;

/**
 * @author timpkins
 */
public class NurseRequestEntity {
    private int userId;
    private int schemeId;
    private int deviceId;
    private List<DetailList> detailList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(int schemeId) {
        this.schemeId = schemeId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public List<DetailList> getList() {
        return detailList;
    }

    public void setList(List<DetailList> list) {
        this.detailList = list;
    }

    public class DetailList {
        private String operationId;
        private int second;

        public String getOperationId() {
            return operationId;
        }

        public void setOperationId(String operationId) {
            this.operationId = operationId;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }
}
