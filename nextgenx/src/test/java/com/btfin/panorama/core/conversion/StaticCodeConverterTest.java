package com.btfin.panorama.core.conversion;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Created by F058391 on 27/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticCodeConverterTest {

    @Mock
    StaticIntegrationService staticIntegrationService;

    @InjectMocks
    StaticCodeConverter staticCodeConverter;

    @Test
    public void convertShouldReturnIntlIdIfStaticCodeExist(){
        String intlId = "intlId";
        Code code = new CodeImpl("codeId", "name", "userId", intlId);
        when(staticIntegrationService.loadCode(eq(CodeCategory.MEDIUM), eq("staticCode"), any(ServiceErrors.class))).thenReturn(code);
        String convertedString = staticCodeConverter.convert("staticCode", "MEDIUM");// using any code category for testing purpose
        assertThat(convertedString, is(intlId));
    }

    @Test
    public void convertShouldReturnSameCodeIdIfStaticCodeDoesNotExist(){
        when(staticIntegrationService.loadCode(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(null);
        String convertedString = staticCodeConverter.convert("staticCode", "btfg$medium");// using any code category for testing purpose
        assertThat(convertedString, is("staticCode"));
    }

}