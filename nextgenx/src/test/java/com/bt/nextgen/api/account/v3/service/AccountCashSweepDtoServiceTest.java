
package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvaloqContainerIntegrationService;
import com.bt.nextgen.service.integration.account.CashSweepAccountRequest;
import com.btfin.panorama.service.integration.account.CashSweepInvestmentAsset;
import com.bt.nextgen.service.integration.account.CashSweepInvestmentRequest;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.account.UpdateCashSweepAccountResponse;
import com.bt.nextgen.service.integration.account.UpdateCashSweepInvestmentResponse;
import com.bt.nextgen.service.integration.account.direct.CashSweepInvestmentAssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountCashSweepDtoServiceTest {

    @InjectMocks
    private AccountCashSweepDtoServiceImpl accountCashSweepDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AvaloqContainerIntegrationService containerIntegrationService;

    @Mock
    private AccountProductsHelper accountProductsHelper;

    @Mock
    private CmsService cmsService;

    private ServiceErrors serviceErrors;
    private List<CashSweepInvestmentAsset> cashSweepInvestmentAssets;
    private AccountCashSweepDto cashSweepDto;
    private WrapAccountDetailImpl account;
    private SubAccount subAccount;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        cashSweepInvestmentAssets = new ArrayList<>();
        CashSweepInvestmentAssetImpl cashSweepInvestmentAsset = new CashSweepInvestmentAssetImpl();
        cashSweepInvestmentAsset.setSweepPercent(new BigDecimal(20));
        cashSweepInvestmentAsset.setInvestmentAssetId("123456");
        cashSweepInvestmentAssets.add(cashSweepInvestmentAsset);

        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("123456");
        when(asset.getAssetCode()).thenReturn("code1");
        when(asset.getAssetName()).thenReturn("asset1");
        when(asset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        account = setupMockAccount(true);
        final List<CashSweepInvestmentDto> cashSweepInvestmentDtos = Collections.singletonList(new CashSweepInvestmentDto(asset, new BigDecimal(20)));
        cashSweepDto = new AccountCashSweepDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), BigDecimal.TEN, true, BigDecimal.ONE, cashSweepInvestmentDtos);
        UpdateCashSweepInvestmentResponse investmentResponse = mock(UpdateCashSweepInvestmentResponse.class);

        when(accountProductsHelper.convertToCashSweepInvestmentDto(anyList(), any(ServiceErrors.class))).thenReturn(cashSweepInvestmentDtos);
        when(accountProductsHelper.getCashSweepAssets(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(SubAccountImpl.class), any(ServiceErrors.class)))
                .thenReturn(cashSweepInvestmentDtos);
        when(containerIntegrationService.updateCashSweepInvestmentDetails(any(CashSweepInvestmentRequest.class), any(ServiceErrors.class))).thenReturn(investmentResponse);
        when(cmsService.getContent(anyString())).thenReturn("Error");
    }

    @Test
    public void testFindCashSweepDetails() {
        verifyCashSweepResponse(accountCashSweepDtoService.find(new AccountKey(EncodedString.fromPlainText("12345").toString()), serviceErrors));
    }

    @Test
    public void testFindCashSweepDetailsForInvalidAccountKey() {
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);
        Assert.assertNull(accountCashSweepDtoService.find(new AccountKey(EncodedString.fromPlainText("12345").toString()), serviceErrors));
    }

    @Test
    public void testFindCashSweepForInvalidSubAccountKey() {
        when(containerIntegrationService.loadSubAccountDetails(anyString(), any(ServiceErrors.class))).thenReturn(null);
        AccountCashSweepDto result = accountCashSweepDtoService.find(new AccountKey(EncodedString.fromPlainText("12345").toString()), serviceErrors);
        assertNull(result);
        Mockito.verify(accountProductsHelper, Mockito.never()).getCashSweepAssets(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(SubAccount.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdateCashSweepAccountTrue() {
        verifyUpdateCashSweepResponse(true);
    }

    @Test
    public void testUpdateCashSweepAccountFalse() {
        verifyUpdateCashSweepResponse(false);
    }

    @Test
    public void testUpdateCashSweepForAccountError() {
        when(accountIntegrationService.updateCashSweepAccountDetails(any(CashSweepAccountRequest.class), any(ServiceErrors.class))).thenReturn(null);
        AccountCashSweepDto result = accountCashSweepDtoService.update(cashSweepDto, serviceErrors);
        Assert.assertNull(result.isCashSweepAllowed());
        Assert.assertNull(result.getMinCashSweepAmount());
        Assert.assertNotNull(result.getError());
    }

    @Test
    public void testUpdateCashSweepForInvestmentError() {
        when(containerIntegrationService.updateCashSweepInvestmentDetails(any(CashSweepInvestmentRequest.class), any(ServiceErrors.class))).thenReturn(null);
        AccountCashSweepDto result = accountCashSweepDtoService.update(cashSweepDto, serviceErrors);
        Assert.assertNull(result.getCashSweepInvestments());
        Assert.assertNotNull(result.getError());
    }

    @Test
    public void testUpdateCashSweepForInvalidAccountKey() {
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);
        AccountCashSweepDto result = accountCashSweepDtoService.update(cashSweepDto, serviceErrors);
        Assert.assertNull(result.isCashSweepAllowed());
        Assert.assertNull(result.getMinCashSweepAmount());
        Assert.assertNull(result.getCashSweepInvestments());
    }

    @Test
    public void verifyUpdateCashSweepInvestmentResponse() {
        account = setupMockAccount(true);
        UpdateCashSweepInvestmentResponse investmentResponse = mock(UpdateCashSweepInvestmentResponse.class);
        UpdateCashSweepAccountResponse accountResponse = mock(UpdateCashSweepAccountResponse.class);
        when(investmentResponse.getSubAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.SubAccountKey.valueOf("121212"));
        when(investmentResponse.getCashSweepInvestmentAssets()).thenReturn(cashSweepInvestmentAssets);
        when(containerIntegrationService.updateCashSweepInvestmentDetails(any(CashSweepInvestmentRequest.class), any(ServiceErrors.class))).thenReturn(investmentResponse);
        when(accountIntegrationService.updateCashSweepAccountDetails(any(CashSweepAccountRequest.class), any(ServiceErrors.class))).thenReturn(accountResponse);

        AccountCashSweepDto result = accountCashSweepDtoService.update(cashSweepDto, serviceErrors);
        assertEquals(result.getCashSweepInvestments().size(), 1);
        assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetId(), "123456");
        assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetCode(), "code1");
        assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetName(), "asset1");
        assertEquals(result.getCashSweepInvestments().get(0).getAllocationPercent(), new BigDecimal(20));
    }

    private void verifyUpdateCashSweepResponse(Boolean isCashSweepAllowed) {
        account = setupMockAccount(isCashSweepAllowed);
        subAccount = setupMockSubAccount();
        UpdateCashSweepAccountResponse response = mock(UpdateCashSweepAccountResponse.class);
        when(response.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"));
        when(response.isCashSweepApplied()).thenReturn(isCashSweepAllowed);
        when(response.getMinCashSweepAmount()).thenReturn(BigDecimal.valueOf(200));
        when(accountIntegrationService.updateCashSweepAccountDetails(any(CashSweepAccountRequest.class), any(ServiceErrors.class))).thenReturn(response);

        verifyCashSweepResponse(accountCashSweepDtoService.update(cashSweepDto, serviceErrors));
    }

    private void verifyCashSweepResponse(AccountCashSweepDto result) {
        assertNotNull(result);
        Assert.assertEquals(result.isCashSweepAllowed(), account.isCashSweepApplied());
        Assert.assertEquals(result.getMinCashAmount(), account.getMinCashAmount());
        Assert.assertEquals(result.getMinCashSweepAmount(), account.getMinCashSweepAmount());

        if (CollectionUtils.isNotEmpty(result.getCashSweepInvestments())) {
            assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetId(), "123456");
            assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetCode(), "code1");
            assertEquals(result.getCashSweepInvestments().get(0).getAsset().getAssetName(), "asset1");
            assertEquals(result.getCashSweepInvestments().get(0).getAllocationPercent(), new BigDecimal(20));
        }
    }

    private WrapAccountDetailImpl setupMockAccount(Boolean isCashSweepAllowed) {
        WrapAccountDetailImpl accountDetail = Mockito.mock(WrapAccountDetailImpl.class);
        subAccount = setupMockSubAccount();

        when(accountDetail.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("Account1"));
        when(accountDetail.getModificationSeq()).thenReturn("2");
        when(accountDetail.isOpen()).thenReturn(false);
        when(accountDetail.isHasMinCash()).thenReturn(false);
        when(accountDetail.isCashSweepApplied()).thenReturn(isCashSweepAllowed);
        when(accountDetail.getMinCashAmount()).thenReturn(BigDecimal.valueOf(2000));
        when(accountDetail.getMinCashSweepAmount()).thenReturn(BigDecimal.valueOf(200));
        when(accountDetail.getSubAccounts()).thenReturn(Collections.singletonList(subAccount));

        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        return accountDetail;
    }

    private SubAccount setupMockSubAccount() {
        SubAccount subAccount = mock(SubAccount.class);
        when(subAccount.getSubAccountKey()).thenReturn(SubAccountKey.valueOf("subAccount1"));
        when(subAccount.getSubAccountType()).thenReturn(ContainerType.DIRECT);
        when(subAccount.getCashSweepInvestmentAssetList()).thenReturn(cashSweepInvestmentAssets);
        when(containerIntegrationService.loadSubAccountDetails(anyString(), any(ServiceErrors.class))).thenReturn(subAccount);

        return subAccount;
    }
}
