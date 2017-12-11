/**
 * @fileOverview passwordPolicy jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
  $('.jq-registerUsername').usernamePolicy({'userNameElement':'#username','hintElement':'.jq-usernamePolicyHintsContainer'});
 
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
    $.fn.usernamePolicy = function(method) {

        var methods = {
                /**
                 * @memberOf $.fn.passwordPolicy
                 * @desc Init method for plugin
                 * @param {Object} options plugin configuration
                 * @returns {Object} jQuery object
                 * @public
                 */
                init : function(options) {
                    this.usernamePolicy.settings = $.extend({}, this.usernamePolicy.defaults, options);
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
                        plugin      = this.usernamePolicy,
                        settings    = $element.data('usernamePolicy');

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
                        plugin      = this.usernamePolicy,
                        settings    = plugin.settings;

                    $element.data('usernamePolicy',settings);//store settings                   
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
                       $element.removeClass('jq-validField');
                       $element.addClass('textInputError'); 
                        $('.createusername .validation-container .icon-notification-success').addClass('noDisplay');                       
                        org.bt.utils.log.info(errors.length+' errors found!');
                    } else {
                        $element.removeClass('jq-inputError');
                        $element.siblings('.iconCheck').removeClass('noDisplay');
                        $('.createusername .validation-container .icon-notification-success').removeClass('noDisplay');
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


                    //alphabetic character
                    if(settings.alphabeticCharacter){
                        if(/[\w]/g.test(value)){
                            errors.push('error.alphabeticCharacter');
                        }
                    }

                    //nonAlphabetic character
                    if(settings.nonAlphabeticCharacter){
                        if(/[^\w]/g.test(value)){
                            errors.push('error.nonAlphabeticCharacter');
                        }
                    }

                    //non alphanumeric character
                    if(settings.nonAlphaNumericCharacter) {
                        if (!/^(((?=[^\s]*?[0-9])(?=[^\s]*?[a-zA-Z*^$#@.!]))|(?=[^\s]*?[a-zA-Z]))[a-zA-Z0-9*^$#@.!]+$/.test(value)) {
                            if (!settings.emailRegex.test(value)) {
                                errors.push('error.nonAlphaNumericCharacter');
                            }
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
                        if($.inArray('error.minLength',errors) === -1 && $.inArray('error.nonAlphaNumericCharacter',errors) === -1){
                            $hintElement.find('li.jq-minLength em').addClass(validHintClass).removeClass(invalidHintClass);
                            $hintElement.find('li.jq-minLength').addClass(Blur).removeClass(BlurOut);
                        }
                    }
                }
            };

        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error( 'Method "' +  method + '" does not exist in usernamePolicy plugin!');
        }

    };

    /**
     * Default settings for plugin
     * @type {{userNameElement: null, hintElement: null, validHintClass: string, minLength: number, maxLength: number, validChars: RegExp, spaces: boolean, consecutiveCharacters: boolean, userNameInString: boolean, nonAlphabeticCharacter: boolean, alphabeticCharacter: boolean}}
     */
    $.fn.usernamePolicy.defaults = {
        userNameElement:null,
        hintElement:null,
        invalidHintClass:'iconbullet',
        validHintClass:'icon-notification-success',
        Blur:'deemphasis',
        BlurOut:'color-secondary',
        minLength:8,
        maxLength:50,
        validChars:/^[a-zA-Z_0-9@\.\-\!#\$\^%&*]+$/, //ASCII printable chars
		emailRegex:/^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\.([a-zA-Z])+([a-zA-Z])+/,
        spaces:true,
        consecutiveCharacters:false,
        consecutiveNumbers:false,
        nonAlphabeticCharacter :false,
        alphabeticCharacter :false,
        nonAlphaNumericCharacter :true
    };

    $.fn.usernamePolicy.settings = {}

})(jQuery);
