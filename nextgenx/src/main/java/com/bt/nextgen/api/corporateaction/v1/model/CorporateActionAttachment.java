package com.bt.nextgen.api.corporateaction.v1.model;


public class CorporateActionAttachment {
	private String id;
	private String name;
	private long size;
	private CorporateActionAttachmentStatus status;

	public CorporateActionAttachment(String id, String name, long size,
									 CorporateActionAttachmentStatus status) {
		this.id = id;
		this.name = name;
		this.size = size;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public CorporateActionAttachmentStatus getStatus() {
		return status;
	}
}
