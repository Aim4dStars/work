org.bt.modules.registerStepTwo = (function($,window,document) {
    /**
     * Bind DOM Events function
     * @private
     */
	function PopIt() { return "This page is asking you to confirm that you want to leave - data you have entered may not be saved."; }
    function UnPopIt()  { /* nothing to return */ } 
	$(document).ready(function(){  
	    //$(".BlueContainer").css('display','inline-table');
	    window.onbeforeunload = PopIt;
		$(".jq-registerStepTwoSubmitButton").click(function(){
			window.onbeforeunload = UnPopIt;
		});
	});

    var hostNameArray = window.location.hostname.split('.');
    var hostName = hostNameArray[1];
    if (hostName === 'panoramaadviser') {
        $('.registerPlaceholder').html('Professionals registration');
    } else if (hostName === 'panoramainvestor') {
        $('.registerPlaceholder').html('Investor registration');
    } else {
        $('.registerPlaceholder').html('Register');
    }
	
    var _DOMElements = {
        termsAndCondWrapper: '.jq-termsAndCondWrapper',
        formTcCheckBox: '.jq-formTcCheckBox',
        regTermsAndCondHidden: '.jq-regTermsAndCondHidden',
        adviserTermsCondWrapper: '.jq-adviserTermsCondWrapper',
		halgmField                                  :'.jq-halgmField',
        brandField                                  :'.jq-brandField',
		registerStepTwoForm						    :'.jq-registerStepTwoForm',
		usernameField                               :'.jq-registerUsername',
		passwordField                               :'.jq-registerPassword',
		confirmpasswordField						:'.jq-registerConfirmPassword',
		usernameHintWrap                            :'.jq-usernamePolicyHintsContainer'
    };
		/**
	* @desc keep the key map as a private property, has the proposed cipher
	* @type Object
	* @private
	*/
	var _keyMap = {};
	var isadviser = $('.jq-isAdvisorHidden').val();
	var passwordSettings = {};
	
	
	
    var _bindDOMEvents = function(){
        /**
         * Step Two
         */
        org.bt.utils.communicate.ajax.call(this, {
                'type':'GET',
                'async': false,
                'url':org.bt.cryptoUrl +'?uid='+$('.jq-registerStepTwo #registrationCode').val(),
                'onSuccess':function(res){
                    passwordSettings = res;
                }
        });

        $(_DOMElements.termsAndCondWrapper).on('click', _DOMElements.formTcCheckBox, function(event){
            _onTcCheckBoxClick.call(this,event);
        });

        //bind password policy
        $('.jq-registerPassword').passwordPolicy({'userNameElement':'.jq-registerUsername',
                                                  'hintElement':'.jq-passwordPolicyHintsContainer',
                                                  'minLength':passwordSettings.inputRestrictions.password.minLength,
                                                  'maxLength':passwordSettings.inputRestrictions.password.maxLength,
                                                  'validChars': new RegExp(passwordSettings.inputRestrictions.password.pattern)});
		$(_DOMElements.usernameField).usernamePolicy({userNameElement:_DOMElements.registerStepTwoForm,hintElement:_DOMElements.usernameHintWrap});

        //init validation engine for register step two
        $('.jq-registerStepTwoForm').validationEngine(
            {
                ajaxValidationUrl   :org.bt.utils.serviceDirectory.validateRegistration,
                ajaxSubmitUrl		:org.bt.utils.serviceDirectory.registrationStep2,
                ajaxSubmitType      :'GET',
                dataType            :'json',
                submitOnSuccess     :false,
				customFunctions:{
                    //modifiedUsername:_validateModifiedUsername
					newUserName:_validateAgainstUsernamePolicy,
					password:_validateAgainstPasswordPolicy
                },
                onValidationComplete:function($form,success){
                	$('.jq-registerStepTwoSubmitButton').removeClass('disabled');
                    org.bt.utils.log.info('form '+$form+' success '+success);                   
                    _onRegisterStepTwoValidationComplete($form,success);                   
                    
                }
            }
        );
        
        //confirm password blur
        $('.jq-registerConfirmPassword').bind({
            focusout:function(ev){
                _validateRegisterConfirmPassword.apply(this,[ev]);
                _enableSigninButton.apply(this,[ev]);
            },
            keyup: function(ev) {
                _onRegisterConfirmPasswordKeyUp.apply(this,[ev]);               
            }
        });
        $(_DOMElements.usernameField).bind({
            focusout:function(ev){
               _enableSigninButton.apply(this,[ev]);
            }           
        });
        $(_DOMElements.passwordField).bind({
            focusout:function(ev){
            	_enableSigninButton.apply(this,[ev]);
            },            
        });

    };//End DOM Event binding
    
    $('.TNCLabel').click(function(){
    	$('.TNCLabel').toggleClass('selected');
    	$('#regStepTC1').attr('aria-checked',!$(this).attr('true'));    	
    });
	var _validateAgainstUsernamePolicy = function(rules,value,isStillValid){
		//only trigger password policy validator if basic validation is success
		var config,
			errors;

		if(isStillValid){
			errors = $(_DOMElements.usernameField).usernamePolicy('validate');
			if(errors.length > 0){
				//display only a one message at a time!
				rules.push({'name':errors[0].split('.')[1],'matched':false});
			} 
			/*else {
				//do the server side validation.
				config = {
					url:org.bt.utils.serviceDirectory.validateNewPassword,
					async:false,
					data:{userName:$(_DOMElements.usernameField).val(),newPassword:value},
					onSuccess:function(data){
						if(!data.success){
							$(_DOMElements.passwordField).attr('data-validation-server-error',data.data);
							this.push({'name':'server','matched':false});
							return this;
						}
					}
				};
				org.bt.utils.communicate.ajax.call(rules,config);
			}*/
		}
		return rules;
	};
	
	var _validateAgainstPasswordPolicy = function(rules,value,isStillValid){
		//only trigger password policy validator if basic validation is success
		var config,
			errors;

		if(isStillValid){
			errors = $(_DOMElements.passwordField).passwordPolicy('validate');
			if(errors.length > 0){
				//display only a one message at a time!
				rules.push({'name':errors[0].split('.')[1],'matched':false});
			} 
			/*else {
				//do the server side validation.
				config = {
					url:org.bt.utils.serviceDirectory.validateNewPassword,
					async:false,
					data:{userName:$(_DOMElements.passwordField).val(),newPassword:value},
					onSuccess:function(data){
						if(!data.success){
							$(_DOMElements.passwordField).attr('data-validation-server-error',data.data);
							this.push({'name':'server','matched':false});
							return this;
						}
					}
				};
				org.bt.utils.communicate.ajax.call(rules,config);
			}*/
		}
		return rules;
	};

    /**
     * Fired when on register step two validation request complete
     * @param $form
     * @param success
     * @private
     */
    var _onRegisterStepTwoValidationComplete = function($form,success){
        if(success){
        	$('.jq-registerStepTwoSubmitButton').removeClass('disabled');
            //check password is a valid one or not
            var $passwordField          = $('.jq-registerPassword'),
                validatePassword        = $passwordField.passwordPolicy('validate'),
                invalidPassword         =  (validatePassword.length > 0),
                validConfirmPassword    = _validateRegisterConfirmPassword.apply($('.jq-registerConfirmPassword')[0]),
                regTermsAndCondHidden   = $('.jq-regTermsAndCondHidden'),
                noticeBoxTC			    = $('.jq-noticeBoxTC').removeClass('noDisplay').hide(),
                termsCondErrMessage	    = $('.jq-termsCondErrMessage'),
                adviserTermsCondWrapper = $('.jq-adviserTermsCondWrapper').attr('data-validate'),
                regTermsCondCheck = true;

            if(!invalidPassword){
                $.removeMessage($passwordField,['jq-inputError','formFieldMessageError'],'textInputError','');
                $('.jq-registerStepTwoSubmitButton').removeClass('disabled');
            }else {
                $.promptMessage($passwordField,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
                $('.jq-registerStepTwoSubmitButton').removeClass('disabled');                

            }


            //Check Terms and Condition
            if(adviserTermsCondWrapper === 'true'){
                if(regTermsAndCondHidden.val() === 'false'){
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
            if(!invalidPassword  && regTermsCondCheck){
				_initCrypto($form);
            }
        }
    };

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

        	if(password === null && value === null){
        	 $.promptMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError','');        	 
             $form.addClass('jq-preventSubmit');
             //$element.siblings('.iconCheck').addClass('noDisplay');
             $('.confirmpassword .validation-container .icon-notification-success').addClass('noDisplay');            
        	}
        	if(password !== null && value !== null && password !== value){
                $.promptMessage($element,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');               
                $form.addClass('jq-preventSubmit');
                //$element.siblings('.iconCheck').addClass('noDisplay');
                $('.confirmpassword .validation-container .icon-notification-success').addClass('noDisplay');                
            } 
        	if(password.length > 1 && password === value){
                $form.removeClass('jq-preventSubmit');
				$(_DOMElements.usernameField).focusout();
				//$element.siblings('.iconCheck').removeClass('noDisplay');
				$('.confirmpassword .validation-container .icon-notification-success').removeClass('noDisplay');				
				$.removeMessage($element,'required',['jq-inputError','formFieldMessageError'],'textInputError',''); 
				$.removeMessage($element,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
				$('.jq-registerStepTwoSubmitButton').removeClass('disabled');
				
		             
            }
       
       
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
                $('.confirmpassword .validation-container .icon-notification-success').removeClass('noDisplay');
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
     * @description set the encrypt data to collections
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
     * @desc encrypt the password on submit
     * @private
     */
    var _encryptPassword = function(){
        var $form       = $(_DOMElements.registerStepTwoForm);

        $form.find('input[type="password"]').each(function(){
            var $el   = $(this),
                text  = $el.val(),
                value = [];

            value = _.map(text, _applyCipher);

            $el.val(value.join(''));
        });

    };

    //sign in button to enabled 
    $(".jq-formTcCheckBox").click( function() {
		   _enableSigninButton.apply(this,[0]);
    });
    var _enableSigninButton = function(){
    	if(isadviser === 'false'){    	
    		usernameerrors = $(_DOMElements.usernameField).hasClass('jq-validField');
    		passworderrors = $(_DOMElements.passwordField).hasClass('jq-validField');
    		confirmpassworderrors = $(_DOMElements.confirmpasswordField).hasClass('jq-validField');
    		if(usernameerrors == true && passworderrors == true && confirmpassworderrors == true){   
        		$('.jq-registerStepTwoSubmitButton').removeClass('disabled');
            	$('.jq-registerStepTwoForm').removeClass('jq-preventSubmit');    		
        	} else{    			
    			$('.jq-registerStepTwoSubmitButton').addClass('disabled');
    			$('.jq-registerStepTwoForm').addClass('jq-preventSubmit');
    		}
    	}
    	if(isadviser === 'true'){
    		var TNCchecked = $('.jq-formTcCheckBox').prop( "checked");
    		usernameerrors = $(_DOMElements.usernameField).hasClass('jq-validField');
    		passworderrors = $(_DOMElements.passwordField).hasClass('jq-validField');
    		confirmpassworderrors = $(_DOMElements.confirmpasswordField).hasClass('jq-validField');
    		if(TNCchecked == true && usernameerrors == true && passworderrors == true && confirmpassworderrors == true){    			
    			$('.jq-registerStepTwoSubmitButton').removeClass('disabled');
    			$('.jq-registerStepTwoForm').removeClass('jq-preventSubmit');
    		}
    		else{    			
    			$('.jq-registerStepTwoSubmitButton').addClass('disabled');
    			$('.jq-registerStepTwoForm').addClass('jq-preventSubmit');
    		}    		
    	}
   	};
    
   
   
    /**
     * @desc set form params on page load using EAM data
     * @private
     */
    var _setFormParams = function(){
        var $form       = $(_DOMElements.registerStepTwoForm),
            operation   = null;

        $(_DOMElements.halgmField).val(org.bt.collections.cryptography.keymap.halgm);

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
     * @desc return the converted character given the current cipher, no cipher text will leave character unchanged
     * @private
     */
    var _applyCipher = function(character){
        var cipherCharacter = _keyMap[character];

        // return original character is cipher doesn't have a mapping
        return cipherCharacter ? cipherCharacter : character;
    };
	var _initCrypto = function($form){
        org.bt.collections.cryptography = passwordSettings;
        _preProcessData();
        _setFormParams();
        _encryptPassword();

        /*$('.jq-registerBusyDialog').dialog( 'open' );
        $('.ui-widget-overlay').addClass('modalBG');*/
        $('.jq-registerStepTwoSubmitButton').find(".iconWLoader").removeClass("noDisplay");
        $('.jq-registerStepTwoSubmitButton').find(".label-content").addClass("noDisplay");
        $('.jq-registerStepTwoSubmitButton').addClass('jq-preventSubmit');

        //add SAFI token
        $form.find('input[name="deviceToken"]').val(encode_deviceprint());
        org.bt.utils.log.info('Submit form.....');
        var data    = $.createFormSubmitRequestBody($form, false),
            action  = org.bt.utils.serviceDirectory.registrationStep2;


        org.bt.utils.communicate.ajax(
            {
                url:action,
                type:'POST',
                data:data,
                onSuccess:function(res){
                 if(res.success){
                        org.bt.utils.log.info('redirect user');
                        var url = $('.jq-registerStepTwoSubmitButton').attr('href'),
                        data = [{'name':'userId','value':res.data}];

                        org.bt.utils.communicate.get({data:data,action:org.bt.utils.serviceDirectory.registrationStep3});
                    } else {
                       // $('.jq-registerBusyDialog').dialog( "close" );
                        $('.jq-registerStepTwoSubmitButton').find(".iconWLoader").addClass("noDisplay");
                        $('.jq-registerStepTwoSubmitButton').find(".label-content").removeClass("noDisplay");
                        $('.jq-registerStepTwoSubmitButton').removeClass('jq-preventSubmit');
                        $.showServerSideErrors(res,$form);
                        $(document).scrollTop(0);
                    }
                },
                onError:function(){
                    $('.jq-registerBusyDialog').dialog( "close" );
                }
            }
        );
    };
    var _init = function(){
        _bindDOMEvents();		
    };

    return{
        /**
         * @memberOf org.bt.modules.registerStepTwo
         * @public
         */
        init:function(){
            _init();
            _initRegisterBusyModal();
        }
    }
})(jQuery,window, document);