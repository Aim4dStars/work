package com.bt.nextgen.service.avaloq;

import org.joda.time.DateTime;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.order.OrderIdentifierImpl;
import com.btfin.panorama.core.security.avaloq.userinformation.ClientIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIdentifier;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.btfin.panorama.core.security.integration.messages.Notification;
import com.btfin.panorama.core.security.integration.messages.NotificationCategory;
import com.bt.nextgen.service.integration.messages.NotificationEventConverter;
import com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.messages.NotificationSubCategory;
import com.btfin.panorama.core.security.integration.order.OrderIdentifier;
import com.bt.nextgen.service.integration.userinformation.ClientIdentifier;

/**
 * @author L070354
 * 
 * Notification Messages -  xpath mapping to load notifications from the avaloq response
 *
 */
@ServiceBean(xpath = "ntfcn_head")
public class NotificationImpl implements Notification
{

	public static final String PATH_NTFCN_MSG_ID = "ntfcn_id/val";
	public static final String PATH_NTFCN_TIMESTAMP = "timestamp/val";
	public static final String PATH_NTFCN_VALIDTO = "valid_to/val";
	public static final String PATH_NTFCN_RESP_USERID = "resp_sec_user_id/val";
	public static final String PATH_NTFCN_RECPID = "recp_sec_user_id/val";
	public static final String PATH_NTFCN_EVENTTYPE = "event_type_id/val";
	public static final String PATH_NTFCN_PRIORITY = "event_type_prio/val";
	public static final String PATH_NTFCN_STATUS = "ntfcn_status_id/val";
	public static final String PATH_NTFCN_TRIGOBJ = "trig_obj_id/val";
	public static final String PATH_NTFCN_ORDERID = "ctx_doc_id/val";
	public static final String PATH_NTFCN_MSG = "msg_text/val";
	public static final String PATH_NTFCN_CAT = "msg_cat_id/val";
	public static final String PATH_NTFCN_SUB_CAT = "msg_subcat_id/val";
	public static final String PATH_NTFCN_DOCM_URL = "docm_url/val";
	public static final String PATH_NTFCN_IS_ADVR_DSHBD = "is_advsr_dshbrd/val";
    // This is done because there is a discrepancy between the values of this field in
    // `btfg$ui_ntfcn_list.user#unread_cnt` and `btfg$ui_ntfcn_list.user#ntfcn`
	public static final String PATH_NTFCN_MSGTYPE = "if(msg_avsr_ctr_id/val!='1') then true() else false()";
	public static final String PATH_NTFCN_BP_ID = "bp_id/val";
	public static final String PATH_NTFCN_OWNER_TYPE_ID = "owner_type_id/val";
	public static final String PATH_NTFCN_MACC_NR = "macc_nr/val";
	public static final String PATH_NTFCN_PERSON_ID = "person_id/val";

	@ServiceElement(xpath = PATH_NTFCN_MSG_ID)
	String notificationId;

	@ServiceElement(xpath = PATH_NTFCN_TIMESTAMP, converter = DateTimeTypeConverter.class)
	DateTime notificationTimeStamp;

	@ServiceElement(xpath = PATH_NTFCN_VALIDTO, converter = DateTimeTypeConverter.class)
	DateTime notificationValidUntil;

	@ServiceElement(xpath = PATH_NTFCN_RESP_USERID)
	int responsibleUserId;

	@ServiceElement(xpath = PATH_NTFCN_RECPID)
	int recipientId;

	@ServiceElement(xpath = PATH_NTFCN_EVENTTYPE, converter = NotificationEventConverter.class)
	String eventName;

	@ServiceElement(xpath = PATH_NTFCN_PRIORITY)
	int eventPriority;

	@ServiceElement(xpath = PATH_NTFCN_STATUS, converter = NotificationStatusConverter.class)
	NotificationStatus status;

	@ServiceElement(xpath = PATH_NTFCN_TRIGOBJ)
	int triggeringObject;

	@ServiceElement(xpath = PATH_NTFCN_ORDERID)
	String orderId;

	@ServiceElement(xpath = PATH_NTFCN_MSG)
	String notificationMessage;

	@ServiceElement(xpath = PATH_NTFCN_CAT, converter = NotificationCategoryConverter.class)
	NotificationCategory notificationCategoryId;

	@ServiceElement(xpath = PATH_NTFCN_SUB_CAT, converter = NotificationSubCategoryConverter.class)
	NotificationSubCategory notificationSubCategoryId;

	@ServiceElement(xpath = PATH_NTFCN_DOCM_URL)
	private String documentUrl;

	@ServiceElement(xpath = PATH_NTFCN_IS_ADVR_DSHBD)
	boolean isAdviserDshBd;

	@ServiceElement(xpath = PATH_NTFCN_MSGTYPE)
	boolean isMyMessage;

	@ServiceElement(xpath = PATH_NTFCN_BP_ID)
	String accountIdentifier;

