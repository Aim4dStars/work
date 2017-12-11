package com.bt.nextgen.reports.beneficiary;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BeneficiaryDetailsPdfReportTest {
    private List<BeneficiaryDto> beneficiaryDtos;

    private Map<String, Object> paramsMap;

    private Map<String, Object> dataCollections;

    @InjectMocks
    private BeneficiaryDetailsPdfReport beneficiaryDetailsPdfReport;

    @Mock
    private BeneficiaryDtoService beneficiaryDtoService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private ContentDtoService contentService;

    @Captor
    private ArgumentCaptor<ContentKey> contentKeyCaptor;


    @Test
    public void getData() {
        initData();

        when(beneficiaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).
                thenReturn(beneficiaryDtos);
        //
        // primary code
        CodeImpl codePrimary = new CodeImpl("1", "nomn_auto_revsnry", "Auto Revisionary");
        codePrimary.addField("btfg$ui_name", Beneficiary.NOMINATION_TYPE_AUTO_REVISIONARY);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.SUPER_NOMINATION_TYPE), eq("nomn_auto_revsnry"),
                any(ServiceErrorsImpl.class))).thenReturn(codePrimary);
        // secondary code
        CodeImpl codeSecondary = new CodeImpl("2", "nomn_nbind_sis", "NOT Auto Revisionary");
        codeSecondary.addField("btfg$ui_name", "Something not AR");
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.SUPER_NOMINATION_TYPE), eq("nomn_nbind_sis"),
                any(ServiceErrorsImpl.class))).thenReturn(codeSecondary);

        Collection<?> collection = beneficiaryDetailsPdfReport.getData(paramsMap, dataCollections);
        BeneficiaryDetailsReportData beneficiaryDetailsReportData = (BeneficiaryDetailsReportData) ((List) collection).get(0);

        assertNotNull("BeneficiaryDetailsReportData is not null", beneficiaryDetailsReportData);
        assertThat("Beneficiaries Last updated datetime:", beneficiaryDetailsReportData.getBeneficiariesLastUpdatedTime(),
                is(equalTo("01 Jul 2016")));

        // primary
        assertThat("Total allocation percent is 100.00", beneficiaryDetailsReportData.getTotalAllocationPercentPrimary(),
                is(equalTo("100.00%")));
        assertThat("Primary list size is 1:", beneficiaryDetailsReportData.getPrimaryBeneficiaries().size(), is(equalTo(1)));
        BeneficiaryData beneficiaryData = beneficiaryDetailsReportData.getPrimaryBeneficiaries().get(0);
        assertThat("Beneficiary allocation percent :", beneficiaryData.getAllocationPercent(), is(equalTo("100.00%")));
        assertThat("Beneficiary dateOfBirth:", beneficiaryData.getDateOfBirth(), is(equalTo("26 Feb 1985")));
        assertThat("Beneficiary email :", beneficiaryData.getEmail(), is(equalTo("ankit.tyagi@gmail.com")));
        assertThat("Beneficiary first name :", beneficiaryData.getFirstName(), is(equalTo("Ankit")));
        assertThat("Beneficiary last name :", beneficiaryData.getLastName(), is(equalTo("Tyagi")));
        assertThat("Beneficiary phone number :", beneficiaryData.getPhoneNumber(), is(equalTo("0456 236 598")));
        assertThat("Beneficiary relationship type :", beneficiaryData.getRelationshipType(), is(equalTo("Spouse")));

        // secondary
        assertThat("Secondary list size is 2:", beneficiaryDetailsReportData.getSecondaryBeneficiaries().size(), is(equalTo(2)));
        beneficiaryData = beneficiaryDetailsReportData.getSecondaryBeneficiaries().get(0);
        assertThat("Beneficiary allocation percent :", beneficiaryData.getAllocationPercent(), is(equalTo("60.00%")));
        assertThat("Beneficiary dateOfBirth:", beneficiaryData.getDateOfBirth(), is(equalTo("26 Mar 1955")));
        assertThat("Beneficiary email :", beneficiaryData.getEmail(), is(equalTo("ankita.tyagi@gmail.com")));
        assertThat("Beneficiary first name :", beneficiaryData.getFirstName(), is(equalTo("Ankita")));
        assertThat("Beneficiary last name :", beneficiaryData.getLastName(), is(equalTo("Tyagi")));
        assertThat("Beneficiary phone number :", beneficiaryData.getPhoneNumber(), is(equalTo("0448 956 232")));
        assertThat("Beneficiary relationship type :", beneficiaryData.getRelationshipType(), is(equalTo("Interdependent")));
        //
        beneficiaryData = beneficiaryDetailsReportData.getSecondaryBeneficiaries().get(1);
        assertThat("Beneficiary allocation percent :", beneficiaryData.getAllocationPercent(), is(equalTo("40.00%")));
        assertThat("Beneficiary dateOfBirth:", beneficiaryData.getDateOfBirth(), is(equalTo("26 Mar 1956")));
        assertThat("Beneficiary email :", beneficiaryData.getEmail(), is(equalTo("b.b@gmail.com")));
        assertThat("Beneficiary first name :", beneficiaryData.getFirstName(), is(equalTo("Batman")));
        assertThat("Beneficiary last name :", beneficiaryData.getLastName(), is(equalTo("Bruce")));
        assertThat("Beneficiary phone number :", beneficiaryData.getPhoneNumber(), is(equalTo("0448 956 233")));
        assertThat("Beneficiary relationship type :", beneficiaryData.getRelationshipType(), is(equalTo("Interdependent")));
    }


    @Test
    public void getReportType() {
        assertThat(beneficiaryDetailsPdfReport.getReportType(null, null),
                equalTo("Beneficiary details"));
    }

    @Test
    public void getSummaryDescription() {
        final DateTime lastUpdatedTime = new DateTime(2016, 8, 3, 9, 5);
        final List<BeneficiaryData> dataList = new ArrayList<>();
        final BeneficiaryDetailsReportData reportData = new BeneficiaryDetailsReportData(lastUpdatedTime, dataList);
        final Map<String, Object> dataCollections = new HashMap<>();

        dataCollections.put("BeneficiaryDetailsPdfReport.beneficiaryDetails", reportData);

        assertThat(beneficiaryDetailsPdfReport.getSummaryDescription(null, dataCollections), equalTo("Last updated 03 Aug 2016"));
    }

    @Test
    public void getDisclaimer() {
        final String content = "my Content";
        final ContentDto contentDto = new ContentDto("Content Key", content);

        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);
        assertThat(beneficiaryDetailsPdfReport.getDisclaimer(null), equalTo(content));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(null);
        assertThat(beneficiaryDetailsPdfReport.getDisclaimer(null), equalTo(""));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    @Test
    public void getInfoMessageWhenNoData() {
        final String content = "no data message";
        final ContentDto contentDto = new ContentDto("Content Key", content);

        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);
        assertThat(beneficiaryDetailsPdfReport.getInfoMessageWhenNoData(null), equalTo(content));

        verify(contentService).find(contentKeyCaptor.capture(), any(ServiceErrors.class));
        assertThat(contentKeyCaptor.getValue().getContentId(), equalTo("Ins-IP-0150"));
    }

    @Test
    public void getInfoMessageSecondaryWhenNoData() {
        final String content = "no secondary data message";
        final ContentDto contentDto = new ContentDto("Content Key", content);

        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);
        assertThat(beneficiaryDetailsPdfReport.getInfoMessageSecondaryWhenNoData(null), equalTo(content));

        verify(contentService).find(contentKeyCaptor.capture(), any(ServiceErrors.class));
        assertThat(contentKeyCaptor.getValue().getContentId(), equalTo("Ins-IP-0236"));
    }

    @Test
    public void getInfoMessageAutoRevNomination() {
        final String content = "auto reversionary message";
        final ContentDto contentDto = new ContentDto("Content Key", content);

        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);
        assertThat(beneficiaryDetailsPdfReport.getInfoMessageAutoRevNomination(null), equalTo(content));

        verify(contentService).find(contentKeyCaptor.capture(), any(ServiceErrors.class));
        assertThat(contentKeyCaptor.getValue().getContentId(), equalTo("Ins-IP-0232"));
    }


    private void initData() {
        final DateTime dateTime = new DateTime(2016, 07, 01, 0, 0);
        final String totalAllocation = "100.00";

        final List<Beneficiary> beneficiaryList = new ArrayList<>();
        beneficiaryList.add(populateBeneficiaryObject("nomn_auto_revsnry", "spouse", "100.00", "26 Feb 1985", "Ankit",
                "Tyagi", "ankit.tyagi@gmail.com", "male", "0456236598"));
        beneficiaryList.add(populateBeneficiaryObject("nomn_nbind_sis", "interdependent", "60.00", "26 Mar 1955",
                "Ankita", "Tyagi", "ankita.tyagi@gmail.com", "female", "0448956232"));
        beneficiaryList.add(populateBeneficiaryObject("nomn_nbind_sis", "interdependent", "40.00", "26 Mar 1956",
                "Batman", "Bruce", "b.b@gmail.com", "male", "0448956233"));

        beneficiaryDtos = getBeneficiaryDto(dateTime, totalAllocation, beneficiaryList);
        paramsMap = new HashMap<>();
        paramsMap.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "E872817000501F2BA21043CB70EB82F2DBFADCD605F785A2");
        dataCollections = new HashMap<>();
    }


    private List<BeneficiaryDto> getBeneficiaryDto(DateTime dateTime, String totalAllocation, List<Beneficiary> beneficiaryList) {
        List<BeneficiaryDto> beneficiaryDtos = new ArrayList<>();
        BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
        beneficiaryDto.setBeneficiariesLastUpdatedTime(dateTime);
        beneficiaryDto.setTotalAllocationPercent(totalAllocation);
        beneficiaryDto.setBeneficiaries(beneficiaryList);
        beneficiaryDtos.add(beneficiaryDto);
        return beneficiaryDtos;
    }

    private Beneficiary populateBeneficiaryObject(String nominationType, String relationshipType, String allocationPercent,
                                                  String dateOfBirth, String firstName, String lastName, String email,
                                                  String gender, String phoneNumber) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setNominationType(nominationType);
        beneficiary.setRelationshipType(relationshipType);
        beneficiary.setAllocationPercent(allocationPercent);
        beneficiary.setDateOfBirth(dateOfBirth);
        beneficiary.setFirstName(firstName);
        beneficiary.setEmail(email);
        beneficiary.setLastName(lastName);
        beneficiary.setGender(gender);
        beneficiary.setPhoneNumber(phoneNumber);
        return beneficiary;
    }


}
