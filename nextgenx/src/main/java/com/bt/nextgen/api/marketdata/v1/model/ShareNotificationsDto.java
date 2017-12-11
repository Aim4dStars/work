package com.bt.nextgen.api.marketdata.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement(name = "ShareNotification")
public class ShareNotificationsDto extends BaseDto implements KeyedDto<String> {

    private String key;
    private List<String> consistentlyEncryptedClientKeys;
    private List<String> consistentlyEncryptedAccountKeys;
    private String status;

    private String type;
    private String url;
    private String urlText;
    private String personalizedMessage;

    public String getStatus() {
        return status;
    }

    @XmlTransient
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getKey() {
        return key;
    }

    @XmlTransient
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlText() {
        return urlText;
    }

    public void setUrlText(String urlText) {
        this.urlText = urlText;
    }

    public List<String> getConsistentlyEncryptedClientKeys() {
        return consistentlyEncryptedClientKeys;
    }

    @XmlTransient
    public void setConsistentlyEncryptedClientKeys(List<String> consistentlyEncryptedClientKeys) {
        this.consistentlyEncryptedClientKeys = consistentlyEncryptedClientKeys;
    }

    public List<String> getConsistentlyEncryptedAccountKeys() {
        return consistentlyEncryptedAccountKeys;
    }

    @XmlTransient
    public void setConsistentlyEncryptedAccountKeys(List<String> consistentlyEncryptedAccountKeys) {
        this.consistentlyEncryptedAccountKeys = consistentlyEncryptedAccountKeys;
    }

    public String getPersonalizedMessage() {
        return personalizedMessage;
    }

    public void setPersonalizedMessage(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }
}
