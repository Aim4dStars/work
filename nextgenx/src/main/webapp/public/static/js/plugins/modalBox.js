/**
 * ModalBox Jquery function
 * @param Object
 */
(function($) {
    $.fn.modalBox = function(method) {
        var methods = {

            // this the constructor method that gets called when the object is created
            init : function(options) {

                this.modalBox.settings = $.extend({}, this.modalBox.defaults, options);
                var self = this;
                // iterate through all the DOM elements we are attaching the plugin to
                return this.each(function() {
                    helpers.init.apply(this,[self.modalBox]);
                });

            },

            // public methods
            open: function() {
                //create overlay
                var overlayId       = 'overlay-'+new Date().getTime(),
                    overlayStyle    = 'width:'+$(document).width()+'px'+';height:'+$(document).height()+'px',
                    overlayClazz    = this.modalBox.settings.overlayClass+' jq-modalBoxOverlay';

                $('<div>').attr({
                    style:overlayStyle,
                    id:overlayId
                }).appendTo('body').addClass(overlayClazz).fadeIn();

                $(this).fadeIn();

                $('html, body').animate({ scrollTop: 0 }, 'slow');

                $(window).resize(function() {
                    $('#'+overlayId).css({width:$(document).width()+'px',height:$(document).height()+'px'});
                });

            },
            close:function(){
                helpers.close.apply(this);
            }

        };

        // private methods
        var helpers = {

            // a private method. for demonstration purposes only - remove it!
            init: function(plugin) {

                var $element        = $(this),
                    self            = this,
                    settings        = plugin.settings,
                    title           = (settings.title !== null)? settings.title:$element.attr('title'),
                    modalStyles     = {'position' : 'absolute', 'top' : '0', 'left':'0', 'bottom':'0', 'right':'0',
                        'margin':'auto','zIndex':'1001'},
                    titleContent    = [
                        '<div class="modalTitleBar">',
                        '<h2 class="modalTitle">',
                        title,
                        '</h2>',
                        '<a href="#" class="modalClose jq-modalClose" title="Close">',
                        '<span>Close</span>',
                        '</a>',
                        '</div>'
                    ];
                if(settings.height){
                    modalStyles = $.extend(modalStyles,{'height':settings.height})
                }if(settings.width){
                    modalStyles = $.extend(modalStyles,{'width':settings.width})
                }if(!settings.autoOpen){
                    modalStyles = $.extend(modalStyles,{'display':'none'})
                }
                $element.prepend(titleContent.join('')).addClass(settings.modalClass).css(modalStyles);

                $element.find('.jq-modalClose').click(function(){
                    helpers.close.apply(self);
                });
            },
            close: function(){
                $('.jq-modalBoxOverlay').fadeOut('normal',function(){
                    $(this).remove();
                });
                $(this).fadeOut();
            }

        };

        // if a method as the given argument exists
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));

            // if an object is given as method OR nothing is given as argument
        } else if (typeof method === 'object' || !method) {

            // call the initialization method
            return methods.init.apply(this, arguments);

            // otherwise
        } else {

            // trigger an error
            $.error( 'Method "' +  method + '" does not exist in pluginName plugin!');

        }

    };

    // plugin's default options
    $.fn.modalBox.defaults = {
        modal:true,
        autoOpen:false,
        width:'630px',
        height:'500px',
        modalClass:'modalBox',
        overlayClass:'modalBG',
        title:null
    };

    $.fn.modalBox.settings = {}

})(jQuery);
