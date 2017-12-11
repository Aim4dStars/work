package com.bt.nextgen.core.ws;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.account.AccountTemplate;
import com.bt.nextgen.service.avaloq.broker.BrokerEnumTemplate;
import com.bt.nextgen.service.avaloq.client.PersonDetailsEnumTemplate;
import com.bt.nextgen.service.avaloq.collection.CollectionTemplate;
import com.bt.nextgen.service.avaloq.fees.FeesTemplate;
import com.bt.nextgen.service.avaloq.installation.request.AvaloqSystemInformationTemplate;
import com.bt.nextgen.service.avaloq.modelpreferences.ModelPreferenceTemplate;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioTemplate;
import com.bt.nextgen.service.avaloq.rules.RuleTemplate;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceTemplate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositEnumTemplate;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.ws.WebServiceFileImpl.withXpath;
import static com.bt.nextgen.service.avaloq.Template.ACCOUNT_VALUATION;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_DASHBOARD_CURR_FINANCIAL_PERFORMANCE;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_DASHBOARD_CURR_MONTH_PERFORMANCE;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_DASHBOARD_CURR_QUARTER_PERFORMANCE;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_DASHBOARD_CURR_YEAR_PERFORMANCE;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_DASHBOARD_LAST_FINANCIAL_PERFORMANCE;
import static com.bt.nextgen.service.avaloq.Template.ADVISER_TERM_DEPOSIT;
import static com.bt.nextgen.service.avaloq.Template.ASSET_DETAILS;
import static com.bt.nextgen.service.avaloq.Template.ASSET_VALUATION;
import static com.bt.nextgen.service.avaloq.Template.CLIENT_TERM_DEPOSIT;
import static com.bt.nextgen.service.avaloq.Template.CONT_BP_LIST;
import static com.bt.nextgen.service.avaloq.Template.FUND_PAYMENT_NOTICE;
import static com.bt.nextgen.service.avaloq.Template.GET_NOTIFICATION_MESSAGES;
import static com.bt.nextgen.service.avaloq.Template.HOLDINGS;
import static com.bt.nextgen.service.avaloq.Template.HOLDING_BREACH_REPORT;
import static com.bt.nextgen.service.avaloq.Template.INDUSTRY_SECTORS;
import static com.bt.nextgen.service.avaloq.Template.JOB_PROFILE_LIST;
import static com.bt.nextgen.service.avaloq.Template.JOB_PROFILE_LIST_FOR_USER;
import static com.bt.nextgen.service.avaloq.Template.ORDER;
import static com.bt.nextgen.service.avaloq.Template.ORDERS;
import static com.bt.nextgen.service.avaloq.Template.ORDERS_ACCOUNT;
import static com.bt.nextgen.service.avaloq.Template.ORDERS_IN_PROGRESS;
import static com.bt.nextgen.service.avaloq.Template.PAYEE_DETAILS;
import static com.bt.nextgen.service.avaloq.Template.REALISED_CGT_DETAILS;
import static com.bt.nextgen.service.avaloq.Template.SAVED_DEPOSITS;
import static com.bt.nextgen.service.avaloq.Template.STATIC_CODES;
import static com.bt.nextgen.service.avaloq.Template.STATIC_FUNCTIONAL_ROLE;
import static com.bt.nextgen.service.avaloq.Template.SUBACCOUNT_PERFORMANCE_SINCE_INCEPTION_SUMMARY;
import static com.bt.nextgen.service.avaloq.Template.SUBACCOUNT_PERFORMANCE_SUMMARY;
import static com.bt.nextgen.service.avaloq.Template.TD_ASSET_RATES;
import static com.bt.nextgen.service.avaloq.Template.TD_PRODUCT_RATES;
import static com.bt.nextgen.service.avaloq.Template.USER_INFORMATION;
import static com.bt.nextgen.service.avaloq.Template.VALUATION_MOVEMENT;
import static com.bt.nextgen.service.avaloq.asset.AssetEnumTemplate.ASSET_ACCOUNT_HOLDINGS;
import static com.bt.nextgen.service.avaloq.asset.aal.AalEnumTemplate.AAL_INDEX;
import static com.bt.nextgen.service.avaloq.asset.aal.AalEnumTemplate.INDEX_ASSET;

