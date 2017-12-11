package com.bt.nextgen.reports.account.drawdownstrategy;

import com.bt.nextgen.api.drawdown.v2.model.AssetPriorityDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownStrategyReportDataConverterTest {

    @InjectMocks
    private DrawdownStrategyReportDataConverter converter;

    @Test
    public void test_whenAssetPriorityListRelevant_thenListIsPopulated() {
        DrawdownDetailsDto details = mockDetailsDto();
        DrawdownStrategyReportData data = converter.toReportData(DrawdownStrategy.ASSET_PRIORITY, details);

        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getDisplayName(), data.getDrawdownStrategy());
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getDescription(), data.getDrawdownStrategyDescription());
        Assert.assertEquals(Boolean.TRUE, data.getDisplayPriorityList());

        Assert.assertEquals(3, data.getAssetPriorityList().size());

        AssetPriorityReportData priorityData = data.getAssetPriorityList().get(0);
        Assert.assertEquals("<b>RIO</b> &#183 Rio Tinto", priorityData.getAssetTitle());
        Assert.assertEquals("SUSPENDED", priorityData.getAssetStatus());
        Assert.assertEquals("$1.00", priorityData.getMarketValue());
        Assert.assertEquals("1", priorityData.getPriority());

        priorityData = data.getAssetPriorityList().get(1);
        Assert.assertEquals("<b>BTA123AU</b> &#183 BT Fund", priorityData.getAssetTitle());
        Assert.assertEquals(null, priorityData.getAssetStatus());
        Assert.assertEquals("-", priorityData.getMarketValue());
        Assert.assertEquals("5", priorityData.getPriority());

        priorityData = data.getAssetPriorityList().get(2);
        Assert.assertEquals("<b>BHP</b> &#183 BHP Billiton", priorityData.getAssetTitle());
        Assert.assertEquals(null, priorityData.getAssetStatus());
        Assert.assertEquals("$10.00", priorityData.getMarketValue());
        Assert.assertEquals("123", priorityData.getPriority());
    }

    @Test
    public void test_whenAssetPriorityListNotRelevant_thenListIsNotPopulated() {
        DrawdownStrategyReportData data = converter.toReportData(DrawdownStrategy.HIGH_PRICE, null);

        Assert.assertEquals(DrawdownStrategy.HIGH_PRICE.getDisplayName(), data.getDrawdownStrategy());
        Assert.assertEquals(DrawdownStrategy.HIGH_PRICE.getDescription(), data.getDrawdownStrategyDescription());
        Assert.assertEquals(Boolean.FALSE, data.getDisplayPriorityList());
        Assert.assertEquals(null, data.getAssetPriorityList());
    }

    private DrawdownDetailsDto mockDetailsDto() {

        DrawdownDetailsDto dto = Mockito.mock(DrawdownDetailsDto.class);

        Mockito.when(dto.getDrawdownType()).thenReturn(DrawdownStrategy.ASSET_PRIORITY.getIntlId());

        AssetPriorityDto priorityDto = Mockito.mock(AssetPriorityDto.class);
        Mockito.when(priorityDto.getAssetId()).thenReturn("12345");
        Mockito.when(priorityDto.getAssetCode()).thenReturn("BHP");
        Mockito.when(priorityDto.getAssetName()).thenReturn("BHP Billiton");
        Mockito.when(priorityDto.getAssetType()).thenReturn(AssetType.SHARE.getDisplayName());
        Mockito.when(priorityDto.getStatus()).thenReturn("Open");
        Mockito.when(priorityDto.getMarketValue()).thenReturn(BigDecimal.TEN);
        Mockito.when(priorityDto.getDrawdownPriority()).thenReturn(123);

        AssetPriorityDto priorityDto2 = Mockito.mock(AssetPriorityDto.class);
        Mockito.when(priorityDto2.getAssetId()).thenReturn("23456");
        Mockito.when(priorityDto2.getAssetCode()).thenReturn("BTA123AU");
        Mockito.when(priorityDto2.getAssetName()).thenReturn("BT Fund");
        Mockito.when(priorityDto2.getAssetType()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());
        Mockito.when(priorityDto2.getStatus()).thenReturn(null);
        Mockito.when(priorityDto2.getMarketValue()).thenReturn(null);
        Mockito.when(priorityDto2.getDrawdownPriority()).thenReturn(5);

        AssetPriorityDto priorityDto3 = Mockito.mock(AssetPriorityDto.class);
        Mockito.when(priorityDto3.getAssetId()).thenReturn("34567");
        Mockito.when(priorityDto3.getAssetCode()).thenReturn("RIO");
        Mockito.when(priorityDto3.getAssetName()).thenReturn("Rio Tinto");
        Mockito.when(priorityDto3.getAssetType()).thenReturn(AssetType.SHARE.getDisplayName());
        Mockito.when(priorityDto3.getStatus()).thenReturn("Suspended");
        Mockito.when(priorityDto3.getMarketValue()).thenReturn(BigDecimal.ONE);
        Mockito.when(priorityDto3.getDrawdownPriority()).thenReturn(1);

        List<AssetPriorityDto> priorityList = new ArrayList<>();
        priorityList.add(priorityDto);
        priorityList.add(priorityDto2);
        priorityList.add(priorityDto3);

        Mockito.when(dto.getPriorityDrawdownList()).thenReturn(priorityList);

        return dto;
    }
}
