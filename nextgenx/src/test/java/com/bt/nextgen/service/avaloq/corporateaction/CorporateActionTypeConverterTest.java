package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionTypeConverterTest {
    @InjectMocks
    private CorporateActionTypeConverter corporateActionTypeConverter;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Before
    public void setup() {
    }

    @Test
    public void testConvert_whenCodeIsValid_thenReturnCorporateActionType() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn("exe_right");
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        CorporateActionType type = corporateActionTypeConverter.convert("4");

        assertNotNull(type);
        assertEquals(CorporateActionType.EXERCISE_RIGHTS, type);
    }

    @Test
    public void testConvert_whenCodeIsNotValid_thenReturnNull() {
        Code code = mock(Code.class);

        when(code.getIntlId()).thenReturn("xxx");
        when(staticCodeService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        assertNull(corporateActionTypeConverter.convert("4"));
    }
}
