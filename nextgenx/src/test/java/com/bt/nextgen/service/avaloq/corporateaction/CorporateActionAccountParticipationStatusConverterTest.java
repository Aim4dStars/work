package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountParticipationStatusConverterTest {
    @InjectMocks
    private CorporateActionAccountParticipationStatusConverter actionAccountParticipationStatusConverter;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Before
    public void setup() {
    }

    @Test
    public void testConvert_whenCodeIsValid_thenReturnCorporateActionType() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn("clt_conf");
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        CorporateActionAccountParticipationStatus participationStatus = actionAccountParticipationStatusConverter.convert("4");

        assertNotNull(participationStatus);
        assertEquals(CorporateActionAccountParticipationStatus.SUBMITTED, participationStatus);
    }

    @Test
    public void testConvert_whenCodeIsNotValid_thenReturnNull() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn("xxx");
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        assertNull(actionAccountParticipationStatusConverter.convert("4"));
    }
}
