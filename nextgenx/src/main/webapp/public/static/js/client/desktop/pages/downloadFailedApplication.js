org.bt.modules.idpsSnapshotReport = (function($,window,document) {
	
	 var _DOMElements = {
		        failedappFromDatePickerHolder : '.jq-failedappDownloadFromDateCalendarPlaceHolder',
		        failedappToDatePickerHolder : '.jq-failedappDownloadToDateCalendarPlaceHolder',
		        failedappSummaryReportForm : 'form.jq-idpssnapshotReportfailedappSummary',
		        failedappDownloadFromDate: '.jq-failedappDownloadFromDate',
		        failedappDownloadToDate: '.jq-failedappDownloadToDate'
	 };
	 
	var dateFormat = 'dd mmm yyyy',
        dateFormatCSV = 'yyyy-mm-dd';
	 
	//bind all the DOM events - starts
    var _bindDOMEvents = function(){
    	
    	_initDatePickers();
		//failedapp Summary Form AJAX Submit
		$(_DOMElements.failedappSummaryReportForm).validationEngine({
			ajaxSubmit          :false,
            onValidationComplete:function(form,success){
                if(success){
                    $(".jq-largeDataSetErrorMessage").addClass("noDisplay");
                    var failedAppFromDate =new Date($(_DOMElements.failedappDownloadFromDate).val()),
                        failedAppToDate =new Date($(_DOMElements.failedappDownloadToDate).val());
                    failedAppFromDate = $.formatDate(failedAppFromDate,'yyyymmdd');
                    failedAppToDate = $.formatDate(failedAppToDate,'yyyymmdd');
                    var data    = [{name:'fromDate', value:failedAppFromDate},{name:'toDate', value:failedAppToDate}],
                        action  = org.bt.utils.serviceDirectory.failedAppDownload();
                    org.bt.utils.communicate.get({action:action,data:data});

                }
            },
            submitOnSuccess     :false,
            onSubmitError:function(){},
            customFunctions:{
                'fromDate':function(rules,value){
                    return _compareDate(rules,value);
                },
                'toDate':function(rules,value){
                    return _compareDate(rules,value);
                }
            }
        });
    }; //bind all the DOM events - ends


    var _clearErrorMsgs = function(){
        //Remove errors if any
        $.removeMessage($(_DOMElements.failedappDownloadFromDate),['jq-inputError','formFieldMessageError'],'textInputError');
        $.removeMessage($(_DOMElements.failedappDownloadToDate),['jq-inputError','formFieldMessageError'],'textInputError');
    };
    var _compareDate = function(rules,value) {
        $(_DOMElements.failedappDownloadFromDate).attr('data-validation-customFunction-error','From date can\'t be later than the to date');
        $(_DOMElements.failedappDownloadToDate).attr('data-validation-customFunction-error','To date can\'t be earlier than from date');
        var fromDateNotValid =	$(_DOMElements.failedappDownloadFromDate).attr('data-error-date');
        var toDateNotFutureValid =	$(_DOMElements.failedappDownloadToDate).attr('data-error-future');
        var fromDateNotFutureValid =	$(_DOMElements.failedappDownloadFromDate).attr('data-error-future');
        var fromDateNotPastValid =	$(_DOMElements.failedappDownloadFromDate).attr('data-error-past');
        var toDateNotValid =	$(_DOMElements.failedappDownloadToDate).attr('data-error-date');
        var toDateNotPastValid =	$(_DOMElements.failedappDownloadToDate).attr('data-error-past');
        var fromDate = new Date( $(_DOMElements.failedappDownloadFromDate).val() );
        var toDate = new Date( $(_DOMElements.failedappDownloadToDate).val() );
        var matched = true;
        var selectedToDate =new Date( $(_DOMElements.failedappDownloadToDate).val() );
        selectedToDate.setMonth(selectedToDate.getMonth()-3);
        if(fromDate > toDate){
            matched = false;
        } else if(fromDate < selectedToDate){
            $(_DOMElements.failedappDownloadFromDate).attr('data-validation-customFunction-error','From Date can\'t be lesser than 3 months from To Date');
            $(_DOMElements.failedappDownloadToDate).attr('data-validation-customFunction-error','From Date can\'t be lesser than 3 months from To Date');
            matched = false;
        }
        else {
            if(fromDateNotFutureValid=='false' && fromDateNotPastValid =='false' && toDateNotFutureValid =='false' && toDateNotPastValid =='false' && fromDateNotValid =='false' && toDateNotValid =='false') {
                _clearErrorMsgs();
            }
        }
        //add rule to array
        rules.push({'name':'customFunction','matched':matched});

        return rules;
    };
    
    
    
    /**
     *  Init Date pickers for Portfolio, failedapp Summary
     */
    var _initDatePickers = function(){
        var todayDate = new Date(),initialFromDate = new Date(),
            minAllowedYear = todayDate.getFullYear()-7,
            currentMonth = todayDate.getMonth(),
            currentDate = todayDate.getDate(),
            minAllowedDate = new Date(minAllowedYear, currentMonth, currentDate);
        initialFromDate.setMonth(initialFromDate.getMonth()-3);
        $(_DOMElements.failedappDownloadFromDate).attr('data-future',$.formatDate(todayDate,dateFormatCSV));
        $(_DOMElements.failedappDownloadFromDate).attr('data-past',$.formatDate(minAllowedDate,dateFormatCSV));
        $(_DOMElements.failedappDownloadToDate).attr('data-future',$.formatDate(todayDate,dateFormatCSV));
        $(_DOMElements.failedappDownloadToDate).attr('data-past',$.formatDate(minAllowedDate,dateFormatCSV));
    	
		//date field date picker - Report Portfolio Valuation - ends
		var currFinDate = "07/01/" + todayDate.getFullYear();
    	currFinDate = new Date(currFinDate);    	
    	currFinDate.setFullYear(currFinDate.getFullYear()-1);
    	
		//date field date picker - failedapp Summary From - starts
		$(_DOMElements.failedappFromDatePickerHolder).datepicker({
			maxDate: todayDate,
            minDate: minAllowedDate,
			changeMonth: true,
	        changeYear: true,	 
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $('.jq-failedappDownloadFromDate')
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');
		$('.jq-failedappDownloadFromDate')
	    .val($.formatDate(initialFromDate,dateFormat))
	    .attr('data-placeholder',$.formatDate(initialFromDate,dateFormat));
		
		$(_DOMElements.failedappFromDatePickerHolder).datepicker("setDate", new Date($('.jq-failedappDownloadFromDate').val()));
		
		$('.jq-failedappDownloadFromDateCalendarIcon').click(_onDateCalendarISFIconClick);
		
		//date field date picker - failedapp Summary To - starts
		$(_DOMElements.failedappToDatePickerHolder).datepicker({
			maxDate: todayDate,
            minDate: minAllowedDate,
			changeMonth: true,
	        changeYear: true,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $('.jq-failedappDownloadToDate')
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');
		
		$(_DOMElements.failedappToDatePickerHolder).datepicker("setDate", new Date($('.jq-failedappDownloadToDate').val()));
		
		$('.jq-failedappDownloadToDate')
	    .val($.formatDate(new Date(),dateFormat))
	    .attr('data-placeholder',$.formatDate(new Date(),dateFormat));
		
		$('.jq-failedappDownloadToDateCalendarIcon').click(_onDateCalendarISTIconClick);
		
        //hide date picker on window click
        $(document).click(function(e){
            var target              = $(e.target),
                $dateCalendar       = $(_DOMElements.portfolioDatePickerHolder),
            	$dateCalendarFrom   = $(_DOMElements.failedappFromDatePickerHolder),
            	$dateCalendarTo     = $(_DOMElements.failedappToDatePickerHolder);

            if(!target.hasClass('jq-idpsReportPVDateCalendarIcon')){
                if(target.parents().filter('.jq-idpsReportPVDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendar.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-failedappDownloadFromDateCalendarIcon')){
                if(target.parents().filter('.jq-failedappDownloadFromDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarFrom.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-failedappDownloadToDateCalendarIcon')){
                if(target.parents().filter('.jq-failedappDownloadToDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarTo.datepicker().addClass('noDisplay');
                }
            }
        }); 
    };    

    var _onDateCalendarISFIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.failedappFromDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };
    
    var _onDateCalendarISTIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.failedappToDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };

    var _init = function(){
        _bindDOMEvents();
    };
    
    return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document);