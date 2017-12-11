package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;


@Service
public class CorporateActionValidationConverterImpl implements CorporateActionValidationConverter {
    private final static String ERROR_CODE_PREFIX = "errorcode.ca.";

    @Autowired
    protected CmsService cmsService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    public List<CorporateActionElectionResultDto> toElectionResultDtoList(CorporateActionElectionGroup electionGroup,
                                                                          CorporateActionElectionDetailsDto electionDetailsDto,
                                                                          List<CorporateActionValidationError> validationErrors) {
        List<CorporateActionElectionResultDto> resultDtos = new ArrayList<>();

        for (CorporateActionPosition position : electionGroup.getPositions()) {
            CorporateActionAccountDetailsDto accountDetails =
                    selectFirst(electionDetailsDto.getAccounts(), having(on(CorporateActionAccountDetailsDto.class).getPositionId(),
                            IsEqual.equalTo(position.getId())));

            List<String> errorList = null;

            if (validationErrors != null) {
                List<CorporateActionValidationError> errors = select(validationErrors,
                        having(on(CorporateActionValidationError.class).getPositionId(), IsEqual.equalTo(position.getId())));

                if (errors != null && !errors.isEmpty()) {
                    errorList = new ArrayList<>(errors.size());

                    for (CorporateActionValidationError error : errors) {
                        String errorId = Properties.get(ERROR_CODE_PREFIX + error.getErrorCode());
                        errorList.add(errorId == null ? error.getErrorMessage() : cmsService.getDynamicContent(errorId, null));
                    }
                }
            }

            resultDtos.add(new CorporateActionElectionResultDto(accountDetails.getAccountId(), errorList != null ?
                                                                                               CorporateActionValidationStatus.ERROR :
                                                                                               CorporateActionValidationStatus.SUCCESS,
                    errorList));
        }

        return resultDtos;
    }

    @Override
    public String getCmsText(String cmsId, String... params) {
        return params == null ? cmsService.getContent(cmsId) : cmsService.getDynamicContent(cmsId, params);
    }

    @Override
    public boolean validateOptions(CorporateActionElectionDetailsBaseDto electionDetailsDto, CorporateActionContext context,
                                   ServiceErrors serviceErrors) {
        CorporateActionResponseConverterService responseConverter =
                corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

        List<CorporateActionOptionDto> serverOptions = responseConverter.toElectionOptionDtos(context, serviceErrors);

        if (serverOptions == null || serverOptions.isEmpty() || serverOptions.size() != electionDetailsDto.getOptions().size()) {
            return false;
        }

        for (int i = 0; i < electionDetailsDto.getOptions().size(); i++) {
            if (!electionDetailsDto.getOptions().get(i).getSummary().equals(serverOptions.get(i).getSummary())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<CorporateActionElectionResultDto> createElectionResults() {
        return new ArrayList<>();
    }

    @Override
    public ServiceErrors createServiceErrors() {
        return new ServiceErrorsImpl();
    }
}
