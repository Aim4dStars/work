/**
 * @fileOverview validationEngine jQuery plugin
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @requires $.promptMessage
 * @requires $.removeMessage
 * @requires $.validateDate
 * @requires $.createFormSubmitRequestBody
 * @requires $.resetForm
 * @example
 $('form#myForm').validationEngine({
     errorMessageClass :['jq-inputError','formFieldMessageError'],
     errorClass :'textInputError',
     clearButtonClass :'jq-formClear',
     submitButtonClass :'jq-formSubmit',
     genericErrorMessage :'Please enter a valid input',
     formatOnBlur :true,
     dateFormat :'dd mmm yyyy',
     submitOnSuccess :true,
     onSubmitSuccess :function(){},
     onSubmitError :function(){},
     ajaxSubmit :true,
     ajaxSubmitType :'POST',
     ajaxSubmitUrl :'controller/method/submit.do',
     ajaxValidationUrl :'controller/method/validation.do',
     dataType :'json',
     validateCustomFields:true,
     setFocus :true,
     customFunctions :{}
    }
 );

 <p>Formatting Inputs</p>
 Start of added contentBy default End of added content Valdiation engine will format Date, Number or Currency. If you have declared more than one validation rule for the input, then validation engine will do the formatting for last rule that you have declared.
 E.g.validate[required,custom[number],custom[currency]] will format as a currency.
 Start of added contentIf you want to exclude formatting add following data attribute to input. End of added content
 Start of added contentdata-format-value="false" End of added content

 <p>Sample html snippet</p>
 <i>Validation engine can validate multiple conditions. E.g. validate[required,custom[signedInteger]]. this will validate field is empty and input is signed integer</i>
 <input id="amount" name="amount" tabindex="2" data-validation="validate[required,custom[currency]]" data-validation-required-error="Field cannot be empty" data-validation-currency-error="Enter a valid input"/>

 <p>tips</p>
 If you need to validate particular type and if you need to display a custom message you must declare a new attribute.
 E.g. if custom validation type is currency then custom attribute has to be data-validation- currency -error

 <p>Available validation types</p>
 <ul>
 <li>required - <input name="amount" data-validation="validate[required]" data-validation-required-error="Custom message" /></li>
 <li>email - <input name="email" data-validation="validate[email]" data-validation-email-error="Custom message" /></li>
 <li>letters - <input name="letters" data-validation="validate[letters]" data-validation-letters-error="Custom message" /></li>
 <li>name - <input name="name" data-validation="validate[name]" data-validation-name-error="Custom message" /></li>
 <li>unsignedInteger - <input name="qty" data-validation="validate[unsignedInteger]" data-validation-unsignedInteger-error="Custom message" /></li>
 <li>signedInteger - <input name="qty" data-validation="validate[signedInteger]" data-validation-signedInteger-error="Custom message" /></li>
 <li>unsignedDecimal - <input name="price" data-validation="validate[unsignedDecimal]" data-validation-unsignedDecimal-error="Custom message" /></li>
 <li>signedDecimal - <input name="price" data-validation="validate[signedDecimal]" data-validation-signedDecimal-error="Custom message" /></li>
 <li>min - <input name="price" data-validation="validate[min]" data-min="5" data-validation-min-error="Custom message" /></li>
 <li>max - <input name="price" data-validation="validate[max]" data-max="15" data-validation-max-error="Custom message" /></li>
 <li>numericRange - <input name="price" data-validation="validate[numericRange]" data-min="2" data-max="15" data-validation-numericRange-error="Custom message" /></li>
 <li>alphaNumeric - <input name="char" data-validation="validate[alphaNumeric]" data-validation-alphaNumeric-error="Custom message" /></li>
 <li>currency - <input name="currency" data-validation="validate[currency]" data-validation-currency-error="Custom message" /></li>
 <li>date - <input name="date" data-validation="validate[date]" data-validation-date-error="Custom message" /></li>
 <li>past - <input name="past" data-validation="validate[past]" data-past="yyyy mm dd" data-validation-past-error="Custom message" /></li>
 <li>future - <input name="future" data-validation="validate[future]" data-future="yyyy mm dd" data-validation-future-error="Custom message" /></li>
 <li>customFunction - <input name="custom" data-validation="validate[customFunction]" data-validation-customFunction-error="Custom message" /></li>
 <li>ajax - <input name="ajax" data-validation="validate[ajax]" data-validation-ajax-error="Custom message" /></li>
 </ul>
 <p>Available date formats</p>
 1/1/12
 01/01/2012
 Jan 1 12
 Jan 1 2012
 1.1.12
 1-1-12
 1 Jan 12
 January 1 2012
 Jan 1
 1 Jan
 1st Jan

 <p>Handle a validation specific to a field</p>
 You can use custom function method to handle it. E.g. as follows
 customFunctions:{
    'repeatEndDate':function(rules,value){
    return _endDateFrequencyCheck(rules,value);
}
}
 above code chunck repeatEndDate is the name of the input and _endDateFrequencyCheck is the custom validation function define by the developer.
 You must return array back to the validation engine and if there is any error you can add that to array as follows
 rules.push({'name':anyName,'matched':boolean});

 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>
 * @name fn
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 * @memberOf $
 */

