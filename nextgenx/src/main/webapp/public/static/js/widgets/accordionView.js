/**
 * @fileOverview accordionView jQuery UI plugin
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @requires jquery ui
 * @example
 $('#view-accordion').accordionView({
                    fakeElement:'#fake-view',
                    toggleButton:'#toggle-btn'
                });
 <div class="content">
 <a href="#" id="toggle-btn">toggle accordion</a>
 <div id="fake-view" class="noDisplay"></div>
 <p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. </p>
 </div>

 <div id="view-accordion" class="accordion noDisplay">
 <p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.</p>
 </div>
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.ui.accordionView
 */
;(function ( $, window, document ) {

    $.widget( 'ui.accordionView' , {

        /**
         * Options to be used as defaults
         */
        options: {
            fakeElement: null,
            toggleButton:null,
            closeButtonClass:'.jq-accordionViewCloseButton',
            disabledButtonClass:'jq-disabled',
            pointerGuide:null,
            beforeSlideDown:function(){},
            beforeSlideUp:function(){},
            onSlideUp:function(){},
            onSlideDown:function(){},
            onToggle: function(){},
            panelOpenClass:'jq-accordionViewStatusOpen'
        },
        /**
         * @desc accordion view create method
         * @public
         */
        _create: function () {
            var self    = this,
                $element= self.element.removeClass('noDisplay').uniqueId().hide(),
                options = self.options;

            this.isHidden = true;

            this.id = $element.attr('id');
            $element.attr({'role':'tabpanel','data-accordion':true});

            this.pointer = $element.find('.jq-accordionPointer');

            if(options.pointerGuide){
                this.pointerGuide = $(options.pointerGuide);
            }

            this.fakeElement = $(options.fakeElement).removeClass('noDisplay');

            this.fakeElement.hide().attr({'aria-hidden':'true'});

            this.closeButton = self.element.find(options.closeButtonClass).bind('click.accordionView.closeButton',function(){
                self.slideUp();
            });

            if(options.toggleButton !== null){
                this.toggleButton           = $(options.toggleButton);
                this.toggleButtonDisabled   = this.toggleButton.hasClass(options.disabledButtonClass);

                if(this.toggleButtonDisabled) {
                    this.toggleButton.attr({'aria-disabled':'true'});
                } else {
                    this.toggleButton.bind('click.accordionView.toggleButton',function(event){
                        self.toggle();
                    });

                    this.toggleButton.attr({'aria-controls':this.id,'role':'button'}).uniqueId();

                    this.toggleButtonId = this.toggleButton.attr('id');
                    $element.attr({'aria-labelledby':this.toggleButtonId});
                }
            }
        },
        // Destroy an instantiated plugin and clean up
        // modifications the widget has made to the DOM
        destroy: function () {

            // this.element.removeStuff();
            // For UI 1.8, destroy must be invoked from the
            // base widget
            $.Widget.prototype.destroy.call(this);
            // For UI 1.9, define _destroy instead and don't
            // worry about
            // calling the base widget
        },
        /**
         * @desc accordion view toggle method
         * @public
         */
        toggle: function(){
            var self = this;
            if(this.isHidden){
                self.slideDown();
            } else {
                self.slideUp();
            }
        },
        refresh: function(){
            var self = this;
            setTimeout(function(){
                self.height = self.element.show().outerHeight();
                self.fakeElement.css('height',self.height + 'px');
            },170);
        },
        /**
         * @desc accordion view slideDown method
         * @public
         */

        slideDown: function(){
            var self = this,
                pointerLeft;
            if(self.isHidden){
                //set the accordion pointer arrow
                if((self.toggleButton || self.pointerGuide) && self.pointer.length == 1){
                    pointerLeft = (self.pointerGuide) ? self.pointerGuide.offset().left + (self.pointerGuide.outerWidth()/2) +'px' :
                        self.toggleButton.offset().left + (self.toggleButton.width()/2)  +'px';

                    self.pointer.css('left',pointerLeft);
                }
                this.options.beforeSlideDown();

                //have to get the position just before slideDown
                this.top = this.fakeElement.show().offset().top;
                this.element.css('top',this.top +'px');
                this.fakeElement.hide();

                this.height = this.element.show().outerHeight();
                this.fakeElement.css('height',this.height + 'px');
                this.element.hide();

                this.fakeElement.slideDown(400);

                this.element.attr({'aria-expanded':'true','aria-hidden':'false'});
                this.element.slideDown(400,function(){
                    self.isHidden = false;
                    self.options.onSlideDown();
                    self.options.onToggle();
                    self.element.addClass(self.options.panelOpenClass);
                    self.setFocus();
                });
            }
        },
        /**
         * @desc accordion view slideUp method
         */
        slideUp: function(){
            var self = this;
            if(!self.isHidden){
                this.options.beforeSlideUp();
                this.fakeElement.slideUp(400);
                this.element.removeClass(self.options.panelOpenClass);

                this.element.slideUp(400,function(){
                    self.isHidden = true;
                    self.options.onSlideUp();
                    self.options.onToggle();
                    self.element.attr({'aria-expanded':'false','aria-hidden':'true'});
                });
            }
        },
        setFocus: function(){
            var self = this;
            //this.element.focus();
            window.setTimeout(function(){
                self.element.find('.jq-close').focus();
            }, 100);
        },
        hide: function(){
            var self = this;
            if(!self.isHidden){
                this.fakeElement.hide();
                this.element.hide();
                self.isHidden = true;
                self.element.attr({'aria-expanded':'false','aria-hidden':'true'});
            }
        },
        /**
         * @desc Developer can override the default settings using setOption
         * @param key
         * @param value
         * @private
         */
        _setOption: function ( key, value ) {
            this.options[ key ] = value;

            // For UI 1.8, _setOption must be manually invoked
            // from the base widget
            $.Widget.prototype._setOption.apply( this, arguments );
            // For UI 1.9 the _super method can be used instead
            // this._super( "_setOption", key, value );
        }
    });

})( jQuery, window, document );
