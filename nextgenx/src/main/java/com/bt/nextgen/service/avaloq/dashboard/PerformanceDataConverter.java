package com.bt.nextgen.service.avaloq.dashboard;

import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.Period;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.PeriodDet;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.PeriodDetHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.PeriodHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.Rep;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.TopHead;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.dashboard.PerformanceData;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class PerformanceDataConverter extends AbstractMappingConverter
{
	@Autowired
	private StaticIntegrationService staticIntegrationService;

	@SuppressWarnings({"squid:UnusedProtectedMethod","squid:MethodCyclomaticComplexity"})

	protected AdviserPerformanceImpl toModel(Rep report,
											 ServiceErrors serviceErrors)
	{
		AdviserPerformanceImpl advPerformance = new AdviserPerformanceImpl();

		Mapper mapper = getMapper();
		List <PerformanceData> performanceList=null;
		List<PerformanceData> totalValue=null;
		// This check is added because Avaloq currently has a bug where the
		// isEmpty is always set to true regardless whether data does exist or not.
		// Fix will not make it in time for R1.
		// TODO: Remove this once Avaloq releases their fix.
		boolean isEmpty = false;
		if (MappingUtil.isEmpty(report, serviceErrors))
		{
			// Verify that there is no data.
			isEmpty = (report.getData() == null || report.getData().getTop() == null || report.getData().getTop().getTopHeadList() == null ||  report.getData().getTop().getPeriodList() == null);
		}else{

			isEmpty = (report.getData() == null || report.getData().getTop() == null ||  report.getData().getTop().getPeriodList() == null);
		}

		if (!isEmpty) {
			// Loop through and retrieve data for each period
			performanceList = new ArrayList<>();
			totalValue=new ArrayList<>();

			TopHead topHead = MappingUtil.singleItem(report.getData().getTop().getTopHeadList().getTopHead(),
					serviceErrors);
			totalValue.add(mapper.map(topHead, PerformanceDataImpl.class, serviceErrors));
			advPerformance.setPeriodPerformanceData(totalValue);
			for (Period period : report.getData().getTop().getPeriodList().getPeriod()) {

				for (PeriodDet periodDet : period.getPeriodDetList().getPeriodDet()) {

					for(PeriodDetHead periodDetHead : periodDet.getPeriodDetHeadList().getPeriodDetHead()) {
						performanceList.add(mapper.map(periodDetHead, PerformanceDataImpl.class, serviceErrors));

					}
				}
				if (!performanceList.isEmpty()) {
					PeriodHead periodHead = MappingUtil.singleItem(period.getPeriodHeadList().getPeriodHead(), serviceErrors);
					String periodId = AvaloqGatewayUtil.asString(periodHead.getPeriodId());
					Code code = staticIntegrationService.loadCode(CodeCategory.PERF_PERIOD, periodId, serviceErrors);
					PerformancePeriodType periodType = PerformancePeriodType.forCode(code.getIntlId());
					advPerformance.setPerformanceData(periodType, performanceList);
				}

			}


		}
		return advPerformance;

	}


}