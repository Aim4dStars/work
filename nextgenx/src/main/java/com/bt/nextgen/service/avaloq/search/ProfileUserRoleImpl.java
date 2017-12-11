package com.bt.nextgen.service.avaloq.search;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.search.ProfileUserRole;

import static com.bt.nextgen.integration.xml.annotation.ServiceBeanType.CONCRETE;

/**
 * Created by L070815 on 11/06/2015.
 */
@ServiceBean(xpath="profile", type = CONCRETE)
public class ProfileUserRoleImpl implements ProfileUserRole {

    @ServiceElement(xpath = "user_role/val",staticCodeCategory = "JOB_TYPE")
    private JobRole userRole;
    @ServiceElement(xpath = "dealer_group/val")
    private String dealerGroup;
    //TODO:Need to populate from the Broker Hierarchy if this field is removed
    @ServiceElement(xpath = "dealer_group/annot/displ_text")
    private String dealerGroupName;
    @ServiceElement(xpath = "accountant/val")
    private String companyName;
    @ServiceElement(xpath = "close_date/val")
    private String closeDate;

    @Override
    public JobRole getUserRole() {
        return userRole;
    }

    @Override
    public void setUserRole(JobRole userRole) {
        this.userRole = userRole;

    }

    public void setDealerGroup(String dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    @Override
    public String getDealerGroupName() {
        return dealerGroupName;
    }

    @Override
    public String getDealerGroup() {
        return dealerGroup;
    }

    public String getCompanyName() {
        return companyName;
    }


    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }
    @Override
    public String getCloseDate() {
        return this.closeDate;
    }

}
