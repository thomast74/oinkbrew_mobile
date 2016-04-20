package info.vhowto.oinkbrewmobile.domain;

import java.util.Date;

public class Configuration {

    private String name;
    private Date createDate;

    public Configuration() {
    }

    public Configuration(String name, Date createDate) {
        this.name = name;
        this.createDate = createDate;
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
}
