package com.bt.nextgen.api.notification.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

public class NotificationDto extends BaseDto
{
	private String id;
	private DateTime date;
	private String category;
	private String categoryCode;
	private String accountName;
	private String accountType;
	private String accountId;
	private String accountNumber;
	private String productName;
	private String adviserName;
	private String details;
	private boolean highPriority;
	private boolean unread;
	private boolean adviserDshBd;
	private String subCategory;
	private String documentUrl;

	private String url;
	private String urlText;
	private String messageType;
	private String personalizedMessage;
	

	public DateTime getDate()
	{
		return date;
	}

	public void setDate(DateTime dateTime)
	{
		this.date = dateTime;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getDetails()
	{
		return details;
	}

	public void setDetails(String details)
	{
		this.details = details;
	}

	public boolean isHighPriority()
	{
		return highPriority;
	}

	public void setHighPriority(boolean highPriority)
	{
		this.highPriority = highPriority;
	}

	public boolean isUnread()
	{
		return unread;
	}

	public void setUnread(boolean unread)
	{
		this.unread = unread;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getCategoryCode()
	{
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode)
	{
		this.categoryCode = categoryCode;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getSubCategory()
	{
		return subCategory;
	}

	public void setSubCategory(String subCategory)
	{
		this.subCategory = subCategory;
	}

	public boolean isAdviserDshBd()
	{
		return adviserDshBd;
	}

	public void setAdviserDshBd(boolean adviserDshBd)
	{
		this.adviserDshBd = adviserDshBd;
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

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getPersonalizedMessage() {
		return personalizedMessage;
	}

	public void setPersonalizedMessage(String personalizedMessage) {
		this.personalizedMessage = personalizedMessage;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
}
