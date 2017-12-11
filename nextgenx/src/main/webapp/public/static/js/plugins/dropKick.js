/**
 * DropKick
 *
 * Highly customizable <select> lists
 * https://github.com/JamieLottering/DropKick
 *
 * &copy; 2011 Jamie Lottering <http://github.com/JamieLottering>
 *                        <http://twitter.com/JamieLottering>
 *Important - Before upgrading dropkick,please ensure to copy custom function 'reload'.
 */
(function ($, window, document) {

    var ie6 = false;
    var
    // Public methods exposed to $.fn.dropkick()
        methods = {},

    // Cache every <select> element that gets dropkicked
        lists   = [],

    // Convenience keys for keyboard navigation
        keyMap = {
            'left'  : 37,
            'up'    : 38,
            'right' : 39,
            'down'  : 40,
            'enter' : 13,
            'tab'   : 9,
            'space' :32
        },


    // HTML template for the dropdowns
        dropdownTemplate = [
            '<div data-ng-key="{{ testKey }}-auto" class="selectContainer jq-dropkickWrap {{ containerClasses }}" id="selectContainer_{{ id }}">',
            '<span class="iconWrapper">',
            '<input data-ng-key="input" class="selectToggle dk_input {{ inputClasses }}" value="{{ label }}" readonly="readonly" tabindex="{{ tabindex }}"',
            ' aria-haspopup="true" aria-owns="selectOptionsInner_{{ time }}" role="button" {{ disabledAttribute }} />',
            '<a data-ng-key="arrow" tabindex="-1" class="selectButton jq-buttonSelect {{ anchorClasses }}" href="#">',
            '<em class="iconSelect iconarrowfulldown"></em>',
            '</a>',
            '</span>',
            '<div class="selectOptions" aria-hidden="true">',
            '<ul data-ng-key="options" class="selectOptionsInner dk_options" role="listbox" id="selectOptionsInner_{{ time }}">',
            '</ul>',
            '</div>',
            '</div>'
        ].join(''),

    // HTML template for dropdown options
        optionTemplate = '<li class="{{ current }} resultItem" role="presentation"><a role="option" aria-selected="false" data-dk-dropdown-value="{{ value }}">{{ text }}</a></li>',

    // Some nice default values
        defaults = {
            startSpeed : 1000,  // I recommend a high value here, I feel it makes the changes less noticeable to the user
            theme  : false,
            change : false,
            inputClasses:[]
        },

    // Make sure we only bind keydown on the document once
        keysBound = false
        ;

    // Called by using $('foo').dropkick();
    methods.init = function (settings) {
        settings = $.extend({}, defaults, settings);

        return this.each(function () {
            var
            // The current <select> element
                $select = $(this),

            // Store a reference to the originally selected <option> element
                $original = $select.find(':selected').first(),

            // Save all of the <option> elements
                $options = $select.find('option'),

            // We store lots of great stuff using jQuery data
                data = $select.data('dropkick') || {},

            // This gets applied to the 'selectContainer' element
                id = $select.attr('id') || $select.attr('name'),

                id = $select.attr('data-id') ? id + '_'+ $select.attr('data-id') : id,

            // This gets updated to be equal to the longest <option> element
                width  = settings.width || $select.outerWidth(),

            // Check if we have a tabindex set or not
                tabindex  = $select.attr('tabindex') ? $select.attr('tabindex') : '',

                automatedTestId = $select.attr('data-ng-key') || id,

            // The completed selectContainer element
                $dk = false,

                placeholder = $select.attr('data-placeholder'),

                theme;

            // Dont do anything if we've already setup dropkick on this element
            if (data.id) {
                return $select;
            } else {
                data.settings  = settings;
                data.tabindex  = tabindex;
                data.id        = id;
                data.$original = $original;
                data.$select   = $select;
                data.value     = _notBlank($select.val()) || _notBlank($original.attr('value'));
                data.label     = $original.text();
                data.options   = $options;
                data.testKey   = automatedTestId;
            }
            //remove the placeholder
            if(placeholder) {
                $('.'+placeholder).remove();
            }
            // Build the dropdown HTML
            $dk = _build(dropdownTemplate, data);

            // Make the dropdown fixed width if desired
            /*
             $dk.find('.selectToggle').css({
             'width' : width + 'px'
             });*/

            // Hide the <select> list and place our new one in front of it
            $select.before($dk);

            // Update the reference to $dk
            $dk = $('#selectContainer_' + id).fadeIn(settings.startSpeed);

            // Save the current theme
            theme = settings.theme ? settings.theme : 'default';
            $dk.addClass('dk_theme_' + theme);
            data.theme = theme;

            // Save the updated $dk reference into our data object
            data.$dk = $dk;

            // Save the dropkick data onto the <select> element
            $select.data('dropkick', data);

            // Do the same for the dropdown, but add a few helpers
            $dk.data('dropkick', data);

            lists[lists.length] = $select;

            // Focus events
            $dk.bind('focus.dropkick', function (e) {
                $dk.addClass('selectFocus');
            }).bind('blur.dropkick', function (e) {
                $dk.removeClass('selectOpen selectFocus');
            });

            $dk.find('li').bind('mouseenter.dropkick', function (e) {
                $(this).addClass('hover');
            }).bind('mouseleave.dropkick', function (e) {
                $(this).removeClass('hover');
            });
            setTimeout(function () {
                $select.hide();
            }, 0);

            //on input keypress
            $dk.on('keypress', 'input', function(event){
                if (event.which > 0) {
                    if($dk.hasClass('jq-disabled')){
                        return;
                    }
                    _typeAhead.apply($dk,[event.which, 'focus']);
                }
                return true;
            });

            $(document).click(function(e){
                var target      = $(e.target),
                    $pulldown   = $('.selectContainer');

                if(target.parents().filter('.selectContainer').length === 0){
                    $pulldown.removeClass('selectOpen selectFocus');
                }
            });

        });
    };

    // Allows dynamic theme changes
    methods.theme = function (newTheme) {
        var
            $select   = $(this),
            list      = $select.data('dropkick'),
            $dk       = list.$dk,
            oldtheme  = 'dk_theme_' + list.theme
            ;

        $dk.removeClass(oldtheme).addClass('dk_theme_' + newTheme);

        list.theme = newTheme;
    };

    // Reset all <selects and dropdowns in our lists array
      /*changes added to this  method adds the feature of
      resetting individual dropkick .otherwise all dropdowns
      will get resetted which is not an expected behaviour*/
    methods.reset = function () {
        if (this && this.length !== 0) {
            var listData  = this.data('dropkick');
                if(listData){
                    var $dk  = listData.$dk,
                    $current  = $dk.find('li').first();
                    $dk.find('.dk_input').val(listData.label);
                    $dk.find('.selectOptionsInner').animate({ scrollTop: 0 }, 0);

                    _setCurrent($current, $dk);
                    _updateFields($current, $dk, true);
                }

        }
    };
	
	//Customised dropkick.js.Please ensure to include this function in any of the future upgrade of this plugin.
	methods.reload = function (settings) {
                                settings = $.extend({},defaults, settings);
                                var $select = $(this);
                                $select.removeData("dropkick");
                                settings.container.remove();
                                $select.dropkick(settings);
    };

    //Customised dropkick.js.Please ensure to include this function in any of the future upgrade of this plugin.
    methods.setValue = function (value) {
        var $dk = $(this).data('dropkick').$dk;
        var $option = $dk.find('.dk_options a[data-dk-dropdown-value="' + value + '"]:first');


        _updateFields($option, $dk);
    };

    // Expose the plugin
    $.fn.dropkick = function (method) {
        if (!ie6) {
            if (methods[method]) {
                return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
            } else if (typeof method === 'object' || ! method) {
                return methods.init.apply(this, arguments);
            }
        }
    };

    // private

    function _typeAhead(code, eventType){
        var self = this,
            c = String.fromCharCode(code).toLowerCase(),
            matchee = null,
            nextIndex = null;

        //set changed flag false
        self.attr('data-type-ahead','false');

        // Clear any previous timer if present
        if ( self._typeAhead_timer ) {
            window.clearTimeout( self._typeAhead_timer );
            self._typeAhead_timer = undefined;
        }

        // Store the character typed
        self._typeAhead_chars = (self._typeAhead_chars === undefined ? "" : self._typeAhead_chars).concat(c);

        // Detect if we are in cyciling mode or direct selection mode
        if ( self._typeAhead_chars.length < 2 ||
            (self._typeAhead_chars.substr(-2, 1) === c && self._typeAhead_cycling) ) {
            self._typeAhead_cycling = true;

            // Match only the first character and loop
            matchee = c;
        }
        else {
            // We won't be cycling anymore until the timer expires
            self._typeAhead_cycling = false;

            // Match all the characters typed
            matchee = self._typeAhead_chars;
        }

        // We need to determine the currently active index

        var selectedIndex = (eventType !== 'focus' ?
            self.find('.selectCurrentOption') :
            self.find('.selectCurrentOption')) || self.find('.selectOptionsInner li:first');

        for (var i = 0; i < self.find('li').length; i++) {
            var thisText = self.find('li').eq(i).text().substr(0, matchee.length).toLowerCase();

            if ( thisText === matchee ) {
                if ( self._typeAhead_cycling ) {
                    if ( nextIndex === null )
                        nextIndex = i;

                    if ( i > selectedIndex ) {
                        nextIndex = i;
                        break;
                    }
                } else {
                    nextIndex = i;
                }
            }
        }

        if ( nextIndex !== null ) {
            //set changed flag true
            self.attr('data-type-ahead','true');

            var $selected = self.find('li').eq(nextIndex);

            _updateFields($selected.find('a'), self, true);
            //set style
            self.find('.selectCurrentOption').removeClass('selectCurrentOption').removeClass('resultItemHover');
            $selected.addClass('selectCurrentOption resultItemHover');
        }

        self._typeAhead_timer = window.setTimeout(function() {
            self._typeAhead_timer = undefined;
            self._typeAhead_chars = undefined;
            self._typeAhead_cycling = undefined;
        }, self.data('dropkick').typeAhead);
    }

    function _handleKeyBoardNav(e, $dk) {
        var
            code     = e.which,
            data     = $dk.data('dropkick'),
            options  = $dk.find('.selectOptions'),
            open     = $dk.hasClass('selectOpen'),
            current  = $dk.find('.selectCurrentOption'),
            changed  = ($dk.attr('data-type-ahead') && $dk.attr('data-type-ahead') === 'true'), //user type in the input
            first    = options.find('li').first(),
            last     = options.find('li').last(),
            next,
            prev
            ;

        switch (code) {
            case keyMap.tab:
                if (open) {
                    _updateFields(current.find('a'), $dk);
                    _closeDropdown($dk);
                } else if(changed){
                    _updateFields(current.find('a'), $dk);
                    $dk.removeAttr('data-type-ahead');
                }
                break;
            case keyMap.enter:
                if (open) {
                    _updateFields(current.find('a'), $dk);
                    _closeDropdown($dk);
                } else {
                    _openDropdown($dk);
                }
                e.preventDefault();
                break;

            case keyMap.up:
                prev = current.prev('li');
                if (open) {
                    if (prev.length) {
                        _setCurrent(prev, $dk);
                        _updateFields(prev.find('a'), $dk);
                    } else {
                        _setCurrent(last, $dk);
                        _updateFields(last.find('a'), $dk);
                    }
                } else {
                    _openDropdown($dk);
                }
                e.preventDefault();
                break;

            case keyMap.down:
                if (open) {
                    next = current.next('li').first();
                    if (next.length) {
                        _setCurrent(next, $dk);
                        _updateFields(next.find('a'), $dk);
                    } else {
                        _setCurrent(first, $dk);
                        _updateFields(first.find('a'), $dk);
                    }
                } else {
                    _openDropdown($dk);
                }
                e.preventDefault();
                break;
            case keyMap.space:
                _openDropdown($dk);
                e.preventDefault();
                break;
            default:
                break;
        }
    }


    // Update the <select> value, and the dropdown label
    function _updateFields(option, $dk, reset) {
        var value, label, data;

        value = option.attr('data-dk-dropdown-value');
        label = option.text();
        data  = $dk.data('dropkick');

        $dk.find('.resultItem a').attr('aria-selected','false');
        option.attr('aria-selected','true');

        $select = data.$select;
        $select.val(value);

        $dk.find('.dk_input').val(label);

        reset = reset || false;

        if (data.settings.change && !reset) {
            data.settings.change.call($select, value, label);
        }
    }

    // Set the currently selected option
    function _setCurrent($current, $dk) {
        $dk.find('.selectCurrentOption').removeClass('selectCurrentOption').removeClass('resultItemHover');
        $current.addClass('selectCurrentOption resultItemHover');

        _setScrollPos($dk, $current);
    }

    function _setScrollPos($dk, anchor) {
        var height = anchor.prevAll('li').outerHeight() * anchor.prevAll('li').length;
        $dk.find('.selectOptionsInner').animate({ scrollTop: height + 'px' }, 0);
    }

    // Close a dropdown
    function _closeDropdown($dk) {
        $dk.removeClass('selectOpen');
        $dk.find('.selectOptions').attr('aria-hidden','true');

    }

    // Open a dropdown
    function _openDropdown($dk) {
        if($dk.hasClass('jq-disabled')){
            return;
        }
        var isOpen  = $dk.hasClass('selectOpen');

        //hide all dropdowns
        $('body').find('.selectContainer').removeClass('selectOpen');
        if(isOpen){ //if already open hide it!
            _closeDropdown($dk);
        } else {
            var data = $dk.data('dropkick');
            $dk.find('.selectOptions').css({ top : $dk.find('.selectToggle').outerHeight() - 5 }).attr('aria-hidden','false');
            $dk.toggleClass('selectOpen');
        }
    }

    /**
     * Turn the dropdownTemplate into a jQuery object and fill in the variables.
     */
    function _build (tpl, view) {
        var
        // Template for the dropdown
            template  = tpl,
        // Holder of the dropdowns options
            options   = [],
            disabled  = view.$select.hasClass('jq-disabled'),
            $dk
            ;

        template = template.replace(/{{ id }}/g, view.id);
        template = template.replace(/{{ testKey }}/g, view.testKey);
        template = template.replace(/{{ time }}/g,new Date().getTime());
        template = template.replace('{{ label }}', view.label);
        template = template.replace('{{ tabindex }}', view.tabindex);
        template = template.replace('{{ containerClasses }}', (disabled ? 'jq-disabled' : ''));
        template = template.replace('{{ anchorClasses }}', (disabled ? 'noDisplay' : ''));
        template = template.replace('{{ disabledAttribute }}', (disabled ? 'disabled="disabled"' : ''));
        template = template.replace('{{ inputClasses }}', view.settings.inputClasses.join(' '));

        if (view.options && view.options.length) {
            for (var i = 0, l = view.options.length; i < l; i++) {
                var
                    $option   = $(view.options[i]),
                    current   = 'selectCurrentOption',
                    oTemplate = optionTemplate
                    ;

                oTemplate = oTemplate.replace('{{ value }}', $option.val());
                oTemplate = oTemplate.replace('{{ current }}', (_notBlank($option.val()) === view.value) ? current : '');
                oTemplate = oTemplate.replace('{{ text }}', $option.text());

                options[options.length] = oTemplate;
            }
        }

        $dk = $(template);
        $dk.find('.selectOptionsInner').html(options.join(''));

        return $dk;
    }

    function _notBlank(text) {
        return ($.trim(text).length > 0) ? text : false;
    }

    $(function () {

        // Handle click events on the dropdown toggler
        $('.selectToggle, .jq-buttonSelect').live('click', function (e) {
            var $dk  = $(this).parents('.selectContainer').first();

            _openDropdown($dk);

            /*if ("ontouchstart" in window) {
                $dk.addClass('selectTouch');
                $dk.find('.selectOptionsInner').addClass('scrollable vertical');
            }*/

            e.preventDefault();
            //return false;
        });


        // Handle click events on individual dropdown options
        $('.selectOptions a').live(($.browser.msie ? 'mousedown' : 'click'), function (e) {
            var
                $option = $(this),
                $dk     = $option.parents('.selectContainer').first(),
                data    = $dk.data('dropkick')
                ;

            _closeDropdown($dk);
            _updateFields($option, $dk);
            _setCurrent($option.parent(), $dk);

            e.preventDefault();
            //return false;
        });

        // Setup keyboard nav
        $(document).bind('keydown.dk_nav', function (e) {
            var
            // Look for an open dropdown...
                $open    = $('.selectContainer.selectOpen'),

            // Look for a focused dropdown
                $focused = $('.selectContainer.selectFocus'),

            //Look for a focused input
            $inputFocus = $('.dk_input:focus'),

            // Will be either $open, $focused, or null
                $dk = null
                ;

            // If we have an open dropdown, key events should get sent to that one
            if ($open.length) {
                $dk = $open;
            } else if ($focused.length && !$open.length) {
                // But if we have no open dropdowns, use the focused dropdown instead
                $dk = $focused;
            } else if($inputFocus.length && !$open.length) {
                //input is focus
                $dk = $inputFocus.parents('.selectContainer');
            }

            if ($dk) {
                _handleKeyBoardNav(e, $dk);
            }
        });
    });
})(jQuery, window, document);