/**
 * @namespace $.fn.validationEngine
 */

(function($) {
    $.fn.validationEngine = function(options) {
        //default values for validation engine. These values can be override
        var defaults = {
                errorMessageClass	:['jq-inputError','formFieldMessageError'],
                errorClass			:'textInputError',
                clearButtonClass	:'jq-formClear',
                submitButtonClass	:'jq-formSubmit',
                genericErrorMessage	:'Please enter a valid input',
                formatOnBlur		:true,
                dateFormat			:'dd mmm yyyy',
                submitOnSuccess		:true,
                onValidationComplete:function(){},
                beforeSubmit        :function(e){return e},
                onSubmitSuccess		:function(){},
                onSubmitError		:function(){},
                ajaxSubmit			:true,
                ajaxSubmitType		:'POST',
                ajaxSubmitUrl		:'controller/method/submit.do',
                ajaxValidationUrl	:'secure/api/validateField',
                dataType			:'json',
                validateCustomFields:true,
                setFocus            :true,
                customFunctions :{},
				escapeHtml :true,
                focusToErrorField   :false,
				ajaxLoaderSettings 	:{
											showLoader:true,
											iconLoaderClass:'iconWLoader'
									}
            },
            timer = null;
        options = $.extend(defaults, options);
        return this.each(function() {
            var $form 			= $(this),
                $elements 			= $form.find('textarea, input, select').filter('[data-validation^="validate"]'),
                $clearBtn			= $form.find('.'+options['clearButtonClass']),
                $submitBtn			= $form.find('.'+options['submitButtonClass']),
                ajaxCache			= [],
                errors 				= [];

            /**
             * set ARIA tags
             */
            $elements.attr('aria-required','true');
            $elements.attr('aria-invalid','false');
            $submitBtn.attr('role','button');

            /**
             * Off auto complete
             */
            $elements.attr('autocomplete','off');

            /**
             * reset data-submit-value on keyup
             */
            $elements.bind('keyup', function(){
                var $element 			= $(this),
                    invalidField        = ($element.hasClass('jq-validField') === false),
                    forceClear          = (typeof $element.attr('data-force-clear') !== "undefined" ),
                    submitValue		    = (typeof $element.attr('data-submit-value') !== "undefined" && $element.attr('data-submit-value') !== false) ? $element.attr('data-submit-value'): false;

                if((submitValue && invalidField) || forceClear){
                    $element.attr('data-submit-value','NULL');
                }

                $element.removeClass('jq-validField');//reset
            });

            /*
             *	validate user input on blur
             *	This will be applicable to all the elements which has data-validation attribute and validate start value
             */
            $elements.bind('focusout', function(){

                var $element 			= $(this),
                    validationRules 		= $element.attr('data-validation').split(/\[|,|\]/),
                    submitValue				= (typeof $element.attr('data-submit-value') !== 'undefined' && $element.attr('data-submit-value') !== false) ? $element.attr('data-submit-value'): false,
                    value                   = (submitValue && submitValue !== 'NULL') ? submitValue : $.trim($element.val()),
                    floatValue 				= parseFloat(value.replace(/,/g, '')),
                    customFunction          = (typeof $element.attr('data-custom-function') !== 'undefined') ? $element.attr('data-custom-function') : false,
                    name 					= $element.attr('name'),
                    type                    = $element.attr('type'),
                    isCheckBox              = (typeof type !== 'undefined' && type === 'checkbox'),
                    valid					= true,
                    validating				= $element.hasClass('jq-validating'),
                    skip                    = $element.hasClass('jq-skip'), //developer can skip the validation depend on state
                    min						= removeFormattingFromNumbers($element.attr('data-min')),
                    max						= removeFormattingFromNumbers($element.attr('data-max')),
                    minLength               = $element.attr('data-min-length'),
                    maxLength               = $element.attr('data-max-length'),
                    minCharLength           = $element.attr('data-min-alphabetic-char'),
                    past					= $element.attr('data-past'),
                    future					= $element.attr('data-future'),
                    formatValue             = (typeof $element.attr('data-format-value') !== "undefined") ? /true/i.test($element.attr('data-submit-value')): true,
                    removeValidatingClass	= true,
                    rules					= [],
                    hasAjax                 = false,
                    date					= null;
                //data-min & data-max might have formatting

                if(!validating && !skip){
                    $element.addClass('jq-validating');
                    //iterate all the rules
                    $.each(validationRules,function(k,rule){
                        var matched = false;
                        switch(rule){
                            case 'disabled':
                                //field is disabled but required. User needs to enable the field before proceed
                                matched = !($element.is(':disabled'));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'required':
                                matched = (isCheckBox) ? $element.is(':checked') : /^\s*\S.*$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'email':
                                matched = /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalEmail':
                                matched = (value === '') || /^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'letters':
                                matched = /^[a-zA-Z\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalUnitNumber':
                                matched = (value === '') || /^[a-zA-Z0-9-\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'address':
                                matched = /^[a-zA-Z0-9-/'\s,]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalAddress':
                                matched = (value === '') || /^[a-zA-Z0-9-/'\s,]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'name':
                                matched = /^[a-zA-Z0-9-.&,/()'\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'lastname':
                                matched = /^[a-zA-Z0-9\&\,\/\_\(\)\<\>\+\.\'\-\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'searchText':
                                matched = /^[0-9a-zA-Z\.\'\-\,\/\{\}\&\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'suburb':
                                matched = /^[a-zA-Z0-9-/'\s]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalName':
                                matched = (value === '') || /^[a-zA-Z0-9-.&,/'\s]]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'username':
                                matched = /^[a-zA-Z0-9*&^%#!]+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'bsb':
                                //123-456 or 123456 or 123 456
                                matched = /^((\d{3})\-(\d{3})|\d{6}|(\d{3}) (\d{3}))$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'accountNumber':
                                matched = /^\d{5,9}$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'unsignedInteger':
                                matched = /^[-+]?\d*$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'signedInteger':
                                matched = /^\d+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalSignedInteger':
                                matched = (value === '') || /^\d+$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'unsignedDecimal':
                                matched = /^[-+]?\d+(\.\d+)?$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'phoneNumber':
                                matched = /^[-+]?\d+[\d+(\s)?]*$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;    
                                
                            case 'signedDecimal':
                                matched = /^\d+(\.\d+)?$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'min':
                                matched = (!isNaN(floatValue) && floatValue >= parseFloat(min));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'max':
                                matched = (!isNaN(floatValue) && floatValue <= parseFloat(max));
                                rules.push({'name':rule,'matched':matched});
                                break;
							case 'optionalMax':
                                matched = ((value === '') || floatValue <= parseFloat(max));
                                rules.push({'name':rule,'matched':matched});
								$element.attr('data-error-optionalmax',!matched);
                                break;	
                            case 'numericRange':
                                matched = (!isNaN(floatValue) && floatValue >= parseFloat(min) && floatValue <= parseFloat(max));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'minLength':
                                matched = (value.length >= parseFloat(minLength));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'optionalMinLength':
                                matched = (value === '') || (value.length >= parseFloat(minLength));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'maxLength':
                                matched = (value.length <= parseFloat(maxLength));
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'alphaNumeric':
                                matched = /^[A-Za-z\d\s]*$/i.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
							case 'alphaNumericWithAmpersand':
                                matched = /^[A-Za-z\d\s&]*$/i.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;	
                            case 'minAlphabeticChar':
                                var regX = new RegExp('[A-Za-z]{'+parseInt(minCharLength,10)+'}','i');
                                matched = regX.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'currency':
                                value = value.replace(/,/g,'');
                                matched = /^\$?([1-9]{1}[0-9]{0,2}(\,[0-9]{3})*(\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
							case 'optionalCurrency':
							if(value=='' || value=='$') {
								rules.push({'name':rule,'matched':true});
							} else {
							value = value.replace(/,/g,'');
							matched = /^\$?([1-9]{1}[0-9]{0,2}(\,[0-9]{3})*(\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/.test(value);
							$element.attr('data-error-optionalcurrency',!matched);
							rules.push({'name':rule,'matched':matched});
							}
							break;
                            case 'date':
                                rules = validateDateRule(date,matched,$element,rules,rule,value);
                                break;
                            case 'optionalDate':
                                if(value === '') {
                                    rules.push({'name':rule,'matched':true});
                                } else {
                                    rules = validateDateRule(date,matched,$element,rules,rule,value);
                                }
                                break;
                            case 'past':
                                rules = validatePastDateRule(date,matched,$element,rules,rule,past);
                                break;
                            case 'optionalPast':
                                if(value === '') {
                                    rules.push({'name':rule,'matched':true});
                                } else {
                                    rules = validatePastDateRule(date,matched,$element,rules,rule,past);
                                }
                                break;
                            case 'future':
                                rules = validateFutureDateRule(date,matched,$element,rules,rule,future);
                                break;
                            case 'optionalFuture':
                                if(value === '') {
                                    rules.push({'name':rule,'matched':true});
                                } else {
                                    rules = validateFutureDateRule(date,matched,$element,rules,rule,future);
                                }
                                break;
                            case 'searchNumber' :
                                matched = /^[0-9]/.test(value);
                                rules.push({'name':rule,'matched':matched});
                                break;
                            case 'customFunction':
                                var functionName = customFunction ? customFunction : name,
                                    customFunc = options.customFunctions[functionName],
                                    stillValid = true;

                                if(customFunc){
                                    //check up to now validation success or not
                                    for (var i=0; i<rules.length; i++){
                                        if(!rules[i].matched){
                                            stillValid = false;
                                            break;
                                        }
                                    }
                                    rules = customFunc.apply($element,[rules,value,stillValid]);
                                }
                                break;
                            case 'ajax':
                                hasAjax = true;
                                //set 150 millisecond delay
                                setTimeout(function(){
                                    //reload submit value and value after delay
                                    var basicValidationSuccess  = ($.inArray(name,errors) === -1),
                                        submitValue				= (typeof $element.attr('data-submit-value') !== "undefined" && $element.attr('data-submit-value') !== false) ? $element.attr('data-submit-value'): false,
                                        inputValue              = $.trim($element.val()),
                                        value                   = (submitValue && submitValue !== 'NULL') ? submitValue : inputValue;

                                    //basic validation success and value is not empty. So do server-side validation

                                    if(basicValidationSuccess && value !== ''){
                                        removeValidatingClass = false;
                                        value = (inputValue === '') ? inputValue : value; //user deleted input. therefore send empty input
                                        //ajax request configuration
                                        var config = {
                                            type            :'GET',
                                            url             :options['ajaxValidationUrl'],
                                            dataType        :'json',
                                            data            :{'fieldId':name,'data':value,'conversationId':$form.attr('action')},
                                            onSubmitSuccess :function(res){
                                                $element.removeClass('jq-validating'); //remove validating flag
                                                if(!res.success){
                                                    if($.inArray(name,errors) === -1){
                                                        errors.push(name);
                                                    }
                                                    $element.removeClass('jq-validField').attr('data-validation-custom-error',res.data);
                                                    //reset submit value
                                                    if(submitValue){
                                                        $element.attr('data-submit-value','NULL');
                                                    }
                                                    promptMessage($element,'custom'); //prompt error message
                                                }else{
                                                    errors.remove(name);
                                                    //remove error
                                                    $.removeMessage($element,options['errorMessageClass'],options['errorClass']);
                                                    //set submit value only if it is NULL
                                                    if(submitValue === 'NULL'){
                                                        $element.attr('data-submit-value',value);
                                                    }
                                                }
                                            },
                                            onSubmitError   :function(){
                                                $element.removeClass('jq-validating');
                                            }
                                        };
                                        //send ajax request
                                        ajaxRequest(config);
                                    }
                                },150);
                                break;
                        }

                    });//end validation rule iteration
                    //iterate rules
                    $.each(rules,function(k,rule){
                        if(!rule['matched']){
                            setTimeout(function(){
                                promptMessage($element,rule['name']);
                            },20);
                            if($.inArray(name,errors) === -1){
                                errors.push(name);
                            }
                            valid = false;
                            return false; //break the loop
                        }
                    });
                    if(valid){
                        //valid input
                        errors.remove(name);

                        $.removeMessage($element,options['errorMessageClass'],options['errorClass']);

                        //format value
                        if(formatValue){
                            var lastRule                = null,
                                availableFormatTypes    = ['date','past','future','optionalDate','optionalPast','optionalFuture','number','currency','optionalCurrency','optionalMax'];

                            for (var i=rules.length; i--;) {
                                lastRule = ($.inArray(rules[i]['name'], availableFormatTypes) === -1) ? false : rules[i];
                                //format input if rule matched
                                if(lastRule){
                                    formatInput($element,lastRule['name'],(typeof lastRule['value'] === 'undefined') ? value : lastRule['value']);
                                    break;
                                }
                            }
                        }

                    }
                    //remove validating flag
                    if(removeValidatingClass){
                        $element.removeClass('jq-validating');
                    }
                }
            });//end focusout event bind

            /*Bind Form Submit Event*/
            $submitBtn.bind('click',function(e){
                //get form and elements using current target
                var $button         = $(e.currentTarget),
                    $targetForm     = $button.parents('form:first'),
                    $targetElements = $targetForm.find('textarea, input, select').filter('[data-validation^="validate"]');

                e.preventDefault();

                //prevent submit
                if($targetForm.hasClass('jq-preventSubmit') || $button.hasClass('jq-disabled')||$targetForm.hasClass('jq-analyzeSubmitFailed')){
                    return;
                }
                $targetForm.addClass('jq-validationInProgress');
                org.bt.utils.log.info('clicked submit button');
                errors = []; //reset errors

                //fire before submit callback!
                errors = options.beforeSubmit.call($targetForm,errors);

                var ajaxValidationRequiredFields = 0;

                //fire on blur event in all the fields
                $targetElements.each(function(i){
                    //check is there any ajax validation required fields exists in the form
                    var $el            = $(this),
                        validationType = $el.attr('data-validation').match(/ajax/gi),
                        value		   = $.trim($el.val()),
                        invalid        = ($el.hasClass('jq-validField') === false); //only do the validation for not validated fields

                    if(validationType !== null && invalid){
                        //only require to wait if the value in not empty and value is not in the cache
                        var cacheKey = $el.attr('name')+'~'+value;
                        if($.inArray(cacheKey,ajaxCache) === -1 && value !== ''){
                            ajaxValidationRequiredFields ++;
                        }
                    }
                    //trigger blur event
                    if(invalid){
                        $(this).focusout();
                    }
                });

                //validate custom validation required fields
                if(options.validateCustomFields){
                    errors = validateCustomFields($targetForm,errors);
                }

                if(ajaxValidationRequiredFields > 0){
                    org.bt.utils.log.info('periodic ajax validation complete checker started');
                    //periodic checker
                    timer = setTimeout(function(){
                        if($targetForm.find('.jq-validating').length === 0){
                            //execute onValidationComplete private function
                            onValidationComplete($targetForm,errors);
                        }
                    },250);
                } else {
                    //execute onValidationComplete private function
                    onValidationComplete($targetForm,errors);

                }
            });
            /*Bind Form Clear event*/
            $clearBtn.bind('click',function(e){
                //get form and elements using current target
                var $targetForm     = $(e.currentTarget).parents('form:first');

                e.preventDefault();

                $targetForm.resetForm({errorFieldClass:options['errorClass'],errorMessageClasses:options['errorMessageClass']});
                errors = []; //reset errors
                ajaxCache = []; //reset ajaxCache
            });
        });//end iterate all the form elements

        /**
         * @memberOf $.fn.validationEngine
         * @desc remove formatting of numbers
         * @returns String
         */
        function removeFormattingFromNumbers(string){
            string = string ? string : '';
            string = string.replace(/,|\$/g, '');
            return string;
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc validateCustomFields private function
         * @param $form
         * @param errors
         * @private
         * @returns array
         */
        function validateCustomFields($form,errors){
            $form.find('input[data-submit-value="NULL"]').each(function(){
                var $element        = $(this),
                    name            = $element.attr('name'),
                    inputValue      = $.trim($element.val()),
                    placeholder     = (typeof $element.attr('placeholder') !== "undefined" && $element.attr('placeholder') !== false),
                    action          = (inputValue === '') ? 'required' : 'custom';

                promptMessage($element,(placeholder && $element.attr('placeholder') === inputValue) ? 'required' : action);
                errors.push(name);
            });
            return errors;
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc formatInput private function
         * @param $element
         * @param val
         * @private
         * @param type
         */
        function formatInput($element,type,val){
            switch(type){
                case 'date':
                case 'past':
                case 'future':
                    setDateToDatePicker($element,type,val);
                    break;
                case 'optionalDate':
                case 'optionalPast':
                case 'optionalFuture':
                    if(val !== '') {
                        setDateToDatePicker($element,type,val);
                    }
                    break;
                case 'number':
                    $element.val(parseFloat(val).toFixed(2));
                    break;
                case 'currency':
				case 'optionalCurrency':
				case 'optionalMax':
                    if(val.charAt(0) === '$'){
                        val = val.substring(1);
                    }
					if(val) {
                    var value =  parseFloat(val).toFixed(2);
                    $element.attr('data-submit-value',value);
                    $element.val( value.replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,"));
					}
					else {
						$element.attr('data-submit-value','');
					}
                    break;
            }
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc setDateToDatePicker private function
         * @param $element
         * @param val
         * @private
         * @param type
         */
        function setDateToDatePicker($element,type,val){
            $element.val($.formatDate(val,options.dateFormat));
            //update date picker
            var datepicker      = $element.attr('data-calendar'),
                hasDatepicker   = (typeof datepicker !== "undefined" && datepicker !== false);

            if(hasDatepicker){
                var currentDay      = $(datepicker).datepicker( 'getDate' );
                if(currentDay.getTime() !== val.getTime()){
                    $(datepicker).datepicker('setDate', val );
                }
            }
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc validate date rule private function
         * @param date
         * @param matched
         * @param $element
         * @param rules
         * @param rule
         * @param value
         * @returns Array
         */
        function validateDateRule (date,matched,$element,rules,rule,value){
            date = validateDate(value);
            matched = (date !== null);
            if(matched){
                $element.attr('data-date',date.getTime());
                //fix for tab out on date custom validations
                setDateToDatePicker($element,'date',date);
            }
            rules.push({'name':rule,'matched':matched,'value':date});
			$element.attr('data-error-date',!matched);
            return rules;
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc validate past date rule private function
         * @param date
         * @param matched
         * @param $element
         * @param rules
         * @param rule
         * @param past
         * @returns Array
         */
        function validatePastDateRule (date,matched,$element,rules,rule,past){
            date = new Date(parseInt($element.attr('data-date'),10));
            if(typeof date.getMonth === 'function'){
                var pastArr		= past.split('-'),
                    pastDate	= new Date(pastArr[0],parseInt(pastArr[1])-1,pastArr[2]);//yyyy mm dd
                matched = (pastDate.getTime() <= date.getTime());
            }
            rules.push({'name':rule,'matched':matched,'value':date});
			$element.attr('data-error-past',!matched);
            return rules;
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc validate future date rule private function
         * @param date
         * @param matched
         * @param $element
         * @param rules
         * @param rule
         * @param future
         * @returns Array
         */
        function validateFutureDateRule (date,matched,$element,rules,rule,future){
            date = new Date(parseInt($element.attr('data-date'),10));
            if(typeof date.getMonth === 'function'){
                var futureArr	= future.split('-'),
                    futureDate	= new Date(futureArr[0],parseInt(futureArr[1])-1,futureArr[2]);//yyyy mm dd
                matched = (futureDate.getTime() >= date.getTime());
            }
            rules.push({'name':rule,'matched':matched,'value':date});
			$element.attr('data-error-future',!matched);
            return rules;
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc onValidationComplete private function
         * @param $form as jQuery Object
         * @param errors as array
         * @private
         */
        function onValidationComplete($form,errors){
            //check values of custom submit values
            var $customSubmitErrorFields	= $form.find('input[data-submit-value="NULL"]'),
                errorCount					= errors.length + $customSubmitErrorFields.length;

            $form.removeClass('jq-validationInProgress');
            options.onValidationComplete($form,errorCount === 0);

            //focus to first error element
            if(errorCount >  0 && options.focusToErrorField) {
                //trigger on a delay since prompt message fires after 20 msec
                setTimeout(function(){
                    focusPageToErrorField($form);
                },25);
            }

            //submit form
            if(options.submitOnSuccess){
                if(errorCount === 0){
                    org.bt.utils.log.info('validation success');
                    //clear timer if not null
                    if(timer !== null){
                        window.clearTimeout(timer);
                    }
                    //valid form therefore send the request to server
                    submitForm($form);
                }
            }
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc focusPageToErrorField private function
         * @param $form
         */
        function focusPageToErrorField ($form){
            var $firstErrorEl   = $form.find('*[aria-invalid="true"],*[data-error-message]:visible').filter(':first');

            if($firstErrorEl.is('select')) {
                $firstErrorEl = $firstErrorEl.siblings('.jq-dropkickWrap').find('input');
            }
            $('html, body').animate({ scrollTop: $firstErrorEl.offset().top - 30}, 'slow',function(){
                $firstErrorEl.focus();
            });
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc submitForm private function
         * @param {Object} $form as jQuery object
         * @private
         */
        function submitForm($form){
            if(options['ajaxSubmit']){
				  var data = $.createFormSubmitRequestBody($form,options['escapeHtml']),
                    url     = (options.ajaxSubmitUrl !== null) ? options.ajaxSubmitUrl : $form.attr('data-ajax-submit-url');
               if(url===""){
                   return;
               }
                var config = {
                    type: 			options.ajaxSubmitType,
                    url: 				url,
                    dataType:			options.dataType,
                    data: 			data,
                    onSubmitSuccess:	options.onSubmitSuccess,
                    onSubmitError:	options.onSubmitError,
					ajaxLoaderSettings: options.ajaxLoaderSettings,
					$form: $form
                };

                ajaxRequest.call($form,config);
            } else {
                $form.submit();
            }
        }
        /**
         * @memberOf $.fn.validationEngine
         * @desc promptMessage private function
         * @param $element
         * @param rule
         * @private
         */
        function promptMessage($element,rule){
            //$element,rule,errorMessageClasses,errorFieldClass,genericErrorMessage
            $.promptMessage($element,rule,options['errorMessageClass'],options['errorClass'],options['genericErrorMessage']);
        }

        /**
         * @memberOf $.fn.validationEngine
         * @desc validateDate private function
         * @param value String
         * @returns {Date}
         * @private
         */
        function validateDate(value){
            return $.validateDate(value);
        }

        /**
         *  @memberOf $.fn.validationEngine
         *	ajaxRequest private function
         *	@param config object
         *  @private
         */
        function ajaxRequest(config){
            config.onSuccess = config.onSubmitSuccess;
            config.onError = config.onSubmitError;
            org.bt.utils.communicate.ajax.call(this,config);
        }
    }
})(jQuery);
