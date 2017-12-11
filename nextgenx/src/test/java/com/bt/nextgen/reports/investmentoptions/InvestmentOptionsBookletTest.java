package com.bt.nextgen.reports.investmentoptions;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.api.product.v1.model.ProductCategory;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductKey;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.ips.InvestmentPolicyStatementImpl;
import com.bt.nextgen.service.avaloq.ips.IpsFeeImpl;
import com.bt.nextgen.service.avaloq.ips.IpsTariffImpl;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.bt.nextgen.service.avaloq.product.ProductLevel.WHITE_LABEL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentOptionsBookletTest {

    @InjectMocks
    InvestmentOptionsBooklet investmentOptionsBooklet;

    @Mock
    private AvailableAssetDtoService availableAssetDtoService;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private ProductDtoService productDtoService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BadgingService badgingService;

    @Mock
    private Configuration configuration;

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    @Qualifier("jsonObjectMapper")
    private ObjectMapper jsonObjectMapper;

    private static final String COMPACT_SUPER_SHORT_NAME = "PROD.WL.SUP.COMP.797475D1E1B246528C49EF8A75A9315E";
    private static final String PROFESSIONAL_SUPER_SHORT_NAME = "PROD.WL.SUP.797475d1e1b246528c49ef8a75a9315e";
    private static final String PROFESSIONAL_SUPER_PRODUCT_NAME = "BT Panorama Super";
    private static final String COMPACT_SUPER_FILE_NAME = "Panorama Super Investment Options Booklet - Compact Menu";
    private static final String PROFESSIONAL_SUPER_FILE_NAME = "Panorama Super Investment Options Booklet - Full Menu";

    private ProductDto superProduct;

    @Before
    public void setup() {
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/");
        superProduct = mock(ProductDto.class);
        when(superProduct.getProductLevel()).thenReturn(WHITE_LABEL.name());
        when(superProduct.getProductCategory()).thenReturn(ProductCategory.SUPER);
        when(superProduct.getProductName()).thenReturn("BT Panorama Super");
        when(superProduct.getKey()).thenReturn(new ProductKey("porduct"));
        when(productDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(superProduct));
    }

    @Test
    public void testGetDateGeneratedString() {
        String dateString = investmentOptionsBooklet.getDateGeneratedString(null);
        assertThat(dateString, equalTo(new SimpleDateFormat("dd MMMM yyyy").format(new Date())));
    }


    @Test
    public void testGetAssets_forAllowedStatus() {
        List<AssetDto> assets = new ArrayList<>();
        ShareAssetImpl openAsset = new ShareAssetImpl();
        openAsset.setStatus(AssetStatus.OPEN);
        openAsset.setAssetCode("BHP");

        ShareAssetImpl openWithRedemptionAsset = new ShareAssetImpl();
        openWithRedemptionAsset.setStatus(AssetStatus.OPEN_RED);
        openWithRedemptionAsset.setAssetCode("NAB");

        ShareAssetImpl closeWithRedemptionAsset = new ShareAssetImpl();
        closeWithRedemptionAsset.setStatus(AssetStatus.CLOSED_RED);
        closeWithRedemptionAsset.setAssetCode("CBA");

        ShareAssetImpl closeToNew = new ShareAssetImpl();
        closeToNew.setStatus(AssetStatus.CLOSED_TO_NEW);
        closeToNew.setAssetCode("NAN");

        ShareAssetImpl noStatus = new ShareAssetImpl();
        noStatus.setAssetCode("WBC");

        assets.add(new ShareAssetDto(openAsset));
        assets.add(new ShareAssetDto(openWithRedemptionAsset));
        assets.add(new ShareAssetDto(closeWithRedemptionAsset));
        assets.add(new ShareAssetDto(closeToNew));
        assets.add(new ShareAssetDto(noStatus));

        when(availableAssetDtoService.getFilteredValue(anyString(), any(List.class), any(ServiceErrors.class))).thenReturn(assets);
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getListedSecurities(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(3));
        assertThat(iobAssets.get(0).getAssetCode(), equalTo("BHP"));
        assertThat(iobAssets.get(1).getAssetCode(), equalTo("NAB"));
        assertThat(iobAssets.get(2).getAssetCode(), equalTo("WBC"));
    }

    @Test
    public void testGetListedSecurities_whenCalled_thenItReturnsTheCorrectList() {
        List<AssetDto> assets = new ArrayList<>();
        ShareAssetImpl assetImpl = new ShareAssetImpl();
        assetImpl.setAssetName("BHP");
        assetImpl.setInvestmentHoldingLimit(BigDecimal.ONE);
        assetImpl.setInvestmentHoldingLimitBuffer(BigDecimal.TEN);
        ShareAssetDto shareAsset = new ShareAssetDto(assetImpl, null);
        shareAsset.setAssetCode("BHP");
        shareAsset.setGroupClass("Australian Shares");
        assets.add(shareAsset);
        Mockito.when(
                availableAssetDtoService.getFilteredValue(anyString(), Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getListedSecurities(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(1));
        assertThat(iobAssets.get(0).getAssetCode(), equalTo("BHP"));
        assertThat(iobAssets.get(0).getGroupClass(), equalTo("Australian Shares"));
        assertThat(iobAssets.get(0).getInvestmentBuffer(), equalTo("9"));
        assertThat(iobAssets.get(0).getHoldingLimit(), equalTo("1"));
    }

    @Test
    public void testGetManagedFunds_whenCalled_thenItReturnsTheCorrectList() {
        List<AssetDto> assets = new ArrayList<>();
        ManagedFundAssetImpl assetImpl = new ManagedFundAssetImpl();
        assetImpl.setIndirectCostRatioPercent(BigDecimal.TEN);
        assetImpl.setAssetClass(AssetClass.DIVERSIFIED);
        assetImpl.setAssetName("mf1-name");
        ManagedFundAssetDto mfAsset = new ManagedFundAssetDto(assetImpl);
        mfAsset.setAssetCode("mf1");
        // TODO: ManagedFundAssetDto hides assetClass from super AssetDto
        // mfAsset.setAssetClass("Diversified");
        mfAsset.setRiskMeasure("Low");
        assets.add(mfAsset);
        Mockito.when(
                availableAssetDtoService.getFilteredValue(anyString(), Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getManagedFunds(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(1));
        assertThat(iobAssets.get(0).getAssetCode(), equalTo("mf1"));
        assertThat(iobAssets.get(0).getRiskMeasure(), equalTo("Low"));
        assertThat(iobAssets.get(0).getAssetClass(), equalTo("Diversified"));
        assertThat(iobAssets.get(0).getAssetName(), equalTo("mf1-name"));
        assertThat(iobAssets.get(0).getFeeMeasure(), equalTo("10.00%"));
    }

    @Test
    public void testGetManagedFunds_whenNoFeeMeasureAvailable() {
        List<AssetDto> assets = new ArrayList<>();
        ManagedFundAsset asset1 = mock(ManagedFundAsset.class);
        when(asset1.getIndirectCostRatioPercent()).thenReturn(null);

        ManagedFundAsset asset2 = mock(ManagedFundAsset.class);
        when(asset2.getIndirectCostRatioPercent()).thenReturn(BigDecimal.ZERO);

        ManagedFundAsset asset3 = mock(ManagedFundAsset.class);
        when(asset3.getIndirectCostRatioPercent()).thenReturn(BigDecimal.TEN);

        assets.add(new ManagedFundAssetDto(asset1));
        assets.add(new ManagedFundAssetDto(asset2));
        assets.add(new ManagedFundAssetDto(asset3));

        Mockito.when(availableAssetDtoService.getFilteredValue(anyString(), Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getManagedFunds(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(3));
        assertThat(iobAssets.get(0).getFeeMeasure(), equalTo("-"));
        assertThat(iobAssets.get(1).getFeeMeasure(), equalTo("-"));
        assertThat(iobAssets.get(2).getFeeMeasure(), equalTo("10.00%"));
    }

    @Test
    public void testGetManagedPortfolios_whenCalled_thenItReturnsTheCorrectList() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, IOException {
        ManagedPortfolioAssetImpl assetImpl = new ManagedPortfolioAssetImpl();
        assetImpl.setAssetCode("mp1");
        assetImpl.setIpsId("ips1");
        List<AssetDto> assets = new ArrayList<>();
        ManagedPortfolioAssetDto mpAsset = new ManagedPortfolioAssetDto(assetImpl);
        assets.add(mpAsset);
        Mockito.when(
                availableAssetDtoService.getFilteredValue(anyString(), Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);
        InvestmentPolicyStatementImpl ips = new InvestmentPolicyStatementImpl();
        Field field = ips.getClass().getDeclaredField("ipsKey");
        field.setAccessible(true);
        field.set(ips, IpsKey.valueOf("ips1"));
        ips.setInvestmentManagerPersonId("1");
        IpsFeeImpl fee = new IpsFeeImpl();
        IpsTariffImpl tariff = new IpsTariffImpl();
        tariff.setTariffFactor(BigDecimal.valueOf(0.1));
        List<IpsTariff> tariffs = new ArrayList<>();
        tariffs.add(tariff);
        fee.setTariffList(tariffs);
        fee.setMasterBookKind(FeesType.INVESTMENT_MANAGEMENT_FEE);
        fee.setBookKind(FeesType.INVESTMENT_MANAGEMENT_FEE_IM);
        List<IpsFee> fees = new ArrayList<>();
        fees.add(fee);
        ips.setFeeList(fees);
        Mockito.when(ipsService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonMap(ips.getIpsKey(), (InvestmentPolicyStatementInterface) ips));
        BrokerImpl broker = new BrokerImpl(null, null);
        broker.setPositionName("Bruce");
        Mockito.when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        Mockito.when(jsonObjectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(Collections.singletonMap("mp1", "0.342"));
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getManagedPortfolios(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(1));
        assertThat(iobAssets.get(0).getAssetCode(), equalTo("mp1"));
        assertThat(iobAssets.get(0).getInvestmentManagerName(), equalTo("Bruce"));
        assertThat(iobAssets.get(0).getFeeMeasure(), equalTo("10.00%"));
    }

    @Test
    public void testGetTermDeposits_whenCalled_thenItReturnsTheCorrectList() {
        List<AssetDto> assets = new ArrayList<>();
        TermDepositAssetDtoV2 tdAsset = new TermDepositAssetDtoV2(new TermDepositAssetImpl(), "Westpac", "Westpac", 30, null, "monthly", null, null, new ArrayList(), null);
        tdAsset.setAssetCode("TD1");
        assets.add(tdAsset);
        Mockito.when(
                availableAssetDtoService.getFilteredValue(anyString(), Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);
        List<InvestmentOptionsBookletAssetData> iobAssets = investmentOptionsBooklet.getTermDeposits(new HashMap<String, Object>());
        assertThat(iobAssets, hasSize(1));
        assertThat(iobAssets.get(0).getTermDepositInterestFrequency(), equalTo("Monthly"));
        assertThat(iobAssets.get(0).getTermDepositTerm(), equalTo("30"));
    }

    @Test
    public void testGetDiversifiedManagedInvestmentOptionsTableData_whenCalled_thenItUsesTheCorrectKey() throws IOException {
        Collection<InvestmentOptionsBookletTableData> readTableData = new ArrayList<>();
        readTableData.add(new InvestmentOptionsBookletTableData("Conservative", "Standard Risk Measure", "2 (Low)"));
        Mockito.when(jsonObjectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(readTableData);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("JSON content");
        List<InvestmentOptionsBookletTableData> tableData = investmentOptionsBooklet
                .getDiversifiedManagedInvestmentOptionsTableData(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.diversifiedManagedTableData"));
        assertThat(tableData, hasSize(1));
        assertThat(tableData.get(0).getInvestmentType(), equalTo("Conservative"));
        assertThat(tableData.get(0).getInvestmentMeasure(), equalTo("Standard Risk Measure"));
        assertThat(tableData.get(0).getInvestmentMeasureValue(), equalTo("2 (Low)"));
    }

    @Test
    public void testGetSectorSpecificManagedInvestmentOptionsTableData_whenCalled_thenItUsesTheCorrectKey() throws IOException {
        Mockito.when(cmsService.getContent(anyString())).thenReturn("JSON content");
        Collection<InvestmentOptionsBookletTableData> tableData = investmentOptionsBooklet
                .getSectorSpecificManagedInvestmentOptionsTableData(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.sectorSpecificManagedTableData"));
    }

    @Test
    public void testGetSectorSpecificDirectInvestmentOptionsTableData_whenCalled_thenItUsesTheCorrectKey() throws IOException {
        Collection<InvestmentOptionsBookletTableData> tableData = investmentOptionsBooklet
                .getSectorSpecificDirectInvestmentOptionsTableData(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.sectorSpecificDirectTableData"));
    }

    @Test
    public void testGetBackpageLogo_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");
        Renderable logo = investmentOptionsBooklet.getBackpageLogo(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.backpageLogo"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetTocBackground_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");
        Renderable logo = investmentOptionsBooklet.getTocBackground(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.tocBackground"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetCoverImage_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");
        Renderable logo = investmentOptionsBooklet.getCoverImage(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.coverImage"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetCoverLogo_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getCoverLogo(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("IOB.coverLogo"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconRightChevron_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconRightChevron(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconRightChevron"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconPhone_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconPhone(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconPhone"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconEnvelope_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconEnvelope(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconEnvelope"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconMobile_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconMobile(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconMobile"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconLetter_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconLetter(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconLetter"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testGetIconPostbox_whenCalled_thenItUsesTheCorrctKey() {
        when(cmsService.getContent(anyString())).thenReturn("cms/vectorImage.svg");
        Renderable logo = investmentOptionsBooklet.getIconPostbox(null);
        Mockito.verify(cmsService).getContent(Mockito.eq("iconPostbox"));
        assertThat(logo, notNullValue());
    }

    @Test
    public void testIsProductCompactSuper_NotFound() {
        assertFalse(investmentOptionsBooklet.isProductCompactSuper(new HashMap<String, Object>()));
    }

    @Test
    public void testIsProductProfessionalSuper_NotFound() {
        assertFalse(investmentOptionsBooklet.isProductCompactSuper(new HashMap<String, Object>()));
    }

    @Test
    public void testIsProductCompactSuper() {
        when(superProduct.getShortName()).thenReturn(COMPACT_SUPER_SHORT_NAME);
        assertTrue(investmentOptionsBooklet.isProductCompactSuper(new HashMap<String, Object>()));
    }


    @Test
    public void testIsProductProfessionalSuper() {
        when(superProduct.getShortName()).thenReturn(PROFESSIONAL_SUPER_SHORT_NAME);
        assertTrue(investmentOptionsBooklet.isProductProfessionalSuper(new HashMap<String, Object>()));
    }

    @Test
    public void testGetFileNameProfessionalSuper() {
        when(superProduct.getShortName()).thenReturn(PROFESSIONAL_SUPER_SHORT_NAME);
        assertEquals(investmentOptionsBooklet.getReportFileName(Collections.singletonList(superProduct)), PROFESSIONAL_SUPER_FILE_NAME);
    }

    @Test
    public void testGetFileNameCompactSuper() {
        when(superProduct.getShortName()).thenReturn(COMPACT_SUPER_SHORT_NAME);
        assertEquals(investmentOptionsBooklet.getReportFileName(Collections.singletonList(superProduct)), COMPACT_SUPER_FILE_NAME);
    }

    @Test
    public void testGetFileNameNoProduct() {
        assertEquals(investmentOptionsBooklet.getReportFileName(Collections.emptyList()), PROFESSIONAL_SUPER_FILE_NAME);
    }

    @Test
    public void testHasWfslanager() throws IOException {
        assertFalse(investmentOptionsBooklet.hasWfslManager(new HashMap<String, Object>()));
    }

    @Test
    public void testHasWfslanagerSuccess() throws Exception {
        ManagedPortfolioAssetImpl assetImpl = new ManagedPortfolioAssetImpl();
        assetImpl.setAssetCode("mp1");
        assetImpl.setIpsId("ips1");

        List<AssetDto> assets = new ArrayList<>();
        assets.add(new ManagedPortfolioAssetDto(assetImpl));

        Code code = mock(Code.class);
        when(code.getName()).thenReturn(AssetClass.DIVERSIFIED.getDescription());

        Mockito.when(availableAssetDtoService.getFilteredValue(anyString(), anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assets);
        Mockito.when(staticService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        InvestmentPolicyStatementImpl ips = new InvestmentPolicyStatementImpl();
        Field field = ips.getClass().getDeclaredField("ipsKey");
        field.setAccessible(true);
        field.set(ips, IpsKey.valueOf("ips1"));
        ips.setInvestmentManagerPersonId("1");
        Mockito.when(ipsService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonMap(ips.getIpsKey(), (InvestmentPolicyStatementInterface) ips));

        Broker broker = mock(Broker.class);
        when(broker.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("IM.WFSL"));
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);

        assertTrue(investmentOptionsBooklet.hasWfslManager(new HashMap<String, Object>()));
    }
}
