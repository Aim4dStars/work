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
public class GenericClientTest
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
    AddressKeyConverter addressKeyConverter;

    @Mock
    DateTimeTypeConverter dateTimeTypeConverter;

    @Mock
    TitleConverter titleConverter;


    @Test
    public void testAdviserResponse() throws Exception
    {

        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/genericclient/PersonDetailsAdviserResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
        PowerMockito.mockStatic(ParsingContext.class);

        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(codeConverter.convert("1001","PERSON_TITLE")).thenReturn("btfg$mr");
        Mockito.when(codeConverter.convert("1","GENDER")).thenReturn("male");
        Mockito.when(codeConverter.convert("99","EXEMPTION_REASON")).thenReturn("none");
        Mockito.when(codeConverter.convert("2061","COUNTRY")).thenReturn("2061");

        Mockito.when(applicationContext.getBean(
                ClientKeyConverter.class)).thenReturn(clientKeyConverter);
        Mockito.when(clientKeyConverter.convert("32815")).thenReturn(ClientKey.valueOf("32815"));

        Mockito.when(applicationContext.getBean(
                AddressKeyConverter.class)).thenReturn(addressKeyConverter);
        Mockito.when(addressKeyConverter.convert("32816")).thenReturn(AddressKey.valueOf("32816"));
        Mockito.when(addressKeyConverter.convert("32817")).thenReturn(AddressKey.valueOf("32817"));
        Mockito.when(addressKeyConverter.convert("32818")).thenReturn(AddressKey.valueOf("32818"));
        Mockito.when(addressKeyConverter.convert("32819")).thenReturn(AddressKey.valueOf("32819"));

        Mockito.when(applicationContext.getBean(
                DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(dateTimeTypeConverter.convert("1911-11-01")).thenReturn(new DateTime("1911-11-01"));

        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);
        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));
        // Check GenericClientAdviserImpl instantiated
        assertThat(abstractHolder, instanceOf(GenericClientIntermediaryImpl.class));
        GenericClientIntermediaryImpl holder =(GenericClientIntermediaryImpl)abstractHolder;

        assertThat(holder.getClientKey(), is(ClientKey.valueOf("32815")));
        //assertThat(holder.getPanoramaNumber(), is("201603468"));
        assertThat(holder.getFullName(), is("Mr Bradley Jackson"));
        assertThat(holder.getFirstName(), is("Bradley"));
        assertThat(holder.getLastName(), is("Jackson"));
        //assertThat(holder.getAge(), is(103));
        //String content;assertThat(holder.getDateOfBirth(), is(new DateTime("1911-11-01")));
        //assertThat(holder.getGender(), is(Gender.MALE));
        assertThat(holder.isAdviser(), is(true));
        assertThat(holder.isAdminAssist(), is(false));
        assertThat(holder.isParaPlanner(), is(false));
        //assertThat(holder.isTfnProvided(), is(false));
        //assertThat(holder.getExemptionReason(), is(ExemptionReason.NO_EXEMPTION));
        //assertThat(holder.getSafiDeviceId(), is("c9d93796-a77e-4805-b8f9-b62add201ab1"));
        //assertThat(holder.getResiCountryCodeForTax(), is("2061"));
        //assertThat(holder.getCisId(), is("07331610075"));
        //assertThat(holder.getModificationSeq(), is("2"));
        assertThat(holder.getAddresses(), is(notNullValue()));
        assertThat(holder.getAddresses().size(), is(not(0)));
        assertThat(holder.getAddresses().get(0), is(notNullValue()));
        assertThat(holder.getAddresses().get(0).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(0).getCategoryId(), is(1));
        assertThat(holder.getAddresses().get(0).getAddressKey(), is(AddressKey.valueOf("32816")));
        assertThat(holder.getAddresses().get(0).isMailingAddress(), is(true));
        assertThat(holder.getAddresses().get(0).isDomicile(), is(true));
        assertThat(holder.getAddresses().get(0).getCountryCode(), is("2061"));
        assertThat(holder.getAddresses().get(0).getFloor(), is("17"));
        assertThat(holder.getAddresses().get(0).getStreetNumber(), is("33"));
        assertThat(holder.getAddresses().get(0).getStreetName(), is("Pitt"));
        assertThat(holder.getAddresses().get(0).getStreetType(), is("33"));
        assertThat(holder.getAddresses().get(0).getBuilding(), is("First Financial"));
        assertThat(holder.getAddresses().get(0).getState(), is("5001"));
        assertThat(holder.getAddresses().get(0).getSuburb(), is("Sydney"));
        assertThat(holder.getAddresses().get(0).getStateOther(), is("New South Wales"));
        assertThat(holder.getAddresses().get(0).getPostCode(), is("2000"));

        assertThat(holder.getAddresses().get(0).getAddressType(), is(AddressMedium.getAddressMedium("6")));

        assertThat(holder.getAddresses().get(1).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(1).getAddressKey(), is(AddressKey.valueOf("32817")));
        assertThat(holder.getAddresses().get(1).getCategoryId(), is(3));
        assertThat(holder.getAddresses().get(1).isMailingAddress(), is(false));
        assertThat(holder.getAddresses().get(1).isDomicile(), is(false));

        assertThat(holder.getAddresses().get(2).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(2).getAddressKey(), is(AddressKey.valueOf("32818")));
        assertThat(holder.getAddresses().get(2).getCategoryId(), is(3));
        assertThat(holder.getAddresses().get(2).isMailingAddress(), is(false));
        assertThat(holder.getAddresses().get(2).isDomicile(), is(false));

        assertThat(holder.getAddresses().get(3).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(3).getAddressKey(), is(AddressKey.valueOf("32819")));
        assertThat(holder.getAddresses().get(3).getCategoryId(), is(3));
        assertThat(holder.getAddresses().get(3).isMailingAddress(), is(false));
        assertThat(holder.getAddresses().get(3).isDomicile(), is(false));

       }

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
        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);

        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));

        // Check GenericClientInvestorImpl instantiated
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
    }





}
