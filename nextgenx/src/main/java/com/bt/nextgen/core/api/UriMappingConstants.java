package com.bt.nextgen.core.api;

/**
 * Uri mapping strings for the LEGACY module. New apis should go into their own module.
 * 
 * @deprecated Create a UriConfig.properties to specify the uris for your api module.
 */
@Deprecated
public class UriMappingConstants {
    /**
     * URI parameters
     */
    public static final String ACL_ID_URI_MAPPING = "acl-id";
    public static final String ASSET_ID_URI_MAPPING = "asset-id";
    public static final String ASSET_TYPE_URI_MAPPING = "asset-type";
    public static final String CLIENT_ID_URI_MAPPING = "client-id";
    public static final String ADVISER_ID_URI_MAPPING = "position-id";
    public static final String CONTENT_ID_URI_MAPPING = "content-id";
    public static final String INVESTMENT_ID_URI_MAPPING = "investment-id";
    public static final String MODEL_ID_URI_MAPPING = "model-id";
    public static final String OPERATION_ID_URI_MAPPING = "operation-id";
    public static final String JOB_PROFILE_ID_URI_MAPPING = "profile_id";
    @Deprecated
    public static final String PORTFOLIO_ID_URI_MAPPING = "portfolio-id";
    public static final String ACCOUNT_ID_URI_MAPPING = "account-id";
    public static final String CATEGORY_ID_MAPPING = "category-id";
    public static final String POLICY_ID_URI_MAPPING = "policy-id";


    public static final String ROLE_ID_URI_MAPPING = "role-id";
    public static final String ORDER_ID_URI_MAPPING = "order-id";
    public static final String INDUSTRYGROUP_URI_MAPPING = "industry-group";
    public static final String INDUSTRYSUBGROUP_URI_MAPPING = "industry-subgroup";
    public static final String DOCUMENT_ID_URI_MAPPING = "document-id";
    public static final String PRODUCT_ID_URI_MAPPING = "product-id";
    public static final String READ_STATUS_URI_MAPPING = "read-status";
    public static final String USER_TYPE_URI_MAPPING = "user-type";
    public static final String USER_PREF_URI_MAPPING = "pref-id";
    public static final String CORPORATE_ACTION_ID_URI_MAPPING = "ca-id";
    public static final String CORPORATE_ACTION_TITLE_URI_MAPPING = "selected-option-title";
    public static final String CORPORATE_ACTION_SUMMARY_URI_MAPPING = "selected-option-summary";
    public static final String CORPORATE_ACTION_OPTION_URI_MAPPING = "selected-option-id";
    public static final String CORPORATE_ACTION_OPTION_UNITS_MAPPING = "selected-option-units";
    public static final String CORPORATE_ACTION_OPTION_OVERSUBSCRIBE_MAPPING = "selected-option-oversubscribe";
    public static final String CORPORATE_ACTION_OPTION_MINIMUM_PRICE = "selected-option-minimumPrice";
    public static final String MODULE_ID_URI_MAPPING = "module-id";
    public static final String RIP_ID_URI_MAPPING = "rip-id";
    public static final String UPDATE_TYPE_URI_MAPPING = "update-type";
    public static final String TRANSFER_ID_URI_MAPPING = "transfer-id";
    public static final String IPS_ID_URI_MAPPING = "ips-id";

