package com.bt.nextgen.api.supermatch.v1.util;

import com.bt.nextgen.api.supermatch.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.btesb.supermatch.model.MemberImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SuperMatchDtoHelperTest {

    @InjectMocks
    private SuperMatchDtoHelper helper;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();
    private ClientDetail clientDetail;

    @Before
    public void setUp() {
        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);
        when(accountDetail.getAccountNumber()).thenReturn("147852");

        when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(accountDetail);

        UserProfile profile = mock(UserProfile.class);
        when(profile.getClientKey()).thenReturn(ClientKey.valueOf("111111"));
        when(userProfileService.getActiveProfile()).thenReturn(profile);

        Product product = mock(Product.class);
        when(product.getParentProductKey()).thenReturn(ProductKey.valueOf("11111"));
        when(product.isSuper()).thenReturn(true);
        when(product.getProductUsi()).thenReturn("11111");

        clientDetail = mock(IndividualDetailImpl.class);
        when(clientDetail.getClientKey()).thenReturn(ClientKey.valueOf("1234"));
        when(clientDetail.getFirstName()).thenReturn("John");
        when(clientDetail.getLastName()).thenReturn("Smith");
        when(clientDetail.getDateOfBirth()).thenReturn(new DateTime("2017-01-01"));

        List<Email> emails = new ArrayList<>();
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn("something@do.com");
        emails.add(email);

        when(clientDetail.getEmails()).thenReturn(emails);
        when(((Individual) clientDetail).getCISKey()).thenReturn(CISKey.valueOf("147852"));

        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);
        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(clientDetail);
    }

    @Test
    public void getSuperFundAccount() {
        SuperFundAccount superfundAccount = helper.getSuperFundAccount("1234",
                new SuperMatchDtoKey(EncodedString.fromPlainText("12345").toString()), serviceErrors);
        assertEquals(superfundAccount.getAccountNumber(), "147852");
        assertEquals(superfundAccount.getUsi(), "11111");
        assertEquals(superfundAccount.getMembers().size(), 1);
        assertEquals(superfundAccount.getMembers().get(0).getCustomerId(), "1234");
        assertEquals(superfundAccount.getMembers().get(0).getIssuer(), "WestpacLegacy");
    }

    @Test
    public void createUpdateRollOverRequest() {
        List<SuperMatchFundDto> funds = new ArrayList<>();
        SuperMatchFundDto fund = mock(SuperMatchFundDto.class);
        when(fund.getAccountNumber()).thenReturn("1234");
        when(fund.getUsi()).thenReturn("1234567");


        RolloverDetailsDto rolloverDetail = mock(RolloverDetailsDto.class);
        when(rolloverDetail.getRolloverId()).thenReturn("1");
        when(rolloverDetail.getRolloverAmount()).thenReturn(BigDecimal.TEN);
        when(rolloverDetail.getRolloverStatus()).thenReturn(true);
        Mockito.when(fund.getRolloverDetails()).thenReturn(Collections.singletonList(rolloverDetail));

        funds.add(fund);

        SuperMatchDto superMatchDto = mock(SuperMatchDto.class);
        when(superMatchDto.getSuperMatchFundList()).thenReturn(funds);

        List<SuperFundAccount> rolloverFunds = helper.createUpdateRollOverRequest(superMatchDto);

        assertEquals(rolloverFunds.size(), 1);
        assertEquals(rolloverFunds.get(0).getAccountNumber(), "1234");
        assertEquals(rolloverFunds.get(0).getUsi(), "1234567");
        assertEquals(rolloverFunds.get(0).getRolloverStatus(), Boolean.TRUE);
        assertEquals(rolloverFunds.get(0).getRolloverAmount(), BigDecimal.TEN);
    }

    @Test
    public void getClient() {
        Client client = helper.getClient(serviceErrors);
        assertEquals(client.getClientKey(), ClientKey.valueOf("1234"));
        assertEquals(client.getFirstName(), "John");
        assertEquals(client.getLastName(), "Smith");
        assertEquals(client.getDateOfBirth(), new DateTime("2017-01-01"));
    }

    @Test
    public void setMemberDetailsForNotifyCustomer() {
        MemberImpl member1 = new MemberImpl();
        member1.setCustomerId("123");
        member1.setIssuer("Westpaclegacy");

        SuperFundAccount superfundAccount = mock(SuperFundAccount.class);
        when(superfundAccount.getMembers()).thenReturn(Collections.<Member>singletonList(member1));

        helper.setMemberDetails(superfundAccount, clientDetail, "ab@cd.com");

        Member member = superfundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "John");
        assertEquals(member.getLastName(), "Smith");
        assertEquals(member.getEmailAddresses().get(0), "ab@cd.com");
        assertEquals(member.getDateOfBirth(), new DateTime("2017-01-01"));
    }

    @Test
    public void setMemberDetailsForCreateMember() {
        MemberImpl member1 = new MemberImpl();
        member1.setCustomerId("123");
        member1.setIssuer("Westpaclegacy");

        SuperFundAccount superfundAccount = mock(SuperFundAccount.class);
        when(superfundAccount.getMembers()).thenReturn(Collections.<Member>singletonList(member1));

        helper.setMemberDetails(superfundAccount, clientDetail, null);

        Member member = superfundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "John");
        assertEquals(member.getLastName(), "Smith");
        assertEquals(member.getEmailAddresses().get(0), "something@do.com");
        assertEquals(member.getDateOfBirth(), new DateTime("2017-01-01"));
    }
}