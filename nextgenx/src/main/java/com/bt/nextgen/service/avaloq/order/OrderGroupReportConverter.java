package com.bt.nextgen.service.avaloq.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Doc;
import com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.DocHead;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.ServiceErrors;

@Service
class OrderGroupReportConverter extends AbstractMappingConverter
{
	protected List <OrderGroupImpl> toModel(com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep report,
		ServiceErrors serviceErrors)
	{
		List <OrderGroupImpl> savedOrders = new ArrayList <>();
		Mapper mapper = getMapper();

		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (Doc doc : report.getData().getDocList().getDoc())
			{
				DocHead docHead = MappingUtil.singleItem(doc.getDocHeadList().getDocHead(), serviceErrors);
				OrderGroupImpl orders = mapper.map(docHead, OrderGroupImpl.class, serviceErrors);
				savedOrders.add(orders);
			}
		}

		return savedOrders;
	}
}
