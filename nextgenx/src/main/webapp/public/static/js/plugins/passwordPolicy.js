/**
 * @fileOverview passwordPolicy jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
 *  $('.jq-registerPassword').passwordPolicy({'userNameElement':'#username','hintElement':'#passwordPolicyHintsContainer'});
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
 * @namespace $.fn.passwordPolicy
 */
(function($) {
    $.fn.passwordPolicy = function(method) {

        var methods = {
                /**
                 * @memberOf $.fn.passwordPolicy
                 * @desc Init method for plugin
                 * @param {Object} options plugin configuration
                 * @returns {Object} jQuery object
                 * @public
                 */
                init : function(options) {
                    this.passwordPolicy.settings = $.extend({}, this.passwordPolicy.defaults, options);
                    var self    = this;
                    return this.each(function() {
                        helpers.init.apply(self);
                    });

                },
                /**
                 * @memberOf $.fn.passwordPolicy
                 * @desc Validate method
                 * @returns {Array} array of errors
                 * @public
                 */
                validate: function() {
                    var $element    = $(this),
                        plugin      = this.passwordPolicy,
                        settings    = $element.data('passwordPolicy');

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
                        plugin      = this.passwordPolicy,
                        settings    = plugin.settings;

                    $element.data('passwordPolicy',settings);//store settings

                    $element.bind({
                        keyup: function() {
                            helpers.onKeyUp.apply(this,[$element,plugin,settings]);
                        },
                        focusout: function() {
                            helpers.onFocusOut.apply(this,[$element,plugin,settings]);
                        },
                        focus: function(){
                            helpers.onFocus.apply(this,[$element,plugin,settings]);
                        }
                    });

                },
                /**
                 * Fired when on key up
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                onKeyUp: function($element,plugin,settings){
                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.animate.apply(this,[$element,plugin,settings]); //animate fields
                },
                /**
                 * Fired when on focus out
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                onFocusOut: function($element,plugin,settings){
                    errors = []; //reset array
                    helpers.validate.apply(this,[$element,plugin,settings]);
                    helpers.displayErrors.apply(this,[$element,plugin,settings]);
                    if(settings.hintElement !== null){
                        var $hintElement = $(settings.hintElement);
                        $hintElement.fadeOut();
                    }
                },
                /**
                 * Fired when on focus
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                onFocus: function($element,plugin,settings){
                    helpers.displayHintElement.apply(this,[$element,plugin,settings]);
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
                        $('.createpassword .validation-container .icon-notification-success').addClass('noDisplay');
                        org.bt.utils.log.info(errors.length+' errors found!');
                    } else {
                        $element.removeClass('jq-inputError');
                        $element.siblings('.iconCheck').removeClass('noDisplay');
                        $('.createpassword .validation-container .icon-notification-success').removeClass('noDisplay');
                    }
                },
                /**
                 * Validate helper
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                validate: function($element,plugin,settings){
                    var value           = $element.val(),
                        length          = value.length,
                        regex           = null,
                        username        = null;

                    //check minLength
                    if(length < settings.minLength){
                        errors.push('error.minLength');
                    }

                    //check max length
                    if(length > settings.maxLength){
                        errors.push('error.maxLength');
                    }

                    //check valid chars
                    if(!settings.validChars.test(value)){
                        errors.push('error.validChars');
                    }

                    //has spaces
                    if(settings.spaces){
                        if(/\s/g.test(value)){
                            errors.push('error.spaces');
                        }
                    }

                    //consecutive numbers
                    if(settings.consecutiveNumbers){
                        if(/(^|(.)(?!\2))(\d)\3{3}(?!\3)/.test(value)){
                            errors.push('error.consecutiveNumbers');
                        }
                    }

                    //consecutive characters
                    if(settings.consecutiveCharacters){
                        if(/(.)\1{3,}/g.test(value)){
                            errors.push('error.consecutiveCharacters');
                        }
                    }

                    //user name in string
                    if(settings.userNameInString && settings.userNameElement !== null){
                        username = $.trim($(settings.userNameElement).val());
                        regex= new RegExp(username, "i");
                        if(username != '' && regex.test(value)){
                            errors.push('error.userNameInString');
                        }
                    }

                    //non alphabetic character
                    if(!/^(?=.*[^a-zA-Z])/.test(value)){
                        errors.push('error.nonAlphabeticCharacter');
                    }

                    //alphabetic character
                    if(!/^(?=.*[a-zA-Z])/.test(value)){
                        errors.push('error.alphabeticCharacter');
                    }

                    //numbers
                    if(!/^(?=.*[0-9])/.test(value)) {
                        errors.push('error.number')
                    }

                    //alphabets in caps
                    if (settings.validChars.toString().indexOf('a-z') === -1) {
                        if(!/^[A-Z][^a-z]*$/.test(value) || value.length === 0) {
                            errors.push('error.alphabetsInCaps')
                        }
                    }

                },
                /**
                 * Animate helper
                 * @param $element
                 * @param plugin
                 * @param settings
                 */
                animate:function($element,plugin,settings){
                    if(settings.hintElement !== null){
                        var $hintElement    = $(settings.hintElement),
                            validHintClass  = settings.validHintClass,
                            invalidHintClass= settings.invalidHintClass;
                        	Blur= settings.Blur;
                        	BlurOut= settings.BlurOut;

                        $hintElement.find('li em').removeClass(validHintClass).addClass(invalidHintClass);
                        $hintElement.find('li').removeClass(Blur).addClass(BlurOut);
                        
                        if($.inArray('error.alphabeticCharacter',errors) === -1){
                            $hintElement.find('li.jq-alphabeticCharacter em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-alphabeticCharacter').addClass(Blur).removeClass(BlurOut);
                        }
                        if($.inArray('error.nonAlphabeticCharacter',errors) === -1){
                            $hintElement.find('li.jq-nonAlphabeticCharacter em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-nonAlphabeticCharacter').addClass(Blur).removeClass(BlurOut);
                        }
                        if($.inArray('error.minLength',errors) === -1){
                            $hintElement.find('li.jq-minLength em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-minLength').addClass(Blur).removeClass(BlurOut);
                        }
                        if ($.inArray('error.number',errors) === -1){
                            $hintElement.find('li.jq-number em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-number').addClass(Blur).removeClass(BlurOut);
                        }
                        if ($.inArray('error.alphabetsInCaps',errors) === -1){
                            $hintElement.find('li.jq-upperCase em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-upperCase').addClass(Blur).removeClass(BlurOut);
                        }

                       
                    }
                }
            };

        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error( 'Method "' +  method + '" does not exist in passwordPolicy plugin!');
        }

    };

    /**
     * Default settings for plugin
     * @type {{userNameElement: null, hintElement: null, validHintClass: string, minLength: number, maxLength: number, validChars: RegExp, spaces: boolean, consecutiveCharacters: boolean, userNameInString: boolean, nonAlphabeticCharacter: boolean, alphabeticCharacter: boolean}}
     */
    $.fn.passwordPolicy.defaults = {
        userNameElement:null,
        hintElement:null,
        invalidHintClass:'iconbullet',
        validHintClass:'icon-notification-success',
        Blur:'deemphasis',
        BlurOut:'color-secondary',
        minLength:null,
        maxLength:null,
        validChars:null, //ASCII printable chars
        spaces:true,
        consecutiveCharacters:true,
        consecutiveNumbers:true,
        userNameInString:true,
        nonAlphabeticCharacter :true,
        alphabeticCharacter :true
    };

    $.fn.passwordPolicy.settings = {}

})(jQuery);
