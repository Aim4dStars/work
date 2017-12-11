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
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionSecurityExchangeTypeConverterTest {
    @InjectMocks
    private CorporateActionSecurityExchangeTypeConverter securityExchangeTypeConverter;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Before
    public void setup() {
    }

    @Test
    public void testConvert_whenCodeIsValid_thenReturnCorporateActionType() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn(CorporateActionSecurityExchangeType.REINVESTMENT.getId());
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        CorporateActionSecurityExchangeType type = securityExchangeTypeConverter.convert("4");

        assertNotNull(type);
        assertEquals(CorporateActionSecurityExchangeType.REINVESTMENT, type);
    }

    @Test
    public void testConvert_whenCodeIsNotValid_thenReturnNull() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn("xx");
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        assertNull(securityExchangeTypeConverter.convert("4"));
    }

    @Test
    public void testConvert_whenCodeDoesNotExist_thenReturnNull() {
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(null);

        CorporateActionSecurityExchangeType type = securityExchangeTypeConverter.convert("4");

        assertNull(type);
    }

    @Test
    public void testConvert_whenNoOfferType_thenReturnNull() {
        assertNull(securityExchangeTypeConverter.convert(null));
    }
}
