package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.List;

/**
 * Created by L067218 on 14/07/2016.
 */
@ServiceBean(xpath = "/")
public class SaveBeneficiariesDetailsImpl implements SaveBeneficiariesDetails {

    private static final String XML_HEADER = "//data/sa_benef_det_list/sa_benef_det";

    private AccountKey accountKey;

    @ServiceElementList(xpath = XML_HEADER, type = BeneficiaryDetailsImpl.class)
    private List<BeneficiaryDetails> beneficiaries;
    private String transactionStatus;



    private String modificationSeq;

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public List<BeneficiaryDetails> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<BeneficiaryDetails> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getModificationSeq() {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }
}
