package com.bt.nextgen.service.cmis.constants;

import com.bt.nextgen.service.avaloq.userinformation.JobRole;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * Job Role mapping for Document Library used to apply Document Visibility rules.
 */
public enum VisibilityRoles {
    ADVISER("Adviser", JobRole.ADVISER, JobRole.PARAPLANNER, JobRole.DEALER_GROUP_MANAGER, JobRole.ASSISTANT, JobRole.PRACTICE_MANAGER),
    ACCOUNTANT("Accountant", JobRole.ACCOUNTANT, JobRole.ACCOUNTANT_SUPPORT_STAFF),
    INVESTOR("Investor", JobRole.INVESTOR),
    INVESTMENT_MANAGER("InvestmentManager", JobRole.INVESTMENT_MANAGER, JobRole.PORTFOLIO_MANAGER),
    PANORAMA("Panorama", JobRole.SERVICE_AND_OPERATION),
    CHALLENGER("Challenger");

    private transient Collection<JobRole> roles;
    private String description;
    private String[] classes;
    private String[] categories;
    private VisibilityRoles[] rolesAllowedToDelete;

    VisibilityRoles(String description, JobRole... roles) {
        this.description = description;
        this.roles = asList(roles);
    }

    public Collection<JobRole> getRoles() {
        return roles;
    }

    public String getDescription() {
        return description;
    }

    public static VisibilityRoles forRole(JobRole jobRole) {
        for (VisibilityRoles visibilityRoles : VisibilityRoles.values()) {
            if (visibilityRoles.roles.contains(jobRole)) {
                return visibilityRoles;
            }
        }
        return null;
    }

    private void init(VisibilityRoles visibilityRoles) {
        switch (visibilityRoles) {
            case ADVISER:
                classes = getInvestorAndAdviserClasses();
                categories = getInvestorAndAdviserCategories();
                rolesAllowedToDelete=getTheRolesAllowedForAdviser();
                break;
            case INVESTOR:
                classes = getInvestorAndAdviserClasses();
                categories = getInvestorAndAdviserCategories();
                rolesAllowedToDelete=getTheRolesAllowedForInvestor();
                break;
            case ACCOUNTANT:
                classes = getAccountantAndSupportStaffClass();
                categories = getAccountantAndSupportStaffCategories();
                rolesAllowedToDelete=getTheRolesAllowedForAccountant();
                break;
            case PANORAMA:
                classes = getPanoramaClass();
                categories = getPanoramaCategories();
                rolesAllowedToDelete=getTheRolesAllowedForPanorama();
                break;
            case INVESTMENT_MANAGER:
                classes = getInvestmentManagerClass();
                categories = getInvestmentManagerCategories();
                rolesAllowedToDelete=getTheRolesAllowedForInvestmentManager();
                break;

            default:
                throw new IllegalArgumentException("Mapping of Role missing.");
        }
    }



    private VisibilityRoles[] getTheRolesAllowedForAdviser() {
        Set<VisibilityRoles> roles=new HashSet<>();
        roles.add(VisibilityRoles.ADVISER);
        roles.add(VisibilityRoles.INVESTOR);
        roles.add(VisibilityRoles.ACCOUNTANT);
        return roles.toArray(new VisibilityRoles[0]);
    }

    private VisibilityRoles[] getTheRolesAllowedForAccountant() {
        Set<VisibilityRoles> roles=new HashSet<>();
        roles.add(VisibilityRoles.ACCOUNTANT);
        return roles.toArray(new VisibilityRoles[0]);
    }

    private VisibilityRoles[] getTheRolesAllowedForInvestor() {
        Set<VisibilityRoles> roles=new HashSet<>();
        roles.add(VisibilityRoles.INVESTOR);
        return roles.toArray(new VisibilityRoles[0]);
    }

    private VisibilityRoles[] getTheRolesAllowedForPanorama() {
        Set<VisibilityRoles> roles=new HashSet<>();
        roles.add(VisibilityRoles.PANORAMA);
        roles.add(VisibilityRoles.INVESTOR);
        roles.add(VisibilityRoles.ACCOUNTANT);
        roles.add(VisibilityRoles.ADVISER);
        return roles.toArray(new VisibilityRoles[0]);
    }

    /**
     * Returns List of unique supported filenet class
     * @return
     */
    public List<String> getSupportedClass() {
        if (classes == null) {
            init(this);
        }
        return Arrays.asList(classes);
    }

    /**
     * Returns unique list of supported filenet categories
     * @return
     */
    public List<String> getSupportedCategories() {
        if (categories == null) {
            init(this);
        }
        return Arrays.asList(categories);
    }

