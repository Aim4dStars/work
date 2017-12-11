/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintainIpToIpRelationship = ( function ( $, window, document ) {

	
    var _DOMElements = {

        silo : '#silo',
        personType : '#personType',
        cisKey:"#cisKey",
        requestedAction:"#requestedAction",
        addressType:"#addressType",
        usageId:"#usageId",
        validityStatus:"#validityStatus",
        priorityLevel:"#priorityLevel",
        emailAddress:"#emailAddress",
        countryCode:"#countryCode",
        areaCode:"#areaCode",
        localNumber:"#localNumber",
        contactMedium:"#contactMedium",
        maintainIpContactForm:"#maintainIpContactForm",
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]',
        dataValidationTagValueWithoutMinLength:'validate[required,custom[searchNumber],custom[customFunction]]',
        ariaRequired:'aria-required',
        ariaRequiredValue:'true',
        ariaInvalid:'aria-invalid',
        ariaInvalidValue:'true',
        confirmSubmit:"#confirmBtn",
        confirmModal:'.jq-confirmModal',
        cancelSubmit:"#cancelBtn"

    }
    
    var _confirmModalData = function(){      
        $("#tabDataPersonType").html($("#personType option:selected").text());
        $("#tabDataSilo").html($("#silo option:selected").text());
        $("#tabDatacisKey").html($("#cisKey").val());
        $("#tabDatarequestedAction").html($("#requestedAction option:selected").text());
        $("#tabDataaddressType").html($("#addressType option:selected").text());
        $("#tabDatausageId").html($("#usageId").val());
        $("#tabDatavalidityStatus").html($("#validityStatus").val());
        $("#tabDatapriorityLevel").html($("#priorityLevel").val());
        $("#tabDataemailAddress").html($("#emailAddress").val());
        $("#tabDatacountryCode").html($("#countryCode").val());
        $("#tabDataemailAddress").html($("#emailAddress").val());
        $("#tabDatalocalNumber").html($("#localNumber").val());
        $("#tabDataareaCode").html($("#areaCode").val());
        $("#tabDatacontactMedium").html($("#contactMedium option:selected").text());
        

        if($("#addressType option:selected").val() == "EMAIL"){
            $("#tabHeademailAddress").show();
            $("#tabDataemailAddress").show();
            $("#tabHeadcountryCode").hide();
            $("#tabDatacountryCode").hide();
            $("#tabHeadareaCode").hide();
            $("#tabDataareaCode").hide();
            $("#tabHeadlocalNumber").hide();
            $("#tabDatalocalNumber").hide();
            $("#tabHeadcontactMedium").hide();
            $("#tabDatacontactMedium").hide();
        }
        
        if($("#addressType option:selected").val() == "MOBILE"){
            $("#tabHeademailAddress").hide();
            $("#tabDataemailAddress").hide();
            $("#tabHeadcountryCode").show();
            $("#tabDatacountryCode").show();
            $("#tabHeadareaCode").show();
            $("#tabDataareaCode").show();
            $("#tabHeadlocalNumber").show();
            $("#tabDatalocalNumber").show();
            $("#tabHeadcontactMedium").show();
            $("#tabDatacontactMedium").show();
        }
    };
    $( _DOMElements.requestedAction ).dropkick( {
        inputClasses : [
            'inputStyleEightAddRemove'
        ],
        change : function ( value, label ) {

        }
    } );
    
    
    $( _DOMElements.personType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.silo ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.addressType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
        	if ( value == 'EMAIL' ) {
               _onAddTypeEMAIL();
            }
        	  if ( value == 'MOBILE' ) {
        		_onAddTypePHONE();
        		}

        }
    } );
    $( _DOMElements.contactMedium ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
   
    var _onAddTypeEMAIL=function()
    {
    	 _showElements([".jq-emailId" ]);
         _hideElements([".jq-areaCode",".jq-countryCode",".jq-localNo",".jq-contactMedium"]);
         _removeValidation(["#areaCode", "#countryCode", "#localNumber"]);
         $( "#emailAddress" ).attr(_DOMElements.dataValidationTag);
	
    };
    var _onAddTypePHONE=function()
    {
        _showElements([".jq-areaCode",".jq-countryCode",".jq-localNo",".jq-contactMedium"]);
    	_hideElements([".jq-emailId" ]);
         _removeValidation(["#emailAddress"]);
         $( "#areaCode" ).attr(_DOMElements.dataValidationTag);
         $( "#countryCode" ).attr(_DOMElements.dataValidationTag);
         $( "#localNumber" ).attr(_DOMElements.dataValidationTag);
	
    };
    var _showElements = function(classList) {
        classList.forEach(function(entry) {
            $(entry).removeClass("noDisplay");
        });
    };
    var _hideElements = function(classList) {
        classList.forEach(function(entry) {
            if(!$(entry).hasClass("noDisplay")) {
                $(entry).addClass("noDisplay");
                $(entry).removeAttr(_DOMElements.dataValidationTag);
            }
        });
    };
    var _removeValidation = function(list) {
        list.forEach(function(entry){
            $(entry).removeAttr(_DOMElements.dataValidationTag);
        });
    }
    var _initConfirmModalDialog = function(){
        _confirmModalData();
        var $dialogWindowElement    = $(_DOMElements.confirmModal);
        $dialogWindowElement.dialog({
            width:580,
            height:'auto',
            modal: true,
            autoOpen: false,
            draggable:false,
            dialogClass: 'modalBox',
            resizable: true,
            title:'',
            appendTo:'.layoutContent'
        }).removeClass('jq-cloak').end()
            .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
            .find('.ui-dialog-titlebar-close').addClass('modalClose');

        $(window).resize(function() {
            $dialogWindowElement.dialog('option', 'position', 'center');
        });
        
        $(_DOMElements.confirmModal).dialog('open');
        $('.ui-widget-overlay').addClass('modalBG');
    };
    
    $(_DOMElements.confirmSubmit).click(function(event) {
        $("#maintainIpContactForm").unbind('submit').submit();
    });
    
    $(_DOMElements.cancelSubmit).click(function(event) {
        event.preventDefault();
        $(_DOMElements.confirmModal).dialog('close');
      });
    
    /*
     * init validation engine
    */ 
   $(_DOMElements.maintainIpContactForm).validationEngine({    
        ajaxSubmit:false,
        beforeSubmit:function(form, errors){
           errors = [];
           return errors;

          },

        onValidationComplete:function(form,success){      
             if(success){
            	 _initConfirmModalDialog();
             }
        },
    
        customFunctions:{
        
        }
    
    }).submit(function(event) {
            event.stopPropagation();
            event.preventDefault();

        });
   _onAddTypeEMAIL();
} )( jQuery, window, document );