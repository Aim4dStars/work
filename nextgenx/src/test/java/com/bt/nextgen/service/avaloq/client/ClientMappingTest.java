package com.bt.nextgen.service.avaloq.client;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.domain.ClientHolder;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.StateNameConverter;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorType;
import org.junit.Assert;
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

import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientMappingTest {

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private CountryNameConverter mockCountryNameConverter;
    @Mock
    private StateNameConverter mockStateNameConverter;
    @Mock
    private CodeConverter mockConverter;
    @Mock
    private ClientKeyConverter mockClientKeyConverter;

    @Ignore
    @Test
    public void testIndividualMapping() throws Exception {

        new ParsingContext().setApplicationContext(applicationContext);

        when(mockCountryNameConverter.convert("2061")).thenReturn("Australia");
        when(mockStateNameConverter.convert("5004")).thenReturn("New South Wales");
        when(mockConverter.convert("2061","COUNTRY")).thenReturn("AU");
        when(mockConverter.convert("5004","STATES")).thenReturn("NSW");
        when(mockConverter.convert("201","LEGALFORM")).thenReturn("COMPANY");
        when(mockConverter.convert("611803","IDENTIFICATION_STATUS")).thenReturn("pend");

        when(mockClientKeyConverter.convert(anyString())).thenAnswer(new Answer<ClientKey>() {
            @Override
            public ClientKey answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ClientKey.valueOf((String) invocationOnMock.getArguments()[0]);
            }
        });

        when(applicationContext.getBean(CountryNameConverter.class)).thenReturn(mockCountryNameConverter);
        when(applicationContext.getBean(StateNameConverter.class)).thenReturn(mockStateNameConverter);
        when(applicationContext.getBean(CodeConverter.class)).thenReturn(mockConverter);
        when(applicationContext.getBean(ClientKeyConverter.class)).thenReturn(mockClientKeyConverter);

        final ClassPathResource responseResource =
                new ClassPathResource("/webservices/response/PERSON_LIST_FOR_INDIVIDUAL_UT.xml");
        String response = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        ClientHolder clientHolder  = new DefaultResponseExtractor<>(ClientHolder.class).extractData(response);

        assertNotNull(clientHolder);
        IndividualImpl individualClient = (IndividualImpl) clientHolder.getClients().get(0);
        assertNotNull(individualClient);
        Assert.assertThat(individualClient.getClientKey().getId(), is("60512"));
        assertThat(individualClient.getGcmId(), is("201617515"));

        assertThat(individualClient.getFirstName(), is("Adrian"));
        assertThat(individualClient.getLastName(), is("Smith"));
        assertThat(individualClient.getFullName(), is("Adrian Demo Smith"));
        assertThat(individualClient.isRegistrationOnline(), is(false));
        Assert.assertThat(individualClient.getIdentityVerificationStatus(), is(IdentityVerificationStatus.Pending));
        Assert.assertNull(individualClient.getLegalForm());

        Address individualAddress = individualClient.getAddresses().get(0);
        assertThat(individualAddress.getSuburb(), is("Sydney"));
        assertThat(individualAddress.getStateOther(), is("New South Wales"));
        assertThat(individualAddress.getPostCode(), is("2000"));

        assertThat(individualAddress.getStateCode(), is("NSW"));
        assertThat(individualAddress.getState(), is("New South Wales"));
        assertThat(individualAddress.getCountryCode(), is("AU"));
        assertThat(individualAddress.getCountry(), is("Australia"));

        CompanyImpl company = (CompanyImpl) clientHolder.getClients().get(1);
        Assert.assertThat(company.getClientKey().getId(), is("80494"));
        Assert.assertThat(company.getGcmId(), is("201634002"));
        Assert.assertThat(company.getFullName(), is("Demo Wilson Parking Pty Ltd"));
        Assert.assertThat(company.isRegistrationOnline(), is(false));
        Assert.assertThat(company.getAsicName(), is("Demo Wilson Parking Pty Ltd"));
        Assert.assertThat(company.getLegalForm(), is(InvestorType.COMPANY));
        assertThat(company.getIdentityVerificationStatus(), is(IdentityVerificationStatus.Pending));

        Address companyAddress = company.getAddresses().get(0);
        Assert.assertThat(companyAddress.getCountryCode(),is("AU"));
        Assert.assertThat(companyAddress.getSuburb(),is("Sydney"));
        Assert.assertThat(companyAddress.getStateCode(),is("NSW"));
        Assert.assertThat(companyAddress.getStateOther(),is("New South Wales"));
        Assert.assertThat(companyAddress.getPostCode(),is("2000"));
    }

}
