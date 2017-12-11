package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.core.web.model.BaseObject;
import com.bt.nextgen.payments.web.validator.BpayBiller;
import com.bt.nextgen.web.validator.ValidationErrorCode;

import javax.validation.constraints.Pattern;

public class BpayPayeeModel extends BaseObject implements PayeeConversation
{
	private static final long serialVersionUID = -6466592690519625808L;

	@Pattern(regexp = "^[0-9]{1,10}$", message = ValidationErrorCode.INVALID_BPAY_BILLER)
	@BpayBiller
	private String billerCode;

	@Pattern(regexp = "^[0-9]{1,20}$", message = ValidationErrorCode.INVALID_CUSTOMER_REFERENCE_NUMBER)
	private String customerReference;

	@Pattern(regexp = "^[0-9A-Za-z]+[0-9A-Za-z ]{1,29}$",
		message = ValidationErrorCode.INVALID_CUSTOMER_REFERENCE_NUMBER)
	private String nickname;

	private boolean saveToBillersList;
	private String id;

	private String paymentCategory;

	private String paymentSubCategory;

	public BpayPayeeModel(String billerCode, String customerReference, String nickname, Boolean saveToList)
	{
		this.billerCode = billerCode;
		this.nickname = nickname;
		this.customerReference = customerReference;
		this.saveToBillersList = saveToList;
	}

	public BpayPayeeModel()
	{
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getCustomerReference()
	{
		return customerReference;
	}

	public void setCustomerReference(String customerReference)
	{
		this.customerReference = customerReference;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public boolean isSaveToBillersList()
	{
		return saveToBillersList;
	}

	public void setSaveToBillersList(boolean saveToBillersList)
	{
		this.saveToBillersList = saveToBillersList;
	}

	@Override
	public boolean isSaveToList()
	{
		return saveToBillersList;
	}

	public String getPaymentCategory()
	{
		return paymentCategory;
	}

	public void setPaymentCategory(String paymentCategory)
	{
		this.paymentCategory = paymentCategory;
	}

	public String getPaymentSubCategory()
	{
		return paymentSubCategory;
	}

	public void setPaymentSubCategory(String paymentSubCategory)
	{
		this.paymentSubCategory = paymentSubCategory;
	}

}
