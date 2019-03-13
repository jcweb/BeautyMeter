package cn.yaman.entity;

import java.util.List;

/**
 * @author timpkins
 */
public class RecordMonthEntity {

    /**
     * xyOutVoList : null
     * totalTime : null
     * aveTime : 0
     * aveCompleteness : 0
     * scheme : 无
     * effact : 0
     * schemeId : null
     * schemeTotal : null
     * totalSecond : null
     * recordTotal : 0
     * totalDegree : null
     */

    private List<XyOutVoList> xyOutVoList;
    private int totalTime;
    private int aveTime;
    private int aveCompleteness;
    private String scheme;
    private int effact;

    public class XyOutVoList {
        int aveTime;
        String day;

        public int getAveTime() {
            return aveTime;
        }

        public void setAveTime(int aveTime) {
            this.aveTime = aveTime;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }

    public List<XyOutVoList> getXyOutVoList() {
        return xyOutVoList;
    }

    public void setXyOutVoList(List<XyOutVoList> xyOutVoList) {
        this.xyOutVoList = xyOutVoList;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getAveTime() {
        return aveTime;
    }

    public void setAveTime(int aveTime) {
        this.aveTime = aveTime;
    }

    public int getAveCompleteness() {
        return aveCompleteness;
    }

    public void setAveCompleteness(int aveCompleteness) {
        this.aveCompleteness = aveCompleteness;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getEffact() {
        return effact;
    }

    public void setEffact(int effact) {
        this.effact = effact;
    }
}
