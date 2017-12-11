package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class TransactionErrorDetailsImplTest {

    @InjectMocks
    DefaultResponseExtractor<TransactionErrorDetailsImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    private static final String requestErrorMessage = "XML validation error of WebUI request: 1/822: Element not completed: 'dest_benef_text' See: line: 1, column: 822";
    private static final String environmentErrorMessage = "Level:Error ,Code:999 ,Mediation: ,Desc:ReasonCode(999)Query timed out";

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
    }

    @Test
    public void testErrorMessageIsFound_whenEnvironmentErrorOccurs() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/TransactionEnvironmentError.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(TransactionErrorDetailsImpl.class);
        TransactionErrorDetailsImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(environmentErrorMessage, response.getErrorMessage());
        assertEquals(true, response.isErrorResponse());
    }

    @Test
    public void testErrorMessageIsFound_whenRequestErrorOccurs() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/TransactionRequestError.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(TransactionErrorDetailsImpl.class);
        TransactionErrorDetailsImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(requestErrorMessage, response.getErrorMessage());
        assertEquals(true, response.isErrorResponse());
    }
}
