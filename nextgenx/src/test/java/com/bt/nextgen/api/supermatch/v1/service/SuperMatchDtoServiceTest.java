package com.bt.nextgen.api.supermatch.v1.service;

import com.bt.nextgen.api.supermatch.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.api.supermatch.v1.util.SuperMatchDtoHelper;
import com.bt.nextgen.api.supermatch.v1.validation.SuperMatchErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.btesb.supermatch.model.FundCategory;
import com.bt.nextgen.service.btesb.supermatch.model.MemberImpl;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import com.bt.nextgen.service.integration.supermatch.SuperMatchIntegrationService;
import com.bt.nextgen.service.integration.supermatch.SuperMatchResponseHolder;
import com.bt.nextgen.service.integration.supernotification.SuperNotificationIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuperMatchDtoServiceTest {

    @InjectMocks
    private SuperMatchDtoServiceImpl superMatchDtoService;

    @Mock
    private SuperMatchIntegrationService superMatchIntegrationService;

    @Mock
    private SuperNotificationIntegrationService superNotificationIntegrationService;

    @Mock
    private SuperMatchDtoHelper superMatchDtoHelper;

    @Mock
    private SuperMatchErrorMapper errorMapper;

    private ServiceErrors serviceErrors;
    private List<SuperMatchDetails> superMatchDetailList;
    private List<SuperFundAccount> superFundAccounts;
    private List<SuperMatchFundDto> superMatchFundDtos;
    private List<SuperMatchDetails> superMatchDetails;

    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        superFundAccounts = getSuperFundAccounts();
        superMatchDetailList = getSuperMatchDetailList();

        superMatchFundDtos = getSuperMatchFundDtoList();

        superMatchDetails = createSuperMatchDetails().getSuperMatchDetails();
        when(superMatchIntegrationService.retrieveSuperDetails(Mockito.anyString(), any(SuperFundAccount.class), any(ServiceErrors.class))).thenReturn(superMatchDetails);

        Member member1 = new MemberImpl();
        ((MemberImpl) member1).setCustomerId("123");
        ((MemberImpl) member1).setIssuer("Westpaclegacy");

        SuperFundAccount superfundAccount = mock(SuperFundAccount.class);
        when(superfundAccount.getAccountNumber()).thenReturn("123");
        when(superfundAccount.getUsi()).thenReturn("112233");
        when(superfundAccount.getMembers()).thenReturn(Collections.singletonList(member1));

        Mockito.when(superMatchDtoHelper.getSuperFundAccount(anyString(), any(SuperMatchDtoKey.class), any(ServiceErrors.class))).thenReturn(superfundAccount);

        ClientDetail clientDetail = mock(IndividualDetailImpl.class);
        Mockito.when(clientDetail.getClientKey()).thenReturn(ClientKey.valueOf("1234"));
        Mockito.when(clientDetail.getFirstName()).thenReturn("John");
        Mockito.when(clientDetail.getLastName()).thenReturn("Smith");
        Mockito.when(clientDetail.getDateOfBirth()).thenReturn(new DateTime("2017-01-01"));
        Mockito.when(((Individual) clientDetail).getCISKey()).thenReturn(CISKey.valueOf("147852"));
        Mockito.when(superMatchDtoHelper.getClient(any(ServiceErrors.class))).thenReturn(clientDetail);

        Mockito.when(superMatchDtoHelper.createUpdateRollOverRequest(any(SuperMatchDto.class))).thenReturn(superFundAccounts);
    }

    @Test
    public void searchSuperDetails() {
        SuperMatchDtoKey key = new SuperMatchDtoKey(EncodedString.fromPlainText("123456").toString());
        SuperMatchDto superMatchDetails = superMatchDtoService.find(key, serviceErrors);

        assertEquals(superMatchDetails.isConsentProvided(), true);
        assertEquals(EncodedString.toPlainText(superMatchDetails.getKey().getAccountId()), "123456");
        assertEquals(superMatchDetails.getSuperMatchFundList().size(), 1);

        SuperMatchFundDto superMatchFund = superMatchDetails.getSuperMatchFundList().get(0);
        assertEquals(superMatchFund.getAccountNumber(), "123456");
        assertEquals(superMatchFund.getBalance(), BigDecimal.valueOf(1000).setScale(2));
        assertEquals(superMatchFund.getInsuranceCovered(), true);
        assertEquals(superMatchFund.getRolloverDetails().get(0).getRolloverStatus(), true);
    }

    @Test
    public void update() {
        SuperMatchDto reqSuperMatchDto = new SuperMatchDto();

        // Consent
        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), "consent"));
        when(superMatchIntegrationService.updateConsentStatus(Mockito.anyString(), any(SuperFundAccount.class), anyBoolean(), any(ServiceErrors.class))).thenReturn(superMatchDetails);
        SuperMatchDto result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);
        assertNotNull(result);

        // acknowledge
        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), "acknowledge"));
        when(superMatchIntegrationService.updateAcknowledgementStatus(Mockito.anyString(), any(SuperFundAccount.class), any(ServiceErrors.class))).thenReturn(superMatchDetails);
        result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);
        assertNotNull(result);

        // rollover
        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), "rollover"));
        when(superMatchIntegrationService.updateRollOverStatus(Mockito.anyString(), any(SuperFundAccount.class), anyList(), any(ServiceErrors.class))).thenReturn(superMatchDetails);
        result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);
        assertNotNull(result);

        // create
        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), "create"));
        when(superMatchIntegrationService.createMember(Mockito.anyString(), any(SuperFundAccount.class), any(ServiceErrors.class))).thenReturn(true);
        result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);
        assertNotNull(result);

        // invalid input
        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), null));
        result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);
        assertNull(result);
    }

    @Test
    public void update_withError() {
        SuperMatchDto reqSuperMatchDto = new SuperMatchDto();
        DomainApiErrorDto domainError = mock(DomainApiErrorDto.class);
        when(domainError.getErrorId()).thenReturn("Error");

        reqSuperMatchDto.setKey(new SuperMatchDtoKey(EncodedString.fromPlainText("123").toString(), "consent"));
        when(superMatchIntegrationService.createMember(Mockito.anyString(), any(SuperFundAccount.class), any(ServiceErrors.class))).thenReturn(true);
        when(errorMapper.map(anyList())).thenReturn(Collections.singletonList(domainError));

        SuperMatchDto result = superMatchDtoService.update(reqSuperMatchDto, serviceErrors);

        assertEquals(result.getErrors().size(), 1);
    }

    @Test
    public void notifyCustomer() {
        when(superNotificationIntegrationService.notifyCustomer(anyString(), any(SuperFundAccount.class), any(ServiceErrors.class))).thenReturn(true);
        assertTrue(superMatchDtoService.notifyCustomer(EncodedString.fromPlainText("123456").toString(), "ab@cd.com", serviceErrors));
    }

    private SuperMatchResponseHolder createSuperMatchDetails() {
        SuperMatchResponseHolder responseHolder = mock(SuperMatchResponseHolder.class);
        Mockito.when(responseHolder.getStatus()).thenReturn("Success");
        Mockito.when(responseHolder.getSuperMatchDetails()).thenReturn(superMatchDetailList);

        return responseHolder;
    }

    private List<SuperMatchDetails> getSuperMatchDetailList() {
        superMatchDetailList = new ArrayList<>();
        SuperMatchDetails superMatchDetails = mock(SuperMatchDetails.class);
        StatusSummary statusSummary = mock(StatusSummary.class);
        Mockito.when(statusSummary.isConsentStatusProvided()).thenReturn(true);
        Mockito.when(superMatchDetails.getStatusSummary()).thenReturn(statusSummary);
        Mockito.when(superMatchDetails.getSuperFundAccounts()).thenReturn(superFundAccounts);

        superMatchDetailList.add(superMatchDetails);
        return superMatchDetailList;
    }

    private List<SuperFundAccount> getSuperFundAccounts() {
        superFundAccounts = new ArrayList<>();
        SuperFundAccount superFundAccount = mock(SuperFundAccount.class);
        Mockito.when(superFundAccount.getAbn()).thenReturn("123456");
        Mockito.when(superFundAccount.getAccountNumber()).thenReturn("123456");
        Mockito.when(superFundAccount.getFundCategory()).thenReturn(FundCategory.PARTIALLY_ROLLOVERED);
        Mockito.when(superFundAccount.getAccountBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(superFundAccount.getInsuranceIndicator()).thenReturn(true);
        Mockito.when(superFundAccount.getRolloverStatus()).thenReturn(true);

        superFundAccounts.add(superFundAccount);
        return superFundAccounts;
    }

    private List<SuperMatchFundDto> getSuperMatchFundDtoList() {
        superMatchFundDtos = new ArrayList<>();
        SuperMatchDtoKey key = mock(SuperMatchDtoKey.class);
        Mockito.when(key.getAccountId()).thenReturn("123233");

        SuperMatchFundDto superMatchFundDto = mock(SuperMatchFundDto.class);
        Mockito.when(superMatchFundDto.getAccountNumber()).thenReturn("147852");
        Mockito.when(superMatchFundDto.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(superMatchFundDto.getInsuranceCovered()).thenReturn(true);

        RolloverDetailsDto rolloverDetail = mock(RolloverDetailsDto.class);
        when(rolloverDetail.getRolloverId()).thenReturn("1");
        when(rolloverDetail.getRolloverAmount()).thenReturn(BigDecimal.TEN);
        when(rolloverDetail.getRolloverStatus()).thenReturn(true);
        Mockito.when(superMatchFundDto.getRolloverDetails()).thenReturn(Collections.singletonList(rolloverDetail));

        superMatchFundDtos.add(superMatchFundDto);
        return superMatchFundDtos;
    }
}