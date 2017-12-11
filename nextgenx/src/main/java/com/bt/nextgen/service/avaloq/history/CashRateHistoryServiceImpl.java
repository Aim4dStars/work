package com.bt.nextgen.service.avaloq.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.history.CashRateHistoryResponse;
import com.bt.nextgen.service.integration.history.CashRateHistoryService;
import com.bt.nextgen.service.integration.history.CashReport;

@Service
public class CashRateHistoryServiceImpl extends AbstractAvaloqIntegrationService implements CashRateHistoryService 
{
	private static final Logger logger = LoggerFactory.getLogger(CashRateHistoryServiceImpl.class);
	
	@Autowired
	private AvaloqExecute avaloqExecute;
	
	@Override
	public List<CashReport> loadCashRateHistory(final Collection<AssetKey> assetIds, final ServiceErrors serviceErrors)
	{
		final Collection<String> assets = new ArrayList<>();
		for(AssetKey assetKey : assetIds)
		{
			assets.add(assetKey.getId());
		}
		return new IntegrationSingleOperation <List<CashReport>>("loadCashRateHistory", serviceErrors)
		{
			@Override
			public List<CashReport> performOperation()
			{
				logger.info("Loading cash rate history from Avaloq");
				CashRateHistoryResponse response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.CASH_RATE_HISTORY.getName()).forAssetIds(assets), CashRateHistoryResponseImpl.class, serviceErrors);
				return response.getCashReports();
			}
		}.run();
	}
}
