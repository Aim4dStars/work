/**
 * Service logging module
 * @namespace org.bt.utils.log
 */
org.bt.utils.log = (function($,window) {
    var _console = window.console,
        _opts = {
            loglevel:4
        };

    return {
        init: function(param) {
            $.extend(_opts, param);
        },
        /**
         * @memberOf org.bt.utils.log
         * @public
         * @param {String} msg Message to log
         * @param {object} [obj=undefined] Object to log
         */
        debug: function(msg, obj) {
            if (_console && _opts.loglevel >= 4) {
                obj = (obj) ? _console.debug(msg, [obj]) : _console.debug(msg);
            }
        },
        /**
         * @memberOf org.bt.utils.log
         * @public
         * @param {String} msg Message to log
         * @param {object} [obj=undefined] Object to log
         */
        info: function(msg, obj) {
            if (_console && _opts.loglevel >= 3) {
                obj = (obj) ? _console.info(msg, [obj]) : _console.info(msg);
            }
        },
        /**
         * @memberOf org.bt.utils.log
         * @public
         * @param {String} msg Message to log
         * @param {object} [obj=undefined]  Object to log
         */
        warn: function(msg, obj) {
            if (_console && _opts.loglevel >= 2) {
                obj = (obj) ? _console.warn(msg, [obj]) : _console.warn(msg);
            }
        },
        /**
         * @memberOf org.bt.utils.log
         * @public
         * @param {String} msg Message to log
         * @param {object} [obj=undefined] Object to log
         */
        error: function(msg, obj) {
            if (_console && _opts.loglevel >= 1) {
                obj = (obj) ? _console.error(msg, [obj]) : _console.error(msg);
            }
        }
    };
})(jQuery,window);

/**
 * Service directory
 * @namespace org.bt.utils.serviceDirectory
 */
