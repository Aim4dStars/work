package com.bt.nextgen.tasks.constants;

public enum TaskCategory
{
	CHANGE_REQUESTED("Change Requested"),
	WAITING_APPROVAL_REQUEST("Waiting Approval"),
	IN_PROGRESS_REQUEST("In Progress"),
	ACTIVE("Active"),
	APPROVED("Approved"),
	REJECTED_BY_INVESTOR("Rejected by Investor"),
	PENDING_ACCEPTANCE("Pending Acceptance"),
	DONE("Done");

	String name;

	TaskCategory(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
}
