/**
 * @namespace org.bt.modules.accountBalance
 * @desc Display account balance top of third level navigation. Will be displayed in Overview, Transactions, Move Money etc pages.
 */
org.bt.modules.accountBalance = (function ($, window, document) {
    /**
     * @desc state of the view. By default it will be Collapse
     * @type {string}
     * @private
     */
    var _viewState  = 'collapse';

    /**
     * @checks user has multiple accounts
     * @type {boolean}
     * @private
     */
    var _hasMultipleAccounts = false;

    /**
     * @desc DOM element map
     * @private
     */
    var _DOMElements = {
        moduleWrapper   :'.jq-accountBalanceWrap',
        toggleButton    :'.jq-accountBalanceToggleButton',
        expandView      :'.jq-accountBalanceExpandView',
        collapseView    :'.jq-accountBalanceCollapseView',
        accountsSwitcher:'.jq-multipleAccountSwitcher',
        accountsWrapper :'.jq-multipleAccountsWrapper',
        accountSwitcherContainer: '.jq-multiAccountHeadContainer'
    };

    /**
     * @desc Bind DOM events to elements of the module
     * @private
     */
    var _bindDOMEvents = function(){
        /**
         * Toggle account view on click
         */
        $(_DOMElements.moduleWrapper).on('click', _DOMElements.toggleButton, function(event){
            _onToggleButtonClick.call(this,event);
        });

        /**
         * Investor only
         * bind fly-out only there are multi accounts.
         */
        if($(_DOMElements.accountsWrapper).length == 1){
            //$(_DOMElements.accountsSwitcher).flyout({
            $(_DOMElements.accountSwitcherContainer).flyout({
                adjustLeft:false,
                flyoutWrapper :_DOMElements.accountsWrapper
            });
            _hasMultipleAccounts = true;
        }
    };

    /**
     * @desc Fires when on toggle button click
     * @param event
     * @private
     */
    var _onToggleButtonClick = function(event){
        if(_viewState === 'expand'){
            //need to collapse
            $(_DOMElements.expandView).hide();
            $(_DOMElements.collapseView).show();
            _viewState = 'collapse';
            $.cookie('account_panel', 'collapse');
        } else {
            //need to expand
            $(_DOMElements.expandView).show();
            $(_DOMElements.collapseView).hide();
            _viewState = 'expand';
            $.cookie('account_panel', 'expand');
        }

        // Communicate the status of expandable fatHeader
        org.bt.utils.communicate.publish('/accountDetails/expand',[]);
    };

    /**
     * @desc remove no display class of the expand view element
     * @private
     */
    var _removeNoDisplayClass = function(){
        $(_DOMElements.expandView).removeClass('noDisplay').hide();
    };

    /**
     * @desc add cursor style is there are more than one account for investor
     * @private
     */
    var _addStylesForMultipleAccountsLink = function(){
        if(_hasMultipleAccounts){
            $(_DOMElements.accountsSwitcher).addClass('cursor');
        }
    };

    var _checkPanelStatus = function(){
        var status = $.cookie('account_panel');
        if(status && status === 'expand'){
            _viewState = 'expand';
            $(_DOMElements.collapseView).hide();
            $(_DOMElements.expandView).show();
        }
    };

    /**
     * @desc Module init method
     * @private
     */
    var _init = function(){
        _removeNoDisplayClass();
        _checkPanelStatus();
        _bindDOMEvents();
        _addStylesForMultipleAccountsLink();
    };

    /**
     * @desc interface methods
     */
    return{
        init:function(){
            _init();
        }
    }
})(jQuery,window, document);
