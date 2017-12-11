package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.api.account.v2.model.AccountKey;

import java.util.List;

/**
 * Created by L067218 on 14/07/2016.
 */
public interface SaveBeneficiariesDetails {
    AccountKey getAccountKey();

    void setAccountKey(AccountKey accountKey);

    List<BeneficiaryDetails> getBeneficiaries();

    void setBeneficiaries(List<BeneficiaryDetails> beneficiaries);

    String getTransactionStatus();

    void setTransactionStatus(String transactionStatus);

    String getModificationSeq();

    void setModificationSeq(String modificationSeq);
}
