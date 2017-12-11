org.bt.modules.idpsSnapshotReport = (function($,window,document) {
	
	 var _DOMElements = {
			   effectiveStartDatePickerHolder : '.jq-effectiveStartDateCalendarPlaceHolder',
		                  effectiveStartDate  : '.jq-effectiveStartDate',
		        	    startDatePickerHolder : '.jq-startDatePickerHolder',
			                        startDate : '.jq-startDate',
			          dateOfBirthPickerHolder : '.jq-dateOfBirthPickerHolder',
			                      dateOfBirth : '.jq-dateOfBirth'
	 };
	 
	var dateFormat = 'yyyy-MM-dd',
        dateFormatCSV = 'yyyy-mm-dd';
	 
	//bind all the DOM events - starts
    var _bindDOMEvents = function(){
    	
    	_initDatePickers();
    }; //bind all the DOM events - ends


    /**
     *  Init Date pickers for Portfolio, failedapp Summary
     */
    var _initDatePickers = function(){
        var todayDate = new Date(),
        	initialFromDate = new Date(),
            minAllowedYear = todayDate.getFullYear()-90,
            currentMonth = todayDate.getMonth(),
            currentDate = todayDate.getDate(),
            minAllowedDate = new Date(minAllowedYear, currentMonth, currentDate);
            //initialFromDate.setMonth(initialFromDate.getMonth()-12);
		var currFinDate = "07/01/" + todayDate.getFullYear();
    	currFinDate = new Date(currFinDate);    	
    	currFinDate.setFullYear(currFinDate.getFullYear());
		$(_DOMElements.effectiveStartDatePickerHolder).datepicker({
			maxDate: todayDate,
            minDate: minAllowedDate,
			changeMonth: true,
	        changeYear: true,
	        yearRange: "-90:+0",
	        //numberOfMonths: 12,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $('.jq-effectiveStartDate')
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');

		var effectiveStartDate = new Date();
		if($('.jq-effectiveStartDate').val() != null && $('.jq-effectiveStartDate').val() != "" && $('.jq-effectiveStartDate').val() != undefined){
			effectiveStartDate = $('.jq-startDate').val();
		}else{
			$('.jq-effectiveStartDate')
		    .val($.formatDate(effectiveStartDate,dateFormat))
		    .attr('data-placeholder',$.formatDate(initialFromDate,dateFormat));
		}
		
		
		$(_DOMElements.effectiveStartDatePickerHolder).datepicker("setDate", new Date($('.jq-effectiveStartDate').val()));
		
		$('.jq-effectiveStartDateCalendarIcon').click(_onDateCalendarEffectiveDateconClick);
		
		//Added for Start Date
		$(_DOMElements.startDatePickerHolder).datepicker({
			maxDate: todayDate,
            minDate: minAllowedDate,
			changeMonth: true,
	        changeYear: true,
	        yearRange: "-90:+0",
	        //numberOfMonths: 12,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $('.jq-startDate')
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');
		var startDate = new Date();
		if($('.jq-startDate').val() != null && $('.jq-startDate').val() != "" && $('.jq-startDate').val() != undefined){
			startDate = $('.jq-startDate').val();
		}else{
			$('.jq-startDate')
		    .val($.formatDate(startDate,dateFormat))
		    .attr('data-placeholder',$.formatDate(initialFromDate,dateFormat));
		}
		
		$(_DOMElements.effectiveStartDatePickerHolder).datepicker("setDate", new Date($('.jq-startDate').val()));
		
		$('.jq-startDateCalendarIcon').click(_onDateCalendarStartClick);
		
		//Added for Date of Birth
		$(_DOMElements.dateOfBirthPickerHolder).datepicker({
			maxDate: null != $('.jq-dateOfBirth').val() ? $('.jq-dateOfBirth').val(): todayDate,
            minDate: minAllowedDate,
			changeMonth: true,
	        changeYear: true,
	        yearRange: "-90:+0",
	        //numberOfMonths: 12,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $('.jq-dateOfBirth')
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');
		
		var dateOfBirth = new Date();
		if($('.jq-dateOfBirth').val() != null && $('.jq-dateOfBirth').val() != "" && $('.jq-dateOfBirth').val() != undefined){
			dateOfBirth = $('.jq-dateOfBirth').val();
		}else{
			$('.jq-dateOfBirth')
		    .val($.formatDate(dateOfBirth,dateFormat))
		    .attr('data-placeholder',$.formatDate(initialFromDate,dateFormat));
		}
		
		$(_DOMElements.effectiveStartDatePickerHolder).datepicker("setDate", new Date($('.jq-dateOfBirth').val()));
		
		$('.jq-birthDateCalendarIcon').click(_onDateCalendarBirthClick);
    };    

    var _onDateCalendarEffectiveDateconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.effectiveStartDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };
    
    var _onDateCalendarStartClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.startDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };
    
    var _onDateCalendarBirthClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.dateOfBirthPickerHolder),
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