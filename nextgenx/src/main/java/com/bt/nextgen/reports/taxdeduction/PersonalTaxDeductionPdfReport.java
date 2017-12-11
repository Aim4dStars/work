package com.bt.nextgen.reports.taxdeduction;

/**
 * Created by L067218 on 22/11/2016.
 */

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionNoticeValidator;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

import static com.bt.nextgen.core.reporting.ReportFormat.CURRENCY;
import static com.bt.nextgen.core.reporting.ReportFormat.MEDIUM_DATE;
import static com.bt.nextgen.service.integration.domain.AddressMedium.MOBILE_PHONE_PRIMARY;

/**
 * This class defines the parameters for PersonalTaxDeduction pdf report.
 * Created by L067218 on 22/11/2016.
 */
@Report("personalTaxDeductionPdfReport")
public class PersonalTaxDeductionPdfReport extends AccountReportV2 {
    private static final String REPORT_NAME_NOTICE = "Notice of intent to claim a deduction for personal super contributions";
    private static final String REPORT_NAME_VARIATION = "Notice of intent to vary a deduction for personal super contributions";
    private static final String PARAM_ACCOUNT_ID = "account-id";
    private static final String PARAM_AMOUNT = "am";
    private static final String PARAM_DATE = "date";
    private static final String PARAM_DOC_ID = "di";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0146";
    private static final String DATE_PARAM_PATTERN = "[1-9][0-9]{3}-07-01";
    private static final String TFN_PROVIDED = "Provided";
    private static final String TFN_NOT_PROVIDED = "Not provided";
    private static final String SPACE = " ";

    @Autowired
    protected CmsService cmsService;
    /**
     * DTO Service for getting content information
     */
    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private PersonalTaxDeductionNoticeValidator validator;

    @Autowired
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Autowired
    private PersonalTaxDeductionPdfHelper helper;