    private String[] getInvestorAndAdviserCategories() {
        Set<String> categorySet = new HashSet<>();
        categorySet.add(DocumentCategories.SMSF.getCode());
        categorySet.add(DocumentCategories.ADVICE.getCode());
        categorySet.add(DocumentCategories.TAX.getCode());
        categorySet.add(DocumentCategories.CORRESPONDENCE.getCode());
        categorySet.add(DocumentCategories.OTHER.getCode());
        categorySet.add(DocumentCategories.INVESTMENTS.getCode());
        categorySet.add(DocumentCategories.STATEMENTS.getCode());
        categorySet.add(DocumentCategories.OFFLINEAPPROVAL.getCode());
        categorySet.add(DocumentCategories.TAX_SUPER.getCode());
        categorySet.add(DocumentCategories.CHALLENGER.getCode());
        return categorySet.toArray(new String[0]);
    }

    private String[] getInvestorAndAdviserClasses() {
        Set<String> classes = new HashSet<>();
        classes.add(DocumentCategories.SMSF.getDocumentClass());
        classes.add(DocumentCategories.ADVICE.getDocumentClass());
        classes.add(DocumentCategories.TAX.getDocumentClass());
        classes.add(DocumentCategories.CORRESPONDENCE.getDocumentClass());
        classes.add(DocumentCategories.OTHER.getDocumentClass());
        classes.add(DocumentCategories.INVESTMENTS.getDocumentClass());
        classes.add(DocumentCategories.STATEMENTS.getDocumentClass());
        classes.add(DocumentCategories.OFFLINEAPPROVAL.getDocumentClass());
        classes.add(DocumentCategories.TAX_SUPER.getDocumentClass());
        return classes.toArray(new String[0]);
    }

    private String[] getAccountantAndSupportStaffCategories() {
        Set<String> categorySet = new HashSet<>();
        categorySet.add(DocumentCategories.SMSF.getCode());
        categorySet.add(DocumentCategories.TAX.getCode());
        categorySet.add(DocumentCategories.CORRESPONDENCE.getCode());
        categorySet.add(DocumentCategories.OTHER.getCode());
        categorySet.add(DocumentCategories.INVESTMENTS.getCode());
        categorySet.add(DocumentCategories.STATEMENTS.getCode());
        categorySet.add(DocumentCategories.TAX_SUPER.getCode());
        return categorySet.toArray(new String[0]);
    }

    private String[] getAccountantAndSupportStaffClass() {
        Set<String> classes = new HashSet<>();
        classes.add(DocumentCategories.SMSF.getDocumentClass());
        classes.add(DocumentCategories.TAX.getDocumentClass());
        classes.add(DocumentCategories.CORRESPONDENCE.getDocumentClass());
        classes.add(DocumentCategories.OTHER.getDocumentClass());
        classes.add(DocumentCategories.INVESTMENTS.getDocumentClass());
        classes.add(DocumentCategories.STATEMENTS.getDocumentClass());
        classes.add(DocumentCategories.TAX_SUPER.getDocumentClass());
        return classes.toArray(new String[0]);
    }

    private String[] getPanoramaCategories() {
        DocumentCategories[] categories = DocumentCategories.values();
        Set<String> categoriesSet = new HashSet<>();
        for(DocumentCategories category: categories) {
            categoriesSet.add(category.getCode());
        }
        return categoriesSet.toArray(new String[0]);
    }

    private String[] getPanoramaClass() {
        DocumentCategories[] categories = DocumentCategories.values();
        Set<String> classSet = new HashSet<>();
        for(DocumentCategories category: categories) {
            classSet.add(category.getDocumentClass());
        }
        return classSet.toArray(new String[0]);
    }

    private String[] getInvestmentManagerCategories() {
        Set<String> categorySet = new HashSet<>();
        categorySet.add(DocumentCategories.STATEMENTS.getCode());
        return categorySet.toArray(new String[0]);
    }

    private String[] getInvestmentManagerClass() {
        Set<String> classes = new HashSet<>();
        classes.add(DocumentCategories.STATEMENTS.getDocumentClass());
        return classes.toArray(new String[0]);
    }

    private VisibilityRoles[] getTheRolesAllowedForInvestmentManager() {
        Set<VisibilityRoles> roles=new HashSet<>();
        roles.add(VisibilityRoles.INVESTMENT_MANAGER);
        return roles.toArray(new VisibilityRoles[0]);
    }

    public boolean isDeleteAllowed(String documentRole) {
        init(this);
        return Arrays.asList(rolesAllowedToDelete).contains(VisibilityRoles.valueOf(documentRole.toUpperCase()));
    }
}