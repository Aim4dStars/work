/**
 * @desc temp password module
 * @namespace org.bt.modules.tempPassword
 */

$(document).ready(function(){  
   // $(".BlueContainer").css('display','inline-table'); 
});

org.bt.modules.tempPassword = (function($,window,document) {
    var _DOMElements = {
        form                :'.jq-passwordResetStepTwoForm',
        passwordField       :'.jq-passwordResetNewPassword',
        confirmPasswordField:'.jq-passwordResetConfirmPassword',
        halgmField          :'.jq-halgmField',
        brandField          :'.jq-brandField',
		tokenField			:'.jq-tokenField'
    };
	
	/**
     * @desc keep the key map as a private property, has the proposed cipher
     * @type Object
     * @private
     */
    var _keyMap = {};

    var passwordSettings = {};
	
    /**
     * Bind DOM events function
     * @private
     */
    var _bindDOMEvents = function(){

        org.bt.utils.communicate.ajax.call(this, {
                'type':'GET',
                'async': false,
                'url':org.bt.cryptoUrl +'?uid=' + encodeURIComponent($.cookie("userNameResetPwd")),
                'onSuccess':function(res){
                    passwordSettings = res;
                    manipulateCallOutElements(passwordSettings);
                    //bind password policy
                    $(_DOMElements.passwordField).passwordPolicy({'userNameElement':'.jq-passwordResetUsername',
                                                                  'hintElement':'.jq-passwordResetPasswordPolicyHintsContainer',
                                                                  'minLength':passwordSettings.inputRestrictions.password.minLength,
                                                                  'maxLength':passwordSettings.inputRestrictions.password.maxLength,
                                                                  'validChars': new RegExp(passwordSettings.inputRestrictions.password.pattern)});
                }
        });


        //init validation engine for register step two
        $(_DOMElements.form).validationEngine(
            {
            	escapeHtml			:false,            
                ajaxValidationUrl   :null,
                ajaxSubmitUrl		:org.bt.utils.serviceDirectory.resetPasswordStep2,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                submitOnSuccess     :false,
                customFunctions :{
                    newpassword:_validateAgainstPasswordPolicy
                },
                onValidationComplete:function($form,success){
                    org.bt.utils.log.info('form '+$form+' success '+success);
                    _onResetPasswordStepTwoValidationComplete($form,success);
                }
            }
        );
       
        //confirm password blur
        $(_DOMElements.confirmPasswordField).bind({
            focusout:function(ev){
                _onConfirmPasswordBlur.apply(this,[ev]);
            },
            keyup: function(ev) {
                _onConfirmPasswordKeyUp.apply(this,[ev]);
            }
        });

        $(_DOMElements.passwordField).bind({
            focusout: function(ev) {
                var signInButton= '.jq-passwordResetStepTwoSubmitButton',
                    password = $.trim($('.jq-passwordResetNewPassword').val()),
                    confirmPassword = $.trim($('.jq-passwordResetConfirmPassword').val());
                if ($(_DOMElements.passwordField).passwordPolicy('validate').length > 0 || (confirmPassword.length>0 && password!= confirmPassword)) {
                    if(!$(signInButton).hasClass('disabled')) {
                      $(signInButton).addClass('disabled');
                    }
                    if (confirmPassword.length>0 && password != confirmPassword) {
                        $('.jq-passwordResetConfirmPassword').trigger('focusout');
                    }
                }
            }
        });

    };//End DOM Event binding

    var manipulateCallOutElements = function (pwdPolicy) {
       var regexNonAlphaNumeric =/.*[^a-z0-9].*$/i;

       var pwd = pwdPolicy.inputRestrictions.password;
       var pat = pwd.pattern;
       pat = pat.substring(pat.indexOf('[')+1, pat.lastIndexOf(']'));

       if (!(pat.indexOf("A-Z") !== -1 && pat.indexOf("a-z") === -1)) {
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-upperCase').hide();
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-lowerCase').hide();
       }

       pat = pat.replace("A-Z", "").replace("a-z", "").replace("0-9","");
       if (regexNonAlphaNumeric.test(pat)) {
            $('.jq-passwordResetPasswordPolicyHintsContainer .jq-number').hide();
       } else {
            $('.jq-passwordResetPasswordPolicyHintsContainer .jq-nonAlphabeticCharacter').hide();
       }

       if (pwd.minLength === pwd.maxLength){
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-maxLength span').text("More than " + pwd.maxLength + " characters");
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-minLength').hide();
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-mustbeText').text("Must be exactly "+pwd.maxLength+" characters, which include");
       } else {
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-maxLength').hide();
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-minLength span').text(pwd.minLength + " characters");
           $('.jq-passwordResetPasswordPolicyHintsContainer .jq-mustbeText').text("Must be at least");
       }
    };

    var _validateAgainstPasswordPolicy = function(rules,value,isStillValid){
        //only trigger password policy validator if basic validation is success
        var errors;

        if(isStillValid){
            errors = $(_DOMElements.passwordField).passwordPolicy('validate');
            if(errors.length > 0){
                //display only a one message at a time!
                rules.push({'name':errors[0].split('.')[1],'matched':false});
            }
        }
        return rules;
    };

    /**
     * Fired when on register step two validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onResetPasswordStepTwoValidationComplete = function($form,success){
        if(success){
            //no errors. Submit the request to backend
            /*$('.jq-tmpPasswordBusyDialog').dialog('open');
            $('.ui-widget-overlay').addClass('modalBG');*/
            $('.jq-passwordResetStepTwoSubmitButton').find(".iconWLoader").removeClass("noDisplay");
            $('.jq-passwordResetStepTwoSubmitButton').find(".label-content").addClass("noDisplay");
            $('.jq-passwordResetStepTwoSubmitButton').addClass('jq-preventSubmit');

            org.bt.utils.log.info('Submit form.....');
            //encrypt password
			_initCrypto($form);
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
            password    = $.trim($('.jq-passwordResetNewPassword').val()),
            signInButton= '.jq-passwordResetStepTwoSubmitButton';
        
        if(password === null && value === null){
       	 $.promptMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError','');        	 
            $form.addClass('jq-preventSubmit');
            $('.confirmpassword .validation-container .icon-notification-success').addClass('noDisplay');
            if(!$(signInButton).hasClass('disabled')) {
                $(signInButton).addClass('disabled');
            }
       	}
       	if(password !== null && value !== null && password !== value){
               $.promptMessage($element,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');               
               $form.addClass('jq-preventSubmit');
               $('.confirmpassword .validation-container .icon-notification-success').addClass('noDisplay');
               if(!$(signInButton).hasClass('disabled')) {
                   $(signInButton).addClass('disabled');
               }
        }
       	if(password.length > 1 && password === value && $(_DOMElements.passwordField).passwordPolicy('validate').length <= 0){
            valid= true;
            $form.removeClass('jq-preventSubmit');
            $(signInButton).removeClass('disabled');
		    $('.confirmpassword .validation-container .icon-notification-success').removeClass('noDisplay');
			$.removeMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError','');
			$.removeMessage($element,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
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
     * Init busy dialog
     * @private
     */
    var _initTempPasswordBusyModal = function(){
        var $dialogWindowElement    = $( '.jq-tmpPasswordBusyDialog' );

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
     * @desc encrypt the password on submit
     * @private
     */
    var _encryptPassword = function(){
        var $form       = $(_DOMElements.form);

        $form.find('input[type="password"]').each(function(){
            var $el   = $(this),
                text  = $el.val(),
                value = [];

            value = _.map(text, _applyCipher);

            $el.val(value.join(''));
        });
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
     * @description
     * @private
     */
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
     * @desc set form params on page load using EAM data
     * @private
     */
    var _setFormParams = function(){
        var $form       = $(_DOMElements.form),
            operation   = null;

        $(_DOMElements.halgmField).val(org.bt.collections.cryptography.keymap.halgm);
		$(_DOMElements.tokenField).val(org.bt.collections.cryptography.reference.token);
        //set form properties
        $.each(org.bt.collections.cryptography.operations,function(k,v){
            if(v.name === 'chgPwd'){
                operation = v;
                return false;
            }
        });

        if(operation !== null){
            $form.attr({'method':operation.method,'accept-charset':operation['accept-charset']});
        }
    };

    /**
     * Request the crypto javascript
     * @private
     */
    var _initCrypto = function($form){
        org.bt.collections.cryptography = passwordSettings;
        _preProcessData();
        _setFormParams();
        _encryptPassword();
        var data    = $.createFormSubmitRequestBody($form,false),
        action  = org.bt.utils.serviceDirectory.resetPasswordStep2;
        org.bt.utils.communicate.post({action:action,data:data});
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
         * @memberOf org.bt.modules.passwordReset
         * @public
         */
        init:function(){
            _initTempPasswordBusyModal();
            _init();			
        }
    }
})(jQuery,window, document);
