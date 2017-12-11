package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Beneficiary Details response holder class
 *      - List of account with beneficiary detail
 */
@ServiceBean(xpath = "/")
public class BeneficiaryDetailsResponseHolderImpl extends AvaloqBaseResponseImpl {

    @ServiceElementList(xpath = "//bp_list/bp/bp_head_list/bp_head", type = AccountBeneficiaryDetailsResponseImpl.class)
    private List<AccountBeneficiaryDetailsResponseImpl> beneficiaryDetailsList = new ArrayList<>();

    public List<AccountBeneficiaryDetailsResponseImpl> getBeneficiaryDetailsList() {
        return beneficiaryDetailsList;
    }

    public void setBeneficiaryDetailsList(List<AccountBeneficiaryDetailsResponseImpl> beneficiaryDetailsList) {
        this.beneficiaryDetailsList = beneficiaryDetailsList;
    }
}
