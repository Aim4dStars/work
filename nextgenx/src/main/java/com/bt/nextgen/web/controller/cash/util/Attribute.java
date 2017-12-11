package com.bt.nextgen.web.controller.cash.util;

@SuppressWarnings("squid:S2068")
public final class Attribute {

    private Attribute() {
    }

    public static final String MOVE_MONEY_MODEL = "moveMoneyModel";
    public static final String REGISTRATION_MODEL = "registrationModel";
    public static final String PASSWORD_RESET_MODEL = "passwordResetModel";
    public static final String SMS_CODE_MODEL = "smsCodeModel";
    public static final String PAYMENT_ID = "paymentId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String USER_ID = "userId";
    public static final String PAYEE_TYPE = "payeeType";
    public static final String TERMS_AND_CONDITIONS = "termsAndConditions";
    public static final String DDA_TERMS_AND_CONDITIONS = "ddaTermsAndConditions";
    public static final String ACCOUNT_STATUS_MODEL = "accountStatusModel";
    public static final String PORTFOLIO_MODEL = "portfolioModel";
    public static final String RENEW_MODE_ID = "renewModeId";
    public static final String MESSAGE = "message";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String EMPTY_STRING = "";
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final String FAILURE_MESSAGE = "FAIL";
    public static final String WARNING_MESSAGE = "WARNING";
    public static final String ERROR_MESSAGE = "ERROR";
    public static final String USER_RESET_MODEL = "userReset";
    public static final String ADVISER_ID = "adviserId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ASSET_CODE = "assetCode";
    public static final String ACCOUNT_ID = "accountId";
    public static final String CA_TYPE = "type";
    public static final String CA_ROA = "roa";
    public static final String COMPREHENSIVE_ASSET_PRICE = "comprehensive";
    public static final String LIVE_ASSET_PRICE = "live";
    public static final String FALLBACK = "fallback";
    public static final String CACHE = "cache";
    public static final String TRUSTEE_APPROVAL_ACCESS = "trusteeApprovalAccess";
    public static final String IRG_APPROVAL_ACCESS = "irgApprovalAccess";


    /**
     * @deprecated Id is deprecated
     */
    @Deprecated
    public static final String PORTFOLIO_ID = "portfolioId";
    public static final String LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String ORDER_ID = "orderId";
    public static final String PERSON_MODEL = "person";
    public static final String ROLE_INVESTOR = "ROLE_INVESTOR";
    public static final String ADVISER_GLOBAL_ALERT_NOTIFICATION = "adviserGlobalAlertNotification";
    public static final String GLOBAL_ALERT_NOTIFICATION = "globalAlertNotification";
    public static final String CLIENT_GLOBAL_ALERT_NOTIFICATION = "clientGlobalAlertNotification";
    public static final String SAFI_KEY = "safi";
    public static final String BT_ESB_VALIDATE_PARTY_KEY = "btesb-validate-party";
    public static final String BT_ESB_VALIDATE_PARTY_SMS_KEY = "btesb-validate-party-sms";
    public static final String PARTY_KEY = "party";
    public static final String ONBOARDING_KEY = "onboarding";
    public static final String RETRIEVE_TOKEN_KEY = "oauth2-retrieve-token";
    public static final String SAVE_TOKEN_KEY = "oauth2-save-token";
    public static final String APPLICATION_SUBMISSION_KEY = "application-submission";
    public static final String ADVISOR_ONBOARDING_KEY = "process-adviser-onboarding";
    public static final String INVESTOR_ONBOARDING_KEY = "process-investor-onboarding";
    public static final String PROVISION_MFA_DEVICE="provision-mfa-device";
    public static final String RESEND_REGISTRATION_ONBOARDING_KEY = "resend-registration-onboarding";
    public static final String RESEND_EXISTING_REGISTRATION_CODE_KEY = "resend-existing-registration-code";
    public static final String GROUP_ESB_GENERATE_SECURITY_CREDENTIAL = "gesb-generate-credentials";
    public static final String GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL = "gesb-modify-username";
    public static final String GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL = "gesb-modify-password";
    public static final String GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS = "gesb-maintain-mfadevicearrangements";
    public static final String GROUP_ESB_UPDATE_PPID = "gesb-modify-ppid";
    public static final String USER_NAME = "username";
    public static final String SMS_CODE = "smsCode";
    public static final String OBFUSCATION_URL = "obfuscationUrl";

    public static final String MOBILE = "Mobile";
    public static final String LANDLINE = "Landline";
    public static final String PAYMENT = "payment";

    public static final String EVENT_MODEL = "eventModel";
    public static final String ATTACHMENT = "attachment";
    public static final String TERM_DEPOSIT_FILENAME = "Term_deposits_rate.csv";
    public static final String APPLICATION_CSV = "application/csv";

    public static final String LOAD_VALIDATION_ERROR_MESSAGES = "loadValidationErrorMessages";

    public static final String LOGON_BRAND = "logonBrand";
    public static final String USERNAME_FIELD_NAME = "usernameFieldName";
    public static final String PASSWORD_FIELD_NAME = "passwordFieldName";
    public static final String BRAND_FIELD_NAME = "brandFieldName";
    public static final String HALGM_FIELD_NAME = "halgmFieldName";

    public static final String POSTAL = "Postal";
    public static final String RESIDENTIAL = "Residential";
    public static final String ACCOUNT_LOCKED_MESSAGE = "LOCKED";

    public static final String SERVICE_OPS_MODEL = "serviceOpsModel";
    public static final String SUCCESS = "Success";

