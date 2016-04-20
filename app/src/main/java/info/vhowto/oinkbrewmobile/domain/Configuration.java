package info.vhowto.oinkbrewmobile.domain;

import java.util.Date;

public class Configuration {

    private String name;
    private Date createDate;
    private ConfigirationType type;

    public Configuration() {
    }

    public Configuration(String name, Date createDate, ConfigirationType type) {
        this.name = name;
        this.createDate = createDate;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ConfigirationType getType() { return this.type; }

    public void setType(ConfigirationType type) { this.type = type; }
}
