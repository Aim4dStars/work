package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.builder.SuperNominationTypeDtoConverter;
import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by L067218 on 1/07/2016.
 */
@Service
public class NominationTypesDtoServiceImpl implements NominationTypesDtoService {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public List<SuperNominationTypeDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        AccountKey accountKey = null;
        String filter =  null;

        for (ApiSearchCriteria parameter : criteriaList)
        {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty()))
            {
                accountKey = AccountKey.valueOf(EncodedString.toPlainText(parameter.getValue()));
            }
            else if(Attribute.FILTER_FOR_ACCOUNT.equals(parameter.getProperty()))
            {
                filter=parameter.getValue();
            }
            else
            {
                throw new IllegalArgumentException("Unsupported search");
            }
        }
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        final SuperNominationTypeDtoConverter dtoConverter = new SuperNominationTypeDtoConverter();

        return dtoConverter.createNominationTypeList(staticIntegrationService.loadCodes(CodeCategory.SUPER_NOMINATION_TYPE, serviceErrors), account.getSuperAccountSubType(), filter);
    }
}