	@ServiceElement(xpath = PATH_NTFCN_OWNER_TYPE_ID, converter = NotificationOwnerAccountTypeConverter.class)
	NotificationOwnerAccountType ownerAccountType;

	@ServiceElement(xpath = PATH_NTFCN_MACC_NR)
	int moneyAccountNumber;

	@ServiceElement(xpath = PATH_NTFCN_PERSON_ID)
	String personId;

	private String type;
	private String url;
	private String urlText;
	private String personalizedMessage;

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrlText() {
		return urlText;
	}

	public void setUrlText(String urlText) {
		this.urlText = urlText;
	}

	@Override
	public String getPersonalizedMessage() {
		return personalizedMessage;
	}

	public void setPersonalizedMessage(String personalizedMessage) {
		this.personalizedMessage = personalizedMessage;
	}

	@Override
	public String getNotificationId()
	{
		return notificationId;
	}

	@Override
	public void setNotificationId(String id)
	{
		this.notificationId = id;

	}

	@Override
	public AccountIdentifier getAccount()
	{
		AccountIdentifier identifer = new WrapAccountDetailImpl();
		AccountKey key = AccountKey.valueOf(accountIdentifier);
		identifer.setAccountKey(key);

		return identifer;
	}

	@Override
	public ClientIdentifier getPerson()
	{
		ClientIdentifier clientIdentifier = new ClientIdentifierImpl();
		clientIdentifier.setClientKey(ClientKey.valueOf(this.personId));
		return clientIdentifier;
	}

	@Override
	public DateTime getNotificationTimeStamp()
	{
		return notificationTimeStamp;
	}

	@Override
	public DateTime getNotificationValidUntil()
	{
		return notificationValidUntil;
	}

	@Override
	public int getResponsibleUserId()
	{
		return responsibleUserId;
	}

	@Override
	public Integer getRecipientId()
	{
		return recipientId;
	}

	@Override
	public String getEventName()
	{
		return eventName;
	}

	@Override
	public int getEventPriority()
	{
		return eventPriority;
	}

	@Override
	public NotificationStatus getNotificationStatus()
	{
		return status;
	}

	@Override
	public int getTriggeringObject()
	{
		return triggeringObject;
	}

	@Override
	public OrderIdentifier getOrder()
	{
		OrderIdentifierImpl order = new OrderIdentifierImpl();
		order.setOrderId(orderId);
		return order;
	}

	public String getOrderId()
	{
		return orderId;
	}

	@Override
	public String getNotificationMessage()
	{
		return notificationMessage;
	}

	@Override
	public NotificationCategory getNotificationCategoryId()
	{
		return notificationCategoryId;
	}

	@Override
	public int getMoneyAccountNumber()
	{
		return moneyAccountNumber;
	}

	@Override
	public void setBpId(String accountIdentifier)
	{
		this.accountIdentifier = accountIdentifier;
	}

	public NotificationOwnerAccountType getOwnerAccountType()
	{
		return ownerAccountType;
	}

	public void setOwnerAccountType(NotificationOwnerAccountType ownerAccountType)
	{
		this.ownerAccountType = ownerAccountType;
	}

	@Override
	public boolean isMyMessage()
	{
		return isMyMessage;
	}

	public NotificationStatus getStatus()
	{
		return status;
	}

	public void setStatus(NotificationStatus status)
	{
		this.status = status;
	}

	public void setNotificationTimeStamp(DateTime notificationTimeStamp)
	{
		this.notificationTimeStamp = notificationTimeStamp;
	}

	public void setNotificationValidUntil(DateTime notificationValidUntil)
	{
		this.notificationValidUntil = notificationValidUntil;
	}

	public void setResponsibleUserId(int responsibleUserId)
	{
		this.responsibleUserId = responsibleUserId;
	}

	public void setRecipientId(int recipientId)
	{
		this.recipientId = recipientId;
	}

	public void setEventName(String eventName)
	{
		this.eventName = eventName;
	}

	public void setEventPriority(int eventPriority)
	{
		this.eventPriority = eventPriority;
	}

	public void setTriggeringObject(int triggeringObject)
	{
		this.triggeringObject = triggeringObject;
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

	public void setNotificationMessage(String notificationMessage)
	{
		this.notificationMessage = notificationMessage;
	}

	public void setNotificationCategoryId(NotificationCategory notificationCategoryId)
	{
		this.notificationCategoryId = notificationCategoryId;
	}

	public void setMyMessage(boolean isMyMessage)
	{
		this.isMyMessage = isMyMessage;
	}

	public void setMoneyAccountNumber(int moneyAccountNumber)
	{
		this.moneyAccountNumber = moneyAccountNumber;
	}

	@Override
	public NotificationSubCategory getNotificationSubCategoryId()
	{
		return notificationSubCategoryId;
	}

	@Override
	public boolean isAdviserDashBoadFlag()
	{
		return isAdviserDshBd;
	}

	@Override
	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
}
