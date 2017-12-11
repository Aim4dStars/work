package com.bt.nextgen.reports.account.cgt;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtMpSecurityDto;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.api.cgt.model.CgtSecurityDto;
import com.bt.nextgen.api.cgt.service.RealisedCgtDtoService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RealisedCapitalGainsTaxCsvReportTest {
    @InjectMocks
    private RealisedCapitalGainsTaxCsvReport realisedCapitalGainsTaxCsvReport;

    @Mock
    private RealisedCgtDtoService cgtService;

    @Test
    public void testGetCgtData_WithValidResponse() {
        List<CgtGroupDto> cgtGroupDtoList = new ArrayList<>();
        CgtDto cgtDto = new CgtDto(new CgtKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", new DateTime("2014-09-09"),
                new DateTime("2014-12-09"), "ASSET_TYPE"), cgtGroupDtoList);
        List<CgtSecurity> cgtSecurities = new ArrayList<>();
        String securityCode = "ABC";
        String securityName = "ABC Managed Fund";
        String securityType = "MANAGED_FUND";
        String parentInvId = "123";
        String parentInvCode = "DEF";
        String parentInvName = "DEF Managed Portfolio";
        String parentInvType = "MANAGED_PORTFOLIO";
        CgtSecurity cgtSecurity = new CgtSecurityDto(securityCode, securityName, securityType);
        CgtSecurity cgtMpSecurity = new CgtMpSecurityDto(securityCode, securityName, securityType, parentInvId, parentInvCode,
                parentInvName, parentInvType);
        cgtSecurities.add(cgtSecurity);
        cgtSecurities.add(cgtMpSecurity);
        CgtGroupDto cgtGroupDto = new CgtGroupDto(null, null, null, null, null, null, null, null, null, null, null,
                cgtSecurities,
                null);
        cgtGroupDtoList.add(cgtGroupDto);
        Mockito.when(cgtService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrorsImpl.class))).thenReturn(cgtDto);

        Map<String, String> params = new HashMap<>();
        params.put("account-id", "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        params.put("startDate", "2014-09-09");
        params.put("endDate", "2014-12-09");

        List<CgtSecurity> cgtSecurityList = realisedCapitalGainsTaxCsvReport.getCgtData(params);
        Assert.assertNotNull(cgtSecurityList);
        Assert.assertEquals(cgtSecurityList.size(), 2);
        Assert.assertEquals(cgtSecurityList.get(0).getSecurityCode(), cgtSecurities.get(0).getSecurityCode());
        Assert.assertEquals(cgtSecurityList.get(1).getSecurityCode(), cgtSecurities.get(1).getSecurityCode());
        Assert.assertEquals(cgtSecurityList.get(1).getParentInvCode(), cgtSecurities.get(1).getParentInvCode());
        Assert.assertNull(cgtSecurityList.get(0).getParentInvCode());

    }
}
