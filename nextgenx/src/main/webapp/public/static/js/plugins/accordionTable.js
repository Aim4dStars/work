/**
 * @fileOverview accordionTable jQuery plugin
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
 $('#my-table').accordionTable({
	onRowClick:callback
});
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
 * @namespace $.fn.accordionTable
 */
(function($) {
    $.fn.accordionTable = function(method) {
        var methods = {
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Init method for plugin
                 * @param {options} options config for accordion table
                 * @returns jQuery object
                 * @public
                 */
                init : function(options) {
                    this.accordionTable.settings = $.extend({}, this.accordionTable.defaults, options);
                    var self    = this;
                    return this.each(function() {
                        helpers.init.apply(self);
                    });
                },
                destroy : function (){
                    var self    = this;
                    return this.each(function() {
                        helpers.destroy.apply(self);
                    });
                },
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Toggle inner sliders method
                 * @param {element} $slider slider element
                 * @public
                 */
                toggleInnerSliders:function($slider){
                    var $element    = $(this),
                        plugin      = this.accordionTable,
                        settings    = $element.data('accordionTable');

                    helpers.toggleInnerSliders.apply(null,[$slider]);
                },
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Close all method
                 * @public
                 */
                closeAll:function(){
                    var $element    = $(this),
                        plugin      = this.accordionTable,
                        settings    = $element.data('accordionTable');

                    helpers.hideRows.apply(null,[$element])
                },
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Close method
                 * @param {function} cb callback function
                 * @public
                 */
                close:function(cb){
                    var $element    = $(this),
                        plugin      = this.accordionTable,
                        settings    = $element.data('accordionTable'),
                        callback    = cb || function(){};

                    helpers.toggle.apply(null,[$element,plugin,settings,false,callback]);
                }
            },
            keyMap = {
                'left'      : 37,
                'up'        : 38,
                'right'     : 39,
                'down'      : 40,
                'enter'     : 13,
                'tab'       : 9,
                'space'     : 32,
                'end'       : 35,
                'home'      : 36,
                'pageUp'    : 33,
                'pageDown'  : 34
            },
            helpers = {

                destroy: function () {
                    var $table      = $(this),
                        plugin      = this.accordionTable,
                        settings    = plugin.settings,
                        $infoRows   = $table.find('tbody tr').not('.jq-accordion,.jq-skip'),
                        $closeBtn   = $table.find('.jq-accordionCloseButton');

                    $table.data('accordionTable',undefined);//remove settings

                    $table.find('tr').each(function(i){
                        var $tr = $(this);
                        $tr.removeAttr('id');
                    });

                    $infoRows.removeAttr('aria-controls');

                    $table.find('tr.jq-accordion').each(function(){
                        var $tr = $(this);

                        $tr.removeAttr('aria-hidden');
                        $tr.removeAttr('aria-expanded');
                        $tr.removeAttr('aria-labeledby');

                    });

                    $infoRows.unbind('click');
                    $infoRows.unbind('focus');
                    $closeBtn.unbind('click');


                },
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Init helper function
                 * @private
                 */
                init: function() {
                    var $table      = $(this),
                        plugin      = this.accordionTable,
                        settings    = plugin.settings,
                        $infoRows   = $table.find('tbody tr').not('.jq-accordion,.jq-skip'),
                        $closeBtn   = $table.find('.jq-accordionCloseButton');

                    $table.data('accordionTable',settings);//store settings

                    //add reference tags
                    helpers.addReferenceTags.apply(this,[$table,plugin,settings,$infoRows]);

                    //bind events
					//$('.jq-skip').on('click',function(event){
					//	console.log("I am jq-skip");
					//	console.log(this);
                    //});
                    $infoRows.on('click',function(event){
						if(!$(event.target).hasClass('jq-skip'))
                        helpers.onRowClick.apply(this,[$table,plugin,settings,event]);
                    });
					//$infoRows.find('input.jq-skip').off('click');

                    $infoRows.on('focus',function(event){
                        helpers.onRowFocus.apply(this,[$table,plugin,settings,event]);
                    });

                    $infoRows.on('keydown',function(event){
                        //helpers.handleKeyBoardEvents.apply(this,[$table,plugin,settings,event]);
                    });

                    $closeBtn.on('click',function(event){
                        helpers.onCloseButtonClick.apply(this,[$table,plugin,settings,event]);
                    });

                    //hide rows
                    helpers.hideRows.apply(this,[$table]);
                },
                /**
                 * @memberOf $.fn.accordionTable
                 * @desc Add reference tags private method
                 * @param {object} $table table element
                 * @param {object} plugin plugin object
                 * @param {object} settings settings
                 * @param {object} $infoRows table rows
                 * @private
                 */
                addReferenceTags: function($table,plugin,settings,$infoRows){
                    //add ids
                    var uniqueId    = 'accordionTable-'+new Date().getTime();
                    $table.find('tr').each(function(i){
                        $(this).attr({'id':uniqueId+'-'+i});
                    });
                    //add aria tags
                    $infoRows.each(function(i){
                        var $tr = $(this),
                            id  = parseInt($tr.attr('id').split('-')[2],10),
                            ref = id + 1;
                        $tr.attr({'role':'tab','aria-controls':uniqueId+'-'+ref})
                    });

                    $table.find('tr.jq-accordion').each(function(){
                        var $tr         = $(this),
                            isActive    = $tr.hasClass('jq-active'),
                            expanded    = isActive ? 'true' : 'false',
                            hidden      = isActive ? 'false': 'true',
                            ref         = parseInt($tr.attr('id').split('-')[2],10) - 1;
                        $tr.attr({'aria-hidden':hidden,'aria-expanded':expanded,'role':'tabpanel','aria-labeledby':uniqueId+'-'+ref})
                    });
                },
                /**
                 * Fired on row click
                 * @param $table
                 * @param plugin
                 * @param settings
                 * @param event
                 */
                onRowClick: function($table,plugin,settings,event){
                    settings.onRowClick.apply(this);

                    helpers.toggle.apply(this,[$table,plugin,settings,event]);
                },
                /**
                 * Fired on close button click
                 * @param $table
                 * @param plugin
                 * @param settings
                 * @param event
                 */
                onCloseButtonClick: function($table,plugin,settings,event){
                    helpers.toggle($table,plugin,settings,event);
                },
                /**
                 * Hide rows helper
                 * @param $table
                 */
                hideRows:function($table){
                    $table.find('.jq-accordion,.jq-accordionContainerDeprecated').not('.jq-active').hide().removeClass('noDisplay');    //jq-accordionContainerDeprecated was jq-accordionContainer
                    var $icon = $table.find('.jq-tablePointer').filter('.iconClosePanel');
                    helpers.toggleIcon($icon,true);
                },
                /**
                 * Toggle icon helper
                 * @param $icon
                 * @param close
                 */
                toggleIcon: function($icon,close){
                    if(close){
                        $icon.removeClass('iconarrowfulldown');
                        $icon.addClass('iconarrowfullright');
                        $icon.attr('title','Open this panel').html('<span>Open this panel</span>');
                    } else {
                        $icon.addClass('iconarrowfulldown');
                        $icon.removeClass('iconarrowfullright');
                        $icon.attr('title','Close this panel').html('<span>Close this panel</span>');
                    }
                },
                /**
                 * Toggle helper
                 * @param $table
                 * @param plugin
                 * @param settings
                 * @param event
                 * @param callback
                 */
                toggle: function($table,plugin,settings,event,callback){
                    if(event){
                        event.preventDefault();
                    }

                    var $row                = (this !== null) ? $(this) : $table.find('.jq-active').prev(),
                        $accordionPanel     = $row.next('tr.jq-accordion'),
                        $siblings           = $table.find('.jq-active'),
                        $arrowIcon          = null;

                    //slide up rows
                    $siblings.find('.jq-accordionContainer').slideUp('500','swing',function(){
                        $siblings.hide().removeClass('jq-active').attr({'aria-hidden':'true','aria-expanded':'false'});
                        $siblings.prev().removeClass('dataTableRowActive'); //remove active class
                        $siblings.prev().prev().prev().removeClass('dataTableRowClearBorder');
                        if(callback){
                            callback();
                        }
                    });
                    //switch icon class
                    $arrowIcon = $siblings.prev('tr').find('.jq-tablePointer');
                    if($arrowIcon.length !== 0){
                        helpers.toggleIcon($arrowIcon,true);
                    }

                    var $container              = $accordionPanel.find('.jq-accordionContainer'),
                        $innerSiblings          = $accordionPanel.find('.jq-innerSlider').not('.jq-defaultInnerSlider');

                    if($container.is(':visible')){
                        $container.slideUp('500','swing',function(){
                            $accordionPanel.hide().removeClass('jq-active').attr({'aria-hidden':'true','aria-expanded':'false'});
                        });
                        //switch icon class
                        $arrowIcon = $accordionPanel.prev('tr').find('.jq-tablePointer');
                        helpers.toggleIcon($arrowIcon,true);
                    } else {
                        $accordionPanel.show().addClass('jq-active').attr({'aria-hidden':'false','aria-expanded':'true'});
                        $container.hide();
                        // show default panel
                        $innerSiblings.hide();
                        $accordionPanel.find('.jq-defaultInnerSlider').show();
                        $container.slideDown('500','swing',function(){
                        });
                        //switch icon class
                        $arrowIcon = $accordionPanel.prev('tr').find('.jq-tablePointer');
                        helpers.toggleIcon($arrowIcon,false);
                    }
                },
                /**
                 * Toggle inner sliders helper
                 * @param $selectedSlider
                 */
                toggleInnerSliders: function($selectedSlider){
                    var sibling         = $selectedSlider.siblings('.jq-innerSlider').filter(':visible'); //filter current visible slider

                    if(!$selectedSlider.is(':visible')){
                        sibling.hide('slide',{ direction: 'up' },500,function(){
                            $selectedSlider.show('slide', { direction: 'up' }, 500,function(){
								$('.jq-confirmStopScheduleButton').focus();
                            });
                        });

                    }
                },
                /**
                 * On Row Focus helper
                 * @param $table
                 * @param plugin
                 * @param settings
                 * @param event
                 */
                onRowFocus: function($table,plugin,settings,event){
                    var $row                = $(this);
                    $table.find('tbody > tr').removeClass('jq-focus');
                    $row.addClass('jq-focus');
                },
                /**
                 * Handle key board events helper
                 * @param $table
                 * @param plugin
                 * @param settings
                 * @param event
                 */
                handleKeyBoardEvents: function($table,plugin,settings,event){
                    var code     = event.which,
                        current  = $table.find('.jq-focus'),
                        next, prev;

                    switch (code) {
                        case keyMap.space:
                        case keyMap.enter:
                            helpers.onRowClick.apply(current[0],[$table,plugin,settings,event]);
                            break;
                        case keyMap.down:
                        case keyMap.right:
                            next = current.next().next();
                            next = (next.length > 0) ? next : $table.find('tbody tr').not('.jq-accordion').first();
                            next.focus();
                            break;
                        case keyMap.up:
                        case keyMap.left:
                            prev = current.prev().prev();
                            prev = (prev.length > 0) ? prev : $table.find('tbody tr').not('.jq-accordion').last();
                            prev.focus();
                            break;
                        case keyMap.home:
                            next = $table.find('tbody tr').not('.jq-accordion').first();
                            next.focus();
                            break;
                        case keyMap.end:
                            next = $table.find('tbody tr').not('.jq-accordion').last();
                            next.focus();
                            break;
                    }
                }
            };
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error( 'Method "' +  method + '" does not exist in accordionTable plugin!');
        }
    };
    /**
     * Default settings
     * @type {{onRowClick: Function}}
     */
    $.fn.accordionTable.defaults = {
        onRowClick:function(el){}
    };
    $.fn.accordionTable.settings = {};
})(jQuery);

