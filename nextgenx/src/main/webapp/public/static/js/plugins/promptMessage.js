/**
 * @fileOverview promptMessage jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example $.promptMessage($payeeElement,'custom',['jq-inputError','formFieldMessageError'],'textInputError','');
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.promptMessage
 */

$.extend( {
    /**
     * @memberOf $.promptMessage
     * @param {Object} $element jquery object
     * @param {String} rule validation rule
     * @param {Array} errorMessageClasses error message classes
     * @param {String} errorFieldClass error field classes
     * @param {String} genericErrorMessage generic error message
     */
    promptMessage: function($element,rule,errorMessageClasses,errorFieldClass,genericErrorMessage) {
        var errorAttr   = $element.attr('data-error-showing'),
            exisits     = (errorAttr && errorAttr === 'yes'),
            message     = $element.attr('data-validation-'+rule+'-error'),
            message     = (typeof message === 'undefined') ? genericErrorMessage:message,
            appendAfter = ($element.siblings().not('label,.ui-helper-hidden-accessible,em.jq-inputError').length > 0),
            errorClass  = errorMessageClasses[0],
            errorEl     = null,
            elemId;

        if(exisits){
            if(appendAfter){
                $element.parents(':first').siblings('em.'+errorClass).html(message);
            } else {
                $element.siblings('em.'+errorClass).html(message);
            }
        } else {
            errorEl = [
                '<em role="alert" data-validation-error="" class="'+errorMessageClasses.join(' ')+'">',
                message,
                '</em>'
            ];
            if(appendAfter){
                setTimeout(function(){
                    $element.parents(':first').after(errorEl.join(''))
                },0);
            } else {
                setTimeout(function(){$element.after(errorEl.join(''))},0);
            }
        }
        $element.attr('data-error-showing','yes'); //add flag

        $element.addClass(errorFieldClass).removeClass('jq-validField textInputSuccess').attr('aria-invalid','true'); //set ARIA tag

        //for dropKick
        if($element.is('select')){
            $element.siblings('.jq-dropkickWrap').find('input').addClass(errorFieldClass);
        }

        // Label styling
        elemId = $element.attr('id');
        $('label[for="'+ elemId +'"]').addClass('formLabelError');
    }
});
