package com.bt.nextgen.addressbook;

import java.math.BigDecimal;

import com.bt.nextgen.core.domain.BaseObject;
import com.bt.nextgen.payments.domain.PayeeType;

public class PayeeModel extends BaseObject implements Comparable<PayeeModel>
{
	private String id;
	private String name;
	private String nickname;
	private PayeeType payeeType;
	private String code;
	private String reference;
	private String description;
	private String crnType;
	private boolean saveToList;
	private String displayName;
	private boolean primary;
	private BigDecimal limit;
	private String page;

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public PayeeModel()
	{
	}

	public PayeeModel(String id, String name, PayeeType payeeType, String code, String reference)
	{
		this.id = id;
		this.name = name;
		this.payeeType = payeeType;
		this.code = code;
		this.reference = reference;
	}

	public boolean isSaveToList()
	{
		return saveToList;
	}

	public void setSaveToList(boolean saveToList)
	{
		this.saveToList = saveToList;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public PayeeType getPayeeType()
	{
		return payeeType;
	}

	public void setPayeeType(PayeeType payeeType)
	{
		this.payeeType = payeeType;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}

	@Override
	public int compareTo(PayeeModel other)

	{
		if (payeeType.equals(PayeeType.PRIMARY_LINKED) && !other.getPayeeType().equals(PayeeType.PRIMARY_LINKED))
		{
			return -1;
		}
		else if (!payeeType.equals(PayeeType.PRIMARY_LINKED) && other.getPayeeType().equals(PayeeType.PRIMARY_LINKED))
		{
			return 1;
		}else
		{
			return displayName.compareToIgnoreCase(other.displayName);
		}
	}

	public String getCrnType()
	{
		return crnType;
	}

	public void setCrnType(String crnType)
	{
		this.crnType = crnType;
	}

	public String getPage() 
	{
		return page;
	}

	public void setPage(String page) 
	{
		this.page = page;
	}
}