    public static final String ACCOUNT_ID_LIST_URI_MAPPING = "account-id-list";
    public static final String POSITION_ID_URI_MAPPING = "position-id";
    public static final String DRAWDOWN_URI_MAPPING = "drawdown";
    /**
     * Request parameters
     */
    // only add commonly used ones here.
    public static final String EFFECTIVE_DATE_PARAMETER_MAPPING = "effective-date";
    public static final String START_DATE_PARAMETER_MAPPING = "start-date";
    public static final String END_DATE_PARAMETER_MAPPING = "end-date";
    public static final String PERIOD_TYPE_MAPPING = "period";
    public static final String ASSET_CODE = "asset-code";
    public static final String BENCHMARK_ID = "benchmark-id";
    /**
     * Base URI Mappings
     */
    public static final String CURRENT_MODULE = "nextgen/";
    public static final String CURRENT_VERSION_API = "/secure/api/" + ApiVersion.CURRENT_VERSION;
    public static final String CURRENT_MOBILE_VERSION_API = "/secure/api/mobile/" + ApiVersion.CURRENT_MOBILE_VERSION;
    public static final String CURRENT_DIRECT_ONBOARDING_VERSION_API = "/onboard/api/" + ApiVersion.CURRENT_VERSION;
    public static final String CURRENT_VERSION_API_PUBLIC = "/public/api/" + ApiVersion.CURRENT_VERSION;
    public static final String CURRENT_VERSION_CURRENT_MODULE_API_PUBLIC = "/public/api/" + CURRENT_MODULE + ApiVersion.CURRENT_VERSION;
    public static final String NEXTGEN_WEB = "/public/web/" + CURRENT_MODULE;


    public static final String VALIDATION = "/validation";
    public static final String SUBMISSION = "/submission";

    public static final String CONTENT = "/content";
    public static final String CONTENT_BY_ID = CONTENT + "/{" + CONTENT_ID_URI_MAPPING + "}";

    public static final String ROLES = "/roles";
    public static final String ROLE = ROLES + "/{" + ROLE_ID_URI_MAPPING + "}";

    public static final String ADVISERS = "/advisers";
    public static final String ADVISER = ADVISERS + "/{" + ADVISER_ID_URI_MAPPING + "}";

    public static final String CLIENTS = "/clients";
    public static final String CLIENT = CLIENTS + "/{" + CLIENT_ID_URI_MAPPING + "}";
    public static final String CUSTOMER_DATA = CLIENT + "/data";
    public static final String CUSTOMER_DATA_UPDATES = CLIENT + "/gcmUpdate/{" + UPDATE_TYPE_URI_MAPPING + "}";
    public static final String DIRECT_CLIENT_INFO = CLIENTS + "/info";
    public static final String DIRECT_CLIENT_KEY = CLIENTS + "/key";
    public static final String DIRECT_PAN_NUMBER = CLIENTS + "/pannumber";
    public static final String CUSTOMERS = "/customers";
    public static final String GLOBAL_CUSTOMER_RETRIEVE = CUSTOMERS + "/{" + CLIENT_ID_URI_MAPPING + "}";
    public static final String GLOBAL_CUSTOMER_UPDATE = CUSTOMERS + "/update";
    public static final String GLOBAL_CUSTOMER_RETRIEVE_BY_OPERATION = CUSTOMERS + "/{" + CLIENT_ID_URI_MAPPING + "}" + "/data";

    public static final String PORTFOLIOS = CLIENT + "/portfolios";
    public static final String PORTFOLIO = PORTFOLIOS + "/{" + PORTFOLIO_ID_URI_MAPPING + "}";
    public static final String LIST_ASSETS = PORTFOLIO + "/industry-group/{" + INDUSTRYGROUP_URI_MAPPING
            + "}/industry-subgroup/{" + INDUSTRYSUBGROUP_URI_MAPPING + "}/listassets";
    // added for investor details (Different permissions issue for existing
    // CLIENT entry).
    public static final String INVESTORS = "/investors";
    public static final String INVESTOR = INVESTORS + "/{" + CLIENT_ID_URI_MAPPING + "}";
    public static final String INVESTOR_DETAIL = "investorDetail/{" + CLIENT_ID_URI_MAPPING + "}";

    /**
     * Mobile
     */
    public static final String MOBILE_PLATFORM = "platform";
    public static final String MOBILE_CLIENT_VERSION = "version";

    /**
     * Versioning
     */
    public static final String PUBLIC_API = "/public/api";
    public static final String MODULE_VERSION = PUBLIC_API + "/{" + MODULE_ID_URI_MAPPING + "}";
    public static final String NEXTGEN_MODULE_VERSION = PUBLIC_API + "/nextgen/" + ApiVersion.CURRENT_VERSION;
    public static final String MOBILE_VERSION = PUBLIC_API + "/mobile/" + ApiVersion.CURRENT_MOBILE_VERSION;
    public static final String MOBILE_MIN_VERSION = MOBILE_VERSION + "/min-version";

