/**
 * @namespace org.bt.modules.docLibUtils
 */
org.bt.modules.docLibUtils =  (function($,window,document,moment) {

    var _hideMessageBox = function () {
        if($(".jq-messageBox").attr('class').indexOf('noDisplay') === -1) {
            $('.jq-messageBox').addClass("noDisplay");
        }
    };

    var _showMessageBox = function (msg, msgClass) {
        $('.jq-messageBox').addClass(msgClass);
        $('.jq-messageBox .jq-message').text(msg);
        $('.jq-messageBox').removeClass("noDisplay");
    };

    var _toTitleCase = function (str){
        return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
    }

    var _removeDropKick = function (dropdownId){

        var dropdown = $('#'+dropdownId);
        dropdown.removeData("dropkick");
        dropdown.find('option').remove();
        $("#selectContainer_"+dropdownId).remove();

    };

    var _getLabel = function (v, items){
        var label;
        $.each(items, function (i, item) {
            if(item.value === v){
                label = item.text;
            }
        });
        return label;
    };

    var _attachOptions = function (dropdownElement, items){
        $.each(items, function (i, item) {
            $(dropdownElement).append($('<option>', {
               value: item.value,
               text : item.text
            }));
        });
    };

    var _initDropdown = function(dropdownElement, iClasses){

        var dropdown  = $(dropdownElement);

        $(dropdown).dropkick({
            change: function (value, label) {
                $(this).change();
            },
            inputClasses: [iClasses]
        });
    };

    var _initDropdownWithOptions = function(dropdownElement, items, iClasses){
        var dropdownElement = $(dropdownElement);

        _attachOptions(dropdownElement, items);
        $(dropdownElement).dropkick({
            startSpeed: 0,
            change: function (value, label) {
                $(this).change();
            },
            inputClasses: [iClasses]
        });
    };

    var _checkSelectedDropdownValue = function (input){
        var filter;
        if($(input).val() !== 'Any'){
            filter = $(input).val();
        }
        return filter;
    };

    /**
     * To build financial year drop down.
     * @private
     */
    var _buildFinancialYears = function () {
        var years =  [];
        for(i=4; i >= 0; i--){
            var year =(new Date().getFullYear() - (i+1)) +'/'+ (new Date().getFullYear() - i);
            years.push({text:year, value:year});
        }
        for(i=0; i<=4; i++){
            var year =(new Date().getFullYear() + i) +'/'+ (new Date().getFullYear() + (i+1));
            years.push({text:year, value:year});
        }
        return years;
    };

    var _initFinancialYearDropdown = function(dropdownElement){
        _attachOptions(dropdownElement, _buildFinancialYears());
        _initDropdown(dropdownElement, 'selectDropDown');
    };

 return{

        hideMessageBox : function(){
            _hideMessageBox();
        },

        showMessageBox : function(msg, msgClass){
            _showMessageBox(msg, msgClass);
        },

        toTitleCase : function(str){
            return _toTitleCase(str);
        },

        removeDropKick : function (dropdownId) {
            _removeDropKick(dropdownId);
        },

        getLabel : function(v, items){
            return _getLabel(v, items);
        },

        attachOptions : function(dropdownElement, items){
            _attachOptions(dropdownElement, items);
        },

        initDropdown : function(dropdownElement, iClasses){
            _initDropdown(dropdownElement, iClasses);
        },

        initDropdownWithOptions : function(dropdownElement, items, iClasses){
            _initDropdownWithOptions(dropdownElement, items, iClasses);
        },

        checkSelectedDropdownValue : function (input) {
            return _checkSelectedDropdownValue(input);
        },

        initFinancialYearDropdown : function (dropdownElement) {
            _initFinancialYearDropdown(dropdownElement);
        },


    };
})(jQuery,window, document, moment);