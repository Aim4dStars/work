package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.ProductKey;


public interface CorporateActionAccountHelper {
    String getAdviserName(BrokerKey brokerKey, ServiceErrors serviceErrors);

    String getPortfolioName(ProductKey productKey, ServiceErrors serviceErrors);

    CorporateActionAccountElectionsDto getSubmittedElections(CorporateActionContext context, CorporateActionAccount caa);

    CorporateActionAccountElectionsDto getSavedElections(CorporateActionContext context, String accountId,
                                                         CorporateActionSavedDetails savedDetails);

    String getPreferredPhone(List<Phone> phones);

    String getPreferredEmail(List<Email> emails);

    String getPreferredAddress(List<Address> addresses);
}
