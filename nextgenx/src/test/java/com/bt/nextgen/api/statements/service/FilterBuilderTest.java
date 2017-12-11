package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.cmis.CmisCriteriaImpl;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Restriction;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Ignore //Fails under mockito upgrade
@RunWith(MockitoJUnitRunner.class)
public class FilterBuilderTest {

    @Mock
    private UserProfileService profileService;

    @InjectMocks
    private DocumentDto documentDto;

    @InjectMocks
    private DocumentKey documentKey;

    @InjectMocks
    FilterBuilder filterUtil = new FilterBuilder(profileService, "folderId");


    public UserProfile getProfile(JobProfile jobProfile) {
        return new UserProfileAdapterImpl(null, jobProfile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertValue() {
        String value = "true";
        Object obj = filterUtil.convertValue(value, Boolean.class);
        Assert.assertTrue(obj instanceof Boolean);
        value = "2";
        obj = filterUtil.convertValue(value, Integer.class);
        Assert.assertTrue(obj instanceof Integer);
        value = "2015-08-25T14:00:00.000Z";
        obj = filterUtil.convertValue(value, DateTime.class);
        Assert.assertTrue(obj instanceof DateTime);
        DateTime dateObj = (DateTime) obj;
        Assert.assertEquals(2015, dateObj.getYear());
        Assert.assertEquals(8, dateObj.getMonthOfYear());
        value = "2015/08/25";
        obj = filterUtil.convertValue(value, DateTime.class);
        Assert.assertTrue(obj instanceof DateTime);
        Assert.assertEquals(25, dateObj.getDayOfMonth());
    }

    @Test
    public void testCreateCommonCriteriaForInvestors() {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        Mockito.when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Criteria criteria = filterUtil.createCommonCriteria();
        List<Restriction> restrictions = criteria.getRestrictionList();
        System.out.println(criteria.applyCriteria());
        Assert.assertEquals(4, restrictions.size());
        Restriction restriction = restrictions.get(0);
        Assert.assertTrue(restriction.toString().contains(DocumentConstants.COLUMN_BUSINESS_AREA));
        Assert.assertTrue(restriction.toString().contains(DocumentConstants.BUSINESS_AREA_PANORAMA));
       /* restriction = restrictions.get(1);
        Assert.assertTrue(restriction.toString().contains("cmis:objectTypeId IN"));
        Assert.assertTrue(restriction.toString().contains("PanoramaIPCorroAdhoc"));
        Assert.assertTrue(restriction.toString().contains("PanoramaIPStatement"));
        Assert.assertTrue(restriction.toString().contains("PanoramaIPSMSFDocs"));
        Assert.assertTrue(restriction.toString().contains("PanoramaIPOtherDocs"));
        restriction = restrictions.get(1);
        Assert.assertTrue(restriction.toString().contains("PanoramaIPDocumentCategory IN"));
        Assert.assertTrue(restriction.toString().contains("TAX"));
        Assert.assertTrue(restriction.toString().contains("OTHER"));
        Assert.assertTrue(restriction.toString().contains("CORRO"));
        Assert.assertTrue(restriction.toString().contains("ADVICE"));
        Assert.assertTrue(restriction.toString().contains("STM"));
        Assert.assertTrue(restriction.toString().contains("SMSF"));*/


    }
    @Test
    public void testCreateCommonCriteriaForAccountantUser() {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ACCOUNTANT);
        Mockito.when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Criteria criteria = filterUtil.createCommonCriteria();

        List<Restriction> restrictions = criteria.getRestrictionList();
        Assert.assertEquals(4, restrictions.size());
        Restriction restriction = restrictions.get(1);
        Assert.assertFalse(restriction.toString().contains(DocumentCategories.ADVICE.getCode()));
    }
    @Test
    public void testCreateCommonCriteriaForAdviser() {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ADVISER);
        Mockito.when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Criteria criteria = filterUtil.createCommonCriteria();
        System.out.println(criteria.applyCriteria());
        List<Restriction> restrictions = criteria.getRestrictionList();
        Assert.assertEquals(4, restrictions.size());
    }

    @Test
    public void testVerifyFinancialYearCriteria() {
        Criteria criteria = new CmisCriteriaImpl();
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("financialYear", "2015/2016"));
        criteriaList = filterUtil.customFilters(criteriaList, criteria);
        Assert.assertTrue(criteriaList.isEmpty());
        /*Assert.assertEquals("WHERE AND ( PanoramaIPFinancialYear = '2015/2016' " +
                "OR ( PanoramaIPEndDate >= timestamp '2015-07-01T00:00:00.000+10:00' " +
                "AND ( PanoramaIPEndDate < timestamp '2016-07-01T00:00:00.000+10:00' )))", criteria.applyCriteria());*/
        ApiSearchCriteria anotherCriteria = new ApiSearchCriteria("audit", "true");
        criteriaList.add(anotherCriteria);
        criteriaList = filterUtil.customFilters(criteriaList, criteria);
        Assert.assertEquals(1, criteriaList.size());
        Assert.assertTrue(criteriaList.contains(anotherCriteria));
    }

    @Test
    public void TestFilterUploadedRoleCriteria() {
        Criteria criteria = new CmisCriteriaImpl();
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("uploadedRole", "Panorama"));
        criteriaList = filterUtil.customFilters(criteriaList, criteria);
        Assert.assertTrue(criteriaList.isEmpty());
        Assert.assertEquals("WHERE AND ( PanoramaIPAddedByRole = 'Panorama' OR ( PanoramaIPAddedByRole IS NULL ))", criteria.applyCriteria());
        ApiSearchCriteria anotherCriteria = new ApiSearchCriteria("audit", "true");
        criteriaList.add(anotherCriteria );
        criteriaList = filterUtil.customFilters(criteriaList, criteria);
        Assert.assertEquals(1, criteriaList.size());
        Assert.assertTrue(criteriaList.contains(anotherCriteria));
    }

    @Test
    public void testCreateUserFilterCriteria(){

        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTMENT_MANAGER);
        jobProfile.setPersonJobId("123456");
        Mockito.when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Mockito.when(profileService.getPositionId()).thenReturn("123");
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("documentTitleCode", ApiSearchCriteria.SearchOperation.EQUALS,"IMRCTI"));
        System.out.print("criteriaList : : "+ criteriaList);
        Criteria criteria = filterUtil.createUserFilterCriteria(criteriaList);
        System.out.print("Criteria : : "+ criteria);
        Assert.assertEquals(3, criteria.getRestrictionList().size());
        Assert.assertTrue(criteria.applyCriteria().contains("AND ( PanoramaIPDocumentTitleCode = 'IMRCTI' )"));
        Assert.assertTrue(criteria.applyCriteria().contains("AND ( PanoramaIPRelationshipType = 'INVST_MGR_POS' )"));
        Assert.assertTrue(criteria.applyCriteria().contains("PanoramaIPRelationshipID = '123'"));


    }

    /*@Test
    public void TestCheckForDuplicateName()
    {
        documentKey.setDocumentId("145");
        documentKey.setAccountId("123");
        documentDto.setDocumentName("abc.txt");
        documentDto.setDocumentType("INVESTMENTS");
        documentDto.setKey(documentKey);
        Criteria criteria=filterUtil.createDuplicateNameCheckCriteria(documentDto);
        Assert.assertEquals("WHERE PanoramaIPRelationshipID = '123' AND ( PanoramaIPDocumentCategory = 'INVESTMENTS' ) AND ( PanoramaIPDocumentName = 'abc.txt' OR ( cmis:contentStreamFileName = 'abc.txt' ))",criteria.applyCriteria());

    }*/
}