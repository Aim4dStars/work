package com.bt.nextgen.core.web.model;

import java.util.List;

/**
 * 
 * Class to club the advisor, paraplanner and admin assistant.
 *
 */
public interface Intermediaries extends PersonInterface
{
	public String getDealerGroupName() ;
	
	public String getOfficeAndPracticeName();
	
	public List<Investor> getClients();
	
	public String getRole();
	
	public void setDealerGroupName(String dealerGroup);
	
	public void setDealerGroupId(String dealerGroupId);
	
	public String getDealerGroupId();

    public String getCompanyName();
}