    public static final String CLIENTS = "Clients";

    //Emulation Data
    public static final String IS_EMULATING = "isEmulating";
    public static final String ACTION_PERFORMED = "actionPerformed";

    public static final String CLIENT_STATEMENT_SUCCESS_STATUS = "Success";
    public static final String CLIENT_STATEMENT_ERROR_STATUS = "Error";
    public static final String REPORT_SOURCE_BASIL = "BASIL";
    public static final String QRTLY_PAYG_STMTS = "Quarterly PAYG statements";
    public static final String QRTLY_STMTS = "Quarterly statements";
    public static final String ANNUAL_STMTS = "Annual statements";
    public static final String EXIT_STMTS = "Exit Statement/Closure";
    public static final String FAILURE_NOTIFICATION = "E-Statement Failure Notification";
    public static final String ANNUAL_INVESTOR_STMT = "Annual investor statement";
    public static final String ANNUAL_TAX_STMT = "Annual tax statement";
    public static final String FINANCIAL_YEAR_START_DATE = "01 Jul ";
    public static final String FINANCIAL_YEAR_END_DATE = "30 Jun ";
    public static final String EFFECTIVE_DATE = "effectiveDate";

    public static final String DEALER_GROUP = "dealerGroup";

    public static final String SHOW_TERMS_CONDITION = "show";

    public static final String HIDE_TERMS_CONDITION = "hide";

    public static final String INDIVIDUAL = "individual";

    //searchDuration Text
    public static final String DEFAULT_DATE_PERIOD = "next30days";
    public static final String TRANSACTION_TYPE = "transactionType";

    public static final String SCHEDULED_TRANSACTIONS = "scheduledTransactions";
    public static final String RECENT_TRANSACTIONS = "recentTransactions";
    public static final String STOP_SCHEDULED_TRANSACTIONS = "stopScheduledTransactions";

    public static final String DOCUMENT_TYPE = "DocumentType";
    public static final String DOCUMENT_TYPES = "DocumentTypes";

    public static final String IS_DEALERGROUP = "isDealerGroup";
    public static final String ACCOUNT_CONFIRMATIONS = "Account confirmations";
    public static final String STATEMENT = "Statement";

    public static final String BSB_CODE = "bsb";

    public static final String FROM_TERM_DATE = "fromTermDate";
    public static final String TO_TERM_DATE = "toTermDate";
    public static final String STATUS = "STATUS";
    public static final String ERR_MESSAGE = "errorMessage";

    public static final String ERROR_CODE_INVALID_PARAMETER = "InvalidParameter";
    public static final String ERROR_CODE_INVALID_REGISTRATION_NUMBER = "PartyNotFound";
    public static final String INVESTOR_TYPE = "investorType";
    public static final String INVESTORS = "investortype";
    public static final String ID_VERIFIED = "idVerified";
    public static final String INDIVIDUAL_INVESTOR = "individualInvestor";

    public static final String ADD = "ADD";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";
    public static final String PAY_MESSAGE = "Payment";
    public static final String IN_PAY_MESSAGE = "Incoming Payment";
    public static final String UNKNOWN = "Unknown";

    public static final String SYSTEM_UNAVAILABLE = "System Unavailable";
    public static final String INVESTOR = "INVESTOR";
    public static final String ADVISER = "ADVISER";

    public static final String MONTH = "month";
    public static final String YEAR = "year";

    public static final String CLIENTAPPLICATION = "clientApplication";

    public static final String ASSETTYPEINTLID = "assetTypeIntlId";

    public static final String SERVICE_TYPE = "serviceType";

    public static final String TXN_RETURNED_MESSAGE = "Returned";
    public static final String TXN_SUCCESS_MESSAGE = "Successful";
    public static final String TXN_FAILURE_MESSAGE = "Failed";

    public static final String INVESTMENT_TRUST_KEY = "investmentTrustDetail";
    public static final String INSURANCE_SEARCH_KEY = "insuranceSearchPolicy";
    public static final String INSURANCE_RETRIEVE_KEY = "insuranceRetrievePolicy";
    public static final String INVESTMENT_ACCOUNT_KEY = "investmentAccountService";
    public static final String INSURANCE_SEARCH_BY_ADVISER_KEY = "insuranceSearchPolicyByAdviser";
    public static final String INSURANCE_SEARCH_BY_CUSTOMER_NUMBER = "insuranceSearchPolicyByCustomerNumber";
    public static final String INSURANCE_RECENT_LIVES_BY_ADVISER = "insuranceRecentLivesInsuredByAdviser";
    public static final String INSURANCE_UNDERWRITING_NOTES = "insuranceUnderwritingNotesByPolicyNumber";


    public static final String ASSET_TYPE = "assetType";
    public static final String ASSET_STATUS = "assetStatus";

    public static final String INSURANCE_FNUMBER = "fnumber";
    public static final String INSURANCE_CUSTOMER_NUMBER = "customer";
    public static final String BROKER_ID = "brokerId";
    public static final String CONSISTENT_ID_FLAG = "consistent";

    public static final String FILTER_FOR_ACCOUNT = "filterForAccount";
public static final String ONBOARDING_STATUS_MODEL = "onBoardingStatusModel";
 public static final String FINANCIAL_YEAR_DATE = "financialYearDate";
    public static final String USE_CACHE = "useCache";
    public static final String TRUE = "true";
    public static final String EXTERNAL = "external";
    public static final String DEFAULT = "default";}
