package com.bt.nextgen.service.avaloq.corporateaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionConverterImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelper;
import com.bt.nextgen.api.corporateaction.v1.service.EffectiveCorporateActionType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionConverterImplTest {
    @InjectMocks
    private CorporateActionConverterImpl converter;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private CorporateActionHelper helper;

    private CorporateActionListResult corporateActionListResult;

    private Map<String, Asset> assetMap = new HashMap<>();

    private Map<AccountKey, WrapAccount> wrapAccountMap = new HashMap<>();

    private DateTime currentDateTime = new DateTime();

    @Before
    public void setup() {
        corporateActionListResult = new CorporateActionListResult();
        corporateActionListResult.setCorporateActions(new ArrayList<CorporateAction>());

        ShareAsset asset = mock(ShareAsset.class);
        when(asset.getAssetId()).thenReturn("1");
        when(asset.getAssetCode()).thenReturn("XXX");
        when(asset.getAssetName()).thenReturn("YYY");
        when(asset.getInvestmentHoldingLimit()).thenReturn(BigDecimal.TEN);
        assetMap.put(asset.getAssetId(), asset);

        ManagedFundAsset mfAsset = mock(ManagedFundAsset.class);
        when(mfAsset.getAssetId()).thenReturn("2");
        when(mfAsset.getAssetCode()).thenReturn("XXX");
        when(mfAsset.getAssetName()).thenReturn("YYY");
        assetMap.put(mfAsset.getAssetId(), mfAsset);

        AccountKey accountKey = AccountKey.valueOf("1234567");
        WrapAccount wrapAccount = mock(WrapAccount.class);
        when(wrapAccount.getAccountNumber()).thenReturn(accountKey.getId());
        wrapAccountMap.put(accountKey, wrapAccount);

        when(assetIntegrationService.loadAssets(any(ArrayList.class), any(ServiceErrors.class))).thenReturn(assetMap);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(wrapAccountMap);
        when(helper.generateCorporateActionStatus(any(CorporateActionGroup.class), any(CorporateActionStatus.class),
                any(DateTime.class), any(DateTime.class), any(DateTime.class))).thenReturn(CorporateActionStatus.OPEN);

        when(helper.getEffectiveCorporateActionType(any(CorporateActionType.class), any(CorporateActionOfferType.class),
                any(CorporateActionSecurityExchangeType.class), anyBoolean()))
                .thenReturn(new EffectiveCorporateActionType(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.PUBLIC_OFFER.name(),
                        CorporateActionOfferType.PUBLIC_OFFER.getDescription()));
    }

    private CorporateAction createMockCorporateAction() {
        CorporateAction ca = mock(CorporateAction.class);
        when(ca.getOrderNumber()).thenReturn("0");
        when(ca.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(ca.getAssetId()).thenReturn("1");
        when(ca.getCloseDate()).thenReturn(currentDateTime);
        when(ca.getAnnouncementDate()).thenReturn(currentDateTime);
        when(ca.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(ca.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.CAPITAL_CALL);
        when(ca.getEligible()).thenReturn(1);
        when(ca.getUnconfirmed()).thenReturn(2);
        when(ca.getVoluntaryFlag()).thenReturn("1");
        when(ca.getPayDate()).thenReturn(currentDateTime);
        when(ca.getNotificationCnt()).thenReturn(BigInteger.ONE);
        when(ca.isNonProRata()).thenReturn(Boolean.FALSE);
        when(ca.getIncomeRate()).thenReturn(BigDecimal.ZERO);
        when(ca.getFullyUnfrankedAmount()).thenReturn(BigDecimal.ZERO);
        when(ca.getTrusteeApprovalStatus()).thenReturn(TrusteeApprovalStatus.APPROVED);
        when(ca.getTrusteeApprovalUserId()).thenReturn("0");
        when(ca.getTrusteeApprovalUserName()).thenReturn("Trustee");
        when(ca.getTrusteeApprovalStatusDate()).thenReturn(currentDateTime);
        when(ca.getIrgApprovalStatus()).thenReturn(IrgApprovalStatus.APPROVED);
        when(ca.getIrgApprovalUserId()).thenReturn("0");
        when(ca.getIrgApprovalUserName()).thenReturn("IRG");
        when(ca.getIrgApprovalStatusDate()).thenReturn(currentDateTime);
        when(ca.getExDate()).thenReturn(currentDateTime);
        when(ca.isEarlyClose()).thenReturn(Boolean.FALSE);

        return ca;
    }

    private CorporateActionListResult createCorporateActionListResult(CorporateAction corporateAction) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();
        corporateActionListResult.setCorporateActions(Arrays.asList(corporateAction));

        return corporateActionListResult;
    }

    @Test
    public void testToCorporateActionDtoList_whenThereIsACorporateAction_thenReturnCorrectlyPopulatedDto() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);
        corporateActionListResult.setHasSuperPension(Boolean.TRUE);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null, null);

        assertNotNull(corporateActionListDto);
        assertEquals(corporateActionListDto.getCorporateActions().size(), 1);
        assertTrue(corporateActionListDto.getHasSuperPension());

        CorporateActionDto corporateActionDto = (CorporateActionDto) corporateActionListDto.getCorporateActions().get(0);

        assertEquals(EncodedString.toPlainText(corporateActionDto.getId()), "0");
        assertNull(corporateActionDto.getKey());
        assertEquals("XXX", corporateActionDto.getCompanyCode());
        assertEquals("YYY", corporateActionDto.getCompanyName());
        assertTrue(corporateActionDto.getCloseDate().equals(currentDateTime));
        assertTrue(corporateActionDto.getAnnouncementDate().equals(currentDateTime));
        assertEquals(CorporateActionOfferType.PUBLIC_OFFER.getDescription(), corporateActionDto.getCorporateActionTypeDescription());
        assertEquals((Integer) 1, corporateActionDto.getEligible());
        assertEquals((Integer) 2, corporateActionDto.getUnconfirmed());
        assertTrue(corporateActionDto.getPayDate().equals(currentDateTime));
        assertEquals(CorporateActionStatus.OPEN, corporateActionDto.getStatus());
        assertEquals("No", corporateActionDto.getEarlyClose());
    }

    @Test
    public void testToCorporateActionDtoList_whenThereIsNoCorporateAction_thenReturnAnEmptyList() {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null, null);

        assertNotNull(corporateActionListDto);
        assertEquals(0, corporateActionListDto.getCorporateActions().size());
    }

    @Test
    public void testToCorporateActionDtoList_whenThereIsAnAccountNumberSpecified_thenReturnCorrectlyPopulatedDto() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, "1234567", null);

        assertNotNull(corporateActionListDto);
        assertEquals(corporateActionListDto.getCorporateActions().size(), 1);

        CorporateActionDto corporateActionDto = (CorporateActionDto) corporateActionListDto.getCorporateActions().get(0);

        assertEquals("1234567", corporateActionDto.getAccountId());
    }

    @Test
    public void testToCorporateActionDtoList_whenThereIsNoAccountFound_thenReturnNoAccountNumber() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, "1234567890", null);

        assertNotNull(corporateActionListDto);
        assertEquals(corporateActionListDto.getCorporateActions().size(), 1);

        CorporateActionDto corporateActionDto = (CorporateActionDto) corporateActionListDto.getCorporateActions().get(0);

        assertNull(corporateActionDto.getAccountId());
    }

    @Test
    public void testToCorporateActionDtoList_whenNoCorporateActionType_thenThereShouldNotBeACorporateActionDtoCreated() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        when(corporateAction.getCorporateActionType()).thenReturn(null);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null, null);

        assertNotNull(corporateActionListDto);
        assertTrue(corporateActionListDto.getCorporateActions().isEmpty());
    }

    @Test
    public void testToCorporateActionApprovalListDto_whenThereIsACorporateAction_thenReturnCorrectlyPopulatedDto() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionApprovalListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null);

        assertNotNull(corporateActionListDto);
        assertEquals(corporateActionListDto.getCorporateActions().size(), 1);
        assertTrue(corporateActionListDto.getCorporateActions().get(0) instanceof CorporateActionApprovalDto);

        CorporateActionApprovalDto corporateActionDto = (CorporateActionApprovalDto) corporateActionListDto.getCorporateActions().get(0);

        assertEquals(BigDecimal.TEN, corporateActionDto.getHoldingLimitPercent());
        assertEquals("0", corporateActionDto.getTrusteeApprovalUserId());
        assertEquals("Trustee", corporateActionDto.getTrusteeApprovalUserName());
        assertEquals(TrusteeApprovalStatus.APPROVED, corporateActionDto.getTrusteeApprovalStatus());
        assertTrue(corporateActionDto.getTrusteeApprovalStatusDate().equals(currentDateTime));
        assertEquals("0", corporateActionDto.getIrgApprovalUserId());
        assertEquals("IRG", corporateActionDto.getIrgApprovalUserName());
        assertEquals(IrgApprovalStatus.APPROVED, corporateActionDto.getIrgApprovalStatus());
        assertTrue(corporateActionDto.getIrgApprovalStatusDate().equals(currentDateTime));
    }

    @Test
    public void testToCorporateActionApprovalListDto_whenApprovalIsNull_thenApprovalStatusShouldBePending() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        when(corporateAction.getTrusteeApprovalStatus()).thenReturn(null);
        when(corporateAction.getIrgApprovalStatus()).thenReturn(null);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionApprovalListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null);

        CorporateActionApprovalDto corporateActionDto = (CorporateActionApprovalDto) corporateActionListDto.getCorporateActions().get(0);

        assertEquals(TrusteeApprovalStatus.PENDING, corporateActionDto.getTrusteeApprovalStatus());
        assertEquals(IrgApprovalStatus.PENDING, corporateActionDto.getIrgApprovalStatus());
    }

    @Test
    public void testToCorporateActionApprovalListDto_whenNotShareAsset_thenDoNotPopulateHoldingLimitPercent() {
        CorporateAction corporateAction = createMockCorporateAction();
        when(corporateAction.getAssetId()).thenReturn("2");

        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionApprovalListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null);

        CorporateActionApprovalDto corporateActionDto = (CorporateActionApprovalDto) corporateActionListDto.getCorporateActions().get(0);

        assertNull(corporateActionDto.getHoldingLimitPercent());
    }

    @Test
    public void testToCorporateActionApprovalListDto_whenEarlyClose_thenSetEarlyCloseToYes() {
        CorporateAction corporateAction = createMockCorporateAction();
        when(corporateAction.isEarlyClose()).thenReturn(true);

        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto =
                converter.toCorporateActionApprovalListDto(CorporateActionGroup.VOLUNTARY, corporateActionListResult, null);

        CorporateActionApprovalDto corporateActionDto = (CorporateActionApprovalDto) corporateActionListDto.getCorporateActions().get(0);

        assertEquals("Yes", corporateActionDto.getEarlyClose());
    }

    @Test
    public void testToCorporateActionListDtoForIm_whenThereIsACorporateAction_thenReturnCorrectlyPopulatedDto() {
        CorporateAction corporateAction = createMockCorporateAction();
        CorporateActionListResult corporateActionListResult = createCorporateActionListResult(corporateAction);

        CorporateActionListDto corporateActionListDto = converter.toCorporateActionListDtoForIm(CorporateActionGroup.VOLUNTARY,
                corporateActionListResult, "0", null);

        assertNotNull(corporateActionListDto);
        assertEquals(corporateActionListDto.getCorporateActions().size(), 1);
        assertTrue(corporateActionListDto.getCorporateActions().get(0) instanceof ImCorporateActionDto);
    }
}
