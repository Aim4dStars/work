package com.bt.nextgen.service.integration.account;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import org.junit.After;
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

import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class TransitionAccountDetailHolderTest {

    @InjectMocks
    DefaultResponseExtractor<TransitionAccountDetailHolderImpl> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CodeConverter codeConverter;

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void transitionListHolderShouldRepresentTheCorrectStructureFromAvaloq() throws Exception {

        final ClassPathResource responseResource =new ClassPathResource("/webservices/response/transitionAccounts/TransitionAccountDetailsResponse-brokerId.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(codeConverter.convert("1", "CODE_TRANSITION_STATUS")).thenReturn("wait_aprv");
        Mockito.when(codeConverter.convert("2","CODE_TRANSITION_STATUS")).thenReturn("init_xfer");

        defaultResponseExtractor = new DefaultResponseExtractor<>(TransitionAccountDetailHolderImpl.class);
        TransitionAccountDetailHolder response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);

        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerId(),is("100747"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getAccountKey().getId(), is("173133"));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedCashAmount().intValue(), is(50000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getExpectedAssetAmount().intValue(), is(150000));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransitionStatus(), is(TransitionStatus.INITIATE_TRANSFER));
        assertThat(response.getTransitionAccountDetailList().get(0).getTransitionAccountBPDetailList().get(0).getTransferType(), is("Internal Transfer"));
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerName(), is("OE Darryl  Gunther"));
        assertThat(response.getTransitionAccountDetailList().get(0).getBrokerId(), is("100747"));

    }
}