    /**
     * Push notification
     */
    public static final String PUSH_SUBSCRIPTION_UPDATE = "push-notification/subscription/update";

    /**
     * Mod Encryption
     */
    public static final String ACCOUNT_ASSOCIATES = "/account-associates";

    /**
     * Accounts
     */
    public static final String ACCOUNTS = "/accounts";
    public static final String ACCOUNTS_SEARCH = "/accounts/search";
    public static final String ACCOUNT = ACCOUNTS + "/{" + ACCOUNT_ID_URI_MAPPING + "}";
    public static final String ACCOUNT_UPDATE = ACCOUNT + "/update";
    public static final String AVAILABLE_CASH = ACCOUNT + "/available-cash";
    public static final String VALUATION = ACCOUNT + "/valuation";
    public static final String ACCOUNT_PERFORMANCE = ACCOUNT + "/performance";
    public static final String ACCOUNT_PERFORMANCE_CHART = ACCOUNT + "/performance-chart";
    public static final String BENCHMARK_PERFORMANCE = ACCOUNT + "/benchmark-performance";
    public static final String ACCOUNT_STATEMENTS = ACCOUNT + "/statements";
    public static final String STATEMENTS = "/feerevenuestatements";
    public static final String ACCOUNT_MOVEMENTS = ACCOUNT + "/movements";
    public static final String ACCOUNT_BGL_DOWNLOAD = ACCOUNT + "/bgl";
    public static final String ACCOUNT_BALANCE = ACCOUNT + "/balance";

    /**
     * Permission API.
     */
    public static final String PERMISSION = "/permission";
    public static final String ACCOUNT_PERMISSION = ACCOUNT + PERMISSION;

    /**
     * Portfolio
     */
    public static final String PERFORMANCE = ACCOUNT + "/account-performance";
    // public static final String ALLOCATION = PORTFOLIO + "/allocation-sector";
    public static final String ALLOCATION = ACCOUNT + "/allocation-sector";
    public static final String ALLOCATION_EXPOSURE = ACCOUNT + "/allocation-exposure";

    public static final String ALLOCATION_GICS = ACCOUNT + "/allocation-industry";
    public static final String INVESTMENT = PORTFOLIO + "/asset/{" + ASSET_ID_URI_MAPPING + "}/assettype/{"
            + ASSET_TYPE_URI_MAPPING + "}/investment";
    public static final String MANAGED_PORTFOLIO_ASSETS = PORTFOLIO + "/investment-id/{" + INVESTMENT_ID_URI_MAPPING
            + "}/mp-assets";
    public static final String REALISED_CGT = ACCOUNT + "/realised-cgt";
    public static final String UNREALISED_CGT = ACCOUNT + "/unrealised-cgt";
    public static final String REALISED_CGT_BY_SECURITY = ACCOUNT + "/realised-cgtBySecurity";
    public static final String UNREALISED_CGT_BY_SECURITY = ACCOUNT + "/unrealised-cgtBySecurity";
    public static final String BENCHMARKS = "/benchmarks";

    /**
     * Transactions
     */
    public static final String TRANSACTION_HISTORY = ACCOUNT + "/transaction-history";
    public static final String TRANSACTIONS = PORTFOLIO + "/transactions";
    public static final String INCOME = ACCOUNT + "/income";
    public static final String INVESTMENT_TRANSACTIONS = TRANSACTIONS + "/investments/{" + INVESTMENT_ID_URI_MAPPING + "}";
    public static final String TRANSACTION_RECENT_TEN = ACCOUNT + "/recent_10_transaction";
    public static final String TRANSACTION_SCHEDULED = ACCOUNT + "/scheduled_transaction";
    public static final String TRANSACTION_SCHEDULED_SINGLE = ACCOUNT + "/scheduled_transaction/{" + POSITION_ID_URI_MAPPING + "}";

