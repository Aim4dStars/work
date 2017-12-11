/**
 * @fileOverview smsCode jQuery function
 * @version 1.00
 * @author  Rajashekhar Sheela
 * @requires jquery
 * @example $(".form").smsCode();
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.fn.smsCode
 */

(function($,window) {
    $.fn.smsCode = function(method) {

        var methods = {
                init: function(options) {
                    this.smsCode.settings = $.extend({}, this.smsCode.defaults, options);
                    var self = this;
                    return this.each(function() {
                        helpers.init.apply(self);
                    });
                }
            },

            options = {

            },

            helpers = {

                /**
                 * @memberOf $.fn.smsCode
                 */
                init: function() {
                    var plugin          =  this.smsCode,
                        settings        =  plugin.settings,
                        $formObj        =  $(this),
                        $smsCodeButton  =  $formObj.find(settings.smsCodeButton),
						formSubmit      =  settings.formSubmit,
                        $smsCodeInput   =  $(settings.smsCodeInput),
                        skipAnalyze     =  settings.skipAnalyze,
						fromPaymentPage = settings.fromPaymentPage;

                    settings.formObj = $formObj;

                    //Reset SMS code wrapper
                    helpers.resetSmsWrapper.apply(this,[$smsCodeButton, settings]);

                    //TODO: Refactor to externalize analyze calls from the pages, but not from the plugin.

                    if(skipAnalyze){
                        if(formSubmit){
                            $(settings.formObj).find(settings.smsCodeWrapContainer).removeClass('noDisplay');
                            $smsCodeInput.removeClass('jq-skip');
                            $smsCodeButton.addClass('jq-formSubmit');
                            $(settings.formObj).addClass('jq-preventSubmit');
                        }
						if(fromPaymentPage ==true){
							if($(settings.analyzeErrorContainer).find('.jq-FormErrorMessage').hasClass('noDisplay')) {
								var response ={"success":true,"data":null};
								helpers.onCheckIncludeSuccess.apply(this, [response, $smsCodeButton, settings]);
							}
						}
                    }else{
                        //Analyse to show the sms code wrap
                        helpers.checkInclude.apply(this,[$smsCodeButton, settings]);
                    }




                },

                /**
                 *   Reset SMS code wrapper
                 */
                resetSmsWrapper: function($smsCodeButton, settings){

                    var $smsButtonTextHolder     =  $smsCodeButton.find(settings.smsButtonTextHolder),
                        $smsButtonIcon           =  $smsCodeButton.find(settings.smsButtonIcon),
                    $smsCodeInput        =  $(settings.smsCodeInput),
                    $smsCodeErrorContainer   = $(settings.smsCodeErrorContainer);

                   
                   
                    $smsCodeInput.val('');
                    $smsButtonTextHolder.text('Get SMS code');
                    $smsButtonIcon.removeClass('noDisplay').addClass('iconmobile');
                    $smsCodeErrorContainer.addClass('noDisplay');
                    $(this).resetForm();

                },

                /**
                 *   Make a ajax call to check whether to show/hide the SMS wrap section.
                 */
                checkInclude : function($smsCodeButton, settings){

                    // console.log('..checkInclude...', $smsCodeButton);
                    var    data        = $.extend({},settings.analyzeData,{'deviceToken':encode_deviceprint()});
					if(settings.fromPaymentPage==true){
						$(settings.accordionWrapper).find('form').addClass('jq-analyzeSubmitFailed');
					}
					else {
						$(settings.formObj).addClass('jq-analyzeSubmitFailed');
					}					
					$(settings.analyzeErrorContainer).find('.jq-FormErrorMessage').addClass('noDisplay');
                    var config      =  {
                            'url':settings.analyseSmsURL,
                            'data':data,
                            'type':'POST',
                            'onSuccess': function(response){
								if(response.success) {
									if(settings.fromPaymentPage==true){
										$(settings.accordionWrapper).find('form').removeClass('jq-analyzeSubmitFailed');
									}
									else {
										$(settings.formObj).removeClass('jq-analyzeSubmitFailed');
									}									
									helpers.onCheckIncludeSuccess.apply(this, [response, $smsCodeButton, settings]);
								} else {
									$.showServerSideErrors(response,$(settings.analyzeErrorContainer));									
									if(settings.accordionWrapper !== ''){
										$(settings.accordionWrapper).accordionView('hide');
									}
								}
								
                            },
                            'onError': function(){
								//$(settings.formObj).removeClass('jq-preventSubmit');
                                helpers.onCheckIncludeError.apply(this, [$smsCodeButton, settings]);
                            }
                        };
                    org.bt.utils.communicate.ajax.apply(this, [config]);
                },

            /**
            *   On Success call of the analyse SMS code, based on which SMS wrap form will be shown.
            */
            onCheckIncludeSuccess : function(response, $smsCodeButton, settings) {
                 var $smsCodeInput          =  $(settings.smsCodeInput),
                     parentAccordion        =  $smsCodeButton.parents('div[data-accordion]:first'),
                     formSubmit      		=  settings.formSubmit,
                     inEmulationMode        =  (typeof $smsCodeInput.attr('data-service-ops') !== "undefined") ? /true/i.test($smsCodeInput.attr('data-service-ops')): false;





                    if(parentAccordion.length > 0){
                        parentAccordion.accordionView('refresh');
                        window.setTimeout(function(){parentAccordion.find('.jq-close').focus();}, 100);
                    }

                    if(inEmulationMode){
                        $(settings.formObj).find(settings.smsCodeWrapContainer).removeClass('noDisplay');
                        $smsCodeInput.addClass('jq-skip');
                        $(settings.formObj).removeClass('jq-preventSubmit');
                        $(settings.formSubmitButton).removeClass('jq-inactive').removeClass('primaryButtonDisabled');
                        $smsCodeButton.addClass('actionButtonIconDisabled jq-disabled');
                        return false;
                    }

                    //if Check Include returns true
                    if(response.success){
                        $smsCodeInput.removeClass('jq-skip');
                        $(settings.formObj).find(settings.smsCodeWrapContainer).removeClass('noDisplay');
                        if(formSubmit){
                            //Bind the validation for forgot password and registration page
                            $smsCodeButton.addClass('jq-formSubmit');
                            $(settings.formObj).addClass('jq-preventSubmit');
                        }else{

                            //Enable 2FA plugin
                            $smsCodeButton.on('click', function(event){
								event.stopImmediatePropagation();
                                helpers.onSMSBtnClick.apply(this, [settings]);
                            });
                        }
                    }else{
                        $(settings.formObj).find(settings.smsCodeWrapContainer).addClass('noDisplay');
                        $smsCodeInput.addClass('jq-skip');
                        if(formSubmit){
                            $(settings.formObj).removeClass('jq-preventSubmit');
                            $(settings.formSubmitButton).removeClass('jq-inactive').removeClass('primaryButtonDisabled');
                        }
                    }

                    settings.onVisible.call(this);

                },

                /**
                 *   On Error call of the analyse SMS code, based on which SMS wrap form will be shown.
                 */
                onCheckIncludeError : function($smsCodeButton, settings) {
                    // TODO : Error handling.
                },

                /**
                 * On button click for Get SMS Code
                 * @memberOf $.fn.smsCode
                 * @param {Object} settings jquery object
                 */
                onSMSBtnClick: function(settings){
                    var    $smsButton  =  $(this),
                        data        =  {},
                        config      =  {
                            'url':settings.sendSmsURL,
                            'data':data,
                            'type':'POST',
                            'onSuccess': function(response){
                                helpers.onSMSGenerateSuccess.apply(this, [response, $smsButton, settings])
                            }
                            //'onError':_onLoadMoreMessagesError
                        };
                    $smsButton.find('em').removeClass('noDisplay').removeClass('iconmobile').addClass('iconLoader');
					$(settings.formObj).find('.jq-FormErrorMessage').html('').addClass('noDisplay');
                    $(settings.smsCodeInput).attr('placeholder','');
                    $(settings.smsCodeInput).val('');
                    $(settings.smsCodeInput).addClass('textInputDisabled').attr('disabled','disabled');
                    $(settings.smsCodeErrorContainer).addClass('noDisplay');
                    org.bt.utils.communicate.ajax.apply(this, [config]);
                },

                /**
                 *   On success of the call for send sms code
                 */
                onSMSGenerateSuccess : function(response, $smsButton, settings){
                    var $smsButtonTextHolder     =  $smsButton.find(settings.smsButtonTextHolder),
                        $smsButtonIcon           =  $smsButton.find(settings.smsButtonIcon),
                        $smsCodeInput        =  $(settings.smsCodeInput),
                        $smsCodeErrorContainer   = $(settings.smsCodeErrorContainer);

                    if(response.success){
                        $smsCodeInput.removeClass('textInputDisabled').removeAttr('disabled');
                        $.removeMessage($smsCodeInput,['jq-inputError','formFieldMessageError'],'textInputError');
                        $smsCodeInput.attr('placeholder','Enter code');
                        $smsCodeInput.removeClass('jq-skip jq-validField');
                        //$smsCodeInput.trigger('focus');
                        $smsCodeInput.placeholder();
                        $smsCodeErrorContainer.removeClass('noDisplay');
                        $smsButtonTextHolder.text('Try again');
                        $smsButtonIcon.removeClass('iconLoader').addClass('iconmobile');
                        if(settings.accordionWrapper !== ''){
                            $(settings.accordionWrapper).accordionView('refresh');
                        }

                    }
					else {
						$smsButton.find('em').removeClass('noDisplay').removeClass('iconLoader').addClass('iconmobile');
						$.showServerSideErrors(response,settings.formObj);

					}
                }

            };

        return methods.init.apply(this, arguments);

    };

    $.fn.smsCode.defaults = {
        sendSmsURL              :   org.bt.utils.serviceDirectory.sendSmsCode(),
        analyseSmsURL           :   org.bt.utils.serviceDirectory.analyseSmsCode(),
        smsCodeButton           :   '.jq-getSMSCodeButton',
        smsCodeWrapContainer    :   '.jq-smsCodeWrapContainer',
        smsButtonTextHolder     :   '.jq-smsButtonTextHolder',
        smsButtonIcon           :   '.jq-smsButtonIcon',
        smsCodeInput            :   '.jq-smsCode',
        smsCodeErrorContainer   :   '.jq-smsCodeErrorContainer',
        formSubmit              :   false,
        formSubmitButton        :   '',
        onVisible               :   function (){},
        accordionWrapper        :   '',
        analyzeData             :   {},
        skipAnalyze             :   false,
		analyzeErrorContainer:'.jq-analyzeErrorContainer'
    };

    $.fn.smsCode.settings = {};

})(jQuery,window);
