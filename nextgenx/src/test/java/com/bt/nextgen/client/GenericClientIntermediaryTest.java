package com.bt.nextgen.client;


import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AbstractGenericClientImpl;
import com.bt.nextgen.service.avaloq.client.AddressKeyConverter;
import com.bt.nextgen.service.avaloq.client.ClientKeyConverter;
import com.bt.nextgen.service.avaloq.client.GenericClientIntermediaryImpl;
import com.bt.nextgen.service.avaloq.domain.TitleConverter;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.PersonTitle;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import net.thucydides.core.annotations.Title;
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class GenericClientIntermediaryTest
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
    TitleConverter titleConverter;

    @Mock
    ClientKeyConverter clientKeyConverter;

    @Mock
    AddressKeyConverter addressKeyConverter;

    @Mock
    DateTimeTypeConverter dateTimeTypeConverter;

    @Test
    public void testAdviserResponse() throws Exception
    {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/genericclient/PersonDetailsAdviserResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

       PowerMockito.mockStatic(ParsingContext.class);
       Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(
                TitleConverter.class)).thenReturn(titleConverter);
        Mockito.when(titleConverter.convert("1001")).thenReturn("Mr");
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

        Mockito.when(dateTimeTypeConverter.convert("2014-04-23")).thenReturn(new DateTime("2014-04-23"));
        Mockito.when(codeConverter.convert("1000","PERSON_TITLE")).thenReturn("btfg$mr");

        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);

        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));


        assertThat(abstractHolder, instanceOf(GenericClientIntermediaryImpl.class));
        GenericClientIntermediaryImpl holder =(GenericClientIntermediaryImpl)abstractHolder;
        assertThat(holder.getClientKey(), is(ClientKey.valueOf("32815")));
        assertThat(holder.getGcmId(), is("201603468"));
        assertThat(holder.getFullName(), is("Mr Bradley Jackson"));
        assertThat(holder.getFirstName(), is("Bradley"));
        assertThat(holder.getLastName(), is("Jackson"));
        assertThat(holder.getAge(), is(103));
        assertThat(holder.getDateOfBirth(), is(new DateTime("1911-11-01")));
        assertThat(holder.getGender(), is(Gender.MALE));
        assertThat(holder.isAdviser(), is(true));
        assertThat(holder.isAdminAssist(), is(false));
        assertThat(holder.isParaPlanner(), is(false));
        //assertThat(holder.isTfnProvided(), is(false));
        //assertThat(holder.getExemptionReason(), is(ExemptionReason.NO_EXEMPTION));
        assertThat(holder.getSafiDeviceId(), is("c9d93796-a77e-4805-b8f9-b62add201ab1"));
        //assertThat(holder.getResiCountryCodeForTax(), is("2061"));
        //assertThat(holder.getCisId(), is("07331610075"));
        assertThat(holder.getModificationSeq(), is("2"));
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
        assertThat(holder.getOpenDate(), is(new DateTime("2014-04-23")));
        assertThat(holder.isRegistrationOnline(), is(true));
        assertThat(holder.getTitle(), is("Mr"));
       }

    @Test
    public void testParaPlannerResponse() throws Exception
    {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/genericclient/PersonDetailsParaPlannerResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(
                TitleConverter.class)).thenReturn(titleConverter);
        Mockito.when(titleConverter.convert("1004")).thenReturn("Ms");
        Mockito.when(codeConverter.convert("2","GENDER")).thenReturn("female");
        Mockito.when(codeConverter.convert("99","EXEMPTION_REASON")).thenReturn("none");
        Mockito.when(applicationContext.getBean(
                ClientKeyConverter.class)).thenReturn(clientKeyConverter);
        Mockito.when(clientKeyConverter.convert("68833")).thenReturn(ClientKey.valueOf("68833"));

        Mockito.when(applicationContext.getBean(
                AddressKeyConverter.class)).thenReturn(addressKeyConverter);
        Mockito.when(addressKeyConverter.convert("68834")).thenReturn(AddressKey.valueOf("68834"));

        Mockito.when(applicationContext.getBean(
                DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);

        Mockito.when(dateTimeTypeConverter.convert("1992-08-16")).thenReturn(new DateTime("1992-08-16"));

         DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);

        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));


        assertThat(abstractHolder, instanceOf(GenericClientIntermediaryImpl.class));
        GenericClientIntermediaryImpl holder =(GenericClientIntermediaryImpl)abstractHolder;
        assertThat(holder.getClientKey(), is(ClientKey.valueOf("68833")));
        assertThat(holder.getGcmId(), is("201639567"));
        assertThat(holder.getFullName(), is("Ms Shirley Shi"));
        assertThat(holder.getFirstName(), is("Shirley"));
        assertThat(holder.getLastName(), is("Shi"));
        assertThat(holder.getAge(), is(23));
        assertThat(holder.getDateOfBirth(), is(new DateTime("1992-08-16")));
        assertThat(holder.getGender(), is(Gender.FEMALE));
        assertThat(holder.isAdviser(), is(false));
        assertThat(holder.isAdminAssist(), is(false));
        assertThat(holder.isParaPlanner(), is(true));

        assertThat(holder.getSafiDeviceId(), is("eeac130a-6d77-407f-a880-9e624bc696b5"));

        assertThat(holder.getModificationSeq(), is("2"));
        assertThat(holder.getAddresses(), is(notNullValue()));
        assertThat(holder.getAddresses().size(), is(not(0)));
        assertThat(holder.getAddresses().get(0), is(notNullValue()));
        assertThat(holder.getAddresses().get(0).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(0).getCategoryId(), is(3));
        assertThat(holder.getAddresses().get(0).getAddressKey(), is(AddressKey.valueOf("68834")));
        assertThat(holder.getAddresses().get(0).isMailingAddress(), is(false));
        assertThat(holder.getAddresses().get(0).isDomicile(), is(false));


        assertThat(holder.isRegistrationOnline(), is(true));
        assertThat(holder.getTitle(), is("Ms"));
    }

    @Test
    public void testAdminAssistResponse() throws Exception
    {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/genericclient/PersonDetailsAdminAssistantResp.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(
                CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(
                TitleConverter.class)).thenReturn(titleConverter);
        Mockito.when(titleConverter.convert("1002")).thenReturn("Mrs");
        Mockito.when(codeConverter.convert("2","GENDER")).thenReturn("female");
        Mockito.when(codeConverter.convert("99","EXEMPTION_REASON")).thenReturn("none");
        Mockito.when(applicationContext.getBean(
                ClientKeyConverter.class)).thenReturn(clientKeyConverter);
        Mockito.when(clientKeyConverter.convert("42689")).thenReturn(ClientKey.valueOf("42689"));

        Mockito.when(applicationContext.getBean(
                AddressKeyConverter.class)).thenReturn(addressKeyConverter);
        Mockito.when(addressKeyConverter.convert("42690")).thenReturn(AddressKey.valueOf("42690"));

        Mockito.when(applicationContext.getBean(
                DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);

        Mockito.when(dateTimeTypeConverter.convert("1977-11-28")).thenReturn(new DateTime("1977-11-28"));

        DefaultResponseExtractor<AbstractGenericClientImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(AbstractGenericClientImpl.class);

        AbstractGenericClientImpl abstractHolder = defaultResponseExtractor.extractData(content);

        assertThat(abstractHolder, is(notNullValue()));


        assertThat(abstractHolder, instanceOf(GenericClientIntermediaryImpl.class));
        GenericClientIntermediaryImpl holder =(GenericClientIntermediaryImpl)abstractHolder;
        assertThat(holder.getClientKey(), is(ClientKey.valueOf("42689")));
        assertThat(holder.getGcmId(), is("201612779"));
        assertThat(holder.getFullName(), is("Mrs Kellie OToole"));
        assertThat(holder.getFirstName(), is("Kellie"));
        assertThat(holder.getLastName(), is("OToole"));
        assertThat(holder.getAge(), is(37));
        assertThat(holder.getDateOfBirth(), is(new DateTime("1977-11-28")));
        assertThat(holder.getGender(), is(Gender.FEMALE));
        assertThat(holder.isAdviser(), is(false));
        assertThat(holder.isAdminAssist(), is(true));
        assertThat(holder.isParaPlanner(), is(false));

        assertThat(holder.getSafiDeviceId(), is("8bba42d4-ef1b-4347-be4c-b238c71fe012"));

        assertThat(holder.getModificationSeq(), is("2"));
        assertThat(holder.getAddresses(), is(notNullValue()));
        assertThat(holder.getAddresses().size(), is(not(0)));
        assertThat(holder.getAddresses().get(0), is(notNullValue()));
        assertThat(holder.getAddresses().get(0).getModificationSeq(), is("0"));
        assertThat(holder.getAddresses().get(0).getCategoryId(), is(1));
        assertThat(holder.getAddresses().get(0).getAddressKey(), is(AddressKey.valueOf("42690")));
        assertThat(holder.getAddresses().get(0).isMailingAddress(), is(false));
        assertThat(holder.getAddresses().get(0).isDomicile(), is(true));


        assertThat(holder.isRegistrationOnline(), is(false));
        assertThat(holder.getTitle(), is( "Mrs"));
    }



}
