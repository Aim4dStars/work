package com.bt.nextgen.core.web.model;

import java.util.List;

public class Intermediary extends Person implements Intermediaries
{
	private String advisorId;
	private String dealerGroupName;
	private String dealerGroupId;
	private String officeAndPracticeName;
	private List<Investor> clients;
    private String role;
	private String companyName;

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
		return "Intermediary [dealerGroupName=" + dealerGroupName + ", officeAndPracticeName=" + officeAndPracticeName + ", clients="
			+ clients + "]";
	}
	@Override
	public String getRole() {
		return role;
	}
    public void setRole(String role) {
       this.role =  role ;
    }
	public String getDealerGroupId()
	{
		return dealerGroupId;
	}
	public void setDealerGroupId(String dealerGroupId)
	{
		this.dealerGroupId = dealerGroupId;
	}

    @Override
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
