/**
 * @namespace org.bt.modules.idpsSnapshot
 */
org.bt.modules.idpsSnapshot = (function ($, window, document, moment) {
    var _DOMElements = {
            bglStatementPeriod: '.jq-searchApplication',
            directClientApplication: '.jq-directClientApplication',
            searchForm: '.jq-applicationSearchForm',
            directSearchForm: '.jq-directApplicationSearchForm',
            searchAppTable: '.jq-searchAppTable',
            searchAppNoResults: '.jq-searchAppNoResults',
            moveFailedStatusDraft: '.jq-moveFailedStatusDraft',
            moveStatusFailedDraftSuccess: '.jq-moveStatusFailedDraftSuccess',
            loaderIcon: '.jq-loaderIcon',
            movedStatusAjaxResponse: '.jq-movedStatusAjaxResponse',
            directAdvisedTabs: '.toggleMenuLineWrap',
            moveStatusToDraftLink: 'jq-moveStatusToDraft'
        },
        _referenceNumber, _clientApplicationKey;
    currentTab = 'advisedInvestors';
    var accountTypes = {
        'individual': 'BT Invest',
        'superAccumulation': 'Super Accumulation',
        'superPension': 'Super Pension'
    };

    var _bindDOMEvents = function () {
        _advisedApplicationsDomEvents();
        _directApplicationDomEvents();
    };

    var _advisedApplicationsDomEvents = function () {

        $(document).on('click', _DOMElements.moveFailedStatusDraft, _onClickMoveFailedStatusDraft);
        //submit form on enter event
        $('.jq-applicationSearchForm').keypress(function (e) {
            var c = e.which ? e.which : e.keyCode;
            if (c === 13) {
                $('.jq-formSubmit').trigger('click');
                e.preventDefault();
            }
        });

        $(_DOMElements.searchForm).validationEngine({
            ajaxSubmit: true,
            ajaxSubmitType: 'GET',
            ajaxValidationUrl: org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl: null,
            dataType: 'json',
            onValidationComplete: function (form, success) {
                var action, data = {};
                data.status = {
                    name: 'status',
                    value: 'active'
                };
                if ($('.jq-searchApplication').val() === 'Approved Application' && success) {
                    action = 'secure/page/serviceOps/applicationDetails/account/' + $('.jq-accountNumber').val();
                    org.bt.utils.communicate.get({action: action, data: data});
                }
            },
            ajaxLoaderSettings: {
                showLoader: true,
                replaceClass: 'iconsearch',
                iconLoaderClass: 'iconLoader'
            },
            onSubmitSuccess: function (response) {
                _onSearchAppSubmitSuccess(response);
            },
            onSubmitError: _onSearchAppSubmitError,
            customFunctions: {
                'applicationId': function (rules, value) {
                    var minMaxCheckArr = value.slice(1);
                    var firstCharCheck = value.slice(0, 1);
                    if (firstCharCheck[0] == 'R' || firstCharCheck[0] == 'r') {
                        if (minMaxCheckArr.length < 9) {
                            rules.push({'name': 'customMinLength', 'matched': false});
                        }
                        else if (minMaxCheckArr.length > 9) {
                            rules.push({'name': 'customMaxLength', 'matched': false});
                        }
                        matched = /^\d+$/.test(minMaxCheckArr);
                        rules.push({'name': 'unsignedInteger', 'matched': matched});
                        if (matched) {
                            if (!(minMaxCheckArr > 0)) {
                                rules.push({'name': 'allZero', 'matched': false});
                            }
                        }
                    }
                    else {
                        rules.push({'name': 'firstCharNotR', 'matched': false});
                    }
                    return rules;
                },
                'accountNumber': function (rules, value) {
                    matched = /^\d+$/.test(value);
                    rules.push({'name': 'unsignedInteger', 'matched': matched});
                    return rules;
                }
            }
        });
    };

    var _directApplicationDomEvents = function () {

        //submit form on enter event
        $('.jq-directApplicationSearchForm').keypress(function (e) {
            var c = e.which ? e.which : e.keyCode;
            if (c === 13) {
                $('.jq-formSubmit').trigger('click');
                e.preventDefault();
            }
        });

        $(_DOMElements.directSearchForm).validationEngine({
            ajaxSubmit: true,
            ajaxSubmitType: 'GET',
            ajaxValidationUrl: org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl: null,
            dataType: 'json',
            ajaxLoaderSettings: {
                showLoader: true,
                replaceClass: 'iconsearch',
                iconLoaderClass: 'iconLoader'
            },
            onSubmitSuccess: function (response) {
                _onSearchAppSubmitSuccess(response);
            },
            onSubmitError: _onSearchAppSubmitError,
            customFunctions: {
                'cisKey': function (rules, value) {
                    if (value.length < 11) {
                        rules.push({'name': 'customMinLength', 'matched': false});
                    }
                    else if (value.length > 11) {
                        rules.push({'name': 'customMaxLength', 'matched': false});
                    }
                    matched = /^\d+$/.test(value);
                    rules.push({'name': 'unsignedInteger', 'matched': matched});
                    if (matched) {
                        if (!(value > 0)) {
                            rules.push({'name': 'allZero', 'matched': false});
                        }
                    }
                    return rules;
                }
            }
        });
    }


    var _initStatementDropdown = function () {
        $(_DOMElements.bglStatementPeriod).dropkick({
            change: function (value, label) {
                $(this).change();
                _onApplicationTypeChange(value, label);
            },
            inputClasses: ["inputStyleFive"]
        });
    };

    var _initStatementDropdownDirect = function () {
        $(_DOMElements.directClientApplication).dropkick({
            change: function (value, label) {
                $(this).change();
                _onApplicationTypeChangeDirect(value, label);
            },
            inputClasses: ["inputStyleFive"]
        });
    };

    var _onApplicationTypeChange = function (value) {
        _clearErrorMsgs();
        if (value === 'Approved Application') {
            $('.jq-searchAppNoResults .caption').html('Approved Applications');
            $('.jq-searchAppTable .caption').html('Approved Applications');
            $('.jq-accountNumber').removeAttr("disabled").removeClass("jq-skip noDisplay").removeClass("jq-validField");
            $('.jq-applicationId').attr("disabled", "disabled").addClass("jq-skip noDisplay");
            $('.jq-applicationSearchForm').attr('data-ajax-submit-url', '');
            $('.jq-accountNumber').val('');
        } else {
            $('.jq-searchAppNoResults .caption').html('Failed Applications');
            $('.jq-searchAppTable .caption').html('Failed Applications');
            $('.jq-applicationId').removeAttr("disabled").removeClass("jq-skip noDisplay").removeClass("jq-validField");
            $('.jq-accountNumber').attr("disabled", "disabled").addClass("jq-skip noDisplay");
            $('.jq-applicationSearchForm').attr('data-ajax-submit-url', 'secure/api/searchFailedApplication');
            $('.jq-applicationId').val('');
        }

    };

    var _onApplicationTypeChangeDirect = function (value) {
        _clearErrorMsgs();
        if (value === 'Approved Application') {
            $('.jq-searchAppNoResults .caption').html('Approved Applications');
            $('.jq-searchAppTable .caption').html('Approved Applications');
            $('.jq-directApplicationSearchForm').attr('data-ajax-submit-url', 'secure/api/directApprovedApplications');
        } else {
            $('.jq-searchAppNoResults .caption').html('Failed Applications');
            $('.jq-searchAppTable .caption').html('Failed Applications');
            $('.jq-directApplicationSearchForm').attr('data-ajax-submit-url', 'secure/api/searchFailedDirectApplication');
        }
        $('.jq-cisKey').val('');
    };

    var _onClickMoveFailedStatusDraft = function (e) {
        e.preventDefault();
        var $element = $(e.target);
        $(_DOMElements.loaderIcon).removeClass('noDisplay');
        org.bt.utils.communicate.ajax.apply($element, [{
            'url': org.bt.utils.serviceDirectory.serviceOpsMoveFailedStatusToDraft(),
            'data': {'applicationId': _referenceNumber, 'clientApplicationKey': _clientApplicationKey},
            'type': 'GET',
            'onSuccess': _onMoveFailedStatusDraftSuccess,
            'onError': _onMoveFailedStatusDraftError
        }]);
    };

    var _clearErrorMsgs = function () {
        //Remove errors if any
        $.removeMessage($('.jq-applicationId'), ['jq-inputError', 'formFieldMessageError'], 'textInputError');
        $.removeMessage($('.jq-accountNumber'), ['jq-inputError', 'formFieldMessageError'], 'textInputError');
        $.removeMessage($('.jq-cisKey'), ['jq-inputError', 'formFieldMessageError'], 'textInputError');
        _init();
    };

    var _onMoveFailedStatusDraftSuccess = function (response) {
        if (response.status !== 1) {
            $(_DOMElements.movedStatusAjaxResponse).html("Some error occured.Please search and try again.");
        }
        $(_DOMElements.searchAppTable).hide();
        $(_DOMElements.searchAppNoResults).hide();
        $(_DOMElements.moveStatusFailedDraftSuccess).show();
    };

    var _onMoveFailedStatusDraftError = function () {
        console.log("This is error");
    };

    var _onSearchAppSubmitSuccess = function (response) {
        if (response.status === 1 && _.isNull(response.data.resultList)) {
            $(_DOMElements.searchAppNoResults).show();
            $(_DOMElements.searchAppTable).hide();
            $(_DOMElements.moveStatusFailedDraftSuccess).hide();
        }
        else {
            _showApplicationActiveClientsSuccess(response);
            $(_DOMElements.searchAppNoResults).hide();
            $(_DOMElements.moveStatusFailedDraftSuccess).hide();
            $(_DOMElements.searchAppTable).show();

        }
    };

    var _onSearchAppSubmitError = function (response) {
        $(_DOMElements.searchAppNoResults).show();
        $(_DOMElements.searchAppTable).hide();
        $(_DOMElements.moveStatusFailedDraftSuccess).hide();
    };

    /**
     *   Add the the rows from the response with Handle Bars template
     */
    var _showApplicationActiveClientsSuccess = function (res) {

        var clientsTbl = $(_DOMElements.searchAppTable),
            tmplId = clientsTbl.attr('data-row-tmpl'),
            viewData = {clients: []},
            tmpl, compiledTmpl;
       
        clientsTbl.find('tbody').html('');
        if (res.status === 1) {
            _.each(res.data.resultList, function (clientApplicationRecord) {
                _referenceNumber = clientApplicationRecord.referenceNumber;
                _clientApplicationKey = clientApplicationRecord.key.clientApplicationKey;
                clientApplicationRecord.lastModifiedTimeSpan = moment(clientApplicationRecord.lastModified, 'YYYY-MM-DDTHH:mm:ss.SSSSZ')
                    .from(moment(res.lastUpdatedTime, 'YYYY-MM-DDTHH:mm:ss.SSSSZ'));
                clientApplicationRecord.lastModified = moment(clientApplicationRecord.lastModified, 'YYYY-MM-DDTHH:mm:ss.SSSSZ').format('DD MMM YYYY');
                if (currentTab === 'directInvestors') {
                    clientApplicationRecord.directInvestors = true;
                    clientApplicationRecord.accountType = accountTypes[clientApplicationRecord.accountType];
                }
                if(clientApplicationRecord.status === 'Failed'){
                    clientApplicationRecord.viewLink = 'applicationDetails/id/' + clientApplicationRecord.referenceNumber;
                } else if(clientApplicationRecord.status === 'Approved' && currentTab === 'directInvestors') {
                    clientApplicationRecord.viewLink = 'directApplicationDetails/account/' + clientApplicationRecord.accountNumber;
                }
                viewData.clients.push(clientApplicationRecord);
            });
            tmpl = $(tmplId).html();
            compiledTmpl = Handlebars.compile(tmpl);
            clientsTbl.find('tbody').append(compiledTmpl(viewData));
        }
    };

    /**
     * Init tabs
     * @private
     */
    var _initTabs = function () {
        var $directAdvisedTabs = $(_DOMElements.directAdvisedTabs);
        if (!window.location.hash) {
            window.location.hash = '#jq-advisedInvestors'
        } else {
            _toggleCurrentTab(window.location.hash)
        }

        $directAdvisedTabs.tabs({
            select: function (event, ui) {
                var hash = ui.tab.hash;
                window.location.hash = hash;
                $(ui.tab).focus();
                _toggleCurrentTab(hash)
                $('.dk_options > li:nth-child(2) > a').trigger("click");
                $('.dk_options > li:nth-child(1) > a').trigger("click");

                _init();
                console.log("select " + currentTab);
            }
        });

         _initFormData();
    };

    var _toggleCurrentTab = function (hash) {
        if (hash == '#jq-advisedInvestors') {
            currentTab = 'advisedInvestors';
            $('.jq-columnHeaderAcctName').text('Account name / Application ref no.')
        } else {
            $('.jq-columnHeaderAcctName').text('Account name')
            currentTab = 'directInvestors';
        }
    }

    var _init = function () {
        $(_DOMElements.searchAppTable).hide();
        $(_DOMElements.searchAppNoResults).hide();
        $(_DOMElements.moveStatusFailedDraftSuccess).hide();
    };

    var _initFormData = function () {
        var hash = window.location.hash.replace('#', '');
        var dropDownState;
        switch (hash) {
            case 'jq-advisedInvestors':
                dropDownState = $('#directClientApplication').val();
                _onApplicationTypeChange(dropDownState);
            case 'jq-directInvestors':
                dropDownState = $('#clientApplication').val();
                _onApplicationTypeChangeDirect(dropDownState);
        }

    }

    window.addEventListener("hashchange", _initFormData, false);

    return {
        init: function () {
            _init();
            _initTabs();
            _bindDOMEvents();
            _initStatementDropdown();
            _initStatementDropdownDirect();
            $(_DOMElements.bglStatementPeriod).dropkick('reset');
            $(_DOMElements.directClientApplication).dropkick('reset');
        }
    };
})(jQuery, window, document, moment);

