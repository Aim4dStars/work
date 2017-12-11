package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.termdeposit.service.ProductToAccountType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIdentifier;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl.InterestRateImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyListOf;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.xml.ws.Service;

@RunWith(MockitoJUnitRunner.class)
public class AvailableAssetDtoServiceTest {
    @InjectMocks
    private AvailableAssetDtoServiceImpl availableAssetService;

    @Mock
    private AssetDtoConverter converter;

    @Mock
    private AssetDtoConverterV2 assetDtoConverterV2;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserProfileService userProfileService;
    
    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private FeatureToggles featureToggles;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;


    private TermDepositAssetImpl tdAsset;
    private ManagedFundAssetImpl mfAsset;
    private ShareAssetImpl shAsset;
    private AssetImpl mpAsset;
    private List<Asset> assets;
    private List<Asset> emptyAssets;
    private TermDepositAssetDetailImpl tdAssetDetail;
    Map<String, TermDepositAssetDetail> tdAssetDetails;
    private ServiceErrors serviceErrors;
    private List<TermDepositInterestRate> termDepositInterestRates;
    private List<AssetDto> assetDtos;

    @Before
    public void setup() throws Exception {
        shAsset = new ShareAssetImpl();
        shAsset.setAssetId("110707");
        shAsset.setAssetCode("TIX");
        shAsset.setAssetType(AssetType.SHARE);
        shAsset.setAssetName("360 Capital Industrial Fund AUD");
        shAsset.setPrice(new BigDecimal(2.67));
        shAsset.setStatus(AssetStatus.OPEN);

        mfAsset = new ManagedFundAssetImpl();
        mfAsset.setAssetId("92655");
        mfAsset.setAssetCode("AMP0254AU");
        mfAsset.setAssetType(AssetType.MANAGED_FUND);
        mfAsset.setAssetName("AMP Capital Investors International Bond");
        mfAsset.setPrice(new BigDecimal(.89));
        mfAsset.setStatus(AssetStatus.OPEN);
        mfAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        mfAsset.setDistributionMethod("Cash Only");

        mpAsset = new AssetImpl();
        mpAsset.setAssetId("28737");
        mpAsset.setAssetCode("BR0001");
        mpAsset.setAssetName("Blackrock International");
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset.setStatus(AssetStatus.OPEN);
        mpAsset.setAssetClass(AssetClass.DIVERSIFIED);

        tdAsset = new TermDepositAssetImpl();
        tdAsset.setAssetId("65484");
        tdAsset.setAssetType(AssetType.TERM_DEPOSIT);
        tdAsset.setAssetClass(AssetClass.CASH);
        tdAsset.setAssetName("BT Term Deposit: 3 months interest payment at maturity");
        tdAsset.setStatus(AssetStatus.OPEN);

        tdAssetDetail = new TermDepositAssetDetailImpl();
        tdAssetDetail.setAssetId("65484");
        tdAssetDetail.setIssuer("BT");
        tdAssetDetail.setPaymentFrequency(PaymentFrequency.ANNUALLY);
        tdAssetDetail.setTerm(new Term("3Y"));
        TreeSet<InterestRate> interestRates = new TreeSet<>();
        InterestRateImpl interestRate = tdAssetDetail.new InterestRateImpl();
        interestRate.setIrcId("12345");
        interestRate.setLowerLimit(new BigDecimal("5000"));
        interestRate.setUpperLimit(new BigDecimal("500000"));
        interestRate.setRate(new BigDecimal("0.045"));
        interestRates.add(interestRate);
        interestRate = tdAssetDetail.new InterestRateImpl();
        interestRate.setIrcId("45678");
        interestRate.setLowerLimit(new BigDecimal("500000"));
        interestRate.setUpperLimit(new BigDecimal("50000000"));
        interestRate.setRate(new BigDecimal("0.047"));
        tdAssetDetail.setInterestRates(interestRates);

        tdAssetDetails = new HashMap<>();
        tdAssetDetails.put(tdAssetDetail.getAssetId(), tdAssetDetail);

        assets = new ArrayList<>();
        assets.add(mfAsset);
        assets.add(mpAsset);
        assets.add(tdAsset);
        assets.add(shAsset);

        List<InterestRateDto> rateDtos = new ArrayList<>();

        assetDtos = new ArrayList<>();
        assetDtos.add(new ManagedFundAssetDto(mfAsset));
        assetDtos.add(new ManagedPortfolioAssetDto(mpAsset));
        assetDtos.add(
                new TermDepositAssetDto(tdAsset, tdAsset.getAssetName(), null, null, null, null, null, null, rateDtos, null));
        assetDtos.add(new ShareAssetDto(shAsset));

        emptyAssets = new ArrayList<>();


        TermDepositInterestRate tdAssetDetail01 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65484")).withIssuerName("BT").withIssuerId("800000054")
                .withPaymentFrequency(PaymentFrequency.ANNUALLY).withTerm(new Term("3Y")).withLowerLimit(new BigDecimal("5000")).withUpperLimit(new BigDecimal("500000")).withRate(new BigDecimal("0.045")).buildTermDepositRate();
        TermDepositInterestRate tdAssetDetail02 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65484")).withIssuerName("BT").withIssuerId("800000054")
                .withPaymentFrequency(PaymentFrequency.ANNUALLY).withTerm(new Term("3Y")).withLowerLimit(new BigDecimal("500000")).withUpperLimit(new BigDecimal("50000000")).withRate(new BigDecimal("0.047")).buildTermDepositRate();

        termDepositInterestRates = new ArrayList<>();
        termDepositInterestRates.add(tdAssetDetail01);
        termDepositInterestRates.add(tdAssetDetail02);

        Mockito.when((assetIntegrationService.loadTermDepositRates(any(TermDepositAssetRateSearchKey.class),any(ServiceErrors.class)))).thenReturn(termDepositInterestRates);
        Mockito.when(assetIntegrationService.loadTermDepositRates(any(BrokerKey.class),any(DateTime.class),anyListOf(Asset.class),any(ServiceErrors.class))).thenReturn(tdAssetDetails);

        emptyAssets = new ArrayList<>();
        serviceErrors = new ServiceErrorsImpl();
        Mockito.when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        Mockito.when(bankDateIntegrationService.getBankDate(any(ServiceErrors.class))).thenReturn(new DateTime());

        WrapAccountDetail wrapAccountDetail = mock(WrapAccountDetail.class);
        when(wrapAccountDetail.getAccountNumber()).thenReturn("12345");
        final List<SubAccount> subAccounts = new ArrayList<>();
        subAccounts.add(getSubAccount("prod7", ContainerType.DIRECT));
        when(wrapAccountDetail.getProductKey()).thenReturn(ProductKey.valueOf("prod4"));
        when(wrapAccountDetail.getSubAccounts()).thenReturn(subAccounts);

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class),any(ServiceErrors.class))).thenReturn(wrapAccountDetail);
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);

        Answer<List<AssetDto>> assetAnswer = new Answer<List<AssetDto>>() {
            @Override
            public List<AssetDto> answer(InvocationOnMock invocation) throws Throwable {
                List<Asset> assets = (List<Asset>) invocation.getArguments()[0];
                List<AssetDto> result = new ArrayList<>();
                for (Asset asset : assets) {
                    if (asset.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                        result.add(new ManagedPortfolioAssetDto(asset));
                    } else if (asset.getAssetType() == AssetType.MANAGED_FUND) {
                        ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
                        result.add(new ManagedFundAssetDto(mfAsset));
                    } else if (asset.getAssetType() == AssetType.SHARE) {
                        ShareAsset shareAsset = (ShareAsset) asset;
                        result.add(new ShareAssetDto(shareAsset));
                    } else {
                        List<InterestRateDto> interestBands = Collections.emptyList();
                        result.add(new TermDepositAssetDto(asset, asset.getAssetName(), null, null, null,
                                null, null, null, interestBands, null));
                    }
                }

                return result;
            }
        };
        Mockito.when(assetDtoConverterV2.toAssetDto(Mockito.anyList(), Mockito.anyList())).thenAnswer(assetAnswer);


    }

    @Test
    public void testToAssetDto_sizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Mockito.when(converter.toAssetDto(anyList(), anyMap())).thenReturn(assetDtos);
        List<AssetDto> availableAssets = availableAssetService.toAssetDto(assets, tdAssetDetails);
        assertNotNull(availableAssets);
        Assert.assertEquals(4, availableAssets.size());
    }

    @Test
    public void testToAssetDto_sizeMatches_InvalidAssets() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        ManagedPortfolioAssetImpl mpAsset01 = new ManagedPortfolioAssetImpl();
        mpAsset01.setAssetId("28737");
        mpAsset01.setAssetCode("BR0001");
        mpAsset01.setAssetName(null);
        mpAsset01.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset01.setStatus(AssetStatus.OPEN);
        mpAsset01.setAssetClass(AssetClass.DIVERSIFIED);

        ManagedPortfolioAssetImpl mpAsset02 = new ManagedPortfolioAssetImpl();
        mpAsset02.setAssetId("28736");
        mpAsset02.setAssetCode("BR0002");
        mpAsset02.setAssetName(null);
        mpAsset02.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset02.setStatus(AssetStatus.OPEN);
        mpAsset02.setAssetClass(AssetClass.DIVERSIFIED);

        List<Asset> invalidMFAssets = new ArrayList<>();
        invalidMFAssets.add(mpAsset01);
        invalidMFAssets.add(mpAsset02);

        List<AssetDto> assetDtosTemp = new ArrayList<>();
        assetDtosTemp.add(new ManagedPortfolioAssetDto(mpAsset01));
        assetDtosTemp.add(new ManagedPortfolioAssetDto(mpAsset02));

        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Mockito.when(converter.toAssetDto(anyList(), anyMap())).thenReturn(assetDtosTemp);
        List<AssetDto> availableAssets = availableAssetService.toAssetDto(invalidMFAssets, tdAssetDetails);
        assertNotNull(availableAssets);
        Assert.assertEquals(2, availableAssets.size());
    }

    @Test
    public void testToAssetDto_InvalidTDAssets_withTDToggleOn() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        TermDepositAssetImpl tdInvAsset01 = new TermDepositAssetImpl();
        tdInvAsset01.setAssetId("65484");
        tdInvAsset01.setAssetType(AssetType.TERM_DEPOSIT);
        tdInvAsset01.setAssetClass(AssetClass.CASH);
        tdInvAsset01.setAssetName(null);
        tdInvAsset01.setStatus(AssetStatus.OPEN);

        TermDepositAssetImpl tdInvAsset02 = new TermDepositAssetImpl();
        tdInvAsset02.setAssetId("65484");
        tdInvAsset02.setAssetType(AssetType.TERM_DEPOSIT);
        tdInvAsset02.setAssetClass(AssetClass.CASH);
        tdInvAsset02.setAssetName(null);
        tdInvAsset02.setStatus(AssetStatus.OPEN);

        List<Asset> invalidTDAssets = new ArrayList<>();
        invalidTDAssets.add(tdInvAsset01);
        invalidTDAssets.add(tdInvAsset02);

        List<AssetDto> availableAssets = availableAssetService.toAssetDto(invalidTDAssets, termDepositInterestRates);
        assertNotNull(availableAssets);
        Assert.assertEquals(2, availableAssets.size());
    }


    @Test
    public void testToAssetDto_assetsEmpty() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<AssetDto> assetDtos = availableAssetService.toAssetDto(emptyAssets, tdAssetDetails);
        Assert.assertEquals(0, assetDtos.size());
    }

    @Test
    public void testGetFilteredValue_withMF() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, ApiSearchCriteria.SearchOperation.EQUALS, "MANAGED_FUND", ApiSearchCriteria.OperationType.STRING));

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put(mfAsset.getAssetId(), mfAsset);
        assetMap.put(mpAsset.getAssetId(), mpAsset);
        assetMap.put(tdAsset.getAssetId(), tdAsset);

        List<AssetDto> assetDtos = new ArrayList<>();
        assetDtos.add(new ManagedFundAssetDto(mfAsset));

        Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),Mockito.any(ServiceErrors.class))).thenReturn(assets);
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class))).thenReturn(assetMap);
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);
        Mockito.when(converter.toAssetDto(anyList(), anyMap())).thenReturn(assetDtos);


        List<AssetDto> result = availableAssetService.getFilteredValue("amc", criteriaList, serviceErrors);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetFilteredValue_withTD() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, ApiSearchCriteria.SearchOperation.EQUALS, "Term deposit", ApiSearchCriteria.OperationType.STRING));

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put(mfAsset.getAssetId(), mfAsset);
        assetMap.put(mpAsset.getAssetId(), mpAsset);
        assetMap.put(tdAsset.getAssetId(), tdAsset);

        List<AssetDto> assetDtos = new ArrayList<>();
        assetDtos.add(new ManagedFundAssetDto(mfAsset));

        Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),Mockito.any(ServiceErrors.class))).thenReturn(assets);
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class))).thenReturn(assetMap);
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);
        Mockito.when(converter.toAssetDto(anyList(), anyMap())).thenReturn(assetDtos);


        List<AssetDto> result = availableAssetService.getFilteredValue("amc", criteriaList, serviceErrors);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testSearchForOldTD() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("12345").toString(), ApiSearchCriteria.OperationType.STRING));
        final Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));

        ProductDetailImpl directOffer = new ProductDetailImpl();
        directOffer.setDirect(true);
        directOffer.setProductLevel(ProductLevel.OFFER);
        directOffer.setParentProductId("white-label");
        directOffer.setProductId("direct-offer");

        ProductDetailImpl directModel = new ProductDetailImpl();
        directModel.setDirect(true);
        directModel.setProductLevel(ProductLevel.MODEL);
        directModel.setParentProductId("direct-offer");
        directModel.setProductId("direct-model");
        List<Product> productHierarchy = new ArrayList<>();
        productHierarchy.add(directOffer);
        productHierarchy.add(directModel);

        List<Asset> assets = new ArrayList<>();
        assets.add(tdAsset);
        Map<String,Asset> filteredAssets = new HashMap<String,Asset>();
        filteredAssets.put(tdAsset.getAssetId(),tdAsset);

        List<AssetDto> assetDtos = new ArrayList<>();
        assetDtos.add(new ManagedFundAssetDto(mfAsset));


        when(productIntegrationService.loadProducts(any(ServiceErrors.class))).thenReturn(productHierarchy );

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assets);
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class)))
                .thenReturn(filteredAssets);
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);
        Mockito.when(converter.toAssetDto(assets, tdAssetDetails)).thenReturn(assetDtos);

        final List<AssetDto> result = availableAssetService.search(criteriaList, serviceErrors);
        Assert.assertEquals(1, result.size());
    }
    
    @Test
    public void testGetFilteredValue_whenAProductIdIsProvided_thenTheCorrectServiceMethodIsCalledWithTheCorrectProductId() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, ApiSearchCriteria.SearchOperation.EQUALS, "MANAGED_FUND",
                ApiSearchCriteria.OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("product-id", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("white-label").toString(), ApiSearchCriteria.OperationType.STRING));

        final Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));
        
        ProductDetailImpl directOffer = new ProductDetailImpl();
        directOffer.setDirect(true);
        directOffer.setProductLevel(ProductLevel.OFFER);
        directOffer.setParentProductId("white-label");
        directOffer.setProductId("direct-offer");
        
        ProductDetailImpl directModel = new ProductDetailImpl();
        directModel.setDirect(true);
        directModel.setProductLevel(ProductLevel.MODEL);
        directModel.setParentProductId("direct-offer");
        directModel.setProductId("direct-model");
        List<Product> productHierarchy = new ArrayList<>();
        productHierarchy.add(directOffer);
        productHierarchy.add(directModel);
        
		when(productIntegrationService.loadProducts(any(ServiceErrors.class))).thenReturn(productHierarchy );

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<Asset>());
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class)))
                .thenReturn(new HashMap<String, Asset>());
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);
        Mockito.when(converter.toAssetDto(anyList(), anyMap())).thenReturn(new ArrayList<AssetDto>());

        final List<AssetDto> result = availableAssetService.getFilteredValue("amc", criteriaList, serviceErrors);

        Mockito.verify(assetIntegrationService).loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.eq(ProductKey.valueOf("direct-model")),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(assetIntegrationService, Mockito.never()).loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testGetFilteredValue_whenAProductIdIsProvided_forTD() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, ApiSearchCriteria.SearchOperation.EQUALS, "Term deposits",
                ApiSearchCriteria.OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("product-id", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("white-label").toString(), ApiSearchCriteria.OperationType.STRING));

        final Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));

        ProductDetailImpl directOffer = new ProductDetailImpl();
        directOffer.setDirect(true);
        directOffer.setProductName(ProductToAccountType.INVESTMENTS.getProductId());
        directOffer.setProductLevel(ProductLevel.OFFER);
        directOffer.setParentProductId("white-label");
        directOffer.setProductId("direct-offer");

        ProductDetailImpl directModel = new ProductDetailImpl();
        directModel.setDirect(true);
        directModel.setProductLevel(ProductLevel.MODEL);
        directModel.setParentProductId("direct-offer");
        directModel.setProductId("direct-model");
        List<Product> productHierarchy = new ArrayList<>();
        productHierarchy.add(directOffer);
        productHierarchy.add(directModel);

        List<Asset> assets = new ArrayList<>();
        assets.add(tdAsset);
        Map<String,Asset> filteredAssets = new HashMap<String,Asset>();
        filteredAssets.put(tdAsset.getAssetId(),tdAsset);

        when(productIntegrationService.loadProducts(any(ServiceErrors.class))).thenReturn(productHierarchy );
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(directOffer );

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assets);
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class)))
                .thenReturn(filteredAssets);
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);

        final List<AssetDto> result = availableAssetService.getFilteredValue("maturity", criteriaList, serviceErrors);

        Mockito.verify(assetIntegrationService).loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.eq(ProductKey.valueOf("direct-model")),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(assetIntegrationService, Mockito.never()).loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(assetIntegrationService,times(1)).loadTermDepositRates(any(TermDepositAssetRateSearchKey.class),any(ServiceErrors.class));

    }

    @Test
    public void testSearch_whenAProductIdIsProvided_forTD() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("12345").toString(), ApiSearchCriteria.OperationType.STRING));
        final Broker adviser = mock(Broker.class);
        when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("broker1"));

        ProductDetailImpl directOffer = new ProductDetailImpl();
        directOffer.setDirect(true);
        directOffer.setProductLevel(ProductLevel.OFFER);
        directOffer.setParentProductId("white-label");
        directOffer.setProductId("direct-offer");

        ProductDetailImpl directModel = new ProductDetailImpl();
        directModel.setDirect(true);
        directModel.setProductLevel(ProductLevel.MODEL);
        directModel.setParentProductId("direct-offer");
        directModel.setProductId("direct-model");
        List<Product> productHierarchy = new ArrayList<>();
        productHierarchy.add(directOffer);
        productHierarchy.add(directModel);

        List<Asset> assets = new ArrayList<>();
        assets.add(tdAsset);
        Map<String,Asset> filteredAssets = new HashMap<String,Asset>();
        filteredAssets.put(tdAsset.getAssetId(),tdAsset);

        when(productIntegrationService.loadProducts(any(ServiceErrors.class))).thenReturn(productHierarchy );

        when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assets);
        when(assetIntegrationService.loadAssetsForCriteria(anyList(), anyString(), anyList(), any(ServiceErrors.class)))
                .thenReturn(filteredAssets);
        when(userProfileService.getDealerGroupBroker()).thenReturn(adviser);

        final List<AssetDto> result = availableAssetService.search(criteriaList, serviceErrors);
        Assert.assertEquals(1, result.size());
    }


    @Test
    public void testToAssetDtoForTDRate_sizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        List<AssetDto> availableAssets = availableAssetService.toAssetDto(assets, termDepositInterestRates);
        assertNotNull(availableAssets);
        Assert.assertEquals(4, availableAssets.size());
    }

    private SubAccount getSubAccount(String productId, ContainerType containerType) {
        SubAccount subAccount = mock(SubAccount.class);
        ProductIdentifier pid = mock(ProductIdentifier.class);
        when(pid.getProductKey()).thenReturn(ProductKey.valueOf(productId));
        when(subAccount.getProductIdentifier()).thenReturn(pid);
        when(subAccount.getSubAccountType()).thenReturn(ContainerType.DIRECT);
        return subAccount;
    }

}
