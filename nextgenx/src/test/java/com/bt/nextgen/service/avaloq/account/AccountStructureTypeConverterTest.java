package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountStructureTypeConverterTest {

    @InjectMocks
    private AccountStructureTypeConverter accountStructureTypeConverter;

    @Mock
    private CodeConverter codeConverter;

    @Test
    public void shouldConvert() throws Exception {
        when(codeConverter.convert("20611", "ACCOUNT_STRUCTURE_TYPE")).thenReturn("I");
        when(codeConverter.convert("20612", "ACCOUNT_STRUCTURE_TYPE")).thenReturn("J");
        when(codeConverter.convert("20613", "ACCOUNT_STRUCTURE_TYPE")).thenReturn("C");
        when(codeConverter.convert("20614", "ACCOUNT_STRUCTURE_TYPE")).thenReturn("T");
        when(codeConverter.convert("20615", "ACCOUNT_STRUCTURE_TYPE")).thenReturn("S");

        assertThat(accountStructureTypeConverter.convert("20611"), equalTo(AccountStructureType.Individual));
        assertThat(accountStructureTypeConverter.convert("20612"), equalTo(AccountStructureType.Joint));
        assertThat(accountStructureTypeConverter.convert("20613"), equalTo(AccountStructureType.Company));
        assertThat(accountStructureTypeConverter.convert("20614"), equalTo(AccountStructureType.Trust));
        assertThat(accountStructureTypeConverter.convert("20615"), equalTo(AccountStructureType.SMSF));
        assertThat(accountStructureTypeConverter.convert("ANY OTHER RUBBISH"), equalTo(AccountStructureType.Unknown));
    }
}