package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.service.helper.OrderItemHelper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapperImpl;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RIPScheduleImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentBaseDtoServiceTest {

    @InjectMocks
    private RegularInvestmentBaseDtoServiceImpl regularInvestmentBaseService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Mock
    private OrderItemHelper orderItemHelper;

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Spy
    private final OrderGroupDtoErrorMapper orderDtoErrorMapper = new OrderGroupDtoErrorMapperImpl();

    private List<OrderItem> orderItems;
    private OrderItemSummaryImpl summary;
    private OrderItemImpl buyOrderModel1;
    private RegularInvestmentImpl regInv;
    private OrderGroupImpl orderGroup;

    private RegularInvestmentDto invDto;
    private RecurringDepositDetailsImpl recurrDeposit;
    private WrapAccountValuationImpl valuation;

    @Before
    public void setup() throws Exception {

        UserProfile userInfo = Mockito.mock(UserProfile.class);
        Mockito.when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userInfo);

        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf("AccountKey"));
        valuation.setSubAccountValuations(new ArrayList<SubAccountValuation>());
        Mockito.when(
                cachedPortfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                        Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        setupRegularInvestmentDto();
        setupRegularInvestment();
    }

    @Test
    public void testToRegularInvestment_nullDto_NoExceptionThrown() {
        RegularInvestmentDto dto = null;
        RegularInvestment inv = regularInvestmentBaseService.toRegularInvestment(dto, new ServiceErrorsImpl());
        Assert.assertNull(inv);
    }

    @Test
    public void testToRegularInvestment_NoDepositDetailDto() {
        RegularInvestment inv = regularInvestmentBaseService.toRegularInvestment(invDto, new ServiceErrorsImpl());
        Assert.assertNotNull(inv);
    }

    @Test
    public void testToRegularInvestmentDto_NoDepositDetails() {
        RegularInvestmentDto dto = regularInvestmentBaseService.toRegularInvestmentDto(regInv, invDto, new ServiceErrorsImpl(),
                new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(RIPStatus.SUSPENDED.getDisplayName(), dto.getRipStatus());
    }

    @Test
    public void testToRegularInvestmentDto_NullDto_NoDepositDetails() {
        RegularInvestmentDto dto = regularInvestmentBaseService.toRegularInvestmentDto(regInv, null, new ServiceErrorsImpl(),
                new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(RIPStatus.SUSPENDED.getDisplayName(), dto.getRipStatus());
        Assert.assertNull(dto.getCashAccountDto());
    }

    private void setupRegularInvestment() {
        orderItems = new ArrayList<>();

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.CASH.getDisplayName(), null, null,
                null, null);
        buyOrderModel1 = mock(OrderItemImpl.class);
        when(buyOrderModel1.getAmount()).thenReturn(BigDecimal.TEN);
        when(buyOrderModel1.getAssetId()).thenReturn("assetId");
        when(buyOrderModel1.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        when(buyOrderModel1.getOrderId()).thenReturn("orderId");
        when(buyOrderModel1.getOrderType()).thenReturn("orderType");
        when(buyOrderModel1.getIsFull()).thenReturn(Boolean.FALSE);
        when(buyOrderModel1.getDistributionMethod()).thenReturn("distributionMethod");
        when(buyOrderModel1.getFundsSource()).thenReturn(null);
        when(buyOrderModel1.getUnits()).thenReturn(BigInteger.TEN);
        when(buyOrderModel1.getPrice()).thenReturn(BigDecimal.ONE);
        when(buyOrderModel1.getExpiry()).thenReturn("");
        when(buyOrderModel1.getPriceType()).thenReturn(PriceType.LIMIT);

        orderItems.add(buyOrderModel1);

        orderGroup = new OrderGroupImpl();
        orderGroup.setOrders(orderItems);

        regInv = new RegularInvestmentImpl(this.orderGroup);
        regInv.setAccountKey(AccountKey.valueOf("accountId"));
        regInv.setRIPSchedule(new RIPScheduleImpl(DateTime.now(), getDefaultEndDate(), RIPRecurringFrequency.Quarterly));
        regInv.setRIPStatus(RIPStatus.SUSPENDED);
    }

    private void setupRegularInvestmentDto() {
        invDto = mock(RegularInvestmentDto.class);
        when(invDto.getKey()).thenReturn(new OrderGroupKey("accountId", "orderGroupId"));
        when(invDto.getLastUpdateDate()).thenReturn(new DateTime());
        when(invDto.getTransactionSeq()).thenReturn(BigInteger.ONE);
        when(invDto.getWarnings()).thenReturn(null);
        when(invDto.getOwner()).thenReturn("owner");
        when(invDto.getOwnerName()).thenReturn("ownerName");
        when(invDto.getReference()).thenReturn("reference");
        when(invDto.getAccountName()).thenReturn("accountName");
        when(invDto.getAccountKey()).thenReturn(
                new com.bt.nextgen.api.account.v3.model.AccountKey("9BE792880789BE27BDF51D56C27A8B10EE538C9A0834620A"));

        when(invDto.getDepositDetails()).thenReturn(null);
        when(invDto.getInvestmentAmount()).thenReturn(BigDecimal.valueOf(1000d));
        when(invDto.getInvestmentStartDate()).thenReturn(DateTime.now());
        when(invDto.getInvestmentEndDate()).thenReturn(DateTime.now().plusDays(10));
        when(invDto.getStatus()).thenReturn(RIPStatus.ACTIVE.getDisplayName());
        when(invDto.getFrequency()).thenReturn(RIPRecurringFrequency.Fortnightly.name());
    }

    private DateTime getDefaultEndDate() {
        return DateTime.now().plusYears(2);
    }
}
