package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionRequestConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the class to handle portfolio model election submission.
 */

@Service("imCorporateActionElectionDtoService")
@Transactional(value = "springJpaTransactionManager")
public class ImCorporateActionElectionDtoServiceImpl implements CorporateActionElectionDtoService {
    private static final Logger logger = LoggerFactory.getLogger(ImCorporateActionElectionDtoServiceImpl.class);
    private static final String CMS_SYSTEM_ERROR_ID = "Err.IP-0406";

    @Autowired
    private CorporateActionIntegrationService corporateActionService;

    @Autowired
    private CorporateActionElectionIntegrationService corporateActionElectionService;

    @Autowired
    private CorporateActionPersistenceDtoServiceImpl corporateActionPersistenceDtoService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Autowired
    private ImCorporateActionValidationConverter corporateActionValidationConverter;

    @Autowired
    private CorporateActionCommonService corporateActionCommonService;

    @Override
    public CorporateActionElectionDetailsBaseDto submit(CorporateActionElectionDetailsBaseDto electionDetailsDto,
                                                        ServiceErrors serviceErrors) {
        CorporateActionElectionDetailsBaseDto result;
        final boolean isDg = corporateActionCommonService.getUserProfileService().isDealerGroup();
        final boolean isPm = corporateActionCommonService.getUserProfileService().isPortfolioManager();

        CorporateActionDetails details = corporateActionService.loadCorporateActionDetails(electionDetailsDto.getKey().getId(),
                serviceErrors).getCorporateActionDetailsList().iterator().next();

        CorporateActionContext context = new CorporateActionContext();
        context.setCorporateActionDetails(details);
        context.setDealerGroup(isDg || isPm);
        context.setInvestmentManager(corporateActionCommonService.getUserProfileService().isInvestmentManager());

        if (corporateActionValidationConverter.validateOptions(electionDetailsDto, context, serviceErrors)) {
            if (isDg || isPm) {
                result = submitElectionsForDg(context, electionDetailsDto);
            } else {
                result = submitElections(context, electionDetailsDto);
            }
        } else {
            if (isDg || isPm) {
                // Delete all as the entries are no longer valid
                corporateActionPersistenceDtoService.deleteDraftParticipation(electionDetailsDto.getKey().getId());
            }
            result = new CorporateActionElectionDetailsDto(CorporateActionResponseCode.OPTIONS_CHANGED, 0, 0, null,
                    "Unable to submit corporate action elections because the available options have changed.");
        }

        return result;
    }

    private CorporateActionElectionDetailsBaseDto submitElections(CorporateActionContext context,
                                                                  CorporateActionElectionDetailsBaseDto electionDetailsDto) {
        final String imId = corporateActionCommonService.getUserProfileService().getPositionId();
        final ServiceErrors serviceErrors = corporateActionValidationConverter.createServiceErrors();

        List<CorporateActionAccount> accounts =
                corporateActionService
                        .loadCorporateActionAccountsDetailsForIm(imId, context.getCorporateActionDetails().getOrderNumber(), serviceErrors);
        context.setCorporateActionAccountList(accounts);

        ImCorporateActionElectionDetailsDto electionDetails = (ImCorporateActionElectionDetailsDto) electionDetailsDto;

        final Collection<CorporateActionElectionGroup> electionGroups = getElectionGroups(context, electionDetails);
        ElectionResults electionResults = corporateActionValidationConverter.createElectionResults();

        for (CorporateActionElectionGroup electionGroup : electionGroups) {
            final ServiceErrors groupServiceErrors = corporateActionValidationConverter.createServiceErrors();
            electionResults.incrementPortfolioModelCount();

            CorporateActionElectionGroup response = corporateActionElectionService.submitElectionGroupForIm(electionGroup,
                    groupServiceErrors);

            // Handle unknown service errors
            if (groupServiceErrors.hasErrors()) {
                corporateActionValidationConverter.handleServiceErrors(electionDetailsDto, groupServiceErrors, electionResults);
            } else if (response.getValidationErrors() != null &&
                    corporateActionValidationConverter.hasManagedPortfolioValidationError(electionGroup, response.getValidationErrors())) {
                corporateActionValidationConverter.handleValidationErrors(electionGroup, electionDetails, accounts,
                        response.getValidationErrors(), electionResults);
            } else {
                List<ImCorporateActionElectionResultDto> electionResultDtoList =
                        corporateActionValidationConverter.toElectionResultDtoList(electionGroup, electionDetails, accounts,
                                response.getValidationErrors());
                electionResults.getResults().addAll(electionResultDtoList);
                electionResults.setCompleteFailure(false);
                electionResults.setSystemFailure(false);
                electionResults.incrementSuccessCount();
            }
        }

        // Error messages are to be revisited and may be driven from CMS instead
        return !electionResults.isSubmitErrors() ?
               new ImCorporateActionElectionDetailsDto(CorporateActionResponseCode.SUCCESS, electionResults.getSuccessCount(),
                       electionResults.getPortfolioModelCount(), electionResults.getResults(), null) :
               new ImCorporateActionElectionDetailsDto(
                       electionResults.isCompleteFailure() ? CorporateActionResponseCode.ERROR : CorporateActionResponseCode.WARNING,
                       electionResults.getSuccessCount(), electionResults.getPortfolioModelCount(), electionResults.getResults(),
                       electionResults.isSystemFailure() ? corporateActionValidationConverter.getCmsText(CMS_SYSTEM_ERROR_ID) : null);
    }