org.bt.utils.serviceDirectory = (function($,window) {
    var path    = document.location.pathname.split('page'),
        params  = (typeof path[1] === 'undefined') ? 'test/undefined/undefined' : path[1].split('/');

    return {
        /**
         * TODO This needs to be dynamic can't hardcode this
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} contextPath context path for the application
         */
        contextPath                 :function(){
            var hash = window.location.pathname,pathParam;
            if(hash.indexOf('/ng_')==0){
                pathParam = hash.split('/');
                return "/"+pathParam[1]+org.bt.contextPath;
            }
            else {
                return org.bt.contextPath;
            }
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} home home url for the application
         */
        home                        :'secure/page/home',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} logout logout url for the application
         */
        logout                      :org.bt.url.logout,
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} where to send the user after logout
         */
        afterLogout                      :org.bt.url.afterLogout,
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} where to send the user after successful logout
         */
        logoutSuccess               : org.bt.url.logoutSuccess,
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} logging logging url for the application
         */
        logging                     :'secure/api/log',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} billerCode biller code validation url
         */
        billerCode                  :'secure/api/billercode',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validate validate field url
         */
        validate                    :'secure/api/validateField',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateRegistration validate registration url
         */
        validateRegistration        :'secure/api/validateField',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateRegistration validate registration url for login
         */
        validateLoginRegistration        :'public/api/validateRegistration',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateCredentials validate credentials url for registration
         */
        validateCredentials : 'public/api/validateCredentials',


          /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} verifySmsAndRegistration validate registration url for Next button click
         */
        verifySmsAndRegistration        :'public/api/verifySmsAndRegistration',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} verifyDealerGroupSmsAndRegistration validate registration url for Next button click. When 2FA configured on dealer group level.
         */
        verifyDealerGroupSmsAndRegistration : 'public/api/verifyDealerGroupSmsAndRegistration',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} forgetPasswordVerifySms verify sms and forgot password
         */
        forgetPasswordVerifySms        :'public/api/verifySmsAndForgotPassword',


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} forgetPasswordValidate validate forgot password url for login
         */
        forgetPasswordValidate        :'public/api/validateRegistration',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} forgotPassword validate data
         */
        forgotPassword : 'public/api/forgotPassword',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateCrn validate crn url
         */
        validateCrn                 :'secure/api/validateCrn',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateSmsCode validate sms code url
         */
        validateSmsCode             :'secure/api/sendSmsCode',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} resetPasswordStep1 reset password step one url
         */

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validateDailyLimit to validate daily limit from payments
         */
        validateDailyLimit : 'secure/api/'+params[1]+'/'+params[2]+'/checkPaymentLimit',


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} submitDailyLimitForm to submit the new daily limit
         */
        submitDailyLimitForm : 'secure/api/'+params[2]+'/submitDailyLimit',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         */
        setPrimaryAccount:'secure/api/'+params[1]+'/'+params[2]+'/setPrimaryLinked',

        resetPasswordStep1          :'public/api/verifySmsCode',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} resetPasswordStep2 reset password step two url
         */
        resetPasswordStep2          :'/eam/servlet/ChangePasswordNGServlet',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} resetPasswordStep2 reset password step two url
         */
        forgotPasswordStep2          :'secure/api/setForgotPassword',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} registrationStep1 registration step one url
         */
        registrationStep1           :'public/api/verifySmsCode',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} registrationStep2 registration step two url
         */
        registrationStep2           :'secure/api/registerUser',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} registrationStep3 registration step three url
         */
        registrationStep3           :'secure/app',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} change password URL
         */

        changePassword: 'secure/api/changePassword',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validate password URL
         */
        validateCurrentPassword: 'public/api/validateCurrentPassword',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} change username URL
         */
        changeUsername:'secure/api/resetUsername',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validate modified username
         */
        validateModifiedUsername:'public/api/validateModifiedUsername',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @constant {String} validate new password
         */
        validateNewPassword:'public/api/validateNewPassword',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for search clients
         */
        searchClients:'secure/api/searchClients',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for search support staff
         */
        searchSupportStaff:'secure/api/searchSupportStaff',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for searchClientAccounts
         */
        searchClientAccounts :'secure/api/searchClientAccounts',

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for csvDownload
         */
        csvDownload: 'secure/page/csvDownload',


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for clients
         */
        clients :'secure/page/clients#accounts',


         /**
         * @returns {String} returns url for update personal details
         */
        updatePersonalDetails:'secure/api/updateDetails',
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for add a new BPAY payee
         */
        addBpayPayee    :function () {
            return 'secure/api/'+params[2]+'/addPayee';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for editing a BPAY payee
         */
        editPayee    :function () {
            //return 'secure/api/'+params[2]+'/editPayee';
            return 'secure/api/'+params[1]+'/'+params[2]+'/updatePayee';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for editing a BPAY payee
         */
        deletePayee : function(){
             return 'secure/api/'+params[2]+'/deletePayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for add a new pay anyone payee
         */
        addPayAnyone     :function () {
            return 'secure/api/'+params[2]+'/addPayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for editing a pay anyone payee
         */
        editAccAnyone    :function () {
            return 'secure/api/'+params[1]+'/'+params[2]+'/updatePayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for editing a pay anyone payee
         */
        deleteAccAnyone    :function () {
            return 'secure/api/'+params[2]+'/deletePayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for add linked accounts
         */
        addLinkedAccounts     :function () {
            return 'secure/api/'+params[2]+'/addPayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for editing add linked accounts
         */
        editLinkedAccount    :function () {
            return 'secure/api/'+params[1]+'/'+params[2]+'/updatePayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for deleting add linked accounts
         */
        deleteLinkedAccount    :function () {
            return 'secure/api/'+params[2]+'/deletePayee';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for confirm payment
         */
        confirmPayment   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/confirmPayment';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for submit payment
         */
        submitPayment   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/submitPayment';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for confirm deposit
         */
        confirmDeposit   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/confirmDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for submit deposit
         */
        submitDeposit   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/submitDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for confirm term deposit
         */
        confirmTermDeposit   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/confirmTermDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for add term deposit
         */
        submitTermDeposit   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/addTermDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for confirm withdraw term deposit
         */
        confirmWithdrawTermDeposit   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/confirmWithdrawTermDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for withdraw term deposit
         */
        withdrawTermDeposit   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/withdrawTermDeposit';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for stopPayment
         */
        stopPayment   :function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/stopPayment';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for load more transactions
         */
        loadMorePastTransactions : function () {
            return 'secure/api/'+params[1]+'/'+params[2]+'/moreTransactions';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for load more transactions for advance filters
         */
        loadMorePastTransactionsAdvSearch : function () {
            return 'secure/api/'+params[1]+'/'+params[2]+'/searchTransactions';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for idp snapshot
         */
        idssnapshotReport   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/idssnapshotReport';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for idp snapshot pdf
         */
        idssnapshotReportPdf   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/idssnapshotReport.pdf';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for income summary report
         */
        incomeSummaryReport   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/incomeSummaryReport';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for income summary report pdf
         */
        incomeSummaryReportPdf   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/incomeSummaryReport.pdf';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for client listing report pdf
         */
        clientsPdf   :function(){
            return 'secure/page/'+params[1]+'/'+'clients.pdf';
        },

         /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for idps snapshop details pdf
         */
        getIdpsSnapshotDetailsViewPdf   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/'+params[3]+'/getIdpsSnapshotDetailsView.pdf';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for account listing report pdf
         */
        accountsPdf   :function(){
            return 'secure/page/'+params[1]+'/'+'accounts.pdf';
        },

        updateStatus: function(){
            return 'secure/api/'+params[1]+'/updateStatus';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for update adviser message
         */
        updateAdviserMessage: function(){
            return 'secure/api/'+params[1]+'/updateAdviserMessage';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for preferences form submit url
         */
        updateAdviserPreferences: function(){
            return 'secure/api/updateAdviserPreference';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for update investor message
         */
        updateInvestorMessage: function(){
            return 'secure/api/'+params[1]+'/updateInvestorMessage';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for load more adviser messages
         */
        moreAdviserMessages: function(){
            return 'secure/api/'+params[1]+'/moreAdviserMessages';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for load more investor messages
         */
        moreInvestorMessages: function(){
            return 'secure/api/'+params[1]+'/moreInvestorMessages';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for navigate to snapshot view
         */
        toSnapshotViewFromMessageCentre: function(){
            //return 'secure/page/'+params[1]+'/{id}/{id}/getIdpsSnapshotDetailsView';
            return 'secure/page/{clientId}/{portfolioId}/overview';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for BGL data download form submission
         */
        downloadBGLData: function(){
              return 'secure/page/'+params[1]+'/'+params[2]+'/getDataDownloadsFile';
        },
         /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for validate BGL data download search type selection
         */
        validateSelection: function(){
              return 'secure/page/'+params[1]+'/'+params[2]+'/validateSelection';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for dashboard dismiss all messages (mark to read)
         */
        dismissAllMessages: function(){
              return 'secure/api/{investorId}/{portfolioId}/dismissAll';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for sending sms code
         */
        sendSmsCode: function(){
              return 'secure/api/sendSmsCode';
        },

         /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for analysing sms code
         */
        analyseSmsCode: function(){
              return 'secure/api/analyze';
        },
         /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for verifying sms code
         */
        verifySmsCode: function(){
              return 'secure/api/verify';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for my tasks messages
         */
        myTasksMessages: function(){
            return 'secure/api/moreAdviserTask';
        },
        updateTermDeposit: function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/updateTermDeposit';
        },
        /**
         * @desc calc term deposit when there is no client selected
         * @returns {string}
         */
        noClientCalculateTermDeposit: function(){
            return 'secure/api/NA/NA/calculateTermDeposit';
        },
        /**
         * @desc calc term deposit when there is a client selected
         * @returns {string}
         */
        calculateTermDeposit: function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/calculateTermDeposit'
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for deleting saved drafts
         */
        deleteSavedDraft: function(){
            return 'secure/api/{applicationReferenceNo}/deleteTask';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for list of clients for an application
         */
        showAllClients: function(){
            return 'secure/api/{applicationReferenceNo}/showAllClients';
        },


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for retrieving a client's portofolio
         */
        getPortfolio: function(){
            return 'secure/api/'+params[1]+'/'+params[2]+'/portfolio';
        },


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for Term deposit maturity report
         */
        tdMaturityReport: function(){
            return 'secure/api/business/tdMaturityReport';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for edit Support staff User
         */
        editSupportStaffUser: function(){
            return 'secure/api/editSupportStaffUser';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for supportstaffdetails
         */
        supportstaffdetails: function(){
            return 'secure/page/supportstaffdetails';
        },


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for pdf statement
         */
        pdfStatement   :function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/pdfStatement';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for Fua report
         */
        fuaReport: function(){
            return 'secure/api/business/fuaReport';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for manage users
         */
        manageUsers: function(){
            return 'secure/page/manageusers';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for addDealerAdviser
         */
        addDealerAdviser: function(){
            return 'secure/api/addAdviser';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for addSupportStaff
         */
        addSupportStaff: function(){
            return 'secure/api/addsupportstaff';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for inOutFlow Report
         */
        inOutFlowReport: function(){
            return 'secure/api/business/inflowsOutflowsReport';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for searchClientDetails
         */
        searchClientDetails: function(){
            return 'secure/api/searchClientDetails';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for searchAccountList
         */
        searchAccountList: function(){
            return 'secure/api/searchAccountList';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for searchAccounts
         */
        searchAccounts: function(){
            return 'secure/api/searchAccounts';
        },

        /* @returns {string} returns url for data download subscription
         */
        subscribeAllDownloads: function(){
            return 'secure/api/addDataDownloadsSubscription';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public

         * @returns {string} returns url for data download editSubscription
         */
        editSubscription: function(){
            return 'secure/api/editSubscription';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for data download delete subscription

         * @returns {string} returns url for downloadFile
         */
        downloadFile: function(){
            return 'secure/api/downloadFile.pdf';
        },

        /* @returns {string} returns url for data download delete subscription

         */
        deleteSubscription: function(){
            return 'secure/api/deleteSubscription';
        },

        /* @returns {string} returns url for data download refresh subscription

         */
        portfolioDetails: function(){
            return 'secure/api/portfolioDetails';
        },

        /* @returns {string} returns url for data download refresh subscription

         */
        refreshSubscription: function(){
            return 'secure/api/refreshSubscription';
        },

        /* @returns {string} returns url for idps term deposit report

         */
        idpsTermDepositReport: function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/idpsTransactionReport';
        },

        /* @returns {string} returns url for idps term deposit report

         */
        idpsIncomeExpenseReport: function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/incomeExpenseReport';
        },

        /* @returns {string} returns url for idps term deposit report

         */
        csvDownloadTermDepositTransactionsReport: function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/downloadTermDepositTransactionsCsv';
        },


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for income expense report pdf
         */
        incomeExpenseReportPdf:function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/incomeExpenseReport.pdf';
        },


        /* @returns {string} returns url for idps pdf term deposit report

         */
        pdfDownloadTermDepositTransactionsReport: function(){
            return 'secure/page/'+params[1]+'/'+params[2]+'/termDepositTransactions.pdf';
        },
        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {String} returns url for Term Deposit download pdf
         */
        termDepositDownloadPdf   :function(){
            return 'overview.pdf';
        },


        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for Service Ops Moved Failed status to draft
         */
        serviceOpsMoveFailedStatusToDraft: function(){
            return 'secure/api/moveFailedApplicationToDraft';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} returns url for Service Ops Search Application
         */
        serviceOpsSearchApp: function(){
            return 'secure/api/searchFailedApplication';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} submit url for failed app download
         */
        failedAppDownload: function(){
            return 'secure/page/serviceOps/downloadApplication';
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} submit url for failed app download
         */
        viewFailedApp: function(referenceNumber){
            return 'secure/page/serviceOps/applicationDetails/id/'+referenceNumber;
        },

        /**
         * @memberOf org.bt.utils.serviceDirectory
         * @public
         * @returns {string} submit url for view failed direct application details
         */
        viewFailedDirectApp: function(cisKey){
            return 'secure/page/serviceOps/applicationDetails/direct/cisKey/'+cisKey;
        }
    };
})(jQuery,window);

/**
 * Location util module
 * @namespace org.bt.utils.location
 */
org.bt.utils.location = function(){
    /**
     * @memberOf org.bt.utils.location
     * @private
     * @returns {String} returns base url for the application
     */
    var _getBaseUrl = function(){
        return location.protocol + "//" + location.hostname +
            (location.port && ":" + location.port);
    };
    return{
        /**
         * @memberOf org.bt.utils.location
         * @public
         * @returns {String} returns base url for the application
         */
        getBaseUrl: function(){
            return _getBaseUrl()+org.bt.utils.serviceDirectory.contextPath();
        },
        /**
         * @memberOf org.bt.utils.location
         * @public
         * @returns {Boolean} returns true if connection is secure
         */
        isSecure:function(){
            return (location.protocol === 'https:')
        }
    }
}();

/**
 * Communicate util module
 * @namespace org.bt.utils.communicate
 */
org.bt.utils.communicate = (function($, window) {
    /**
     * @desc Cache object
     * @type {Object}
     * @private
     */
    var _cache = {};

    var _generateUUID = function (settings) {
        var password = new Date().getTime(),
            data = _.has(settings, 'data') ? settings.data : settings.url,
            key = sjcl.codec.utf8String.toBits(password),
            out = (new sjcl.misc.hmac(key, sjcl.hash.sha256)).mac(data);

        return encodeURI(sjcl.codec.hex.fromBits(out));
    }

    var setSecurityToken = function (settings) {
        if (!/^(GET|HEAD|TRACE|OPTIONS)$/.test(settings.type) ) {
            var token = _generateUUID(settings);
            $.cookie.raw = true;
            $.cookie('securityToken', token, {
                path: '/'
            });
            $.cookie.raw = false;
            $.ajaxSetup({
                headers: { 'securityToken': token }
            });
        }
    }

    var unsetSecurityToken = function (settings) {
        $.removeCookie('securityToken', {
            path: '/'
        });

        $.ajaxSetup({
            headers: { 'securityToken': null }
        });
    }

    var onBeforeSend = function (settings) {
        setSecurityToken (settings);
    }

    /**
     * @desc ajax private method
     * @param {Object} options ajax settings
     * @private
     */
    var _ajax = function(options){
        var defaults    = {type:'GET',dataType:'json',url:'',async:true,data:{},onSuccess:function(){},onError:function(){},callback:function(){}},
            ref         = this,data,token;

        options         = $.extend(defaults, options);
        data            = options.data;

        //add token only for POST req.
        if(options.type === 'POST'){
            token = encodeURI(org.bt.token);
            if($.isArray(data)){
                //push token to array.
                data.push({name:'cssftoken',value:token});
            } else {
                data = $.extend(data,{'cssftoken':token});
            }
        }

        var isRelative  = (options.url && options.url.charAt(0) != '/');
        options.url      = (isRelative) ? org.bt.utils.location.getBaseUrl()+''+options.url : options.url;
        if(options.url !==org.bt.utils.serviceDirectory.resetPasswordStep2) {
            options.url =options.url+(options.url.indexOf('?') !== -1 ? "&" : "?")+'ts='+new Date().getTime();
        }
        if(typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
            org.bt.utils.communicate.ajaxLoaderSet(options.$form,options.ajaxLoaderSettings);
        }
        $.ajax({
            type      : options.type,
            dataType  : options.dataType,
            url       : options.url,
            data      : data,
            async     : options.async,
            beforeSend : onBeforeSend(options),
            success   : function(response, textStatus, jqXHR){
                unsetSecurityToken(options);
                if( typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
                        org.bt.utils.communicate.ajaxLoaderReset(options.$form,options.ajaxLoaderSettings);
                    }
                options.onSuccess.apply(ref,[response,options.callback]);
            },
            error     : function(event, jqXHR, ajaxSettings, thrownError){
                unsetSecurityToken(options);
                if(typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
                    org.bt.utils.communicate.ajaxLoaderReset(options.$form,options.ajaxLoaderSettings);
                }
                if (event.responseText && event.responseText.indexOf
                    && window.location.href.indexOf('/public/page/') === -1
                    && event.responseText.indexOf('id="logonPageDisplayed"') > -1) {
                    // The response is trying to show the logon page and the current page is not
                    // public so the user needs to login again.
                    // So reload the page to trigger redirecting to the logon page.
                    window.location.reload();
                } else {
                    org.bt.utils.log.warn('event', 'Problem with ajax');
                    options.onError.apply(ref, [event, jqXHR, ajaxSettings, thrownError]);
                }
            }
        });
    };

    /**
     * @desc ajax private method
     * @param {Object} options ajax settings needs to replace v1 above
     * @private
     */
    var _ajax2 = function(options){
        var defaults    = {type:'GET',dataType:'json',url:'',async:true,data:{},onSuccess:function(){},
        onError:function(event, jqXHR, ajaxSettings, thrownError){
                                if (event.responseText && event.responseText.indexOf
                                    && window.location.href.indexOf('/public/page/') === -1
                                    && event.responseText.indexOf('id="logonPageDisplayed"') > -1) {
                                    // The response is trying to show the logon page and the current page is not
                                    // public so the user needs to login again.
                                    // So reload the page to trigger redirecting to the logon page.
                                    window.location.reload();
                                } else {
                                    org.bt.utils.log.warn('event', 'Problem with ajax');
                                    options.onError.apply(ref, [event, jqXHR, ajaxSettings, thrownError]);
                                }
                            }
        ,callback:function(){}},
            ref         = this,data,token;

        options         = $.extend(defaults, options);
        data            = options.data;

        //add token only for POST req.
        if(options.type === 'POST'){
            token = encodeURI(org.bt.token);
            if($.isArray(data)){
                //push token to array.
                 data.push({name:'cssftoken',value:token});
            } else {
                data = $.extend(data,{'cssftoken':token});
            }
        }

        var isRelative  = (options.url && options.url.charAt(0) != '/');
        options.url      = (isRelative) ? org.bt.utils.location.getBaseUrl()+''+options.url : options.url;
        if(options.url !==org.bt.utils.serviceDirectory.resetPasswordStep2) {
            options.url =options.url+(options.url.indexOf('?') !== -1 ? "&" : "?")+'ts='+new Date().getTime();
        }
        $.ajax({
            type      : options.type,
            dataType  : options.dataType,
            url       : options.url,
            data      : data,
            async     : options.async,
            success   : function(response, textStatus, jqXHR){
                options.onSuccess.apply(ref,[response,options.callback]);
            },
            error     : options.onError
        });
    };

    /**
         * @desc ajax private method
         * @param {Object} options ajax settings
         * @private
         */
        var _ajax3 = function(options){
            var defaults    = {type:'GET',dataType:'json',url:'',async:true,data:{},onSuccess:function(){},onError:function(){},callback:function(){}},
                ref         = this,data,token;

            options         = $.extend(defaults, options);
            data            = options.data;

            if(typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
                org.bt.utils.communicate.ajaxLoaderSet(options.$form,options.ajaxLoaderSettings);
            }
            $.ajax({
                type      : options.type,
                dataType  : options.dataType,
                crossDomain: true,
                url       : options.url,
                data      : data,
                async     : options.async,
                success   : function(response, textStatus, jqXHR){
                    if( typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
                            org.bt.utils.communicate.ajaxLoaderReset(options.$form,options.ajaxLoaderSettings);
                        }
                    options.onSuccess.apply(ref,[response,options.callback]);
                },
                error     : function(event, jqXHR, ajaxSettings, thrownError){
                    if(typeof options.ajaxLoaderSettings !='undefined' && options.ajaxLoaderSettings.showLoader ==true){
                        org.bt.utils.communicate.ajaxLoaderReset(options.$form,options.ajaxLoaderSettings);
                    }
                    if (event.responseText && event.responseText.indexOf
                        && window.location.href.indexOf('/public/page/') === -1
                        && event.responseText.indexOf('id="logonPageDisplayed"') > -1) {
                        // The response is trying to show the logon page and the current page is not
                        // public so the user needs to login again.
                        // So reload the page to trigger redirecting to the logon page.
                        window.location.reload();
                    } else {
                        org.bt.utils.log.warn('event', 'Problem with ajax');
                        options.onError.apply(ref, [event, jqXHR, ajaxSettings, thrownError]);
                    }
                }
            });
        };

    /**
     * @desc Create request helper
     * @param {Object} form form object
     * @param {String} type req. type GET or POST
     * @private
     */
    var _createRequest = function(form,type){
        var uniqueId    = 'form-'+new Date().getTime(),
            data        = form.data || false,
            formHTML    = [],
            isRelative  = (form.action && form.action.charAt(0) != '/'),
            action      = (isRelative) ? org.bt.utils.location.getBaseUrl()+''+form.action : form.action;

        if(data){
            $.each(data,function(k,v){
                var html = '<input type="hidden" name="'+ v.name+'" value="'+ v.value+'"/>';
                formHTML.push(html);
            });
            //add the token
            if(type === 'POST'){
                formHTML.push('<input type="hidden" name="cssftoken" value="'+ org.bt.token +'"/>');
            }
        }

        $('<form>').attr({
            'accept-charset':'UTF-8',
            'target':(form.target) ? form.target :'',
            'method': type,
            'id': uniqueId,
            'data-ajax':false,
            'action': action
        }).appendTo('body').html(formHTML.join('')).submit();
    };

    /**
     * post private method
     * @param {Object} form form object
     * @private
     */
    var _post = function(form){
        _createRequest(form,'POST');
    };

    /**
     * get private method
     * @param form
     * @private
     */
    var _get = function(form){
        _createRequest(form,'GET');
    };

    /**
     * publish private method
     * @param topic
     * @param args
     * @param scope
     * @private
     */
    var _publish = function(topic, args, /** {Object=} */ scope){
        if (_cache[topic]) {
            var thisTopic = _cache[topic],
                i = thisTopic.length - 1;

            for (i ; i >= 0 ; i -= 1) {
                thisTopic[i].apply(scope || this, args || []);
            }
        }
    };

    /**
     * subscribe private method
     * @param topic
     * @param callback
     * @returns {Array}
     * @private
     */
    var _subscribe = function(topic, callback){
        if (!_cache[topic]) {
            _cache[topic] = [];
        }
        _cache[topic].push(callback);
        return [topic, callback];
    };

    /**
     * unsubscribe private method
     * @param handle
     * @param completly
     * @private
     */
    var _unsubscribe = function(handle, completly){
        var t = handle[0],
            i = _cache[t].length - 1;

        if (_cache[t]) {
            for (i ; i >= 0 ; i -= 1) {
                if (_cache[t][i] === handle[1]) {
                    _cache[t].splice(_cache[t][i], 1);
                    if (completly) {
                        delete _cache[t];
                    }
                }
            }
        }
    };

    var _ajaxLoaderReset =function($form,ajaxLoaderSettings){
        var findElement ='em.'+ajaxLoaderSettings.iconLoaderClass;
        $form.find(findElement).parents('.jq-formSubmit').removeClass('jq-disabled');
        if(ajaxLoaderSettings.replaceClass) {
                $form.find(findElement).addClass(ajaxLoaderSettings.replaceClass).removeClass(ajaxLoaderSettings.iconLoaderClass);
            } else {
                $form.find(findElement).addClass("noDisplay");
            }
    }

    var _ajaxLoaderSet =function($form,ajaxLoaderSettings){
        if(ajaxLoaderSettings.showLoader) {
                    var iconElement ='em.'+ajaxLoaderSettings.iconLoaderClass;
                    if(ajaxLoaderSettings.replaceClass) {
                        var findElement ='em.'+ajaxLoaderSettings.replaceClass;
                        $form.find(findElement).parents('.jq-formSubmit').addClass('jq-disabled');
                        $form.find(findElement).removeClass('noDisplay').removeClass(ajaxLoaderSettings.replaceClass).addClass(ajaxLoaderSettings.iconLoaderClass);
                    } else {
                        $form.find(iconElement).parents('.jq-formSubmit').addClass('jq-disabled');
                        $form.find(iconElement).removeClass("noDisplay");
                    }
                }
    }
    return {
        /**
         * @memberOf org.bt.utils.communicate
         * @public
         * @param {Object} options ajax settings
         */
        ajax:function(options){
            _ajax.apply(this, [options]);
        },
        /**
         * @memberOf org.bt.utils.communicate
         * @public
         * @param {Object} options ajax settings
         */
        ajax2:function(options){
            _ajax2.apply(this, [options]);
        },
        /**
         * @memberOf org.bt.utils.communicate
         * @public
         * @param {Object} options ajax settings
         */
        ajax3:function(options){
            _ajax3.apply(this, [options]);
        },
        /**
         * @memberOf org.bt.utils.communicate
         * @public
         * @param {Object} form object
         */
        post:function(form){
            _post(form);
        },
        /**
         * @memberOf org.bt.utils.communicate
         * @public
         * @param {Object} form object
         */
        get:function(form){
            _get(form);
        },
        /**
         *    e.g.: org.bt.utils.communicate.publish("/payee/added", [payee], this);
         *
         *    @memberOf org.bt.utils.communicate
         *    @public
         *    @param topic {String}
         *    @param args    {Array}
         *    @param {object} [scope=undefined] Optional
         */
        publish : function (topic, args, /** {Object=} */ scope) {
            _publish(topic, args, /** {Object=} */ scope);
        },
        /**
         *    e.g.: org.bt.utils.communicate.subscribe("/payee/added", payee.validate)
         *
         *    @memberOf org.bt.utils.communicate
         *    @public
         *    @param topic {String}
         *    @param callback {Function}
         *    @return {Array} Event handler {Function} callback
         */
        subscribe : function (topic, callback) {
            _subscribe(topic, callback);
        },
        /**
         *    e.g.: var handle = org.bt.utils.communicate.subscribe("/payee/added", payee.validate);
         *
         *    @memberOf org.bt.utils.communicate
         *    @public
         *    @param handle {Array}
         *    @param completly {Boolean}
         *    @return {null}
         */
        unsubscribe : function (handle, completly) {
            _unsubscribe(handle, completly);
        },
        ajaxLoaderReset:function($form,ajaxLoaderSettings){
            _ajaxLoaderReset($form,ajaxLoaderSettings);
        },
        ajaxLoaderSet:function($form,ajaxLoaderSettings){
            _ajaxLoaderSet($form,ajaxLoaderSettings);
        }
    };
})(jQuery, window);

/**
 * @description application config module
 */
org.bt.modules.applicationConfiguration = (function($,window) {
    var _setOs = function(){
        var OSName='';
        if (navigator.appVersion.indexOf('Win')!=-1) {OSName='windows';}
        if (navigator.appVersion.indexOf('Mac')!=-1) {OSName='mac';}
        if (navigator.appVersion.indexOf('X11')!=-1) {OSName='unix';}
        if (navigator.appVersion.indexOf('Linux')!=-1){OSName='linux';}
        $('body').addClass(OSName);
    };
    var _init = function(){
        _setOs();
    };
    return{
        init:function(){
            _init();
        }
    }
})(jQuery,window);

