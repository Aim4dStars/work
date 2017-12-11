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
        selectAll                   :'#selectAll'
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

        /*
         * init validation engine
         */
        $(_DOMElements.selectAll).change(function () {
        	
        	if($("#selectAll").prop("checked")){
        		$(".checkbox").prop('checked', true);
            } else{
            	$(".checkbox").prop('checked', false);
            }
        });
        $(_DOMElements.searchForm).validationEngine({
            ajaxSubmit:false,
            beforeSubmit:function(form, errors){
            	errors = [];
            	 if ($('input[name^=operationTypes]:checked').length <= 0) {
            	        errors.push("operationTypes");
            	        $(".jq-termCondCheckboxErrorFix").removeClass("noDisplay");
            	        return errors;
            	 }
            	 return errors;
            },
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
         * Detail Page only
         * @desc init select drop-down
         */
        $(_DOMElements.actionSelectElement).dropkick({
            inputClasses:['inputStyleEight'],
            change: function (value, label) {
                _onActionSelectChange(value,label);
            }
        });

        
    }; //End Bind DOM events

     /**
     * @desc fires on search form validation complete
     * @param form
     * @param success
     * @private
     */
    var _onSearchValidationComplete = function(form,success){
    	if(success){
            var selectedTab =  $(_DOMElements.tabsWrap).tabs('option', 'active'),
            filter = selectedTab == 0 ?'#jq-intermediariesSearch':'#jq-clientsSearch';
            $(_DOMElements.selectionInput).val(filter);
            var config = {
                data:$.createFormSubmitRequestBody(form),
                action:document.location.pathname
            };
            console.log(config);
            org.bt.utils.communicate.get(config);
        }
    };
    
    /**
     * @desc fires on search criteria text field validation complete
     * @private
     */
    var _onSearchCriteriaFieldValidationComplete = function(rules,value,isStillValid){
        //reset tab content as requested by
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
    };

    return{
        init:function(){
            _init();
        }
    }
})(jQuery,window, document);