@SuppressWarnings({ "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck", "squid:S138",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.ArrayTrailingCommaCheck", "squid:S00104" })
public final class MockWebServiceResponseFactory {
    private static Map<Profiles, List<MockWebServiceResponse>> defaultResponseFactoryMap = new EnumMap<>(Profiles.class);

    private MockWebServiceResponseFactory() {

    }

    public static void setDefaultResponses() {
        final List<MockWebServiceResponse> defaultResponses = new ArrayList<>(512);
        final List<MockWebServiceResponse> safiDefaultResponses = new ArrayList<>(10);
        // Added for fetching the Avaloq bank Date
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.BANK_DATE.getName() + "')]").respondWith(
                "/BTFG$UI_BASE.ALL#SYSTEM_DET.xml").withName(Template.BANK_DATE.getName()));

        defaultResponses.add(withXpath("//text()[contains(.,'data_valid_req') and contains(.,'648188480')]").withName("data_valid_req1").respondWith(
                "/BTFG$COM.BTFIN.TRXSVC_DATA_MATCHED.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'data_valid_req') and contains(.,'778188488')]").withName("data_valid_req2").respondWith(
                "/BTFG$COM.BTFIN.TRXSVC_DATA_NOTMATCHED.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'async')]").respondWith("/JMS_RESPONSE.xml").withName("JMS"));

        defaultResponses.add(response(PAYEE_DETAILS, "/BTFG$UI_BP.BP#PAY_DET.xml"));
        defaultResponses.add(response(JOB_PROFILE_LIST, "/BTFG$UI_SEC_USER_LIST.MY#JOB_USER.xml"));
        defaultResponses.add(response(JOB_PROFILE_LIST_FOR_USER, "/BTFG$UI_SEC_USER_LIST.LOOKUP#JOB_USER.xml"));
        defaultResponses.add(response(STATIC_FUNCTIONAL_ROLE, "/UI_SEC_USER_LIST.USER_ROLE#FUNCT_ROLE.xml"));
        defaultResponses.add(response(AvaloqSystemInformationTemplate.AVALOQ_INSTALLATION_DETAILS,
                "BTFG$TASK_UI_CHG.ALL#CHG.xml", "All_ABS_CHANGES"));

        String myId = "application001";

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.ACC_ACTIV_STATUS.getName() + "') and not (contains(.,'"
                        + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "')) and contains(.,'" + myId + "') ]").withName(
                Template.ACC_ACTIV_STATUS.getName() + myId).respondWith("/BTFG$UI_DOC_CUSTR_LIST_DOC_Application001.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.ACC_ACTIV_STATUS.getName() + "') and not (contains(.,'"
                        + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "')) and not(contains(.,'" + myId + "')) ]")
                .withName(Template.ACC_ACTIV_STATUS.getName()).respondWith("/BTFG$UI_DOC_CUSTR_LIST_DOC.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.ACC_ACTIV_STATUS_BP.getName() + "') and not (contains(.,'"
                        + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "')) ]").withName(
                Template.ACC_ACTIV_STATUS_BP.getName()).respondWith("/BTFG$UI_DOC_CUSTR_LIST_DOC.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'90000')]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "NO_MATCH").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_NoMatch.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName()
                        + "') and contains(.,'20000') and contains(.,'30000')]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "MATCH1").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_MultipleMatch.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'10000') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "MATCH").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_SingleMatch.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'50000') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "FEEMATCH").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_FeeMatch.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'50001') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "CORPORATE_SMSF").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_CorporateSMSF.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'70001') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "FORMER_NAME").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_FormerName.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'70000') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "COMPANY").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_Company.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'60000') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "INDIVIDUAL_TRUST").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_IndividualTrust_govSuper.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'9757') ]")
                .withName(Template.APPLICATION_DOCUMENT_DETAILS.getName() + "TRUST").respondWith("/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_IndividualTrust_Regulated.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'9758') ]")
                .withName(Template.APPLICATION_DOCUMENT_DETAILS.getName() + "INDV_TRUST").respondWith("/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_IndividualTrustwithCRSData__govSuper.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'120067418') ]")
                .withName(Template.APPLICATION_DOCUMENT_DETAILS.getName()).respondWith("/applicationDocumentBpNr120067418.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'9759') ]")
                .withName(Template.APPLICATION_DOCUMENT_DETAILS.getName() + "INDIVIDUAL").respondWith("/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_Individual_withCRSData.xml"));

        // provision mfa device for investor
        defaultResponses.add(withXpath("//*[local-name()='ProvisionMFAMobileDeviceRequestMsg']").respondWith(
                "/ProvisionMFASuccessResponse.xml").withName("ProvisionMFAMobileDeviceRequestMsg"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS_CUSTOMER.getName()
                        + "') and contains(.,'11111111111') ]")
                .withName(Template.APPLICATION_DOCUMENT_DETAILS_CUSTOMER.getName()).respondWith(
                        "/applicationDocumentsByCisKey.xml"));

        defaultResponses.add(withXpath(
                "//*[contains(.,'" + Template.APPLICATION_DOCUMENT_DETAILS.getName() + "') and contains(.,'12345') ]").withName(
                Template.APPLICATION_DOCUMENT_DETAILS.getName() + "NEW_CORPORATE_SMSF").respondWith(
                "/BTFG$UI_DOC_CUSTR_LIST_BP#DOC_DET_NewCorporateSMSF.xml"));

        // Mapping for user information
        defaultResponses.add(withXpath("//*[contains(.,'" + Template.USER_INFORMATION.getName() + "')  and contains(.,'7210') ]")
                .withName(Template.USER_INFORMATION.getName() + "Emulated").respondWith("/UserInformation_Emulated.xml"));

        defaultResponses.add(withXpath("//*[contains(.,'" + Template.USER_INFORMATION.getName() + "')  and contains(.,'711') ]")
                .withName(Template.USER_INFORMATION.getName() + "ServiceOps").respondWith("/UserInformationServiceOps.xml"));

        // Mapping for user information
        defaultResponses.add(response(USER_INFORMATION, "/UserInformation.xml"));

        // Added for loading new notification messages
        defaultResponses.add(response(GET_NOTIFICATION_MESSAGES, "/BTFG$UI_NTFCN_LIST.NTFCN.xml"));

        // Added the sample file stub response for the fund payment notice
        defaultResponses.add(response(FUND_PAYMENT_NOTICE, "/FundPaymentNoticeSampleResponse.xml"));

        // Added for client term deposit
        defaultResponses.add(response(CLIENT_TERM_DEPOSIT, "/ClientMaturingTDResponse.xml"));

        // Added for Adviser Term Deposits
        defaultResponses.add(response(ADVISER_TERM_DEPOSIT, "/AdviserMaturingTDResponse.xml"));

        defaultResponses.add(response(TD_ASSET_RATES, "/TermDepositAssetRatesResponse.xml"));

        defaultResponses.add(response(TD_PRODUCT_RATES, "/TermDepositProductRatesResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,':fidd_req') and contains(.,'valid')]").withName("fidd_req")
                .respondWith("/ValidateTermDepositBreakResponse.xml").withName(AvaloqOperation.FIDD_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,':fidd_req') and contains(.,'exec')]").withName("fidd_req")
                .respondWith("/SubmitTermDepositBreakResponse.xml").withName(AvaloqOperation.FIDD_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,'fidd_req')]").withName("fidd_req").respondWith("/UpdateTD.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'uar_req')]").withName(AvaloqOperation.UAR_REQ.name()).respondWith(
                "/UARGet.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.UAR_DOC.getName() + "')  ]").withName(
                Template.UAR_DOC.getName()).respondWith("/BTFG$UI_DOC_UAR.DOC.xml"));

        defaultResponses.add(response(CONT_BP_LIST, "/BTFG$UI_CONT_LIST.ALL_USER#CUSTR.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'person_req')]").withName("person_req_online_register").respondWith(
                "/ClientRegisterOnlineUpdate.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'person_req')]").withName("person_req").respondWith(
                "/BTFG$COM.BTFIN.TRXSVC_PERSON_V1.xml"));

        defaultResponses.add(response(STATIC_CODES, "/StaticCodesResponse.xml"));
        defaultResponses.add(response(SUBACCOUNT_PERFORMANCE_SINCE_INCEPTION_SUMMARY,
                "/SubAccountPerformanceSinceInceptionSummaryResponse.xml"));
        defaultResponses.add(response(SUBACCOUNT_PERFORMANCE_SUMMARY, "/SubAccountPerformanceSummaryResponse.xml"));
        defaultResponses.add(response(ADVISER_DASHBOARD_LAST_FINANCIAL_PERFORMANCE, "/AdviserDashboardLastFinYearResponse.xml"));
        defaultResponses.add(response(ADVISER_DASHBOARD_CURR_FINANCIAL_PERFORMANCE, "/AdviserDashboardCurrFinYearResponse.xml"));
        defaultResponses.add(response(ADVISER_DASHBOARD_CURR_MONTH_PERFORMANCE, "/AdviserDashboardCurrMonthResponse.xml"));
        defaultResponses.add(response(ADVISER_DASHBOARD_CURR_QUARTER_PERFORMANCE, "/AdviserDashboardCurrQuarterResponse.xml"));
        defaultResponses.add(response(ADVISER_DASHBOARD_CURR_YEAR_PERFORMANCE, "/AdviserDashboardCurrYearResponse.xml"));
        
        // For Logout Service
        defaultResponses.add(withXpath("//text()[contains(.,'reg_user_req')]").withName("reg_user_req").respondWith(
                "/logout/BTFG$COM.BTFIN.TRXSVC_REG_USER_V1.xml"));
        defaultResponses.add(withXpath("//text()[contains(.,'user_req')]").withName("user_req").respondWith(
                "/BTFG$COM.BTFIN.TRXSVC_USER.xml"));
        defaultResponses.add(response(ACCOUNT_VALUATION, "/ValuationResponse.xml"));
        defaultResponses.add(response(ACCOUNT_VALUATION, "/ValuationResponse.xml"));
        defaultResponses.add(response(REALISED_CGT_DETAILS, "/RealisedCgtDetails.xml"));
        defaultResponses.add(response(ASSET_DETAILS, "/AssetDetailsResponse.xml"));
        defaultResponses.add(response(Template.SPECIALISED_ASSET_DETAILS, "/SpecialAssetDetailsResponse.xml"));
        defaultResponses.add(response(INDUSTRY_SECTORS, "/IndustrySectorListResponse.xml"));
        defaultResponses.add(response(ASSET_VALUATION, "/AssetValuationResponse.xml"));
        defaultResponses.add(response(HOLDINGS, "/HoldingResponse.xml"));
        defaultResponses.add(response(VALUATION_MOVEMENT, "/ValuationMovementResponse.xml"));
        defaultResponses.add(response(ORDERS_ACCOUNT, "/PortfolioOrderLoadSingleResponse.xml"));
        defaultResponses.add(response(ORDERS, "/OrderLoadResponse.xml"));
        defaultResponses.add(response(ORDER, "/OrderLoadSingleResponse.xml"));
        defaultResponses.add(response(ORDERS_IN_PROGRESS, "/OrderInProgressResponse.xml"));
        defaultResponses.add(response(Template.BROKER_PRODUCT_ASSETS, "/BTFG$UI_AAL_LIST.PROD_ASSET#ASSET.xml",
                Template.BROKER_PRODUCT_ASSETS.name()));
        defaultResponses.add(response(AAL_INDEX, "/BTFG$UI_AAL_LIST.AAL#IDX.xml", AAL_INDEX.name()));
        defaultResponses.add(response(INDEX_ASSET, "/BTFG$TASK_IDX.ALL#IDX.xml", INDEX_ASSET.name()));
        defaultResponses.add(response(ASSET_ACCOUNT_HOLDINGS, "/BTFG$UI_POS_LIST.CLT_HLDG.xml", ASSET_ACCOUNT_HOLDINGS.name()));


        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.ACCOUNT.getName() + "')] and //text()[contains(.,'77175')]").withName(
                Template.ACCOUNT.getName()).respondWith("/Account_SMSF_Corporate_Blocked.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.ACCOUNT.getName() + "')] and //text()[not(contains(.,'77175'))]").withName(
                Template.ACCOUNT.getName()).respondWith("/Account_SMSF_Corporate.xml"));

        defaultResponses.add(withXpath(
                    "//text()[contains(.,'" + Template.ACCOUNTS_SEARCH.getName() + "')] and //text()[contains(.,'%120025531%')]").withName(
                Template.ACCOUNTS_SEARCH.getName()).respondWith("/Accounts_Search_Response.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName()
                        + "') and (contains(., '2584') or contains(., '4181') or contains(., '45278') or contains(., '1597'))]")
                .withName(Template.CLIENT_DETAILS.getName() + "Individual").respondWith("/Individual.xml"));

        defaultResponses.add(withXpath("//*[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(.,'54321')]")
                .withName("CLIENT_DETAILS_54321").respondWith("/Individual_54321.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(., '93887')]").withName(
                Template.CLIENT_DETAILS.getName() + "ExistingClient").respondWith("/ExistingClientSearch.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains (., '55829')]").withName(
                Template.CLIENT_DETAILS.getName() + "IntermediaryNotOnboarded").respondWith(
                "/ClientDetailsIntermediaryNotOnboarded.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains (., '55830')]").withName(
                Template.CLIENT_DETAILS.getName() + "IntermediaryNotRegistered").respondWith(
                "/ClientDetailsIntermediaryNotRegistered.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(., '73328')]").withName(
                Template.CLIENT_DETAILS.getName() + "IntermediaryNotRegistered").respondWith("/ClientDetailsNotOnboarded.xml"));
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + PersonDetailsEnumTemplate.PERSON_DET.getTemplateName()
                        + "') and contains(., '157365')]").withName(
                PersonDetailsEnumTemplate.PERSON_DET.getTemplateName() + "Investors").respondWith(
                "/PersonDetailsInvestorResp.xml"));
        defaultResponses
                .add(withXpath(
                        "//text()[contains(.,'" + PersonDetailsEnumTemplate.PERSON_DET.getTemplateName()
                                + "') and contains(., '32815')]").withName(
                        PersonDetailsEnumTemplate.PERSON_DET.getTemplateName() + "Adviser").respondWith(
                        "/PersonDetailsAdviserResp.xml"));
        defaultResponses.add(withXpath("//text()[contains(.,'" + PersonDetailsEnumTemplate.PERSON_DET.getTemplateName() + "') and contains(., '32561')]")
                .withName(PersonDetailsEnumTemplate.PERSON_DET.getTemplateName() + "Investor").respondWith(
                        "/BTFG$UI_PERSON.PERSON#PERSON_DET_GetClientDetailsByGcmId2.xml"));
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(., '201617515')]").withName(
                Template.CLIENT_DETAILS.getName()).respondWith("/ClientDetails.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(., '32561')]").withName(
                Template.CLIENT_DETAILS.getName() + "Investor1").respondWith("/ClientDetails.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "') and contains(., '32562')]").withName(
                Template.CLIENT_DETAILS.getName() + "Investor2").respondWith("/BTFG$UI_PERSON.PERSON#PERSON_DET_GetClientDetailsByGcmId.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CLIENT_DETAILS.getName() + "')]").withName(
                Template.CLIENT_DETAILS.getName()).respondWith("/BTFG$UI_PERSON.PERSON#PERSON_DET_GetClientDetailsByGcmId.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.AVAILABLE_CASH.getName() + "')]").withName(
                Template.AVAILABLE_CASH.getName()).respondWith("/AvailableCashResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CASH_RATE_HISTORY.getName() + "')]").withName(
                Template.CASH_RATE_HISTORY.getName()).respondWith("/BTFG$UI_INTR_RATE.ASSET#HIST.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ORDER_GROUP.getName() + "')]").withName(
                Template.ORDER_GROUP.getName()).respondWith("/OrderGroupLoadResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.BACKGROUND_PROCESS.getName() + "')]").withName(
                Template.BACKGROUND_PROCESS.getName()).respondWith("/TASK_OBJ_BGP.ALL#BGP.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ADVISER_ORDER_GROUP.getName() + "')]").withName(
                Template.ADVISER_ORDER_GROUP.getName()).respondWith("/OrderGroupLoadResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_SUMMARY.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_SUMMARY.getName()).respondWith("/ModelPortfolioLoadResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,':mp_cton_req') and contains(.,'exec')]").respondWith(
                "/ModelPortfolioUploadResponse.xml").withName(AvaloqOperation.MP_CTON_REQ.name() + "ExecReq"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_HEADER_BULK.getName() + "')]")
                .withName(Template.MODEL_PORTFOLIOS_HEADER_BULK.getName()).respondWith("/ModelHeaderBulkLoadResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_HEADER_SINGLE.getName() + "')]")
                .withName(Template.MODEL_PORTFOLIOS_HEADER_SINGLE.getName()).respondWith("/ModelHeaderSingleLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_CASH_FORECASTING_BULK.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_CASH_FORECASTING_BULK.getName()).respondWith("/CashForecastBulkLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_CASH_FORECASTING_SINGLE.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_CASH_FORECASTING_SINGLE.getName()).respondWith("/CashForecastSingleLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_BULK.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_BULK.getName()).respondWith("/ShadowPortfolioBulkLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_SINGLE.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_SHADOW_PORTFOLIO_SINGLE.getName())
                .respondWith("/ShadowPortfolioSingleLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + ModelPreferenceTemplate.ACCOUNT_PREFERENCES.getTemplateName() + "')]").withName(
                ModelPreferenceTemplate.ACCOUNT_PREFERENCES.getTemplateName()).respondWith("/ModelPreferencesLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + ModelPreferenceTemplate.SUBACCOUNT_PREFERENCES.getTemplateName() + "')]").withName(
                ModelPreferenceTemplate.SUBACCOUNT_PREFERENCES.getTemplateName())
                .respondWith("/ModelPreferencesLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_BULK.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_BULK.getName()).respondWith(
                "/ShadowTransactionBulkLoadResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_SINGLE.getName() + "')]").withName(
                Template.MODEL_PORTFOLIOS_SHADOW_TRANSACTIONS_SINGLE.getName()).respondWith(
                "/ShadowTransactionSingleLoadResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CASH_DIVIDEND.getName() + "')]").withName(
                Template.CASH_DIVIDEND.getName()).respondWith("/CashDividendResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.INCOME_RECEIVED.getName() + "')]").withName(
                Template.INCOME_RECEIVED.getName()).respondWith("/IncomeReceivedResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.TRANSACTION_HISTORY.getName() + "')]").withName(
                Template.TRANSACTION_HISTORY.getName()).respondWith("/TransactionHistoryResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.TRANSACTIONS.getName() + "')]").withName(
                Template.TRANSACTIONS.getName()).respondWith("/TransactionResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.PORTFOLIOVALUE_BY_BAND.getName() + "')]").withName(
                Template.PORTFOLIOVALUE_BY_BAND.getName()).respondWith("/AdviserDashboardPortfolioValueByBand.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.RECENT_10_TRANSACTION.getName() + "')  ]").withName(
                Template.RECENT_10_TRANSACTION.getName()).respondWith("/BTFG$UI_BOOK_LIST_POS_EVT.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SCHEDULED_TRANSACTION_LIST.getName() + "')  ]")
                .withName(Template.SCHEDULED_TRANSACTION_LIST.getName()).respondWith("/BTFG$UI_SCD_TRX_LIST.BP#SCD_TRX_DET.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.REALISED_CGT.getName() + "')]").withName(
                Template.REALISED_CGT.getName()).respondWith("/RealisedCgtResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.UNREALISED_CGT.getName() + "')]").withName(
                Template.UNREALISED_CGT.getName()).respondWith("/UnrealisedCgtResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.PORTFOLIO_PERFORMANCE.getName() + "')]").withName(
                Template.PORTFOLIO_PERFORMANCE.getName()).respondWith("/PortfolioPerformanceResponse.xml"));

        defaultResponses.add(withXpath("//*[contains(.,'" + PortfolioTemplate.CASH_MOVEMENT.getTemplateName() + "')]")
                .respondWith("/CashMovementsResponse.xml").withName(PortfolioTemplate.CASH_MOVEMENT.getTemplateName()));

        defaultResponses.add(withXpath("//*[contains(.,'" + FeesTemplate.RCTI_REQUEST.getTemplateName() + "')]")
                .respondWith("/RecipientCreatedTaxInvoicesResponse.xml").withName(FeesTemplate.RCTI_REQUEST.getTemplateName()));
        
        // Cash Contributions
        defaultResponses
                .add(withXpath(
                        "//text()[contains(.,'"
                                + Template.CASH_CONTRIBUTIONS_FOR_BP.getName()
                                + "')] and //text()[contains(., 'bp_list_id')] and //text()[contains(., 'fy_date')] and //text()[contains(., 'cash_cat_type_id')]")
                        .withName("CategorisationSummary").respondWith("/UI_CASH_CAT.BP#DOC.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.CASH_CONTRIBUTIONS_FOR_BP.getName()
                        + "')] and //text()[contains(., 'bp_list_id')] and //text()[contains(., 'fy_date')]").withName(
                "CashContributionSummary").respondWith("/BTFG$UI_BOOK_LIST.DOC#CASH_CAT-SUMMARY.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CASH_CONTRIBUTIONS_FOR_DOC.getName() + "')]").withName(
                Template.CASH_CONTRIBUTIONS_FOR_DOC.getName()).respondWith("/BTFG$UI_BOOK_LIST.DOC#CASH_CAT.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CONTRIBUTIONS_CAPS.getName() + "')  ]").withName(
                Template.CONTRIBUTIONS_CAPS.getName()).respondWith("/BTFG$UI_CASH_CAT_BP_CAPS.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.UNREALISED_CGT_DETAILS.getName() + "')]").withName(
                Template.UNREALISED_CGT_DETAILS.getName()).respondWith("/UnrealisedCgtDetails.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.BROKER_HIERARCHY.getName() + "')  ]").withName(
                Template.BROKER_HIERARCHY.getName()).respondWith("/BrokerHierarchy.xml"));
        
        defaultResponses.add(withXpath("//text()[contains(.,'" + BrokerEnumTemplate.PAGINATED_BROKER_HIERARCHY.getTemplateName() + "')  ]").withName(
        		"BTFG$UI_OE_STRUCT.OE_PERSON#HIRA").respondWith("/btfg$ui_oe_struct_oe_person_hira.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + BrokerEnumTemplate.PAGINATED_BROKER_HIERARCHY_SYNC.getTemplateName() + "')  ]").withName(
                "BTFG$UI_OE_STRUCT.OE_PERSON#HIRA").respondWith("/btfg$ui_oe_struct_oe_person_hira.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + TermDepositEnumTemplate.TD_ASSET_RATES.getTemplateName() + "')  ]").withName(
                TermDepositEnumTemplate.TD_ASSET_RATES.getTemplateName()).respondWith("/AssetRateResponse.xml"));
        defaultResponses.add(withXpath("//text()[contains(.,'" + TermDepositEnumTemplate.TD_PRODUCT_RATES.getTemplateName() + "')  ]").withName(
                TermDepositEnumTemplate.TD_PRODUCT_RATES.getTemplateName()).respondWith("/ProductRateResponse.xml"));
        
        defaultResponses.add(withXpath("//text()[contains(.,'" + BrokerEnumTemplate.PAGINATED_JOB_HIERARCHY.getTemplateName() + "')  ]").withName(
        		"BTFG$TASK_PERSON_LIST.JOB").respondWith("/btfg$task_person_list_job.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + BrokerEnumTemplate.PAGINATED_JOB_HIERARCHY_SYNC.getTemplateName() + "')  ]").withName(
                "BTFG$TASK_PERSON_LIST.JOB").respondWith("/btfg$task_person_list_job.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CLIENT_LIST.getName() + "')  ]").withName(
                Template.CLIENT_LIST.getName()).respondWith("/BTFG$UI_CUSTR_LIST.BP_PERSON_CONT.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_LIST.getName() + "')  ]").withName(
                Template.ACCOUNT_LIST.getName()).respondWith("/BP_LIST.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.ACCOUNT_LIST_GCM.getName() + "') and  //text()[contains(.,'bp_nr')]  ]")
                .withName("ACCOUNT_LIST_BY_BP_NR").respondWith("/UI_BP_LIST_BY_BP_NR.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.ACCOUNT_LIST_GCM.getName() + "') and  //text()[contains(.,'gcm_id')] ]")
                .withName("ACCOUNT_LIST_BY_GCM_ID").respondWith("/UI_BP_LIST_GCM.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_BALANCE_LIST.getName() + "')  ]").withName(
                Template.ACCOUNT_BALANCE_LIST.getName()).respondWith("/BTFG$UI_POS_LIST.ALL_BP#BP_BAL.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_BALANCE.getName() + "')  ]").withName(
                Template.ACCOUNT_BALANCE.getName()).respondWith("/BTFG$UI_POS_LIST.BP#BP_BAL.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.PERSON_LIST.getName() + "')  ]").withName(
                Template.PERSON_LIST.getName()).respondWith("/PERSON_LIST_FOR_INDIVIDUAL.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,':pay_req') and contains(.,'valid') and contains(.,'pay_anyone_benef')]").respondWith(
                "/ConfirmPaymentResponseAvaloq.xml").withName(AvaloqOperation.PAY_REQ.name()));

        defaultResponses.add(withXpath(
                "//text()[contains(.,':pay_req') and contains(.,'exec') and contains(.,'pay_anyone_benef')]").respondWith(
                "/PayAnyonePaymentResponseAvaloq.xml").withName(
                AvaloqOperation.PAY_REQ.name() + "ReqExec" + PayeeType.PAY_ANYONE.name()));

        defaultResponses.add(withXpath("//text()[contains(.,':pay_req') and contains(.,'valid') and contains(.,'bpay_biller')]")
                .respondWith("/ConfirmBpayPaymentResponseAvaloq.xml").withName(
                        AvaloqOperation.PAY_REQ.name() + "ReqValid" + PayeeType.BPAY.name()));

        defaultResponses.add(withXpath("//text()[contains(.,':pay_req') and contains(.,'exec') and contains(.,'bpay_biller')]")
                .respondWith("/BpayPaymentResponseAvaloq.xml").withName(
                        AvaloqOperation.PAY_REQ.name() + "ReqExec" + PayeeType.BPAY.name()));

        defaultResponses.add(withXpath("//text()[contains(.,':stex_req') and contains(.,'valid')]").respondWith(
                "/OrderCancelStexResponse.xml").withName(AvaloqOperation.STEX_REQ.name() + "ValidReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'valid') and contains(.,'MB0001') ]")
                .respondWith("/OrderGroupResponseErr.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "ValidReqErr"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'valid')]").respondWith(
                "/OrderGroupValidateResponse.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "ValidReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'exec')]").respondWith(
                "/OrderGroupSubmitResponse.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "ExecReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'delete')]").respondWith(
                "/OrderGroupResponse.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "DeleteReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'get')]").respondWith(
                "/OrderGroupTrxLoadResponse.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "LoadReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':trx_bdl_req') and contains(.,'save')]").respondWith(
                "/OrderGroupResponse.xml").withName(AvaloqOperation.TRX_BDL_REQ.name() + "SaveReq"));

        defaultResponses.add(withXpath("//text()[contains(.,':cancel_doc_req')]").respondWith("/DeleteOrderResponse.xml")
                .withName(AvaloqOperation.CANCEL_DOC_REQ.name() + "DeleteReq"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'CONT_REQ')] and  (//text()[contains(.,'aw')] or  "
                        + "//text()[contains(.,'rcvd')] or  //text()[contains(.,'manual')] or  "
                        + "//text()[contains(.,'stop')])").respondWith("/AccountingSoftwareStatusUpdateResponse.xml").withName(
                "AccountingSoftwareStatusUpdateResponse"));

        defaultResponses.add(withXpath("//text()[contains(.,':cont_req')]").respondWith("/ChangeContResponse.xml").withName(
                AvaloqOperation.CONT_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,':reg_acc_veri_req')]").respondWith("/RegAccVerificationResponse.xml").withName(
                AvaloqOperation.REG_ACC_VERI_REQ.name()));

        // Stop Transaction
        defaultResponses.add(withXpath("//text()[contains(.,':pay_req') and contains(.,'exec') and contains(.,'cancel')]")
                .respondWith("/PayAnyonePaymentResponseAvaloq.xml").withName(
                        AvaloqOperation.PAY_REQ.name() + "ReqCancel" + PayeeType.PAY_ANYONE.name()));

        defaultResponses.add(withXpath("//text()[contains(.,'BTFG$UI_BOOK_LIST.BP#CASH_TRX')]").respondWith(
                "/PastTransactions#CashTrx.xml").withName("PastTransactions"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.PRODUCTS.getName() + "')  ]").withName(
                Template.PRODUCTS.getName()).respondWith("/BTFG$UI_PROD_LIST.ALL#PROD_DET.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ADVISOR_PRODUCTS.getName() + "')  ]").withName(
                Template.ADVISOR_PRODUCTS.getName()).respondWith("/BTFG$UI_APL_LIST.ALL#PROD.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_PERFORMANCE_SUMMARY_PRD.getName() + "')  ]")
                .withName(Template.ACCOUNT_PERFORMANCE_SUMMARY_PRD.getName()).respondWith("/PerformanceSummaryPdtResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_PERFORMANCE_QUARTERLY.getName() + "')  ]")
                .withName(Template.ACCOUNT_PERFORMANCE_QUARTERLY.getName()).respondWith("/PerformanceResponse.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.ACCOUNT_PERFORMANCE_SUMMARY_SINCE_INCEPTION.getName() + "')  ]").withName(
                Template.ACCOUNT_PERFORMANCE_SUMMARY_SINCE_INCEPTION.getName()).respondWith(
                "/PerformanceSummarySinceInceptionResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_PERFORMANCE_SUMMARY_LIST.getName() + "')  ]")
                .withName(Template.ACCOUNT_PERFORMANCE_SUMMARY_LIST.getName()).respondWith("/PerformanceSummaryResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.ACCOUNT_PERFORMANCE_OVERALL.getName() + "')]")
                .withName(Template.ACCOUNT_PERFORMANCE_OVERALL.getName()).respondWith("/AccountPerformanceOverallResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.TOP_ACCOUNTS_CASH.getName() + "')]").withName(
                Template.TOP_ACCOUNTS_CASH.getName()).respondWith("/DashboardTopBpAccountsByCash.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.TOP_ACCOUNTS_PORTFOLIO.getName() + "')]").withName(
                Template.TOP_ACCOUNTS_PORTFOLIO.getName()).respondWith("/DashboardTopBpAccountsByPortfolio.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.AVAILABLE_BENCHMARKS.getName() + "')]").withName(
                Template.AVAILABLE_BENCHMARKS.getName()).respondWith("/AvailableBenchmarksResponse.xml"));

        // Valid and Exec for Deposits

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveChannelAccessCredentialRequest' and namespace-uri()='http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/']")
                        .respondWith("/CredentialResponseFromEam.xml").withName("RetrieveChannelAccessCredentialRequest"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name()='retrieveChannelAccessCredentialRequest' and namespace-uri()='http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v5/SVC0311/']")
                        .respondWith("/RetrievelCredentialResponse_WithPPID.xml").withName(
                                "RetrieveChannelAccessCredentialRequestV5"));

        // Response for GCM retrival and update
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest'and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678901']")
                        .respondWith("/gesb/retrieve-customerdetails/12345678901.xml").withName(
                                "retrieve-customerdetails-12345678901"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678902']")
                        .respondWith("/gesb/retrieve-customerdetails/12345678902.xml").withName(
                                "retrieve-customerdetails-12345678902"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678903']")
                        .respondWith("/gesb/retrieve-customerdetails/12345678903.xml").withName(
                                "retrieve-customerdetails-12345678903"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '11223344556']")
                        .respondWith("/gesb/retrieve-customerdetails/11223344556.xml").withName(
                                "retrieve-customerdetails-11223344556"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest'and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '98026671784']")
                        .respondWith("/gesb/retrieve-customerdetails/98026671784.xml").withName(
                                "retrieve-customerdetails-98026671784"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678701']")
                        .respondWith("/gesb/retrieve-customerdetails/12345678701.xml").withName(
                                "retrieve-customerdetails-12345678701"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '11111111111']")
                        .respondWith("/gesb/retrieve-customerdetails/11111111111.xml").withName(
                                "retrieve-customerdetails-11111111111"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v7/SVC0258/']")
                        .respondWith("/Individual_ResponseALL_Arrangement_FTC.xml").withName(
                                "RetrieveDetailsAndArrangementRelationshipsForIPsRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'notifyEventForFraudAssessmentRequest']").respondWith(
                "/NotifyEventForFraudAssessment_Response.xml").withName("NotifyEventForFraudAssessmentResponse"));

        // svc0610 - retrieve TFN - Response from GCM when TFN exists (use CisKey: 12345678903 (V11) which has associated bank accounts)
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveIPTaxRegistrationRequest' and namespace-uri() ='http://www.westpac.com.au/gn/arrangementReporting/services/arrangementReporting/xsd/retrieveIPTaxRegistration/v2/SVC0610/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678903']")
                        .respondWith("/gesb/retrieve-tfn/RetrieveIPTaxRegistrationResponse_success.xml").withName(
                                "retrieve-tfn-12345678903")); // 12345678903 = /gesb.retrieve-customerdetailsV11/individualResponse_12345678903.xml

        // svc0610 - retrieve TFN - Response from GCM when no TFN data exists for entered CIS key
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveIPTaxRegistrationRequest' and namespace-uri() ='http://www.westpac.com.au/gn/arrangementReporting/services/arrangementReporting/xsd/retrieveIPTaxRegistration/v2/SVC0610/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '66786610081']")
                        .respondWith("/gesb/retrieve-tfn/RetrieveIPTaxRegistrationResponse_error.xml").withName(
                                "retrieve-tfn-66786610081")); // /gesb.retrieve-customerdetailsV11/Individual_Response_66786610081_V11.xml

        // gesb.retrieve-customerdetails V10 - sample Response for version 10
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678902']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/12345678902_v10.xml").withName(
                                "retrieve-customerdetails-12345678902-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678901']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/12345678901_v10.xml").withName(
                                "retrieve-customerdetails-12345678901-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678903']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/12345678903_v10.xml").withName(
                                "retrieve-customerdetails-12345678903-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '11223344556']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/11223344556_v10.xml").withName(
                                "retrieve-customerdetails-11223344556-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '98026671784']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/98026671784_v10.xml").withName(
                                "retrieve-customerdetails-98026671784-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678701']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/12345678701_v10.xml").withName(
                                "retrieve-customerdetails-12345678701-v10"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '11111111111']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/IP_notFound.xml").withName(
                                "retrieve-customerdetails-IP_notFound"));

        defaultResponses
				.add(withXpath(
		                "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '00000000000']")
		                .respondWith("/gesb.retrieve-customerdetailsV10/IP_notFound.xml")
		                .withName("retrieveCustomerDetails-version10"));
		        
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v10/SVC0258/']")
                        .respondWith("/gesb.retrieve-customerdetailsV10/Individual_ResponseALL_Arrangement_FTC_V10.xml")
                        .withName("retrieveCustomerDetails-version10"));

        // gesb.retrieve-customerdetailsV11 - sample Response for version 11

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '87458125478']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/Organisation_Response_87458125478_V11.xml")
                        .withName("retrieveCustomerDetails-organisation-87458125478-version11"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '66786610081']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/Individual_Response_66786610081_V11.xml")
                        .withName("retrieveCustomerDetails-individual-66786610081-version11"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '65826520018']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/Individual_Response_65826520018_V11.xml")
                        .withName("retrieveCustomerDetails-individual-65826520018-version11"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '1234567892']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_1234567892.xml")
                        .withName("retrieveCustomerDetails-Individual-1234567892-version11"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678910']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_1234567892.xml")
                        .withName("retrieveCustomerDetails-Individual-12345678910-version11"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678902']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_12345678902.xml")
                        .withName("retrieveCustomerDetails-Individual-12345678902-version11"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678903']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_12345678903.xml")
                        .withName("retrieveCustomerDetails-Individual-12345678903-version11"));//use this CISKey in TFN retrieval too
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678904']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_12345678904.xml")
                        .withName("retrieveCustomerDetails-Individual-12345678904-version11"));
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/organisationInvolvedPartyRole/roleType[text() = 'Organisation']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/Organisation_Response_V11.xml")
                        .withName("retrieveCustomerDetails-Organisation-version11"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/individualInvolvedPartyRole/roleType[text() = 'Individual']")
                        .respondWith("/gesb.retrieve-customerdetailsV11/Individual_Response_V11.xml")
                        .withName("retrieveCustomerDetails-Individual-version11"));
        
        //Added for silo Movement
        defaultResponses
        .add(withXpath(
                "//*[local-name() = 'retrieveDetailsAndArrangementRelationshipsForIPsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/retrieveDetailsAndArrangementRelationshipsForIPs/v11/SVC0258/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '12345678905']")
                .respondWith("/gesb.retrieve-customerdetailsV11/individualResponse_12345678905.xml")
                .withName("retrieveCustomerDetails-Individual-12345678905-version11"));

        defaultResponses.add(withXpath("//*[local-name() = 'maintainIPContactMethodsRequest']").respondWith(
                "/SVC0418v1_Modify_Response_FTC.xml").withName("MaintainIPContactMethodsRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'modifyIndividualCustomerRequest']").respondWith(
                "/PreferredNameUpdateResponseFromEam.xml").withName("ModifyIndividualCustomerRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'modifyIndividualIPRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/modifyIndividualIP/v5/SVC0338/']/individual/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '47658592']")
                .respondWith("/TaxResidenceCountryIndividualUpdate.xml").withName("modifyIndividualIPRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'modifyIndividualIPRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/modifyIndividualIP/v5/SVC0338/']")
                .respondWith("/TaxResidenceCountryIndividualUpdate.xml").withName("modifyIndividualIPRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'modifyOrganisationIPRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/modifyOrganisationIP/v5/SVC0339/']/organisation/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '47658591']")
                .respondWith("/TaxResidenceCountryOrganisationUpdate.xml").withName("modifyOrganisationIPRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'modifyOrganisationCustomerRequest']").respondWith(
                "/Organisation_State_Response_Updates.xml").withName("ModifyOrganisationCustomerRequest"));

        // defaultResponses.add(withXpath("//*[local-name()='ValidatePartySMSOneTimePasswordChallengeRequestMsg']").respondWith("/ValidateRegistrationCredentialResponse_Success.xml")
        // .withName("ValidatePartySMSOneTimePasswordChallengeRequest"));

        // create account adviser/intermediaries
        defaultResponses.add(withXpath("//*[local-name()='ProcessAdvisersRequestMsg']").respondWith(
                "/ProcessAdviserResponseMsg.xml").withName("ProcessAdvisersRequestMsg"));

        // create account investor
        defaultResponses.add(withXpath("//*[local-name()='ProvisionOnlineAccessAndNotifyRequestMsg']").respondWith(
                "/ProvisionOnlineAccessAndNotifyResponseMsg.xml").withName("ProvisionOnlineAccessAndNotifyRequestMsg"));

        // deviceArrangementService
        defaultResponses.add(withXpath("//*[local-name()='retrieveMFADeviceArrangementsRequest']").respondWith(
                "/DeviceArrangementServiceResponse.xml").withName("retrieveMFADeviceArrangementsRequest"));

        // Update Mobile Number
        defaultResponses.add(withXpath("//*[local-name()='maintainMFADeviceArrangementRequest']").respondWith(
                "/UpdateDeviceArrangementResponse.xml").withName("maintainMFADeviceArrangementRequest"));

        // ModifyChannelAccessCredentialService to block and unblock user access
        defaultResponses
                .add(withXpath(
                        "//*[local-name()='modifyChannelAccessCredentialRequest' and namespace-uri()='http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/modifyChannelAccessCredential/v6/SVC0310/']")
                        .respondWith("/ModifyChannelAccessCredentialResponseV6.xml").withName(
                                "ModifyChannelAccessCredentialRequestv6"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name()='modifyChannelAccessCredentialRequest' and namespace-uri()='http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/modifyChannelAccessCredential/v5/SVC0310/']")
                        .respondWith("/ModifyChannelAccessCredentialResponse.xml").withName(
                                "ModifyChannelAccessCredentialRequestv5"));
        // ResetUserPasswordService
        defaultResponses.add(withXpath(
                "//*[local-name()='maintainChannelAccessServicePasswordRequest'] and //text()[contains(.,'VOICE')]").respondWith(
                "/ResetPasswordServiceResponse.xml").withName("MaintainChannelAccessServicePasswordRequest1"));

        defaultResponses.add(withXpath("//*[local-name() = 'analyze']").respondWith("/AnalyzeResponseFromSafi.xml").withName(
                "AnalyzeResponseFromSafi"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'challenge']/*[local-name() = 'request']/*[local-name() = 'identificationData']/*[local-name()='userName' and (not(text()) or text() != '20161983')]")
                        .respondWith("/ChallengeResponseFromSafi.xml").withName("ChallengeResponseFromSafi"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'challenge']/*[local-name() = 'request']/*[local-name() = 'identificationData']/*[local-name()='userName' and text() = '20161983']")
                        .respondWith("/ChallengeResponseInvalidStatus.xml").withName("ChallengeResponseWithErrorFromSafi"));

        defaultResponses.add(withXpath("//*[local-name() = 'authenticate'] and //text()[contains(.,'111111')]").respondWith(
                "/AuthenticateResponseFromSafi.xml").withName("AuthenticateResponseFromSafi"));

        defaultResponses.add(withXpath("//*[local-name() = 'authenticate']  and //text()[not(contains(.,'111111'))]")
                .respondWith("/AuthenticateResponseFromSafiError.xml").withName("AuthenticateResponseFromSafiError"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'maintainChannelAccessServicePasswordRequest'] and //text()[contains(.,'Invalid_Password_1')]")
                        .respondWith("/PasswordFailedResponse.xml").withName("MaintainChannelAccessServicePasswordRequest2"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'maintainChannelAccessServicePasswordRequest'] and //text()[not(contains(.,'Invalid_Password_1'))]")
                        .respondWith("/PasswordResponse.xml").withName("MaintainChannelAccessServicePasswordRequest3"));

        defaultResponses.add(withXpath(
                "//*[local-name() = 'ParseAustralianAddressRequestMsg'] and //text()[contains(.,'NOWHERE')]").respondWith(
                "/ParseAustralianAddressFailureResponseMsg.xml").withName("ParseAustralianAddressFailureResponseMsg"));

        defaultResponses.add(withXpath("//*[local-name() = 'ParseAustralianAddressRequestMsg']").respondWith(
                "/ParseAustralianAddressSuccessResponseMsg.xml").withName("ParseAustralianAddressSuccessResponseMsg"));

        // Valid and Exec for Recurring Deposits
        defaultResponses.add(withXpath(
                "//text()[contains(.,'inpay_req') and  contains(.,'contr_period') and contains(.,'valid')]").respondWith(
                "/DepositResposeAvaloqValidRecurring.xml").withName("ConfirmRecurringDeposit"));

        defaultResponses
                .add(withXpath("//text()[contains(.,'inpay_req') and  contains(.,'contr_period') and contains(.,'exec')]")
                        .respondWith("/DepositResposeAvaloq_Recurring.xml").withName("MakeRecurringDeposit"));

        // Valid and Exec for Deposits
        defaultResponses.add(withXpath("//text()[contains(.,'inpay_req')and contains(.,'valid')]").respondWith(
                "/ConfirmDepositResponseAvaloq.xml").withName("ConfirmDeposit"));

        defaultResponses.add(withXpath("//text()[contains(.,'inpay_req') and contains(.,'exec') ]").respondWith(
                "/DepositResposeAvaloq_PayOnce.xml").withName("MakeDeposit"));

        defaultResponses.add(withXpath("//text()[contains(.,'EXTL_HOLD_REQ')]").respondWith("/SubmitExternalAssetResponse.xml")
                .withName("submitExternalAssetResponse"));

        defaultResponses.add(withXpath("//text()[contains(.,'CASH_CAT_REQ')]").respondWith(
                "/SubmitCashCategorisationResponse.xml").withName("cashCategorisationSubmitResponse"));

        // Stop Deposit
        defaultResponses.add(withXpath("//text()[contains(.,'inpay_req') and contains(.,'exec') and contains(.,'cancel')]")
                .respondWith("/DepositResposeAvaloq.xml").withName(
                        AvaloqOperation.PAY_REQ.name() + "ReqCancelDeposit" + PayeeType.PAY_ANYONE.name()));

        // delete saved deposit
        defaultResponses.add(withXpath("//text()[contains(.,'inpay_req') and contains(.,'hold_inpay_discd')]")
                .respondWith("/CancelSavedDepositResponse.xml").withName("CancelSavedDeposit"));

        // delete saved recurring deposit
        defaultResponses.add(withXpath("//text()[contains(.,'inpay_req') and contains(.,'hold_stcoll_discd')]")
                .respondWith("/CancelSavedDepositResponse.xml").withName("CancelSavedRecurringDeposit"));

        // load saved deposits
        defaultResponses.add(response(SAVED_DEPOSITS, "/SavedDepositsLoadResponse.xml"));

        defaultResponses.add(withXpath("//*[local-name() = 'SearchPaymentInstructionsRequestMsg']").respondWith(
                "/TransactionListResponse.xml").withName("TransactionListResponse"));

        defaultResponses.add(withXpath("//*[local-name() = 'SearchPaymentInstructionsRequestMsg']").respondWith(
                "/UpdatePaymentInstructionResponse.xml").withName("SearchPaymentInstructionsRequestMsg"));

        defaultResponses.add(withXpath("//*[local-name() = 'IncomeSummaryReportRequest']").respondWith(
                "/IncomeSummaryReportResponse.xml").withName("IncomeSummaryReportRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'ClientStatementsRequest']").respondWith(
                "/ClientStatementsResponse.xml").withName("ClientStatementsRequest"));

        defaultResponses.add(withXpath("//*[local-name() = 'SetPasswordRequest']").respondWith("/SetPasswordResponse.xml")
                .withName("SetPasswordRequest"));

        defaultResponses.add(withXpath("//*[local-name() ='ClientOnBoardingAvaloqRequest']").respondWith(
                "/OnBoardingClientDetailsFromAvaloq.xml").withName("ClientOnBoardingAvaloqRequest"));

        defaultResponses.add(withXpath("//text()[contains(.,'UI_SUPP_STAFF_DET')]").respondWith(
                "/SupportStaffAvaloqResponse_DealerGroup.xml").withName("SupportStaffAvaloqResponse_DealerGroup"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'tay%')] or  //text()[contains(.,'TAY%')]")
                .respondWith("/UserSearchAvaloq.xml").withName("TAY-personSearch"));

        defaultResponses
                .add(withXpath(
                        "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'test%')] or  //text()[contains(.,'TEST%')] or  //text()[contains(.,'201631316')]")
                        .respondWith("/UserSearchAvaloq.xml").withName("TEST-personSearch"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'test%')] or  //text()[contains(.,'10000667')]")
                .respondWith("/UserSearchAvaloq-Deepshikha.xml").withName("TEST-personSearchDeepshikha"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'tayl%')] or  //text()[contains(.,'TAYL%')]")
                .respondWith("/UserSearchAvaloq-TAYL.xml").withName("TAYL-personSearch"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'taylo%')] or  //text()[contains(.,'TAYLO%')]")
                .respondWith("/UserSearchAvaloq-TAYLO.xml").withName("TAYLO-personSearch"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'taylor%')] or  //text()[contains(.,'TAYLOR%')]")
                .respondWith("/UserSearchAvaloq-TAYLOR.xml").withName("TAYLOR-personSearch"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'SRCH_REQ')] and  //text()[contains(.,'201649533')] or  //text()[contains(.,'201649533')]")
                .respondWith("/UserSearchAvaloq-201649533.xml").withName("201649533-personSearch"));

        defaultResponses.add(withXpath("//text()[contains(.,'SRCH_REQ')] and  //text()[(contains(.,'30767'))]").respondWith(
                "/UserSearchAvalq-30767.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'SRCH_REQ')] and  //text()[(contains(.,'68489'))]").respondWith(
                "/UserSearchAvalq-68489.xml").withName("p68489-personSearch"));

        defaultResponses.add(withXpath("//text()[contains(.,'SRCH_REQ')] and  //text()[not(contains(.,'tay'))]").respondWith(
                "/UserSearchAvaloq-NOREC.xml").withName("NOREC-personSearch"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'ntfcn_req')] and //text()[contains(.,'update')] and //text()[contains(.,'ntfcn_id')]")
                .respondWith("/UpdateNotificationResponseAvaloq.xml").withName("UpdateNotificationResponseAvaloq"));

        defaultResponses
                .add(withXpath(
                        "//text()[contains(.,'ntfcn_req')] and //text()[contains(.,'add')] and //text()[contains(.,'ntfcn_evt_type_id')]")
                        .respondWith("/AddNotificationResponseAvaloq.xml").withName("AddNotificationResponseAvaloq"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')]  and //text()[contains(.,'999888')] and //text()[contains(.,'cgt_lkm_tax')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("UpdateTaxPrefError"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'cgt_lkm_tax')]").respondWith(
                "/bp_req_upd_cgt_lkm_tax_rsp.xml").withName("UpdateTaxPreference"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'pri_ctact_person')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("UpdatePrimaryContactError"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'pri_ctact_person')]")
                .respondWith("/bp_resp_upd_pri_ctact_person.xml").withName("UpdatePrimaryContact"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'add_linked_acc')]")
                .respondWith("/bp_req_link_acc_rsp_err.xml").withName("AddLinkedAccountErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'add_linked_acc')]").respondWith(
                "/bp_req_add_link_acc_rsp.xml").withName("AddLinkedAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'upd_linked_acc')]")
                .respondWith("/bp_req_link_acc_rsp_err.xml").withName("UpdateLinkedAccountErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'upd_linked_acc')]").respondWith(
                "/bp_req_update_link_acc_rsp.xml").withName("UpdateLinkedAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'del_linked_acc')]")
                .respondWith("/bp_req_del_link_acc_rsp_err.xml").withName("DeleteLinkedAccountErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'del_linked_acc')]").respondWith(
                "/bp_req_del_link_acc_rsp.xml").withName("DeleteLinkedAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'add_reg_payee')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("AddPayeeErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'add_reg_payee')]").respondWith(
                "/bp_req_add_payee_rsp.xml").withName("AddPayeeAccount"));
        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'upd_reg_payee')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("UpdatePayeeErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'upd_reg_payee')]").respondWith(
                "/bp_req_update_payee_rsp.xml").withName("UpdatePayeeAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'del_reg_payee')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("DeletePayeeErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'del_reg_payee')]").respondWith(
                "/bp_req_del_payee_rsp.xml").withName("DeletePayeeAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'add_reg_biller')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("AddBillerErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'add_reg_biller')]").respondWith(
                "/bp_req_add_biller_rsp.xml").withName("AddBillerAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'upd_reg_biller')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("UpdateBillerErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'upd_reg_biller')]").respondWith(
                "/bp_req_update_biller_rsp.xml").withName("UpdateBillerAccount"));
        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'del_reg_biller')]")
                .respondWith("/bp_resp_upd_bp_error.xml").withName("DeleteBillerErr"));

        defaultResponses
                .add(withXpath(
                        "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'999888')] and //text()[contains(.,'upd_trx_limit')] and //text()[contains(.,'PAY')]")
                        .respondWith("/bp_req_update_trx_limit_rsp_err.xml").withName("UpdateTransactionLimitErr"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'del_reg_biller')]").respondWith(
                "/bp_req_del_biller_rsp.xml").withName("DeleteBillerAccount"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'BP_REQ')] and //text()[contains(.,'upd_trx_limit')] and //text()[contains(.,'PAY')]")
                .respondWith("/bp_req_update_trx_limit_rsp.xml").withName("UpdateTransactionLimit"));

        defaultResponses.add(withXpath("//text()[contains(.,'BP_REQ')] and //text()[contains(.,'upd_sa_benef_det')]")
                .respondWith("/bp_req_add_benef_rsp.xml").withName("Beneficiaries"));

        defaultResponses.add(withXpath("//text()[contains(.,'AU_SA_PENS_REQ')] and //text()[contains(.,'au_sa_pens_req')]")
                .respondWith("/au_sa_pens_rsp.xml").withName("Pension"));

        defaultResponses.add(withXpath("//text()[contains(.,'REG_ACC_REQ')]").respondWith("/AddressBook.xml").withName(
                "REG_ACC_REQ"));

        defaultResponses.add(withXpath("//text()[contains(.,'EPI_DATA_REQ')]").respondWith("/EPI_20110324_100001_001.xml")
                .withName("EPI_DATA_REQ"));

        defaultResponses.add(withXpath("//text()[contains(.,'AU_SA_REQ')] and //text()[contains(.,'au_sa_req')]").respondWith(
                "/PersonalTaxDeductionSaveResponse.xml").withName("PersonalTaxDeduction"));

        // Onboarding service 3.0
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '110011001100' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '110011001100' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '111111000000' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1100']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeInvestorResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeInvestorResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty/CredentialDetails[OneTimePassword = '222222']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeWarningResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeWarningResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '99999999' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/ValidatePartyAndSMSOneTimePassChallengeRegistrationErrResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePassChallengeRegistrationErrResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '666666666666' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeSAFIDeviceErrorResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeSAFIDeviceErrorResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']/InvolvedParty/CredentialDetails[OneTimePassword = '888888888888']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeInvalidParErrResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeInvalidParErrResponse"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']")
                        .respondWith("/ValidatePartyAndSMSOneTimePasswordChallengeErrorResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeResponseError"));

        // Credentials Service 1.0
        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/UserAlias = '110011001100' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/UserAlias = '111111000000' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1100']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeInvestorResponse.xml")
                        .withName("ValidatePartyAndSMSOneTimePasswordChallengeInvestorResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty/CredentialDetails[UserAlias = '222222']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeWarningResponse.xml")
                        .withName("ValidatePartyAndSMSOneTimePasswordChallengeWarningResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/UserAlias = '99999999' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePassChallengeRegistrationErrResponse.xml")
                        .withName("ValidatePartyAndSMSOneTimePassChallengeRegistrationErrResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/UserAlias = '666666666666' and PartyDetails/Individual/LastName = 'test' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '1010']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeSAFIDeviceErrorResponse.xml")
                        .withName("ValidatePartyAndSMSOneTimePasswordChallengeSAFIDeviceErrorResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty/CredentialDetails[UserAlias = '888888888888']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeInvalidParErrResponse.xml")
                        .withName("ValidateParty_AndSMSOneTimePasswordChallengeInvalidParErrResponse_credentialservice"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartySMSOneTimePasswordChallengeRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty/CredentialDetails[UserAlias = 'error']")
                        .respondWith("/credentialservice/ValidatePartyAndSMSOneTimePasswordChallengeErrorResponse.xml").withName(
                                "ValidatePartyAndSMSOneTimePasswordChallengeResponseError_credentialservice"));
        // End of CredentialsService 1.0

        // For STS requests
        defaultResponses.add(withXpath("//*[local-name() = 'RequestSecurityToken']").respondWith("/ServerSamlResponse.xml")
                .withName("ServerSTSAuthorityResponse"));

        // CreateOneTimePasswordAndSendEmailRequestMsg
        defaultResponses.add(withXpath("//*[local-name() = 'CreateOneTimePasswordAndSendEmailRequestMsg']").respondWith(
                "/CreateOneTimePasswordAndSendEmailResponseMsg.xml").withName("CreateOneTimePasswordAndSendEmailRequestMsg"));

        // CreateOneTimePasswordSendEmailRequestMsg
        defaultResponses
                .add(withXpath(
                        "//*[local-name()='CreateOneTimePasswordSendEmailRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']")
                        .respondWith("/CreateOneTimePasswordSendEmailResponse-V3_0.xml").withName(
                                "CreateOneTimePasswordSendEmailRequestMsg-V3_0"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name()='CreateOneTimePasswordSendEmailRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']")
                        .respondWith("/CreateOneTimePasswordSendEmail.xml").withName(
                                "CreateOneTimePasswordSendEmailResponse10"));

        // ProcessInvestorApplicationRequestMsg
        defaultResponses
                .add(withXpath(
                        "//*[local-name()='ProcessInvestorApplicationRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/OnboardingService/OnboardingRequest/V3_0']")
                        .respondWith("/ProcessInvestorApplicationResponse-V3_0.xml").withName(
                                "ProcessInvestorApplicationRequest-V3_0"));

        defaultResponses.add(withXpath("//*[local-name() = 'SearchImagesRequestMsg']").respondWith("/BasilServiceResponse.xml")
                .withName("SearchImagesRequestMsg"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.NOTIFICATIONS_COUNT_UNREAD.getName() + "')]")
                .respondWith("/BTFG$TASK_NTFCN.UI_NTFCN_UNREAD.xml").withName("MessageCountDetails123"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.IPS_INVST_OPT.getName() + "')]").respondWith(
                "/BTFG$UI_PROD_LIST.USER#IPS_INVST_OPT_DET.xml").withName("InvestmentOptionsAssociatedList"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.GET_NOTIFICATION_COUNT_UNREAD.getName() + "')]")
                .respondWith("/BTFG$UI_NTFCN_LIST.PRIO_CAT_CTR.xml").withName("MessageCountDetails345"));

        defaultResponses.add(withXpath("//text()[contains(.,'crm_iss_req') ]").withName(AvaloqOperation.CRM_ISS_REQ.name())
                .respondWith("/FeedbackReferenceNoResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.FEEDBACK_TRACK_DET.getName() + "')  ]").withName(
                Template.FEEDBACK_TRACK_DET.getName()).respondWith("/TrackingComplaintDetailsResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.YEARLY_FEES.getName() + "')  ]").withName(
                Template.YEARLY_FEES.getName()).respondWith("/OneOffFeesAvaloqResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.FEE_SCHEDULE.getName() + "')  ]").withName(
                Template.FEE_SCHEDULE.getName()).respondWith("/FeeScheduleDetails.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CONT_LIST.getName() + "')  ]").withName(
                Template.CONT_LIST.getName()).respondWith("/ContList.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SAVED_PAYMENTS.getName() + "')  ]").withName(
                Template.SAVED_PAYMENTS.getName()).respondWith("/SavedPayments.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.IPS_LIST.getName() + "')  ]").withName(
                Template.IPS_LIST.getName()).respondWith("/IpsList.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.FEE_SCHEDULE_ADV.getName() + "')  ]").withName(
                Template.FEE_SCHEDULE_ADV.getName()).respondWith("/FeeScheduleDetails.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'adv_fee_req') and contains(.,'valid')]").respondWith(
                "/OneOffAdviceFeesTransactionResponse_Error.xml").withName(AvaloqOperation.ADV_FEE_VAL_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,'adv_fee_req') and contains(.,'exec') ]").respondWith(
                "/OneOffAdviceFeesTransactionResponse.xml").withName(AvaloqOperation.ADV_FEE_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,'mdf_fee_req') and contains(.,'valid')]").respondWith(
                "/FeeScheduleTransactionResponse_validate.xml").withName(AvaloqOperation.MDF_FEE_VAL_REQ.name()));

        defaultResponses.add(withXpath("//text()[contains(.,'mdf_fee_req') and contains(.,'exec')]").respondWith(
                "/FeeScheduleTransactionResponse.xml").withName(AvaloqOperation.MDF_FEE_REQ.name()));

        defaultResponses.add(withXpath("//*[local-name() = 'query']").respondWith("/CMISEnquiryResponse.xml").withName(
                "CMISEnquiry"));

        defaultResponses.add(withXpath("//*[local-name() = 'getContentStream']").respondWith("/CMISDocumentResponse.xml")
                .withName("CMISDocument"));

        defaultResponses.add(withXpath(
                "//*[local-name()='RetrievePolicyByPolicyNumberRequestMsg'] and //text()[contains(., 'C5698745')]").respondWith(
                "/RetrievePolicyByPolicyNumber_Error.xml").withName("RetrievePolicyByPolicyNumberRequestMsg_Error"));
        defaultResponses.add(withXpath("//*[local-name()='RetrievePolicyByPolicyNumberRequestMsg']").respondWith(
                "/RetrievePolicyByPolicyNumber.xml").withName("RetrievePolicyByPolicyNumberRequestMsg"));

        defaultResponses.add(withXpath("//*[local-name()='SearchPolicyByPaymentAccountRequestMsg']").respondWith(
                "/SearchPolicyByPaymentAccountResponse.xml").withName("SearchPolicyByPaymentAccountRequestMsg"));

        defaultResponses.add(withXpath("//*[local-name()='RetrieveUnderwritingByPolicyNumberRequestMsg']").respondWith(
                "/RetrieveUnderwritingByPolicyNumberResponseMsg.xml").withName("RetrieveUnderwritingByPolicyNumberResponseMsg"));

        defaultResponses.add(withXpath(
                "//*[local-name()='SearchAccessibleAccountsRequestMsg'] and //text()[contains(., '735258629')]").respondWith(
                "/SearchAccessibleAccountsResponseMsg_Error.xml").withName("SearchAccessibleAccountsResponseMsg_For_Error"));
        defaultResponses.add(withXpath(
                "//*[local-name()='SearchAccessibleAccountsRequestMsg'] and //text()[contains(., '425059904')]").respondWith(
                "/SearchAccessibleAccountsResponseMsg_For_425059904.xml").withName(
                "SearchAccessibleAccountsResponseMsg_For_425059904"));
        defaultResponses.add(withXpath(
                "//*[local-name()='SearchAccessibleAccountsRequestMsg'] and //text()[contains(., '675056129')]").respondWith(
                "/SearchAccessibleAccountsResponseMsg_For_675056129.xml").withName(
                "SearchAccessibleAccountsResponseMsg_For_675056129"));
        defaultResponses.add(withXpath(
                "//*[local-name()='SearchAccessibleAccountsRequestMsg'] and //text()[contains(., '735208629')]").respondWith(
                "/SearchAccessibleAccountsResponseMsg_For_735208629.xml").withName(
                "SearchAccessibleAccountsResponseMsg_For_735208629"));
        defaultResponses.add(withXpath("//*[local-name()='SearchAccessibleAccountsRequestMsg']").respondWith(
                "/SearchAccessibleAccountsResponseMsg.xml").withName("SearchAccessibleAccountsResponseMsg"));
        defaultResponses.add(withXpath("//*[local-name()='SearchPolicyByAdviserRequestMsg'] and //text()[contains(., '32658')]")
                .respondWith("/SearchPolicyByAdviserResponseMsg_Error.xml").withName("SearchPolicyByAdviserResponseMsgError"));
        defaultResponses.add(withXpath("//*[local-name()='SearchPolicyByAdviserRequestMsg']").respondWith(
                "/SearchPolicyByAdviserResponseMsg.xml").withName("SearchPolicyByAdviserResponseMsg"));
        defaultResponses.add(withXpath(
                "//*[local-name()='SearchRecentLivesInsuredByAdviserRequestMsg'] and //text()[contains(., '425062904')]")
                .respondWith("/SearchRecentLivesInsuredByAdviserResponseMsg_Error.xml").withName(
                        "SearchRecentLivesInsuredByAdviserResponseMsg_for_Error"));
        defaultResponses.add(withXpath("//*[local-name()='SearchRecentLivesInsuredByAdviserRequestMsg']").respondWith(
                "/SearchRecentLivesInsuredByAdviserResponseMsg.xml").withName("SearchRecentLivesInsuredByAdviserResponseMsg"));

        defaultResponses.add(withXpath(
                "//*[local-name()='SearchPolicyByCustomerNumberRequestMsg'] and //text()[contains(., '65984')]").respondWith(
                "/SearchPolicyByCustomerNumber_Error.xml").withName("SearchPolicyByCustomerNumberRequestMsg_for_Error"));
        defaultResponses.add(withXpath("//*[local-name()='SearchPolicyByCustomerNumberRequestMsg']").respondWith(
                "/SearchPolicyByCustomerNumber.xml").withName("SearchPolicyByCustomerNumberRequestMsg"));

        // New mapping for portfolio valuation
        defaultResponses.add(withXpath("//*[local-name() ='RetrievePortfolioValuationRequestMsg']").withName(
                "InvestmentAccountRequestV2_0").respondWith("/ESBPortfolioValuationResponse.xml"));

        // Mapping for asset allocation
        defaultResponses.add(withXpath("//*[local-name() ='RetrieveAssetAllocationRequestMsg']").withName(
                "RetrieveAssetAllocationRequestMsg").respondWith("/ESBAssetAllocationResponse.xml"));

        // Mapping for Tax Invoice
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.TAX_INVOICE.getName() + "')]").respondWith(
                "/BTFG$UI_BOOK_LIST_DOC_EVT_GST.xml").withName(Template.TAX_INVOICE.getName()));

        // Mapping for Tax Invoice PMF
        defaultResponses.add(withXpath("//text()[contains(.,'" + TaxInvoiceTemplate.TAX_INVOICE_PMF.getTemplateName() + "')]")
                .respondWith("/BTFG$UI_BOOK_LIST_PMF_GST.xml").withName(TaxInvoiceTemplate.TAX_INVOICE_PMF.getTemplateName()));

        // Mapping for Corporate Actions
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTIONS_VOLUNTARY.getName() + "')]")
                .withName(Template.CORPORATE_ACTIONS_VOLUNTARY.getName()).respondWith(
                        "/corporateactions/CorporateActionResponse.xml"));

        // Mapping for Corporate Action details
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTION_DETAILS.getName() + "')]").withName(
                Template.CORPORATE_ACTION_DETAILS.getName()).respondWith("/corporateactions/CorporateActionDetailsResponse.xml"));
        // Mapping for Corporate Action Account details
        defaultResponses
                .add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTION_ACCOUNTS.getName() + "')]").withName(
                        Template.CORPORATE_ACTION_ACCOUNTS.getName()).respondWith(
                        "/corporateactions/CorporateActionAccountResponse.xml"));
        // Mapping for Corporate Action notification pending count
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTION_PENDING_COUNT.getName() + "')]")
                .withName(Template.CORPORATE_ACTION_PENDING_COUNT.getName()).respondWith(
                        "/corporateactions/CorporateActionPendingNotificationCount.xml"));
        // Mapping for Corporate Action Transaction details
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTION_PARTICIPATION.getName() + "')]")
                .withName(Template.CORPORATE_ACTION_PARTICIPATION.getName()).respondWith(
                        "/corporateactions/CorporateActionTransactionDetailsResponse.xml"));
        // Mapping for Corporate Action Transaction details
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CORPORATE_ACTION_PARTICIPATION_IM.getName() + "')]")
                .withName(Template.CORPORATE_ACTION_PARTICIPATION_IM.getName()).respondWith(
                        "/corporateactions/CorporateActionTransactionDetailsResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SUPER_CONTRIBUTIONS_CAPS.getName() + "')]").withName(
                Template.SUPER_CONTRIBUTIONS_CAPS.getName()).respondWith("ContributionCapsResponse.xml"));
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SUPER_CONTRIBUTIONS_HISTORY.getName() + "')]")
                .withName(Template.SUPER_CONTRIBUTIONS_HISTORY.getName()).respondWith("ContributionHistoryResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SUPER_PENSION_COMMENCEMENT_STATUS.getName() + "')]")
                .withName(Template.SUPER_PENSION_COMMENCEMENT_STATUS.getName()).respondWith("PendingSuperCommencementResponse.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.PERSON_LIST_INVSTR.getName() + "')  ]").withName(
                Template.PERSON_LIST_INVSTR.getName()).respondWith("/PERSON_LIST_FOR_EXISTING_CLIENT_SEARCH.xml"));
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.PERSON_LIST_INVSTR_FLAT.getName()
                        + "') ] and //text()[contains(.,'36420920000')]").withName(Template.PERSON_LIST_INVSTR_FLAT.getName())
                .respondWith("/PERSON_LIST_FOR_EXISTING_CIS_SEARCH.xml")
                .withName(Template.PERSON_LIST_INVSTR_FLAT.getName() + "validCISKey"));

        //CIS search - multiple accounts with DIRECT ( -> user_exper_id) SUPER ( acc_struct_type_id & sa_sub_type_id)
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.PERSON_LIST_INVSTR_FLAT.getName()
                        + "') ] and //text()[contains(.,'36420920011')]").withName(Template.PERSON_LIST_INVSTR_FLAT.getName())
                .respondWith("/PERSON_LIST_FOR_EXISTING_CIS_SEARCH_DIRECT_SUPER.xml")
                .withName(Template.PERSON_LIST_INVSTR_FLAT.getName() + "validCISKeyDirectSuper"));

        //CIS search - multiple accounts with DIRECT ( -> user_exper_id) PENSION ( acc_struct_type_id & sa_sub_type_id)
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.PERSON_LIST_INVSTR_FLAT.getName()
                        + "') ] and //text()[contains(.,'36420920077')]").withName(Template.PERSON_LIST_INVSTR_FLAT.getName())
                .respondWith("/PERSON_LIST_FOR_EXISTING_CIS_SEARCH_DIRECT_PESION.xml")
                .withName(Template.PERSON_LIST_INVSTR_FLAT.getName() + "validCISKeyDirectPension"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + Template.PERSON_LIST_INVSTR_FLAT.getName()
                        + "') ] and //text()[contains(.,'48813020036')]").withName(Template.PERSON_LIST_INVSTR_FLAT.getName())
                .respondWith("/BTFG$UI_PERSON_LIST.USER#INVSTR_FLAT_InvestorDetails.xml")
                .withName(Template.PERSON_LIST_INVSTR_FLAT.getName() + "validIndividualCISKey"));

        defaultResponses
                .add(withXpath("//text()[contains(.,'" + Template.PERSON_LIST_INVSTR_FLAT.getName() + "') ] and //text()")
                        .withName(Template.PERSON_LIST_INVSTR_FLAT.getName())
                        .respondWith("/PERSON_LIST_FOR_EXISTING_CLIENT_SEARCH.xml")
                        .withName(Template.PERSON_LIST_INVSTR_FLAT.getName() + "InvalidCISKey"));



        // Mapping for Financial Instrument Market
        defaultResponses.add(withXpath("//*[local-name() = 'FinancialMarketInstrument']").respondWith(
                "/FinancialMarketInstrumentResponse.xml").withName("FinancialMarketInstrument"));

        defaultResponses.add(withXpath(
                "//*[local-name() = 'RetrieveAuthorisedTrustsRequestMsg'] and //text()[contains(., '77777')]").respondWith(
                "/InvestmentTrustResponseCustomerNotFound.xml").withName("RetrieveAuthorisedTrustsRequestCustomerNotFound"));

        defaultResponses.add(withXpath(
                "//*[local-name() = 'RetrieveAuthorisedTrustsRequestMsg'] and //text()[contains(., '66666')]").respondWith(
                "/InvestmentTrustResponseError.xml").withName("RetrieveAuthorisedTrustsRequestMsgWithError"));
        // Mapping for Investment trust
        defaultResponses.add(withXpath("//*[local-name() = 'RetrieveAuthorisedTrustsRequestMsg']").respondWith(
                "/InvestmentTrustResponse.xml").withName("RetrieveAuthorisedTrustsRequestMsg"));

        // Mapping for collection list response
        defaultResponses.add(withXpath("//*[contains(.,'" + CollectionTemplate.COLLECTION_ASSETS.getTemplateName() + "')  ]")
                .respondWith("/collection/CollectionListResponse.xml").withName(
                        CollectionTemplate.COLLECTION_ASSETS.getTemplateName() + "ValidReq"));

        // Mapping for Transaction Fees
        defaultResponses.add(withXpath("//*[contains(.,'" + Template.TRANSACTION_FEES.getName() + "')  ]").respondWith(
                "/TransactionFeesResponse.xml").withName(Template.TRANSACTION_FEES.getName() + "ValidReq"));

        defaultResponses.add(withXpath("//*[contains(.,'" + Template.LICENSE_ADVISER_FEE.getName() + "')  ]").respondWith(
                "/LicenseAdviserFeesResponse.xml").withName(Template.LICENSE_ADVISER_FEE.getName() + "ValidReq"));



        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.CHESS_PARAMETER.getName() + "')]").respondWith(
                "/ChessSponsorResponse.xml").withName(Template.CHESS_PARAMETER.getName()));
        // Mapping for Transition Accounts - broker id
        defaultResponses.add(withXpath(
                "//*[contains(.,'" + AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName()
                        + "') ] and //text()[contains(.,'100747')]")
                .respondWith("/TransitionAccountDetailsResponse-brokerId.xml").withName(
                        AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "ValidReq"));
        // date range
        defaultResponses.add(withXpath(
                "//*[contains(.,'" + AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "')  ] and "
                        + "//param/name='open_date_from' and //param/name='open_date_to'").respondWith(
                "/TransitionAccountDetailsResponse-dateRange.xml").withName(
                AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "ValidReq"));
        // all fields
        defaultResponses.add(withXpath(
                "//*[contains(.,'" + AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "')  ] and "
                        + "count(//task/param_list/param/val/val) = 3").respondWith(
                "/TransitionAccountDetailsResponse-allParameters.xml").withName(
                AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "ValidReq"));
        // all accounts
        defaultResponses.add(withXpath(
                "//*[contains(.,'" + AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "')  ] and "
                        + "count(//task/param_list/param/val/val) = 0").respondWith(
                "/TransitionAccountDetailsResponse-allAccounts.xml").withName(
                AccountTemplate.ADVISER_TRANSACTION_BP_LIST.getTemplateName() + "ValidReq"));

        // Mappings for settlements by account key.
        defaultResponses.add(withXpath(
                "//*[contains(.,'" + AccountTemplate.ASSET_TRANSFER_SETTLEMENTS.getTemplateName() + "')  ]").respondWith(
                "/AssetTransferSettlementsForBPNo.xml").withName(
                AccountTemplate.ASSET_TRANSFER_SETTLEMENTS.getTemplateName() + "ValidReq"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartyRegistrationRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '990099009900' and PartyDetails/Individual/LastName = 'err' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '3000']")
                        .respondWith("/ValidatePartyRegistrationInvalidResponse.xml").withName(
                                "ValidatePartyRegistrationRequestMsg_Invalid"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartyRegistrationRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '222222222222' and PartyDetails/Individ"
                                + "ual/LastName = 'safi' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '2000']")
                        .respondWith("/ValidatePartyRegistrationSafiFail.xml").withName(
                                "ValidatePartyRegistrationRequestMsg_SAFIFail"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'ValidatePartyRegistrationRequestMsg' and namespace-uri()='ns://btfin.com/Product/Panorama/CredentialService/CredentialRequest/V1_0']/InvolvedParty"
                                + "[CredentialDetails/OneTimePassword = '777777777777' and PartyDetails/Individual/LastName = 'saml' and PostalAddress/AddressDetail/StructuredAddressDetail/Postcode = '3000']")
                        .respondWith("/ValidatePartyRegistrationSamlFailResponse.xml").withName(
                                "ValidatePartyRegistrationSamlFailResponse"));

        defaultResponses.add(withXpath("//*[local-name() = 'ValidatePartyRegistrationRequestMsg']").respondWith(
                "/ValidatePartyRegistrationResponse.xml").withName("ValidatePartyRequestMsg"));

        // View Beneficiary Mapping
        defaultResponses.add(withXpath("//text()[contains(.,'" + Template.SUPER_VIEW_BENEFICIARIES.getName() + "')]").withName(
                Template.SUPER_VIEW_BENEFICIARIES.getName()).respondWith("ViewBeneficiaryResponse.xml"));

        // adviser holding limit breach report
        defaultResponses.add(response(HOLDING_BREACH_REPORT, "/HoldingBreachLoadResponse.xml"));

        // Avaloq Rule service
        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + RuleTemplate.AVALOQ_RULE.getTemplateName() + "') and not(contains(., '201617777'))]")
                .withName(RuleTemplate.AVALOQ_RULE.getTemplateName()).respondWith("/BTFG$TASK_2FA_RULE.FILTER#RULE.xml"));

        defaultResponses.add(withXpath(
                "//text()[contains(.,'" + RuleTemplate.AVALOQ_RULE.getTemplateName() + "') and contains(., '201617777')]")
                .withName(RuleTemplate.AVALOQ_RULE.getTemplateName() + "NOT").respondWith(
                        "/BTFG$TASK_2FA_RULE.FILTER#RULE_NOT.xml"));

        defaultResponses.add(withXpath("//text()[contains(.,'FA_RULE_REQ')]").respondWith("/FA_RULE_REQ.xml").withName(
                "FA_RULE_REQ"));

        // --------
        defaultResponses.add(withXpath("//*[local-name()='ResendRegistrationCode']").respondWith("/ResendRegistrationCode.xml")
                .withName("ResendRegistrationCode"));

        defaultResponses.add(withXpath("//*[local-name() = 'retrievePostalAddressRequest']").respondWith(
                "/RetrievePostalAddressResponse.xml").withName("RetrievePostalAddressRequest"));
      //Addede for service 256
        
        defaultResponses.add(withXpath("//*[local-name() = 'maintainArrangementAndIPArrangementRelationshipsRequest']").respondWith(
                "/UC1_Create_IP_AR_Response.xml").withName("RetrievePostalAddressRequest"));
        defaultResponses
        .add(withXpath(
                "//*[local-name() = 'maintainArrangementAndIPArrangementRelationshipsRequest' and namespace-uri() ='http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/xsd/maintainArrangementAndIPArrangementRelationships/v1/SVC0256/']/involvedPartyIdentifier/*[local-name() = 'involvedPartyId' and text() = '11111111111']")
                .respondWith("/gesb/retrieve-customerdetails/11111111111.xml").withName(
                        "retrieve-customerdetails-11111111111"));
        
        // Addede for service 260

        defaultResponses
                .add(withXpath("//*[local-name() = 'retrieveIPToIPRelationshipsRequest']").respondWith(
                        "/RetrieveIPToIPRelationships_Response_Spec.xml").withName(
                        "RetrieveIPToIPRelationships_Response_Spec"));

        // Addde for service 257

        defaultResponses
                .add(withXpath("//*[local-name() = 'maintainIPToIPRelationshipsRequest']").respondWith(
                        "/MaintainIPToIPRelationships_Response_Spec.xml").withName(
                        "MaintainIPToIPRelationships_Response_Spec"));
        // Addde for service 324

        defaultResponses.add(withXpath("//*[local-name() = 'retrieveIDVDetailsRequest']").respondWith(
                "/SVC0324_Response.xml").withName("SVC0324_Response"));
        // Addde for service 337

        defaultResponses.add(withXpath("//*[local-name() = 'createOrganisationIPRequest']").respondWith(
                "/SVC337_Res1.xml").withName("SVC337_Rersponse"));
      //Added for service 336
        defaultResponses.add(withXpath("//*[local-name() = 'createIndividualIPRequest']").respondWith(
                "/SVC0336_Response.xml").withName("SVC0336_Response"));
        
        defaultResponseFactoryMap.put(Profiles.COMMON, defaultResponses);

     // Addde for service 325

        defaultResponses.add(withXpath("//*[local-name() = 'maintainIDVDetailsRequest']").respondWith(
                "/SVC0325_Response.xml").withName("SVC0325_Response"));
     
        // Added for service 454
        defaultResponses.add(withXpath("//*[local-name() ='retrievePostalAddressRequest']/*[local-name()='addressType' and text() = 'S']").respondWith(
                "retrievePostalAddressResponse_stadard.xml").withName("retrievePostalAddressResponse_stadard"));
        
        defaultResponses.add(withXpath("//*[local-name() ='retrievePostalAddressRequest']/*[local-name()='addressType' and text() = 'N']").respondWith(
                "retrievePostalAddressResponse_non-standard.xml").withName("retrievePostalAddressResponse_non-stadard"));
        
        defaultResponses.add(withXpath("//*[local-name() ='retrievePostalAddressRequest']/*[local-name()='addressType' and text() = 'P']").respondWith(
                "retrievePostalAddressResponse_provider.xml").withName("retrievePostalAddressResponse_provider"));
        
        defaultResponses.add(withXpath("//*[local-name() ='retrievePostalAddressRequest']").respondWith(
                "retrievePostalAddressResponse_all.xml").withName("retrievePostalAddressResponse_all"));

        defaultResponseFactoryMap.put(Profiles.COMMON, defaultResponses);

        // Set Safi Responses
        safiDefaultResponses.add(withXpath("//*[local-name() = 'analyze']").respondWith("/AnalyzeResponseFromSafi.xml").withName(
                "AnalyzeResponseFromSafi"));

        safiDefaultResponses
                .add(withXpath(
                        "//*[local-name() = 'challenge']/*[local-name() = 'request']/*[local-name() = 'identificationData']/*[local-name()='userName' and (not(text()) or text() != '20161983')]")
                        .respondWith("/ChallengeResponseFromSafi.xml").withName("ChallengeResponseFromSafi"));

        safiDefaultResponses
                .add(withXpath(
                        "//*[local-name() = 'challenge']/*[local-name() = 'request']/*[local-name() = 'identificationData']/*[local-name()='userName' and text() = '20161983']")
                        .respondWith("/ChallengeResponseInvalidStatus.xml").withName("ChallengeResponseWithErrorFromSafi"));

        safiDefaultResponses.add(withXpath("//*[local-name() = 'authenticate'] and //text()[contains(.,'111111')]").respondWith(
                "/AuthenticateResponseFromSafi.xml").withName("AuthenticateResponseFromSafi"));

        safiDefaultResponses.add(withXpath("//*[local-name() = 'authenticate']  and //text()[not(contains(.,'111111'))]")
                .respondWith("/AuthenticateResponseFromSafiError.xml").withName("AuthenticateResponseFromSafiError"));
        safiDefaultResponses.add(withXpath("//*[local-name() ='maintainMFADeviceArrangementRequest']").respondWith(
                "/MaintainSafi.xml").withName("MaintainMFADeviceArrangementRequest"));
        defaultResponseFactoryMap.put(Profiles.SAFI, safiDefaultResponses);

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'generateSecurityCredentialRequest']//*[local-name()='customerNumber' and count(child::*)=0 and not(contains(text(), '222222222222'))]")
                        .respondWith("/GenerateSecurityCredentialResponse.xml").withName("generateSecurityCredentialRequest"));

        defaultResponses
                .add(withXpath(
                        "//*[local-name() = 'generateSecurityCredentialRequest']//*[local-name()='customerNumber' and count(child::*)=0 and contains(text(), '222222222222')]")
                        .respondWith("/GenerateSecurityCredentialFaultResponse.xml").withName(
                                "generateSecurityCredentialRequestFault"));

        defaultResponses.add(withXpath("//*[local-name()='RetrieveDetailsRequestMsg'][Context/Requester = '74061352']").respondWith("/supermatch/RetrieveDetailsResponseMsg_error.xml").withName("RetrieveDetailsRequestMsgError"));
        defaultResponses.add(withXpath("//*[local-name()='RetrieveDetailsRequestMsg'][Context/Requester != '74061352']").respondWith("/supermatch/RetrieveDetailsResponseMsg.xml").withName("RetrieveDetailsRequestMsg"));

        defaultResponses.add(withXpath("//*[local-name()='UpsertStatusSummaryRequestMsg']").respondWith("/supermatch/UpsertStatusSummaryResponseMsg.xml").withName("UpsertStatusSummaryRequestMsg"));
        defaultResponses.add(withXpath("//*[local-name()='UpdateRolloverStatusRequestMsg']").respondWith("/supermatch/UpdateRolloverStatusResponseMsg.xml").withName("UpdateRolloverStatusRequestMsg"));
        defaultResponses.add(withXpath("//*[local-name()='NotifyCustomerRequestMsg']").respondWith("/supermatch/NotifyCustomerResponseMsg.xml").withName("NotifyCustomerRequestMsg"));
        defaultResponses.add(withXpath("//*[local-name()='MaintainECOCustomerRequestMsg']").respondWith("/supermatch/MaintainECOCustomerResponseMsg.xml").withName("MaintainECOCustomerRequestMsg"));
    }

    public static List<MockWebServiceResponse> getDefaultResponses(String key) {

        Profiles profile = Profiles.getProfile(key);
        if (profile != null) {
            return defaultResponseFactoryMap.get(profile);
        }
        return defaultResponseFactoryMap.get(Profiles.COMMON);
    }

    private static MockWebServiceResponse response(Template template, String responseFile, String name) {
        return withXpath("//*[contains(.,'" + template.getName() + "')]").respondWith(responseFile).withName(name);
    }

    private static MockWebServiceResponse response(AvaloqTemplate template, String responseFile, String name) {
        return withXpath("//*[contains(.,'" + template.getTemplateName() + "')]").respondWith(responseFile).withName(name);
    }

    private static MockWebServiceResponse response(Template template, String responseFile) {
        return response(template, responseFile, template.getName());
    }

    enum Profiles {
        SAFI("safi"),
        COMMON("common");

        private String profileName;

        Profiles(String profile) {
            this.profileName = profile;
        }

        public static Profiles getProfile(String profileName) {
            if (profileName != null) {
                for (Profiles profile : Profiles.values()) {
                    if (profileName.equalsIgnoreCase(profile.profileName)) {
                        return profile;
                    }
                }
            }
            return null;
        }
    }
}
