
/**
 * @namespace org.bt.modules.cashPayment
 * @desc Cash Payment Module
 */
org.bt.modules.cashPayment = (function($,window,document) {
    /**
     * DOM Element selector map
     * @private
     */
    var _DOMElements = {
        paymentForm:'.jq-cashPaymentMakePaymentForm',
        paymentFromCrnWrap:'.jq-crnOnlyField',
        dailyLimitFormWrap:'.jq-paymentIncreaseDaily',
        dailyLimitFormWrapPlaceholder:'.jq-paymentIncreaseDailyPlaceholder',
        payeeInput : '.jq-cashPaymentPayee',
        dailyLimitAmountList: '.jq-dailyLimitAmounts',
        exceedDailyLimit:'.jq-exceedDailyLimit',
        addPayeeBillerWrap:'.jq-paymentAddPayeeBiller',
        addPayeeBillerPlaceholder:'.jq-paymentAddPayeeBillerPlaceholder',
        addNewPayeeButton:'.jq-addNewPayeeButton',
        cashPaymentPaymentAmount:'.jq-cashPaymentPaymentAmount',
        clearDailyLimit:'.jq-clearDailyLimit',
        dailyLimitFormSubmit : '.jq-dailyLimitFormSubmit',
        confirmPaymentDialog :'.jq-confirmPaymentDialog'
    };


    // COMMON FOR BOTH MODULES
    var enabledDays = [];

    //bind all the DOM events
    var _bindDOMEvents = function(){


        //Payment receipt form.
        $('.jq-paymentReceiptDialogForm').validationEngine({
             ajaxSubmit          :true,
             ajaxSubmitUrl		:org.bt.utils.serviceDirectory.verifySmsCode(),
             ajaxSubmitType      :'GET',
             dataType            :'json',
             onSubmitSuccess:function(data){
                if(data.success){

                  var $form   = $('.jq-cashPaymentMakePaymentForm'),
                      data    = $.createFormSubmitRequestBody($form),
                      action  = org.bt.utils.serviceDirectory.submitPayment();

                  org.bt.utils.communicate.post({action:action,data:data});

                } else {
                  $.showServerSideErrors(data,this);
                }

             }
         });

        //make payment form validation
        $('form.jq-cashPaymentMakePaymentForm').validationEngine({
            ajaxSubmit          :true,
            ajaxValidationUrl   :org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl		:org.bt.utils.serviceDirectory.confirmPayment(),
            ajaxSubmitType      :'GET',
            dataType            :'json',
            onValidationComplete:function(form,success){},
            onSubmitSuccess:function(d){_onCashPaymentMakePaymentSuccess('form.jq-cashPaymentMakePaymentForm',d);},
            onSubmitError:function(){},
            customFunctions:{
                'repeatEndDate':function(rules,value){
                    return _endDateFrequencyCheck(rules,value);
                },
                'amount': _onCashPaymentPaymentAmountFocusout
            }

        });

        //payee drop-down
        $(_DOMElements.payeeInput).searchableDropDown({searchData:'payeeList',onSelect:function(obj,item){
            _onPayeeSelect(obj,item);
        },formatter:function(data){
            return _formatPayeeData(data);
        },
            onError:function(){
                _hideCrnField();
            },
            onInputClear:function(){
                _hideCrnField();
            },
            filters:null
        }).placeholder();

        //date field date picker
        $(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder').datepicker({
            minDate: new Date(),
            changeMonth: true,
            changeYear: true,
            onSelect: function(dateText, inst) {
                var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);

                $('.jq-cashPaymentPaymentDate')
                    .val($.formatDate(selectedDate,'dd mmm yyyy'))
                    .trigger('focusout');
                $(this).addClass('noDisplay');
                _onPaymentFrequencyChange();
            }
        }).addClass('noDisplay');
        $('.jq-cashPaymentPaymentDate')
            .val($.formatDate(new Date(),'dd mmm yyyy'))
            .attr('data-placeholder',$.formatDate(new Date(),'dd mmm yyyy')).focusout(_onCashPaymentDateFocusout);

        $('.jq-cashPaymentDateCalendarIcon').click(_onDateCalendarIconClick);

        //Prepopulate Calendar Nominated Dates
        enabledDays = [];
        var d1 = new Date($(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder').val());
        var d2 = new Date(d1.getFullYear(), d1.getMonth()+1, d1.getDate());;

        enabledDays = nominatedDateCalculator(d1, d2, GetSelectedRepeatFrequency('.jq-cashPaymentFrequency'));

        //end date field date picker
        var endRepeatDate   = new Date();
        endRepeatDate.setMonth( endRepeatDate.getMonth( ) + 1 );
        $(_DOMElements.paymentForm).find('.jq-cashPaymentEndDateCalendarPlaceHolder').datepicker({
            dateFormat:'mm/dd/yy',
            minDate: endRepeatDate,
            defaultDate: '+1m',
            changeMonth: true,
            changeYear: true,

            onSelect: _onDateSelect,
            beforeShowDay: nominatedDates,
            onChangeMonthYear: _onDateMonthYearChange
        }).addClass('noDisplay');

        $(_DOMElements.paymentForm).find('.jq-cashPaymentRepeatEndDate').val($.formatDate(endRepeatDate,'dd mmm yyyy'))
            .attr('data-placeholder',$.formatDate(endRepeatDate,'dd mmm yyyy'))
            .attr('data-past', $.formatDate(endRepeatDate,'yyyy-mm-dd'));

        $('.jq-cashPaymentEndDateCalendarIcon').click(_onEndDateCalendarIconClick);

        //Daily Limit validation
        //$(_DOMElements.cashPaymentPaymentAmount).focusout(_onCashPaymentPaymentAmountFocusout);
        $(_DOMElements.clearDailyLimit).click(_onClearDailyLimitClick);
        $(_DOMElements.dailyLimitFormSubmit).click(_onDailyLimitFormSubmit);

        //init accordion view plugin for add biller & payee
        $(_DOMElements.addPayeeBillerWrap).accordionView({
            fakeElement:_DOMElements.addPayeeBillerPlaceholder,
            toggleButton:_DOMElements.addNewPayeeButton,
            beforeSlideDown: function(){
                $(_DOMElements.addPayeeBillerWrap).tabs('select', '#jq-paymentAddPayee');
                $(_DOMElements.addPayeeBillerWrap).find('form').resetForm();
                $(_DOMElements.dailyLimitFormWrap).accordionView('hide');
            }
        });

        //clear buttons in sliders
        $(_DOMElements.addPayeeBillerWrap).find('.jq-formClear').click(_onAddBillerClearButtonsClick);

        //init accordion view for increase daily limit module
        $(_DOMElements.dailyLimitFormWrap).accordionView({
            pointerGuide:_DOMElements.cashPaymentPaymentAmount,
            fakeElement:_DOMElements.dailyLimitFormWrapPlaceholder,
            beforeSlideDown:function(){
                $(_DOMElements.addPayeeBillerWrap).accordionView('hide');
            }
        });

        //copy description
        $('.jq-cashPaymentCopyDescription').click(_onCopyDescriptionClick);
        $('.jq-cashPaymentCopyDescriptionLabel').click(_onCopyDescriptionLabelClick);
        $('.jq-cashPaymentDescription').focusout(_onDescriptionFocusout).focus(_validatePayeeField);

        //description
        $('.jq-cashPaymentYourDescription').focus(_validatePayeeField);
        //copy name to nickname
        $('.jq-payAnyonePayeeName').focusout(_onPayeeNameFocusout);

        //reset datepicker
        $('.jq-cashPaymentClear').click(_onCashPaymentClearClick);

        //on amount field focus
        $(_DOMElements.cashPaymentPaymentAmount).focus(_validatePayeeField);

        //recurring payment check box
        $('.jq-cashPaymentRecurringPayment').click(_onRecurringPaymentClick);

        //payment spinner
        $('.jq-repeatNumberSpinnerButton').click(_onRepeatNumberSpinnerButtonClick);//.spinner();
        //$('.jq-repeatNumberSpinnerButton').spinner();

        //submit payment
        $('.jq-confirmPaymentSubmitButton').click(_onSubmitPaymentClick);

        $('.jq-repeatNumberInput').focus(_onRepeatNumberFocus).change(_onRepeatNumberFocus);

        //validate CRN in make payment form
        $(_DOMElements.paymentForm).find(_DOMElements.paymentFromCrnWrap).find('input').focusout(_validatePaymentCrn);

        $(_DOMElements.exceedDailyLimit).click(_onExceedDailyLimitClick);

        $('.jq-textClear', _DOMElements.paymentForm).hide();

    };//End _bindDOMEvents

    // Enable a list of dates
    function nominatedDates(date) {
        var m = date.getMonth(), d = date.getDate(), y = date.getFullYear();
        for (i = 0; i < enabledDays.length; i++) {
            if($.inArray(d + '/' + (m+1) + '/' + y, enabledDays) != -1) {
                return [true,"ui-state-special"];
            }
        }
        return [false];
    }

    function CheckNominatedDates(dateStart, frequency) {
        var d1 = new Date(dateStart);
        var d2 = new Date(d1);

        d2 = GetFrequencyDate(d2, frequency);
        enabledDays = nominatedDateCalculator(d1, d2, frequency);
    }

    function GetFrequencyDate(dateStart, frequency) {
        switch(frequency){
            case 'Weekly': dateStart.setDate(dateStart.getDate()+7); break;
            case 'Fortnightly': dateStart.setDate(dateStart.getDate()+14); break;
            case 'Monthly': dateStart.addMonths(1); break;
            case 'Quarterly': dateStart.addMonths(3); break;
            case 'Yearly': dateStart.addMonths(12); break;
        }
        return dateStart;
    }

    function GetSelectedRepeatFrequency(element) {
        var selectedVal     = (typeof value !== 'undefined' ) ? value : $(element).val();
        return selectedVal;
    }

    // frequency parameter accepts the values : WEEK | FORTNIGHT | MONTH | QUARTER | YEAR
    function nominatedDateCalculator(dateSelected, dateStart, frequency) {
        var dateSelected, dateStart;
        var validDates = [];

        var dateRunner = new Date(dateSelected);
        var dateMonthFirstDay = new Date(dateStart.getFullYear(), dateStart.getMonth(), 1);
        var dateMonthLastDay = new Date(dateStart.getFullYear(), dateStart.getMonth() + 1, 0, 23, 59, 59);

        //calculate number of days in difference //dateRunner = new Date(d2 - d1)/1000/60/60/24;

        while (dateRunner < dateMonthLastDay) {
            dateRunner = GetFrequencyDate(dateRunner, frequency);

            if ((dateRunner >= dateMonthFirstDay) && (dateRunner <= dateMonthLastDay)) {
                validDates.push(dateRunner.getDate() + "/" + (dateRunner.getMonth()+1) + "/" + dateRunner.getFullYear());
            }
        };

        return validDates;
    }
    // COMMON FOR BOTH MODULES - ENDS

    var _onExceedDailyLimitClick = function(e){
        var checked = $(e.currentTarget).is(':checked'),
            $select = $(_DOMElements.dailyLimitFormWrap).find('.jq-dropkickWrap');

        if(checked){
            $select.addClass('jq-disabled').find('input').addClass('readOnly');
        } else {
            $select.removeClass('jq-disabled').find('input').removeClass('readOnly');
        }

    };

    var _onDateSelect = function(dateText, inst) {
        $('.jq-cashPaymentRepeatEndDate')
            .val($.formatDate(new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay),'dd mmm yyyy'))
            .trigger('focusout');
        $(this).addClass('noDisplay');
    }

    var _onDateMonthYearChange = function(year, month, inst) {
        //var d1 = new Date(inst.currentYear, inst.currentMonth, inst.currentDay);
        var d1 = new Date($(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder').val());
        var d2 = new Date(inst.selectedYear, inst.selectedMonth, inst.selectedDay);

        enabledDays = nominatedDateCalculator(d1, d2, GetSelectedRepeatFrequency('.jq-cashPaymentFrequency'));

        var $endDateEl  = $(_DOMElements.paymentForm).find('.jq-cashPaymentEndDateCalendarPlaceHolder');
        $endDateEl.datepicker("refresh");
    };

    var _handleDeviceSpecific = function(){
        if($.isMobile()){

        } else {
            $('.jq-payeeSlidingSlider').find('input[name="_saveToList"]').remove();
        }
    };

    var _endDateFrequencyCheck = function(rules,value) {
        var matched         = true,
            $element        = $('.jq-cashPaymentRepeatEndDate'),
            newDate         = $.validateDate(value),
            datepicker      = $element.attr('data-calendar'),
            hasDatepicker   = (typeof datepicker !== "undefined" && datepicker !== false);


        if (newDate === null) {
            matched = false;
        } else {
            var d1 = new Date($(_DOMElements.paymentForm).find('.jq-cashPaymentPaymentDate').val()),
                frequencyDates = nominatedDateCalculator(d1, newDate, GetSelectedRepeatFrequency('.jq-cashPaymentFrequency')),
                newDateFormat = newDate.getDate() + '/' + (newDate.getMonth()+1) + '/' + newDate.getFullYear();

            if (!(frequencyDates.indexOf( newDateFormat ) > -1)) {
                matched = false;
            }
        }
        //if matched updated the date picker
        if(matched){
            $element.val($.formatDate(newDate,'dd mmm yyyy'));
            //update date picker
            if(hasDatepicker){
                var currentDay      = $(datepicker).datepicker( 'getDate' );
                if(currentDay.getTime() !== newDate.getTime()){
                    $(datepicker).datepicker('setDate', newDate );
                }
            }
        }
        //add rule to array
        rules.push({'name':'customFunction','matched':matched});
        return rules;
    };

    //on repeat number focus
    var _onRepeatNumberFocus = function(){
        var paymentType = $('.jq-cashPaymentPayee').attr('data-type'),
            tabIndex    = 0;

        if(paymentType === 'PAY_ANYONE'){
            tabIndex = parseFloat($('.jq-cashPaymentDescription').attr('tabindex'));
        } else {
            tabIndex = parseFloat($('.jq-cashPaymentYourDescription').attr('tabindex'));
        }
        //$(this).removeAttr('data-error-showing');
        $(this).attr('tabindex',tabIndex-1);
        CalculateSpinnerDate();
    };
    //format payee data
    var _formatPayeeData = function(searchData){
        var i;
        for (i = 0; i < searchData.length; i += 1) {
            if(!searchData[i].typeRaw){
                searchData[i].typeRaw = searchData[i].payeeType;
                searchData[i].type = (searchData[i].typeRaw === 'BPAY') ? 'BPAY' : 'Pay anyone';
            }
            if(!searchData[i].codeRaw){
                //searchData[i].codeRaw = (searchData[i].typeRaw === 'BPAY') ? searchData[i].code : searchData[i].code.substring(0, 3)+'-'+searchData[i].code.substring(3, 6);
            	searchData[i].codeRaw = searchData[i].code;
                searchData[i].code = (searchData[i].typeRaw === 'BPAY') ? 'Biller code: '+searchData[i].code : 'BSB: '+searchData[i].codeRaw;
            }
            if(!searchData[i].refRaw){
                searchData[i].refRaw = searchData[i].reference;
                searchData[i].ref = (searchData[i].typeRaw === 'BPAY') ? 'Reference: '+searchData[i].reference : 'Account: '+searchData[i].reference;
            }
        }
        return searchData;
    };

    //on submit payment click
    var _onSubmitPaymentClick = function(e){
        e.preventDefault();

        var $form   = $('.jq-cashPaymentMakePaymentForm'),
            data    = $.createFormSubmitRequestBody($form),
            action  = org.bt.utils.serviceDirectory.submitPayment();

        org.bt.utils.communicate.post({action:action,data:data});

    };

    //on cash payment date blur
    var _onCashPaymentDateFocusout = function(){
        var $element    = $(this),
            $form       = $element.parents('form:first'),
            validating  = $form.hasClass('jq-validationInProgress'); //check form validation status

        if(!validating){
            _onPaymentFrequencyChange();
        }
    };

    function CalculateSpinnerDate() {
        var $display      = $(_DOMElements.paymentForm).find('.jq-repeatEndDate');
        var newValue      = $(_DOMElements.paymentForm).find('.jq-repeatNumberInput').val();

        var dateStart = new Date($('.jq-cashPaymentPaymentDate').val());
        var frequency = GetSelectedRepeatFrequency('.jq-cashPaymentFrequency');

        newValue = parseInt(newValue);
        if (newValue > 0) {
            switch(frequency){
                case 'Weekly': dateStart.setDate(dateStart.getDate()+(7*newValue)); break;
                case 'Fortnightly': dateStart.setDate(dateStart.getDate()+(14*newValue)); break;
                case 'Monthly': dateStart.addMonths(1*newValue); break;
                case 'Quarterly': dateStart.addMonths(3*newValue); break;
                case 'Yearly': dateStart.addMonths(12*newValue); break;
            }

            $display.html('Ends on [' + $.formatDate(dateStart,'dd mmm yyyy') + ']');
        } else {
            $display.html('');
        }
    }

    //on repeat number spinner button click
    var _onRepeatNumberSpinnerButtonClick = function(){
        var $element    = $(this),
            $input      = $('.jq-repeatNumberInput'),

            value       = $input.val(),
            min         = parseInt($input.attr('data-min'),10),
            max         = parseInt($input.attr('data-max'),10),
            intValue    = parseInt(value,10),
            isNumber    = /^\d+$/.test(value),
            newValue    = 0,
            rel         = $element.attr('data-rel');

        if(isNumber){ //check user input is a number
            newValue = (rel === 'plus') ? intValue+1 : intValue -1;
            if(newValue >= min && newValue <= max){ //check new value is in the range
                $.removeMessage($input,['jq-inputError','formFieldMessageError'],'textInputError');
                $input.val(newValue);
                CalculateSpinnerDate();
            }
        }
    };

    //on recurring payment check box click
    var _onRecurringPaymentClick = function(){
        var $repeatElement = $(this);

        //check for permissions
        if($repeatElement.hasClass('jq-disabled')){
            return false;
        }

        var fields = $('.jq-cashPaymentRepeatPaymentOnlyFields');
        if(fields.hasClass('noDisplay')){
            fields.removeClass('noDisplay').hide();
            fields.find('.selectContainer').addClass('block');/*IE issue*/
        }
        //fields.slideDown();
        var toggle = ($repeatElement.is(':checked')) ? fields.slideDown(400,function(){fields.removeAttr('style')}):fields.slideUp();
    };

    //display confirm modal
    var  _showPaymentConfirmModal = function(){
         var paymentType = $('.jq-cashPaymentPayee').attr('data-type'),
             analyzeData = {eventType:'', payeeType:paymentType, eventDescription: '', clientDefinedEventType:'PAYMENT'};

        $(_DOMElements.confirmPaymentDialog).dialog( 'open' ).css('height','auto');
        $('.ui-widget-overlay').addClass('modalBG');
        $('.jq-paymentReceiptDialogForm').smsCode({analyzeData:analyzeData, onVisible: function() {
            $('.jq-smsCode').placeholder();
        }});

    };

    //hide crn field
    var _hideCrnField = function(){
        $(_DOMElements.paymentForm).find(_DOMElements.paymentFromCrnWrap).slideUp();
    };
    //validate payee field on field focus

    var _validatePayeeField = function(){
        var $payeeElement       = $('.jq-cashPaymentPayee'),
            $crnField           = $(_DOMElements.paymentForm).find(_DOMElements.paymentFromCrnWrap),
            value               = $.trim($payeeElement.val()),
            placeholder         = $payeeElement.attr('data-placeholder'),
            payAnyoneOnlyFields = $('.jq-cashPaymentPayAnyoneOnlyFields'),
            payeeList           = $payeeElement.siblings('.jq-selectSearch');

        if(value === '' || value === placeholder){
            $.promptMessage($payeeElement,'required',['jq-inputError','formFieldMessageError'],'textInputError','');
            payAnyoneOnlyFields.slideUp();
            $crnField.slideUp();
        }else if($payeeElement.attr('data-submit-value') === 'NULL'){
            $.promptMessage($payeeElement,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
            payAnyoneOnlyFields.slideUp();
            $crnField.slideUp();
        }
        //hide payee pull-down if visible
        if(payeeList.find('li').length > 0){
            $(payeeList).fadeOut(400,function(){
                $(payeeList).html('');
            });
        }
    };
    //clear datepicker on cancel
    var _onCashPaymentClearClick = function(){
        var $repeatPaymentCheckbox  = $(_DOMElements.paymentForm).find('.jq-cashPaymentRecurringPayment'),
            $paymentDate            = $(_DOMElements.paymentForm).find('.jq-cashPaymentPaymentDate'),
            endDate                 = new Date();


        $(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder').datepicker('setDate',new Date());

        endDate.setMonth( endDate.getMonth( ) + 1 );

        $(_DOMElements.paymentForm).find('.jq-cashPaymentEndDate').html($.formatDate(endDate,'dd'));
        $(_DOMElements.paymentForm).find('.jq-cashPaymentRepeatEndDate').val($.formatDate(endDate,'dd mmm yyyy')).attr('data-past', $.formatDate(endDate,'yyyy-mm-dd'));

        $(_DOMElements.paymentForm).find('.jq-cashPaymentPayAnyoneOnlyFields, .jq-cashPaymentRepeatPaymentOnlyFields').slideUp() ;
        $(_DOMElements.paymentForm).find('.jq-repeatEndDateWrapper').removeClass('noDisplay');
        $(_DOMElements.paymentForm).find('.jq-repeatNumberWrapper').addClass('noDisplay');
        $(_DOMElements.paymentForm).find('.jq-crnOnlyField').slideUp();
        $(_DOMElements.paymentForm).find('.jq-cashPaymentRecurringPayment').removeAttr('disabled');

        if($repeatPaymentCheckbox.hasClass('jq-disabled')){
            $repeatPaymentCheckbox.attr('disabled','disabled');
        } else{
            $repeatPaymentCheckbox.removeAttr('disabled');
        }
        if($paymentDate.hasClass('jq-disabled')){
            $paymentDate.val('');
        }
        $(_DOMElements.addPayeeBillerWrap).accordionView('slideUp');
        $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
    };
    //copy payee name
    var _onPayeeNameFocusout = function(){
        //$('.jq-payAnyonePayeeNickname').val($(this).val());
    };
    //copy description
    var _onCopyDescriptionClick = function(){
        var payeeDescription    = $('.jq-cashPaymentDescription').val(),
            $yourDescriptionEl  = $('.jq-cashPaymentYourDescription');

        if($(this).is(':checked')){
            $yourDescriptionEl.val(payeeDescription);
            $.removeMessage($yourDescriptionEl,['jq-inputError','formFieldMessageError'],'textInputError');
        }
    };
    var _onCopyDescriptionLabelClick = function(){
        var $copyDescriptionEl = $('.jq-cashPaymentCopyDescription');

        if($copyDescriptionEl.is(':checked')){
            $copyDescriptionEl.attr('checked','checked');
        } else {
            $copyDescriptionEl.removeAttr('checked');
        }
        _onCopyDescriptionClick.apply($copyDescriptionEl[0]);
    };
    var _onDescriptionFocusout = function(){
        var valid = $(this).hasClass('jq-validField');
        if(valid && $.trim($(this).val()) !== ''){
            $.removeMessage($('.jq-cashPaymentYourDescription'),['jq-inputError','formFieldMessageError'],'textInputError');
            _onCopyDescriptionClick.apply($('.jq-cashPaymentCopyDescription')[0]);
        }
    };

    //Validate Amount for daily limit check
    var _onCashPaymentPaymentAmountFocusout = function(rules, value, isStillValid){

        //only trigger backend validation if basic validation success
        var obj = {element: this, rules:rules},
            config;

        var payeeType = $(_DOMElements.payeeInput).attr('data-type'),
            exceedDailyLimit = $(_DOMElements.exceedDailyLimit);
        $(_DOMElements.dailyLimitFormWrap).accordionView('hide');
        if(isStillValid && !exceedDailyLimit.is(':checked'))
        {
            //do the server side validation.
            config = {
                url:org.bt.utils.serviceDirectory.validateDailyLimit,
                async:false,
                data:{'amount':value, 'payeeType': payeeType},
                onSuccess:_onDailyLimitValidationSuccess
            };
            org.bt.utils.communicate.ajax.call(obj,config);
        }
        return rules;
    };


    var _onDailyLimitValidationSuccess = function(res){

        var payeeType = $(_DOMElements.payeeInput).attr("data-type"),
            errorMessage = res.data.errorMessage,
            $dailyLimitAmountListContainer = $('#selectContainer_paymentLimit');
        _clearDailyLimitMessages();

        //enable drop-down
        $(_DOMElements.dailyLimitFormWrap).find('.jq-dropkickWrap').removeClass('jq-disabled').find('input').removeClass('readOnly');
        $(_DOMElements.paymentForm).removeClass('jq-preventSubmit');

        //check the user has permission to change daily limit. only applicable for BPAY and PAY ANYONE
        if(!res.success && this.element.hasClass('jq-cannotUpdate') && (payeeType=='BPAY' || payeeType=='PAY_ANYONE')){
            this.rules.push({'name':'permission','matched':false});
            return this.rules;
        }

        if(!res.success && res.data.maxLimit!=null){
            errorMessage = res.data.errorMessage;
            errorMessage = errorMessage.replace("{maxLimit}",res.data.maxLimit);
            this.element.attr('data-validation-server-error', errorMessage);
            this.rules.push({'name':'server','matched':false});
            return this.rules;
        }

        if(!res.success && res.data.maxLimit===null && (payeeType=='BPAY' || payeeType=='PAY_ANYONE')){


            $('.jq-payeeType').val(payeeType);
            var remainingLimit = '',
                availableLimit = '',
                limitAmounts;

            $(_DOMElements.dailyLimitFormWrap).accordionView('slideDown');

            //Populate the values for the daily limit form.
            if(payeeType==='BPAY'){
                availableLimit = res.data.bpayLimit;
                remainingLimit = res.data.remainingBpayLimit;
                limitAmounts = res.data.bpayLimits;
                $(".jq-increaseLimitPayee").text('BPAY');

            }  //else if(payeeType==='PAY_ANYONE'){
            else{
                availableLimit = res.data.payAnyoneLimit;
                remainingLimit = res.data.remainingPayAnyoneLimit;
                limitAmounts = res.data.payAnyoneLimits;
                $(".jq-increaseLimitPayee").text('Pay Anyone');
            }

            /*clear daily limit drop downs*/
            $(_DOMElements.dailyLimitAmountList).removeData('dropkick');
            $(_DOMElements.dailyLimitAmountList).find('option').remove();
            $dailyLimitAmountListContainer.remove();

            var limitAmountOptions = [];

            $('.jq-remainingLimit').html(remainingLimit);
            $('.jq-availableLimit').html(availableLimit);
            for(var i in limitAmounts){
                if(i==='sortBy' || i==='remove'){
                    continue;
                }
                limitAmountOptions.push('<option value='+limitAmounts[i]+'>'+limitAmounts[i]+'</option>');
            }



            var limitAmountOptionsHtml = limitAmountOptions.join(' ');
            $(_DOMElements.dailyLimitAmountList).html(limitAmountOptionsHtml);
            $(_DOMElements.dailyLimitAmountList).dropkick({
                inputClasses:['smallInput']
            });


            this.element.attr('data-validation-server-error', errorMessage);
            this.rules.push({'name':'server','matched':false});
            return this.rules;

        }
        return this.rules;
    };

    var _onDailyLimitValidationError = function(res){

        $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
        // TODO: Handle Ajax Error

    };


    var _onClearDailyLimitClick = function(event){
        event.preventDefault();
        $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
    };

    var _onDailyLimitFormSubmit = function(e){
        e.preventDefault();

        var $element    = $(e.target),
            payeeType = $(_DOMElements.payeeInput).attr('data-type'),
//    	  	payAmount = $(_DOMElements.cashPaymentPaymentAmount).attr('data-submit-value'),
            paymentLimit = $(_DOMElements.dailyLimitAmountList).val(),
            data =   {'payeeType': payeeType, 'paymentLimit': paymentLimit },
            dailyLimitSubmitURL     =  org.bt.utils.serviceDirectory.submitDailyLimitForm,
            exceedDailyLimit = $(_DOMElements.exceedDailyLimit),
            exceedForTransactionMessage='Your daily limit will be exceeded for this transaction only';

        if(exceedDailyLimit.is(':checked')){
//    		 $(_DOMElements.cashPaymentPaymentAmount).attr('data-new-value', )
            _showDailyLimitSuccessMessage(exceedForTransactionMessage);
            $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
            $('.jq-cashPaymentRecurringPayment', _DOMElements.paymentForm).attr("disabled", "disabled");
            $(_DOMElements.paymentForm).removeClass('jq-preventSubmit');
        }
        else{
            org.bt.utils.communicate.ajax.apply($element,[{
                'url': dailyLimitSubmitURL,
                'type':'POST',
                'data': data,
                'onSuccess':_onSuccessDailySubmission
            }]);
        }


    };

    var _clearDailyLimitMessages = function(){
        $.removeMessage($(_DOMElements.cashPaymentPaymentAmount),['jq-inputError','formFieldMessageError'],'textInputError');
        $.removeMessage($(_DOMElements.cashPaymentPaymentAmount),['positive'],'formFieldSuccess');
        $(_DOMElements.cashPaymentPaymentAmount).addClass('jq-validField');
        $(_DOMElements.cashPaymentPaymentAmount).removeAttr('data-validation-customFunction-error');
        $(_DOMElements.cashPaymentPaymentAmount).removeAttr('data-validation-server-error');
    };

    var _showDailyLimitSuccessMessage = function(message){
        _clearDailyLimitMessages();
        $.promptMessage( $(_DOMElements.cashPaymentPaymentAmount),'custom',['positive','normalFont','jq-inputInfo'],'formFieldSuccess',message);
        $(_DOMElements.cashPaymentPaymentAmount).trigger('focusout');
    };
    var _onSuccessDailySubmission = function(response){

        var submissionSuccessMessage='You have successfully changed your daily limit';
        if(response.success){
            _showDailyLimitSuccessMessage(submissionSuccessMessage);
            $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
            $(_DOMElements.paymentForm).removeClass('jq-preventSubmit');
        }
    };

    //add Bpay or pay anyone submit success
    var _onCashPaymentAddBpayOrPayAnyoneSubmitSuccess = function(response){
        if(response.success){
            $(_DOMElements.addPayeeBillerWrap).accordionView('slideUp');
            _setPayee(response.data);
            //set CRN value only for the session
            _setCRNValue(response.data)
        }
    };

    /**
     * @desc set CRN value only for the current session even if VCRN or ICRN
     * @param data
     * @private
     */
    var _setCRNValue = function(data){
        var $crnField = $('.jq-paymentCrn');

        if(data.payeeType === 'BPAY'){
            $crnField.val(data.reference);
        }
    };
    /**
     * @desc Pre select payee function
     * @private
     */
    var _setPayee = function(data){
        //set value to pull-down
        _setPayeeDropDownDefaultValue(data);
        _togglePayeeDescription(data);
        _updateRepeatPaymentOptions(data);
        _toggleCrnField(data);
    };

    /**
     * @desc Find payee by ID
     * @private
     */
    var _findPayeeById = function(id){
        var payees  = org.bt.collections.payeeList,
            payee   = null;
        for (var i=0; i<payees.length; i++){
            if(payees[i].id === id){
                payee = payees[i];
                break;
            }
        }
        //return the payee
        return payee;
    };
    /**
     * Set payee dropdown value
     * @param data
     * @private
     */
    var _setPayeeDropDownDefaultValue = function(data){
        var $element            = $('.jq-cashPaymentPayee'),
            $amountField        = $('.jq-cashPaymentPaymentAmount'),
            code                = (data.codeRaw) ? data.codeRaw : data.code,
            value               = data.displayName+' - '+code;

        //reference is only for pay anyone
        if(data.payeeType !== 'BPAY'){
            value += ' ' + data.reference
        }

        //remove message of payee field
        $.removeMessage($element,['jq-inputError','formFieldMessageError'],'textInputError');
        //remove message of amount field
        $.removeMessage($amountField,['jq-inputError','formFieldMessageError'],'textInputError');

        $element.val(value).attr('data-submit-value',data.id).attr('data-type',data.payeeType).attr('data-name',data.displayName).attr('data-code',code).attr('data-ref',data.reference);

        //console.log("payment cross");
        //$('.jq-textClear', _DOMElements.paymentForm).show();
    };

    //handle cash payment success
    var _onCashPaymentMakePaymentSuccess = function(form,response){
        var $form           = $(form),
            message         = (response.success) ? 'add payment success' : 'add payment unsuccessful',
            displayModal    = true;

        if(displayModal){
            if(response.success){
                //display popup
                _setFormValuesToModal(response.data);
                _showPaymentConfirmModal();
            } else {
                if($.isArray(response.data)){
                    $.each(response.data,function(k,v){
                        //$element,rule,errorMessageClasses,errorFieldClass,genericErrorMessage
                        var $element = $form.find('input[name="'+ v.field+'"]');
                        $.promptMessage($element,'NULL',['jq-inputError','formFieldMessageError'],'textInputError', v.message);
                    });
                } else {
                    alert('Failed: '+response.data);
                    //TODO: display server error
                }
            }
        }
    };
    //set values to modal
    var _setFormValuesToModal = function(data){
        var payeeInput                  = $('.jq-cashPaymentPayee'),
            amount                      = data.amount,
            date                        = data.date,
            fromName                    = data.from.name,
            fromBsb                     = data.from.code,
            fromAccount                 = data.from.reference,
            repeatInfoLine1             = data.repeatLine1,
            repeatInfoLine2             = data.repeatLine2,
            payeeDescription            = data.to.description || '',
            description                 = data.description,
            repeatPayment               = data.recurring,
            payType                     = data.to.payeeType,
            payTypeLabel                = (payType === 'PAY_ANYONE') ? 'BSB':'Biller Code',
            payName                     = data.to.name,
            payCode                     = data.to.code,
            payRef                      = data.to.reference,
            payRefLabel                 = (payType === 'PAY_ANYONE') ? 'Account no.':'CRN',
            repeatOnlyFields            = $('.jq-confirmPaymentRepeatOnlyFields'),
            toggleRepeatFields          = (repeatPayment) ? repeatOnlyFields.show() : repeatOnlyFields.hide(),
            payAnyoneOnlyFields         = $('.jq-confirmPaymentPayAnyoneOnlyFields'),
            togglePayAnyoneFields       = (payType === 'PAY_ANYONE') ? payAnyoneOnlyFields.show() : payAnyoneOnlyFields.hide(),
            descriptionFields           = $('.jq-confirmPaymentPayeeDescriptionFields'),
            toggleDescriptionFields     = (payeeDescription === '') ? descriptionFields.hide() : descriptionFields.show(),
            yourDescriptionFields       = $('.jq-confirmPaymentDescriptionFields'),
            toggleYourDescriptionFields = (description !== '') ? yourDescriptionFields.show() : yourDescriptionFields.hide(),
            repeatText              = [
                repeatInfoLine1,
                '<br/>',
                repeatInfoLine2
            ];

        $('.jq-confirmPaymentFrom').html(
            [
                '<span class="emphasis">',
                fromName,
                '</span>',
                '<br/>',
                'BSB',
                '&#32;',
                fromBsb,
                '<br/>',
                'Account no.',
                '&#32;',
                fromAccount
                ,'<br/>'
            ].join('')
        );

        $('.jq-confirmPaymentPay').html(
            [
                '<span class="emphasis">',
                payName,
                '</span>',
                '<br/>',
                payTypeLabel,
                '&#32;',
                payCode,
                '<br/>',
                payRefLabel,
                '&#32;',
                payRef
                ,'<br/>'
            ].join('')
        );

        $('.jq-confirmPaymentAmount').html(
            [
                '<span class="emphasis">',
                amount,
                '</span>'
            ].join('')
        );
        $('.jq-confirmPaymentDate').html(
            [
                '<span class="emphasis">',
                date,
                '</span>'
            ].join('')
        );

        $('.jq-confirmPaymentRepeat').html((repeatPayment)? repeatText.join(''):'');
        $('.jq-confirmPaymentPayeeDescription').html(payeeDescription);
        $('.jq-confirmPaymentYourDescription').html(description);
        $('.jq-cashPaymentToken').val(data.paymentId);
    };
    var _onCashPaymentAddPayAnyoneSubmitError = function(){
        alert('ajax submit error. Due to server problem')
    };
    var _onDateCalendarIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $('.jq-cashPaymentDateCalendarPlaceHolder'),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay') : $placeholder.addClass('noDisplay');
        //console.log('hmm',$placeholder.is(':visible'))
    };

    var _onEndDateCalendarIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $('.jq-cashPaymentEndDateCalendarPlaceHolder'),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay') : $placeholder.addClass('noDisplay');
    };

    //on accordion clear button click
    var _onAddBillerClearButtonsClick = function(e){

        $(_DOMElements.addPayeeBillerWrap).accordionView('slideUp');
    };

    //on payee select
    var _onPayeeSelect = function(el,data){
        var value       = data.displayName+' - '+data.codeRaw,
            canTransact = el.parents('form:first').attr('data-can-transact');

            if(data.payeeType !== 'BPAY'){
                value +=' '+data.refRaw;
            }
            el.val(value).attr('data-type',data.payeeType).attr('data-name',data.displayName).attr('data-code',data.codeRaw).attr('data-ref',data.refRaw);

        if(canTransact !== 'NONE'){   //check user has rights!
            _togglePayeeDescription(data);
            _updateRepeatPaymentOptions(data);
            _toggleCrnField(data);


            //Clear Daily Limit
            _clearDailyLimitMessages();
            $(_DOMElements.dailyLimitFormWrap).accordionView('slideUp');
        }
    };

    //toggle CRN field
    var _toggleCrnField = function(data){
        org.bt.utils.log.info('crnType',data.crnType);
        var $form           = $(_DOMElements.paymentForm),
            $crnWrap        = $form.find(_DOMElements.paymentFromCrnWrap),
            $crnField       = $crnWrap.find('input'),
            $amountField    = $(_DOMElements.cashPaymentPaymentAmount),
            $repeatBox      = $('.jq-cashPaymentRecurringPayment'),
            $crnHint        = $('.jq-paymentVrnHint'),
            code            = (data.codeRaw) ? data.codeRaw : data.code;

        if($crnWrap.hasClass('noDisplay')){ //fired only for the first time
            $crnWrap.removeClass('noDisplay').hide();
        }
        $crnHint.addClass('noDisplay');
        //remove error
        $.removeMessage($crnField,['jq-inputError','formFieldMessageError'],'textInputError');
        //remove prevent submit flag
        $form.removeClass('jq-preventSubmit');

        if(data.payeeType === 'BPAY'){
            $crnWrap.slideDown();
            $crnField.attr({'data-type':data.crnType,'data-code':code});
            if(data.crnType === 'VCRN' || data.crnType === 'ICRN'){
                $crnField.removeAttr('readonly').removeClass('jq-skip jq-validField').val('').focus();

                //cannot do repeat payment
                $repeatBox.attr('disabled','disabled');

                //we have to hide repeat payment
                if($repeatBox.is(':checked')){
                    $repeatBox.removeAttr('checked');
                    _onRecurringPaymentClick.call($repeatBox[0]);
                }

                //display hint
                $crnHint.removeClass('noDisplay');
            } else {
                $crnField.val(data.reference).attr('readonly','readonly');
                $repeatBox.removeAttr('disabled'); //can do repeat payments
                $amountField.focus();
            }
        }else{
            $crnWrap.slideUp();
            $crnField.addClass('jq-skip');//skip validation
            $repeatBox.removeAttr('disabled'); //can do repeat payments
            $amountField.focus();
        }

        //check user has permission to do repeat payment.
        if($repeatBox.hasClass('jq-disabled')){
            $repeatBox.attr('disabled','disabled');
        }

    };

    var _togglePayeeDescription = function(data){
        var fields = $('.jq-cashPaymentPayAnyoneOnlyFields');
        fields.hide().removeClass('noDisplay');
        var toggle = (data.payeeType === 'BPAY') ? (fields.is('visible')) ? fields.slideUp(): '' : (!fields.is('visible'))? fields.slideDown() : '';

    };

    //on payment form CRN focusout
    var _validatePaymentCrn = function(e){
        var $element    = $(e.target),
            value       = $.trim($element.val()),
            type        = $element.attr('data-type'),
            billerCode  = $element.attr('data-code');

        if($element.hasClass('jq-validField') && type === 'VCRN'){
            org.bt.utils.communicate.ajax.apply($element,[{
                'url':org.bt.utils.serviceDirectory.validateCrn,
                'data':{'customerReference':value,'bpayBillerCode':billerCode},
                'onSuccess':_onPaymentCrnSuccess,
                'onError':_onPaymentCrnError
            }]);
        }
    };

    var _onPaymentCrnSuccess = function(res){
        var $form = $(_DOMElements.paymentForm);

        if(!res.success){
            $form.addClass('jq-preventSubmit'); //prevent submit form
            this.attr('data-validation-custom-error',res.data).removeClass('jq-validField jq-skip');
            $.promptMessage(this,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
        } else {
            $form.removeClass('jq-preventSubmit');
            $.removeMessage(this,['jq-inputError','formFieldMessageError'],'textInputError');
        }
    };

    var _onPaymentCrnError = function(){
        org.bt.utils.log.error('CRN validation failed');
    };

    var _updateRepeatPaymentOptions = function(data){
        var endRepeatContainer      = $('#selectContainer_endRepeat'),
            frequencyContainer      = $('#selectContainer_frequency'),
            payeeType               = data.payeeType,
            endRepeatPulldown       = $('.jq-cashPaymentEndRepeat'),//jq-cashPaymentEndRepeat
            frequencyPulldown       = $('.jq-cashPaymentFrequency');

        /*end repeat pulldowns*/
        endRepeatPulldown.removeData('dropkick');
        endRepeatPulldown.find('option').remove();
        endRepeatContainer.remove();

        endRepeatPulldown.append('<option value="REPEAT_END_DATE" selected="selected">Set end date</option>');
        endRepeatPulldown.append('<option value="REPEAT_NO_END">No end date</option>');
        endRepeatPulldown.append('<option value="REPEAT_NUMBER">Set number</option>');

        /*frequency pulldown*/
        frequencyPulldown.removeData('dropkick');
        frequencyPulldown.find('option').remove();
        frequencyContainer.remove();
        frequencyPulldown.append('<option value="Weekly" >'+org.bt.collections.paymentFrequency.Weekly+'</option>');
        frequencyPulldown.append('<option value="Fortnightly">'+org.bt.collections.paymentFrequency.Fortnightly+'</option>');
        frequencyPulldown.append('<option value="Monthly" selected="selected">'+org.bt.collections.paymentFrequency.Monthly+'</option>');
        frequencyPulldown.append('<option value="Quarterly">'+org.bt.collections.paymentFrequency.Quarterly+'</option>');
        //if(payeeType !== 'BPAY'){
        frequencyPulldown.append('<option value="Yearly">'+org.bt.collections.paymentFrequency.Yearly+'</option>');
        //}

        /*init custom dropdown*/
        _initCustomDropdowns();

        _onEndRepeatChange('REPEAT_END_DATE',''); //trigger on change
    };


    var _initDatePickers = function(){
        $('.jq-cashPaymentCurrentDate').html($.formatDate(new Date(),'dd'));

        //set today to calendar icon
        var nextMonth   = new Date();
        nextMonth.setMonth( nextMonth.getMonth() + 1 );
        $('.jq-cashPaymentEndDate').html($.formatDate(nextMonth,'dd'));

        //hide date picker on window click
        $(document).click(function(e){
            var target              = $(e.target),
                $dateCalendar       = $('.jq-cashPaymentDateCalendarPlaceHolder'),
                $endDateCalendar    = $('.jq-cashPaymentEndDateCalendarPlaceHolder');

            if(!target.hasClass('jq-cashPaymentDateCalendarIcon')){
                if(target.parents().filter('.jq-cashPaymentDateCalendarIcon,.date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendar.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-cashPaymentEndDateCalendarIcon')){
                if(target.parents().filter('.jq-cashPaymentEndDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $endDateCalendar.datepicker().addClass('noDisplay');
                }
            }
        });
    };

    var _initPaymentConfirmModal = function(){
        var $dialogWindowElement    = $(_DOMElements.confirmPaymentDialog);

        $dialogWindowElement.dialog({
            modal: true,
            autoOpen: false,
            width:550,
            height:600,
            draggable:false,
            dialogClass: 'modalBox',
            resizable: true,
            title:''
        }).removeClass('jq-cloak').end()
            .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
            .find('.ui-dialog-titlebar-close').addClass('modalClose');

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });

        $('.jq-confirmPaymentCancelButton').click(function(e){
            e.preventDefault();
            $dialogWindowElement.dialog( "close" )
        });
    };
    var _initCustomDropdowns = function(){
        $('.jq-cashPaymentFrequency').dropkick({
            change: function (value, label) {
                $(this).change();
                _onPaymentFrequencyChange(value, label);
            },
            inputClasses:['smallInput']
        });
        $('.jq-cashPaymentEndRepeat').dropkick({
            change: function (value, label) {
                $(this).change();
                _onEndRepeatChange(value, label);
            },
            inputClasses:['smallInput']
        });
    };
    var _onEndRepeatChange = function(value, label){
        var $endDateSelector            = $('.jq-repeatEndDateWrapper'),
            $repeatNumberSelector       = $('.jq-repeatNumberWrapper'),
            toggleEndDateSelector       = (value === 'REPEAT_END_DATE') ? $endDateSelector.removeClass('noDisplay') : $endDateSelector.addClass('noDisplay'),
            toggleRepeatNumberSelector  = (value === 'REPEAT_NUMBER') ? $repeatNumberSelector.removeClass('noDisplay') : $repeatNumberSelector.addClass('noDisplay'),
            $repeatNumberInput          = $('.jq-repeatNumberInput');

        $repeatNumberInput.val('2').trigger('focusout');
        //focus element
        switch(value){
            case 'REPEAT_END_DATE': $('.jq-cashPaymentRepeatEndDate').focus(); break;
            case 'REPEAT_NUMBER': $repeatNumberInput.focus(); break;
            case 'REPEAT_NO_END': $('.jq-cashPaymentYourDescription').focus(); break;
        }

        CalculateSpinnerDate();
    };

    var _onPaymentFrequencyChange = function(value, label){
        var $paymentDateEl  = $('.jq-cashPaymentDateCalendarPlaceHolder'),
            selectedVal     = (typeof value !== 'undefined' ) ? value : $('.jq-cashPaymentFrequency').val();

        //remove error
        $.removeMessage($('.jq-cashPaymentRepeatEndDate'),['jq-inputError','formFieldMessageError'],'textInputError');

        var paymentDate = $paymentDateEl.datepicker('getDate');
        paymentDate = GetFrequencyDate(paymentDate, selectedVal);

        _updateEndPaymentDate(paymentDate);
        CalculateSpinnerDate();
    };

    var _updateEndPaymentDate = function(date){
        var $endDateEl  = $('.jq-cashPaymentEndDateCalendarPlaceHolder'),
            formatDate  = $.formatDate(date,'mm/dd/yy');

        CheckNominatedDates($(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder').val(), GetSelectedRepeatFrequency('.jq-cashPaymentFrequency'));

        $endDateEl.datepicker( 'option', 'minDate', date);
        $endDateEl.datepicker("destroy");
        $endDateEl.datepicker(
            {
                changeMonth: true,
                changeYear: true,
                setDate: date,
                minDate: date,
                beforeShowDay: nominatedDates,
                onSelect: _onDateSelect,
                onChangeMonthYear: _onDateMonthYearChange
            }
        );

        $(_DOMElements.paymentForm).find('.jq-cashPaymentRepeatEndDate').val($.formatDate(date,'dd mmm yyyy')).attr('data-past', $.formatDate(date,'yyyy-mm-dd'));
        //$('.jq-cashPaymentEndDate').html($.formatDate(date,'dd'));
    };

    /**
     * @desc refresh accordion
     * @private
     */
    var _refreshAccordion = function(){
        $(_DOMElements.addPayeeBillerWrap).accordionView('refresh');
    };

    /**
     * Init tabs for add BPAY & pay anyone
     * @private
     */
    var _initTabs = function(){
        $(_DOMElements.addPayeeBillerWrap).tabs({
            select: function (event, ui) {
                _refreshAccordion();
                $(_DOMElements.addPayeeBillerWrap).find('form').resetForm();
            }
        });

        // Accessibility: Manage tabIndex
        $(_DOMElements.addPayeeBillerWrap).find('li[role="tab"]').removeAttr('tabindex');
        $(_DOMElements.addPayeeBillerWrap).find('.ui-tabs-anchor').removeAttr('tabindex');
    };

    /**
     * Check signal for this module
     * @returns {boolean}
     * @private
     */
    var _isSignalForMe = function(){
        var $form = $(_DOMElements.paymentForm),
            forMe = false;

        if($form.is(':visible')){
            forMe = true;
        }

        return forMe;
    };

    /**
     * Subscribe to add payee & billers
     * @private
     */
    var _subscribeToAddPayeeAndBillers = function(){
        org.bt.utils.communicate.subscribe('/addBiller/success', function(res) {
            if(_isSignalForMe()){
                _onCashPaymentAddBpayOrPayAnyoneSubmitSuccess(res);
            }
        });

        org.bt.utils.communicate.subscribe('/addBiller/failed', function(res) {
            if(_isSignalForMe()){
                _refreshAccordion();
            }
        });

        org.bt.utils.communicate.subscribe('/addBiller/validationComplete', function(res) {
            if(_isSignalForMe()){
                _refreshAccordion();
            }
        });

        org.bt.utils.communicate.subscribe('/addPayee/success', function(res) {
            if(_isSignalForMe()){
                _onCashPaymentAddBpayOrPayAnyoneSubmitSuccess(res);
            }
        });

        org.bt.utils.communicate.subscribe('/addPayee/failed', function(res) {
            if(_isSignalForMe()){
                _refreshAccordion();
            }
        });

        org.bt.utils.communicate.subscribe('/addPayee/validationComplete', function(res) {
            if(_isSignalForMe()){
                _refreshAccordion();
            }
        });

        org.bt.utils.communicate.subscribe('/accountDetails/expand', function(res) {
           $(_DOMElements.addPayeeBillerWrap).accordionView('hide');
        });

    };

    /**
     * @desc check payee needs to pre select
     * @private
     */
    var _preSelectPayee = function(){
        var hash = window.location.hash,
            payeeParam,payeeId,record;

        if(hash){
            payeeParam = hash.split('/');
            if($.isArray(payeeParam) && payeeParam[0] === '#payeeId'){
                payeeId = parseInt(payeeParam[1]);
                record = _findPayeeById(payeeId);
                if(record){
                    _setPayee(record);
                }
            }
        }
    };

    /**
     * @desc handle user permission related logic
     * @private
     */
    var _handlePermissions = function(){
        var $paymentDatePicker  = $(_DOMElements.paymentForm).find('.jq-cashPaymentDateCalendarPlaceHolder'),
            $repeatDatePicker   = $(_DOMElements.paymentForm).find('.jq-cashPaymentEndDateCalendarPlaceHolder'),
            $dateField          = $(_DOMElements.paymentForm).find('.jq-cashPaymentPaymentDate'),
            $repeatDateField    = $(_DOMElements.paymentForm).find('.jq-cashPaymentRepeatEndDate');

        if($paymentDatePicker.hasClass('jq-disabled')){
            $paymentDatePicker.hide();
            $dateField.val('');
        }
        if($repeatDatePicker.hasClass('jq-disabled')){
            $repeatDatePicker.hide();
            $repeatDateField.val('');
        }
    };
    /**
     * @desc init method for cash payment
     * @private
     */
    var _init = function(){
        _handleDeviceSpecific();
        _initPaymentConfirmModal();
        _initCustomDropdowns();
        _bindDOMEvents();
        _initTabs();
        _initDatePickers();
        _subscribeToAddPayeeAndBillers();
        _preSelectPayee();
        _handlePermissions();
    };
    return{
        /**
         * @memberOf org.bt.modules.cashPayment
         * @public
         */
        init:function(){
            _init();
        },
        selectPayee:function(id,record){
            record = _findPayeeById(id);
            if(record){
                _setPayee(record);
            }
        }
    };
})(jQuery,window, document);
