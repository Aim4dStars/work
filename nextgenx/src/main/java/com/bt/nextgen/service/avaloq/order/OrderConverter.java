package com.bt.nextgen.service.avaloq.order;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Bp;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Cont;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Evt;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.EvtHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Pos;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Rep;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.Origin;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;

@Service
class OrderConverter extends AbstractMappingConverter
{
	private static final Logger logger = LoggerFactory.getLogger(OrderConverter.class);

	@Autowired
	private StaticIntegrationService staticService;

	public List <Order> toOrderLoadRequest(Rep report, ServiceErrors serviceErrors)
	{
		List <Order> orders = new ArrayList <>();
		Mapper mapper = getMapper();

		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (Bp bp : report.getData().getBpList().getBp())
			{
				String accountId = AvaloqGatewayUtil.asString(MappingUtil.singleItem(bp.getBpHeadList().getBpHead(), serviceErrors)
					.getBpId());

				for (Cont cont : bp.getContList().getCont())
				{
					for (Pos pos : cont.getPosList().getPos())
					{
						for (Evt evt : pos.getEvtList().getEvt())
						{
							for (EvtHead evtHead : evt.getEvtHeadList().getEvtHead())
							{
								OrderImpl order = mapper.map(evtHead, OrderImpl.class, serviceErrors);
								order.setAccountId(accountId);
								order.setOrigin(Origin.forCode(staticService.loadCode(CodeCategory.MEDIUM,
										AvaloqGatewayUtil.asString(evtHead.getMediumId()),
									serviceErrors).getIntlId()));
								order.setOrderType(OrderType.forCode(staticService.loadCode(CodeCategory.ORDER_TYPE,
										AvaloqGatewayUtil.asString(evtHead.getOrderTypeId()),
									serviceErrors).getIntlId()));
								order.setStatus(OrderStatus.forCode(staticService.loadCode(CodeCategory.ORDER_STATUS,
										AvaloqGatewayUtil.asString(evtHead.getUiWfsId()),
									serviceErrors).getIntlId()));
								orders.add(order);
							}
						}
					}
				}
			}
		}

		return orders;
	}
}
