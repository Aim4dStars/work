/**
 * @namespace org.bt.modules.logon
 * @desc Logon Module
 */

$( document ).ready(function() {
    $.ajax({
        url: "/content/public/general-text/login-outage-notification.advanced.json",
        dataType: "json",
        type: "GET",
        success: function (response) {
            if(response[0].text === null || response[0].text === "" || response[0].text === undefined) {
                $(".outageNotificationWrapper").addClass('noDisplay');
            } else {
                $(".outageNotificationWrapper").removeClass('noDisplay');
                $(".jq-outageMessageTitle").html(response[0].title);
                $(".jq-outageMessageText").html(response[0].text);
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $(".outageNotificationWrapper").addClass('noDisplay');
        }
    });

    $.ajax({
        url: "/content/public/general-text/registration-outage-notification.advanced.json",
        dataType: "json",
        type: "GET",
        success: function (response) {
            if(response[0].text === null || response[0].text === "" || response[0].text === undefined) {
                $(".registerOutageNotificationWrapper").addClass('noDisplay');
            } else {
                $(".registerOutageNotificationWrapper").removeClass('noDisplay');
                $(".jq-registerOutageMessageTitle").html(response[0].title);
                $(".jq-registerOutageMessageText").html(response[0].text);
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $(".registerOutageNotificationWrapper").addClass('noDisplay');
        }
    });
});

var hostNameArray = window.location.hostname.split('.');
var hostName = hostNameArray[1];
if (hostName === 'panoramaadviser') {
    $('.registerPlaceholder').html('Professionals registration');
    $('#postcodeHelp .helpContent').html('Enter your registered business postcode.');
} else if (hostName === 'panoramainvestor') {
    $('.registerPlaceholder').html('Investor registration');
    $('#postcodeHelp .helpContent').html('Enter your residential postcode. It must match the application form.');
} else {
    $('.registerPlaceholder').html('Register');
}

$(".iconHelpContainer .icon-support-help").click(function(){
    var id = this.parentElement.getAttribute('id');
    $(".iconHelpContainer .inlineHelpTooltip").not('#'+id+ ' ' + '.inlineHelpTooltip').addClass('noDisplay');
    $('#'+id+' '+'.inlineHelpTooltip').toggleClass('noDisplay');
});

$('.jq-registrationFields').bind({
    focusout:function(ev){

        $(".iconHelpContainer .inlineHelpTooltip").fadeOut();
    },
    focus: function(ev) {
        var id = this.parentElement.getAttribute('id');
        $('#'+id+' '+'.inlineHelpTooltip').removeClass('noDisplay').fadeIn();
    }
});

org.bt.modules.logon = (function($,window,document) {
    /**
     * @desc keep the key map as a private property, has the proposed cipher
     * @type Object
     * @private
     */
    var _keyMap = {};

    /**
     * @desc DOM Elements selector map
     * @private
     */
    var _DOMElements = {
        logonForm:'.jq-logonForm',
        passwordField:'.jq-logonPassword',
        maskPasswordField:'.jq-logonMaskPassword',
        halgmField:'.jq-halgmField',
        brandField:'.jq-brandField',
        usernameField:'.jq-logonUsername',
        submitButton:'.jq-formSubmit',
        deviceTokenInput:'.jq-deviceToken'
    };
    /**
     * Bind DOM events function
     * @private
     */
    var _bindDOMEvents = function(){
        //init validation engine for logon
        $(_DOMElements.logonForm).validationEngine(
            {
                ajaxSubmit:false,
                submitOnSuccess:false,
                onValidationComplete:function(form,success){
                    _onLogonValidationComplete(form,success)
                }
            }
        ).submit(function(event) {
            event.stopPropagation();
            event.preventDefault();
        });

        $(_DOMElements.passwordField).bind('keyup',function(ev){
            _onPasswordFieldKeyUp(ev);
        });
    };//End DOM Event binding

    /**
     * @desc fires on password field key up
     * @param event
     * @private
     */
    var _onPasswordFieldKeyUp = function(event){
        var code = event.which,
            $submitBtn;

        if(code == 13){
            $submitBtn = $(_DOMElements.logonForm).find(_DOMElements.submitButton);
            $submitBtn.focus();
            //trigger click after a delay.
            setTimeout(function(){
                $submitBtn.trigger('click');
            },100);
        } else {
            _encryptPassword();
        }
    };

    var _checkWPLPassword = function() {
        var password = $(_DOMElements.passwordField).val()
        var passPattern  = org.bt.collections.cryptography.inputRestrictions.password.pattern;
        if (passPattern.indexOf("A-Z") !== -1 && passPattern.indexOf("a-z") === -1) {
            password = password.toUpperCase();
        }
        return password;
    };
    /**
     * @desc encrypt the password on submit
     * @private
     */
    var _encryptPassword = function(text){
        value = [];
        value = _.map(text, _applyCipher);

        $(_DOMElements.maskPasswordField).val(value.join(''));
    };

    /**
     * @desc return the converted character given the current cipher, no cipher text will leave character unchanged
     * @private
     */
    var _applyCipher = function(character){
        var cipherCharacter = _keyMap[character];

        // return original character is cipher doesn't have a mapping
        return cipherCharacter ? cipherCharacter : character;
    };

    /**
     * Submit logon form on validation complete
     * @param form
     * @param success
     * @private
     */
    var _onLogonValidationComplete = function(form,success){
        if(success){
            $('.jq-logonBusyDialog').dialog( 'open' );
            $('.ui-widget-overlay').addClass('modalBG');
            try {
              window.localStorage.setItem('LOGIN_START_TIME', new Date().getTime());
            } catch (e) {
            }
            _initCrypto(form);
        }
    };

    /**
     * Init modal
     * @private
     */
    var _initLogonBusyModal = function(){
        var $dialogWindowElement    = $( '.jq-logonBusyDialog' );

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

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });
    };

    /**
     * Request the crypto javascript
     * @private
     */
    var _initCrypto = function(form){
        org.bt.utils.communicate.ajax.call(this, {
            'type':'GET',
            'url':org.bt.cryptoUrl + '?uid='+encodeURIComponent($('.jq-logonForm').find('.jq-logonUsername').val()),
            'onSuccess':function(res){
                org.bt.collections.cryptography = res;
                _preProcessData();
                _setFormParams();
                //call encrypt password before submit
                _encryptPassword(_checkWPLPassword());
                $.cookie("userNameResetPwd", $('.jq-logonForm').find('.jq-logonUsername').val());
                //avoids sending password
                $(_DOMElements.passwordField).removeAttr('name');
                //set device token value
                $(_DOMElements.deviceTokenInput).val(encode_deviceprint());

                var config = {
                    data:$.createFormSubmitRequestBody(form, false),
                    action:form.attr('action')
                };

                org.bt.utils.communicate.post(config);
            },
            'onError':function(){
                $('.jq-logonBusyDialog').dialog( 'close' );
                $('.eamDownMessage').removeClass('noDisplay');
                $(document).scrollTop(0);
            }
        });

    };

    /**
     * @desc set form params on page load using EAM data
     * @private
     */
    var _setFormParams = function(){
        var $form       = $(_DOMElements.logonForm),
            operation   = null;

        $(_DOMElements.halgmField).val(org.bt.collections.cryptography.keymap.halgm);

        //set form properties
        $.each(org.bt.collections.cryptography.operations,function(k,v){
            if(v.name === 'auth'){
                operation = v;
                return false;
            }
        });

        if(operation !== null){
            $form.attr({'action':operation.submitToUri,'method':operation.method,'accept-charset':operation['accept-charset']});
        }
    };

    /**
     * @clear saved password
     * @private
     */
    var _clearPassword = function(){
        setTimeout(function(){$(_DOMElements.passwordField).val('')},50);
    };

    var _preProcessData = function(){
        var keys = org.bt.collections.cryptography.keymap.keys;

        _.map(keys, function(item) {
            $.each(item, function(key, value) {
                _keyMap[key] = org.bt.collections.cryptography.keymap.malgm[value];
            });
        });

        //reset the map!
        org.bt.collections.cryptography.keymap.keys = {};
    };
    /**
     * Init method for module
     * @private
     */
    var _init = function(){
        _bindDOMEvents();
        _initLogonBusyModal();
        if(window.location.href.indexOf('#jq-passwordReset1') > -1) {
            $('.jq-linkPasswordReset').removeClass('noDisplay');
        }
    };
    return{
        init:function(){
            _init();
        }
    }
})(jQuery,window, document);

