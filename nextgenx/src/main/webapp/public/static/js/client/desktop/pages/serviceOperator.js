/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.serviceOperator = (function($,window,document) {
    /**
     * @desc auto-complete cache
     * @private
     */
    var _autoCompleteCache = {};

    /**
     * @desc flag to set auto-complete cache
     * @type {boolean}
     * @private
     */
    var _cacheResults = false;

    /**
     * @desc DOM elements selector map
     * @private
     */
    var _DOMElements = {
        tabsWrap                    :'.jq-serviceHomeTabWrap',
        searchForm                  :'.jq-intermediariesAndClientsSearchForm',
        searchFormSubmitBtn         :'.jq-formSubmit',
        searchInput                 :'.jq-intermediariesAndClientsSearchInput',
        selectionInput              :'.jq-IntermediariesAndClientsSearchSelection',
        searchResultTBody           :'.jq-searchResult',
        detailPageId                :'.jq-serviceOperatorDetailPage',
        actionSelectElement         :'.jq-serviceOperatorSelectAction',
        detailPageWrap              :'.jq-serviceDetailPageWrap',
        detailPageBackLink          :'.jq-detailPageBackLink',
        detailPageActionPerformed   :'.jq-serviceOperatorDetailActionPerformed',
        detailPageActionButton      :'.jq-detailPageActionSubmitButton',
        detailPageConfirmMobileModal:'.jq-confirmMobileNumberModal',
        detailPageActionForm        :'.jq-clientDetailActionForm',
        detailPageConfirmMobileForm :'.jq-confirmMobileNumberForm',
        detailPageDefaultAction     :'.jq-serviceOperatorDetailDefaultAction',
        actionStatus                :'.jq-actionStatus',
        actionStatusMessage         :'.jq-actionStatusMessage',
        searchResultMessage         :'.jq-searchResultMessage',
        detailPageUpdatePPID        :'.jq-updatePPID',
        detailPageUpdatePPIDModal   :'.jq-updatePPIDModal',
        detailPageUpdateStatementPrefModal :'.jq-updateStatementPrefModal',
        detailPageUpdateStatementPref:'.jq-updateStatementPref'
    };

    var _templates = {
        autoComplete:'#jq-autoCompleteTemplate'
    };
    /**
     * Bind DOM Events
     * @private
     */
    var _bindDOMEvents = function(){
        /**
         * @desc bind auto complete for biller code
         */
        var $searchInput = $(_DOMElements.searchInput);

/* Autocomplete on search was removed to reduce workload on backend */
/*        if($searchInput.length > 0){
            $searchInput.autocomplete({
                minLength: 2,
                source: function( request, response ) {
                    _autocompleteSource.apply(this, [request, response]);
                },
                select: function( event, ui ) {
                    _onAutocompleteSelect(event, ui);
                }
            }).data('autocomplete')._renderItem = _renderItem;
*/
            $searchInput.bind('keyup',function(ev){
                _onSearchInputKeyUp(ev);
            });
//        }

        /*
         * init validation engine
         */
        $(_DOMElements.searchForm).validationEngine({
            ajaxSubmit:false,
            escapeHtml:false,
            onValidationComplete:function(form,success){
                _onSearchValidationComplete(form,success)
            },
            customFunctions:{
                'searchCriteria':_onSearchCriteriaFieldValidationComplete
            }
        }).submit(function(event) {
                event.stopPropagation();
                event.preventDefault();
            });


        /**
         * Home page only
         * @desc navigate user to detail page
         */
        $(_DOMElements.searchResultTBody).on('click','tr',function(event){
            var $tr = $(this);

            _navigateToRecordPage($tr.attr('data-id'),$tr.attr('data-type'));
        });

        /*
         * Detail Page only
         * init validation engine for detail page action form
         */
        $(_DOMElements.detailPageActionForm).validationEngine({
            ajaxSubmit:false
        });

        /**
         * Detail Page only
         * init validation engine for confirm mobile number form
         */
        $(_DOMElements.detailPageConfirmMobileForm).validationEngine({
            ajaxSubmit:false
        });


         /** Update PPID form */
         $(_DOMElements.detailPageUpdatePPID).validationEngine({
                     ajaxSubmit:false
         });

        /**
         * Detail Page only
         * @desc init select drop-down
         */
        $(_DOMElements.actionSelectElement).dropkick({
            inputClasses:['inputStyleEight'],
            change: function (value, label) {
                _onActionSelectChange(value,label);
            }
        });

        /**
         * Deprecated
         * Detail page only
         * @desc back button
         */
        $(_DOMElements.detailPageWrap).on('click',_DOMElements.detailPageBackLink,function(event){
            _onDetailsPageBackButtonClick.call(this,event);
        });
    }; //End Bind DOM events

    /**
     * Fires when on select change
     * @param value
     * @param label
     * @private
     */
    var _onActionSelectChange = function(value,label){
       var $btn        = $(_DOMElements.detailPageActionButton);

        $btn.show();
    };

    /**
     * @desc fires on search form validation complete
     * @param form
     * @param success
     * @private
     */
    var _onSearchValidationComplete = function(form,success){
        if(success){
            var selectedTab =  $(_DOMElements.tabsWrap).tabs('option', 'active'),
                filter      = selectedTab === 0 ? '#jq-intermediariesSearch' : (selectedTab === 1 ? '#jq-clientsSearch' : '#jq-accountsSearch');

            $(_DOMElements.selectionInput).val(filter);

            var config = {
                data:$.createFormSubmitRequestBody(form),
                action:document.location.pathname
            };

            org.bt.utils.communicate.get(config);
        }
    };

    /**
     * @desc fires on search criteria text field validation complete
     * @private
     */
    var _onSearchCriteriaFieldValidationComplete = function(rules,value,isStillValid){
        var selectedTab =  $(_DOMElements.tabsWrap).tabs('option', 'active'),
            filter      =  selectedTab === 0 ? '#jq-intermediariesSearch' : (selectedTab === 1 ? '#jq-clientsSearch' : '#jq-accountsSearch');

        //reset tab content as requested by
        if(!isStillValid){
            $(filter).html('');
        }
        return rules;
    };

    /**
     * @desc fires on search input keyup
     * @param event
     * @private
     */
    var _onSearchInputKeyUp = function(event){
        var code = event.which,
            $submitBtn;

        if(code == 13){
            $submitBtn = $(_DOMElements.searchFormSubmitBtn);
            $submitBtn.focus();
            //trigger click after a delay.
            setTimeout(function(){
                $submitBtn.trigger('click');
            },100);
        }
    };

    /**
     * @desc trigger on option select
     * @param ev as event
     * @param ui
     * @private
     */
    var _onAutocompleteSelect = function(ev,ui){
        var $input      = $(_DOMElements.searchInput),
            hasTabs     = ($(_DOMElements.tabsWrap).length > 0),
            selectedTab,filter;

        $.removeMessage($input,['jq-inputError','formFieldMessageError'],'textInputError');

        if(hasTabs){
            selectedTab = $(_DOMElements.tabsWrap).tabs('option', 'selected');
            filter      = selectedTab === 0 ? '#jq-intermediariesSearch' : (selectedTab === 1 ? '#jq-clientsSearch' : '#jq-accountsSearch');
        }
        _navigateToRecordPage(ui.item.encodedId,ui.item.type);
    };

    /**
     *
     * @param ul
     * @param item
     * @returns {object}
     * @private
     */
    var _renderItem = function(ul, item){
        return $('<li></li>')
            .addClass('resultItem')
            .data('item.autocomplete', item)
            .append(_createItem(item))
            .appendTo(ul);
    };

    var _createItem = function(data){
        var tmpl            = $(_templates.autoComplete).html(),
            compiledTmpl    = Handlebars.compile(tmpl);

        return compiledTmpl(data);
    };
    /**
     * @desc Provide source to client search
     * @param {Object} request
     * @param {function} response
     * @private
     */
    var _autocompleteSource = function(request, response){
        var term = request.term;

        if ( _cacheResults && term in _autoCompleteCache ) {
            //response is jquery ui autocomplete callback
            response( _autoCompleteCache[ term ] );
            return;
        }
        var config = {
            url:org.bt.utils.serviceDirectory.searchClients,
            onSuccess:_onAutoCompleteSuccess,
            onError:_onAutoCompleteError,
            data:{'searchCriteria':term},
            callback:response
        };
        org.bt.utils.communicate.ajax.apply(this,[config]);
    };

    /**
     * @desc Fired on auto-complete ajax req success
     * @param {Object} res JSON response
     * @param {Function} callback callback function
     * @private
     */
    var _onAutoCompleteSuccess = function(res,callback){
        var $element = this.element,
            type     = $element.parents('form:first').siblings('.jq-tabContainer:visible').attr('data-type');

        if(res.success){
            var data = [];
            if(type == 'clients'){
                $.each(res.data.clients,function(k,v){
                    data.push({
                        'id'        : v.userId,
                        'encodedId' : v.clientId,
                        'lastName'  : v.lastName,
                        'firstName' : v.firstName,
                        'label'     : v.firstName+' '+v.lastName,
                        'value'     : v.firstName+' '+v.lastName,
                        'city'      : v.city,
                        'state'     : v.state,
                        'type'      :'client'
                    });
                });
            } else if(type == 'intermediaries'){
                $.each(res.data.intermediaries,function(k,v){
                    data.push({
                        'id'        : v.userId,
                        'encodedId' : v.clientId,
                        'lastName'  : v.lastName,
                        'firstName' : v.firstName,
                        'label'     : v.firstName+' '+v.lastName,
                        'value'     : v.firstName+' '+v.lastName,
                        'city'      : v.city,
                        'state'     : v.state,
                        'type'      :'intermediary'
                    });
                });
            }
            callback(data);
        } else {
            //trigger validation
            $element.trigger('focusout');
        }
    };

    /**
     * @desc Fired on auto-complete ajax req failed
     * @private
     */
    var _onAutoCompleteError = function(){

    };

    /**
     * @desc navigate to record page
     * @private
     */
    var _navigateToRecordPage = function(clientId,type){
        var isDetailPage = ($(_DOMElements.detailPageId).length > 0);

        if (type === 'account') {
            window.location.href = (isDetailPage) ? '../'+clientId+'/accountDetail' : clientId+'/accountDetail';
        } else {
            window.location.href = (isDetailPage) ? '../'+clientId+'/detail' : clientId+'/detail';
        }
    };

    /**
     * Init tabs
     * @private
     */
    var _initTabs = function(){
        var $tabsWrap = $(_DOMElements.tabsWrap);

        if($tabsWrap.length > 0){
            $tabsWrap.tabs().find('.ui-tabs-anchor').removeAttr('tabindex');
            $tabsWrap.find('li[role="tab"]').removeAttr('tabindex');

            $tabsWrap.bind('tabsselect', function(event, ui) {
				var currentSearchInput=$(_DOMElements.searchInput).val();
				if (ui.tab.hash.indexOf('jq-accountsSearch') !== -1) {
				    $(_DOMElements.searchInput).attr("data-min-length", 7);
				    $(_DOMElements.searchInput).attr("data-validation", "validate[required,custom[searchText],custom[minLength],custom[customFunction]]");
				    $(_DOMElements.searchInput).attr("data-validation-minLength-error","Please enter at least 7 characters before clicking the search button");
				    $(_DOMElements.searchInput).attr("data-validation-required-error","Please enter at least 7 characters before clicking the search button");
				    $(_DOMElements.searchInput).attr("data-validation-unsignedInteger-error","Please use only numbers");
				} else {
				    $(_DOMElements.searchInput).attr("data-min-length", 2);
                    $(_DOMElements.searchInput).attr("data-validation", "validate[required,custom[searchText],custom[minLength],custom[customFunction]]");
                    $(_DOMElements.searchInput).attr("data-validation-minLength-error","Please enter at least 2 characters before clicking the search button");
                    $(_DOMElements.searchInput).attr("data-validation-required-error","Please enter at least 2 characters before clicking the search button");
                    $(_DOMElements.searchInput).attr("data-validation-searchText-error","Please use only letters or numbers");
				}
				setTimeout(function(){$tabsWrap.find('li[role="tab"]').removeAttr('tabindex');},100);
                window.location.hash = ui.tab.hash;
                //$.removeMessage($(_DOMElements.searchInput),['jq-inputError','formFieldMessageError'],'textInputError');
				//to fix submit button issue after changing tab
				$(_DOMElements.searchInput).val('');
				$(_DOMElements.searchInput).val(currentSearchInput);
				$(_DOMElements.searchInput).focus();
            });
        }
    };

    /**
     * @deprecated
     * @desc helper to set selected tab index.
     * @param filter
     * @private
     */
    var _setUserSelectedTab = function(filter){
        var $selection = $(_DOMElements.selectionInput);

        $selection.val(filter);
    };

    /**
     * @deprecated
     * @desc update hash util function.
     * @param filter
     * @param searchCriteria
     * @private
     */
    var _updateHash = function(filter,searchCriteria){
        $.address.autoUpdate(false)
            .value('#')
            .parameter('searchCriteria', searchCriteria)
            .parameter('filter', filter)
            .autoUpdate(true)
            .update();
    };

    /**
     * @fires on load and set default values
     * @private
     */
    var _setDefaultValues = function(){
        var href            = window.location.href,
            isDetailPage    = ($(_DOMElements.detailPageId).length > 0),
            actionPerformed = $(_DOMElements.detailPageActionPerformed),
            defaultAction   = $(_DOMElements.detailPageDefaultAction);

        if(href.indexOf('jq-clientsSearch') !== -1){
            $(_DOMElements.tabsWrap).tabs( 'select' , 1);

        }

        if(href.indexOf('jq-accountsSearch') !== -1){
            $(_DOMElements.tabsWrap).tabs( 'select' , 2);

        }

        //detail page only.
        if(isDetailPage){
            //user performed a action
            if(actionPerformed.val() !== ''){
                $(_DOMElements.actionSelectElement).val(actionPerformed.val());
                $(_DOMElements.detailPageActionButton).hide();

                //reload the page
                if(actionPerformed.val() == 'CREATE_ACCOUNT') {
                    setTimeout(function(){window.location.reload();},5000);
                }
            } else {
                //user has not performed a action. therefore set default action
                if(defaultAction.val() !== ''){
                    //try to find the default option
                    if($(_DOMElements.actionSelectElement).find('option[value="'+defaultAction.val()+'"]').length > 0){
                        $(_DOMElements.actionSelectElement).val(defaultAction.val());
                    } else {
                        //fallback to first option
                        $(_DOMElements.actionSelectElement).find('option:first').attr('selected','selected');
                    }
                }
            }
        }
    };

    /**
     * @deprecated
     * Details page only
     * @desc set default settings for detail page
     * @private
     */
    var _setDetailPageDefaults = function(){
        var page        = $(_DOMElements.actionSelectElement),
            isDetailPage= (page.length > 0);

        if(isDetailPage){
            $(_DOMElements.searchForm).attr('action','../home');
        }
    };

    /**
     * Details page only
     * @desc fires on details page back button click
     * @private
     */
    var _onDetailsPageBackButtonClick = function(event){
        event.preventDefault();
        history.go(-1);
    };

    /**
     *
     * @desc init confirm mobile number modal
     * @private
     */
    var _initConfirmMobileModal = function(){
        var $dialogWindowElement    = $(_DOMElements.detailPageConfirmMobileModal);

        $dialogWindowElement.dialog({
            width:580,
            height:'auto',
            modal: true,
            autoOpen: false,
            draggable:false,
            dialogClass: 'modalBox',
            resizable: true,
            title:'',
            appendTo:'.layoutContent'
        }).removeClass('jq-cloak').end()
            .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
            .find('.ui-dialog-titlebar-close').addClass('modalClose');

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });
    };

    /**
         *
         * @desc init confirm mobile number modal
         * @private
         */
        var _initUpdatePPIDModal = function(){
            var $dialogWindowElement    = $(_DOMElements.detailPageUpdatePPIDModal);

            $dialogWindowElement.dialog({
                width:580,
                height:'auto',
                modal: true,
                autoOpen: false,
                draggable:false,
                dialogClass: 'modalBox modalBoxPPID',
                resizable: true,
                title:'',
                appendTo:'.layoutContent'
            }).removeClass('jq-cloak').end()
                .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
                .find('.ui-dialog-titlebar-close').addClass('modalClose');

            $(window).resize(function() {
                $dialogWindowElement.dialog('option', 'position', 'center');
            });

            /*$('.jq-confirmPaymentCancelButton').click(function(e){
                e.preventDefault();
                $dialogWindowElement.dialog( 'close' )
            });*/
        };

                 /**
                 *
                 * @desc init update statment preference modal
                 * @private
                 */

         var _initUpdateStatementPrefModal = function(){
                    var $dialogWindowElement    = $(_DOMElements.detailPageUpdateStatementPrefModal);

                    $dialogWindowElement.dialog({
                        width:580,
                        height:'auto',
                        modal: true,
                        autoOpen: false,
                        draggable:false,
                        dialogClass: 'modalBox modalBoxPPID',
                        resizable: true,
                        title:'',
                        appendTo:'.layoutContent'
                    }).removeClass('jq-cloak').end()
                        .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
                        .find('.ui-dialog-titlebar-close').addClass('modalClose');

                    $(window).resize(function() {
                        $dialogWindowElement.dialog('option', 'position', 'center');
                    });

                    /*$('.jq-confirmPaymentCancelButton').click(function(e){
                        e.preventDefault();
                        $dialogWindowElement.dialog( 'close' )
                    });*/
                };





    /**
     * @desc fires if URL has #showModal
     * @private
     */
    var _displayConfirmMobileModal = function(){
        var hash        = window.location.hash,
            showModal   = false;

        if(hash) {
            showModal = (hash.indexOf('showModal') > 0);
            if(showModal){
                $(_DOMElements.detailPageConfirmMobileModal).dialog('open');
                $('.ui-widget-overlay').addClass('modalBG');
            }
        }
    };

    /**
         * @desc fires if URL has #showModalUpdatePPID
         * @private
         */
        var _displayUpdatePPIDModal = function(){
            var hash        = window.location.hash,
                showModal   = false;

            if(hash) {
                showModal = (hash.indexOf('UpdatePPID') > 0);
                if(showModal){
                    $(_DOMElements.detailPageUpdatePPIDModal).dialog('open');
                    $('.ui-widget-overlay').addClass('modalBG');
                }
            }
        };


        /**
                 * @desc fires if URL has #showModalUpdatePPID
                 * @private
                 */
                var _displayUpdateStatementPrefModal = function(){
                    var hash        = window.location.hash,
                        showModal   = false;

                    if(hash) {
                        showModal = (hash.indexOf('UpdateStatementPref') > 0);
                        if(showModal){
                            $(_DOMElements.detailPageUpdateStatementPrefModal).dialog('open');
                            $('.ui-widget-overlay').addClass('modalBG');
                        }
                    }
                };





    /**
     * @description prompt server response to screen reader
     * @private
     */
    var _promptServerResponse = function(){
        var $status         = $(_DOMElements.actionStatus),
            $messageBox     = $(_DOMElements.actionStatusMessage),
            $searchResultMsg= $(_DOMElements.searchResultMessage);

        if($status.length === 1 && $.trim($status.text()) !== '') {
            setTimeout(function(){
                $messageBox.text($status.text());
            },10);
        }

        if($searchResultMsg.length === 1) {
            setTimeout(function(){
                $searchResultMsg.text($searchResultMsg.prev('p').text());
            },10);
        }
    };

    /**
     * @desc module init method
     * @private
     */
    var _init = function(){
        _initTabs();
        _setDefaultValues();
        _bindDOMEvents();
        _initConfirmMobileModal();
        _displayConfirmMobileModal();
        _promptServerResponse();
        _initUpdatePPIDModal();
        _displayUpdatePPIDModal();
        _initUpdateStatementPrefModal();
        _displayUpdateStatementPrefModal();


    };

    return{
        init:function(){
            _init();
        }
    }
})(jQuery,window, document);
