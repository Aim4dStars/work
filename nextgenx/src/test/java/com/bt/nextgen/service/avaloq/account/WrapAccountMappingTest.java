package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.avaloq.client.ClientKeyConverter;
import com.bt.nextgen.service.avaloq.client.ProductKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class WrapAccountMappingTest
{
    @Mock
    private ApplicationContext applicationContext;


    @Test
	public void testWrapAccountMapping() throws Exception
	{
        new ParsingContext().setApplicationContext(applicationContext);

        CodeConverter mockCodeConverter = createMockCodeConverter();
        when(mockCodeConverter.convert("2750", "ACCOUNT_STATUS")).thenReturn("PEND_OPN");
        when(mockCodeConverter.convert("20611","ACCOUNT_STRUCTURE_TYPE")).thenReturn("btfg$indvl");
        when(mockCodeConverter.convert("20613","ACCOUNT_STRUCTURE_TYPE")).thenReturn("btfg$company");
        when(mockCodeConverter.convert("1125","BP_PERMISSION")).thenReturn("BTFG$PAY_ALL");

        createMockAccountKeyConverter();
        createMockProductKeyConverter();
        createMockClientKeyConverter();
        createMockBrokerKeyConverter();

        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/BP_LIST_UT.xml");
		String response = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        AccountBasicHolder wrapAccounts = new DefaultResponseExtractor<AccountBasicHolder>(AccountBasicHolder.class)
				.extractData(response);

        assertThat(wrapAccounts.getAccounts().size(), equalTo(2));

        WrapAccount wrapAccount0 = wrapAccounts.getAccounts().get(0);
        assertThat(wrapAccount0.getAccountKey().getId(), equalTo("74611"));
        assertThat(wrapAccount0.getAccountNumber(), equalTo("120011366"));
        assertThat(wrapAccount0.getAccountStructureType(), is(AccountStructureType.Individual));
        assertThat(wrapAccount0.getProductKey(), is(ProductKey.valueOf("68445")));
        assertThat(wrapAccount0.getAccountStatus(), is(AccountStatus.PEND_OPN));
        assertThat(wrapAccount0.isOpen(),is(true));
        assertThat(wrapAccount0.getAdviserPositionId(), is(BrokerKey.valueOf("69785")));
        assertThat(wrapAccount0.getAdviserPersonId().getId(), is("69791"));
        assertThat(wrapAccount0.getAdviserPermissions().size(), is(0));

//        assertThat(wrapAccount0.getOwnerClientKeys(), hasSize(1));
//        assertThat(wrapAccount0.getOwnerClientKeys().get(0), is(ClientIdentifier.valueOf("74609")));

        WrapAccount wrapAccount1 = wrapAccounts.getAccounts().get(1);
        assertThat(wrapAccount1.getAccountKey().getId(), equalTo("11263"));
        assertThat(wrapAccount1.getAccountNumber(), equalTo("120000005"));
        assertThat(wrapAccount1.getAccountStructureType(), is(AccountStructureType.Company));
        assertThat(wrapAccount1.getProductKey(), is(ProductKey.valueOf("68445")));
        assertThat(wrapAccount1.getAccountStatus(), is(AccountStatus.ACTIVE));
        assertThat(wrapAccount1.isOpen(),is(true));
        assertThat(wrapAccount1.getAdviserPositionId(), is(BrokerKey.valueOf("65716")));
//        assertThat(wrapAccount1.getOwnerClientKeys().get(0), is(ClientIdentifier.valueOf("11247")));
    }

    private void createMockBrokerKeyConverter() {
        BrokerKeyConverter brokerKeyConverter = mock(BrokerKeyConverter.class);
        when(brokerKeyConverter.convert(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return BrokerKey.valueOf((String) invocationOnMock.getArguments()[0]);
            }
        });
        when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);
    }

    private void createMockProductKeyConverter() {
        ProductKeyConverter productKeyConverter = mock(ProductKeyConverter.class);
        when(productKeyConverter.convert(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ProductKey.valueOf((String) invocationOnMock.getArguments()[0]);
            }
        });
        when(applicationContext.getBean(ProductKeyConverter.class)).thenReturn(productKeyConverter);
    }

    private void createMockClientKeyConverter() {
        ClientKeyConverter clientKeyConverter = mock(ClientKeyConverter.class);
        when(clientKeyConverter.convert(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ClientKey.valueOf((String) invocationOnMock.getArguments()[0]);
            }
        });
        when(applicationContext.getBean(ClientKeyConverter.class)).thenReturn(clientKeyConverter);
    }

    private void createMockAccountKeyConverter() {
        AccountKeyConverter accountKeyConverter = mock(AccountKeyConverter.class);
        when(accountKeyConverter.convert(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return AccountKey.valueOf((String) invocationOnMock.getArguments()[0]);
            }
        });
        when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);
    }

    private CodeConverter createMockCodeConverter() {
        new ParsingContext().setApplicationContext(applicationContext);
        CodeConverter codeConverter = mock(CodeConverter.class);
        when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        return codeConverter;
    }
}
