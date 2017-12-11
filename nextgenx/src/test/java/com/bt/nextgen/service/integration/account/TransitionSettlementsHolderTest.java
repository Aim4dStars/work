package com.bt.nextgen.service.integration.account;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
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

import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class TransitionSettlementsHolderTest {

    @InjectMocks
    DefaultResponseExtractor<TransitionSettlementsHolderImpl> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Test
    public void transitionSettlementsShouldMappedToCorrectAvaloqStructure() throws Exception {

        final ClassPathResource responseResource =new ClassPathResource("/webservices/response/transitionAccounts/TransitionSettlementsResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(codeConverter.convert("1", "CODE_TRANSITION_STATUS")).thenReturn("wait_aprv");
        Mockito.when(codeConverter.convert("2","CODE_TRANSITION_STATUS")).thenReturn("init_xfer");

        defaultResponseExtractor = new DefaultResponseExtractor<>(TransitionSettlementsHolderImpl.class);
        TransitionSettlementsHolder response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertThat(response.getTransitionSettlements().size(), is(2));
        assertThat(response.getAccountKey ().getId(),is("169655"));
        assertThat(response.getTransitionSettlements().get(0).getAssetKey().getId(),is("168546"));
        assertThat(response.getTransitionSettlements().get(0).getQuantity(),is("200"));
        assertThat(response.getTransitionSettlements().get(0).getAmount().intValue(),is(188));
        assertThat(response.getTransitionSettlements().get(0).getTransitionDate().toLocalDate().toString(),is("2015-08-24"));

    }
}