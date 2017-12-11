/**
 * @fileOverview placeholder jQuery plugin
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
 * $('#amount').placeholder();
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>
 * @name fn
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 * @memberOf $
 */
/**
 * @namespace $.fn.placeholder
 */
(function($) {
    /**
     * @memberOf $.fn.placeholder
     * @param {options} options plugin config
     * @public
     * @returns {object} jQuery object
     */
    $.fn.placeholder = function(options) {
        var defaults = {'placeholderClass':'placeholder'};
        options = $.extend(defaults, options);

        return this.each(function() {
            var $element    = $(this);
            //init plugin for only old browsers!
            if (document.createElement('input').placeholder == undefined) {
                $element.focus(function() {
                    var input = $(this);
                    input.removeClass(options.placeholderClass);
                    if (input.val() == input.attr('placeholder')) {
                        input.val('');
                    }
                }).blur(function() {
                      _setPlaceholder();
                    }).bind('setPlaceholder',function(){
                        _setPlaceholder();
                });

                //remove placeholder before submit
                $element.parents('form:first').submit(function() {
                    $(this).find('[placeholder]').each(function() {
                        var input = $(this);
                        if (input.val() == input.attr('placeholder')) {
                            input.val('');
                        }
                    })
                });

                function _setPlaceholder(){
                    if ($element.val() == '' || $element.val() == $element.attr('placeholder')) {
                        $element.addClass(options.placeholderClass);
                        $element.val($element.attr('placeholder'));
                    }
                }
                _setPlaceholder();
            }
        })
    }
})(jQuery);
