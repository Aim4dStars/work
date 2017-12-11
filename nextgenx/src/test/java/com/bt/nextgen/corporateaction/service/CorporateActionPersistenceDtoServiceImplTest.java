package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionDraftAccountElectionImpl;
import com.bt.nextgen.core.repository.CorporateActionDraftParticipationImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipationRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionPersistenceDtoServiceImplTest {
    @InjectMocks
    private CorporateActionPersistenceDtoServiceImpl persistenceDtoService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    @Qualifier("corporateActionDraftParticipationRepository")
    private CorporateActionSavedParticipationRepository draftParticipationRepository;

    private CorporateActionSavedParticipation savedParticipation;

    @Before
    public void setup() {
        savedParticipation = CorporateActionDraftParticipationImpl.create("0", "0", new Date());
        CorporateActionSavedAccount savedAccount = savedParticipation.addAccount("0");
        CorporateActionDraftAccountElectionImpl savedAccountElection =
                (CorporateActionDraftAccountElectionImpl) savedAccount.addAccountElection(1, null, null, null);
        savedAccountElection.setOptionHash("VGVzdA==");

        when(userProfileService.getUserId()).thenReturn("xxx");

        when(draftParticipationRepository.update(any(CorporateActionSavedParticipation.class))).thenReturn(savedParticipation);

        when(draftParticipationRepository.insert(any(CorporateActionSavedParticipation.class))).thenReturn(savedParticipation);

        when(draftParticipationRepository.delete(any(CorporateActionSavedParticipation.class))).thenReturn(1);

        when(draftParticipationRepository.deleteAllExpired(Mockito.anyString())).thenReturn(1);
    }

    @Test
    public void testSaveCorporateActionElection_whenThereIsExistingDraftElection_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", CorporateActionSelectedOptionsDto.createSingleAccountElection(1,
                null, null, null)));

        List<ImCorporateActionPortfolioModelDto> portfolioModels = new ArrayList<>();
        portfolioModels.add(new ImCorporateActionPortfolioModelDto(CorporateActionSelectedOptionsDto.createSingleAccountElection(1, null,
                null, null)));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts,
                portfolioModels, null, false);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElection_whenTheAccountWasNotSaved_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("1", "1", "1", CorporateActionSelectedOptionsDto.createSingleAccountElection(1,
                null, null, null)));

        List<ImCorporateActionPortfolioModelDto> portfolioModels = new ArrayList<>();
        portfolioModels.add(new ImCorporateActionPortfolioModelDto(CorporateActionSelectedOptionsDto.createSingleAccountElection(
                1, null, null, null)));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts,
                portfolioModels, null, false);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElection_whenThereIsNoExistingDraftElection_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0",
                CorporateActionSelectedOptionsDto.createSingleAccountElection(1, null, null, null)));

        List<ImCorporateActionPortfolioModelDto> portfolioModels = new ArrayList<>();
        portfolioModels.add(new ImCorporateActionPortfolioModelDto(CorporateActionSelectedOptionsDto.createSingleAccountElection(
                1, null, null, null)));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts,
                portfolioModels, null, false);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElections_whenThereAreExistingDraftElections_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(
                savedParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0",
                CorporateActionSelectedOptionsDto.createSingleAccountElection(1, null, null, null)));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts, null, null, true);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElections_whenThereNoExistingDraftElections_thenReturnSuccess() {
        CorporateActionSavedParticipation emptyDraftParticipation =
                CorporateActionDraftParticipationImpl.create("0", "0", new Date());
        emptyDraftParticipation.addAccount("0");

        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(
                emptyDraftParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0",
                CorporateActionSelectedOptionsDto.createSingleAccountElection(1, null, null, null)));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts, null, null, true);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElections_whenNoAccountsOrModels_thenReturnSuccess() {
        CorporateActionSavedParticipation emptyDraftParticipation = CorporateActionDraftParticipationImpl.create("0", "0", new Date());

        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(emptyDraftParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> portfolioModels = new ArrayList<>();

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts, portfolioModels,
                null, true);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(result.getStatus(), CorporateActionResponseCode.SUCCESS);

        dto = new CorporateActionPersistenceDto("0", new DateTime(), options, null, portfolioModels,
                null, true);

        result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testSaveCorporateActionElections_whenNoElectionOnModel_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", CorporateActionSelectedOptionsDto
                .createSingleAccountElection(1, null, null, null)));

        List<ImCorporateActionPortfolioModelDto> portfolioModels = new ArrayList<>();
        portfolioModels.add(new ImCorporateActionPortfolioModelDto((CorporateActionSelectedOptionsDto) null));

        CorporateActionPersistenceDto dto = new CorporateActionPersistenceDto("0", new DateTime(), options, accounts,
                portfolioModels, null, true);

        CorporateActionPersistenceDto result = persistenceDtoService.submit(dto, null);

        assertEquals(result.getStatus(), CorporateActionResponseCode.SUCCESS);

        dto = new CorporateActionPersistenceDto("0", new DateTime(), options, null, portfolioModels, null, true);

        result = persistenceDtoService.submit(dto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, result.getStatus());
    }

    @Test
    public void testLoadAndValidateElectedOptions_whenNormalLoad_thenReturnSuccess() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(
                savedParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test", Boolean.FALSE));

        CorporateActionSavedDetails elections = persistenceDtoService.loadAndValidateElectedOptions("0", options);

        assertEquals(CorporateActionResponseCode.SUCCESS, elections.getResponseCode());
    }

    @Test
    public void testLoadAndValidateElectedOptions_whenOptionsChanged_thenReturnOptionsChanged() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        List<CorporateActionOptionDto> options = new ArrayList<>();
        options.add(new CorporateActionOptionDto(1, "Test new", Boolean.FALSE));

        CorporateActionSavedDetails elections = persistenceDtoService.loadAndValidateElectedOptions("0", options);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, elections.getResponseCode());
    }

    @Test
    public void testLoadAndValidateElectedOptions_whenNothingSaved_thenReturn_NO_SAVED_DATA_Status() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        CorporateActionSavedDetails elections = persistenceDtoService.loadAndValidateElectedOptions("0", null);

        assertEquals(CorporateActionResponseCode.NO_SAVED_DATA, elections.getResponseCode());
    }

    @Test
    public void testDeleteSuccessfulDraftAccountElections_whenThereIsNoSavedParticipation_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", null));

        CorporateActionElectionDetailsDto electionDetailsDto = new CorporateActionElectionDetailsDto("0", null, accounts, null);

        int res = persistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetailsDto, null);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteSuccessfulDraftAccountElections_whenThereIsNoMatchingSavedParticipationToDelete_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("1", "1", "0", null));

        CorporateActionElectionDetailsDto electionDetailsDto = new CorporateActionElectionDetailsDto("0", null, accounts, null);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        electionResults.add(new CorporateActionElectionResultDto("0", CorporateActionValidationStatus.SUCCESS, null));

        int res = persistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetailsDto, electionResults);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteSuccessfulDraftAccountElections_whenThereIsNoElectionResults_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("1", "1", "0", null));

        CorporateActionElectionDetailsDto electionDetailsDto = new CorporateActionElectionDetailsDto("0", null, accounts, null);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();

        int res = persistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetailsDto, electionResults);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteSuccessfulDraftAccountElections_whenThereIsMatchingSavedParticipationToDelete_thenReturnOne() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(
                savedParticipation);

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", null));

        CorporateActionElectionDetailsDto electionDetailsDto = new CorporateActionElectionDetailsDto("0", null, accounts, null);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        electionResults.add(new CorporateActionElectionResultDto("0", CorporateActionValidationStatus.SUCCESS, null));

        int res = persistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetailsDto, electionResults);

        assertEquals(1, res);
    }

    @Test
    public void testDeleteSuccessfulDraftAccountElections_whenThereAreDraftAccountElections_thenReturnOne() {
        CorporateActionSavedParticipation savedParticipation = CorporateActionDraftParticipationImpl.create("0", "0", new Date());
        CorporateActionSavedAccount savedAccount1 = savedParticipation.addAccount("0");
        CorporateActionSavedAccount savedAccount2 = savedParticipation.addAccount("1");
        CorporateActionSavedAccount savedAccount3 = savedParticipation.addAccount("2");

        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        CorporateActionSavedAccountElection accountElection = new CorporateActionDraftAccountElectionImpl(null, null, null, null, null);
        List<CorporateActionSavedAccountElection> accountElections1 = new ArrayList<>();
        List<CorporateActionSavedAccountElection> accountElections2 = new ArrayList<>();

        accountElections1.add(accountElection);
        accountElections2.add(accountElection);

        savedAccount1.setAccountElections(new ArrayList<CorporateActionSavedAccountElection>());
        savedAccount2.setAccountElections(accountElections2);
        savedAccount3.setAccountElections(accountElections1);

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", null));
        accounts.add(new CorporateActionAccountDetailsDto("0", "1", "0", null));
        accounts.add(new CorporateActionAccountDetailsDto("0", "2", "0", null));

        CorporateActionElectionDetailsDto electionDetailsDto = new CorporateActionElectionDetailsDto("0", null, accounts, null);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        electionResults.add(new CorporateActionElectionResultDto("0", CorporateActionValidationStatus.ERROR, null));
        electionResults.add(new CorporateActionElectionResultDto("1", CorporateActionValidationStatus.SUCCESS, null));
        electionResults.add(new CorporateActionElectionResultDto("2", CorporateActionValidationStatus.ERROR, null));

        int res = persistenceDtoService.deleteSuccessfulDraftAccountElections(electionDetailsDto, electionResults);

        assertEquals(1, res);
    }

    @Test
    public void testDeleteSuccessfulDraftElectionsForDg_whenThereAreNoAccounts_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        ImCorporateActionPortfolioModelDtoParams params = Mockito.mock(ImCorporateActionPortfolioModelDtoParams.class);
        when(params.getPortfolioModelId()).thenReturn("0");

        ImCorporateActionElectionDetailsDto electionDetailsDto = new ImCorporateActionElectionDetailsDto("0", null, null, null);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();

        int res = persistenceDtoService.deleteSuccessfulDraftElectionsForDg(electionDetailsDto, electionResults);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteSuccessfulDraftElectionsForDg_whenThereAreNoElectionResults_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        ImCorporateActionPortfolioModelDtoParams params = Mockito.mock(ImCorporateActionPortfolioModelDtoParams.class);
        when(params.getPortfolioModelId()).thenReturn("0");

        List<ImCorporateActionPortfolioModelDto> models = new ArrayList<>();
        models.add(new ImCorporateActionPortfolioModelDto(params));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("1", "1", "0", null));

        ImCorporateActionElectionDetailsDto electionDetailsDto = new ImCorporateActionElectionDetailsDto("0", null, models,
                accounts);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();

        int res = persistenceDtoService.deleteSuccessfulDraftElectionsForDg(electionDetailsDto, electionResults);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteSuccessfulDraftElectionsForDg_whenThereIsMatchingSavedParticipationToDelete_thenReturnOne() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(savedParticipation);

        ImCorporateActionPortfolioModelDtoParams params = Mockito.mock(ImCorporateActionPortfolioModelDtoParams.class);
        when(params.getPortfolioModelId()).thenReturn("0");
        when(params.getIpsId()).thenReturn("0");

        List<ImCorporateActionPortfolioModelDto> models = new ArrayList<>();
        models.add(new ImCorporateActionPortfolioModelDto(params));

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", null));

        ImCorporateActionElectionDetailsDto electionDetailsDto = new ImCorporateActionElectionDetailsDto("0", null, models,
                accounts);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        electionResults.add(new CorporateActionElectionResultDto("0", CorporateActionValidationStatus.SUCCESS, null));

        int res = persistenceDtoService.deleteSuccessfulDraftElectionsForDg(electionDetailsDto, electionResults);

        assertEquals(1, res);
    }

    @Test
    public void testDeleteSuccessfulDraftElectionsForDg_whenThereIsNoMatchingSavedParticipationToDelete_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        ImCorporateActionPortfolioModelDtoParams params = Mockito.mock(ImCorporateActionPortfolioModelDtoParams.class);
        when(params.getPortfolioModelId()).thenReturn("0");
        when(params.getIpsId()).thenReturn("0");

        List<CorporateActionAccountDetailsDto> accounts = new ArrayList<>();
        accounts.add(new CorporateActionAccountDetailsDto("0", "0", "0", null));

        ImCorporateActionElectionDetailsDto electionDetailsDto = new ImCorporateActionElectionDetailsDto("0", null, null,
                accounts);

        List<CorporateActionElectionResultDto> electionResults = new ArrayList<>();
        electionResults.add(new CorporateActionElectionResultDto("0", CorporateActionValidationStatus.SUCCESS, null));

        int res = persistenceDtoService.deleteSuccessfulDraftElectionsForDg(electionDetailsDto, electionResults);

        assertEquals(0, res);
    }

    @Test
    public void testDeleteDraftParticipation_whenThereIsSavedParticipationToDelete_thenReturnOne() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(
                savedParticipation);

        int res = persistenceDtoService.deleteDraftParticipation("0");

        assertEquals(1, res);
    }

    @Test
    public void testDeleteDraftParticipation_whenThereIsNoSavedParticipationToDelete_thenReturnZero() {
        when(draftParticipationRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        int res = persistenceDtoService.deleteDraftParticipation("0");

        assertEquals(0, res);
    }

    @Test
    public void testDeleteExpiredDraftParticipation_whenDelete_thenReturnOne() {
        assertEquals(1, persistenceDtoService.deleteExpiredDraftParticipations());
    }
}
