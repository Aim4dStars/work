package com.bt.nextgen.reports.taxdeduction;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionNoticeValidator;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests {@link PersonalTaxDeductionPdfReport}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionPdfReportTest {
    private static final String ACCOUNT_ID = "123";
    private static final String ACCOUNT_NUMBER = "9876";
    private static final String DOC_ID = "998";
    private static final String USI = "123456789";

    private static final String PARAM_ACCOUNT_ID = "account-id";
    private static final String PARAM_AMOUNT = "am";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_DOC_ID = "di";


    @Mock
    private ContentDtoService contentService;

    @Mock
    private PersonalTaxDeductionNoticeValidator validator;

    @Mock
    private PersonalTaxDeductionPdfHelper helper;

    @Mock
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private WrapAccountDetailImpl accountDetail;

    @Mock
    private Address address;

    @Mock
    private Phone phone;

    @InjectMocks
    private PersonalTaxDeductionPdfReport report;


    @Test
    public void getReportType() {
        getReportType(null, "Notice of intent to claim a deduction for personal super contributions");
        getReportType("", "Notice of intent to claim a deduction for personal super contributions");
        getReportType("123", "Notice of intent to vary a deduction for personal super contributions");
    }


    @Test
    public void getPersonalTaxDeductionReport() throws JRException {
        getPersonalTaxDeductionReport("New notice with all values available", ACCOUNT_ID, ACCOUNT_NUMBER, "2016-07-01",
                "5678901.23", null, "1234.56", null, USI, makeInvestor("Mr", "Full Name1", "1998-04-03", "44888777",
                        "0401234567", "Pitt Street", "Sydney", "NSW", "2001", "Australia"),
                "Mr Full Name1", "$5,678,901.23", "-", "$1,234.56",
                "03 April 1998", "0401 234 567", "Pitt Street", "Sydney, NSW 2001, Australia", "Not provided");

        getPersonalTaxDeductionReport("Notice variation with all values available", ACCOUNT_ID, ACCOUNT_NUMBER,
                "2016-07-01",
                "5678901.23", "10876543.43", "1234.56", DOC_ID, USI, makeIndividualDetail(" Ms ", "First", "Middle",
                        "Last", "1998-04-03", "44888777",
                        "0298765432", "Pitt Street", "Sydney", "NSW", "2001", "Australia"),
                "Ms First Middle Last", "$5,678,901.23", "$10,876,543.43", "$1,234.56",
                "03 April 1998", "(02) 9876 5432", "Pitt Street", "Sydney, NSW 2001, Australia", "Provided");

        getPersonalTaxDeductionReport("Small amounts and no TFN", ACCOUNT_ID, ACCOUNT_NUMBER, "2016-07-01", "1", "2.32",
                "0.02", DOC_ID, USI, makeInvestor(null, "Full Name1", "1998-04-03", null, "0401234567",
                        "Pitt Street", "Sydney", "NSW", "2001", "Australia"),
                "Full Name1", "$1.00", "$2.32", "$0.02",
                "03 April 1998", "0401 234 567", "Pitt Street", "Sydney, NSW 2001, Australia", "Not provided");

        getPersonalTaxDeductionReport("No phone number", ACCOUNT_ID, ACCOUNT_NUMBER, "2016-07-01",
                "5678901.23", "10876543.43", "1234.56", null, USI, makeInvestor("", "Super Trust Name", "1998-04-03", "44888777",
                        null, "Pitt Street", "Sydney", "NSW", "2001", "Australia"),
                "Super Trust Name", "$5,678,901.23", "$10,876,543.43", "$1,234.56",
                "03 April 1998", null, "Pitt Street", "Sydney, NSW 2001, Australia", "Not provided");

        getPersonalTaxDeductionReport("No postal address", ACCOUNT_ID, ACCOUNT_NUMBER,
                "2016-07-01",
                "5678901.23", "10876543.43", "1234.56", DOC_ID, USI, makeInvestor("   ", " Super Trust Name ", "1998-04-03", "44888777",
                        "0401234567", null, null, null, null, null),
                "Super Trust Name", "$5,678,901.23", "$10,876,543.43", "$1,234.56",
                "03 April 1998", "0401 234 567", null, null, "Not provided");
    }

    @Test
    public void getDisclaimerWithNullContent() {
        getDisclaimer(true, "");
        getDisclaimer(false, "");
        getDisclaimer(false, "my disclaimer text");
    }

    @Test
    public void getFinancialYear() {
        getFinancialYear("2016-06-30", "FY 2015/2016");
        getFinancialYear("2016-07-01", "FY 2016/2017");
        getFinancialYear("2016-12-23", "FY 2016/2017");
        getFinancialYear("2017-07-01", "FY 2017/2018");
    }

    @Test
    public void getEndDate() {
        getEndDate("2016-06-30", "30 June 2016");
        getEndDate("2016-07-01", "30 June 2017");
        getEndDate("2016-12-23", "30 June 2017");
        getEndDate("2017-07-01", "30 June 2018");
    }


    private void getPersonalTaxDeductionReport(String infoStr, String accountId, String accountNumber, String dateStr,
                                              String amountStr, String originalNoticeAmountStr, String totalContributionsStr,
                                              String docId, String usi,
                                              Client client,
                                              String expectedClientName,
                                              String expectedClaimAmountStr, String expectedOriginalNoticeAmountStr,
                                              String expectedTotalContributionsStr,
                                              String expectedDateOfBirthStr, String expectedPhone,
                                              String expectedAddrLine1, String expectedAddrLine2,
                                              String expectedTfn) throws JRException {
        final Map<String, String> params = new HashMap<>();
        final PersonalTaxDeductionNoticeTrxnDto trxnDto = makeTransactionDto(amountStr, originalNoticeAmountStr, docId, dateStr, accountId,
                totalContributionsStr);
        final PersonalTaxDeductionReportData reportData;

        params.put(PARAM_ACCOUNT_ID, EncodedString.fromPlainText(accountId).toString());
        params.put(PARAM_DATE, dateStr);
        params.put(PARAM_AMOUNT, amountStr);
        params.put(PARAM_DOC_ID, docId);

        reset(validator, avaloqAccountIntegrationServiceFactory, accountIntegrationService, accountDetail, address,
                phone);
        when(helper.getUsi(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn(USI);
        when(validator.validate(any(PersonalTaxDeductionNoticeTrxnDto.class), any(ServiceErrors.class)))
                .thenReturn(trxnDto);
        when(avaloqAccountIntegrationServiceFactory.getInstance(null)).thenReturn(accountIntegrationService);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        when(accountDetail.getAccountNumber()).thenReturn(accountNumber);
        when(accountDetail.getOwners()).thenReturn(Arrays.asList(client));

        reportData = report.getPersonalTaxDeductionReport(params);
        verify(validator).validate(any(PersonalTaxDeductionNoticeTrxnDto.class), any(ServiceErrors.class));
        verify(avaloqAccountIntegrationServiceFactory).getInstance(null);
        verify(accountIntegrationService).loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class));
        verify(accountDetail).getAccountNumber();
        verify(accountDetail).getOwners();

        assertThat(infoStr + ": newNotice", reportData.isNewNotice(), equalTo(docId == null || docId.trim() == ""));
        assertThat(infoStr + ": claimAmount", reportData.getClaimAmount(), equalTo(expectedClaimAmountStr));
        assertThat(infoStr + ": originalNoticeAmountStr", reportData.getOriginalNoticeAmount(), equalTo(expectedOriginalNoticeAmountStr));
        assertThat(infoStr + ": totalContributions", reportData.getPersonalContributions(), equalTo(expectedTotalContributionsStr));
        assertThat(infoStr + ": memberNumber", reportData.getMemberNumber(), equalTo(accountNumber));
        assertThat(infoStr + ": USI", reportData.getUsi(), equalTo(usi));
        assertThat(infoStr + ": Name", reportData.getName(), equalTo(expectedClientName));
        assertThat(infoStr + ": DateOfBirth", reportData.getDob(), equalTo(expectedDateOfBirthStr));
        assertThat(infoStr + ": Phone", reportData.getPhoneNumber(), equalTo(expectedPhone));
        assertThat(infoStr + ": AddressLine1", reportData.getAddressLine1(), equalTo(expectedAddrLine1));
        assertThat(infoStr + ": AddressLine2", reportData.getAddressLine2(), equalTo(expectedAddrLine2));
        assertThat(infoStr + ": TFN", reportData.getTfn(), equalTo(expectedTfn));
    }

    private void getReportType(String docId, String expectedReportType) {
        final PersonalTaxDeductionReportData reportData = new PersonalTaxDeductionReportData();
        final Map<String, Object> params = new HashMap<>();

        params.put(PARAM_DOC_ID, docId);

        assertThat("report name: docId = " + docId, report.getReportType(params, null),
                equalTo(expectedReportType));
    }

    private void getDisclaimer(boolean nullContent, String disclaimerText) {
        final ContentDto contentDto = nullContent ? null : new ContentDto("DS-IP-0146", disclaimerText);

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);

        assertThat("disclaimerText: nullContent = " + nullContent + "disclaimerText = " + disclaimerText,
                report.getDisclaimer(null), equalTo(disclaimerText));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    private void getFinancialYear(String dateStr, String expected) {
        final Map<String, String> params = new HashMap<>();

        params.put(PARAM_DATE, dateStr);

        assertThat("Financial year: dateStr = " + dateStr, report.getFinancialYear(params), equalTo(expected));
    }

    private void getEndDate(String dateStr, String expected) {
        final Map<String, String> params = new HashMap<>();

        params.put(PARAM_DATE, dateStr);

        assertThat("Financial year: dateStr = " + dateStr, report.getEndDate(params), equalTo(expected));
    }

    private PersonalTaxDeductionNoticeTrxnDto makeTransactionDto(String amountStr, String originalNoticeAmountStr,
                                                                 String docId, String date,
                                                                 String accId, String totalContributionsStr) {
        final PersonalTaxDeductionNoticeTrxnDto trxnDto = new PersonalTaxDeductionNoticeTrxnDto();

        trxnDto.setAmount(new BigDecimal(amountStr));
        trxnDto.setDate(date);
        trxnDto.setDocId(docId);
        trxnDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey(accId));
        trxnDto.setTotalContributions(new BigDecimal(totalContributionsStr));

        if (originalNoticeAmountStr != null) {
            trxnDto.setOriginalNoticeAmount(new BigDecimal(originalNoticeAmountStr));
        }

        return trxnDto;
    }

    private Client makeIndividualDetail(final String title, final String firstName, final String middleName, final String lastName,
                                        final String dateOfBirthStr, final String tfn,
                                        final String mobilePhone, final String streetName, final String suburb, final String state,
                                        final String postCode, final String country) {
        return new IndividualDetailImpl() {
            @Override
            public boolean getTfnProvided() {
                return tfn != null;
            }

            @Override
            public ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(ClientKey clientKey) {

            }

            @Override
            public String getFirstName() {
                return firstName;
            }

            @Override
            public String getMiddleName() {
                return middleName;
            }

            @Override
            public String getLastName() {
                return lastName;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                final ArrayList<Address> addresses = new ArrayList<>();

                if (streetName != null) {
                    when(address.getAddressType()).thenReturn(AddressMedium.POSTAL);
                    when(address.getStreetName()).thenReturn(streetName);
                    when(address.getSuburb()).thenReturn(suburb);
                    when(address.getState()).thenReturn(state);
                    when(address.getPostCode()).thenReturn(postCode);
                    when(address.getCountry()).thenReturn(country);
                    addresses.add(address);
                }

                return addresses;
            }

            @Override
            public List<Email> getEmails() {
                return null;
            }

            @Override
            public List<Phone> getPhones() {
                final List<Phone> phones = new ArrayList<>();

                if (mobilePhone != null) {
                    when(phone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_PRIMARY);
                    when(phone.getNumber()).thenReturn(mobilePhone);
                    phones.add(phone);
                }

                return phones;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return dateOfBirthStr == null ? null : new DateTime(dateOfBirthStr);
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public InvestorType getLegalForm() {
                return null;
            }
        };
    }

    private Client makeInvestor(final String title, final String fullName, final String dateOfBirthStr, final String tfn,
                                final String mobilePhone, final String streetName, final String suburb, final String state,
                                final String postCode, final String country) {
        return new InvestorImpl() {
            @Override
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                final ArrayList<Address> addresses = new ArrayList<>();

                if (streetName != null) {
                    when(address.getAddressType()).thenReturn(AddressMedium.POSTAL);
                    when(address.getStreetName()).thenReturn(streetName);
                    when(address.getSuburb()).thenReturn(suburb);
                    when(address.getState()).thenReturn(state);
                    when(address.getPostCode()).thenReturn(postCode);
                    when(address.getCountry()).thenReturn(country);
                    addresses.add(address);
                }

                return addresses;
            }

            @Override
            public List<Email> getEmails() {
                return null;
            }

            @Override
            public List<Phone> getPhones() {
                final List<Phone> phones = new ArrayList<>();

                if (mobilePhone != null) {
                    when(phone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_PRIMARY);
                    when(phone.getNumber()).thenReturn(mobilePhone);
                    phones.add(phone);
                }

                return phones;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return dateOfBirthStr == null ? null : new DateTime(dateOfBirthStr);
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return fullName;
            }

            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(ClientKey clientKey) {
            }
        };
    }
}
