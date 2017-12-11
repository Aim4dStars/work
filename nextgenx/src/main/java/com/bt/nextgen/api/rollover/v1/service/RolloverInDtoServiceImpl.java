package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDtoImpl;
import com.bt.nextgen.api.rollover.v1.model.RolloverKey;
import com.bt.nextgen.api.rollover.v1.validation.RolloverDtoErrorMapperImpl;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.RolloverDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.rollover.CashRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RolloverInDtoServiceImpl implements RolloverInDtoService {

    @Autowired
    private CashRolloverService rolloverService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private RolloverDtoErrorMapperImpl errorMapper;

    @Override
    public RolloverInDto find(RolloverKey key, ServiceErrors serviceErrors) {
        RolloverDetails result = rolloverService.loadRolloverDetails(key.getRolloverId(), serviceErrors);
        RolloverDetailsDto resultDto = new RolloverDetailsDto(key.getAccountId(), result);
        return new RolloverInDtoImpl(key, resultDto.getRolloverType(), Collections.singletonList(resultDto));
    }

    @Override
    public RolloverInDto discard(RolloverKey key, ServiceErrors serviceErrors) {
        RolloverDetails result = rolloverService.discardRolloverDetails(key.getRolloverId(), serviceErrors);
        RolloverDetailsDto resultDto = new RolloverDetailsDto(key.getAccountId(), result);
        return new RolloverInDtoImpl(key, "Rollover Discarded", Collections.singletonList(resultDto));
    }

    @Override
    public RolloverInDto save(RolloverInDto keyedObject, ServiceErrors serviceErrors) {
        List<RolloverDetailsDto> results = new ArrayList<>();

        RolloverType rollType = RolloverType.forDisplay(keyedObject.getRolloverType());
        String accountId = keyedObject.getKey().getAccountId();

        // Empty if this is a new rollover - needed only when changing a single existing rollover.
        String rolloverId = keyedObject.getKey().getRolloverId();
        String lastTransSeqId = getLastTransSeqId(keyedObject.getKey(), serviceErrors);

        for (RolloverDetailsDto inDto : keyedObject.getRolloverDetails()) {
            RolloverDetails rolloverDetails = convertToRolloverDetails(accountId, rolloverId, rollType, lastTransSeqId, inDto);
            RolloverDetails result = rolloverService.saveRolloverDetails(rolloverDetails, serviceErrors);

            results.add(new RolloverDetailsDto(keyedObject.getKey().getAccountId(), result));
        }
        return new RolloverInDtoImpl(keyedObject.getKey(), rollType.getDisplayName(), results);
    }

    @Override
    public RolloverInDto submit(RolloverInDto keyedObject, ServiceErrors serviceErrors) {

        List<RolloverDetailsDto> results = new ArrayList<>();

        RolloverType rollType = RolloverType.forDisplay(keyedObject.getRolloverType());
        String accountId = keyedObject.getKey().getAccountId();

        // Empty if this is a new rollover - needed only when changing a single existing rollover.
        String rolloverId = keyedObject.getKey().getRolloverId();
        String lastTransSeqId = getLastTransSeqId(keyedObject.getKey(), serviceErrors);

        for (RolloverDetailsDto inDto : keyedObject.getRolloverDetails()) {
            if (inDto.getRolloverOption() != null) {
                RolloverDetails rolloverDetails = convertToRolloverDetails(accountId, rolloverId, rollType, lastTransSeqId, inDto);
                RolloverDetails result = rolloverService.submitRolloverInDetails(rolloverDetails, serviceErrors);

                RolloverDetailsDto dto = new RolloverDetailsDto(keyedObject.getKey().getAccountId(), result);

                // Error mapper
                List<ValidationError> validErrorList = ((TransactionResponse) result).getValidationErrors();
                dto.setWarnings(errorMapper.map(validErrorList));
                results.add(dto);
            }
        }
        return new RolloverInDtoImpl(keyedObject.getKey(), rollType.getDisplayName(), results);
    }

    protected RolloverDetails convertToRolloverDetails(String accountId, String rolloverId, RolloverType rollType,
            String lastTransSeqId, RolloverDetailsDto rolloverInDto) {

        RolloverDetailsImpl details = new RolloverDetailsImpl();
        details.setAccountKey(AccountKey.valueOf(EncodedString.toPlainText(accountId)));
        details.setRolloverId(rolloverId);
        details.setAccountNumber(rolloverInDto.getAccountName());
        details.setAmount(null);
        if (rolloverInDto.getFundAmount() != null) {
            details.setAmount(rolloverInDto.getFundAmount());
        }
        details.setFundAbn(rolloverInDto.getFundAbn());

        if (StringUtils.isNotBlank(rolloverInDto.getFundId())) {
            details.setFundId(rolloverInDto.getFundId());
        }
        details.setFundName(rolloverInDto.getFundName());
        details.setPanInitiated(rolloverInDto.getPanInitiated());
        details.setRolloverOption(RolloverOption.forDisplay(rolloverInDto.getRolloverOption()));
        details.setRolloverType(rollType);
        details.setFundUsi(rolloverInDto.getFundUsi());
        details.setIncludeInsurance(rolloverInDto.getIncludeInsurance());
        details.setLastTransSeqId(lastTransSeqId);

        return details;
    }

    private String getLastTransSeqId(RolloverKey key, ServiceErrors serviceErrors) {
        if (key.getRolloverId() != null) {
            RolloverInDto existingOrder = find(key, serviceErrors);
            return existingOrder.getRolloverDetails().get(0).getLastTransSeqId();
        }
        return null;
    }
}
