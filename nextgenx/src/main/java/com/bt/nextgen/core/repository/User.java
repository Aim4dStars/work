package com.bt.nextgen.core.repository;

import org.apache.commons.lang.WordUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "USERS")
@SuppressWarnings({"squid:S1068"})
public class User implements Serializable
{
    @Id @Column(name = "USER_ID")
    private String username;

    @Column(name = "ADVISER_ID")
    private String id;

    @Column(name = "ISFIRSTTIME_LOGGEDIN") @Type(type = "yes_no")
    private boolean firstTimeLoggedIn;

    @Column(name = "TNC_ACCEPTED") @Type(type = "yes_no")
    private boolean tncAccepted;

    @Column(name = "TNC_ACCEPTED_ON")
    private Date tncAcceptedOn;

    @Column(name = "WHATS_NEW_VERSION")
    private String whatsNewVersion;

    public User()
    {
    }

    public User(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFormattedUsername()
    {
        return WordUtils.capitalize(username);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String Id)
    {
        this.id = Id;
    }

    public boolean isFirstTimeLoggedIn()
    {
        return this.firstTimeLoggedIn;
    }

    public void setFirstTimeLoggedIn(final boolean firstTimeLoggedIn)
    {
        this.firstTimeLoggedIn = firstTimeLoggedIn;
    }

/*    public boolean isTncAccepted()
    {
        return tncAccepted;
    }*/

    public void setTncAccepted(boolean tncAccepted)
    {
        this.tncAccepted = tncAccepted;
    }

/*    public Date getTncAcceptedOn()
    {
        return tncAcceptedOn;
    }*/

    public void setTncAcceptedOn(Date tncAcceptedOn)
    {
        this.tncAcceptedOn = tncAcceptedOn;
    }

    public String getWhatsNewVersion()
    {
        return whatsNewVersion;
    }

    public void setWhatsNewVersion(String whatsNewVersion)
    {
        this.whatsNewVersion = whatsNewVersion;
    }
}
