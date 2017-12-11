package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.core.repository.CorporateActionDraftAccountElectionImpl;
import com.bt.nextgen.core.repository.CorporateActionDraftParticipationImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipationRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@Service
public class CorporateActionPersistenceDtoServiceImpl implements CorporateActionPersistenceDtoService {
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("corporateActionDraftParticipationRepository")
    private CorporateActionSavedParticipationRepository draftParticipationRepository;

    /**
     * Save the selected option for each account into the database
     *
     * @param corporateActionPersistenceDto the complete CorporateActionPersistenceDto object
     * @param serviceErrors                 the service errors
     * @return corporateActionPersistenceDto
     */
    @Override
    public CorporateActionPersistenceDto submit(CorporateActionPersistenceDto corporateActionPersistenceDto, ServiceErrors serviceErrors) {
        return saveCorporateActionElections(corporateActionPersistenceDto);
    }

    private CorporateActionPersistenceDto saveCorporateActionElections(CorporateActionPersistenceDto corporateActionPersistenceDto) {
        String oeId = userProfileService.getPositionId();

        CorporateActionSavedParticipation savedParticipation =
                draftParticipationRepository.find(oeId, corporateActionPersistenceDto.getKey().getId());

        if (savedParticipation != null) {
            savedParticipation.setExpiryDate(corporateActionPersistenceDto.getCloseDate().toDate());
            updateSavedAccounts(corporateActionPersistenceDto, savedParticipation);
            updateSavedPortfolioModels(corporateActionPersistenceDto, savedParticipation);
            draftParticipationRepository.update(savedParticipation);
        } else {
            savedParticipation = createDraftParticipation(oeId, corporateActionPersistenceDto);
            draftParticipationRepository.insert(savedParticipation);
        }

        return new CorporateActionPersistenceDto(CorporateActionResponseCode.SUCCESS);
    }