    @Override
    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return isRequestForNewNotice((String) params.get(PARAM_DOC_ID)) ? REPORT_NAME_NOTICE : REPORT_NAME_VARIATION;
    }


    /**
     * Calls tax deduction service and account service
     *
     * @param params
     *
     * @return
     */
    @ReportBean("taxDeductionDto")
    public PersonalTaxDeductionReportData getPersonalTaxDeductionReport(Map<String, String> params) throws JRException {
        final String accId = params.get(PARAM_ACCOUNT_ID);
        // decoding account Id also validates its format
        final String accountKeyId = EncodedString.toPlainText(accId);
        final String date = params.get(PARAM_DATE);
        final String amount = params.get(PARAM_AMOUNT);
        final String docId = params.get(PARAM_DOC_ID);
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        final WrapAccountDetail account = avaloqAccountIntegrationServiceFactory.getInstance(null).
                loadWrapAccountDetail(AccountKey.valueOf(accountKeyId), serviceErrors);
        final PersonalTaxDeductionNoticeTrxnDto trxnDto = validator.validate(makeTransactionDto(amount, docId, date, accId),
                serviceErrors);

        return makeReportData(trxnDto, account, serviceErrors);
    }

    /**
     * This method retrieves the disclaimer text for the content id.
     * Sonar warning represents the issue with params not being used in the method.
     *
     * @param params map of parameters to be passed to jasper report
     *
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, String> params) {
        final ContentDto content = contentService.find(new ContentKey(DISCLAIMER_CONTENT), new FailFastErrorsImpl());

        return content != null ? content.getContent() : "";
    }

    /**
     * Personal Tax Deduction Report - header content
     *
     * @param params
     *
     * @return
     */
    @ReportBean("financialYear")
    public String getFinancialYear(Map<String, String> params) {
        final int fyStartYear = getFinancialYearStartDate(params.get(PARAM_DATE)).getYear();

        return "FY " + fyStartYear + "/" + (fyStartYear + 1);
    }

    private DateTime getFinancialYearStartDate(String dateStr) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM YYYY");
        final DateTime dateTime = DateTime.parse(dateStr);
        final String fyStartDateStr = DateUtil.getFinYearStartDate(dateTime.toDate());

        return DateTime.parse(fyStartDateStr, formatter);
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, String> params) {
        final DateTime fyStartDate = getFinancialYearStartDate(params.get(PARAM_DATE));
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM YYYY");

        return fyStartDate.plusYears(1).minusDays(1).toString(formatter);
    }

    private void validateDateParam(String dateStr) {
        if (!Pattern.compile(DATE_PARAM_PATTERN).matcher(dateStr).matches()) {
            throw new IllegalArgumentException("Invalid date parameter");
        }
    }

    private PersonalTaxDeductionReportData makeReportData(PersonalTaxDeductionNoticeTrxnDto noticeDto,
                                                          WrapAccountDetail accountDetail,
                                                          ServiceErrorsImpl serviceErrors)
            throws JRException {
        final PersonalTaxDeductionReportData reportData = new PersonalTaxDeductionReportData();
        final Client detail = ((WrapAccountDetailImpl) accountDetail).getOwners().get(0);

        setClientDetails(reportData, detail);

        reportData.setNewNotice(isRequestForNewNotice(noticeDto.getDocId()));
        reportData.setClaimAmount(ReportFormatter.format(CURRENCY, noticeDto.getAmount()));
        reportData.setPersonalContributions(ReportFormatter.format(CURRENCY, noticeDto.getTotalContributions()));
        reportData.setOriginalNoticeAmount(ReportFormatter.format(CURRENCY, noticeDto.getOriginalNoticeAmount()));
        reportData.setMemberNumber(accountDetail.getAccountNumber());
        reportData.setUsi(helper.getUsi(accountDetail, serviceErrors));

        if (serviceErrors.hasErrors()) {
            throw new JRException(serviceErrors.getFirstError());
        }
        return reportData;
    }

    private boolean isRequestForNewNotice(String docId) {
        return docId == null || docId.trim().length() == 0;
    }


    private PersonalTaxDeductionNoticeTrxnDto makeTransactionDto(String amount, String docId, String date, String accId) {
        final PersonalTaxDeductionNoticeTrxnDto trxnDto = new PersonalTaxDeductionNoticeTrxnDto();

        validateDateParam(date);

        trxnDto.setAmount(new BigDecimal(amount.replaceAll(",", "")));
        trxnDto.setDate(date);
        trxnDto.setDocId(docId);
        trxnDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey(accId));

        return trxnDto;
    }

    private void setClientDetails(PersonalTaxDeductionReportData reportData, Client client) {
        reportData.setName(getClientName(client));

        if (client.getDateOfBirth() != null) {
            reportData.setDob(ReportFormatter.format(MEDIUM_DATE, client.getDateOfBirth()));
        }

        for (Phone phone : client.getPhones()) {
            if (phone.getType().equals(MOBILE_PHONE_PRIMARY)) {
                reportData.setPhoneNumber(ReportFormatter.formatTelephoneNumber(phone.getNumber()));
            }
        }

        for (Address address : client.getAddresses()) {
            if (address.getAddressType().equals(AddressMedium.POSTAL)) {
                reportData.setAddressLine1(concatStrings(", ", getStreetAddress(address), address.getBuilding()));
                reportData.setAddressLine2(concatStrings(", ", address.getSuburb(),
                        address.getState() + SPACE + address.getPostCode(), address.getCountry()));
            }
        }

        reportData.setTfn(client instanceof InvestorDetailImpl && ((InvestorDetailImpl) client).getTfnProvided() ?
                TFN_PROVIDED : TFN_NOT_PROVIDED);
    }

    private String getClientName(Client client) {
        if (client.getTitle() == null || client.getTitle().trim().length() == 0) {
            return getFullName(client);
        }
        else {
            return client.getTitle().trim() + " " + getFullName(client);
        }
    }

    private String getFullName(Client client) {
        if (client instanceof IndividualDetailImpl) {
            final IndividualDetailImpl individual = (IndividualDetailImpl) client;

            return concatStrings(" ", individual.getFirstName(), individual.getMiddleName(), individual.getLastName());
        }

        return client.getFullName().trim();
    }


    private String getStreetAddress(Address address) {
        return concatStrings("/",
                address.getUnit(),
                concatStrings(SPACE, address.getStreetNumber(), address.getStreetName(), address.getStreetType()));
    }

    private String concatStrings(final String separator, String... strings) {
        final StringBuilder retval = new StringBuilder();

        for (String str : strings) {
            if (str != null && str.trim().length() > 0) {
                if (retval.length() == 0) {
                    retval.append(str.trim());
                }
                else {
                    retval.append(separator + str.trim());
                }
            }
        }

        return retval.toString();
    }

    @ReportImage("infoIcon")
    public Renderable getInfoIcon(Map<String, String> params) throws JRException, IOException {
        return getRasterImage(cmsService.getContent("infoIcon"));
    }
}
