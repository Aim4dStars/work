package com.bt.nextgen.service.integration.cashcategorisation.report;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.MemberContributionDto;
import com.bt.nextgen.service.integration.cashcategorisation.service.RetrieveCashContributionDtoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static com.bt.nextgen.core.api.UriMappingConstants.ACCOUNT_ID_URI_MAPPING;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionDetailCsvReportTest {
    private static final String ACCOUNT_ID = "abc123";
    private static final String FINANCIAL_YEAR = "financialYearDate";

    @InjectMocks
    private TransactionDetailCsvReport transactionDetailCsvReport;

    @Mock
    private ContentDtoService contentDtoService;

    @Mock
    private RetrieveCashContributionDtoService contributionDtoService;

    @Mock
    private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

    @Test
    public void getDisclaimer() {
        final ContentDto disclaimerContent = new ContentDto( "DS-IP-0088", "my disclaimer" );
        when( contentDtoService.find( any( ContentKey.class ), any( ServiceErrors.class ) ) ).thenReturn( disclaimerContent );
        assertThat( "fetches the disclaimer", transactionDetailCsvReport.getDisclaimer( new HashMap<String, String>() ), equalTo( "my disclaimer" ) );
    }

    @Test
    public void getReportName() {
        assertThat( "gets the report name", transactionDetailCsvReport.getReportName( new HashMap<String, String>() ), equalTo( "Categorised Cash Transactions" ) );
    }

    @Test
    public void sortMemberList() {
        //memberOneContributionDto
        final MemberContributionDto memberOneContributionDto = getMemberContributionDetails( "85691", "Martin Taylor", "contri", "spouse_chld_contri", BigDecimal.valueOf( 11000 ), "Cash", "12 July 2016" );
        //memberTwoContributionDto
        final MemberContributionDto memberTwoContributionDto = getMemberContributionDetails( "65691", "John Cusackr", "contri", "empl", BigDecimal.valueOf( 5000 ), "Cash", "12 April 2016" );

        final List<MemberContributionDto> memberContributionDtoList = Arrays.asList( memberOneContributionDto, memberTwoContributionDto );

        /*After calling sortMemberList ContributionDtoList gets sorted according to
        Transaction Date,
        Transaction ID,
        Category,
        Sub Category,
        Member,
        Amount */
        transactionDetailCsvReport.sortMemberList( memberContributionDtoList );
        assertThat( "memberOneContributionDto gets replaced by memberTwoContributionDto after sorting", memberContributionDtoList.get( 0 ), equalTo( memberTwoContributionDto ) );
        assertThat( "memberTwoContributionDto gets replaced by memberOneContributionDto after sorting", memberContributionDtoList.get( 1 ), equalTo( memberOneContributionDto ) );

    }

    @Test
    public void transactionDetailsWithSubcategory() {
        getTransactionDetails( true );

    }

    @Test
    public void transactionDetailsWithoutSubcategory() {
        getTransactionDetails( false );
    }

    private void getTransactionDetails(boolean isSubCategoriesPresent) {
        final Map<String, String> params = new HashMap<>();
        final List<MemberContributionDto> memberContributionDtoList = new ArrayList<>();
        final CategorisableCashTransactionDto dto = new CategorisableCashTransactionDto();
        List<TransactionCategoryDto> tranCatDtoList = new ArrayList<>();
        final String dateStr = "2015-07-01";

        params.put( ACCOUNT_ID_URI_MAPPING, EncodedString.fromPlainText( ACCOUNT_ID ).toString() );
        params.put( FINANCIAL_YEAR, dateStr );

        final MemberContributionDto memberContributionDto = getMemberContributionDetails( "85691", "Martin Taylor", "", "spouse_chld_contri", BigDecimal.valueOf( 11000 ), "12 April 2016", "Cash" );
        memberContributionDtoList.add( memberContributionDto );
        dto.setAccountId( ACCOUNT_ID );
        dto.setFromDate( "12 July 2016" );
        dto.setToDate( "12 May 2017" );
        dto.setMemberContributionDtoList( memberContributionDtoList );

        TransactionCategoryDto dto1 = new TransactionCategoryDto();

        dto1.setIntlId( "contri" );
        dto1.setLabel( "Contribution" );
        dto1.setTransactionMetaType( "Payment" );
        List<StaticCodeDto> subCategories1 = new ArrayList<>();
        if (isSubCategoriesPresent) {
            //Static code dto1
            StaticCodeDto staticDto1 = new StaticCodeDto();
            staticDto1.setId( "1" );
            staticDto1.setIntlId( "spouse_chld_contri" );
            staticDto1.setLabel( "Spouse & children Contribution" );
            subCategories1.add( staticDto1 );
        } else {
            memberContributionDto.setContributionSubType( "contri" );
        }
        dto1.setSubCategories( subCategories1 );
        tranCatDtoList.add( dto1 );

        when( (CategorisableCashTransactionDto) contributionDtoService.getCashTransactionsForAccounts( anyList(), any( ServiceErrors.class ) ) ).thenReturn( dto );
        when( transactionCategoryDtoServiceImpl.search( anyList(), any( ServiceErrors.class ) ) ).thenReturn( tranCatDtoList );
        assertThat( "it fetches and returns CategorisableCashTransactionDto", transactionDetailCsvReport.getTransactionDetails( params ), equalTo( dto ) );

        // assets to test setCategoryTypeInDto()
        assertThat( "it sets the label of transactionCategory", memberContributionDto.getTransactionCategory(), equalTo( dto1.getLabel() ) );
        assertThat( "it sets the transactionType", memberContributionDto.getTransactionType(), equalTo( dto1.getTransactionMetaType() ) );
        if (isSubCategoriesPresent) {
            assertThat( "it sets the contributionSubType", memberContributionDto.getContributionSubType(), equalTo( dto1.getSubCategories().get( 0 ).getLabel() ) );
        }
        // asserts to test toDate and fromDate set by setFinancialYearDates()
        assertThat( "fromDate(12 July 2016) gets formatted", dto.getFromDate(), equalTo( "01/07/2015" ) );
        assertThat( "toDate(12 May 2017) gets formatted", dto.getToDate(), equalTo( "30/06/2016" ) );

    }


    private MemberContributionDto getMemberContributionDetails(String personId, String fullName,
                                                               String transactionCategory, String contributionSubType, BigDecimal amount, String transactionDate, String transactionType) {
        final MemberContributionDto memberContributionDto = new MemberContributionDto();
        memberContributionDto.setPersonId( personId );
        memberContributionDto.setFullName( fullName );
        memberContributionDto.setTransactionCategory( transactionCategory );
        memberContributionDto.setContributionSubType( contributionSubType );
        memberContributionDto.setAmount( amount );
        memberContributionDto.setTransactionType( transactionType );
        memberContributionDto.setTransactionDate( transactionDate );
        return memberContributionDto;
    }


}