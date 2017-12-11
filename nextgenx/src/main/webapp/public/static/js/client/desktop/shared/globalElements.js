/**
 * @desc Global Elements Module
 * @namespace org.bt.modules.globalElements
 */
org.bt.modules.globalElements = (function($,window,document) {

    var _DOMElements = {
            adviserDetailsWrapper  : '.jq-AdviserDetailsWrapper',
            adviserDetailsLink     : '.jq-AdviserDetailsLink',
            messageDetailsWrapper  : '.jq-messageDetailsWrapper',
            messageDetailsLink     : '.jq-messageDetailsLink',
            userMenuLink	  	   : '.jq-userMenuLink',
            userMenuWrapper	   	   : '.jq-userMenuWrapper',
            investorUnread         : '.jq-investorUnread',
            investorHigh           : '.jq-investorHigh',
            adviserClientUnread    : '.jq-adviserClientUnread',
            adviserClientHigh      : '.jq-adviserClientHigh',
            adviserMessageUnread   : '.jq-adviserMessageUnread',
            adviserMessageHigh     : '.jq-adviserMessageHigh',
            alertCount             : '.jq-alertCount',
            logoutButton           : '.jq-logoutButton',
            emuChangeUserLink      : '.jq-emuChangeUserLink',
            emuChangeUserWrapper   : '.jq-emuChangeUserWrapper'
    };

	//bind all the DOM events - starts
    var _bindDOMEvents = function(){

        /**
          * Adviser Details for Investor Login
        **/
       $(_DOMElements.adviserDetailsLink).flyout({
           flyoutWrapper :_DOMElements.adviserDetailsWrapper
       });

        /**
          * Emulation - Change user
        **/
       if( $(_DOMElements.emuChangeUserLink).length != 0) {
           $(_DOMElements.emuChangeUserLink).flyout({
               flyoutWrapper :_DOMElements.emuChangeUserWrapper
           });
           
        }

        /**
          * Alert Details
        **/
       $(_DOMElements.messageDetailsLink).flyout({
             flyoutWrapper :_DOMElements.messageDetailsWrapper
       });


       /**
        * Adivser Menu details
     	**/
       $(_DOMElements.userMenuLink).flyout({
           flyoutWrapper :_DOMElements.userMenuWrapper,
           adjustLeft:true
       });


        $(document).on('click', _DOMElements.logoutButton, function(event){
            _onLogoutButtonClick.call(this,event);
        });

    }; //bind all the DOM events - ends

    /**
     * @desc fires on logout button click
     * @param event
     * @private
     */
    var _onLogoutButtonClick = function(event){
        $.removeCookie('account_panel');
        $('.jq-logoutBusyDialog').dialog( 'open' );
        $('.ui-widget-overlay').addClass('modalBG');

        _.each(org.bt.utils.serviceDirectory.logout.split(','),
            function(url){
                org.bt.utils.communicate.ajax2({
                                        url:url,
										async:false,
										onError:function(){}
                                    });
            });
        window.location.href = org.bt.utils.serviceDirectory.afterLogout;
    };
	
    /**
    *    Update the message count on global elements header section.
    */
    var _updateAlertCount = function(alertDataObj){

        //Adviser Login, Client Messages
        if(alertDataObj.type.toUpperCase()==='CLIENT'){

            _updateCountWrapper(_DOMElements.adviserClientUnread);
            if(alertDataObj.priority=='HIGH'){ // MAY BE REQUIRED LATER
                _updateCountWrapper(_DOMElements.adviserClientHigh);
            }

        }
        //Adviser Login, My Messages
        else if(alertDataObj.type.toUpperCase()==='ADVISER'){
          _updateCountWrapper(_DOMElements.adviserMessageUnread);
          if(alertDataObj.priority=='HIGH'){    // MAY BE REQUIRED LATER
               _updateCountWrapper(_DOMElements.adviserMessageHigh);
          }
        }
        //Investor Login, Messages
        else if(alertDataObj.type.toUpperCase()==='INVESTOR'){
           _updateCountWrapper(_DOMElements.investorUnread);
           if(alertDataObj.priority=='HIGH'){   // MAY BE REQUIRED LATER
                _updateCountWrapper(_DOMElements.investorHigh);
           }
         }
         _updateCountWrapper(_DOMElements.alertCount);
         _hideHeaderAlertCountWrapper();

    };

    /**
    *   Update the counter wrapper
    */
    var _updateCountWrapper = function(wrapper){

            var countValue = $(wrapper).attr('data-remaining-count'),
                updatedCountValue = parseInt(countValue);
            if(updatedCountValue > 0){
                updatedCountValue = updatedCountValue -1;
            }
            $(wrapper).attr('data-remaining-count', updatedCountValue);
            $(wrapper).text(updatedCountValue);
    };

    /**
     * @desc Subscribe to message read count
     * @private
     */
    var _subscribeToMessageReadChannel = function(){
        org.bt.utils.communicate.subscribe('/messageRead/success', function(alertDataObj) {
            _updateAlertCount(alertDataObj);
        });
    };

    /**
     * Hide count bubble if the message count is zero
    **/
    var _hideHeaderAlertCountWrapper = function(){
        var countValue = $(_DOMElements.alertCount).attr('data-remaining-count'),
            countValue = parseInt(countValue);

        if(countValue === 0){
            $(_DOMElements.alertCount).addClass('noDisplay');
        }

    };
    /**
     * Init modal
     * @private
     */
    var _initLogoutBusyModal = function(){
        var $dialogWindowElement    = $( '.jq-logoutBusyDialog' );

        $dialogWindowElement.dialog({
            modal: true,
            autoOpen: false,
            width:630,
            height:530,
            draggable:false,
            resizable: false,
            dialogClass: 'modalBox logonBusyDialog',
            title:''
        }).end()
            .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
            .find('.ui-dialog-titlebar-close').addClass('noDisplay');

        $dialogWindowElement.removeClass('jq-cloak');

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });
    };


    var _init = function(){
        _bindDOMEvents();
        _subscribeToMessageReadChannel();
        _hideHeaderAlertCountWrapper();
        _initLogoutBusyModal();
    };

    return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document);