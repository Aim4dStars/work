/**
 * 
 */
org.bt.modules.createOrganisationIp = ( function ( $, window, document ) {

    var _DOMElements = {

        actionpersonType : '#personType',
        fullName : '#fullName',
        actionSilo : '#silo',
        createOrganisationIpForm:"#createOrganisationIpForm",
        isForeignRegistered : '#isForeignRegistered',
        registrationNumberType:'#registrationNumberType',
        registrationNumber:'#registrationNumber',
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]',
        dataValidationTagValueWithoutMinLength:'validate[required,custom[searchNumber],custom[customFunction]]',
        ariaRequired:'aria-required',
        ariaRequiredValue:'true',
        ariaInvalid:'aria-invalid',
        ariaInvalidValue:'true',
        isIssuedAtC:"#isIssuedAtC",
        isIssuedAtS:"#isIssuedAtS",
        startDate:"#startDate",
        industryCode:"#industryCode",
        priorityLevel:"#priorityLevel",
        addrspriorityLevel:"#addrspriorityLevel",
        usage:"#usage",
        addresseeNameText:"#addresseeNameText",
        addressType:"#addressType",
        streetNumber:"#streetNumber",
        streetName:"#streetName",
        streetType:"#streetType",
        city:"#city",
        state:"#state",
        postCode:"#postCode",
        country:"#country",
        organisationLegalStructureValue:"#organisationLegalStructureValue",
        purposeOfBusinessRelationship:"#purposeOfBusinessRelationship",
        sourceOfFunds:"#sourceOfFunds",
        sourceOfWealth:"#sourceOfWealth",
        characteristicType:"#characteristicType",
        characteristicCode:"#characteristicCode",
        characteristicValue:"#characteristicValue",
        frn:"#frn",
        frntype:"#frntype",
        confirmSubmit:"#confirmBtn",
        confirmModal:'.jq-confirmModal',
        cancelSubmit:"#cancelBtn"
    }
    var _confirmModalData = function(){      
       
        $("#tabDataSilo").html($("#silo option:selected").text());
        $("#tabDataFullName").html($("#fullName").val());
        $("#tabDataPersonType").html($("#personType").val());
        $("#tabIsForeignRegistered").html($("#isForeignRegistered").val());
        $("#tabRegNo").html($("#registrationNumber").val());
        $("#tabRegNoType").html($("#registrationNumberType option:selected").text());
        $("#tabIsIssuedAtC").html($("#isIssuedAtC").val());
        $("#tabIsIssuedAtS").html($("#isIssuedAtS").val());
        $("#tabIndustryCode").html($("#industryCode").val());
        $("#tabPriorityLevel").html($("#priorityLevel option:selected").text());
        $("#tabAddressPriorityLevel").html($("#addrspriorityLevel option:selected").text());
        $("#tabUsage").html($("#usage option:selected").text());
        $("#tabAddresseeName").html($("#addresseeNameText").val());
        $("#tabAddressType").html($("#addressType").val());
        $("#tabStreetNumber").html($("#streetNumber").val());
        $("#tabStreetType").html($("#streetType").val());
        $("#tabStreetName").html($("#streetName").val());
        $("#tabCity").html($("#city").val());
        $("#tabState").html($("#state").val());
        $("#tabPostCode").html($("#postCode").val());
        $("#tabCountry").html($("#country").val());
        $("#tabOrganisationLegalStructureValue").html($("#organisationLegalStructureValue").val());
        $("#tabPurposeOfBusinessRelationship").html($("#purposeOfBusinessRelationship").val());
        $("#tabSourceOfFunds").html($("#sourceOfFunds").val());
        $("#tabSourceOfWealth").html($("#sourceOfWealth").val());
        $("#tabCharacteristicType").html($("#characteristicType").val());
        $("#tabCharacteristicCode").html($("#characteristicCode").val());
        $("#tabCharacteristicValue").html($("#characteristicValue").val());
        $("#tabForeignRegistrationNumber").html($("#frn").val());
        $("#tabForeignRegistrationNumberType").html($("#frntype").val());
    };
    
    $(_DOMElements.confirmSubmit).click(function(event) {
        $("#createOrganisationIpForm").unbind('submit').submit();
    });
    $( _DOMElements.priorityLevel).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.addrspriorityLevel).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.frntype).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.actionSilo ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.registrationNumberType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.usage ).dropkick( {
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

        }
    } );
    
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
        $("#createOrganisationIpForm").unbind('submit').submit();
    });
    
    $(_DOMElements.cancelSubmit).click(function(event) {
        event.preventDefault();
        $(_DOMElements.confirmModal).dialog('close');
      });
 
    /*
     * init validation engine
    */ 
   $(_DOMElements.createOrganisationIpForm).validationEngine({    
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
   

} )( jQuery, window, document );
