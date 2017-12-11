package com.bt.nextgen.service.avaloq.domain;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.client.error.ServiceErrorImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TitleConverterTest {

    @InjectMocks
    private TitleConverter titleConverter;

    @Mock
    private StaticIntegrationService staticService;

    @Test
    public void testConvert_whenMatchingCodeId_TitleIsReturned() {
        Code code = Mockito.mock(Code.class);
        when(code.getName()).thenReturn("Dr");
        when(staticService.loadCode(eq(CodeCategory.PERSON_TITLE), eq("1"), any(ServiceErrors.class))).thenReturn(code);
        assertEquals("Dr", titleConverter.convert("1"));
    }

    @Test
    public void testConvert_whenError_nullIsReturnedWithNoException() {
        when(staticService.loadCode(eq(CodeCategory.PERSON_TITLE), eq("2"), any(ServiceErrors.class))).thenAnswer(new Answer() {

                    @Override
                    public Code answer(InvocationOnMock invocation) throws Throwable {
                        ServiceErrors errors = (ServiceErrors) invocation.getArguments()[2];
                        errors.addError(new ServiceErrorImpl("error"));
                        return null;
                    }
        });
        assertNull("Dr", titleConverter.convert("2"));
    }

}
