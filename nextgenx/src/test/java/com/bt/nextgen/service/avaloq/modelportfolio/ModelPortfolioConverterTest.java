package com.bt.nextgen.service.avaloq.modelportfolio;

import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_aum.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.CashForecast;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
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
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioConverterTest
{
	@InjectMocks
	ModelPortfolioConverter modelConverter = new ModelPortfolioConverter();

	@Mock
	private StaticIntegrationService staticService;

	@Before
	public void setup()
	{
		Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				if (CodeCategory.ASSET_CLASS.equals(args[0]))
				{
					return new CodeImpl("60661", "6006_EQ_AU", "Australian Shares");
				}
				else if (CodeCategory.IPS_STATUS.equals(args[0]))
				{
					return new CodeImpl("9512", "OPN", "Open");
				}
				else if (CodeCategory.ORDER_TYPE.equals(args[0]))
				{
					return new CodeImpl("800", "B", "Buy");
				}
				else
				{
					return new CodeImpl("-1", "UNKNOWN", "Unknown");
				}
			}
		});
	}

	@Test
	public void testToModelHeader_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Rep report = JaxbUtil.unmarshall("/webservices/response/ModelHeaderBulkLoadResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, ModelPortfolio> result = modelConverter.toModelHeader(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
        ModelPortfolio modelPortfolio = result.get(IpsKey.valueOf("77159"));
		Assert.assertEquals("77159", modelPortfolio.getModelKey().getId());
		Assert.assertEquals("Open", modelPortfolio.getStatus());
		DateTime dateTime = DateTime.parse("2014-10-06T04:37:41+11:00", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(dateTime.getMillis(), modelPortfolio.getLastUpdateDate().getMillis());
	}

	@Test
	public void testToModelHeader_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Rep report = JaxbUtil.unmarshall("/webservices/response/ModelHeaderBulkLoadEmptyResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, ModelPortfolio> result = modelConverter.toModelHeader(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testToCashForecast_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Rep report = JaxbUtil.unmarshall("/webservices/response/CashForecastBulkLoadResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, CashForecast> result = modelConverter.toCashForecast(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
        CashForecast cashForecast = result.get(IpsKey.valueOf("77159"));
		Assert.assertEquals(new BigDecimal("10000000"), cashForecast.getAmountToday());
		Assert.assertEquals(new BigDecimal("20000000"), cashForecast.getAmountTodayPlus1());
		Assert.assertEquals(new BigDecimal("399954.69"), cashForecast.getAmountTodayPlus2());
		Assert.assertEquals(new BigDecimal("499954.69"), cashForecast.getAmountTodayPlus3());
		Assert.assertEquals(new BigDecimal("599954.69"), cashForecast.getAmountTodayPlusMax());
	}

	@Test
	public void testToCashForecast_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Rep report = JaxbUtil.unmarshall("/webservices/response/CashForecastBulkLoadEmptyResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, CashForecast> result = modelConverter.toCashForecast(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testToShadowPortfolioModel_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Rep report = JaxbUtil.unmarshall("/webservices/response/ShadowPortfolioBulkLoadResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, ShadowPortfolio> result = modelConverter.toShadowPortfolioModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
        ShadowPortfolio shadowPortfolio = result.get(IpsKey.valueOf("77159"));
		DateTime asAtDate = DateTime.parse("2015-03-26", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(asAtDate.getMillis(), shadowPortfolio.getAsAtDate().getMillis());
		Assert.assertEquals(2, shadowPortfolio.getAssetSummaries().size());
		ShadowPortfolioAssetSummary summary = shadowPortfolio.getAssetSummaries().get(0);
		Assert.assertEquals(3, summary.getAssets().size());
		ShadowPortfolioAsset shadowAsset = summary.getAssets().get(0);
		Assert.assertEquals("74440", shadowAsset.getAssetId());
		Assert.assertEquals(new BigDecimal("3.28"), shadowAsset.getShadowDetail().getLastUpdatedTargetPercent());
		Assert.assertEquals(new BigDecimal("3.2798322"), shadowAsset.getShadowDetail().getFloatingTargetPercent());
		Assert.assertEquals(new BigDecimal("3141"), shadowAsset.getShadowDetail().getUnits());
		Assert.assertEquals(new BigDecimal("327983.22"), shadowAsset.getShadowDetail().getMarketValue());
		Assert.assertEquals(new BigDecimal("3.2798322"), shadowAsset.getShadowDetail().getShadowPercent());
		Assert.assertEquals(new BigDecimal("0"), shadowAsset.getShadowDetail().getDifferencePercent());

		Assert.assertEquals(new BigDecimal("97"), summary.getTotal().getLastUpdatedTargetPercent());
		Assert.assertEquals(new BigDecimal("97.0003533"), summary.getTotal().getFloatingTargetPercent());
		Assert.assertNull(summary.getTotal().getUnits());
		Assert.assertEquals(new BigDecimal("9700045.31"), summary.getTotal().getMarketValue());
		Assert.assertEquals(new BigDecimal("97.0004531"), summary.getTotal().getShadowPercent());
		Assert.assertEquals(new BigDecimal("-.0000998"), summary.getTotal().getDifferencePercent());

		Assert.assertEquals(new BigDecimal("100"), shadowPortfolio.getTotal().getLastUpdatedTargetPercent());
		Assert.assertEquals(new BigDecimal("100"), shadowPortfolio.getTotal().getFloatingTargetPercent());
		Assert.assertNull(shadowPortfolio.getTotal().getUnits());
		Assert.assertEquals(new BigDecimal("10000000"), shadowPortfolio.getTotal().getMarketValue());
		Assert.assertEquals(new BigDecimal("100"), shadowPortfolio.getTotal().getShadowPercent());
		Assert.assertEquals(new BigDecimal("0"), shadowPortfolio.getTotal().getDifferencePercent());
	}

	@Test
	public void testToShadowPortfolioModel_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Rep report = JaxbUtil.unmarshall("/webservices/response/ShadowPortfolioBulkLoadEmptyResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, ShadowPortfolio> result = modelConverter.toShadowPortfolioModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testToShadowTransactionModel_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Rep report = JaxbUtil.unmarshall("/webservices/response/ShadowTransactionBulkLoadResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, List<ShadowTransaction>> result = modelConverter.toShadowTransactionModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
        List<ShadowTransaction> transactions = result.get(IpsKey.valueOf("77159"));
		Assert.assertEquals(14, transactions.size());
		ShadowTransaction cashTransaction = transactions.get(0);
		Assert.assertEquals("252179", cashTransaction.getTransactionId());
		Assert.assertEquals("Buy", cashTransaction.getTransactionType());
		Assert.assertEquals("51094", cashTransaction.getAssetId());
		Assert.assertEquals("MP Cash", cashTransaction.getAssetHolding());
		Assert.assertEquals("Unconfirmed", cashTransaction.getStatus());
		DateTime tradeDate = DateTime.parse("2015-03-27", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(tradeDate.getMillis(), cashTransaction.getTradeDate().getMillis());
		DateTime valueDate = DateTime.parse("2015-04-01", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(valueDate.getMillis(), cashTransaction.getValueDate().getMillis());
		DateTime performanceDate = DateTime.parse("2015-03-27", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(performanceDate.getMillis(), cashTransaction.getPerformanceDate().getMillis());
		Assert.assertEquals(new BigDecimal("-876998.42"), cashTransaction.getAmount());
		Assert.assertEquals("Bought 115699 units of BOQ @  AUD, Brokerage 0 AUD", cashTransaction.getDescription());
		ShadowTransaction shareTransaction = transactions.get(1);
		Assert.assertEquals("252179", shareTransaction.getTransactionId());
		Assert.assertEquals("Buy", shareTransaction.getTransactionType());
		Assert.assertEquals("56139", shareTransaction.getAssetId());
		Assert.assertEquals("BOQ Bank of Queensland Ltd", shareTransaction.getAssetHolding());
		Assert.assertEquals("Unconfirmed", shareTransaction.getStatus());
		Assert.assertEquals(tradeDate.getMillis(), shareTransaction.getTradeDate().getMillis());
		Assert.assertEquals(valueDate.getMillis(), shareTransaction.getValueDate().getMillis());
		Assert.assertEquals(performanceDate.getMillis(), shareTransaction.getPerformanceDate().getMillis());
		Assert.assertEquals(new BigDecimal("115699"), shareTransaction.getAmount());
		Assert.assertEquals("Bought 115699 units of BOQ @  AUD, Brokerage 0 AUD", shareTransaction.getDescription());
	}

	@Test
	public void testToShadowTransactionModel_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Rep report = JaxbUtil.unmarshall("/webservices/response/ShadowTransactionBulkLoadEmptyResponse_UT.xml",
			Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<IpsKey, List<ShadowTransaction>> result = modelConverter.toShadowTransactionModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
}
