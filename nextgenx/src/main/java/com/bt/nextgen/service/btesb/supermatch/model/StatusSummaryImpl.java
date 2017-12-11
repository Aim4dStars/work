package com.bt.nextgen.service.btesb.supermatch.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.btesb.supermatch.SuperMatchDateTimeConverter;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import org.joda.time.DateTime;

/**
 * Status summary details for the Super match
 */
@ServiceBean(xpath = "SuperannuationMatch", type = ServiceBeanType.CONCRETE)
public class StatusSummaryImpl implements StatusSummary {

    @ServiceElement(xpath = "ConsentStatus")
    private String consentStatus;

    @ServiceElement(xpath = "ConsentStatusProvidedFlag")
    private Boolean consentStatusProvided;

    @ServiceElement(xpath = "MatchResultAcknowledgedFlag")
    private Boolean matchResultAcknowledged;

    @ServiceElement(xpath = "MatchResultAvailableFlag")
    private Boolean matchResultAvailable;

    @ServiceElement(xpath = "MatchResultAvailableStatus")
    private String matchResultAvailableStatus;

    @ServiceElement(xpath = "ATOHeldFundCount")
    private Integer atoHeldFundCount;

    @ServiceElement(xpath = "BTFundCount")
    private Integer btFundCount;

    @ServiceElement(xpath = "ExternalFundCount")
    private Integer externalFundCount;

    @ServiceElement(xpath = "LastMatchResultDateTime", converter = SuperMatchDateTimeConverter.class)
    private DateTime lastMatchResultDateTime;

    @ServiceElement(xpath = "ConsentStatusSubmitter")
    private String consentStatusSubmitter;

    @ServiceElement(xpath = "CustomerType")
    private String customerType;

    @Override
    public String getConsentStatus() {
        return consentStatus;
    }

    @Override
    public Boolean isConsentStatusProvided() {
        return consentStatusProvided;
    }

    @Override
    public Boolean isMatchResultAcknowledged() {
        return matchResultAcknowledged;
    }

    @Override
    public Boolean isMatchResultAvailable() {
        return matchResultAvailable;
    }

    @Override
    public String getMatchResultAvailableStatus() {
        return matchResultAvailableStatus;
    }

    @Override
    public Integer getAtoHeldFundCount() {
        return atoHeldFundCount;
    }

    @Override
    public Integer getBtFundCount() {
        return btFundCount;
    }

    @Override
    public Integer getExternalFundCount() {
        return externalFundCount;
    }

    @Override
    public DateTime getLastMatchResultDateTime() {
        return lastMatchResultDateTime;
    }

    @Override
    public String getConsentStatusSubmitter() {
        return consentStatusSubmitter;
    }

    @Override
    public String getCustomerType() {
        return customerType;
    }
}
