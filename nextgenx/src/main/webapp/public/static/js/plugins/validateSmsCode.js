/**
 * @fileOverview smsCode jQuery function
 * @version 1.00
 * @author Krishan Rodrigo
 * @requires jquery
 * @example
 *  $('.jq-getSMSCodeButton').validateSmsCode({'smsCodeInput':'.jq-smsCodeInput'});
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
 * @namespace $.fn.validateSmsCode
 */
(function($) {
    $.fn.validateSmsCode = function(method) {

        var methods = {
                /**
                 * @memberOf $.fn.validateSmsCode
                 * @desc Init method for plugin
                 * @param {Object} options plugin configuration
                 * @returns {Object} jQuery object
                 * @public
                 */
                init : function(options) {
                    this.validateSmsCode.settings = $.extend({}, this.validateSmsCode.defaults, options);
                    var self    = this;
                    return this.each(function() {
                        helpers.init.apply(self);
                    });

                },
                /**
                 * @memberOf $.fn.validateSmsCode
                 * @desc Validate method
                 * @returns {Array} array of errors
                 * @public
                 */
                validate: function() {
                    var $element    = $(this),
                        plugin      = this.validateSmsCode,
                        settings    = $element.data('validateSmsCode');

                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    return errors;
                },
                /**
                 * @memberOf $.fn.validateSmsCode
                 * @desc Enable input field method
                 * @returns {Array} array of errors
                 * @public
                 */
                enableInput: function() {
                    var $element    = plugin.settings.smsCodeInput //$(this),
                        plugin      = this.smsCode,
                        settings    = $element.data('validateSmsCode');

                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    return errors;
                },
                /**
                 * @memberOf $.fn.validateSmsCode
                 * @desc Disable input field method
                 * @returns {Array} array of errors
                 * @public
                 */
                disableInput: function() {
                    var $element    = plugin.settings.smsCodeInput //$(this),
                        plugin      = this.smsCode,
                        settings    = $element.data('validateSmsCode');

                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    return errors;
                }
            },
            errors = [],
            helpers = {
                /**
                 * Init helper
                 */
                init: function() {
                    var $element    = $(this),
                        plugin      = this.validateSmsCode,
                        settings    = plugin.settings,
                        $codeInput = settings.smsCodeField;

                    $element.data('validateSmsCode',settings);//store settings

                    $element.bind({
                        click: function() {
                            helpers.click.apply(this,[$element,plugin,settings]);
                        }
                    });

                    $codeInput.bind({
                        focusout: function() {
                            helpers.onFocusOut.apply(this,[$element,plugin,settings]);
                        }
                    });

                },
                /**
                 * Fired on button click
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                click: function($element,plugin,settings){
                    $smsCodeInput = $(settings.smsCodeInput);

                    $smsCodeInput.addClass('textInputDisabled');
                    $element.find('.actionButtonIconText').html('Try again');
                    $element.find('em').removeClass('icon-refresh').addClass('iconmobile');
                    $element.removeClass('actionButtonIconMod3');

                    /*errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    if(settings.hintElement !== null){
                        var $hintElement = $(settings.hintElement);
                        $hintElement.fadeOut();
                    } */
                },
                /**
                 * Fired when on focus out
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                onFocusOut: function($element,plugin,settings){
                    $smsCodeInput = $(settings.smsCodeInput);

                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    if(settings.hintElement !== null){
                        var $hintElement = $(settings.hintElement);
                        $hintElement.fadeOut();
                    }
                },
                /**
                 * Display hint element helper
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                displayHintElement: function($element,plugin,settings){
                    if(settings.hintElement !== null){
                        var $hintElement = $(settings.hintElement);
                        $hintElement.removeClass('noDisplay');
                        $hintElement.fadeIn();
                    }
                },
                /**
                 * Display errors helper
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                displayErrors: function($element,plugin,settings){
                    if(errors.length > 0){
                        //display errors
                        $element.siblings('.iconCheck').addClass('noDisplay');
                        $element.addClass('jq-inputError');
                        org.bt.utils.log.info(errors.length+' errors found!');
                    } else {
                        $element.removeClass('jq-inputError');
                        $element.siblings('.iconCheck').removeClass('noDisplay');
                    }
                },
                /**
                 * Validate helper
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                validate: function($element,plugin,settings){
                    $smsCodeInput = $(settings.smsCodeInput);
                    var value           = $smsCodeInput.val(),
                        length          = value.length;

                    //check minLength
                    if(length < settings.minLength){
                        errors.push('error.minLength');
                    }
                    //
                    else {

                    }

                }
            };

        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error( 'Method "' +  method + '" does not exist in validateSmsCode plugin!');
        }

    };

    /**
     * Default settings for plugin
     * @type {{smsCodeField: null, smsCodeButton: null, hintElement: null, minLength: number, maxLength: number, validChars: RegExp, validationUrl: url}}
     */
    $.fn.validateSmsCode.defaults = {
        smsCodeField:null,
        smsCodeButton:null,
        hintElement:null,
        minLength:6,
        //maxLength:6,
        validChars:/^[\040-\057]*$/, //ASCII printable chars
        validationUrl:org.bt.utils.serviceDirectory.validateSmsCode
    };

    $.fn.passwordPolicy.settings = {}

})(jQuery);