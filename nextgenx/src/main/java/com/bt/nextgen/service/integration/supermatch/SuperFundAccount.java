package com.bt.nextgen.service.integration.supermatch;

import com.bt.nextgen.service.btesb.supermatch.model.ActivityStatus;
import com.bt.nextgen.service.btesb.supermatch.model.FundCategory;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface SuperFundAccount {

    String getFundIdentifier();

    String getAccountNumber();

    Boolean getInsuranceIndicator();

    Boolean getDefinedBenefitIndicator();

    Boolean getInwardRolloverIndicator();

    ActivityStatus getActivityStatus();

    BigDecimal getAccountBalance();

    FundCategory getFundCategory();

    String getUsi();

    Boolean getRolloverStatus();

    String getRolloverId();

    BigDecimal getRolloverAmount();

    DateTime getRolloverStatusProvidedDateTime();

    String getRolloverStatusSubmitter();

    List<Member> getMembers();

    String getOrganisationName();

    String getAbn();

    String getContactName();

    String getContactNumber();

    String getAddressLine();

    String getLocality();

    String getState();

    String getPostcode();

    String getCountryCode();
}