    public static final String STOP_SCHEDULED_TRANSACTION = ACCOUNT + "/stop_payment";
    public static final String PAST_TRANSACTION = ACCOUNT + "/past_transaction";
    public static final String SMSF_MEMBERS = ACCOUNT + "/members";

    /**
     * Orders
     */

    public static final String ORDERS = "/orders";
    public static final String ADVISER_ORDERS = ADVISER + ORDERS;
    public static final String ACCOUNT_ORDERS = ACCOUNT + ORDERS;
    public static final String USER_ORDER_GROUPS = ORDERS + "/order-groups";
    public static final String ORDER = ORDERS + "/{" + ORDER_ID_URI_MAPPING + "}";
    public static final String ORDER_GROUPS = ACCOUNT + "/order-groups";
    public static final String ACCOUNT_ORDER_GROUPS = ACCOUNT + "/order-groups";
    public static final String ORDER_GROUP = ORDER_GROUPS + "/{" + ORDER_ID_URI_MAPPING + "}";
    public static final String ACCOUNT_ORDERS_IN_PROGRESS = ACCOUNT + "/orderinprogress";
    public static final String ADVISER_ORDER_GROUPS = USER_ORDER_GROUPS + CLIENT;

    /**
     * Assets
     */
    public static final String ASSETS = "/assets";
    public static final String ACCOUNT_AVAILABLE_ASSETS = ACCOUNT + "/available-assets";
    public static final String AVAILABLE_ASSETS = "/available-assets";
    public static final String ASSET_TYPE_INTLID = "assetTypeIntlId";
    public static final String ASSET = "/asset";
    public static final String ASSET_DETAILS = ASSET + "/{" + ASSET_TYPE_INTLID + "}/assetdetails";
    public static final String ASSET_TYPES = ASSET + "/types";
    // public static final String ASSET_ACCOUNT = ASSET + "/{" +
    // ACCOUNT_ID_URI_MAPPING + "}";
    public static final String EXTERNAL_ASSETS = ACCOUNT + "/externalAssets";
    public static final String ASSET_PRICE = ASSET + "/{" + ASSET_ID_URI_MAPPING + "}/price";
    public static final String ASSETS_PRICE = "/assets-price";
    public static final String TRADABLE_ASSETS = "/tradableassets";
    public static final String TRADABLE_ASSETS_TYPE = "/tradableassettypes";
    public static final String ACCOUNT_TRADABLE_ASSETS = ACCOUNT + TRADABLE_ASSETS;
    public static final String ACCOUNT_TRADABLE_ASSETS_TYPE = ACCOUNT + TRADABLE_ASSETS_TYPE;

    public static final String ACC_SOFTWARE = ACCOUNT + "/accountingSoftware";
    public static final String ACCOUNTING_SOFTWARE = ACCOUNT + "/accountingSoftware";

    public static final String SAVE_CONTRIBUTION_SPLIT = ACCOUNT + "/deposits/{deposit-id}/categorisation";

    public static final String AVAILABLE_SHARES = "/available-shares";

    /**
     * Feedback
     */
    public static final String FEEDBACK_ID_URI_MAPPING = "feedback-id";
    public static final String FEEDBACKS = CLIENT + "/feedbacks";
    public static final String FEEDBACK = FEEDBACKS + "/{" + FEEDBACK_ID_URI_MAPPING + "}";
    public static final String LINKED_ACCTS = CLIENT + "/linkedAccts";
    public static final String TRACKING = CLIENT + "/tracking";

