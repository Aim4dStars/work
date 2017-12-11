/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.createIndividualIp = ( function ( $, window, document ) {

    var _DOMElements = {

        actionSilo : '#silo',
        actionPrefixtitle : "#prefix",
        actionGender : "#gender",
        actionUsage  : "#usage",
        actionAddressType  :  "#addressType",
        createIndividaulIPForm:'#createIndividaulIPForm',
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchText],custom[minLength],custom[customFunction]]',
        dataValidationTagValueWithoutMinLength:'validate[required,custom[searchNumber],custom[customFunction]]',
        ariaRequired:'aria-required',
        ariaRequiredValue:'true',
        ariaInvalid:'aria-invalid',
        ariaInvalidValue:'true',
        hourVal:"#hour",
        minVal:"#min",
        secVal:"#sec",
        confirmSubmit:"#confirmBtn",
        confirmModal:'.jq-confirmModal',
        cancelSubmit:"#cancelBtn"
    }
    
    $(_DOMElements.actionSilo).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
          
        }
    } );
    
    $(_DOMElements.actionUsage).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
          
        }
    } );
    
    $(_DOMElements.actionAddressType).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
          
        }
    } );
    
    $(_DOMElements.actionGender).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
          
        }
    } );
    
    $(_DOMElements.actionPrefixtitle).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    
    /*
     * init validation engine
    */ 
   $(_DOMElements.createIndividaulIPForm).validationEngine({    
        ajaxSubmit:false,
        beforeSubmit:function(form, errors){

           errors = [];

           return errors;

          },

        onValidationComplete:function(form,success){      
              if(success){
                  _initConfirmModalDialog();
                  //$("#createIndividaulIPForm").unbind('submit').submit();
              }
        },
    
        customFunctions:{
        
        }
    
    }).submit(function(event) {
            event.stopPropagation();
            event.preventDefault();

        });
   
   var _confirmModalData = function(){ 
       $("#title").html("Create Individual IP");
       $("#selectedSilo").html($("#silo option:selected").text());
       $("#tabDataPrefix").html($("#prefix option:selected").text());
       $("#tabDataFirstName").html($("#firstName").val());
       $("#tabDataLastName").html($("#lastName").val());
       $("#tabDataAltName").html($("#altName").val());
       $("#tabDataGender").html($("#gender").val());
       $("#tabDataBirthDate").html($("#birthDate").val());
       $("#tabDataIsForeignRegistered").html($("#isForeignRegistered").val());
       $("#tabDataRoleType").html($("#roleType").val());
       $("#tabDataPurpOfBusRelation").html($("#purposeOfBusinessRelationship").val());
       $("#tabDataSourceOfFund").html($("#sourceOfFunds").val());
       $("#tabDataSourceOfWealth").html($("#sourceOfWealth").val());
       $("#tabDataUsage").html($("#usage").val());
       $("#tabDataAddressee").html($("#addresseeNameText").val());
       $("#tabDataAddressType").html($("#addressType").val());
       $("#tabDataStreetNumber").html($("#streetNumber").val());
       $("#tabDataStreetName").html($("#streetName").val());
       $("#tabDataStreetType").html($("#streetType").val());
       $("#tabDataCity").html($("#city").val());
       $("#tabDataState").html($("#state").val());
       $("#tabDataPostCode").html($("#postCode").val());
       $("#tabDataCountry").html($("#country").val());
       $("#tabDataRegNumber").html($("#registrationIdentifierNumber").val());
       $("#tabDataRegType").html($("#registrationIdentifierNumberType").val());
       $("#tabDataHasLoansWithOtherBanks").html($("#hasLoansWithOtherBanks").val());
       $("#tabDataMiddleNames").html($("#middleNames").val());
       $("#tabDataPreferredName").html($("#preferredName").val());
       $("#tabDataAlternateName").html($("#altName").val());
       $("#tabDataIsPreferred").html($("#isPreferred").val());
       $("#tabDataEmploymentType").html($("#employmentType").val());
       $("#tabDataOccupationCode").html($("#occupationCode").val());
       $("#tabDataRegArrRegistrationNumber").html($("#registrationArrangementsRegistrationNumber").val());
       $("#tabDataRegArrRegistrationNumberType").html($("#registrationArrangementsRegistrationNumberType").val());
       $("#tabDataRegisArrCountry").html($("#registrationArrangementsCountry").val());
       $("#tabDataRegNumber").html($("#registrationArrangementsState").val());
       $("#tabDataAddressLine1").html($("#addressLine1").val());
       $("#tabDataAddressLine2").html($("#addressLine2").val());
       $("#tabDataAddressLine3").html($("#addressLine3").val());
       $("#tabDataisFloorNumber").html($("#floorNumber").val());
       $("#tabDataUnitNumber").html($("#unitNumber").val());
       $("#tabDataBuildingName").html($("#buildingName").val());
   };
   
   $(_DOMElements.confirmSubmit).click(function(event) {
       $("#createIndividaulIPForm").unbind('submit').submit();
   });
   
   $(_DOMElements.cancelSubmit).click(function(event) {
       event.preventDefault();
       $(_DOMElements.confirmModal).dialog('close');
     });
   
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
   
   
    $( _DOMElements.actionpersonType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );

} )( jQuery, window, document );