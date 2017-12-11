/**
 * @desc Initialize date pickers
 * @namespace org.bt.modules.initDatePickers
 */
org.bt.modules.initDatePickers = (function($,window,document) {

	var _constants =  {
			dateFormat : 'dd mmm yyyy' 	
	};
	
    var _DOMElements = {
    		dateCalendarPlaceHolder 	:	'.jq-dateCalendarPlaceHolder',
            dateCalendarIcon 			: 	'.jq-dateCalendarIcon',
            date 						: 	'.jq-date',
            dateFormFieldContainer		: 	'.jq-dateFormFieldContainer'
    };

	//bind all the DOM events - starts
    var _bindDOMEvents = function() {
    	_initDatePickers();
    }; //bind all the DOM events - ends

   
    
    /**
     *  Init Date pickers
     */
     var _initDatePickers = function(){
    	 
    	 
 	    //date field date picker - From - starts
 	    $(_DOMElements.dateFormFieldContainer).find(_DOMElements.dateCalendarPlaceHolder).each(function(){
 	    	var maxDate 		= 	new Date($(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).attr('data-future'));
 	    	var minDate 		= 	new Date($(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).attr('data-past'));
 	    	$(this).datepicker({
 	 			maxDate: maxDate,
 	 			minDate: minDate,
 	 			changeMonth: true,
 	 	        changeYear: true,
 	 	        onSelect: function(dateText, inst) {
 	 	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay),
 	 	            	$form			= $(this).parents("form");
 	 					
 	 	            $(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date)
 	 	                .val($.formatDate(selectedDate,_constants.dateFormat))
 	 	                .trigger('focusout');
 	 	            $(this).addClass('noDisplay');
 	 	            if($form.hasClass('jq-preventSubmit')){
 	 	            	$form.removeClass('jq-preventSubmit');
 	 	            }
 	 	          
 	 	        }
 	 	    }).addClass('noDisplay');
 	    	
 	    	var deaultDate 		= 	new Date($(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).attr('data-default-value')),
   	 	 		defaultFmtDate 	= 	$.formatDate(deaultDate,_constants.dateFormat);
 	    	$(this).parents(_DOMElements.dateFormFieldContainer).datepicker('setDate', deaultDate);
 	    	$(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).val(defaultFmtDate);
 	    });
 	    
 	    
 	    

         //hide date picker on window click
         $(document).click(function(e){
             var target              = $(e.target);
             
             if(!target.hasClass('jq-dateCalendarIcon')){
                 if(target.parents().filter('.jq-dateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                	 $(_DOMElements.dateCalendarPlaceHolder).addClass('noDisplay');
                 }
             }

         });
         
         $(_DOMElements.dateCalendarIcon).click(_onDateCalendarIconClick);
         
         
         $(_DOMElements.date).on('focusout',function(){
        	 _initDate.call(this);
         });
         
     }; //init date picker
     
     
     var _initDate = function(dateField) {
    	 var obj 			= 	dateField ? $(dateField) : $(this), 
    	 	 deaultDate		= 	new Date(obj.parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).attr('data-default-value')),
    	 	 defaultFmtDate 	= 	$.formatDate(deaultDate,_constants.dateFormat)
    	 	 dateValue		= 	obj.val();
    	 
    	 if((dateValue.trim().length == 0) && (obj.hasClass('jq-idpsReportsTDFromDate'))){
    		 deaultDate =  obj.parents(_DOMElements.dateFormFieldContainer).find('.jq-idpsReportsTDFromDate').attr('data-custom');
    		 defaultFmtDate 	= 	$.formatDate(new Date(deaultDate),_constants.dateFormat);
    		 obj.parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.dateCalendarPlaceHolder).datepicker('setDate', deaultDate);
    		 obj.val(defaultFmtDate); 
    	 }
    	 
    	 else if((dateValue.trim().length == 0) && (obj.hasClass('jq-idpsReportsTDToDate'))){
    		 deaultDate =  obj.parents(_DOMElements.dateFormFieldContainer).find('.jq-idpsReportsTDToDate').attr('data-future');
    		 defaultFmtDate 	= 	$.formatDate(new Date(deaultDate),_constants.dateFormat);
    		 obj.parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.dateCalendarPlaceHolder).datepicker('setDate', deaultDate);
    		 obj.val(defaultFmtDate); 
    	 }
    	 
    	 else if(dateValue.trim().length == 0){
    		 obj.parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.dateCalendarPlaceHolder).datepicker('setDate', deaultDate);
    		 obj.val(defaultFmtDate);
    	 }
    	
     };
     
     var _onDateCalendarIconClick = function(e){
         e.preventDefault();
         
         var $placeholder = $(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.dateCalendarPlaceHolder),
             toggle          =   ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay'),
             currentTargetPlaceHolder = $(this).parents(_DOMElements.dateFormFieldContainer).find(_DOMElements.date).attr('data-calendar');
             
         $(_DOMElements.dateFormFieldContainer).each(function(){
        	 if(currentTargetPlaceHolder != $(this).find(_DOMElements.date).attr('data-calendar')){
        		 $(this).find(_DOMElements.dateCalendarPlaceHolder).addClass('noDisplay');
        	 }
         });    
 	};
 	
 	
    var _init = function(){
        _bindDOMEvents();
    };

    return{
        init:function(){
            _init();
        },
        initDate:function(dateField){
        	_initDate(dateField);
        }
    };
})(jQuery,window, document);