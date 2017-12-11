package com.bt.nextgen.service.avaloq.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp_cash.Bp;
import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp_cash.BpHead;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;

@Service
public class TopAccountsByCashConverter extends AbstractMappingConverter
{
	protected List <TopAccountsByValueImpl> toModel(com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp_cash.Rep report,
		ServiceErrors serviceErrors)
	{
		List <TopAccountsByValueImpl> topAccounts = new ArrayList <>();
		Mapper mapper = getMapper();
		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (Bp bp : report.getData().getBpList().getBp())
			{
				BpHead bpHead = MappingUtil.singleItem(bp.getBpHeadList().getBpHead(), serviceErrors);

				TopAccountsByValueImpl topAccountsByValue = mapper.map(bpHead, TopAccountsByValueImpl.class, serviceErrors);
				topAccountsByValue.setOrderBy(Constants.CASH);
				topAccounts.add(topAccountsByValue);
			}
		}
		return topAccounts;
	}
}
