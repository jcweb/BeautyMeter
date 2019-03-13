package cn.yaman.entity;

/**
 * @author timpkins
 */
public class CountryEntity {
    private String country;
    private String mobilePrefix;
    private String id;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobilePrefix() {
        return mobilePrefix;
    }

    public void setMobilePrefix(String mobilePrefix) {
        this.mobilePrefix = mobilePrefix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
