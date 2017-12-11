package com.bt.nextgen.service.prm.pojo;

/**
 * Created by L081012-Rishi Gupta on 5/02/2016.
 */
public class MqInputParams {

    /** Action*/
    private String action;
    /** Address*/
    private String address;
    /** Message ID*/
    private String messageID;
    /** User Name*/
    private String userName;
    /** PassWord*/
    private String passWord;
    /** App Name*/
    private String appName;
    /** Provider*/
    private String provider;
    /** Url*/
    private String url;
    /** Destination*/
    private String destination;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getAppName() {return appName;}

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getProvider() {return provider;}

    public void setProvider(String provider) {this.provider = provider;}

    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public String getDestination() {return destination;}

    public void setDestination(String destination) {this.destination = destination;}

}


