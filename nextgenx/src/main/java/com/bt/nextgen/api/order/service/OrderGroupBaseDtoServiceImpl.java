package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.service.helper.BrokerHelper;
import com.bt.nextgen.api.order.service.helper.OrderItemHelper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.joda.time.DateMidnight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class OrderGroupBaseDtoServiceImpl implements OrderGroupBaseDtoService {

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Autowired
    protected OrderGroupDtoErrorMapper orderDtoErrorMapper;

    @Autowired
    protected UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    protected AccountIntegrationService accountIntegrationService;

    @Autowired
    private BrokerHelper brokerHelper;

    @Autowired
    private OrderItemHelper orderItemHelper;

    @Override
    public OrderGroupDto toOrderGroupDto(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        String accountName = null;

        if (orderGroup.getAccountKey() != null) {
            WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(orderGroup.getAccountKey(),
                    serviceErrors);
            accountName = account != null ? account.getAccountName() : "";
        }

        String adviserFullName = orderGroup.getOwnerName();
        if (adviserFullName == null && orderGroup.getOwner() != null) {
            adviserFullName = brokerHelper.getAdviserFullName(orderGroup.getOwner().getId(), serviceErrors);
        }

        AccountKey accountKey = orderGroup.getAccountKey();

        OrderGroupDto orderGroupDto = new OrderGroupDto(orderGroup.getOrderGroupId() == null ? null : new OrderGroupKey(
                EncodedString.fromPlainText(accountKey.getId()).toString(), orderGroup.getOrderGroupId()),
                orderGroup.getLastUpdateDate(), orderGroup.getTransactionSeq(), orderItemHelper.toOrdersDto(accountKey,
                        orderGroup.getOrders(), serviceErrors), orderDtoErrorMapper.map(orderGroup.getWarnings()),
                orderGroup.getOwner() == null ? null : EncodedString.fromPlainText(orderGroup.getOwner().getId()).toString(),
                adviserFullName, orderGroup.getReference(), accountName, new com.bt.nextgen.api.account.v3.model.AccountKey(
                        EncodedString.fromPlainText(accountKey.getId()).toString()));
        orderGroupDto.setFirstNotification(orderGroup.getFirstNotification());
        return orderGroupDto;
    }

    @Override
    public OrderGroup toOrderGroup(OrderGroupDto orderGroupDto, ServiceErrors serviceErrors) {
        UserInformation userInfo = userProfileService.getActiveProfile();
        ClientKey userClient = userInfo.getClientKey();
        AccountKey wrapAccountKey = AccountKey.valueOf(EncodedString.toPlainText(orderGroupDto.getAccountKey().getAccountId()));

        WrapAccountValuation valuation = cachedPortfolioIntegrationService.loadWrapAccountValuation(
                                            wrapAccountKey, 
                                            DateMidnight.now().toDateTime(), serviceErrors);

        return new OrderGroupImpl(wrapAccountKey, orderGroupDto.getKey() == null ? null : orderGroupDto.getKey()
                .getOrderGroupId(), userClient, orderGroupDto.getLastUpdateDate(), orderGroupDto.getTransactionSeq(),
                orderItemHelper.toOrderItems(orderGroupDto.getOrders(), valuation), orderDtoErrorMapper.mapWarnings(orderGroupDto
                        .getWarnings()), orderGroupDto.getReference());
    }

}