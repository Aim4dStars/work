/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintaiArrangementRelationship = ( function ( $, window, document ) {

    var _DOMElements = {

        actionUseCaseName : '#useCaseName',
        actionpersonType : '#personType',
        actionlifecycleStatusReason : '#lifecycleStatusReason',
        actionSilo : '#silo',
        actionProductCpc : '#productCpc',
        maintainArrangementForm:'#maintainArrangementForm',
        dataValidationTag:'data-validation',
        dataValidationTagValue:'validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]',
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
  
    var _confirmModalData = function(){      
        $("#usecaseSpan").html($("#useCaseName option:selected").text());
        $("#selectedUsecase").html($("#useCaseName option:selected").text());
        $("#tabDataPersonType").html($("#personType option:selected").text());
        $("#tabDataSilo").html($("#silo option:selected").text());
        $("#tabDataCPC").html($("#productCpc option:selected").text());
        $("#tabDataLifeCycleReason").html($("#lifecycleStatusReason option:selected").text());
        $("#tabDataCisKey").html($("#cisKey").val());
        $("#tabDataVersionNoAr").html($("#versionNumberAr").val());
        $("#tabDataAccNo").html($("#accountNumber").val());
        $("#tabDataBsbNo").html($("#bsbNumber").val());
        $("#tabDataPanNo").html($("#panNumber").val());
        
        $("#tabHeadVersionNoIpAr").hide();
        $("#tabDataVersionNoIpAr").hide();
        $("#tabDataPanNo").hide();
        $("#tabHeadPanNo").hide();
        $("#tabHeadStartDate").hide();
        $("#tabDataStartDate").hide();
        
        if($("#useCaseName option:selected").val() == "ipar" || $("#useCaseName option:selected").val() == "iparthirdparty"){     
            $("#tabHeadAccNo").show();
            $("#tabDataAccNo").show();
            $("#tabHeadBsbNo").show();
            $("#tabDataBsbNo").show();
            $("#tabHeadLifeCycleReason").show();
            $("#tabDataLifeCycleReason").show();
            $("#tabHeadVersionNoAr").hide();
            $("#tabDataVersionNoAr").hide();
        }
        
        if($("#useCaseName option:selected").val() == "enddateiparthirdparty"){
            $("#tabDataVersionNoIpAr").html($("#versionNumberIpAr").val());
            $("#tabHeadVersionNoIpAr").show();
            $("#tabDataVersionNoIpAr").show();
            $("#tabDataVersionNoAr").html($("#versionNumberAr").val());
            $("#tabHeadVersionNoAr").show();
            $("#tabDataVersionNoAr").show();
            $("#tabDataStartDate").html($("#startDate").val());
            $("#tabHeadStartDate").show();
            $("#tabDataStartDate").show();
            $("#tabHeadAccNo").show();
            $("#tabDataAccNo").show();
            $("#tabHeadBsbNo").show();
            $("#tabDataBsbNo").show();
            
        }
        
        if($("#useCaseName option:selected").val() == "enddateiparsol"){
            $("#tabHeadVersionNoAr").show();
            $("#tabDataVersionNoAr").show();
            $("#tabHeadAccNo").show();
            $("#tabDataAccNo").show();
            $("#tabHeadBsbNo").show();
            $("#tabDataBsbNo").show();
            $("#tabHeadLifeCycleReason").hide();
            $("#tabDataLifeCycleReason").hide();
        }
        
        if($("#useCaseName option:selected").val() == "ipsar"){
            $("#tabHeadVersionNoAr").hide();
            $("#tabDataVersionNoAr").hide();
            $("#tabHeadVersionNoIpAr").hide();
            $("#tabDataVersionNoIpAr").hide();
        }
        
        if($("#useCaseName option:selected").val() == "ipsar" || $("#useCaseName option:selected").val() == "enddateipsar"){
            $("#tabHeadBsbNo").hide();
            $("#tabDataBsbNo").hide(); 
            $("#tabHeadAccNo").hide();
            $("#tabDataAccNo").hide();
            $("#tabDataPanNo").show();
            $("#tabHeadPanNo").show();
        } 
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
    
    $(_DOMElements.confirmSubmit).click(function(event) {
        $("#maintainArrangementForm").unbind('submit').submit();
    });
    
    $(_DOMElements.cancelSubmit).click(function(event) {
        event.preventDefault();
        $(_DOMElements.confirmModal).dialog('close');
      });
 
    /*
     * init validation engine
    */ 
   $(_DOMElements.maintainArrangementForm).validationEngine({    
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
   
    $( _DOMElements.actionUseCaseName ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
            if ( value == 'ipar' ) {
                _onActionUseCaseNameIpar();
            }
            if ( value == 'ipsar' ) {
                _onActionUseCaseNameIpsar();
            }
            if ( value == 'iparthirdparty' ) {
                _onActionUseCaseNameIpArThirdParty();
            }
            if ( value == 'enddateiparsol' ) {
                _onActionUseCaseNameEndDateIpArSol();
            }
            if ( value == 'enddateipsar' ) {
                _onActionUseCaseNameEndDateIpSar();
            }
            if ( value == 'enddateiparthirdparty' ) {
                _onActionEndDateIpArThirdParty();
            }
        }
    } );
    $( _DOMElements.actionpersonType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $(_DOMElements.hourVal).bind( {
        focusout: function(ev) {
            _appendZeroToTime($(_DOMElements.hourVal), $(_DOMElements.hourVal).val());
        }
    });
    $(_DOMElements.minVal).bind( {
        focusout: function(ev) {
            _appendZeroToTime($(_DOMElements.minVal), $(_DOMElements.minVal).val());
        }
    });
    $(_DOMElements.secVal).bind( {
        focusout: function(ev) {
            _appendZeroToTime($(_DOMElements.secVal), $(_DOMElements.secVal).val());
        }
    });
    var _appendZeroToTime = function (element, time) {
        time = time.replace(/^0+/, '');
        if(time <= 9) {
            $(element).val('0'+time);
        }
    }
    $( _DOMElements.actionlifecycleStatusReason ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
            if($("#silo").val() == "WPAC" && $("#lifecycleStatusReason").val() == "Restricted"){
                alert("For SILO As WPAC life cycle status reason should be Unrestricted.If you still want to select Restricted then proceed or else select Unrestricted.");
            }
            if($("#silo").val() == "BTPL" && $("#lifecycleStatusReason").val() == "Unrestricted"){
                alert("For SILO As BTPL life cycle status reason should be restricted.If you still want to select Unrestricted then proceed or else select restricted.");
            }         
        }
    } );
    $( _DOMElements.actionSilo ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
            if($("#silo").val() == "WPAC" && $("#lifecycleStatusReason").val() == "Restricted"){
                alert("For SILO As WPAC life cycle status reason should be Unrestricted.If you still want to select Restricted then proceed or else select Unrestricted.");
            }
            if($("#silo").val() == "BTPL" && $("#lifecycleStatusReason").val() == "Unrestricted"){
                alert("For SILO As BTPL life cycle status reason should be restricted.If you still want to select Unrestricted then proceed or else select restricted.");
            }   
        }
    } );
    $( _DOMElements.actionProductCpc ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {
        
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
    var _onActionUseCaseNameIpar = function () {
        var options = [];
        _showElements([".jq-personType",".jq-silo",".jq-productCPC",".jq-cisKey",".jq-accNumber",".jq-bsbNumber",".jq-lifeCycleStatus" ]);
        _hideElements([".jq-versionAR",".jq-versionIPAR",".jq-startDateIPAR",".jq-panNumber"]);
        _removeValidation(["#versionNumberAr", "#versionNumberIpAr", "#startDate", "#hour","#min","#sec", "#panNumber"]);

        $( "#accountNumber" ).attr('data-validation',  _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#bsbNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#selectContainer_productCpc .selectButton" ).prop( "disabled", false );
        options.push('<option value=\'35d1b65704184ae3b87799400f7ab93c\'>BT Invest</option>');
        options.push('<option value=\'797475d1e1b246528c49ef8a75a9315e\'>BT Panorama Super</option>');
        options.push('<option value=\'b38a555bc9bc43e88b776219057a67b8\'>BT Invest Direct</option>');
        _attachOptionsAndReload(options);
   
    };
    var _onActionUseCaseNameIpsar = function () {
        var options = [];
        _showElements([".jq-personType",".jq-silo",".jq-productCPC",".jq-cisKey",".jq-panNumber",".jq-lifeCycleStatus"]);
        _hideElements([".jq-versionAR",".jq-versionIPAR",".jq-startDateIPAR",".jq-accNumber",".jq-bsbNumber"]);
        _removeValidation(["#versionNumberAr", "#versionNumberIpAr", "#startDate", "#hour","#min","#sec", "#accountNumber", "#bsbNumber"]);

        $( "#panNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        options.push('<option value=\'457eba2b65ca4c2f937d0deae9866312\'>BT Panorama Service</option>');
        _attachOptionsAndReload(options);
    };
    var _onActionUseCaseNameIpArThirdParty = function () {
        var options = [];
        _showElements([".jq-personType",".jq-silo",".jq-productCPC",".jq-cisKey",".jq-accNumber",".jq-bsbNumber",".jq-lifeCycleStatus"]);
        _hideElements([".jq-versionAR",".jq-versionIPAR",".jq-startDateIPAR",".jq-panNumber"]);
        _removeValidation(["#versionNumberAr", "#versionNumberIpAr", "#startDate", "#hour","#min","#sec", "#panNumber"]);

        $( "#accountNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#bsbNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#selectContainer_productCpc .selectButton" ).prop( "disabled", false );
        options.push('<option value=\'35d1b65704184ae3b87799400f7ab93c\'>BT Invest</option>');
        options.push('<option value=\'797475d1e1b246528c49ef8a75a9315e\'>BT Panorama Super</option>');
        options.push('<option value=\'b38a555bc9bc43e88b776219057a67b8\'>BT Invest Direct</option>');
        _attachOptionsAndReload(options);
    };
    var _onActionUseCaseNameEndDateIpArSol = function () {
        var options = [];
        _showElements([".jq-personType",".jq-silo",".jq-productCPC",".jq-versionAR",".jq-cisKey",".jq-accNumber", ".jq-bsbNumber" ]);
        _hideElements([".jq-versionIPAR",".jq-startDateIPAR",".jq-panNumber",".jq-lifeCycleStatus"]);
        _removeValidation(["#versionNumberIpAr", "#startDate", "#hour","#min","#sec", "#panNumber"]);

        $( "#versionNumberAr" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#accountNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#bsbNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#selectContainer_productCpc .selectButton" ).prop( "disabled", false ); 
        options.push('<option value=\'35d1b65704184ae3b87799400f7ab93c\'>BT Invest</option>');
        options.push('<option value=\'797475d1e1b246528c49ef8a75a9315e\'>BT Panorama Super</option>');
        options.push('<option value=\'b38a555bc9bc43e88b776219057a67b8\'>BT Invest Direct</option>');
        _attachOptionsAndReload(options);
        
    };
    var _onActionUseCaseNameEndDateIpSar = function () {
        var options = [];
        _showElements([".jq-personType", ".jq-silo",".jq-productCPC", ".jq-versionAR", ".jq-cisKey" ,".jq-panNumber" ]);
        _hideElements([".jq-versionIPAR", ".jq-startDateIPAR", ".jq-accNumber", ".jq-bsbNumber", ".jq-lifeCycleStatus"]);
        _removeValidation(["#versionNumberIpAr", "#startDate", "#hour","#min","#sec", "#accountNumber", "#bsbNumber"]);

        $( "#panNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#versionNumberAr" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        options.push('<option value=\'457eba2b65ca4c2f937d0deae9866312\'>BT Panorama Service</option>');
        _attachOptionsAndReload(options);
    };
    var _onActionEndDateIpArThirdParty = function () {
        var options = [];
        _showElements([".jq-personType", ".jq-silo", ".jq-productCPC", ".jq-versionAR", ".jq-versionIPAR", ".jq-startDateIPAR", ".jq-cisKey", ".jq-accNumber", ".jq-bsbNumber" ]);
        _hideElements([".jq-panNumber", ".jq-lifeCycleStatus"]);
        _removeValidation(["#panNumber"]);
        $( "#versionNumberAr" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#versionNumberIpAr" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#startDate" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#accountNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValueWithoutMinLength);
        $( "#bsbNumber" ).attr(_DOMElements.dataValidationTag, _DOMElements.dataValidationTagValue);
        $( "#selectContainer_productCpc .selectButton" ).prop( "disabled", false ); 
        options.push('<option value=\'35d1b65704184ae3b87799400f7ab93c\'>BT Invest</option>');
        options.push('<option value=\'797475d1e1b246528c49ef8a75a9315e\'>BT Panorama Super</option>');
        options.push('<option value=\'b38a555bc9bc43e88b776219057a67b8\'>BT Invest Direct</option>');
        _attachOptionsAndReload(options);
    };
    
    var _attachOptionsAndReload = function (viewByOptions) {
          $(_DOMElements.actionProductCpc).html(viewByOptions);
           $(_DOMElements.actionProductCpc).dropkick('reload',
              {startSpeed: 0,
              inputClasses: [" selectToggle dk_input inputStyleEight "],
              container:$('#selectContainer_productCpc')
             
          });
      };
      

} )( jQuery, window, document );
