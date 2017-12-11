package com.bt.nextgen.service.avaloq.userprofile;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class AvailableProfilesImplTest {
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @InjectMocks
    DefaultResponseExtractor<AvailableProfilesImpl> profileResponseExtractor;

    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);

        Mockito.when(codeConverter.convert("660274", "JOB_TYPE")).thenReturn("btfg$prplnr");
        Mockito.when(codeConverter.convert("660272", "JOB_TYPE")).thenReturn("btfg$avsr");
        Mockito.when(codeConverter.convert("660277", "JOB_TYPE")).thenReturn("btfg$invstr");

        Mockito.when(codeConverter.convert("660485", "USER_EXPERIENCE")).thenReturn("btfg$advs");
        Mockito.when(codeConverter.convert("660486", "USER_EXPERIENCE")).thenReturn("btfg$bt_invest_self_serv");
        Mockito.when(codeConverter.convert("660487", "USER_EXPERIENCE")).thenReturn("btfg$asim");
    }

    @Test
    public void testUserInformationService() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/LoadJobProfilesParaplanner_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        profileResponseExtractor = new DefaultResponseExtractor<>(AvailableProfilesImpl.class);
        AvailableProfilesImpl response = profileResponseExtractor.extractData(content);

        assertNotNull(response);

        List<JobProfile> profile = response.getJobProfiles();
        assertNotNull(profile);

        assertEquals(JobKey.valueOf("72705"), profile.get(0).getJob());
        assertEquals(JobRole.PARAPLANNER, profile.get(0).getJobRole());
        assertEquals("1531", profile.get(0).getProfileId());
        assertEquals("72705", profile.get(0).getPersonJobId());
        assertEquals(UserExperience.ADVISED, profile.get(0).getUserExperience());
        assertNull(profile.get(0).getCloseDate());

        assertEquals(JobKey.valueOf("83430"), profile.get(1).getJob());
        assertEquals(JobRole.PARAPLANNER, profile.get(1).getJobRole());
        assertEquals("8993", profile.get(1).getProfileId());
        assertEquals("83430", profile.get(1).getPersonJobId());
        assertEquals(UserExperience.ASIM, profile.get(1).getUserExperience());
        assertNull(profile.get(1).getCloseDate());

        assertEquals(JobKey.valueOf("876351"), profile.get(2).getJob());
        assertEquals(JobRole.ADVISER, profile.get(2).getJobRole());
        assertEquals("31126", profile.get(2).getProfileId());
        assertEquals("876351", profile.get(2).getPersonJobId());
        assertEquals(UserExperience.ASIM, profile.get(2).getUserExperience());
        assertEquals("2017-03-07", formatter.print(response.getJobProfiles().get(2).getCloseDate()));
    }

    @Test
    public void testJobProfilesForUser() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/LoadJobProfilesInvestor_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        profileResponseExtractor = new DefaultResponseExtractor<>(AvailableProfilesImpl.class);
        AvailableProfilesImpl response = profileResponseExtractor.extractData(content);

        assertNotNull(response);
        List<JobProfile> profile = response.getJobProfiles();

        assertEquals(JobKey.valueOf("90576"), profile.get(0).getJob());
        assertEquals(JobRole.INVESTOR, profile.get(0).getJobRole());
        assertEquals("7210", profile.get(0).getProfileId());
        assertEquals("90576", profile.get(0).getPersonJobId());
    }
}
