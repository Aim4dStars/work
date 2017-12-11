package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

import com.bt.nextgen.core.reporting.ReportFormatter;

@Service
public class CorporateActionAccountHelperImpl implements CorporateActionAccountHelper {
    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Override
    public String getAdviserName(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        return brokerService.getAdviserBrokerUser(brokerKey, serviceErrors).getFullName();
    }

    @Override
    public String getPortfolioName(ProductKey productKey, ServiceErrors serviceErrors) {
        return productIntegrationService.getProductDetail(productKey, serviceErrors).getProductName();
    }

    @Override
    public CorporateActionAccountElectionsDto getSubmittedElections(CorporateActionContext context, CorporateActionAccount caa) {
        CorporateActionResponseConverterService responseConverter =
                corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

        return CorporateActionAccountParticipationStatus.SUBMITTED.equals(caa.getElectionStatus()) ?
               responseConverter.toSubmittedAccountElectionsDto(context, caa) : null;
    }

    @Override
    public CorporateActionAccountElectionsDto getSavedElections(CorporateActionContext context, String accountId,
                                                                CorporateActionSavedDetails savedDetails) {
        if (savedDetails != null && CorporateActionResponseCode.SUCCESS.equals(savedDetails.getResponseCode())) {
            CorporateActionResponseConverterService responseConverter =
                    corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

            return responseConverter.toSavedAccountElectionsDto(context, accountId, savedDetails);
        }

        return null;
    }

    @Override
    public String getPreferredPhone(List<Phone> phones) {
        if (phones != null && !phones.isEmpty()) {
            Phone phone = phones.iterator().next();

            StringBuilder sb = new StringBuilder();
            addContactComponent(phone.getAreaCode(), null, null, sb);
            addContactComponent(phone.getNumber(), null, null, sb);

            return ReportFormatter.formatTelephoneNumber(sb.toString());
        }

        return null;
    }

    @Override
    public String getPreferredEmail(List<Email> emails) {
        if (emails != null && !emails.isEmpty()) {
            return emails.iterator().next().getEmail();
        }

        return null;
    }

    @Override
    public String getPreferredAddress(List<Address> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            Address address = selectFirst(addresses, having(on(Address.class).isDomicile(), equalTo(true)));

            if (address != null) {
                StringBuilder fullAddress = new StringBuilder();

                addContactComponent(address.getUnit(), null, "/", fullAddress);
                addContactComponent(address.getStreetNumber(), null, null, fullAddress);
                addContactComponent(address.getStreetName(), " ", null, fullAddress);
                addContactComponent(address.getStreetType(), " ", null, fullAddress);

                if (fullAddress.length() > 0) {
                    fullAddress.append(",").append(" ");
                }

                addContactComponent(address.getSuburb(), null, ",", fullAddress);
                addContactComponent(address.getStateAbbr(), " ", null, fullAddress);
                addContactComponent(address.getPostCode(), " ", null, fullAddress);

                return fullAddress.toString();
            }
        }

        return null;
    }

    private void addContactComponent(String contactComponent, String prefix, String suffix, StringBuilder result) {
        if (StringUtils.isNotBlank(contactComponent)) {
            if (prefix != null) {
                result.append(prefix);
            }

            result.append(contactComponent);

            if (suffix != null) {
                result.append(suffix);
            }
        }
    }
}
