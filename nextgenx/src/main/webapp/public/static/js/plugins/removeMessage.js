/**
 * @fileOverview removeMessage jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example $.removeMessage($input,['jq-inputError','formFieldMessageError'],'textInputError');
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.removeMessage
 */
$.extend( {
    /**
     * @memberOf $.removeMessage
     * @param {Object} $element jquery object
     * @param {Array} errorMessageClasses error message classes
     * @param {String} errorFieldClass error field classes
     */
    removeMessage: function($element,errorMessageClasses,errorFieldClass) {
        var $errorMessage       = false,
            appendAfter         = ($element.siblings().not('label,.ui-helper-hidden-accessible,em.jq-inputError').length > 0),
            errorMessageClass   = errorMessageClasses[0],
            highlightField      = (typeof $element.attr('data-validation-highlight-on-success') !== "undefined")
                ? /true/i.test($element.attr('data-validation-highlight-on-success')): false,
            elemId;

        if(appendAfter){
            $errorMessage = $element.parents(':first').siblings('em.'+errorMessageClass);
        } else{
            $errorMessage = $element.siblings('em.'+errorMessageClass);
        }

        if($errorMessage && $errorMessage.length > 0){
            $errorMessage.remove();
        }
        $element.removeClass(errorFieldClass).addClass('jq-validField');

        $element.removeAttr('data-error-showing');
        $element.attr('aria-invalid','false');
        //highlight field
        if(highlightField){
           // $element.addClass('textInputSuccess');
        }

        //for dropKick
        if($element.is('select')){
            $element.siblings('.jq-dropkickWrap').find('input').removeClass(errorFieldClass);
        }
        // Label styling
        elemId = $element.attr('id');
        $('label[for="'+ elemId +'"]').removeClass('formLabelError');
    }
});
