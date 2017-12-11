package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.cmis.CmisCriteriaImpl;
import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Restriction;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to generate Criteria String that is WHERE Clause  for the CMIS Query based on the Filter Criteria recieved
 * from UI
 */
public class FilterBuilder {

    private ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();

    private UserProfileService profileService;

    private String folderId;

    public FilterBuilder(UserProfileService profileService, String folderId) {
        this.profileService = profileService;
        this.folderId = folderId;
    }

    public Criteria createCriteriaForDocumentInfo(DocumentKey documentKey) {
        String documentId = new EncodedString(documentKey.getDocumentId()).plainText();
        Criteria criteria = new CmisCriteriaImpl();
        criteria.add(criteria.equalTo(DocumentConstants.OBJECT_ID_COLUMN, documentId));
        roleCategoryFilters(criteria);
        return criteria;
    }

    public Criteria createCriteriaForDocumentInfo(String... documentKeys) {
        List<String> documentIds = new ArrayList<>();
        for (String documentKey : documentKeys) {
            documentIds.add(new EncodedString(documentKey).plainText());
        }
        Criteria criteria = new CmisCriteriaImpl();
        criteria.add(criteria.in(DocumentConstants.OBJECT_ID_COLUMN, documentIds));
        roleCategoryFilters(criteria);
        return criteria;
    }

    public Criteria createCriteriaForUserDocumentInfo(String... documentKeys) {
        List<String> documentIds = new ArrayList<>();
        for (String documentKey : documentKeys) {
            documentIds.add(new EncodedString(documentKey).plainText());
        }
        Criteria criteria = new CmisCriteriaImpl();
        criteria.add(criteria.in(DocumentConstants.OBJECT_ID_COLUMN, documentIds));
        userRoleCategoryFilters(criteria);
        return criteria;
    }

