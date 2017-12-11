package com.bt.nextgen.reports.modelportfolio.orderstatus;

import com.bt.nextgen.api.modelportfolio.v2.model.orderstatus.ModelOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.service.orderstatus.ModelOrderSummaryDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ModelOrderSummaryCsvReportTest {

    @InjectMocks
    private ModelOrderSummaryCsvReport csvReport;

    @Mock
    private ModelOrderSummaryDtoService orderSummaryService;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private Map<String, Object> mockParams;
    
    @Mock
    private CmsService cmsService;

    private List<ModelOrderReportData> data;

    private static final DateTimeFormatter filenameDateFormat = DateTimeFormat.forPattern("ddMMyyyy");
    private List<ModelOrderDetailsDto> detailList;

    @Before
    public void setup() {

        ModelOrderDetailsDto details = Mockito.mock(ModelOrderDetailsDto.class);
        Mockito.when(details.getAccountName()).thenReturn("accountName");
        Mockito.when(details.getAccountNumber()).thenReturn("1234");
        Mockito.when(details.getAdviserName()).thenReturn("adviserName");

        detailList = new ArrayList<>();
        detailList.add(details);

        ModelOrderReportData orderData = new ModelOrderReportData(DateTime.now(), detailList);
        data = Arrays.asList(orderData);
    }

    @Test
    public void test_getData() {

        Mockito.when(orderSummaryService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(detailList);

        Mockito.when(mockParams.get("effective-date")).thenReturn("2017-08-01");
        Map<String, Object> dataCollections = new HashMap<String, Object>();

        Collection<?> data = csvReport.getData(mockParams, dataCollections);

        Assert.assertNotNull(data);
        Assert.assertEquals(1, data.size());
    }

    @Test
    public void test_getReportFileName_withNullData_defaultNameReturned() {
        List<ModelOrderReportData> emptyList = Collections.emptyList();
        String fileName = csvReport.getReportFileName(emptyList);
        Assert.assertEquals("OrderStatus", fileName);
    }

    @Test
    public void test_getReportFileName() {
        String fileName = csvReport.getReportFileName(data);        
        Assert.assertEquals("OrderStatus_" + filenameDateFormat.print(DateTime.now()), fileName);
    }

    @Test
    public void test_getPageName() {
        Collection<String> names = csvReport.getReportPageNames(data);
        Assert.assertNotNull(names);
    }

    @Test
    public void test_getDisclaimer() {
        Mockito.when(cmsService.getDynamicContent(Mockito.anyString(), Mockito.any(String[].class))).thenReturn("header");

        Map<String, Object> params = new HashMap<>();
        params.put("effective-date", "2017-08-01");
        String headerText = csvReport.getDisclaimer(params);
        Assert.assertNotNull(headerText);
    }

    @Test
    public void test_getRebalanceDate() {
        Map<String, Object> params = new HashMap<>();
        params.put("effective-date", "2017-08-01");
        Assert.assertEquals("01 Aug 2017", csvReport.getRebalanceDate(params));
    }

    @Test
    public void test_modelOrderReportData() {
        ModelOrderReportData rpt = data.get(0);
        Assert.assertTrue(rpt.getOrderDetails().size() == 1);
        Assert.assertTrue(rpt.getReportDate() != null);
    }

    @Test
    public void test_getHeaderText() {

        ContentDto contentDto = Mockito.mock(ContentDto.class);
        Mockito.when(contentDto.getContent()).thenReturn("content");
        Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(contentDto);

        Map<String, Object> params = new HashMap<>();
        String content = csvReport.getHeaderText(params);
        Assert.assertEquals(contentDto.getContent(), content);
    }

}
