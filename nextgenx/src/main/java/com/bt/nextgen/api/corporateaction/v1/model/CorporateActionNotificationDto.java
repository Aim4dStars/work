package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigInteger;
import java.util.List;


public class CorporateActionNotificationDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
	private CorporateActionSendNotificationStatus status;
	private List<CorporateActionAttachment> attachments;
	private BigInteger notificationCount;

	public CorporateActionNotificationDto() {
		super();
	}

	public CorporateActionNotificationDto(CorporateActionSendNotificationStatus status,
										  List<CorporateActionAttachment> attachments) {
		this.status = status;
		this.attachments = attachments;
	}

	public CorporateActionSendNotificationStatus getStatus() {
		return status;
	}

	public List<CorporateActionAttachment> getAttachments() {
		return attachments;
	}

	public BigInteger getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(BigInteger notificationCount) {
		this.notificationCount = notificationCount;
	}

	@Override
	public CorporateActionDtoKey getKey() {
		return null;
	}
}
