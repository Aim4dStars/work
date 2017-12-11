package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.fees.model.OneOffFeesDto;
import com.bt.nextgen.api.fees.validation.OneOffFeesDtoErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.OneOffFeesImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AvailableCash;
import com.bt.nextgen.service.integration.fees.OneOffFees;
import com.bt.nextgen.service.integration.fees.OneOffFeesIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "springJpaTransactionManager")
public class OneOffFeesDtoServiceImpl implements OneOffFeesDtoService {
    @Autowired
    OneOffFeesIntegrationService adviceFeesService;

    @Autowired
    private OneOffFeesDtoErrorMapper oneOffFeesDtoErrorMapper;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public OneOffFeesDto create(OneOffFeesDto oneOffFeesDto, ServiceErrors serviceErrors) {
        OneOffFees adviceFeesInterface = adviceFeesService.submitAdviceFees(toOneOffFeesModel(oneOffFeesDto), serviceErrors);
        return toOneOffFeesDto(adviceFeesInterface, oneOffFeesDto);
    }

    @Override
    public OneOffFeesDto validate(OneOffFeesDto adviceFeesDto, ServiceErrors serviceErrors) {
        OneOffFees adviceFeesInterface = adviceFeesService.validateAdviceFees(toOneOffFeesModel(adviceFeesDto), serviceErrors);
        return toOneOffFeesDto(adviceFeesInterface, adviceFeesDto);
    }

    /**
     * Converts domain objects into dto for transaction.
     * @param feesInterface
     * @param adviceFeesDto
     * @return OneOffFeesDto object converted from Domain objects
     */
    private OneOffFeesDto toOneOffFeesDto(OneOffFees feesInterface, OneOffFeesDto adviceFeesDto) {
        adviceFeesDto.setSubmitDate(ApiFormatter.aestFormat(feesInterface.getSubmitDate()));
        String description = feesInterface.getDescription();
        adviceFeesDto.setFeesAmount(feesInterface.getFees());
        adviceFeesDto.setDescription(description);
        adviceFeesDto.setWarnings(oneOffFeesDtoErrorMapper.map(feesInterface.getValidationErrors()));
        return adviceFeesDto;
    }

    /**
     * Converts DTO objects into domain object manly used for transactions.
     * @param adviceFeesDto
     * @return OneOffFees domain object
     */
    private OneOffFees toOneOffFeesModel(OneOffFeesDto adviceFeesDto) {
        OneOffFeesImpl feesInterface = new OneOffFeesImpl();
        AccountKey key = adviceFeesDto.getKey();
        feesInterface.setFees(adviceFeesDto.getFeesAmount());
        feesInterface.setDescription(adviceFeesDto.getDescription());
        feesInterface.setAccountKey(AccountKey.valueOf(new EncodedString(key.getId()).plainText()));
        return feesInterface;
    }

    @Override
    public OneOffFeesDto find(AccountKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(new EncodedString(key.getId()).plainText());
        OneOffFees adviceFeesInterface = adviceFeesService.getChargedFees(accountKey, serviceErrors);
        AvailableCash availableCash = accountService.loadAvailableCash(accountKey, serviceErrors);
        return toOneOffFeesDto(adviceFeesInterface, availableCash);
    }

    /**
     * Converts domain object into DTO object
     * @param adviceFeesInterface
     * @param availableCash
     * @return one off fees dto -including available cache
     */
    private OneOffFeesDto toOneOffFeesDto(OneOffFees adviceFeesInterface, AvailableCash availableCash) {
        OneOffFeesDto adviceFeesDto = new OneOffFeesDto();
        adviceFeesDto.setYearlyFees(adviceFeesInterface.getYearlyFees());
        adviceFeesDto.setAvailableCash(availableCash.getAvailableCash());
        return adviceFeesDto;
    }
}
