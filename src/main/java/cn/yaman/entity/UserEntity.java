package cn.yaman.entity;

import java.io.Serializable;

/**
 * @author timpkins
 * 用户属性
 */
public class UserEntity implements Serializable {

    /**
     * accessToken : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjEzOTIzNDMwOTIwIiwiZXhwIjoxNTQ4OTE5MjAwfQ.0-5mUJtCSIMkDh6mM-TQaesuiBxBqWgWNzV6Gn7VoFU
     * user : {"id":1,"phone":"13923430920","password":"","name":"枫","iconUrl":null,"sex":1,"birthday":"2019-01-16","height":"150","deleteFlag":0,"createTime":1546914942000,"updateTime":1548144265000,"captcha":null,"birthdayLong":null}
     */

    private String accessToken;
    private UserBean user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : 1
         * phone : 13923430920
         * password :
         * name : 枫
         * iconUrl : null
         * sex : 1
         * birthday : 2019-01-16
         * height : 150
         * deleteFlag : 0
         * createTime : 1546914942000
         * updateTime : 1548144265000
         * captcha : null
         * birthdayLong : null
         */

        private int id;
        private String phone;
        private String password;
        private String name;
        private String iconUrl;
        private int sex;
        private String birthday;
        private String height;
        private int deleteFlag;
        private long createTime;
        private long updateTime;
        private Object captcha;
        private Object birthdayLong;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
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

        public Object getCaptcha() {
            return captcha;
        }

        public void setCaptcha(Object captcha) {
            this.captcha = captcha;
        }

        public Object getBirthdayLong() {
            return birthdayLong;
        }

        public void setBirthdayLong(Object birthdayLong) {
            this.birthdayLong = birthdayLong;
        }
    }
}
