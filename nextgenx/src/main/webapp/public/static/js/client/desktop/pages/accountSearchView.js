/**
 * @namespace org.bt.modules.accountSearchView
 */
org.bt.modules.accountSearchView = (function($,window,document,moment) {
    var _DOMElements = {
        bglStatementPeriod          :'.jq-searchApplication',
        moveFailedStatusDraft       :'.jq-moveFailedStatusDraft',
        moveStatusFailedDraftSuccess:'.jq-moveStatusFailedDraftSuccess',
        loaderIcon                  :'.jq-loaderIcon',
        bglDocumentSearchBy         :'.jq-SearchBy',
        bglDocumentSearchFor        :'.jq-SearchFor',
        roleSearch                  :'.jq-RoleSearch',
        searchForm                  :'.jq-AccountSearchForm',
        searchResultTBody           :'.jq-searchResult',
        movedStatusAjaxResponse     :'.jq-movedStatusAjaxResponse',
        viewFailedApp               :'.jq-viewFailedApp'
    },
    // removed duplicate _DOMElements.searchForm                  :'.jq-applicationSearchForm',
    _referenceNumber,_clientApplicationKey;

    var _bindDOMEvents = function(){
        //bind event for move failed status to Draft

        //submit form on enter event
        $('.jq-AccountSearchForm').keypress(function (e) {
            var c = e.which ? e.which : e.keyCode;
            if (c === 13) {
                $('.jq-formSubmit').trigger('click');
                e.preventDefault();
            }
        });

        $(_DOMElements.searchForm).validationEngine({
            ajaxSubmit          :true,
            ajaxSubmitType      :'GET',
            ajaxValidationUrl   :org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl       :null,
            dataType            :'json',
            onValidationComplete:function(form,success){
                var action;
                if(success){
                    var data = [];
                    data.push({name:'searchType',value:$(_DOMElements.bglDocumentSearchBy).val()});
                    data.push({name:'searchFor',value:$(_DOMElements.bglDocumentSearchFor).val()});
                    action = 'secure/page/serviceOps/client/accounts/'+$('.jq-AccountSearchInput').val();
                    org.bt.utils.communicate.get({action:action,data:data});
                }
            },
            ajaxLoaderSettings 	:{
                showLoader:true,
                replaceClass:'iconsearch',
                iconLoaderClass:'iconLoader'
            },
            onSubmitSuccess     :function(response){
                _onSearchAppSubmitSuccess(response);
            },
            onSubmitError       :_onSearchAppSubmitError,
            customFunctions:{
                'searchCriteria':function(rules,value){
                    var matched = true;
                    $('.jq-NotFoundMsg').addClass('noDisplay');
                    switch ($(_DOMElements.bglDocumentSearchBy).val()) {
                        case "bpNumber":
                        case "gcmId":
                            matched = /^\d+$/.test(value);
                            break;
                        case "name":
                            matched = /^[A-z0-9\s]+$/.test(value); ///^[a-zA-Z\s]*$/
                            break;
                    }
                    rules.push({'name':'customFunction','matched':matched});
                    return rules;
                }
            }
        });

        /**
         * Home page only
         * @desc navigate user to detail page
         */
        $(_DOMElements.searchResultTBody).on('click','tr',function(event){
            var $tr = $(this);
            _loadDocumentsForSelected($tr.attr('data-id'),$tr.attr('data-info'), 'ACCT');
        });


        $('.jq-clientSearchResult').on('click','tr',function(event){
            var $tr = $(this);
            var relationShipType = 'ACCT';

            if($(_DOMElements.bglDocumentSearchFor).val() === 'Person'){
                relationShipType = 'CUST';
                _loadDocumentsForSelected($tr.attr('data-id'), $tr.attr('data-info'), relationShipType);
            } else {
                _loadAccountsForClient($tr.attr('data-id'));
            }
        });

    };

    var _loadAccountsForClient = function(gcmId){
        var data = [];
        data.push({name:'searchType',value:'gcmId'});
        data.push({name:'searchFor',value:'Account'});
        action = 'secure/page/serviceOps/client/accounts/'+gcmId;
        org.bt.utils.communicate.get({action:action,data:data});
    };

    /**
     * @desc navigate to record page
     * @private
     */
    var _loadDocumentsForSelected = function(accountNo, accountName, type){
        var data = [];
        data.push({name:'accountNumber',value:accountNo});
        data.push({name:'name',value:accountName});
        data.push({name:'relationshipType',value:type});

        var action = 'secure/page/serviceOps/account/'+accountNo+'/documents';
        org.bt.utils.communicate.get({action:action, data:data});
    };

    var _clearErrorMsgs = function(){
        //Remove errors if any
        $.removeMessage($('.jq-applicationId'),['jq-inputError','formFieldMessageError'],'textInputError');
        $.removeMessage($('.jq-accountNumber'),['jq-inputError','formFieldMessageError'],'textInputError');
    };

    var _onSearchAppSubmitSuccess =function(response) {
        if(response.status===1 && _.isNull(response.data)){
            $(_DOMElements.moveStatusFailedDraftSuccess).hide();
        }
        else {
            _showApplicationActiveClientsSuccess(response);
            $(_DOMElements.moveStatusFailedDraftSuccess).hide();
        }
    };

    var _onSearchAppSubmitError =function(response) {
        $(_DOMElements.moveStatusFailedDraftSuccess).hide();
    };

    var _initSearchByDropdown = function(){
        $(_DOMElements.bglDocumentSearchBy).dropkick({
            startSpeed: 0,
            change: function (value, label) {
                $(this).change();
                $(".jq-AccountSearchInput").val('');
                $(".jq-AccountSearchInput").attr("placeholder", "Enter "+label);
            },
            inputClasses: ["selectDropDown"]
        });
    };

    var _initSearchForDropdown = function(){
        $(_DOMElements.bglDocumentSearchFor).dropkick({
            startSpeed: 0,
            change: function (value, label) {
                $(this).change();
                var options = [];
                /*clear View by drop downs*/
                $(_DOMElements.bglDocumentSearchBy).removeData("dropkick");
                $(_DOMElements.bglDocumentSearchBy).find('option').remove();
                $("#selectContainer_searchBy").remove();

                if(value == 'Person'){

                    options.push('<option value=\'gcmId\'>Investor GCM ID</option>');
                    options.push('<option value=\'name\'>Investor Name</option>');
                    _attachOptionsAndReload(options);
                    $(_DOMElements.bglDocumentSearchBy).dropkick('setValue','gcmId');

                } else {

                    options.push('<option value=\'bpNumber\'>Account Number</option>');
                    options.push('<option value=\'gcmId\'>Investor GCM ID</option>');
                    options.push('<option value=\'name\'>Investor Name</option>');
                    _attachOptionsAndReload(options);
                    $(_DOMElements.bglDocumentSearchBy).dropkick('setValue','bpNumber');
                }
            },
            inputClasses: ["selectDropDown"]
        });
    };

    var _attachOptionsAndReload = function (viewByOptions) {
        var viewByOptionsHtml = viewByOptions.join(' ');
        $(_DOMElements.bglDocumentSearchBy).html(viewByOptionsHtml);
        $(_DOMElements.bglDocumentSearchBy).dropkick('reload',
            {startSpeed: 0,
            inputClasses: [" selectDropDown "],
            container:$('#selectContainer_searchBy'),
            change: function (value, label) {
                $(".jq-AccountSearchInput").attr("placeholder", "Enter "+label);
            }
        });
    };

    var _buildOptionsSearchBy = function (items) {
        $.each(items, function (i, item) {
            $(_DOMElements.bglDocumentSearchBy).append($('<option>', {
                value: item.value,
                text : item.text
            }));
        });
    };

    var _attachOptionsToDropDown = function () {
        var items =  [ {text:'Account Number', value:'bpNumber'}, {text:'Investor GCM ID', value:'gcmId'}, {text:'Investor Name', value:'name'}];
        _buildOptionsSearchBy(items);
    };

    var _populateValues = function () {
        if(!_.isEmpty($('#searchForCriteria').val())) {
            $(_DOMElements.bglDocumentSearchFor).dropkick('setValue',$('#searchForCriteria').val());
        }
        var searchBy = $('#searchByCriteria').val();
        if(searchBy !== undefined && !_.isEmpty(searchBy)){
            $(_DOMElements.bglDocumentSearchBy).dropkick('setValue', searchBy);
        }
        $("#selectContainer_roleSearch").addClass('disable-dropdown');

    };

    var _initRoleSearchDropdown = function(){
        $(_DOMElements.roleSearch).dropkick({
            startSpeed: 0,
            change: function (value, label) {
                $(this).change();
            },
            inputClasses: ["selectDropDown"]
        });
    };


    var _init = function(){
        _attachOptionsToDropDown();
        _initSearchForDropdown();
        _initSearchByDropdown();
        _initRoleSearchDropdown();
        $(_DOMElements.bglDocumentSearchBy).dropkick('reset');
        $(_DOMElements.bglDocumentSearchFor).dropkick('reset');
        $(_DOMElements.roleSearch).dropkick('reset');
        _populateValues();
    };

    return{
        init:function(){
            _init();
            _bindDOMEvents();
        }
    };
})(jQuery,window, document, moment);

