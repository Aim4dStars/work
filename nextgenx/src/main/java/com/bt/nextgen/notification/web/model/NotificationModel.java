package com.bt.nextgen.notification.web.model;


public class NotificationModel implements NotificationModelInterface {

	private String notificationResponse;
    private String msgId;
    private String msgCtxId;
    private String msgCtxType;

	public String getNotificationResponse() {
		return notificationResponse;
	}

	public void setNotificationResponse(String notificationResponse) {
		this.notificationResponse = notificationResponse;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgCtxId() {
		return msgCtxId;
	}

	public void setMsgCtxId(String msgCtxId) {
		this.msgCtxId = msgCtxId;
	}

	public String getMsgCtxType() {
		return msgCtxType;
	}

	public void setMsgCtxType(String msgCtxType) {
		this.msgCtxType = msgCtxType;
	}
	
}