    private void userRoleCategoryFilters(Criteria criteria) {
        VisibilityRoles role = VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole());
        //List<String> accessibleCategoryClasses = role.getSupportedClass();
        List<String> accessibleCategories = role.getSupportedCategories();
        //criteria.add(criteria.and(criteria.in(DocumentConstants.COLUMN_OBJECT_TYPE_ID, accessibleCategoryClasses)));
        criteria.add(criteria.and(criteria.in(DocumentConstants.COLUMN_DOCUMENT_TYPE, accessibleCategories)));
        //  addFolderIdCriteria(criteria);
        addDeletedCriteria(criteria);
    }

    public Criteria createDocumentsFilterCriteria(String accountNumber, List<ApiSearchCriteria> searchCriterias) {
        Criteria criteria = createCommonCriteria();
        Restriction restriction = criteria.and(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_ID, accountNumber));
        //it will be dynamically added by criteria for service ops.
        if (!profileService.isServiceOperator()) {
            criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_TYPE, Constants.RELATIONSHIP_TYPE_ACCOUNT)));
        }
        criteria.add(restriction);
        List<ApiSearchCriteria> filters = customFilters(searchCriterias, criteria);
        addCriterias(filters, criteria);
        return criteria;
    }

    private Criteria addCriterias(List<ApiSearchCriteria> searchCriterias, Criteria criteria) {
        Restriction restriction = null;
        for (ApiSearchCriteria apiSearchCriteria : searchCriterias) {
            Object value = convertValue(apiSearchCriteria.getValue(), apiSearchCriteria.getOperationType().getInstance());
            switch (apiSearchCriteria.getOperation()) {
                case EQUALS:
                    restriction = criteria.and(criteria.equalTo(processor.getColumn(apiSearchCriteria.getProperty()), value));
                    break;
                case LESS_THAN:
                    restriction = criteria.and(criteria.lessThan(processor.getColumn(apiSearchCriteria.getProperty()), value));
                    break;
                case NEG_LESS_THAN:
                    restriction = criteria.and(criteria.greaterThanEqual(processor.getColumn(apiSearchCriteria.getProperty()), value));
                    break;
                case NEG_GREATER_THAN:
                    restriction = criteria.and(criteria.lessThanEqual(processor.getColumn(apiSearchCriteria.getProperty()), value));
                    break;
                case CONTAINS:
                    restriction = criteria.and(criteria.like(processor.getColumn(apiSearchCriteria.getProperty()), value));
                    break;
                case LIST_CONTAINS:
                    restriction = criteria.and(criteria.in(processor.getColumn(apiSearchCriteria.getProperty()),
                            Arrays.asList(StringUtils.split(apiSearchCriteria.getValue(), ","))));
                    break;
                case NEG_CONTAINS:
                    restriction = criteria.and(criteria.notIn(processor.getColumn(apiSearchCriteria.getProperty()),
                            Arrays.asList(StringUtils.split(apiSearchCriteria.getValue(), ","))));
                    break;
                default:
                    break;
            }
            criteria.add(restriction);
        }
        return criteria;
    }

    /**
     * @param criteria
     */
    private void roleCategoryFilters(Criteria criteria) {
        addFolderIdCriteria(criteria);
        if (!profileService.isServiceOperator()) {
            VisibilityRoles role = VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole());
            //List<String> accessibleCategoryClasses = role.getSupportedClass();
            List<String> accessibleCategories = role.getSupportedCategories();
            //criteria.add(criteria.and(criteria.in(DocumentConstants.COLUMN_OBJECT_TYPE_ID, accessibleCategoryClasses)));
            criteria.add(criteria.and(criteria.in(DocumentConstants.COLUMN_DOCUMENT_TYPE, accessibleCategories)));
            addDeletedCriteria(criteria);
        }
    }

    /**
     * Method converts value in as per second parameter class.@See org.apache.commons.beanutils.ConvertUtils. It also supports
     * ISO Date conversion to org.joda.time.DateTime object, be careful while using it will throw runtime IllegalArgumentException
     * if date format does not matches ISO date and returns null if blank or null object provided in the value parameter
     *
     * @param value
     * @param clazz
     * @return object of type param clazz.
     */
    public Object convertValue(String value, Class<?> clazz) {
        if (clazz.isAssignableFrom(DateTime.class)) {
            return ApiFormatter.parseISODate(value);
        } else {
            return ConvertUtils.convert(value, clazz);
        }
    }

    /**
     * @return Criteria object
     */
    public Criteria createCommonCriteria() {
        Criteria criteria = new CmisCriteriaImpl();
        criteria.add(criteria.equalTo(DocumentConstants.COLUMN_BUSINESS_AREA, DocumentConstants.BUSINESS_AREA_PANORAMA));
        roleCategoryFilters(criteria);
        return criteria;
    }

    /**
     * @param criteriaList
     * @param criteria
     */
    public List<ApiSearchCriteria> customFilters(List<ApiSearchCriteria> criteriaList, Criteria criteria) {
        List<ApiSearchCriteria> criterias = new ArrayList<>();
        for (ApiSearchCriteria apiCriteria : criteriaList) {
            if ("financialYear".equalsIgnoreCase(apiCriteria.getProperty())) {
                addFinancialYearCriteria(apiCriteria.getValue(), criteria);
            } else if ("uploadedRole".equalsIgnoreCase(apiCriteria.getProperty())
                    && "Panorama".equalsIgnoreCase(apiCriteria.getValue())) {
                addUploadedRoleCriteria("Panorama", criteria);
            } else {
                criterias.add(apiCriteria);
            }
        }
        return criterias;
    }

    /**
     * @param value
     * @param criteria
     */
    public void addFinancialYearCriteria(String value, Criteria criteria) {
        if (!StringUtils.isEmpty(value)) {
            String[] financialYears = value.split("/");
            if (!ArrayUtils.isEmpty(financialYears) && financialYears.length > 1 && StringUtils.isNumeric(financialYears[1])) {
                int endYear = Integer.parseInt(financialYears[1]);
                DateTime startDate = ApiFormatter.parseDate("01 Jul " + (endYear - 1)).withTimeAtStartOfDay();
                DateTime endDate = ApiFormatter.parseDate("01 Jul " + endYear).withTimeAtStartOfDay();
                criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_FINANCIAL_YEAR, value),
                        criteria.or(criteria.greaterThanEqual(DocumentConstants.COLUMN_END_DATE, startDate),
                                criteria.and(criteria.lessThan(DocumentConstants.COLUMN_END_DATE, endDate)))));
            }
        }
    }

    /**
     * @param criteria
     */
    public void addUploadedRoleCriteria(String value, Criteria criteria) {
        criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_ADDEDBY_ROLE, value),
                criteria.or(criteria.isNull(DocumentConstants.COLUMN_ADDEDBY_ROLE))));
    }

    public Criteria createDuplicateNameCheckCriteria(DocumentDto dto, String accountNumber, String documentId) {
        Criteria criteria = new CmisCriteriaImpl();
        criteria.add(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_ID, accountNumber));
        criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_TYPE, Constants.RELATIONSHIP_TYPE_ACCOUNT)));
        criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_DOCUMENT_TYPE, dto.getDocumentType())));
        criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_DOCUMENT_NAME, dto.getDocumentName().replaceAll("'", "''"))));
        if (StringUtils.isNotEmpty(documentId)) {
            criteria.add(criteria.and(criteria.notEqual(DocumentConstants.COLUMN_OBJECT_ID, documentId)));
        }
        if (dto.getDocumentSubType() != null) {
            criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_SUB_CATEGORY, dto.getDocumentSubType())));
        } else {
            criteria.add(criteria.and(criteria.isNull(DocumentConstants.COLUMN_SUB_CATEGORY)));
        }
        if (dto.getDocumentSubType2() != null) {
            criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_SUB_CATEGORY_2, dto.getDocumentSubType2())));
        } else {
            criteria.add(criteria.and(criteria.isNull(DocumentConstants.COLUMN_SUB_CATEGORY_2)));
        }
        addDeletedCriteria(criteria);
        addFolderIdCriteria(criteria);
        return criteria;
    }

    private void addFolderIdCriteria(Criteria criteria) {
        //TODO - fetch documents from all P8 folders except notifications for serviceops desk
        if (folderId != null) {
            if (criteria.getRestrictionList().isEmpty()) {
                criteria.add(criteria.inTree(folderId));
            } else {
                criteria.add(criteria.and(criteria.inTree(folderId)));
            }
        }
    }

    private void addDeletedCriteria(Criteria criteria) {
        VisibilityRoles role = VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole());
        if (!VisibilityRoles.PANORAMA.equals(role) && folderId != null) {
            criteria.add(criteria.and(criteria.notEqual(DocumentConstants.COLUMN_DELETED, "Y")));
        }
    }

    public Criteria createUserFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        Criteria criteria = new CmisCriteriaImpl();
        JobRole role = profileService.getActiveProfile().getJobRole();
        if (JobRole.INVESTMENT_MANAGER.equals(role)) {

            //  String positionId = profileService.getActiveProfile().getPersonJobId();
            String positionId = profileService.getPositionId();
            criteria.add(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_ID, positionId));
            criteria.add(criteria.and(criteria.equalTo(DocumentConstants.COLUMN_RELATIONSHIP_TYPE, DocumentConstants.RELATIONSHIP_TYPE_INVESTMENT_MANAGER)));

            addCriterias(criteriaList, criteria);
        }
        return criteria;
    }
}