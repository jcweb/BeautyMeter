package cn.yaman;

/**
 * @author timpkins
 */
public class UserModel {
    private String phone;
    private String passwd;

    public UserModel(String phone, String passwd) {
        this.phone = phone;
        this.passwd = passwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return phone + '\'' + passwd + '\'';
    }
}
