package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@Service
public class ImCorporateActionValidationConverterImpl implements ImCorporateActionValidationConverter {
    private final static String ERROR_CODE_PREFIX = "errorcode.ca.";

    @Autowired
    protected CmsService cmsService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    public List<ImCorporateActionElectionResultDto> toElectionResultDtoList(CorporateActionElectionGroup electionGroup,
                                                                            ImCorporateActionElectionDetailsDto electionDetailsDto,
                                                                            List<CorporateActionAccount> accounts,
                                                                            List<CorporateActionValidationError> validationErrors) {
        List<ImCorporateActionElectionResultDto> resultDtos = new ArrayList<>();
        Map<String, ErrorCount> errorMap = new HashMap<>();

        if (validationErrors != null) {
            List<CorporateActionAccount> ipsMpAccounts = select(accounts,
                    having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.MANAGED_PORTFOLIO)));

            boolean completeSubmissionFailure = isCompleteSubmissionFailure(electionGroup, validationErrors);

            for (CorporateActionValidationError error : validationErrors) {
                // Do not report shadow positions errors
                if (isManagedPortfolioPosition(error.getPositionId(), ipsMpAccounts)) {
                    if (errorMap.containsKey(error.getErrorCode())) {
                        ErrorCount errorCount = errorMap.get(error.getErrorCode());
                        errorCount.incrementAccountCount();
                    } else {
                        errorMap.put(error.getErrorCode(), new ErrorCount(error.getErrorCode(), 1));
                    }
                }
            }

