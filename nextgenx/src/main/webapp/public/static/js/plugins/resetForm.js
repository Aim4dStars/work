/**
 * @fileOverview resetForm jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example $('#form').resetForm();
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.resetForm
 */

(function($) {
    /**
     * @memberOf $.resetForm
     * @param {Object} options reset from config
     */
    $.fn.resetForm = function(options) {
        var defaults = {
            errorMessageClasses	:['jq-inputError','formFieldMessageError'],
            infoMessageClass    :'jq-inputInfo',
            errorFieldClass		:'textInputError'
        };
        options = $.extend(defaults, options);

        return this.each(function() {
            var $form 			= $(this),
                broadcastOnReset= (typeof $form.attr('data-broadcast-on-reset') !== 'undefined') ? /true/i.test($form.attr('data-broadcast-on-reset')): false;

            $form.trigger('reset')
                .find('.jq-validField').removeClass('jq-validField textInputSuccess').end()
                .find('.jq-validating').removeClass('jq-validating').end()
                .find('.'+options.errorFieldClass).removeClass(options.errorFieldClass).end()
                .find('em.'+options.errorMessageClasses[0]).remove().end()
                .find('em.'+options.infoMessageClass).remove().end()
                .find('input[data-submit-value]').attr('data-submit-value','NULL').end()
                .find('.iconCheck').addClass('noDisplay').end()
                .find('.jq-FormErrorMessage').addClass('noDisplay');

            $form.find('input[data-placeholder]').each(function(){
                $(this).val($(this).attr('data-placeholder'));
            });
            $form.find('input[data-error-showing]').each(function(){
                $(this).removeAttr('data-error-showing');
            });
            $form.find('input[aria-invalid]').each(function(){
                $(this).attr('aria-invalid','false');
            });
            $form.removeClass('jq-preventSubmit');

            // Label styling
            $('label', $form).removeClass('formLabelError');

            if(broadcastOnReset) {
                org.bt.utils.communicate.publish('/'+$form.attr('name')+'/reset',[]);
            }
        })
    }
})(jQuery);
