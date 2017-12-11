package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionRequestConverterService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;

/**
 * This is the class to handle account election submission.
 */
@Service("corporateActionElectionDtoService")
@Transactional(value = "springJpaTransactionManager")
public class CorporateActionElectionDtoServiceImpl implements CorporateActionElectionDtoService {
    //private static final Logger logger = LoggerFactory.getLogger(CorporateActionElectionDtoServiceImpl.class);
    private static final String CMS_SYSTEM_ERROR_ID = "Err.IP-0406";
    private static final String CMS_SYSTEM_ERROR_ID_DIR_INV = "Err.IP-0598";

    @Autowired
    private CorporateActionIntegrationService corporateActionService;

    @Autowired
    private CorporateActionElectionIntegrationService corporateActionElectionService;

    @Autowired
    private CorporateActionPersistenceDtoServiceImpl corporateActionPersistenceDtoService;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Autowired
    private CorporateActionValidationConverter corporateActionValidationConverter;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private CorporateActionCommonService corporateActionCommonService;

    @Override
    public CorporateActionElectionDetailsBaseDto submit(CorporateActionElectionDetailsBaseDto electionDetailsDto,
                                                        ServiceErrors serviceErrors) {
        CorporateActionElectionDetailsBaseDto result;

        CorporateActionDetails details = corporateActionService
                .loadCorporateActionDetails(electionDetailsDto.getKey().getId(), serviceErrors).getCorporateActionDetailsList()
                .iterator().next();

        CorporateActionContext context = new CorporateActionContext();
        context.setCorporateActionDetails(details);
        context.setDealerGroup(corporateActionCommonService.getUserProfileService().isDealerGroup());
        context.setInvestmentManager(corporateActionCommonService.getUserProfileService().isInvestmentManager());

        if (validateOptions(electionDetailsDto, context, serviceErrors)) {
            result = submitElections(context, electionDetailsDto);
        } else {
            // Delete all as the entries are no longer valid
            corporateActionPersistenceDtoService.deleteDraftParticipation(electionDetailsDto.getKey().getId());

            result = new CorporateActionElectionDetailsDto(CorporateActionResponseCode.OPTIONS_CHANGED, 0, 0, null,
                    "Unable to submit corporate action elections because the available options have changed.");
        }

        // Remove any previously saved expired elections to keep the database as clean as possible
        // Note: monitor performance
        corporateActionPersistenceDtoService.deleteExpiredDraftParticipations();
        return result;
    }

    private CorporateActionElectionDetailsBaseDto submitElections(CorporateActionContext context,
                                                                  CorporateActionElectionDetailsBaseDto electionDetailsDto) {
        CorporateActionRequestConverterService requestConverter =
                corporateActionConverterFactory.getRequestConverterService(context.getCorporateActionDetails());

        CorporateActionElectionDetailsDto electionDetails = (CorporateActionElectionDetailsDto) electionDetailsDto;

        int accountCount = 0;
        int successCount = 0;

        final Collection<CorporateActionElectionGroup> electionGroups = requestConverter.createElectionGroups(context, electionDetails);

        List<CorporateActionElectionResultDto> electionResults = corporateActionValidationConverter.createElectionResults();
        boolean submitErrors = false;
        boolean validationErrors = false;

        for (CorporateActionElectionGroup electionGroup : electionGroups) {
            final ServiceErrors groupServiceErrors = corporateActionValidationConverter.createServiceErrors();
            accountCount += electionGroup.getPositions().size();

            CorporateActionElectionGroup response = corporateActionElectionService.submitElectionGroup(electionGroup,
                    groupServiceErrors);

            // Handle unknown service errors
            if (groupServiceErrors.hasErrors()) {
                submitErrors = true;
            } else if (response.getValidationErrors() != null) {
                List<CorporateActionElectionResultDto> electionResultDtoList = corporateActionValidationConverter
                        .toElectionResultDtoList(electionGroup, electionDetails, response.getValidationErrors());
                electionResults.addAll(electionResultDtoList);
                validationErrors = true;
                successCount += electionGroup.getPositions().size() - response.getValidationErrors().size();
            } else {
                List<CorporateActionElectionResultDto> electionResultDtoList = corporateActionValidationConverter
                        .toElectionResultDtoList(electionGroup, electionDetails, response.getValidationErrors());
                electionResults.addAll(electionResultDtoList);
                successCount += electionGroup.getPositions().size();
            }
        }

        corporateActionPersistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetails, electionResults);

        String errorText = profileService.isInvestor() ? CMS_SYSTEM_ERROR_ID_DIR_INV : CMS_SYSTEM_ERROR_ID;

        if (submitErrors) {
            return new CorporateActionElectionDetailsDto(CorporateActionResponseCode.ERROR, successCount, accountCount,
                    electionResults, successCount == 0 ? corporateActionValidationConverter.getCmsText(errorText) : null);
        }

        return new CorporateActionElectionDetailsDto(!validationErrors ? CorporateActionResponseCode.SUCCESS
                                                                       : CorporateActionResponseCode.ERROR, successCount, accountCount,
                electionResults, null);
    }

    private boolean validateOptions(CorporateActionElectionDetailsBaseDto electionDetailsDto, CorporateActionContext context,
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
}
