package com.bt.nextgen.service.avaloq.dashboard;

import org.springframework.stereotype.Service;

import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.Period;
import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.PeriodDet;
import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.PeriodDetHead;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.ServiceErrors;

@Service
public class PortfolioValueByBandConverter extends AbstractMappingConverter
{
	protected PortfolioValueByBandImpl toModel(
		com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.Rep report, ServiceErrors serviceErrors)
	{
		PortfolioValueByBandImpl portfolioValueByBand = null;
		Mapper mapper = getMapper();
		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			Period period = MappingUtil.singleItem(report.getData().getPeriodList().getPeriod(), serviceErrors);
			PeriodDet periodDet = MappingUtil.singleItem(period.getPeriodDetList().getPeriodDet(), serviceErrors);
			PeriodDetHead pvbandPeriodHead = MappingUtil.singleItem(periodDet.getPeriodDetHeadList().getPeriodDetHead(),
				serviceErrors);
			portfolioValueByBand = mapper.map(pvbandPeriodHead, PortfolioValueByBandImpl.class, serviceErrors);
		}
		return portfolioValueByBand;
	}
}