/**
 * Register Module
 */
org.bt.modules.register = (function($,window,document) {

    /**
     * Bind DOM Events function
     * @private
     */

    var _DOMElements = {
        termsAndCondWrapper: '.jq-termsAndCondWrapper',
        formTcCheckBox: '.jq-formTcCheckBox',
        regTermsAndCondHidden: '.jq-regTermsAndCondHidden',
        adviserTermsCondWrapper: '.jq-adviserTermsCondWrapper'
    };

    var _bindDOMEvents = function(){

        $(_DOMElements.termsAndCondWrapper).on('click', _DOMElements.formTcCheckBox, function(event){
            _onTcCheckBoxClick.call(this,event);
        });

        //$("#jq-register .smsCodeBody").addClass("noDisplay");
        $("#jq-register .btn-action-primary .label-content").text("Verify details");
        $("#jq-register .SMSCodebuttonContainer").addClass("noDisplay");
        $("#jq-register .jq-smsCodeWrapContainer .sms-label").addClass("noDisplay");
        $("#jq-register .jq-smsCodeWrapContainer .iconHelpContainer").addClass("noDisplay");
        document.getElementById("smsCode").placeholder = "SMS code for your security";
        $('.jq-registerStepOneForm').smsCode({formSubmit:true, formSubmitButton: '.jq-registerStepOneSubmitButton', skipAnalyze: true} );

        //on get SMS code button click
        $('.jq-getSMSCodeButton').bind('click',function(ev){_onGetSMSCodeButtonClick(ev);});
        $('.jq-RegisterTryAgain').bind('click',function(ev){_onRegisterTryAgainClick(ev);});

        $('.jq-registerStepOneSubmitButton').bind('click', _onValidateCredentialsButtonClick);

        $('.jq-registerUsername').bind({
            focusout: function() {
                _onUserNameFocusOut();
            },
            focus: function(){
                _onUserNameFocus();
            }
        });

        /**
         * Step One
         */
        $('.jq-registerStepOneForm').validationEngine(
            {
                ajaxSubmitUrl		:null,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                escapeHtml          : false,
                submitOnSuccess     :true,
                escapeHtml 			:false,
                onValidationComplete:function($form,success){
                    if($form.attr('data-action')!=='smsRegistrationCode'){
                        $('.jq-registerStepOneSubmitButton').find(".iconWLoader").removeClass("noDisplay");
                        $('.jq-registerStepOneSubmitButton').find(".label-content").addClass("noDisplay");
                        $('.registrationCancelBtn').addClass("disabled");
                    }
                    _onRegisterStepOneValidationComplete($form,success);
                },
                onSubmitSuccess     :function(data){
                    _onRegisterStepOneSubmitSuccess(data)
                }
            }
        );


        /**
         * Step Two
         */

        //bind password policy
        $('.jq-registerPassword').passwordPolicy({'userNameElement':'.jq-registerUsername','hintElement':'.jq-passwordPolicyHintsContainer'});

        //init validation engine for register step two
        $('.jq-registerStepTwoForm').validationEngine(
            {
                ajaxValidationUrl   :org.bt.utils.serviceDirectory.validateRegistration,
                ajaxSubmitUrl		:org.bt.utils.serviceDirectory.registrationStep2,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                submitOnSuccess     :false,
                onValidationComplete:function($form,success){
                    org.bt.utils.log.info('form '+$form+' success '+success);
                    _onRegisterStepTwoValidationComplete($form,success);
                }
            }
        );

        //confirm password blur
        $('.jq-registerConfirmPassword').bind({
            focusout:function(ev){
                _validateRegisterConfirmPassword.apply(this,[ev])
            },
            keyup: function(ev) {
                _onRegisterConfirmPasswordKeyUp.apply(this,[ev]);
            }
        });

        /**
         * Omniture Changes Registration Step 1
         */
        var registrationStep1Omniture = function () {
            var pageDetails;
            pageDetails = {
                "pageName": "register:step 1",
                "pageType": "registration",
                "formName": "register",
                "pageStep": "start"
            };
            if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1) {
                window.wa && wa('page', pageDetails);
            }
        };

        var forgotPasswordStep1Omniture = function () {
            var pageDetails;
            pageDetails = {
                "pageName": "forgotten password:step 1",
                "pageType": "selfservice",
                "formName": "forgotten password",
                "pageStep": "forgotpasswordstart"
            };
            if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1) {
                window.wa && wa('page', pageDetails);
            }
        };

        /**
         * Init Tabs
         */
        var $tabContent  = $('.jq-mainContent');
        $tabContent.tabs({
            select: function(event, ui) {
                if(ui.tab.hash === '#jq-register'){
                    registrationStep1Omniture();
                }
                else if(ui.tab.hash === '#jq-logon') {
                    $('.jq-logonUsername').focus();

                }
                else {
                    $('.jq-passwordResetUsername').focus();
                    forgotPasswordStep1Omniture();
                }
            }
        });

        // Accessibility: Manage tabIndex
        $tabContent.find('li[role="tab"]').removeAttr('tabindex');
        $tabContent.find('.ui-tabs-anchor').removeAttr('tabindex');
        $tabContent.bind('tabsselect', function(event, ui) {
            setTimeout(function(){$tabContent.find('li[role="tab"]').removeAttr('tabindex');},100);
        });

        //init history plugin
        $.address.change(function(event){
            $tabContent.tabs( 'select' , window.location.hash );

            if(event.path === '/') {
                // login page
                $('.registerOutageNotificationWrapper').hide();
                $('.outageNotificationWrapper').show();  

                // log the user out
                $.ajax({ url: "/pkmslogout", type: "GET" });          
            }

            if(event.path === '/jq-register') {
                // register page
                $('.registerOutageNotificationWrapper').show();
                $('.outageNotificationWrapper').hide();    
            }
        });
        $tabContent.bind("tabsselect", function(event, ui) {
            var $logonContainer         = $('#jq-logon'),
                $resetPasswordContainer = $('#jq-passwordReset'),
                $smsButton              = $('.jq-registerStepOneSubmitButton'),
                $registerStepOne        = $('.jq-registerStepOne'),
                $registerStepTwo        = $('.jq-registerStepTwo'),
                $stepOneForm            = $('.jq-registerStepOneForm'),
                $smsMessage             = $('.jq-registerSmsNumberMessage'),
                $notices                = $('.noteBoxWrap'),
                $disclaimer             = $('.jq-disclaimer'),
                $contactNumberField     = $('.jq-contactNumber');

            window.location.hash = ui.tab.hash;
            $(ui.tab.hash).find('form').each(function(){
                $(this).resetForm();
            });

            if(ui.tab.hash === '#jq-register'){
                //toggle steps
                $('.jq-linkPasswordReset').addClass('noDisplay');
                $registerStepOne.show();
                $registerStepTwo.hide();
               // $smsMessage.addClass('noDisplay');
                $stepOneForm.addClass('jq-preventSubmit');
                $stepOneForm.smsCode({formSubmit:true, skipAnalyze: true, formSubmitButton: '.jq-registerStepOneSubmitButton'});
                // hide errors
                $logonContainer.find('.noticeBox').hide();
                $notices.removeClass('noteBoxWrapMod1');
                $disclaimer.hide();

                $contactNumberField.removeClass('noDisplay');
            } else if(ui.tab.hash === '#jq-logon') {
                // hide errors
                $('.jq-linkPasswordReset').addClass('noDisplay');
                $logonContainer.find('.noticeBox').hide();
                $notices.removeClass('noteBoxWrapMod1');
                $disclaimer.show();

                $contactNumberField.addClass('noDisplay');
            }
        });

        $(".bodyContainer").removeClass('opacity-zero');
        if ($.cookie('animationPlayed') == null){
            $(".bodyContainer").removeClass('bg-blurred');
            setTimeout(function(){
                $(".bodyContainer").addClass('bg-blurred').css('transition', 'background-image 0.7s ease-in-out');
                $('.panoramaLogo').css('right', '25px').css('transition', 'all 0.7s linear');
            },1000);
            setTimeout(function(){
                $('.signinContainerWrap').removeClass('opacity-zero');
            },1500);
        } else {
            $('.panoramaLogo').css('right', '25px').css('transition', 'all 0.2s linear');
            setTimeout(function(){
                $('.signinContainerWrap').removeClass('opacity-zero').css('transition', 'all 0.2s linear');
            },200);
        }
        $.cookie('animationPlayed','true');

    };//End DOM Event binding

    /**
     * Fired on Terms and Condition click
     * @param event
     * @private
     */
    var _onTcCheckBoxClick = function(event){
        if($('.jq-formTcCheckBox:checked').size() < 1 && $(_DOMElements.adviserTermsCondWrapper).attr('data-validate') === 'true'){
            $(_DOMElements.regTermsAndCondHidden).val('false');
        }else{
            $(_DOMElements.regTermsAndCondHidden).val('true');
        }
    };

    /**
     * Fired when username on focus
     * @private
     */
    var _onUserNameFocus = function(){
        $('.jq-usernamePolicyHintsContainer').removeClass('noDisplay').fadeIn()
    };

    /**
     * Fired when username focus out
     * @private
     */
    var _onUserNameFocusOut = function(){
        $('.jq-usernamePolicyHintsContainer').fadeOut();
    };

    /**
     * Fired when SMS code input key up
     * @param ev
     * @private
     */
    var _onGetSMSCodeKeyUp = function(ev){
        var $element    = $(this),
            charLength  = parseInt($element.attr('data-length'),10),
            $button     = $('.jq-registerStepOneSubmitButton');

        if($element.val().length === charLength){
            $button.removeClass('disabled');
        } else {
            $button.addClass('disabled');
        }
    };

    /**
     * Fired when SMS code input focus out
     * @param ev
     * @private
     */
    var _onGetSMSCodeFocusOut = function(ev){
        var $smsInput   = $(this),
            value       = $.trim($smsInput.val()),
            charLength  = parseInt($smsInput.attr('data-length'),10),
            $form       = $('.jq-registerStepOneForm');

        if(value === '' || value === $smsInput.attr('data-placeholder') || value.length !== charLength){
            $.promptMessage($smsInput,'required',['jq-inputError','formFieldMessageError'],'textInputError','');
            $form.addClass('jq-preventSubmit');
            $('.jq-registerStepOneSubmitButton').addClass('disabled')
        } else {
            $.removeMessage($smsInput,['jq-inputError','formFieldMessageError'],'textInputError');
            $form.removeClass('jq-preventSubmit');
            $('.jq-registerStepOneSubmitButton').removeClass('disabled')
        }
    };

    /**
     * Fired when SMS code button click
     * @param e
     * @private
     */

    var _onGetSMSCodeButtonClick = function(e){
        var $smsCode    = $('.jq-smsCode');

        $('.jq-registerStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.validateLoginRegistration)
            .attr('data-action','smsRegistrationCode')
            .removeClass('jq-preventSubmit');

        $smsCode.addClass('noDisplay');
        $smsCode.removeClass('jq-skip');
        $smsCode.attr('placeholder','');
        $('.jq-smsValidation').removeClass("noDisplay");

        // force logout (and ignore its response) to clear lingering session before step two
        org.bt.utils.communicate.ajax2({
            url: org.bt.utils.serviceDirectory.logoutSuccess,
            async:false,
            onError:function(){}
        });

        $.removeMessage($smsCode,['jq-inputError','formFieldMessageError'],'textInputError');

       // $('.jq-registerSmsNumberMessage').addClass('noDisplay');

    };

    var _onValidateCredentialsButtonClick = function (ev) {
        ev.preventDefault();
        var $smsCode    = $('.jq-smsCode');
        $smsCode.addClass('jq-skip');
        $smsCode.removeClass("noDisplay");
        // force logout (and ignore its response) to clear lingering session before step two
        org.bt.utils.communicate.ajax2({
            url: org.bt.utils.serviceDirectory.logoutSuccess,
            async:false,
            onError:function(){}
        });

        $('.jq-registerStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.validateCredentials)
            .attr('data-action','registrationStep1')
            .removeClass('jq-preventSubmit');
    };

    var _onRegisterTryAgainClick = function(e){

        var $smsCode    = $('.jq-smsCode');

        $('.jq-registerStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.validateCredentials)
            .attr('data-action','smsRegistrationCodeTryAgain')
            .removeClass('jq-preventSubmit');

        $.removeMessage($smsCode,['jq-inputError','formFieldMessageError'],'textInputError');
    };

    /**
     * Fired when on register step one submit button click
     * @param e
     * @private
     */
    var _onRegisterStepOneSubmitButtonClick = function(e){
        e.preventDefault();
        var $smsCode    = $('.jq-smsCode');
        $smsCode.removeClass('jq-skip');
        $('.jq-registerStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.verifyDealerGroupSmsAndRegistration)
            .attr('data-action','registrationStep1')
            .removeClass('jq-preventSubmit');

        $.promptMessage($smsCode,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
    };


    /**
     * Fired when on register step one validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onRegisterStepOneValidationComplete = function($form,success){
        var $button     = $('.jq-getSMSCodeButton'),
            $busyIcon   = $('.jq-smsBusyDialogContainer'),
            $smsInput   = $('.jq-smsCode'),
            $smsButton  = $('.jq-registerStepOneSubmitButton'),
            action      = $form.attr('data-action');

        $form.removeClass('jq-preventSubmit');
        //add safi token
        $form.find('input[name="deviceToken"]').val(encode_deviceprint());
        //Have to find which action is trigger
        if(action === 'smsRegistrationCode'){
            $form.addClass('jq-preventSubmit');

            if(success){
                $button.find('.icon').removeClass('icon-mobile-send').addClass('iconWLoader');
            }
        } else if(action == 'registrationStep1') {
            if (!success) {
                $('.jq-registerStepOneSubmitButton').find(".iconWLoader").addClass("noDisplay");
                $('.jq-registerStepOneSubmitButton').find(".label-content").removeClass("noDisplay");
                $('.registrationCancelBtn').removeClass("disabled");
            }
        }
    };

    /**
     * Fired when on register step one submit request success
     * @param data
     * @private
     */
    var _onRegisterStepOneSubmitSuccess = function(data){
        var $button     = $('.jq-getSMSCodeButton'),
            $busyIcon   = $('.jq-smsBusyDialogContainer'),
            $message    = $('.jq-registerSmsNumberMessage'),
            $smsError    = $('.jq-smsCodeErrorContainer'),
            $smsCodeErrorLabel = $('.jq-smsCodeError'),
            $smsInput   = $('.jq-smsCode'),
            $stepOne    = $('.jq-registerStepOne'),
            $stepTwo    = $('.jq-registerStepTwo'),
            $form       = $('.jq-registerStepOneForm'),
            validSms    = ($smsInput.val().length === 6),
            action      = $form.attr('data-action'),
            $notices    = $('.noteBoxWrap'),
            $registerContainer = $('.jq-registerContainer'),
            $eamFrom    = $('.jq-authWithEamForm'),
            baseUrl     = location.protocol + "//" + location.hostname +
                (location.port && ":" + location.port),
            errorMessageText = '';


        //Have to find which action is trigger
        if(action === 'smsRegistrationCode'){
            $smsInput.removeClass('textInputError');
            $smsInput.addClass('textInputDisabled');

            if(data.success){
                $('.jq-FormErrorMessage').html('').addClass('noDisplay');
                $message.removeClass('noDisplay');
                $message.addClass('smsNumberMessageShow');
                $('.SMSCodebuttonContainer').addClass('noDisplay');
                $smsInput.removeClass('noDisplay');
                $smsInput.placeholder();
                $button.find('.actionButtonIconText').html('Try again');
                $button.find('.icon').removeClass('iconWLoader').addClass('icon-mobile-send');
                $smsError.addClass('noDisplay');
                $notices.removeClass('noteBoxWrapMod1');

            } else {
                //display server side error
                $.showServerSideErrors(data,$form);
                $(document).scrollTop(0);

                $smsError.addClass('noDisplay');
                $smsInput.attr('placeholder','');
                $message.addClass('noDisplay');
                $button.find('.actionButtonIconText').html('Try again');
                $button.find('.icon').removeClass('iconWLoader').addClass('icon-mobile-send');
                $notices.addClass('noteBoxWrapMod1');
                errorMessageText = data.data;
                if(errorMessageText.indexOf('expired')!=-1){
                    $('.jq-registrationCode').val('').select();
                }
            }
        } else {
            if(data.success && data.data.showSMS!=true){
                $('.jq-registerStepOneSubmitButton').addClass('jq-disabled');
                $eamFrom.attr('action',data.data.eamPostUrl);
                $eamFrom.find('input[name="SAMLResponse"]').val(data.data.samlresponse);
                $eamFrom.find('input[name="deviceToken"]').val(encode_deviceprint());
                $eamFrom.submit();
            }
            else if(data.success && data.data.showSMS == true){
                $('.jq-FormErrorMessage').html('').addClass('noDisplay');

                $('.verifyCancelBtn').addClass('verifyCancelBtnMove');
                $('.jq-registerStepOneSubmitButton .iconWLoader').addClass("noDisplay");
                $('.jq-registerStepOneSubmitButton .label-content').removeClass("noDisplay");
                $message.addClass('smsNumberMessageShow');
                $('.registrationCancelBtn').removeClass("disabled");
                //$('#jq-register .smsCodeBody, #jq-register input#smsCode').removeClass("noDisplay");
                $('#jq-register input#smsCode').addClass('smsCodeShow');
                //$('.jq-registerStepOneSubmitButton').addClass('jq-registerStepOneSubmitButtonMove');
                //$('.registrationCancelBtn').addClass('registrationCancelBtnMove');
                //$('.registrationCancelBtn').addClass('registrationCancelBtnMove');
                $('.btn-action-primary .button-inner .label-content').text('Next');
                $('.jq-registerContainer .registrationSuccess .validation-container .icon-notification-success').removeClass('noDisplay');
                //sms code
                $('.jq-smsCode')
                    .placeholder()
                    .keyup(_onGetSMSCodeKeyUp)
                    .focusout(_onGetSMSCodeFocusOut)
                    .addClass('textInputDisabled');

                if (action !== 'smsRegistrationCodeTryAgain') {
                    $('.jq-registerStepOneSubmitButton').addClass("disabled");
                }

                $('.jq-smsCode').removeClass('jq-skip');
                $('.jq-registerStepOneSubmitButton').addClass("jq-preventSubmit");
                $('.jq-registerStepOneSubmitButton').off('click', _onValidateCredentialsButtonClick);
                $('.jq-registerStepOneSubmitButton').on('click', _onRegisterStepOneSubmitButtonClick);
                $('.jq-registerStepOneSubmitButton').attr('label-content', 'Next');
                $('.jq-registerSmsNumberMessage').removeClass('noDisplay');

                $('.jq-registerStepOneForm')
                    .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.verifyDealerGroupSmsAndRegistration)
                    .attr('data-action','registrationStep1')
                    .removeClass('jq-preventSubmit');

            }
            else {
                //display server side error
                $.showServerSideErrors(data,$form);
                $(document).scrollTop(0);
                $notices.addClass('noteBoxWrapMod1');
                $('.jq-registerStepOneSubmitButton').find(".iconWLoader").addClass("noDisplay");
                $('.jq-registerStepOneSubmitButton').find(".label-content").removeClass("noDisplay");
                $('.registrationCancelBtn').removeClass("disabled");
            }
        }
    };

    /**
     * Fired when on register step two validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onRegisterStepTwoValidationComplete = function($form,success){
        if(success){
            //check password is a valid one or not
            var $passwordField      = $('.jq-registerPassword'),
                validatePassword    = $passwordField.passwordPolicy('validate'),
                invalidPassword     =  (validatePassword.length > 0),
                validConfirmPassword= _validateRegisterConfirmPassword.apply($('.jq-registerConfirmPassword')[0]),
                regTermsAndCondHidden = $('.jq-regTermsAndCondHidden'),
                noticeBoxTC			= $('.jq-noticeBoxTC'),
                termsCondErrMessage	= $('.jq-termsCondErrMessage'),
                adviserTermsCondWrapper = $('.jq-adviserTermsCondWrapper').attr('data-validate'),
                regTermsCondCheck = true;

            if(!invalidPassword){
                $.removeMessage($passwordField,['jq-inputError','formFieldMessageError'],'textInputError');
            }else {
                $.promptMessage($passwordField,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');

            }

            //Check Terms and Condition
            if(adviserTermsCondWrapper==='true'){
                if(regTermsAndCondHidden.val()==='false'){
                    termsCondErrMessage.html(regTermsAndCondHidden.attr('data-validation-required-error'));
                    noticeBoxTC.show();
                    $('.noteBoxWrap').addClass('noteBoxWrapMod1');
                    regTermsCondCheck = false;
                }else{
                    noticeBoxTC.hide();
                    $('.noteBoxWrap').removeClass('noteBoxWrapMod1');
                    regTermsCondCheck = true;
                }
            }

            //no errors. Submit the request to backend
            if(!invalidPassword && validConfirmPassword && regTermsCondCheck){
                $('.jq-registerBusyDialog').dialog( "open" );
                $('.ui-widget-overlay').addClass('modalBG');

                org.bt.utils.log.info('Submit form.....');
                var data    = $.createFormSubmitRequestBody($form),
                    action  = org.bt.utils.serviceDirectory.registrationStep2;

                org.bt.utils.communicate.ajax(
                    {
                        url:action,
                        type:'POST',
                        data:data,
                        onSuccess:function(res){
                            if(res.success){
                                org.bt.utils.log.info('redirect user');
                                var url = $('.jq-registerStepTwoSubmitButton').attr('data-href'),
                                    data = [{'name':'userId','value':res.data}];

                                org.bt.utils.communicate.get({data:data,action:org.bt.utils.serviceDirectory.registrationStep3});
                            } else {
                                $('.jq-registerBusyDialog').dialog( "close" );
                                $.showServerSideErrors(res,$form);
                                $(document).scrollTop(0);
                            }
                        },
                        onError:function(){}
                    }
                );
            }
        }
    };

    /**
     * Fired when confirm password on blur
     * @returns {boolean}
     * @private
     */
    var _validateRegisterConfirmPassword = function(){
        var $element    = $(this),
            $form       = $element.parents('form:first'),
            value       = $.trim($element.val()),
            password    = $.trim($('.jq-registerPassword').val()),
            valid     = false;

        if(password !== '' && value !== ''){
            if(password !== value){
                $.promptMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError','');
                $form.addClass('jq-preventSubmit');
                $element.siblings('.iconCheck').addClass('noDisplay');
            } else {
                valid= true;
                $form.removeClass('jq-preventSubmit');
            }
        }
        return valid;
    };

    /**
     * Fired when confirm password keyUp
     * @param e
     * @private
     */
    var _onRegisterConfirmPasswordKeyUp = function(e){
        var $element    = $(this),
            value       = $.trim($element.val()),
            password    = $.trim($('.jq-registerPassword').val());

        $element.siblings('.iconCheck').addClass('noDisplay');

        if(password !== '' && value !== ''){
            if(password === value){
                $element.addClass('valid');
                $element.siblings('.iconCheck').removeClass('noDisplay');
            }
        }
    };

    /**
     * Init register model
     * @private
     */
    var _initRegisterBusyModal = function(){
        var $dialogWindowElement    = $( '.jq-registerBusyDialog' );

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

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });
    };

    /**
     * Init method for module
     * @private
     */
    var _init = function(){
        _bindDOMEvents();
    };
    return{
        /**
         * @memberOf org.bt.modules.logon
         * @public
         */
        init:function(){
            _init();
            _initRegisterBusyModal();
        }
    }
})(jQuery,window, document, jQuery(".jq-registerStepOneForm .smsCodeBody .smsCodeWrapContainer").length == 0);

