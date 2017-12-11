package com.bt.nextgen.api.modelportfolio.v2.util.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RebalanceOrdersSortingHelperTest {

    @InjectMocks
    private RebalanceOrdersSortingHelper sortingHelper;

    private List<RebalanceOrderGroupDto> orderGroupDtoList;

    @Before
    public void setup() {

        RebalanceOrderDetailsDto details1 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details1.getAccountName()).thenReturn("Joe");
        Mockito.when(details1.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details1.getAssetClass()).thenReturn("Australian Shares");

        RebalanceOrderDetailsDto details2 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details2.getAccountName()).thenReturn("Joe");
        Mockito.when(details2.getAssetName()).thenReturn("Woolworths");
        Mockito.when(details2.getAssetClass()).thenReturn("Australian Shares");

        RebalanceOrderDetailsDto details3 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details3.getAccountName()).thenReturn("Joe");
        Mockito.when(details3.getAssetName()).thenReturn("Advance Balance Multi-Blend Fund");
        Mockito.when(details3.getAssetClass()).thenReturn("Diversified");

        RebalanceOrderDetailsDto details4 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details4.getAccountName()).thenReturn("Joe");
        Mockito.when(details4.getAssetName()).thenReturn("TP Cash");
        Mockito.when(details4.getAssetClass()).thenReturn("Cash");

        RebalanceOrderDetailsDto details5 = Mockito.mock(RebalanceOrderDetailsDto.class);
        Mockito.when(details5.getAccountName()).thenReturn("Jane");
        Mockito.when(details5.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(details5.getAssetClass()).thenReturn("Australian Shares");

        RebalanceOrderGroupDto group1 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(group1.getAdviserName()).thenReturn("Manny");
        Mockito.when(group1.getOrderDetails()).thenReturn(Arrays.asList(details1, details2, details3, details4, details5));

        RebalanceOrderGroupDto group2 = Mockito.mock(RebalanceOrderGroupDto.class);
        Mockito.when(group2.getAdviserName()).thenReturn("Danny");
        Mockito.when(group2.getOrderDetails()).thenReturn(Arrays.asList(details5));

        orderGroupDtoList = Arrays.asList(group1, group2);
    }

    @Test
    public void test_basicSort() {
        sortingHelper.basicSort(orderGroupDtoList);

        RebalanceOrderGroupDto firstGroup = orderGroupDtoList.get(0);
        Assert.assertEquals("Danny", firstGroup.getAdviserName());

        RebalanceOrderGroupDto secondGroup = orderGroupDtoList.get(1);
        Assert.assertEquals("Manny", secondGroup.getAdviserName());

        List<RebalanceOrderDetailsDto> detailsList = secondGroup.getOrderDetails();

        RebalanceOrderDetailsDto first = detailsList.get(0);
        Assert.assertEquals("Advance Balance Multi-Blend Fund", first.getAssetName());

        RebalanceOrderDetailsDto second = detailsList.get(1);
        Assert.assertEquals("BHP Billiton", second.getAssetName());

        RebalanceOrderDetailsDto third = detailsList.get(2);
        Assert.assertEquals("BHP Billiton", third.getAssetName());

        RebalanceOrderDetailsDto fourth = detailsList.get(3);
        Assert.assertEquals("Woolworths", fourth.getAssetName());

        RebalanceOrderDetailsDto fifth = detailsList.get(4);
        Assert.assertEquals("TP Cash", fifth.getAssetName());

    }

    @Test
    public void test_detailedSort() {
        sortingHelper.detailedSort(orderGroupDtoList);

        RebalanceOrderGroupDto firstGroup = orderGroupDtoList.get(0);
        Assert.assertEquals("Danny", firstGroup.getAdviserName());

        RebalanceOrderGroupDto secondGroup = orderGroupDtoList.get(1);
        Assert.assertEquals("Manny", secondGroup.getAdviserName());

        List<RebalanceOrderDetailsDto> detailsList = secondGroup.getOrderDetails();

        RebalanceOrderDetailsDto first = detailsList.get(0);
        Assert.assertEquals("Jane", first.getAccountName());
        Assert.assertEquals("BHP Billiton", first.getAssetName());
        Assert.assertEquals("Australian Shares", first.getAssetClass());

        RebalanceOrderDetailsDto second = detailsList.get(1);
        Assert.assertEquals("Joe", second.getAccountName());
        Assert.assertEquals("BHP Billiton", second.getAssetName());
        Assert.assertEquals("Australian Shares", second.getAssetClass());

        RebalanceOrderDetailsDto third = detailsList.get(2);
        Assert.assertEquals("Joe", third.getAccountName());
        Assert.assertEquals("Woolworths", third.getAssetName());
        Assert.assertEquals("Australian Shares", third.getAssetClass());

        RebalanceOrderDetailsDto fourth = detailsList.get(3);
        Assert.assertEquals("Joe", fourth.getAccountName());
        Assert.assertEquals("Advance Balance Multi-Blend Fund", fourth.getAssetName());
        Assert.assertEquals("Diversified", fourth.getAssetClass());

        RebalanceOrderDetailsDto fifth = detailsList.get(4);
        Assert.assertEquals("Joe", fifth.getAccountName());
        Assert.assertEquals("TP Cash", fifth.getAssetName());
        Assert.assertEquals("Cash", fifth.getAssetClass());

    }

}
