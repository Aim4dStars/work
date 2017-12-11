package com.bt.nextgen.reports.asset;

import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.reports.managedfunds.AvailableManagedFundCSVReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING;

/**
 * Created by l078480 on 3/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvailableManagedFundCSVReportTest {

    @InjectMocks
    private AvailableManagedFundCSVReport report;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private UserProfileService userProfileService;

    @Test
    public void testGetDisclaimer() {
        ContentDto contentDto = new ContentDto("xxx", "<p>Hello World!</p>");
        BrokerImpl broker = new BrokerImpl(null, null);
        broker.setPositionName("Broker");
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        DateTime effectiveDate = new DateTime(2016, 1, 1, 0, 0);
        Map<String, String> params = new HashMap<>();
        params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate.toString());
        Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(contentDto);

        Assert.assertEquals(report.disclaimerForCsv(params), "Disclaimer: Hello World!");
    }
}