    /**
     * Product
     */
    public static final String PRODUCTS = "/products";
    public static final String PRODUCT = PRODUCTS + "/{" + PRODUCT_ID_URI_MAPPING + "}";
    public static final String PRODUCT_UPDATE = PRODUCTS + "/update";
    public static final String CLIENT_PRODUCTS = CLIENT + PRODUCTS;
    public static final String CLIENT_LIST_PRODUCTS = CLIENTS + PRODUCTS;
    public static final String ADVISER_PRODUCTS = ADVISER + PRODUCTS;
    public static final String PAYMENTS = ACCOUNT + "/payments";
    public static final String CONFIRM_PAYMENT = PAYMENTS + "/confirmPayment";
    public static final String SUBMIT_PAYMENT = PAYMENTS + "/submitPayment";
    public static final String ADD_PAYEE = PAYMENTS + "/addPayee";
    public static final String DEPOSITS = ACCOUNT + "/deposits";
    public static final String CONFIRM_DEPOSITS = DEPOSITS + "/confirmDeposit";
    public static final String SUBMIT_DEPOSITS = DEPOSITS + "/submitDeposit";
    public static final String CHECK_PAYMENT_LIMIT = PAYMENTS + "/checkPaymentLimit";
    public static final String SUBMIT_PAYMENT_LIMIT = PAYMENTS + "/submitPaymentLimit";
    public static final String SAFI_ANALYZE = ACCOUNT + "/safiAnalyze";
    public static final String SAFI_CHALLENGE = PAYMENTS + "/safiChallenge";
    public static final String GET_BILLER_CODES = PAYMENTS + "/getBillers";
    public static final String DELETE_PAYEE = PAYMENTS + "/deletePayee";
    public static final String UPDATE_PAYEE = PAYMENTS + "/updatePayee";
    public static final String VALIDATE_BSB = PAYMENTS + "/bsbValidate";
    public static final String PRODUCT_DOCS = "/product-documents";

    /**
     * Fees
     */
    public static final String FEES = "/fees";
    public static final String ONE_OFF_ADVICE_FEES = FEES + ACCOUNT + "/advicefees";
    public static final String VALIDATE_ADVICE_FEES = ONE_OFF_ADVICE_FEES + VALIDATION;
    public static final String YEARLY_FEES = ACCOUNT + "/yearlyfees";

    public static final String PERIODIC_FEES = FEES + CLIENT + ACCOUNT + "/periodicfees";
    public static final String PRODUCT_FEE = FEES + ADVISER + PRODUCT + "/productfee";
    public static final String TAX_INVOICE = FEES + ACCOUNT + "/taxinvoice";
    public static final String GENERATE_PDF = TAX_INVOICE + "/generatepdf";
    public static final String DOCUMENTS = "/documents";
    public static final String DOCUMENT = DOCUMENTS + "/{" + DOCUMENT_ID_URI_MAPPING + "}";
    public static final String ACCOUNT_DOCUMENTS = ACCOUNT + DOCUMENTS;
    public static final String DOCUMENTS_DOWNLOAD = DOCUMENTS + "/download";
    public static final String UPDATE_DOCUMENT = DOCUMENT + "/update";
    public static final String UPDATE_AUDIT = DOCUMENT + ACCOUNT + "/updateaudit";
    public static final String DELETE_DOCUMENT = DOCUMENT + "/delete";
    public static final String DOCUMENT_VERSIONS = DOCUMENT + "/versions";
    public static final String CREATE_DOCUMENT = "/upload";
    public static final String DOCUMENT_FILTERS = ACCOUNT + "/filters";

    public static final String FEE_REVENUE_STATEMENT = FEES + CLIENT + ACCOUNT + "/feerevenuestatement";
    public static final String FEE_REVENUE_STATEMENT_DEFAULT_RANGE = FEES + "/defaultrange";
    public static final String FEE_REVENUE_STATEMENT_PDF = FEES + DOCUMENT + "/pdf";
    public static final String FEE_REVENUE_STATEMENT_CSV = FEES + DOCUMENT + "/csv";
    public static final String FEE_SCHEDULE = FEES + ACCOUNT;
    public static final String VALIDATE_FEE_SCHEDULE = FEE_SCHEDULE + VALIDATION;
    public static final String SUBMIT_FEE_SCHEDULE = FEE_SCHEDULE + SUBMISSION;

    public static final String USER_DOCUMENTS = DOCUMENTS + "/userDocuments";
    public static final String USER_DOCUMENT_DOWNLOAD = DOCUMENTS + "/userDocumentDownload" + "/{" + DOCUMENT_ID_URI_MAPPING
            + "}";
    /**
     * Client list and client details
     */

