/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintaiIpToIpRelationship = ( function ( $, window, document ) {

    var _DOMElements = {

        actionUseCaseName : '#useCaseName',
        sourcepersonType : '#sourcePersonType',
        partyRelType : '#partyRelType',
        actionSilo : '#silo',
        targetPersonType : '#targetPersonType',
        partyRelStatus:"#partyRelStatus",
        maintainIpToIpRelationshipForm:'#maintainIPtoIPRelationship',
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]',
        dataValidationTagValueWithoutMinLength:'validate[required,custom[searchNumber],custom[customFunction]]',
        ariaRequired:'aria-required',
        ariaRequiredValue:'true',
        ariaInvalid:'aria-invalid',
        ariaInvalidValue:'true',
        confirmSubmit:"#confirmButton",
        confirmModalDialog:'.jq-confirmModal',
        cancelSubmit:"#cancelButton"

    }
    
    $(_DOMElements.confirmSubmit).click(function(event) {
        $(_DOMElements.maintainIpToIpRelationshipForm).unbind('submit').submit();
    });
    
    $(_DOMElements.cancelSubmit).click(function(event) {
        event.preventDefault();
        $(_DOMElements.confirmModalDialog).dialog('close');
      });
    
    var _confirmModalData = function(){ 
        $("#usecaseContent").html($("#useCaseName option:selected").text());
        $("#selectedUsecase").html($("#useCaseName option:selected").text());
        $("#tabDataSPersonType").html($("#sourcePersonType option:selected").text());
        $("#tabDataTPersonType").html($("#targetPersonType option:selected").text());

        $("#tabDataSilo").html($("#silo option:selected").text());
        $("#tabDataSCisKey").html($("#sourceCISKey").val());
        $("#tabDataTCisKey").html($("#targetCISKey").val());
        $("#tabDataPartyRelType").html($("#partyRelType").val());
        $("#tabDataPartyRelStatus").html($("#partyRelStatus").val());
        $("#tabDataPartyRelStartDt").html($("#partyRelStartDate").val());
        $("#tabDataPartyRelEndDt").html($("#partyRelEndDate").val());       
        $("#tabDataVersionNo").html($("#versionNumber").val());
        
        if($("#useCaseName option:selected").val() == "Add"){
            $("#tabDataPartyRelEndDt").hide();
            $("#tabHeadPartyRelEndDt").hide();
            $("#tabHeadVersionNo").hide();
            $("#tabDataVersionNo").hide();
        }
        if($("#useCaseName option:selected").val() == "Modify"){
            $("#tabDataPartyRelEndDt").show();
            $("#tabHeadPartyRelEndDt").show();
            $("#tabHeadVersionNo").show();
            $("#tabDataVersionNo").show();
        }
        
    }
    
   $(_DOMElements.maintainIpToIpRelationshipForm).validationEngine({    
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
    
    
    $( _DOMElements.sourcepersonType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.partyRelType ).dropkick( {
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
    $( _DOMElements.targetPersonType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.partyRelStatus ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );

    $( _DOMElements.actionUseCaseName ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
            if ( value == 'Add' ) {
            	_onActionUseCaseNameAdd();
            }
            if ( value == 'Modify' ) {
            	_onActionUseCaseNameModify();
            }
        }
    } );
    
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
    
    var _onActionUseCaseNameAdd = function () {    	
    	$("tr").removeClass("noDisplay");
    	$(".jq-formSubmit").attr('disabled',false);
    	_hideElements([".jq-version"]);
    	_removeValidation(["#versionNumber"]);
    	$("#partyRelEndDateP").addClass("noDisplay");
    };
    var _onActionUseCaseNameModify = function () {
    	$("tr").removeClass("noDisplay");
        _showElements([".jq-version"]);
        $( "#versionNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
    	$(".jq-formSubmit").attr('disabled',false);
    };
    
    var _initConfirmModalDialog = function(){
        _confirmModalData();
        var $dialogWindowElement    = $(_DOMElements.confirmModalDialog);
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
        
        $(_DOMElements.confirmModalDialog).dialog('open');
        $('.ui-widget-overlay').addClass('modalBG');
    };

} )( jQuery, window, document );