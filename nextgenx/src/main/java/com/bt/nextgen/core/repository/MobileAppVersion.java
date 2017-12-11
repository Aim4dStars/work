package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MOBILE_APP_VERSION")
public class MobileAppVersion {

    @Id
    @Column(name = "PLATFORM")
    private String platform;

    @Column(name = "VERSION")
    private String version;

    public MobileAppVersion() {
    }

    public MobileAppVersion(String platform, String version) {
        this.platform = platform;
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
