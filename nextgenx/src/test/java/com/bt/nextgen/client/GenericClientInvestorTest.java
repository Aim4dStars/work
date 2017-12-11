package com.bt.nextgen.client;


import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.*;
import com.bt.nextgen.service.avaloq.domain.TitleConverter;
import com.bt.nextgen.service.integration.domain.*;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class GenericClientInvestorTest
{
    @InjectMocks
    DefaultResponseExtractor defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CodeConverter codeConverter;

    @Mock
    ClientKeyConverter clientKeyConverter;

    @Mock
    TitleConverter titleConverter;

    @Mock
    DateTimeTypeConverter dateTimeTypeConverter;

     @Test
    public void testInvestorResponse() throws Exception
    {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/genericclient/PersonDetailsInvestorResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

       PowerMockito.mockStatic(ParsingContext.class);
       Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(
                TitleConverter.class)).thenReturn(titleConverter);
        Mockito.when(titleConverter.convert("1000")).thenReturn("Mr");
        Mockito.when(codeConverter.convert("1","GENDER")).thenReturn("male");
        Mockito.when(codeConverter.convert("123","LEGALFORM")).thenReturn("btfg$legal");
        Mockito.when(codeConverter.convert("99","EXEMPTION_REASON")).thenReturn("none");
        Mockito.when(codeConverter.convert("503004","TRUST_TYPE")).thenReturn("other");

        Mockito.when(applicationContext.getBean(
                ClientKeyConverter.class)).thenReturn(clientKeyConverter);
        Mockito.when(clientKeyConverter.convert("157365")).thenReturn(ClientKey.valueOf("157365"));

        Mockito.when(applicationContext.getBean(
                DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);

        Mockito.when(dateTimeTypeConverter.convert("1934-09-29")).thenReturn(new DateTime("1934-09-29"));
        Mockito.when(dateTimeTypeConverter.convert("2015-08-12")).thenReturn(new DateTime("2015-08-12"));

        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);

        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));
        assertThat(abstractHolder, instanceOf(GenericClientInvestorImpl.class));

        assertThat("Not an Investor Response", abstractHolder instanceof GenericClientInvestorImpl);
        GenericClientInvestorImpl holder =(GenericClientInvestorImpl)abstractHolder;

        assertThat(holder, is(notNullValue()));


        assertThat(holder.getClientKey(), is(ClientKey.valueOf("157365")));
        assertThat(holder.getGcmId(), is("201667459"));
        assertThat(holder.getFullName(), is("person-121_1684person-121_1684person-121_1684"));
        assertThat(holder.getFirstName(), is("person-121_1684"));
        assertThat(holder.getMiddleName(), is("person-121_1684"));
        assertThat(holder.getLastName(), is("person-121_1684"));
        assertThat(holder.isRegistrationOnline(), is(true));
        assertThat(holder.isTfnProvided(), is(true));
        assertThat(holder.getTitle(), is("Mr"));
        assertThat(holder.getAge(), is(81));
        assertThat(holder.getDateOfBirth(), is(new DateTime("1934-09-29")));
        assertThat(holder.getGender(), is(Gender.MALE));
        assertThat(holder.getLegalForm(), is(InvestorType.COMPANY));
        assertThat(holder.getExemptionReason(), is(ExemptionReason.NO_EXEMPTION));
        assertThat(holder.getSafiDeviceId(), is("bd8ead77-ef05-40b4-832e-ca4ddb0c8c56"));
        assertThat(holder.getResiCountryCodeForTax(), is("2061"));
        assertThat(holder.getCisId(), is("44889120086"));
        assertThat(holder.getModificationSeq(), is("3"));

        assertThat(holder.getTrustType(), is(TrustType.OTHER));
        assertThat(holder.getBusinessClassificationDesc(), is("Family Trust"));
        assertThat(holder.getTrustMemberClass(), is("Any person related by blood or marriage to the said Specified Beneficiaries.Such funds, authorities or institutions as mentioned in Section 78(1)(a) of the Income Tax Assessment Act 1936, as amended, which (with the consent of the Guardian, if any) the Trustees may at any time and from time to time nominate to be a General Beneficiary Any person, company or Trustee of any Trust who or which after the date hereof and prior to the Vesting Date has . made or makes a donation to Additional Benef."));
        assertThat(holder.getAddresses(), is(notNullValue()));
        assertThat(holder.getAddresses().size(), is(not(0)));
        assertThat(holder.getOpenDate(), is(new DateTime("2015-08-12")));
       }




}
