package cn.yaman.entity;

import java.io.Serializable;

/**
 * @author timpkins
 */
public class SchemeDetailEntity implements Serializable {

    private int id; //id
    private String name;//  步骤名
    private String stepIurl;//步骤图片url
    private String method;//操作方法
    private String duration;//操作时长(单位：秒)
    private String voice;//语音说明
    private String stepVurl;//步骤语音url
    private String remark;//备注说明
    private String prepareExplain;//准备说明
    private String prepareIurl;//准备图片url
    private String prepareVurl;//准备语音url
    private int serial;//模式序号
    private String pattern;//模式
    private String effectUrl;//参考效果图url
    private String script;//脚本说明
    private int isCyclic;//是否循环(1是，0否)
    private String function;//功能简介
    private String imageUrl;//模式图片url
    private String gifUrl;//模式gif图片url
    private long createTime;
    private long updateTime;
    private String part;//按摩部位

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public String getEffectUrl() {
        return effectUrl;
    }

    public void setEffectUrl(String effectUrl) {
        this.effectUrl = effectUrl;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getIsCyclic() {
        return isCyclic;
    }

    public void setIsCyclic(int isCyclic) {
        this.isCyclic = isCyclic;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getPrepareExplain() {
        return prepareExplain;
    }

    public void setPrepareExplain(String prepareExplain) {
        this.prepareExplain = prepareExplain;
    }

    public String getPrepareIurl() {
        return prepareIurl;
    }

    public void setPrepareIurl(String prepareIurl) {
        this.prepareIurl = prepareIurl;
    }

    public String getPrepareVurl() {
        return prepareVurl;
    }

    public void setPrepareVurl(String prepareVurl) {
        this.prepareVurl = prepareVurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStepIurl() {
        return stepIurl;
    }

    public void setStepIurl(String stepIurl) {
        this.stepIurl = stepIurl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getStepVurl() {
        return stepVurl;
    }

    public void setStepVurl(String stepVurl) {
        this.stepVurl = stepVurl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