    private void updateSavedAccounts(CorporateActionPersistenceDto corporateActionPersistenceDto,
                                     CorporateActionSavedParticipation savedParticipation) {
        if (corporateActionPersistenceDto.getAccounts() != null) {
            for (CorporateActionAccountDetailsDto account : corporateActionPersistenceDto.getAccounts()) {
                CorporateActionSavedAccount savedAccount = selectFirst(
                        savedParticipation.getAccounts(),
                        having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(account.getAccountId())));

                if (savedAccount == null) {
                    savedAccount = savedParticipation.addAccount(account.getAccountId());
                }

                savedAccount.setMinimumPriceId(account.getSelectedElections().getMinimumPriceId());
                setDraftAccountElections(savedAccount, corporateActionPersistenceDto, account.getSelectedElections().getOptions());
            }
        }
    }

    private void updateSavedPortfolioModels(CorporateActionPersistenceDto corporateActionPersistenceDto,
                                            CorporateActionSavedParticipation savedParticipation) {
        if (corporateActionPersistenceDto.getPortfolioModels() != null) {
            for (ImCorporateActionPortfolioModelDto portfolioModel : corporateActionPersistenceDto.getPortfolioModels()) {
                if (portfolioModel.getSelectedElections() != null) {
                    CorporateActionSavedAccount savedAccount = selectFirst(
                            savedParticipation.getAccounts(),
                            having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(),
                                    equalTo(portfolioModel.getIpsId())));

                    if (savedAccount == null) {
                        savedAccount = savedParticipation.addAccount(portfolioModel.getIpsId());
                    }

                    savedAccount.setMinimumPriceId(portfolioModel.getSelectedElections().getMinimumPriceId());
                    setDraftAccountElections(savedAccount, corporateActionPersistenceDto, portfolioModel.getSelectedElections()
                                                                                                        .getOptions());
                }
            }
        }
    }

    private CorporateActionSavedParticipation createDraftParticipation(String oeId,
                                                                       CorporateActionPersistenceDto corporateActionPersistenceDto) {
        CorporateActionSavedParticipation savedParticipation = CorporateActionDraftParticipationImpl
                .create(oeId, corporateActionPersistenceDto.getKey().getId(), corporateActionPersistenceDto.getCloseDate().toDate());

        if (corporateActionPersistenceDto.getAccounts() != null) {
            for (CorporateActionAccountDetailsDto account : corporateActionPersistenceDto.getAccounts()) {
                CorporateActionSavedAccount savedAccount = savedParticipation.addAccount(account.getAccountId());
                savedAccount.setMinimumPriceId(account.getSelectedElections().getMinimumPriceId());
                setDraftAccountElections(savedAccount, corporateActionPersistenceDto, account.getSelectedElections().getOptions());
            }
        }

        if (corporateActionPersistenceDto.getPortfolioModels() != null) {
            for (ImCorporateActionPortfolioModelDto portfolioModel : corporateActionPersistenceDto.getPortfolioModels()) {
                if (portfolioModel.getSelectedElections() != null) {
                    CorporateActionSavedAccount savedAccount = savedParticipation.addAccount(portfolioModel.getIpsId());
                    savedAccount.setMinimumPriceId(portfolioModel.getSelectedElections().getMinimumPriceId());
                    setDraftAccountElections(savedAccount, corporateActionPersistenceDto, portfolioModel.getSelectedElections()
                            .getOptions());
                }
            }
        }

        return savedParticipation;
    }

    private void setDraftAccountElections(CorporateActionSavedAccount savedAccount,
                                          CorporateActionPersistenceDto corporateActionPersistenceDto,
                                          List<CorporateActionSelectedOptionDto> options) {
        // Handle CA's with only one election - update instead of clearing to reuse sequence
        if (options.size() == 1 && savedAccount.getAccountElections() != null && savedAccount.getAccountElections().size() == 1) {
            CorporateActionDraftAccountElectionImpl accountElection =
                    (CorporateActionDraftAccountElectionImpl) savedAccount.getAccountElections().get(0);
            CorporateActionSelectedOptionDto selectedOptionDto = options.get(0);

            accountElection.getKey().setOptionId(selectedOptionDto.getOptionId());
            accountElection.setUnits(selectedOptionDto.getUnits());
            accountElection.setPercent(selectedOptionDto.getPercent());
            accountElection.setOversubscribe(selectedOptionDto.getOversubscribe());
            accountElection
                    .setOptionHash(getElectionOptionHash(corporateActionPersistenceDto.getOptions(), selectedOptionDto.getOptionId()));
        } else {
            if (savedAccount.getAccountElections() != null) {
                savedAccount.getAccountElections().clear();
            }

            for (CorporateActionSelectedOptionDto selectedOptionDto : options) {
                CorporateActionDraftAccountElectionImpl accountElection =
                        (CorporateActionDraftAccountElectionImpl) savedAccount.addAccountElection(selectedOptionDto.getOptionId(),
                                selectedOptionDto.getUnits(), selectedOptionDto.getPercent(), selectedOptionDto.getOversubscribe());

                accountElection
                        .setOptionHash(getElectionOptionHash(corporateActionPersistenceDto.getOptions(), selectedOptionDto.getOptionId()));
            }
        }
    }

    /**
     * Load previously saved elected options if possible.  If any options have been changed then an warning flag is set.
     *
     * @param orderNumber the corporate action order number
     * @param options     the available options
     * @return a copy of the dto object with elected options restored from previous save, if no changes to the available options had
     * occurred. If saved selection is no longer valid, the status flag in CorporateActionDetailsDto will be set to "OPTIONS_CHANGED"
     */
    @Override
    public CorporateActionSavedDetails loadAndValidateElectedOptions(String orderNumber, List<CorporateActionOptionDto> options) {
        CorporateActionSavedParticipation savedParticipation =
                draftParticipationRepository.find(userProfileService.getPositionId(), orderNumber);

        // No data saved
        if (savedParticipation == null) {
            return new CorporateActionSavedDetails(CorporateActionResponseCode.NO_SAVED_DATA);
        }

        Map<Integer, String> optionHashMap = new HashMap<>();

        // Build a list of hash from summary text for matching purposes
        for (CorporateActionOptionDto option : options) {
            optionHashMap.put(option.getId(), generateHash(option.getSummary()));
        }

        // Check to see if any of the saved options are still valid (ie. options have not changed)
        for (CorporateActionSavedAccount savedAccount : savedParticipation.getAccounts()) {
            for (CorporateActionSavedAccountElection savedAccountElection : savedAccount.getAccountElections()) {
                if (!((CorporateActionDraftAccountElectionImpl) savedAccountElection).getOptionHash().equals(optionHashMap.get(
                        savedAccountElection.getKey().getOptionId()))) {
                    return new CorporateActionSavedDetails(CorporateActionResponseCode.OPTIONS_CHANGED);
                }
            }
        }

        return new CorporateActionSavedDetails(CorporateActionResponseCode.SUCCESS, savedParticipation);
    }

    /**
     * Delete draft elections based on the given oe ID and order number
     *
     * @param orderNumber the order number
     * @Return number of records deleted
     */
    @Override
    public int deleteDraftParticipation(String orderNumber) {
        CorporateActionSavedParticipation savedParticipation =
                draftParticipationRepository.find(userProfileService.getPositionId(), orderNumber);

        return savedParticipation != null ? draftParticipationRepository.delete(savedParticipation) : 0;
    }

    /**
     * Database cleanup - delete all expired participation
     *
     * @return number of records deleted
     */
    public int deleteExpiredDraftParticipations() {
        return draftParticipationRepository.deleteAllExpired(userProfileService.getPositionId());
    }

    /**
     * Delete draft elections based on user ID and accounts that had not failed during submission
     *
     * @param electionDetailsDto the election details
     * @param electionResults    list of failed elections, which will be used as a filter
     */
    @Override
    public int deleteSuccessfulDraftAccountElections(CorporateActionElectionDetailsDto electionDetailsDto,
                                                     List<CorporateActionElectionResultDto> electionResults) {
        CorporateActionSavedParticipation savedParticipation =
                draftParticipationRepository.find(userProfileService.getPositionId(), electionDetailsDto.getKey().getId());

        if (savedParticipation != null && processRemoval(electionDetailsDto, electionResults, savedParticipation)) {
            if (hasDraftAccountElections(savedParticipation)) {
                draftParticipationRepository.update(savedParticipation);
            } else {
                // Delete the whole participation record since there is no account/elections remaining
                draftParticipationRepository.delete(savedParticipation);
            }

            return 1;
        }

        return 0;
    }

    /**
     * Delete draft elections based on user ID and models/accounts that had not failed during submission
     *
     * @param electionDetailsDto the election details
     * @param electionResults    list of failed elections, which will be used as a filter
     */
    @Override
    public int deleteSuccessfulDraftElectionsForDg(ImCorporateActionElectionDetailsDto electionDetailsDto,
                                                   List<CorporateActionElectionResultDto> electionResults) {
        CorporateActionSavedParticipation savedParticipation = draftParticipationRepository.find(
                userProfileService.getPositionId(), electionDetailsDto.getKey().getId());

        if (savedParticipation != null && processRemovalForDg(electionDetailsDto, electionResults, savedParticipation)) {
            if (hasDraftAccountElections(savedParticipation)) {
                draftParticipationRepository.update(savedParticipation);
            } else {
                // Delete the whole participation record since there is no account/elections remaining
                draftParticipationRepository.delete(savedParticipation);
            }

            return 1;
        }

        return 0;
    }

    private boolean processRemoval(CorporateActionElectionDetailsDto electionDetailsDto,
                                   List<CorporateActionElectionResultDto> electionResults,
                                   CorporateActionSavedParticipation savedParticipation) {
        boolean update = false;

        if (!electionResults.isEmpty()) {
            for (CorporateActionAccountDetailsDto account : electionDetailsDto.getAccounts()) {
                CorporateActionElectionResultDto success = selectFirst(electionResults,
                        having(on(CorporateActionElectionResultDto.class).getAccountId(), equalTo(account.getAccountId()))
                                .and(having(on(CorporateActionElectionResultDto.class).getStatus(),
                                        equalTo(CorporateActionValidationStatus.SUCCESS))));

                if (success != null) {
                    update = update || removeDraftAccount(account, savedParticipation);
                }
            }
        } else {
            for (CorporateActionAccountDetailsDto account : electionDetailsDto.getAccounts()) {
                update = update || removeDraftAccount(account, savedParticipation);
            }
        }

        return update;
    }

    private boolean processRemovalForDg(ImCorporateActionElectionDetailsDto electionDetailsDto,
                                        List<CorporateActionElectionResultDto> electionResults,
                                        CorporateActionSavedParticipation savedParticipation) {
        boolean update = false;

        update = updateModel(electionDetailsDto, savedParticipation);

        if (!electionResults.isEmpty()) {
            // Remove saved accounts that were submitted successfully
            if (electionDetailsDto.getAccounts() != null) {
                for (CorporateActionAccountDetailsDto account : electionDetailsDto.getAccounts()) {
                    CorporateActionElectionResultDto success = selectFirst(
                            electionResults,
                            having(on(CorporateActionElectionResultDto.class).getAccountId(), equalTo(account.getAccountId()))
                                    .and(having(on(CorporateActionElectionResultDto.class).getStatus(),
                                            equalTo(CorporateActionValidationStatus.SUCCESS))));

                    if (success != null) {
                        update = update || removeDraftAccount(account, savedParticipation);
                    }
                }
            }
        } else {
            if (electionDetailsDto.getAccounts() != null) {
                for (CorporateActionAccountDetailsDto account : electionDetailsDto.getAccounts()) {
                    update = update || removeDraftAccount(account, savedParticipation);
                }
            }
        }

        return update;
    }

    private boolean updateModel(ImCorporateActionElectionDetailsDto electionDetailsDto,
                                CorporateActionSavedParticipation savedParticipation) {
        // Remove saved model
        if (electionDetailsDto.getPortfolioModels() != null) {
            ImCorporateActionPortfolioModelDto portfolioModel = electionDetailsDto.getPortfolioModels().get(0);
            return removeDraftModel(portfolioModel, savedParticipation);
        }
        return false;
    }

    /**
     * Method to check if there are any election remains in the participation object
     *
     * @param savedParticipation the loaded draft participation
     * @return true if there are draft account elections, otherwise false.
     */
    private boolean hasDraftAccountElections(CorporateActionSavedParticipation savedParticipation) {
        for (CorporateActionSavedAccount savedAccount : savedParticipation.getAccounts()) {
            if (!savedAccount.getAccountElections().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Remove draft account from the entity list
     *
     * @param account            the account to delete
     * @param savedParticipation the entity object
     * @return true if it was remove, else false.
     */
    private boolean removeDraftAccount(CorporateActionAccountDetailsDto account,
                                       CorporateActionSavedParticipation savedParticipation) {
        CorporateActionSavedAccount savedAccount = selectFirst(savedParticipation.getAccounts(),
                having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(account.getAccountId())));

        if (savedAccount != null) {
            savedAccount.getAccountElections().clear();
            savedParticipation.getAccounts().remove(savedAccount);
            return true;
        }

        return false;
    }

    /**
     * Remove draft model from the entity list
     *
     * @param portfolioModel     the portfolio model to delete
     * @param savedParticipation the entity object
     * @return true if it was remove, else false.
     */
    private boolean removeDraftModel(ImCorporateActionPortfolioModelDto portfolioModel,
                                     CorporateActionSavedParticipation savedParticipation) {
        CorporateActionSavedAccount savedModel = selectFirst(savedParticipation.getAccounts(),
                having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(portfolioModel.getIpsId())));

        if (savedModel != null) {
            savedModel.getAccountElections().clear();
            savedParticipation.getAccounts().remove(savedModel);
            return true;
        }

        return false;
    }

    /**
     * Generates the option hash based on summary
     *
     * @param options          the list of options
     * @param selectedOptionId the selected option ID (*not* element index)
     * @return hashed string
     */
    private String getElectionOptionHash(List<CorporateActionOptionDto> options, Integer selectedOptionId) {
        CorporateActionOptionDto option =
                selectFirst(options, having(on(CorporateActionOptionDto.class).getId(), IsEqual.equalTo(selectedOptionId)));

        return generateHash(option.getSummary());
    }

    /**
     * Generate a unique hash for the option.  Currently it is just a bas 64 encoded string of the summary.  We could just store the summary
     * text without encoding...
     *
     * @param str the string to encode
     * @return encoded base64 string
     */
    private String generateHash(String str) {
        return Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8));
    }
}
