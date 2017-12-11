package com.bt.nextgen.api.env.service;

import com.bt.nextgen.api.env.model.EnvironmentDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.provisio.ProvisioService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentDtoServiceTest {
    @InjectMocks
    private EnvironmentDtoServiceImpl dtoService;

    @Mock
    private BankDateIntegrationService bankDateService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProvisioService provisioService;

    private DateTime mockBankDate;
    private EnvironmentDto result;

    @Before
    public void setup() {
        mockBankDate = DateTime.parse("2015-01-01T13:14:29+11:00");
        when(bankDateService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(mockBankDate);
        when(bankDateService.getTime(Mockito.any(ServiceErrors.class))).thenReturn(mockBankDate);
        when(provisioService.getProvisioToken()).thenReturn("dummy");

        result = dtoService.findOne(serviceErrors);
    }

    @Test
    public void testMockFalseUseCmsUrlTrue() {
        java.util.Properties properties = Properties.all();

        String backupenv = Properties.get("environment");
        properties.put("environment", "PROD");
        properties.put("aem.mock.content", "false");
        properties.put("aem.useCmsUrl", "true");

        result = dtoService.findOne(serviceErrors);

        assertNotNull(result.getEnvironment());
        assertEquals("PROD", result.getEnvironment());
        assertNotNull(result.getCmsHostForAem());
        assertEquals(Properties.get("aem.cms.url"), result.getCmsHostForAem());
        assertEquals("92262940", result.getLivePersonId());
        assertEquals(mockBankDate, result.getBankDate());
        assertEquals(result.getBankTimeOffsetInMillis(), new Integer(39600000));
        properties.put("environment", backupenv);
    }

    @Test
    public void testMockTrueUseCmsUrlFalse() {
        Properties.all().put("aem.mock.content", "true");
        Properties.all().put("aem.useCmsUrl", "false");

        result = dtoService.findOne(serviceErrors);

        assertNotNull(result.getEnvironment());
        assertEquals(Properties.get("environment"), result.getEnvironment());
        assertNull(result.getCmsHostForAem());
        assertEquals(mockBankDate, result.getBankDate());
        assertEquals(result.getBankTimeOffsetInMillis(), new Integer(39600000));
    }

    @Test
    public void testMockTrueUseCmsUrlTrue() {
        Properties.all().put("aem.mock.content", "true");
        Properties.all().put("aem.useCmsUrl", "true");

        result = dtoService.findOne(serviceErrors);

        assertNotNull(result.getEnvironment());
        assertEquals(Properties.get("environment"), result.getEnvironment());
        assertNull(result.getCmsHostForAem());
        assertEquals(mockBankDate, result.getBankDate());
        assertEquals(result.getBankTimeOffsetInMillis(), new Integer(39600000));
    }

    @Test
    public void testMockFalseUseCmsUrlFalse() {
        Properties.all().put("aem.mock.content", "true");
        Properties.all().put("aem.useCmsUrl", "false");

        result = dtoService.findOne(serviceErrors);

        assertNotNull(result.getEnvironment());
        assertEquals(Properties.get("environment"), result.getEnvironment());
        assertNull(result.getCmsHostForAem());
        assertEquals(mockBankDate, result.getBankDate());
        assertEquals(result.getBankTimeOffsetInMillis(), new Integer(39600000));
    }

    @Test
    public void testCmsForServiceOps() {
        Properties.all().put("aem.mock.content", "false");
        Properties.all().put("aem.service.ops.url", "https://gsso-sit.intranet.westpac.com.au/ngcms_dev2");
        when(userProfileService.isEmulating()).thenReturn(true);
        result = dtoService.findOne(serviceErrors);

        assertEquals(result.getCmsHostForAem(), Properties.get("aem.service.ops.url"));
    }

    @Test
    public void testCmsForNonServiceOps() {
        Properties.all().put("aem.mock.content", "false");
        when(userProfileService.isEmulating()).thenReturn(false);
        result = dtoService.findOne(serviceErrors);

        assertEquals(result.getCmsHostForAem(), Properties.get("aem.cms.url"));
    }

    @Test
    public void verifyQasUrlToBeSet() {
        assertNotNull(result.getEnvironment());
        assertThat(result.getAddressValidationQasApi(), is("https://eforms.sit.panoramaadviser.com.au/util/public/api/address/query"));
    }

    @Test
    public void verifyAppDynamicsKey() {
        assertNotNull(result.getEnvironment());
        assertThat(result.getAppDynamicsKey(), is("dummy"));
    }

    @Test
    public void verifyProvisioToken() {
        assertNotNull(result.getEnvironment());
        assertThat(result.getProvisioToken(), is("dummy"));
    }

    @Test
    public void testProvisioHostForServiceOps() {
        Properties.all().put("provisio.service.ops.url", "https://gsso-sit.intranet.westpac.com.au/ngservices_dev2");
        when(userProfileService.isEmulating()).thenReturn(true);
        result = dtoService.findOne(serviceErrors);

        assertEquals(result.getProvisioHost(), Properties.get("provisio.service.ops.url"));
    }
}
