/**
 * @namespace org.bt.modules.fatalErrors
 * @desc Display fatal errors on fatal.jsp.
 */
org.bt.modules.fatalErrors = (function ($, window, document) {
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
        moduleWrapper   :'.jq-fatalWrap',
        toggleButton    :'.jq-fatalToggleButton',
        expandView      :'.jq-fatalExpandView',
        collapseView    :'.jq-fatalCollapseView'
    };

    /**
     * @desc Bind DOM events to elements of the module
     * @private
     */
    var _bindDOMEvents = function(){
        /**
         * Toggle fatal view on click
         */
        $(_DOMElements.moduleWrapper).on('click', _DOMElements.toggleButton, function(event){
            _onToggleButtonClick.call(this,event);
        });
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
        } else {
            //need to expand
            $(_DOMElements.expandView).removeClass('noDisplay').show();
            $(_DOMElements.collapseView).hide();
            _viewState = 'expand';
        }
    };

    /**
     * @desc Module init method
     * @private
     */
    var _init = function(){
        _bindDOMEvents();
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
