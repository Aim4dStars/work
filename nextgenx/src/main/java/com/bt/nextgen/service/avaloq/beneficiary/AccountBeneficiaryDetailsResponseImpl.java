package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main object that holds the list of mapped {@link BeneficiaryDetails} object.
 * Created by M035995 on 8/07/2016.
 */
@ServiceBean(xpath = "bp_head")
public class AccountBeneficiaryDetailsResponseImpl implements AccountBeneficiaryDetailsResponse {

    @ServiceElement(xpath = "bp_id/val", converter = AccountKeyConverter.class)
    private AccountKey accountKey;

    @ServiceElement(xpath = "last_update/val", converter = IsoDateTimeConverter.class)
    private DateTime beneficiariesLastUpdatedTime;

    @ServiceElement(xpath = "auto_rev_date/val", converter = IsoDateTimeConverter.class)
    private DateTime autoReversionaryActivationDate;

    @ServiceElementList(xpath = "benef_info_list/benef_info", type = BeneficiaryDetailsImpl.class)
    private List<BeneficiaryDetails> beneficiaryDetails = new ArrayList<>();

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BeneficiaryDetails> getBeneficiaryDetails() {
        return beneficiaryDetails;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTime getLastUpdatedDate() {
        return beneficiariesLastUpdatedTime;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public void setBeneficiariesLastUpdatedTime(DateTime beneficiariesLastUpdatedTime) {
        this.beneficiariesLastUpdatedTime = beneficiariesLastUpdatedTime;
    }

    public void setBeneficiaryDetails(List<BeneficiaryDetails> beneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails;
    }

    @Override
    public DateTime getAutoReversionaryActivationDate() {
        return autoReversionaryActivationDate;
    }

    public void setAutoReversionaryActivationDate(DateTime autoReversionaryActivationDate) {
        this.autoReversionaryActivationDate = autoReversionaryActivationDate;
    }
}