    public static final String CLIENT_FILTER = CLIENTS + "/filter";
    public static final String CLIENT_SEARCH = CLIENTS + "/search";
    public static final String INDIVIDUAL_SEARCH = "/individuals/search";
    public static final String CLIENT_UPDATE_WITH_ACCOUNT = CLIENT + ACCOUNT + "/update";
    public static final String CLIENT_UPDATE = CLIENT + "/update";
    /*
     * public static final String CLIENT_PREF_NAME = CLIENT + "/prefname"; public static final String CLIENT_CONTACT = CLIENT +
     * "/contact"; public static final String CLIENT_ADDRESS = CLIENT + "/address"; public static final String CLIENT_TAX_OPTION =
     * CLIENT + "/taxoption"; public static final String CLIENT_COUNTRY_FOR_TAX = CLIENT + "/taxcountry"; public static final
     * String CLIENT_GST_OPTION = CLIENT + "/gst"; public static final String CLIENT_NAME = CLIENT + "/name"; public static final
     * String CLIENT_REGISTRATION_STATE = CLIENT + "/state";
     */

    // TODO: remove this link use only CLIENT
    public static final String CLIENT_DETAILS = CLIENT + "/clientdetails";
    public static final String UPDATE_CLIENT_DETAILS = CLIENT + "/clientdetails/update";

    // Static data
    public static final String STATIC_DATA = "/static";
    /*
     * public static final String STATIC_DATA_CRITERIA = STATIC_DATA + "/criteria";
     * public static final String STATIC_COUNTRY_LIST = STATIC_DATA + "/countries"; public static final String
     * STATIC_AUSTRALIAN_STATES = STATIC_DATA + "/states"; public static final String STATIC_TAX_OPTIONS = STATIC_DATA +
     * "/taxoptions"; public static final String STATIC_TAX_EXEMPTION_REASONS = STATIC_DATA + "/tax-exemption-reasons";
     */

    /**
     * Dashboard
     */
    public static final String DASHBOARD = "/dashboard";
    public static final String TOTAL_FUA_ACCOUNT = DASHBOARD + "/totalfua";
    public static final String DRAFT_APPLICATION = CLIENT + "/draftapplication";
    public static final String TERM_DEPOSIT = CLIENT + "/termdeposit";
    public static final String KEY_ACTIVITY = CLIENT + "/keyactivity";
    public static final String SAVED_ORDER = "/savedorder";
    public static final String DRAFT_APPLICATIONS = "/draftapplications";
    public static final String ADVISER_TERM_DEPOSIT = "/advisertermdeposit";

    /**
     * Profile Details
     */
    public static final String PROFILE = "/profile";
    public static final String WHATS_NEW = PROFILE + "/whatsnew/{" + READ_STATUS_URI_MAPPING + "}";

    /**
     * Notification
     */
    public static final String NOTIFICATION = "/notification";
    public static final String NOTIFICATION_READ = "/notification/read";
    public static final String NOTIFICATION_UNREAD = "/notification/unread";

    /**
     * Adviser Search
     */
    public static final String ADVISER_SEARCH = "/adviser";
    public static final String ADVISER_BY_ID = ADVISER_SEARCH + "/{" + ADVISER_ID_URI_MAPPING + "}";
    public static final String ADVISER_SINGLE_SEARCH = "/single_adviser_for_user";
    public static final String USER_ADVISER_SEARCH = "/advisers";

    /**
     * Adviser Detail
     */
    public static final String ADVISER_DETAIL = "/adviserdetail";
    public static final String ADVISER_DETAIL_URL = ADVISER_DETAIL + "/{" + CLIENT_ID_URI_MAPPING + "}";

    /**
     * TD Maturities
     */
    public static final String TD_MATURITIES = "/tdmaturities";
    public static final String ADVISER_TD_MATURITIES = ADVISER + TD_MATURITIES;
    public static final String ACCOUNT_TD_MATURITIES = ACCOUNT + TD_MATURITIES;

    /**
     * Term Deposit Calculator
     */

