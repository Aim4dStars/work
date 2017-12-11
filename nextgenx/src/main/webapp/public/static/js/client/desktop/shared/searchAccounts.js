/**
 * @namespace org.bt.modules.searchAccount
 */
org.bt.modules.searchAccount = (function($,window,document) {
	
	var _DOMElements = {
			accountSearchInput		: '.jq-accountSearchInput',
			accountSearchButton		: '.jq-accountSearchButton',
			accountAutoSuggestForm  : '.jq-accountAutoSuggestForm',
			selected				: '.jq-selected'
	};
	
	var _templates = {
	        autoComplete:'#jq-accountSuggestTemplate'
	};
	
	
    
	var _bindDOMEvents = function(){
		
		$(_DOMElements.accountSearchInput).placeholder();
		//Initialize autocomplete for accounts
		_initAccountAutoSuggest();
		
		/*
         * init validation engine
         */
        $(_DOMElements.accountAutoSuggestForm).validationEngine({
            ajaxSubmit:false,
            onValidationComplete:function(form,success){
                _onSearchValidationComplete(form,success)
            }
        }).submit(function(event) {
                event.stopPropagation();
                event.preventDefault();
            });
		
		
	};
	
	
	 /**
     * @desc fires on search form validation complete
     * @param form
     * @param success
     * @private
     */
    var _onSearchValidationComplete = function(form,success){
        if(success){
            /*var config = {
                data:$.createFormSubmitRequestBody(form),
                action:org.bt.utils.serviceDirectory.clients
            };

            org.bt.utils.communicate.get(config);*/
        	window.location.href = './clients?searchCriteria='+$(_DOMElements.accountSearchInput).val()+'#accounts';
        }
    };
	
	/**
	 * Initialize auto complete for search accounts.
	 */
	
	var _initAccountAutoSuggest = function(){
		
		
		var $accountSearchInput = $(_DOMElements.accountSearchInput);

        if($accountSearchInput.length > 0){
        	
        	$accountSearchInput.bind('keyup',function(ev){
                _onSearchInputKeyUp(ev,$(this));
            }).parent().find('.jq-textClear').bind('click', function(ev){
            	$accountSearchInput.val('').focus();
            	$(this).addClass('noDisplay');
            	$accountSearchInput.trigger('keyup');
            });
        	/*$accountSearchInput.autocomplete({
                minLength: 2,
                source: function( request, response ) {
                    _autocompleteSource.apply(this, [request, response]);
                },
                select: function( event, ui ) {
                    _onAutocompleteSelect(event, ui);
                }
            }).data('autocomplete')._renderItem = _renderItem;*/
        	
        	
        	
        }
		
	};
	
	/**
     * @desc fires on search input keyup
     * @param event
     * @private
     */
    var _onSearchInputKeyUp = function(event, elem){
        var searchInputValue  = elem.val(),
	         code = event.which,
             $submitBtn,
             selected = $(_DOMElements.selected).val();
        
        /*if(searchInputValue.length == 0){
        	elem.parent().find('.jq-textClear').addClass('noDisplay');
        	$(_DOMElements.selected).val('');
        }else{
        	elem.parent().find('.jq-textClear').removeClass('noDisplay');
        	$(_DOMElements.selected).val('');
        }*/
        
        if(selected == '' && code == 13){
            $submitBtn = $(_DOMElements.accountSearchButton);
            $submitBtn.focus();
            //trigger click after a delay.
            setTimeout(function(){
                $submitBtn.trigger('click');
            },100);
        }
        
    };
	
	 /**
     * @desc trigger on option select
     * @param ev as event
     * @param ui
     * @private
     */
    var _onAutocompleteSelect = function(ev,ui){
    	var url             = org.bt.utils.serviceDirectory.toSnapshotViewFromMessageCentre(),
    		encodedUrl      = url;
    	$(_DOMElements.selected).val(ui.item.label);
	    encodedUrl = encodedUrl.replace(new RegExp('{clientId}','g'), ui.item.clientIdEncoded);
	    encodedUrl = encodedUrl.replace(new RegExp('{portfolioId}','g'), ui.item.portfolioId);

    	org.bt.utils.communicate.get({action:encodedUrl});
    };
	
	

    /**
     *
     * @param ul
     * @param item
     * @returns {object}
     * @private
     */
    var _renderItem = function(ul, item){
    	
    	
        return $('<li></li>')
            .addClass('resultItem')
            .data('item.autocomplete', item)
            .append(_createItem(item))
            .appendTo(ul);
    };

    var _createItem = function(data){
    	
    	var 	term 		= $(_DOMElements.accountSearchInput).val(),
    			re  		 = new RegExp(term, 'gi') ;
				highlightTxt = "<span class='emphasis'>" + term + "</span>";
		
    	Handlebars.registerHelper('accountName', function() {
    		  return new Handlebars.SafeString(
    			data.accountName.replace(re, function (matched) {
    				highlightTxt = "<span class='emphasis'>" + matched + "</span>";
    				return highlightTxt;
                })
               ); 
    	});
    	
    	Handlebars.registerHelper('cashAccountNumber', function() {
  		  return new Handlebars.SafeString(
  			data.cashAccountNumber.replace(re, highlightTxt)
  		  );
    	});
    
        var tmpl            = $(_templates.autoComplete).html(),
            compiledTmpl    = Handlebars.compile(tmpl),
            compiletHtml 	= compiledTmpl(data);
        
        return compiletHtml;
    };
    /**
     * @desc Provide source to client search
     * @param {Object} request
     * @param {function} response
     * @private
     */
    var _autocompleteSource = function(request, response){
        var term = request.term;
        
        var config = {
            url:org.bt.utils.serviceDirectory.searchClientAccounts,
            onSuccess:_onAutoCompleteSuccess,
            onError:_onAutoCompleteError,
            data:{'searchCriteria':term},
            callback:response
        };
        org.bt.utils.communicate.ajax.apply(this,[config]);
    };

    /**
     * @desc Fired on auto-complete ajax req success
     * @param {Object} res JSON response
     * @param {Function} callback function
     * @private
     */
    var _onAutoCompleteSuccess = function(res,callback){

    	var $element = this.element;
    	var data = [];
        if(res.success){
        	$.each(res.data,function(k,v){
        		
                
        		data.push({
                    'accountName'  		: v.accountName,
                    'accountType'  		: v.accountType,
                    'productName' 		: v.productName,
                    'portfolio'    		: '$'+v.portfolio,
                    'cashAccountNumber' : v.cashAccountNumber,
                    'label'    			: v.accountName,
                    'value'    			: v.accountName,
                    'clientIdEncoded'   : v.clientIdEncoded,
                    'portfolioId'		: v.portfolioId
                    
                });
            });
        } else {
        	//trigger validation
            $element.trigger('focusout');
        }
        
        if(data.length == 0){
          	 data.push({
                   'noData'	   : true,
                   'label'     : $element.val(),
                   'value'     : $element.val()
               });
        }
        callback(data);
           
    };

    /**
     * @desc Fired on auto-complete ajax req failed
     * @private
     */
    var _onAutoCompleteError = function(res,callback){
    	
    	var $element = this.element,
    		data = [];
    	
    	data.push({
            'noData'	   : true,
            'label'     : $element.val(),
            'value'     : $element.val(),
            'serverError':true
        });
    	
    	callback(data);
 
    };



    var _init = function(){
        _bindDOMEvents();
    };
   
    return{
       /**
        * @memberOf org.bt.modules.searchAccount
        * @public
        */
        init:function(){
            _init();
        }
    };
})(jQuery,window, document);


