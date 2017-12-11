package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.CashForecastDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ShadowTransactionDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.CashForecastImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioSummaryImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioAssetSummaryImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioDetailImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowTransactionImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioDtoServiceTest
{
	@InjectMocks
    private ModelPortfolioDtoServiceImpl modelPortfolioDtoService;

	@Mock
	private ModelPortfolioIntegrationService modelPortfolioService;

	@Mock
	private BrokerIntegrationService brokerService;

	@Mock
	private AssetIntegrationService assetService;

    @Mock
    private FinancialDocumentIntegrationService documentService;

    @Mock
    private ModelPortfolioHelper helper;

    @Mock
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    @Mock
    private UserProfileService userProfileService;

    IpsKey emptyModelKey;
    IpsKey modelKey;
	List <ModelPortfolio> emptyModelPortfolioList;
	CashForecastImpl cashForecast;
	ShadowPortfolioImpl shadowPortfolio;
	List <ShadowPortfolioAssetSummary> shadowAssetSummaries;
	ShadowPortfolioAssetSummaryImpl shadowAssetSummary;
	List <ShadowPortfolioAsset> shadowAssets;
	ShadowPortfolioAssetImpl shadowAsset;
	ShadowPortfolioDetailImpl shadowDetail;
	List <ShadowTransaction> shadowTransactions;
	ShadowTransactionImpl shadowTransaction;
	ModelPortfolioImpl model1;
	ModelPortfolioSummaryImpl model1Summary;

	ModelPortfolioImpl model2;
	ModelPortfolioSummaryImpl model2Summary;

    ModelPortfolioImpl modelEmpty;
    ModelPortfolioSummaryImpl modelSummaryEmpty;
	List <ModelPortfolio> modelPortfolioList;
	List <ModelPortfolioSummary> modelPortfolioSummaryList;

	Collection <Broker> brokers;
	Map <String, Asset> assetMap;
	AssetImpl asset;

	@Before
	public void setup() throws Exception
	{
		modelPortfolioList = new ArrayList <>();
		modelPortfolioSummaryList = new ArrayList <>();
		emptyModelPortfolioList = new ArrayList <>();
        emptyModelKey = IpsKey.valueOf("99999");
        modelKey = IpsKey.valueOf("11111");

		cashForecast = new CashForecastImpl();
		cashForecast.setAmountToday(new BigDecimal("11"));
		cashForecast.setAmountTodayPlus1(new BigDecimal("22"));
		cashForecast.setAmountTodayPlus2(new BigDecimal("33"));
		cashForecast.setAmountTodayPlus3(new BigDecimal("44"));
		cashForecast.setAmountTodayPlusMax(new BigDecimal("55"));

		shadowPortfolio = new ShadowPortfolioImpl();
		shadowPortfolio.setAsAtDate(new DateTime());
		shadowDetail = new ShadowPortfolioDetailImpl();
		shadowDetail.setLastUpdatedTargetPercent(new BigDecimal("11"));
		shadowDetail.setFloatingTargetPercent(new BigDecimal("22"));
		shadowDetail.setUnits(new BigDecimal("33"));
		shadowDetail.setMarketValue(new BigDecimal("44"));
		shadowDetail.setShadowPercent(new BigDecimal("55"));
		shadowDetail.setDifferencePercent(new BigDecimal("66"));
		shadowAssetSummaries = new ArrayList <ShadowPortfolioAssetSummary>();
		shadowAssetSummary = new ShadowPortfolioAssetSummaryImpl();
		shadowAssetSummary.setAssetClass("Cash");
		shadowAssetSummary.setTotal(shadowDetail);
		shadowAssets = new ArrayList <>();
		shadowAsset = new ShadowPortfolioAssetImpl();
		shadowAsset.setAssetId("1234");
		shadowAsset.setShadowDetail(shadowDetail);
		shadowAssets.add(shadowAsset);
		shadowAssetSummary.setAssets(shadowAssets);
		shadowPortfolio.setTotal(shadowDetail);
		shadowAssetSummaries.add(shadowAssetSummary);
		shadowPortfolio.setAssetSummaries(shadowAssetSummaries);

		shadowTransactions = new ArrayList <>();
		shadowTransaction = new ShadowTransactionImpl();
		shadowTransaction.setTransactionId("1111");
		shadowTransaction.setTransactionType("Buy");
		shadowTransaction.setAssetId("5678");
		shadowTransaction.setAssetHolding("BHP");
		shadowTransaction.setStatus("Confirmed");
		shadowTransaction.setTradeDate(new DateTime());
		shadowTransaction.setValueDate(new DateTime());
		shadowTransaction.setPerformanceDate(new DateTime());
		shadowTransaction.setAmount(new BigDecimal("11"));
		shadowTransaction.setDescription("description");
		shadowTransactions.add(shadowTransaction);

		model1 = new ModelPortfolioImpl();
		model1.setModelKey(modelKey);
		model1.setLastUpdateDate(new DateTime());
		model1.setStatus("In Progress");
		model1.setCashForecast(cashForecast);
		model1.setShadowPortfolio(shadowPortfolio);
		model1.setShadowTransactions(shadowTransactions);
		model1Summary = new ModelPortfolioSummaryImpl();
		model1Summary.setModelKey(model1.getModelKey());
		model1Summary.setModelName("model1 name");
		model1Summary.setModelCode("model1 code");

		model2 = new ModelPortfolioImpl();
        model2.setModelKey(IpsKey.valueOf("22222"));
		model2.setLastUpdateDate(new DateTime());
		model2.setStatus("Open");
		model2Summary = new ModelPortfolioSummaryImpl();
        model2Summary.setModelKey(model2.getModelKey());
		model2Summary.setModelName("model2 name");
		model2Summary.setModelCode("model2 code");
		model2Summary.setVoluntaryCorporateActions(BigDecimal.ONE);
		model2Summary.setMandatoryCorporateActions(BigDecimal.ONE);

        modelEmpty = new ModelPortfolioImpl();
        modelEmpty.setModelKey(IpsKey.valueOf("33333"));
        modelEmpty.setLastUpdateDate(new DateTime());
        modelEmpty.setStatus("Open");
        modelSummaryEmpty = new ModelPortfolioSummaryImpl();
        modelSummaryEmpty.setModelKey(modelEmpty.getModelKey());

		modelPortfolioList.add(model1);
        modelPortfolioList.add(modelEmpty);
		modelPortfolioList.add(model2);
		modelPortfolioSummaryList.add(model1Summary);
        modelPortfolioSummaryList.add(modelSummaryEmpty);
		modelPortfolioSummaryList.add(model2Summary);

		Mockito.when(modelPortfolioService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(modelPortfolioList);

		brokers = new ArrayList <Broker>();
		BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("testUser"), BrokerType.INVESTMENT_MANAGER);
		brokers.add(broker);

		Mockito.when(brokerService.getBrokersForUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(brokers);

		asset = new AssetImpl();
		asset.setAssetId("28737");
		asset.setAssetCode("AUM");
		asset.setAssetType(AssetType.CASH);
		asset.setAssetName("Cash");
		assetMap = new HashMap <String, Asset>();
		assetMap.put("28737", asset);

		Mockito.when(assetService.loadAssets(Mockito.anySet(), Mockito.any(ServiceErrors.class)))
			.thenAnswer(new Answer <Object>()
			{
				@Override
				public Object answer(InvocationOnMock invocation)
				{
					Map <String, Asset> assets = new HashMap <>();
					Object[] args = invocation.getArguments();
					Set <String> assetIds = (Set <String>)args[0];
					for (String assetId : assetIds)
					{
						assets.put(assetId, null);
					}

					return assets;
				}
			});

        Mockito.when(helper.getCurrentBroker(Mockito.any(ServiceErrorsImpl.class))).thenReturn(BrokerKey.valueOf("brokerKey"));
	}

	@Test
	public void testToModelPortfolioDto_whenNoValues_thenDtoListEmpty()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <ModelPortfolioDto> models = modelPortfolioDtoService.toModelPortfolioDto(emptyModelPortfolioList,
			modelPortfolioSummaryList,
			serviceErrors);
		Assert.assertEquals(0, models.size());
	}

	@Test
	public void testToModelPortfolioDto_whenValues_thenSizeMatches()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <ModelPortfolioDto> models = modelPortfolioDtoService.toModelPortfolioDto(modelPortfolioList,
			modelPortfolioSummaryList,
			serviceErrors);
		assertNotNull(models);
        Assert.assertEquals(3, models.size());
	}

	@Test
	public void testToModelPortfolioDto_whenValue_thenDtoMatches()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <ModelPortfolioDto> models = modelPortfolioDtoService.toModelPortfolioDto(modelPortfolioList,
			modelPortfolioSummaryList,
			serviceErrors);
		Assert.assertEquals(modelPortfolioList.get(0).getModelKey().getId(), models.get(0).getKey().getModelId());
		Assert.assertEquals(modelPortfolioSummaryList.get(0).getModelName(), models.get(0).getModelName());
		Assert.assertEquals(modelPortfolioSummaryList.get(0).getModelCode(), models.get(0).getModelCode());
		Assert.assertEquals(modelPortfolioList.get(0).getShadowPortfolio().getAsAtDate(), models.get(0).getAsAtDate());
		Assert.assertEquals(modelPortfolioList.get(0).getLastUpdateDate(), models.get(0).getLastUpdateDate());
		Assert.assertEquals(StringUtils.capitalize(modelPortfolioList.get(0).getStatus().toLowerCase()), models.get(0)
			.getStatus());
		Assert.assertEquals(cashForecast.getAmountToday(), models.get(0).getCashForecast().getAmountToday());
		Assert.assertEquals(shadowPortfolio.getAssetSummaries().get(0).getAssets().get(0).getShadowDetail().getMarketValue(),
			models.get(0).getShadowPortfolios().get(0).getMarketValue());
		Assert.assertEquals(shadowPortfolio.getAssetSummaries()
			.get(0)
			.getAssets()
			.get(0)
			.getShadowDetail()
			.getLastUpdatedTargetPercent(), models.get(0).getShadowPortfolios().get(0).getLastUpdatedTargetPercent());
		Assert.assertEquals(shadowTransaction.getTransactionId(), models.get(0).getShadowTransactions().get(0).getTransactionId());
	}

	@Test
	public void testToCashForecastDto_whenValue_thenDtoMatches()
	{
		CashForecastDto cashForecastDto = modelPortfolioDtoService.toCashForecastDto(cashForecast);
		Assert.assertEquals(cashForecast.getAmountToday(), cashForecastDto.getAmountToday());
		Assert.assertEquals(cashForecast.getAmountTodayPlus1(), cashForecastDto.getAmountTodayPlus1());
		Assert.assertEquals(cashForecast.getAmountTodayPlus2(), cashForecastDto.getAmountTodayPlus2());
		Assert.assertEquals(cashForecast.getAmountTodayPlus3(), cashForecastDto.getAmountTodayPlus3());
		Assert.assertEquals(cashForecast.getAmountTodayPlusMax(), cashForecastDto.getAmountTodayPlusMax());
	}

	@Test
	public void testShadowTransactionDto_whenValues_thenSizeMatches()
	{
		List <ShadowTransactionDto> shadowTransactionDtos = modelPortfolioDtoService.toShadowTransactionDto(shadowTransactions,
			assetMap);
		assertNotNull(shadowTransactionDtos);
		Assert.assertEquals(1, shadowTransactionDtos.size());
	}

	@Test
	public void testToShadowTransactionDto_whenValue_thenDtoMatches()
	{
		ShadowTransactionDto shadowTransactionDto = modelPortfolioDtoService.toShadowTransactionDto(shadowTransaction,
			AssetType.CASH);
		Assert.assertEquals(shadowTransaction.getTransactionId(), shadowTransactionDto.getTransactionId());
		Assert.assertEquals(shadowTransaction.getTransactionType(), shadowTransactionDto.getTransactionType());
		Assert.assertEquals(shadowTransaction.getAssetHolding(), shadowTransactionDto.getAssetHolding());
		Assert.assertEquals(shadowTransaction.getStatus(), shadowTransactionDto.getStatus());
		Assert.assertEquals(shadowTransaction.getTradeDate(), shadowTransactionDto.getTradeDate());
		Assert.assertEquals(shadowTransaction.getValueDate(), shadowTransactionDto.getValueDate());
		Assert.assertEquals(shadowTransaction.getPerformanceDate(), shadowTransactionDto.getPerformanceDate());
		Assert.assertEquals(shadowTransaction.getAmount(), shadowTransactionDto.getAmount());
		Assert.assertNull(shadowTransactionDto.getQuantity());
		Assert.assertEquals(shadowTransaction.getDescription(), shadowTransactionDto.getDescription());
	}

	@Test
	public void testToShadowTransactionDto_whenNotCash_thenQuantity()
	{
		ShadowTransactionDto shadowTransactionDto = modelPortfolioDtoService.toShadowTransactionDto(shadowTransaction,
			AssetType.SHARE);
		Assert.assertEquals(shadowTransaction.getAmount(), shadowTransactionDto.getQuantity());
		Assert.assertNull(shadowTransactionDto.getAmount());
	}


	@Test
	public void testGetAssetsForModels_whenValues_thenAssets()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Map <String, Asset> assets = modelPortfolioDtoService.getAssetsForModels(model1, serviceErrors);
		Assert.assertEquals(2, assets.size());
	}

    @Test
    public void testFind_whenKeyProvided_thenModelPortfolioDtoReturned() {
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioSummaryList);
        Mockito.when(modelPortfolioService.loadModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                model2);

        ModelPortfolioDto dto = modelPortfolioDtoService.find(new ModelPortfolioKey("22222"), new ServiceErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("22222", dto.getKey().getModelId());
    }

    @Test
    public void testFind_whenNoSummary_thenSummaryFieldsEmptyInDto() {
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.<ModelPortfolioSummary> emptyList());
        Mockito.when(modelPortfolioService.loadModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                model2);

        ModelPortfolioDto dto = modelPortfolioDtoService.find(new ModelPortfolioKey("22222"), new ServiceErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("22222", dto.getKey().getModelId());
        Assert.assertNull(dto.getModelName());
        Assert.assertNull(dto.getModelCode());
    }

    @Test
    public void testFindAll_whenCalled_thenModelPortfolioDtoReturned() {
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioSummaryList);

        List<ModelPortfolioDto> dtos = modelPortfolioDtoService.findAll(new ServiceErrorsImpl());

        Assert.assertNotNull(dtos);
        Assert.assertEquals(3, dtos.size());
    }

    @Test
    public void testLoadMonthlyModelDocument_whenCalled_thenDocumentReturned() {
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioSummaryList);
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBankReferenceId()).thenReturn("123");
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Mockito.when(
                documentService.loadIMDocument(Mockito.anyString(), Mockito.anyString(), Mockito.any(DateTime.class),
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<FinancialDocumentData>() {
                    @Override
                    public FinancialDocumentData answer(InvocationOnMock invocation) {
                        Assert.assertEquals(FinancialDocumentType.IMMODEL.getCode(), invocation.getArguments()[0]);
                        Assert.assertEquals("model2 code", invocation.getArguments()[1]);
                        Assert.assertEquals("123", invocation.getArguments()[3]);
                        Assert.assertEquals("INVST_MGR", invocation.getArguments()[4]);

                        return Mockito.mock(FinancialDocumentData.class);
                    }
                });

        FinancialDocumentData document = modelPortfolioDtoService
                .loadMonthlyModelDocument("22222", FinancialDocumentType.IMMODEL);
        Assert.assertNotNull(document);
    }

    @Test
    public void testLoadMonthlyModelDocument_whenCalledWithNoModelId_thenDocumentReturned() {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBankReferenceId()).thenReturn("123");
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Mockito.when(
                documentService.loadIMDocument(Mockito.anyString(), Mockito.anyString(), Mockito.any(DateTime.class),
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<FinancialDocumentData>() {
                    @Override
                    public FinancialDocumentData answer(InvocationOnMock invocation) {
                        Assert.assertEquals(FinancialDocumentType.IMMODEL.getCode(), invocation.getArguments()[0]);
                        Assert.assertNull(invocation.getArguments()[1]);
                        Assert.assertEquals("123", invocation.getArguments()[3]);
                        Assert.assertEquals("INVST_MGR", invocation.getArguments()[4]);

                        return Mockito.mock(FinancialDocumentData.class);
                    }
                });

        FinancialDocumentData document = modelPortfolioDtoService.loadMonthlyModelDocument(null, FinancialDocumentType.IMMODEL);
        Assert.assertNotNull(document);
    }
}