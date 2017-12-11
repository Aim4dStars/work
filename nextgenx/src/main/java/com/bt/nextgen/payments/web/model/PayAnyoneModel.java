package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.core.web.model.BaseObject;
import com.bt.nextgen.payments.web.validator.Bsb;
import com.bt.nextgen.web.validator.ValidationErrorCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PayAnyoneModel extends BaseObject implements PayeeConversation
{
	private static final long serialVersionUID = -7116965555844094516L;

	@Size(max = 32) @Pattern(regexp = "^[a-zA-Z0-9]+[a-zA-Z0-9 ]+$", message = ValidationErrorCode.INVALID_PAYEE_NAME)
	@NotNull
	private String payeeName;

	@Pattern(regexp = "^[0-9]{3}[- ]?[0-9]{3}$", message = ValidationErrorCode.INVALID_BSB) @NotNull @Bsb
	private String bsb;

	@Pattern(regexp = "^[0-9]{1,9}$", message = ValidationErrorCode.INVALID_ACCOUNT_NUMBER) @NotNull
	private String accountNumber;

	@Size(max = 30)
	private String nickname;

	private String paymentCategory;

	private boolean saveToPayeesList;

	private String id;

	private String paymentSubCategory;

	public PayAnyoneModel()
	{
	}

	public String getPayeeName()
	{
		return payeeName;
	}

	public void setPayeeName(String payeeName)
	{
		this.payeeName = payeeName;
	}

	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getPaymentCategory()
	{
		return paymentCategory;
	}

	public void setPaymentCategory(String paymentCategory)
	{
		this.paymentCategory = paymentCategory;
	}

	public boolean isSaveToPayeesList()
	{
		return saveToPayeesList;
	}

	public void setSaveToPayeesList(boolean saveToPayeesList)
	{
		this.saveToPayeesList = saveToPayeesList;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public boolean isSaveToList()
	{
		return saveToPayeesList;
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