            if (!errorMap.isEmpty()) {
                List<String> errors = new ArrayList<>(errorMap.size());
                String totalPositions = Integer.toString(numberOfMpPositions(electionGroup.getPositions()));

                for (ErrorCount errorCount : errorMap.values()) {
                    String errorId = Properties.get(ERROR_CODE_PREFIX + "im_" + errorCount.getErrorCode());
                    errors.add(getCmsText(errorId, Integer.toString(errorCount.getAccountCount()), totalPositions,
                            completeSubmissionFailure ? "all" : "some"));
                }

                resultDtos.add(new ImCorporateActionElectionResultDto(electionGroup.getIpsId(),
                        completeSubmissionFailure ? CorporateActionValidationStatus.ERROR : CorporateActionValidationStatus.WARNING,
                        errors));
            } else {
                resultDtos.add(new ImCorporateActionElectionResultDto(electionGroup.getIpsId(), CorporateActionValidationStatus.SUCCESS,
                        null));
            }
        }

        return resultDtos;
    }

    public List<CorporateActionElectionResultDto> toElectionResultDtoListForDg(CorporateActionElectionGroup electionGroup,
                                                                               ImCorporateActionElectionDetailsDto electionDetailsDto,
                                                                               List<CorporateActionAccount> accounts,
                                                                               List<CorporateActionValidationError> validationErrors) {

        List<CorporateActionElectionResultDto> resultDtos = new ArrayList<>();

        List<CorporateActionAccount> mpAccounts = select(accounts,
                having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.MANAGED_PORTFOLIO)));

        for (CorporateActionPosition position : electionGroup.getPositions()) {
            // Don't report errors on FTP and shadow portfolio
            if (isManagedPortfolioPosition(position.getId(), mpAccounts)) {
                CorporateActionAccountDetailsDto accountDetails = selectFirst(electionDetailsDto.getAccounts(),
                        having(on(CorporateActionAccountDetailsDto.class).getPositionId(), IsEqual.equalTo(position.getId())));

                List<String> errorList = null;

                if (validationErrors != null) {
                    List<CorporateActionValidationError> errors = select(validationErrors,
                            having(on(CorporateActionValidationError.class).getPositionId(), IsEqual.equalTo(position.getId())));

                    if (errors != null && !errors.isEmpty()) {
                        errorList = new ArrayList<>(errors.size());

                        for (CorporateActionValidationError error : errors) {
                            String errorId = Properties.get(ERROR_CODE_PREFIX + error.getErrorCode());
                            errorList
                                    .add(errorId == null ? error.getErrorMessage() : cmsService.getDynamicContent(errorId, null));
                        }
                    }
                }

                resultDtos.add(new CorporateActionElectionResultDto(accountDetails.getAccountId(),
                        errorList != null ? CorporateActionValidationStatus.ERROR : CorporateActionValidationStatus.SUCCESS,
                        errorList));
            }
        }

        return resultDtos;
    }

    @Override
    public boolean hasManagedPortfolioValidationError(CorporateActionElectionGroup electionGroup,
                                                      List<CorporateActionValidationError> validationErrors) {
        for (CorporateActionPosition position : electionGroup.getPositions()) {
            if (ContainerType.MANAGED_PORTFOLIO.equals(position.getContainerType())) {

                CorporateActionValidationError error =
                        selectFirst(validationErrors, having(on(CorporateActionValidationError.class).getPositionId(),
                                equalTo(position.getId())));

                if (error != null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isCompleteSubmissionFailure(CorporateActionElectionGroup electionGroup,
                                               List<CorporateActionValidationError> validationErrors) {
        int mpPositions = 0;
        int failedMpPositions = 0;

        for (CorporateActionPosition position : electionGroup.getPositions()) {
            if (ContainerType.MANAGED_PORTFOLIO.equals(position.getContainerType())) {
                mpPositions++;

                CorporateActionValidationError error =
                        selectFirst(validationErrors, having(on(CorporateActionValidationError.class).getPositionId(),
                                equalTo(position.getId())));

                if (error != null) {
                    failedMpPositions++;
                }
            }
        }

        return mpPositions == failedMpPositions;
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
    public void handleValidationErrors(CorporateActionElectionGroup electionGroup, ImCorporateActionElectionDetailsDto electionDetails,
                                       List<CorporateActionAccount> accounts,
                                       List<CorporateActionValidationError> validationErrors,
                                       ElectionResults electionResults) {
        List<ImCorporateActionElectionResultDto> electionResultDtoList = toElectionResultDtoList(electionGroup, electionDetails, accounts,
                validationErrors);

        electionResults.getResults().addAll(electionResultDtoList);
        electionResults.setSubmitErrors(true);
        boolean fullSubmissionFailure = isCompleteSubmissionFailure(electionGroup, validationErrors);
        electionResults.setCompleteFailure(electionResults.isCompleteFailure() && fullSubmissionFailure);

        if (fullSubmissionFailure) {
            electionResults.decrementSuccessCount();
            electionResults.setSystemFailure(false);
        }
    }

    @Override
    public void handleServiceErrors(CorporateActionElectionDetailsBaseDto electionDetailsDto, ServiceErrors groupServiceErrors,
                                    ElectionResults electionResults) {
        electionResults.setSubmitErrors(true);
        electionResults.setCompleteFailure(electionResults.isCompleteFailure());
        electionResults.setSystemFailure(electionResults.isSystemFailure());
        electionResults.decrementSuccessCount();
    }

    @Override
    public ElectionResults createElectionResults() {
        return new ElectionResults();
    }

    @Override
    public ServiceErrors createServiceErrors() {
        return new ServiceErrorsImpl();
    }

    private boolean isManagedPortfolioPosition(String positionId, List<CorporateActionAccount> corporateActionAccounts) {
        for (CorporateActionAccount account : corporateActionAccounts) {
            if (positionId.equals(account.getPositionId())) {
                return true;
            }
        }

        return false;
    }

    private int numberOfMpPositions(List<CorporateActionPosition> positions) {
        int count = 0;

        for (CorporateActionPosition position : positions) {
            if (ContainerType.MANAGED_PORTFOLIO.equals(position.getContainerType())) {
                count++;
            }
        }

        return count;
    }

    private static final class ErrorCount {
        private String errorCode;
        private int accountCount;

        private ErrorCount(String errorCode, int accountCount) {
            this.errorCode = errorCode;
            this.accountCount = accountCount;
        }

        private String getErrorCode() {
            return errorCode;
        }

        private int getAccountCount() {
            return accountCount;
        }

        private void incrementAccountCount() {
            accountCount++;
        }
    }
}