    private CorporateActionElectionDetailsBaseDto submitElectionsForDg(CorporateActionContext context,
                                                                       CorporateActionElectionDetailsBaseDto electionDetailsDto) {
        final String dgId = corporateActionCommonService.getUserProfileService().getPositionId();
        final ServiceErrors serviceErrors = corporateActionValidationConverter.createServiceErrors();

        List<CorporateActionAccount> accounts = corporateActionService.loadCorporateActionAccountsDetailsForIm(dgId,
                context.getCorporateActionDetails().getOrderNumber(), serviceErrors);
        context.setCorporateActionAccountList(accounts);

        ImCorporateActionElectionDetailsDto electionDetails = (ImCorporateActionElectionDetailsDto) electionDetailsDto;

        // Don't include FTP and shadow portfolio in totals.
        int accountCount = -2;
        int successCount = -2;

        final Collection<CorporateActionElectionGroup> electionGroups = getElectionGroups(context, electionDetails);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        boolean submitErrors = false;
        boolean validationErrors = false;

        for (CorporateActionElectionGroup electionGroup : electionGroups) {
            final ServiceErrors groupServiceErrors = corporateActionValidationConverter.createServiceErrors();
            accountCount += electionGroup.getPositions().size();

            CorporateActionElectionGroup response = corporateActionElectionService.submitElectionGroupForIm(electionGroup,
                    groupServiceErrors);

            // Handle unknown service errors
            if (groupServiceErrors.hasErrors()) {
                logger.error("Unable to submit corporate action order {}: {}", electionDetailsDto.getKey().getId(),
                        groupServiceErrors.getErrorList().iterator().next().getReason());
                submitErrors = true;
            } else if (response.getValidationErrors() != null) {
                List<CorporateActionElectionResultDto> electionResultDtoList = corporateActionValidationConverter
                        .toElectionResultDtoListForDg(electionGroup, electionDetails, accounts, response.getValidationErrors());
                electionResults.addAll(electionResultDtoList);
                validationErrors = true;
                successCount += electionGroup.getPositions().size() - response.getValidationErrors().size();
            } else {
                List<CorporateActionElectionResultDto> electionResultDtoList = corporateActionValidationConverter
                        .toElectionResultDtoListForDg(electionGroup, electionDetails, accounts, response.getValidationErrors());
                electionResults.addAll(electionResultDtoList);
                successCount += electionGroup.getPositions().size();
            }
        }

        corporateActionPersistenceDtoService.deleteSuccessfulDraftElectionsForDg(electionDetails, electionResults);

        if (submitErrors) {
            return new CorporateActionElectionDetailsDto(CorporateActionResponseCode.ERROR, successCount, accountCount,
                    electionResults, successCount == 0 ? corporateActionValidationConverter.getCmsText(CMS_SYSTEM_ERROR_ID) : null);
        }

        return new CorporateActionElectionDetailsDto(!validationErrors ? CorporateActionResponseCode.SUCCESS
                                                                       : CorporateActionResponseCode.ERROR, successCount, accountCount,
                electionResults, null);
    }

    private Collection<CorporateActionElectionGroup> getElectionGroups(CorporateActionContext context,
                                                                       ImCorporateActionElectionDetailsDto electionDetails) {
        Collection<CorporateActionElectionGroup> electionGroups;
        CorporateActionRequestConverterService requestConverter =
                corporateActionConverterFactory.getRequestConverterService(context.getCorporateActionDetails());

        final boolean isDg = corporateActionCommonService.getUserProfileService().isDealerGroup();
        final boolean isPm = corporateActionCommonService.getUserProfileService().isPortfolioManager();

        if (isDg || isPm) {
            electionGroups = requestConverter.createElectionGroupsForDg(context, electionDetails);
        } else {
            electionGroups = requestConverter.createElectionGroupsForIm(context, electionDetails);
        }
        return electionGroups;
    }
}