    public static final String TD_CALCULATOR = "/tdcalculator";
    public static final String TD_CALCULATOR_RATES = "/getTermDepositRatesCsv";

    /**
     * Fund Payment
     */
    public static final String FUND_PAYMENT = "/fundpayment";
    /**
     * Global Details
     */
    public static final String GLOBAL = "/global";

    /**
     * Environment Details
     */
    public static final String ENV = "/env";

    /**
     * Adviser dashboard
     */
    public static final String DASHBOARD_PERFORMANCE = "/dashboard/monitor/performance";
    public static final String DASHBOARD_FUA_BY_PORTFOLIO_BAND = "/dashboard/monitor/portfoliovalue";
    public static final String DASHBOARD_TOP_ACCOUNTS = "/dashboard/monitor/topaccounts";
    public static final String DASHBOARD_SUMMARY = "/dashboard/summary";

    /**
     * Onboarding
     */
    public static final String DRAFT_ACCOUNTS = "/draft_accounts";
    public static final String SCHEMA_ENUMS = "/ob_schema/enums";
    public static final String DRAFT_ACCOUNTS_LATEST = "/draft_accounts/latest";
    public static final String HOLDING_APP = "/holding_app";

    /**
     * ANZSIC Codes
     */
    public static final String ANZSIC_CODES = "/anzsiccodes";

    /** Countries. */
    public static final String COUNTRIES = "/countries";
    public static final String COUNTRY_CODE_URI_MAPPING = "countryCode";
    public static final String COUNTRY_BY_CODE = COUNTRIES + "/{" + COUNTRY_CODE_URI_MAPPING + "}";

    /**
     * Investment Options
     */
    public static final String INVESTMENT_OPTIONS = "/investmentoptions";

    /**
     * Logon
     */
    public static final String UPDATE_PASSWORD = "/user/update_password";
    public static final String UPDATE_USERNAME = "/user/update_username";

    /**
     * Onboarding Confirmation
     */
    public static final String ACCOUNT_TYPE = ACCOUNT + "/accountType";

    /**
     * Registration and Approval
     */
    public static final String ACCOUNT_APPLICATION_STATUS = ACCOUNT + "/accountApplicationStatus";
    public static final String ACCOUNT_NON_APPROVER_ACCEPT_TNC = ACCOUNTS + "/nonApprover/acceptTnC";

    /**
     * On boarding & tracking
     */
    public static final String SUBMITTED = CURRENT_VERSION_API + "/submitted";
    public static final String FORMS = "/forms";
    public static final String RESEND_REGISTRATION_CODE = "/resendRegistrationCode";
    public static final String DRAFT = CURRENT_VERSION_API + "/draft";

    /**
     * User Preferences
     */
    public static final String USER_PREFERENCE = "/userpreference";
    public static final String USER_PREFERENCE_GET = USER_PREFERENCE + "/{" + USER_TYPE_URI_MAPPING + "}/{"
            + USER_PREF_URI_MAPPING + "}";
    public static final String USER_PREFERENCE_UPDATE = USER_PREFERENCE + "/update";

    /**
     * CMS URI Mapping
     */
    public static final String CMS = "/cms";

    /**
     * BPAY Billers
     */
    public static final String BILLER_CODE_URI_MAPPING = "biller-id";
    public static final String BPAY_BILLER = "/bpay/{" + BILLER_CODE_URI_MAPPING + "}";

    /**
     * Managed funds
     */
    public static final String ASSET_ID = "as-id";
    public static final String MORNINGSTAR_FUND_PROFILE_URL = "/morningstarfundprofileurl/{" + ASSET_ID + "}";

    // Cash category
    public static final String CASH_CATEGORY = DEPOSITS + "/{" + DOCUMENT_ID_URI_MAPPING + "}" + "/categorisation";
    public static final String CASH_CONTRIBUTION = ACCOUNT + "/categorisedtransactions";
    public static final String MEMBER_CONTRIBUTION_SUMMARY = ACCOUNT + "/membercontributionsummary";
    public static final String FINANCIAL_YEAR = ACCOUNT + "/availablefinancialyears";

