/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintainIpToIpRelationship = ( function ( $, window, document ) {

    var _DOMElements = {
        actionUseCaseName : '#useCaseName',
        role : '#personType',
        partyRelType : '#partyRelType',
        silo : '#silo',
        country : '#country',
        identityDocument1:"#identityDocument1",
        identityDocumentnonphoto1:"#identityDocumentnonphoto1",
        identityDocumentnonphoto2:"#identityDocumentnonphoto2",
        photoIdDoc:"#photoIdDoc",
        photoIdDocFields:"#photoIdDocFields",
        nonPhotoDiv:"#nonPhotoDiv",
        nonphotoIdDoc2:"#nonphotoIdDoc2",
        verifiedFrom:"#verifiedFrom",
        verifiedFromPassport:"#verifiedFromPassport",
        verifiedFromAgeCard:"#verifiedFromAgeCard",
        state:"#state",
        state_agecard:"#state_agecard",
        documentType:"#documentType",
        photoIdDocFieldsDrivingLicense:"#photoIdDocFieldsDrivingLicense",
        photoIdDocFieldsPassport:"#photoIdDocFieldsPassport",
        photoIdDocFieldsAgeCard:"#photoIdDocFieldsAgeCard",
        registrationNumberType:"#registrationNumberType",
        address:"#address",
        usage:"#usage",
        maintainIndividualIdvForm:'#maintainIndividualIdv',
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]',
        dataValidationTagValueWithoutMinLength:'validate[required,custom[searchNumber],custom[customFunction]]',
        ariaRequired:'aria-required',
        ariaRequiredValue:'true',
        ariaInvalid:'aria-invalid',
        ariaInvalidValue:'true',
        drp:"#optAttrName",
        confirmSubmit:"#confirmBtn",
        confirmModal:'.jq-confirmModal',
        cancelSubmit:"#cancelBtn",
        requestedAction:"#requestedAction",
        idvType:"#idvType",
        involvedPartyNameType:"#involvedPartyNameType",
        isSoleTrader:"#isSoleTrader",
        isForeignRegistered:"#isForeignRegistered"

    }
    
    var _confirmModalData = function(){      
        
        $("#tabDataCustomerCisKey").html($("#cisKey").val());
        $("#tabDataPersonType").html($("#personType option:selected").text());
        $("#tabDataSilo").html($("#silo option:selected").text());
        $("#tabDataCPC").html($("#productCpc option:selected").text());
        $("#tabDataFullName").html($("#fullName").val());
        $("#tabDataFirstName").html($("#firstName").val());
        $("#tabDataLastName").html($("#lastName").val());
        $("#tabDataMiddleName").html($("#middleName").val());
        $("#tabDataAgentCISKey").html($("#agentCisKey").val());
        $("#tabDataAgentName").html($("#agentName").val());
		$("#tabDataEmployerName").html($("#employerName").val());
		$("#tabDataDateOfBirth").html($("#dateOfBirth").val());
		$("#tabDataextIdvDate").html($("#extIdvDate").val());
		$("#tabDataAddressline1").html($("#addressline1").val());
		$("#tabDataAddressline2").html($("#addressline2").val());
		$("#tabDataCity").html($("#city").val());
		$("#tabDataState").html($("#state").val());
		$("#tabDataPincode").html($("#pincode").val());
		$("#tabDataCountry").html($("#country").val());
		$("#tabDataUsage").html($("#usage option:selected").text());
		$("#tabDataDocumentType").html($("#documentType option:selected").text());
		$("#tabDataRegistrationNumberType").html($("#registrationNumberType option:selected").text());
		$("#tabDataRegistrationNumber").html($("#registrationNumber").val());
		$("#tabDataCustomerNumber").html($("#customerNumber").val());
		$("#tabDataEVRoleType").html($("#evRoleType").val());
		$("#tabDataEVBusinessEntityName").html($("#evBusinessEntityName").val());
		$("#tabDataEVRecepientEmail1").html($("#evRecepientEmail1").val());
		$("#tabDataEVRecepientEmail2").html($("#evRecepientEmail2").val());
		$("#tabDataIsSoleTrader").html($("#isSoleTrader").val());
		var tabDataAttributeName=document.getElementById("tabDataAttributeName");
		var optAttrName=$('select[name="optAttrName"]').map(function() {
            return this.value
        }).get();
		tabDataAttributeName.innerHTML=optAttrName;
		var tabDataAttributeValue=document.getElementById("tabDataAttributeValue");
		var optAttrVal=$('input[name="optAttrVal"]').map(function() {
            return this.value
        }).get();
		tabDataAttributeValue.innerHTML=optAttrVal;
		
		$("#tabDataRequestedAction").html($("#requestedAction").val());
		$("#tabDataInvolvedPartyNameType").html($("#involvedPartyNameType").val());
		$("#tabDataIsOtherName").html($("#isOtherName").val());
		$("#tabDataHfcpFullName").html($("#hfcpFullName").val());
		$("#tabDataIsRegulatedBy").html($("#isRegulatedBy").val());
		$("#tabDataIsIssuedAtCountry").html($("#isIssuedAtCountry").val());
		$("#tabDataIsIssuedAtState").html($("#isIssuedAtState").val());
		$("#tabDataIsForeignRegistered").html($("#isForeignRegistered").val());
		$("#tabDataiparAddressLine1").html($("#iparAddressLine1").val());
		$("#tabDataiparCity").html($("#iparCity").val());
		$("#tabDataiparState").html($("#iparState").val());
		$("#tabDataiparPostCode").html($("#iparPostCode").val());
		$("#tabDataiparCountry").html($("#iparCountry").val());
		$("#tabDataiparRoleType").html($("#iparRoleType").val());
		$("#tabDataiparCisKey").html($("#iparCisKey").val());
		$("#tabDataiparRoleId").html($("#iparRoleId").val());
    };
    
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
    $( _DOMElements.isForeignRegistered ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.isSoleTrader ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.requestedAction ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.idvType ).dropkick( {

        change : function ( value, label ) {
        	if ( value == 'Organisation' ) {
                _onSelectOrganisation();
                $("#primaryP").removeClass("noDisplay");
            }else {
            	_onSelectIndividual();
            	$("#primaryP").addClass("noDisplay");
            }
        }
    } );
    $( _DOMElements.involvedPartyNameType ).dropkick( {

        change : function ( value, label ) {
        	
        }
    } );
    
    $( _DOMElements.drp ).dropkick( {
        inputClasses : [
            'inputStyleFive'
        ],
        change : function ( value, label ) {

        }
    } );
    
    $( _DOMElements.registrationNumberType).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.role ).dropkick( {

        change : function ( value, label ) {
        	
        }
    } );
    $( _DOMElements.silo ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.identityDocumentnonphoto1 ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.identityDocumentnonphoto2 ).dropkick( {

        change : function ( value, label ) {

        }
    } );
   
    $( _DOMElements.verifiedFrom ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.verifiedFromPassport ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.verifiedFromAgeCard ).dropkick( {

        change : function ( value, label ) {

        }
    } );
  
    $( _DOMElements.state_agecard ).dropkick( {

        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.usage ).dropkick( {
        inputClasses : [
            'inputStyleFive'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.documentType ).dropkick( {
        inputClasses : [
            'inputStyleFive'
        ],
        change : function ( value, label ) {
        }
    } );
    $( _DOMElements.identityDocument1 ).dropkick( {
        change : function ( value, label ) {
        	if(value =='photoDoc'){
        		 $( _DOMElements.photoIdDoc ).removeClass("noDisplay");
        		 $( _DOMElements.nonPhotoDiv ).addClass("noDisplay");
        	}
        	else if(value=='nonphotoDoc'){
        		 $( _DOMElements.nonPhotoDiv ).removeClass("noDisplay");
        		 $( _DOMElements.photoIdDoc ).addClass("noDisplay");
        	}
        	else{
        		$( _DOMElements.photoIdDoc ).addClass("noDisplay");
        	}
        }
    } );
    
    $(_DOMElements.confirmSubmit).click(function(event) {
        $("#maintainIndividualIdv").unbind('submit').submit();
    });
    $(_DOMElements.cancelSubmit).click(function(event) {
        event.preventDefault();
       $(_DOMElements.confirmModal).dialog('close');
      });
 
    var _onSelectOrganisation = function () {
    	
    	 $("#hfcpFullNameP").removeClass("noDisplay");
    	 $("#isRegulatedByP").removeClass("noDisplay");
    	 $("#isRegulatedByP").removeClass("noDisplay");
    	 $("#isRegulatedByP").removeClass("noDisplay");
    	 $("#isForeignRegisteredP").removeClass("noDisplay");
    	 $("#isIssuedAtCountryP").removeClass("noDisplay");
    	 $("#isIssuedAtStateP").removeClass("noDisplay");
    	 $("#involvedPartyNameTypeP").removeClass("noDisplay");
         $("#isOtherNameP").removeClass("noDisplay");
         $("#iparDetailsP").removeClass("noDisplay");

    };
    var _onSelectIndividual = function () {
    	
   	 $("#hfcpFullNameP").addClass("noDisplay");
   	 $("#isRegulatedByP").addClass("noDisplay");
   	 $("#isRegulatedByP").addClass("noDisplay");
   	 $("#isRegulatedByP").addClass("noDisplay");
   	 $("#isForeignRegisteredP").addClass("noDisplay");
   	 $("#isIssuedAtCountryP").addClass("noDisplay");
  	 $("#isIssuedAtStateP").addClass("noDisplay");
  	 $("#involvedPartyNameTypeP").addClass("noDisplay");
  	 $("#isOtherNameP").addClass("noDisplay");
  	 $("#iparDetailsP").addClass("noDisplay");
   };
    /*
     * init validation engine
    */ 
   $(_DOMElements.maintainIndividualIdvForm).validationEngine({    
        ajaxSubmit:false,
        beforeSubmit:function(form, errors){

           errors = [];

           return errors;

          },

        onValidationComplete:function(form,success){      
             if(success){
            	// $("#maintainIndividualIdv").unbind('submit').submit();
            	  _initConfirmModalDialog();
             }
        },
    
        customFunctions:{
        
        }
    
    }).submit(function(event) {
            event.stopPropagation();
            event.preventDefault();

        });
} )( jQuery, window, document );