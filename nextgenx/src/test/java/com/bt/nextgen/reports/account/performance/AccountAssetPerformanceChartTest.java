package com.bt.nextgen.reports.account.performance;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountAssetPerformanceChartTest {

    @InjectMocks
    private AccountAssetPerformanceChart chart;


    @Before
    public void setup() throws Exception{
    }
    
    @Test
    public void testDecimalFormat_Without_Decimals() {
        XYPlot plot = Mockito.mock(XYPlot.class);
        XYDataset xyDataset = Mockito.mock(XYDataset.class);
        Mockito.when(xyDataset.getSeriesCount()).thenReturn(1);
        Mockito.when(xyDataset.getItemCount(0)).thenReturn(2);
        Mockito.when(xyDataset.getYValue(Mockito.eq(0), Mockito.eq(0))).thenReturn(80d);
        Mockito.when(xyDataset.getYValue(Mockito.eq(0), Mockito.eq(1))).thenReturn(60d);
        Mockito.when(plot.getDatasetCount()).thenReturn(1);
        Mockito.when(plot.getDataset(0)).thenReturn(xyDataset);

        DecimalFormat decimalFormat = chart.getRangeFormat(plot);
        assertEquals("#0'%'", decimalFormat.toPattern());

    }

    @Test
    public void testDecimalFormat_With_Decimals() {
        XYPlot plot = Mockito.mock(XYPlot.class);
        XYDataset xyDataset = Mockito.mock(XYDataset.class);
        Mockito.when(xyDataset.getSeriesCount()).thenReturn(1);
        Mockito.when(xyDataset.getItemCount(0)).thenReturn(2);
        Mockito.when(xyDataset.getYValue(Mockito.eq(0), Mockito.eq(0))).thenReturn(80d);
        Mockito.when(xyDataset.getYValue(Mockito.eq(0), Mockito.eq(1))).thenReturn(80.5d);
        Mockito.when(plot.getDatasetCount()).thenReturn(1);
        Mockito.when(plot.getDataset(0)).thenReturn(xyDataset);

        DecimalFormat decimalFormat = chart.getRangeFormat(plot);
        assertEquals("#0.00'%'", decimalFormat.toPattern());

    }

}
