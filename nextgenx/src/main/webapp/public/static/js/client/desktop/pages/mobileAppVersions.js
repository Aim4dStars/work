org.bt.modules.mobileAppVersion = (function ($, window) {
    var _DOMElements = {
        updatePlatform: '.jq-updatePlatform',
        updateVersion: '.jq-updateVersion',
        addPlatform: '.jq-addPlatform',
        addVersion: '.jq-addVersion',
        updateVersionForm :'.jq-updateVersionForm',
        addVersionForm :'.jq-addVersionForm'
    };

    var _getData = function (operation) {
          return [
            {'name':'platform', 'value' : $.trim($(_DOMElements[operation + 'Platform']).val())},
            {'name':'version', 'value' : $.trim($(_DOMElements[operation + 'Version']).val())}
        ];
    };

    var _validateVersion = function(rules,value){
        matched = /^(\d+\.)?(\d+\.)?(\*|\d+)$/.test(value);
        rules.push({'name':'unsignedInteger','matched':matched});
        return rules;
    };


    return {
        init: function () {
            $(_DOMElements.updatePlatform).dropkick({
               inputClasses: ["inputStyleFive"]
            });

             $(_DOMElements.updateVersionForm).validationEngine({
                customFunctions:{
                 'version': _validateVersion
                },
                onValidationComplete:function(form,success){
                    if( success){
                        var action = 'secure/page/serviceOps/updateMobileAppVersion';
                        org.bt.utils.communicate.post({action:action, data : _getData('update')});
                    }
                }
             });

             $(_DOMElements.addVersionForm).validationEngine({
                 customFunctions:{
                  'version':_validateVersion
                 },
                 onValidationComplete:function(form,success){
                     var action;
                     if( success){
                         action = 'secure/page/serviceOps/admin/updateMobileAppVersion';
                         org.bt.utils.communicate.post({action:action, data : _getData('add')});
                     }
                 }
              });
        }
    };
})(jQuery, window);