/**
 * @desc password Reset Module
 * @namespace org.bt.modules.passwordReset
 */
org.bt.modules.passwordReset = (function($,window,document) {

    /**
     * Bind DOM events function
     * @private
     */
    var _bindDOMEvents = function(){

        //placeholder
        $('.jq-passwordResetSmsCode')
            .placeholder()
            .keyup(_onGetSMSCodeKeyUp)
            .focusout(_onGetSMSCodeFocusOut);

        $('.jq-forgottenPassword').bind('click',function(ev){_onForgottenPasswordLinkClick(ev);});

        /**
         * Step One
         */

        //back button
        $('.jq-passwordResetBackButton').bind('click',function(ev){_onPasswordResetBackButtonClick(ev);});

        //on get SMS code button click
        $('.jq-passwordResetGetSMSCodeButton').bind('click',function(ev){_onGetSMSCodeButtonClick(ev);});
        $('.jq-forgottenpasswordTryAgain').bind('click',function(ev){_onforgottenpasswordTryAgain(ev);});

        $('.jq-passwordResetStepOneSubmitButton').bind('click',function(ev){_onPasswordResetStepOneSubmitButtonClick(ev);});
        $(".jq-passwordResetStepOneForm").smsCode({formSubmit:true,formSubmitButton: '.jq-passwordResetStepOneSubmitButton',skipAnalyze: true});
        //init validation engine for register step one
        $('.jq-passwordResetStepOneForm').validationEngine(
            {
                ajaxSubmitUrl		:null,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                escapeHtml          : false,
                submitOnSuccess     :true,
                onValidationComplete:function($form,success){
                    if($form.attr('data-action')!=='smsCode'){
                        $('.jq-passwordResetStepOneSubmitButton').find(".iconWLoader").removeClass("noDisplay");
                        $('.jq-passwordResetStepOneSubmitButton').find(".label-content").addClass("noDisplay");
                        $('.forgotPasswordCancelBtn').addClass("disabled");
                    }
                    _onPasswordResetStepOneValidationComplete($form,success);
                },
                onSubmitSuccess     :function(data){
                    _onPasswordResetStepOneSubmitSuccess(data);
                }
            }
        );

        /**
         * Step Two
         */

        $('.jq-passwordResetCancelButton').bind('click',function(ev){_onPasswordResetBackButtonClick(ev);});
        //bind password policy
        $('.jq-passwordResetNewPassword').passwordPolicy({'userNameElement':'.jq-passwordResetUsername','hintElement':'.jq-passwordResetPasswordPolicyHintsContainer'});

        //init validation engine for register step two
        $('.jq-passwordResetStepTwoForm').validationEngine(
            {
                ajaxValidationUrl   :null,
                ajaxSubmitUrl		:org.bt.utils.serviceDirectory.resetPasswordStep2,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                submitOnSuccess     :false,
                onValidationComplete:function($form,success){
                    org.bt.utils.log.info('form '+$form+' success '+success);
                    _onResetPasswordStepTwoValidationComplete($form,success);
                }
            }
        );

        //confirm password blur
        $('.jq-passwordResetConfirmPassword').bind({
            focusout:function(ev){
                _onConfirmPasswordBlur.apply(this,[ev]);
            },
            keyup: function(ev) {
                _onConfirmPasswordKeyUp.apply(this,[ev]);
            }
        });


    };//End DOM Event binding

    /**
     * Omniture Changes Forgotten Password step 1
     */


    /**
     * Fired when on sms code key up
     * @param ev
     * @private
     */
    var _onGetSMSCodeKeyUp = function(ev){
        var $element    = $(this),
            charLength  = parseInt($element.attr('data-length'),10),
            $button     = $('.jq-passwordResetStepOneSubmitButton');

        if($element.val().length === charLength){
            $button.removeClass('disabled');
        } else {
            $button.addClass('disabled');
        }
    };

    /**
     * Fired when on SMS code focus out
     * @param ev
     * @private
     */
    var _onGetSMSCodeFocusOut = function(ev){
        var $smsInput   = $(this),
            value       = $.trim($smsInput.val()),
            charLength  = parseInt($smsInput.attr('data-length'),10),
            $form       = $('.jq-passwordResetStepOneForm');


        if(value === '' || value === $smsInput.attr('data-placeholder') || value.length !== charLength){
            $form.addClass('jq-preventSubmit');
            var forgotPasswordStepOneNextbuttonEnabled = $( '.jq-passwordResetSmsCode' ).hasClass('textInputError');
            $('.jq-passwordResetStepOneSubmitButton').addClass('disabled');

        } else {

            $.removeMessage($smsInput,['jq-inputError','formFieldMessageError'],'textInputError');
            $form.removeClass('jq-preventSubmit');
            $('.jq-passwordResetStepOneSubmitButton').removeClass('disabled');
        }
    };

    /**
     * Fired when on forgotten password link click
     * @param e
     * @private
     */
    var _onForgottenPasswordLinkClick = function(e){
        var $logonContainer         = $('#jq-logon'),
            $resetPasswordContainer = $('#jq-passwordReset'),
            $resetPasswordStepOne   = $('.jq-passwordResetStepOne'),
            $resetPasswordStepTwo   = $('.jq-passwordResetStepTwo'),
            $smsCode                = $('.jq-passwordResetSmsCode'),
            $smsMessage             = $('.jq-passwordResetSmsNumberMessage'),
            $smsButton              = $('.jq-passwordResetGetSMSCodeButton'),
            $nextButton             = $('.jq-passwordResetStepOneSubmitButton'),
            $notices                = $('.noteBoxWrap'),
            $toggleMenuItem         = $('.toggleMenuLineItem'),
            analyzeData = {eventType:'', eventDescription: '', clientDefinedEventType:'FORGOTTEN_PASSWORD'},
            $disclaimer             = $('.jq-disclaimer'),
            $contactNumberField     = $('.jq-contactNumber');

        e.preventDefault();

        // hide errors
        var tab = $('.jq-forgottenPassword').attr('href');
        $('.jq-mainContent').tabs({
            active: 2
        });
        $('.jq-linkPasswordReset').removeClass('noDisplay');
        $logonContainer.find('.noticeBox').hide();
        $notices.removeClass('noteBoxWrapMod1');

        // Show contact details
        $contactNumberField.removeClass('noDisplay');

        $resetPasswordContainer.find('.jq-formClear').trigger('click'); //reset form
        $resetPasswordStepOne.show();
        $resetPasswordStepTwo.hide();
        forgotPasswordStep1Omniture();
        $.removeMessage($smsCode,['jq-inputError','formFieldMessageError'],'textInputError');
        $nextButton.addClass('primaryButtonDisabled').attr('aria-disabled','true');
        $smsMessage.addClass('noDisplay');
        $('.jq-passwordResetStepOneForm').addClass('jq-preventSubmit');
        $disclaimer.hide();

        $contactNumberField.removeClass('noDisplay');
    };

    /**
     * Fired when on password reset button click
     * @param e
     * @private
     */
    var _onPasswordResetBackButtonClick = function(e){
        var $logonContainer         = $('#jq-logon'),
            $resetPasswordContainer = $('#jq-passwordReset'),
            $resetPasswordStepOne   = $('.jq-passwordResetStepOne'),
            $resetPasswordStepTwo   = $('.jq-passwordResetStepTwo'),
            $toggleMenuItem         = $('.toggleMenuLineItem'),
            $disclaimer             = $('.jq-disclaimer'),
            $contactNumberField     = $('.jq-contactNumber');

        e.preventDefault();

        $resetPasswordContainer.addClass('noDisplay').hide().attr('aria-expanded','false').attr('aria-hidden','true');
        $logonContainer.removeClass('noDisplay').show().attr('aria-expanded','true').attr('aria-hidden','false');
        $resetPasswordStepOne.show();
        $resetPasswordStepTwo.hide();
        $toggleMenuItem.eq(0).addClass('ui-state-active');
        $disclaimer.show();

        $contactNumberField.addClass('noDisplay');
    };

    /**
     * Fired when on SMS code button click
     * @param e
     * @private
     */
    var _onGetSMSCodeButtonClick = function(e){
        var $smsCode    = $('.jq-passwordResetSmsCode');

        e.preventDefault();
        $('.jq-passwordResetStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.forgotPassword)
            .attr('data-action','smsCode')
            .removeClass('jq-preventSubmit');

        $.removeMessage($smsCode,['jq-inputError','formFieldMessageError'],'textInputError');

        $('.jq-passwordResetSmsNumberMessage').addClass('noDisplay');
    };

    var _onforgottenpasswordTryAgain = function(e){
        var $smsCode    = $('.jq-passwordResetSmsCode');

        e.preventDefault();
        $('.jq-passwordResetStepOneForm')
            .attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.forgotPassword)
            .attr('data-action','smsCode')
            .removeClass('jq-preventSubmit');

        $.removeMessage($smsCode,['jq-inputError','formFieldMessageError'],'textInputError');
    };

    /**
     * Fired on the Forget Password panel (step 1 forgot password), when the Next button is clicked.
     * @param e
     * @private
     */
    var _onPasswordResetStepOneSubmitButtonClick = function(e){
        var $form       = $('.jq-passwordResetStepOneForm');

        e.preventDefault();
        $form.attr('data-ajax-submit-url',org.bt.utils.serviceDirectory.forgetPasswordVerifySms).attr('data-action','resetPasswordStep1');
    };

    /**
     * Fired when on register step one validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onPasswordResetStepOneValidationComplete = function($form,success){
        var $button     = $('.jq-passwordResetGetSMSCodeButton'),
            $busyIcon   = $('.jq-passwordResetSmsBusyDialogContainer'),
            $smsInput   = $('.jq-passwordResetSmsCode'),
            action      = $form.attr('data-action');

        //Have to find which action is trigger
        if(action === 'smsCode'){
            if(success){
                $form.find('input[name="deviceToken"]').val(encode_deviceprint());
                $button.find('.icon').removeClass('icon-mobile-send').addClass('iconWLoader');
            }
        } else {
            //registration step one after basic validation
        }
    };

    /**
     * Fired when on register step one submit request success
     * @param data
     * @private
     */
    var _onPasswordResetStepOneSubmitSuccess = function(data){
        var $button     = $('.jq-passwordResetGetSMSCodeButton'),
            $busyIcon   = $('.jq-passwordResetSmsBusyDialogContainer'),
            $message    = $('.jq-passwordResetSmsNumberMessage'),
            $smsError    = $('.jq-smsCodeErrorContainer'),
            $smsInput   = $('.jq-passwordResetSmsCode'),
            $stepOne    = $('.jq-passwordResetStepOne'),
            $stepTwo    = $('.jq-passwordResetStepTwo'),
            $form       = $('.jq-passwordResetStepOneForm'),
            $eamFrom    = $('.jq-authWithEamForgotPasswordForm'),
            action      = $form.attr('data-action'),
            $notices    = $('.noteBoxWrap');
        $form.removeClass('jq-preventSubmit');
        //Have to find which action is trigger

        if(action === 'smsCode'){
            $('.jq-FormErrorMessage').html('').addClass('noDisplay');
            $button.removeClass('noDisplay');
            $form.addClass('jq-preventSubmit');
            if(data.success){
                $message.removeClass('noDisplay');
                $message.addClass('smsNumberMessageShow');
                $smsInput.removeClass('noDisplay');
                $button.addClass('noDisplay');
                $button.find('.actionButtonIconText').html('Try again');
                $button.find('.icon').removeClass('iconWLoader').addClass('icon-mobile-send');
                $smsError.addClass('noDisplay');
                $notices.removeClass('noteBoxWrapMod1');
                $('.jq-passwordResetHeader'); //.removeClass('mod4').addClass('mod3');
            } else {
                //display server side error
                $.showServerSideErrors(data,$form);
                $(document).scrollTop(0);
                $smsError.removeClass('noDisplay');
                $smsInput.addClass('noDisplay');
                $button.find('.icon').removeClass('iconWLoader').addClass('icon-mobile-send');
                $notices.addClass('noteBoxWrapMod1');
                $('.jq-passwordResetHeader'); //.removeClass('mod3').addClass('mod4');
            }
        } else {
            if(data.success){

                $eamFrom.attr('action',data.data.eamPostUrl);
                $eamFrom.find('input[name="RelayState"]').val(data.data.relayState);
                $eamFrom.find('input[name="SAMLResponse"]').val(data.data.samlresponse);
                $eamFrom.find('input[name="deviceToken"]').val(encode_deviceprint());
                $eamFrom.submit();
                $('.jq-passwordResetStepOneSubmitButton').find(".iconWLoader").removeClass("noDisplay");
                $('.jq-passwordResetStepOneSubmitButton').find(".label-content").addClass("noDisplay");
                $('.forgotPasswordCancelBtn').addClass("disabled");
                $('.jq-registerStepOneSubmitButton').find(".iconWLoader").removeClass("noDisplay");
                $('.jq-registerStepOneSubmitButton').find(".label-content").addClass("noDisplay");
                $('.registrationCancelBtn').addClass("disabled");
            } else {
                //display server side error
                $.showServerSideErrors(data,$form);
                $(document).scrollTop(0);
                $notices.addClass('noteBoxWrapMod1');
                $('.jq-passwordResetStepOneSubmitButton').find(".iconWLoader").addClass("noDisplay");
                $('.jq-passwordResetStepOneSubmitButton').find(".label-content").removeClass("noDisplay");
                $('.forgotPasswordCancelBtn').removeClass("disabled");
                $('.jq-registerStepOneSubmitButton').find(".iconWLoader").addClass("noDisplay");
                $('.jq-registerStepOneSubmitButton').find(".label-content").removeClass("noDisplay");
                $('.registrationCancelBtn').removeClass("disabled");
            }
        }
    };

    /**
     * Fired when on register step two validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onResetPasswordStepTwoValidationComplete = function($form,success){
        if(success){
            //run bank password policy validator
            var $passwordField  = $('.jq-passwordResetNewPassword'),
                invalidPassword  = $passwordField.hasClass('jq-inputError'),
                $userNameField =  $('.jq-passwordResetUsername');

            if(!invalidPassword){
                $.removeMessage($passwordField,['jq-inputError','formFieldMessageError'],'textInputError');
                //no errors. Submit the request to backend
                $('.jq-registerBusyDialog').dialog( "open" );
                $('.ui-widget-overlay').addClass('modalBG');

                org.bt.utils.log.info('Submit form.....');
                var data    = $.createFormSubmitRequestBody($form),
                    action  = org.bt.utils.serviceDirectory.resetPasswordStep2;

                org.bt.utils.communicate.ajax(
                    {
                        url:action,
                        type:'POST',
                        data:data,
                        onSuccess:function(res){
                            if(res.success){
                                org.bt.utils.log.info('redirect user');
                                var url = $('.jq-passwordResetStepTwoSubmitButton').attr('data-href');
                                //org.bt.utils.communicate.get({action:url});

                                //TODO: Find a better way to navigate to dashboard...
                                $('.jq-logonForm').find('.jq-logonUsername').val($userNameField.val());
                                $('.jq-logonForm').find('.jq-logonPassword').val($passwordField.val());
                                $('.jq-logonForm').find('.jq-formSubmit').trigger('click');
                            } else {
                                $('.jq-registerBusyDialog').dialog( "close" );
                                $.showServerSideErrors(res,$form);
                                $(document).scrollTop(0);
                            }
                        },
                        onError:function(){}
                    }
                );

            } else {
                $.promptMessage($passwordField,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
            }
        }

    };

    /**
     * Fired when on confirm password on blur
     * @param e
     * @private
     */
    var _onConfirmPasswordBlur = function(e){
        var $element    = $(this),
            $form       = $element.parents('form:first'),
            value       = $.trim($element.val()),
            password    = $.trim($('.jq-passwordResetNewPassword').val());

        if(password !== '' && value !== ''){
            if(password !== value){
                $.promptMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError','');
                $form.addClass('jq-preventSubmit');
                $('.confirmpassword .validation-container .icon-notification-success').addClass('noDisplay');
            } else {
                $form.removeClass('jq-preventSubmit');
                $('.confirmpassword .validation-container .icon-notification-success').removeClass('noDisplay');
            }
        }
    };

    /**
     * Fired when confirm password keyUp
     * @param e
     * @private
     */
    var _onConfirmPasswordKeyUp = function(e){
        var $element    = $(this),
            value       = $.trim($element.val()),
            password    = $.trim($('.jq-passwordResetNewPassword').val());

        $element.siblings('.iconCheck').addClass('noDisplay');

        if(password !== '' && value !== ''){
            if(password === value){
                $element.addClass('valid');
                $element.siblings('.iconCheck').removeClass('noDisplay');
                $('.confirmpassword .validation-container .icon-notification-success').removeClass('noDisplay');
            }
        }
    };

    /**
     * Fired on init
     * @private
     */
    var _storeHashInCookie = function(){
        var hash = window.location.hash;
        if(hash && (/^#ng/.test(hash))) {
            $.cookie('redirect_to', hash, {
                path: '/'
            });
        }
    };


    /**
     * Init method for module
     * @private
     */
    var _init = function(){
        _storeHashInCookie();
        _bindDOMEvents();
    };
    return{
        /**
         * @memberOf org.bt.modules.passwordReset
         * @public
         */
        init:function(){
            _init();
        }
    }
})(jQuery,window, document);