    public static final String CONTRIBUTION_CAPS = ACCOUNT + "/contributioncaps";

    public static final String ACCEPT_TERMS_AND_CONDITIONS = PROFILE + "/{" + JOB_PROFILE_ID_URI_MAPPING + "}/tnc";

    public static final String TRANSITION_ACCOUNTS = "/transitionAccounts";



    public static final String TRANSACTION_CATEGORY = "/transaction/categories";
    public static final String ACCOUNT_CATEGORISATION_SUMMARY = ACCOUNT + "/{category-id}/summary";
    public static final String ASSET_TRANSFER_STATUS = ACCOUNT + "/assetTransferStatus";

    /**
     * drawdowns
     */
    public static final String DRAWDOWN = ACCOUNT + "/drawdown";
    public static final String UPDATE_DRAWDOWN = ACCOUNT + "/update/drawdown";
    public static final String INVESTMENT_POLICY_STATEMENTS = "/investmentpolicystatements";
    public static final String GET_INVESTMENT_POLICY_STATEMENT = "/investmentpolicystatement/{" + IPS_ID_URI_MAPPING + "}";
    public static final String SUBSCRIPTIONS = "/subscriptions";
    public static final String ACCOUNT_SUBSCRIPTIONS = ACCOUNT + SUBSCRIPTIONS;
    public static final String SAML_TOKEN_REFRESH = "/saml/refresh";


    /**
     * UAR
     *
     */

    public static final String UAR_ADVISERS = "/advisers";
    public static final String UAR_ADVISER = UAR_ADVISERS + "/{" + ADVISER_ID_URI_MAPPING + "}";
    public static final String UAR_RECORDS = "/uarRecords";
    public static final String UAR = UAR_RECORDS+UAR_ADVISER;
    public static final String SUBMIT_UAR = UAR_RECORDS+SUBMISSION;

    /**
     * Insurance policy
     */
    public static final String POLICIES = "/policies";
    public static final String POLICY = POLICIES + "/{"+ POLICY_ID_URI_MAPPING +"}";
    public static final String POLICY_BENEFITS = ACCOUNT + POLICY + "/benefits";
    public static final String ACCOUNT_POLICY_DETAILS = ACCOUNT + POLICY;
    public static final String ACCOUNT_POLICIES = ACCOUNT + POLICIES;
    public static final String RELATED_ACCOUNT_POLICIES = ACCOUNT + "/relatedaccounts";
    public static final String POLICIES_TRACKING = POLICIES + "/tracking";
    public static final String POLICIES_FNUMBERS = POLICIES + "/fnumbers";
    public static final String POLICY_UNDERWRITING_NOTES = POLICIES + "/applications/{customer-number}/underwritingnotes";

     /* Term Deposit Break
     */
    public static final String ACC_TERM_DEPOSIT = ACCOUNT + "/termdeposit";
    public static final String UPDATE_TERM_DEPOSIT = ACC_TERM_DEPOSIT + "/updateTermDeposit";
    public static final String VALIDATE_TERM_DEPOSIT_BREAK = ACC_TERM_DEPOSIT + "/validateTermDepositBreak";
    public static final String SUBMIT_TERM_DEPOSIT_BREAK = ACC_TERM_DEPOSIT + "/submitTermDepositBreak";

    /**
     * Superannuation
     */
    public static final String SUPER_CONTRIBUTIONS_CAPS = ACCOUNT + "/super/contributioncaps";
    public static final String SUPER_CONTRIBUTIONS_HISTORY = ACCOUNT + "/super/contributionhistory";
    public static final String SUPER_RELATIONSHIP_TYPES = ACCOUNT + "/super/relationshiptypes";
    public static final String SUPER_NOMINATION_TYPES = ACCOUNT + "/super/nominationtypes";

    public static final String SUPER_PENSION_COMMENCEMENT = ACCOUNT + "/super/commence-pension";

    public static final String SUPER_PERSONAL_TAX_DEDUCTION = ACCOUNT +  "/super/personal-tax-deduction-notice";

}
