package com.bt.nextgen.api.transaction.service;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.transaction.TransactionHolderImpl;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.transaction.TransactionHolder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class TransactionDtoServiceXpathMappingsTest {

    @Mock
    DateTimeTypeConverter dateTimeTypeConverter;
    @Mock
    private ParsingContext parsingContext;
    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void testXpathMappings() throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/ScheduledTransactionsResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);

        Mockito.when(dateTimeTypeConverter.convert("2016-03-15")).thenReturn(new DateTime("2016-03-15"));

        DefaultResponseExtractor<TransactionHolderImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(TransactionHolderImpl.class);

        TransactionHolder transactionHolder = defaultResponseExtractor.extractData(content);

        assertThat(transactionHolder, is(notNullValue()));
        assertThat(transactionHolder.getScheduledTransactions().get(0).getRecentTrxDate(), is(new DateTime("2016-03-15")));
    }

}
