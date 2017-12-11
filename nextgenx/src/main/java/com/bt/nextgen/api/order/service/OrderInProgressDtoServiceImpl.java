package com.bt.nextgen.api.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.order.model.OrderInProgressDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
@Deprecated
/**
 * @deprecated use orderDtoService
 */
public class OrderInProgressDtoServiceImpl implements OrderInProgressDtoService
{
	@Autowired
	private OrderSearchMapper orderSearchMapper;

	@Autowired
	@Qualifier("avaloqOrderIntegrationService")
	private OrderIntegrationService orderService;

	@Override
	public List <OrderInProgressDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
	{
		EncodedString accountId = null;
		for (ApiSearchCriteria parameter : criteriaList)
		{
			if (Attribute.ACCOUNT_ID.equals(parameter.getProperty()))
			{
				accountId = new EncodedString(parameter.getValue());
			}
			else
			{
				throw new IllegalArgumentException("Unsupported search");
			}
		}

		List <OrderInProgress> orders = orderService.loadInProgressOrders(accountId.plainText(), serviceErrors);
		return toOrderInProgressDto(orders, serviceErrors);
	}

	protected List <OrderInProgressDto> toOrderInProgressDto(List <OrderInProgress> orders, ServiceErrors serviceErrors)
	{
		List <OrderInProgressDto> orderInProgressDtos = new ArrayList <>();
		BigDecimal buyAmount = new BigDecimal(0);
		BigDecimal sellAmount = new BigDecimal(0);

		for (OrderInProgress order : orders)
		{
			if (order.getOrderType() != null && order.getAmount() != null)
			{
				switch (order.getOrderType().getDisplayName())
				{
					case "Buy":
					case "Application":
						buyAmount = buyAmount.add(order.getAmount().negate());
						break;
					case "Full redemption":
					case "Partial redemption":
					case "Drawdown":
						sellAmount = sellAmount.add(order.getAmount().negate());
				}
			}
		}

		OrderInProgressDto sellDto = new OrderInProgressDto("Sells", sellAmount);
		OrderInProgressDto buyDto = new OrderInProgressDto("Buys", buyAmount);
		orderInProgressDtos.add(buyDto);
		orderInProgressDtos.add(sellDto);

		return orderInProgressDtos;
	}
}
