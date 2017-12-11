package com.bt.nextgen.service.integration.search;

import com.bt.nextgen.service.avaloq.userinformation.JobRole;

import java.util.List;

/**
 * Created by L070815 on 11/06/2015.
 */
public interface ProfileUserRole {

    public JobRole getUserRole();

    public void setUserRole(JobRole userRole);

    public String getDealerGroupName();

    public String getDealerGroup();

    public String getCompanyName();

    public String getCloseDate();

}
