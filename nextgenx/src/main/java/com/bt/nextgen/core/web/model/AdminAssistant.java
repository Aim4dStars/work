package com.bt.nextgen.core.web.model;

import java.util.List;

import com.btfin.panorama.core.security.avaloq.Constants;

public class AdminAssistant extends Person implements Intermediaries
{
	private String advisorId;
	private String dealerGroupName;
	private String dealerGroupId;
	private String officeAndPracticeName;
	private List<Investor> clients;
	
	public String getDealerGroupName() {
		return dealerGroupName;
	}
	public void setDealerGroupName(String dealerGroupName) {
		this.dealerGroupName = dealerGroupName;
	}
	public String getOfficeAndPracticeName() {
		return officeAndPracticeName;
	}
	public void setOfficeAndPracticeName(String officeAndPracticeName) {
		this.officeAndPracticeName = officeAndPracticeName;
	}
	public List<Investor> getClients() {
		return clients;
	}
	public void setClients(List<Investor> clients) {
		this.clients = clients;
	}
	public String getAdvisorId() {
		return advisorId;
	}
	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}
	
	@Override
	public String toString()
	{
		return "Paraplanner [dealerGroupName=" + dealerGroupName + ", officeAndPracticeName=" + officeAndPracticeName + ", clients="
			+ clients + "]";
	}
	@Override
	public String getRole() {
		// TODO Auto-generated method stub
		return Constants.ROLE_ADMIN_ASSISTANT;
	}
	public String getDealerGroupId()
	{
		return dealerGroupId;
	}

    @Override
    public String getCompanyName() {
        return null;
    }

    public void setDealerGroupId(String dealerGroupId)
	{
		this.dealerGroupId = dealerGroupId;
	}

}
