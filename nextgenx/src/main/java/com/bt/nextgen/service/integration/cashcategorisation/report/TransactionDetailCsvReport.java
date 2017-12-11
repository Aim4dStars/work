package com.bt.nextgen.service.integration.cashcategorisation.report;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.ReportUtils;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.MemberContributionDto;
import com.bt.nextgen.service.integration.cashcategorisation.service.RetrieveCashContributionDtoService;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

/**
 * Transaction detail csv report
 */
@SuppressWarnings({"squid:S1172", "findbugs:NM_METHOD_NAMING_CONVENTION"})
@Report("transactionDetailCsvReport")
public class TransactionDetailCsvReport extends AccountReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0028";

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private RetrieveCashContributionDtoService contributionDtoService;

    @Autowired
    private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

    /**
     * Setting the start of the financial year and the end of the financial year
     * @param fromDate
     * @param categorisableCashTransactionDto
     */
    private void setFinancialYearDates(String fromDate, CategorisableCashTransactionDto categorisableCashTransactionDto){
        if (StringUtils.isNotBlank(fromDate) && categorisableCashTransactionDto!=null){
            DateTime fromDateTime = DateTime.parse(fromDate, DateTimeFormat.forPattern("YYYY-MM-DD"));
            fromDateTime = new DateTime(fromDateTime.getYear(),7,1,0,0); // setting to 1st July start of financial year
            DateTime toDateTime = new DateTime(fromDateTime.getYear()+1,6,30,0,0); // setting to 30th June end of financial year
            categorisableCashTransactionDto.setFromDate(ReportUtils.toSimpleDateString(fromDateTime));
            categorisableCashTransactionDto.setToDate(ReportUtils.toSimpleDateString(toDateTime));
        }
    }

    /**
     * Returns the transaction details to generate the csv
     * @param params
     * @return
     */
    @ReportBean("transactionDetailDto")
    public CategorisableCashTransactionDto getTransactionDetails(Map<String, String> params) {
        //String depositId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        String accId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        String date = params.get("financialYearDate");

        if (StringUtils.isEmpty(accId))
        {
            throw new IllegalArgumentException("Account id is not valid");
        }

        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accId), ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, date, ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(accountIdCriteria);
        searchCriteriaList.add(dateCriteria);

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        CategorisableCashTransactionDto categorisableCashTransactionDto = (CategorisableCashTransactionDto)
                contributionDtoService.getCashTransactionsForAccounts(searchCriteriaList, serviceErrors);

        setFinancialYearDates(date,categorisableCashTransactionDto);
        List <MemberContributionDto> contributionList = categorisableCashTransactionDto.getMemberContributionDtoList();
        List <ApiSearchCriteria> criteriaListForCatType = new ArrayList<>();
        List<TransactionCategoryDto> tranCatDtoList = transactionCategoryDtoServiceImpl.search(criteriaListForCatType, serviceErrors);
        for (MemberContributionDto memberContributionDto:  contributionList){
            setCategoryTypeInDto(tranCatDtoList,memberContributionDto);
            memberContributionDto.setTransactionDate(ReportUtils.toSimpleDateString(ApiFormatter.parseDate(memberContributionDto.getTransactionDate())));
        }
        sortMemberList(contributionList);
        return categorisableCashTransactionDto;
    }

    public void sortMemberList(List <MemberContributionDto> contributionList) {
        Collections.sort(contributionList, new MemberComparator());
    }

    /**
     * Comparator for sorting the members of the transaction detail based on the fields in order
     * 1.	Transaction Date
     2.	Transaction ID
     3.	Category
     4.	Sub Category
     5.	Member
     6.	Amount
     */
    static class MemberComparator implements Comparator<MemberContributionDto>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(MemberContributionDto memberDto1, MemberContributionDto memberDto2) {
            return new CompareToBuilder().append(memberDto1.getSortDate(), memberDto2.getSortDate()).
                    append(memberDto1.getDocId(), memberDto2.getDocId()).
                    append(memberDto1.getTransactionType(), memberDto2.getTransactionType()).
                    append(memberDto1.getTransactionCategory(), memberDto2.getTransactionCategory()).
                    append(memberDto1.getContributionSubType(), memberDto2.getContributionSubType()).
                    append(memberDto1.getFullName(), memberDto2.getFullName()).
                    append(memberDto1.getAmount(), memberDto2.getAmount()).toComparison();
        }
    }

    /**
     * Sets the transaction Category Name based on the subcategory internal id
     * Also sets the subcategory name and the transaction type
     * @param tranCatDtoList
     * @param dto
     */
    private void setCategoryTypeInDto(List<TransactionCategoryDto> tranCatDtoList, MemberContributionDto dto)
    {
        for(TransactionCategoryDto catDto : tranCatDtoList){
            if(!(catDto.getSubCategories().isEmpty())){
                for(StaticCodeDto subCategory: catDto.getSubCategories()){
                    if(subCategory.getIntlId().equals(dto.getContributionSubType())){
                        dto.setTransactionCategory(catDto.getLabel());
                        dto.setContributionSubType(subCategory.getLabel());
                        dto.setTransactionType(StringUtil.toProperCase(catDto.getTransactionMetaType()));
                        return;
                    }
                }
            }
            else{
                if(catDto.getIntlId().equals(dto.getContributionSubType())) {
                    dto.setTransactionCategory(catDto.getLabel());
                    dto.setTransactionType(StringUtil.toProperCase(catDto.getTransactionMetaType()));
                }
            }
        }
    }

    @ReportBean("reportType")
    public String getReportName(Map<String, String> params)
    {
        return "Categorised Cash Transactions";
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map <String, String> params)
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content!=null ? content.getContent() : "";
    }

}
