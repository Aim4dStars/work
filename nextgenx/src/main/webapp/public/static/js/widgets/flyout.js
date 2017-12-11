
/**
 * @namespace $.ui.flyout
 */
;(function ( $, window, document ) {

    $.widget( 'ui.flyout' , {

        /**
         * Options to be used as defaults
         */
        options: {
            flyoutWrapper: null,
            pointer : null,
            adjustLeft:true,
            onToggle: function(){}
        },
        /**
         * @desc accordion view create method
         * @public
         */
        _create: function () {
            var self    = this,
                options = self.options;


            this.isHidden = true;
            this.pointer =   $(options.pointer);
            
            this.flyoutWrapper = $(options.flyoutWrapper).removeClass('noDisplay').addClass('jq-flyoutWrapper').uniqueId().hide();

            this.flyoutSource = this.element.addClass('jq-flyoutSource').bind('click',function(event){
                event.preventDefault();
                self.toggle();
            });

            this.flyoutSource.attr({'aria-controls':this.id,'role':'button'}).uniqueId();

            this.flyoutSourceId = this.flyoutSource.attr('id');
            this.flyoutWrapper.attr({'aria-labelledby':this.flyoutSourceId,'role':'alert'});

            //hide the flyout on document click
            $(document).click(function(e){
                var target = $(e.target);

                if(target[0] !== self.element[0]){
                    if(target.parents().filter('.jq-flyoutWrapper, .jq-flyoutSource').length === 0){
                        self.hide();
                    }
                }
            });

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
         * @desc hide any flyouts which is open
         */
        hideSiblings: function(){
            var self = this;
            $(document).find('.jq-flyoutWrapper').each(function(){
                var $el = $(this),$parent;
                if($el.attr('id') != self.flyoutWrapper.attr('id')){
                    $parent = $('#'+$el.attr('aria-labelledby'));
                    $parent.flyout('hide');
                }
            });
        },
        /**
         * @desc flyout toggle method
         * @public
         */
        toggle: function(){
            var self = this;
            if(this.isHidden){
                self.hideSiblings();
                self.show();
            } else {
                self.hide();
            }
        },
        /**
         * @desc show flyout method
         * @public
         */

        show: function(){

            var self = this,
                pointerLeft;

            if(self.isHidden){

                //have to get the position just before slideDown
                this.top = this.flyoutSource.offset().top;
                this.left = this.flyoutSource.offset().left;
                this.height = this.flyoutWrapper.outerHeight();
                this.width = this.flyoutWrapper.outerWidth();

                if(this.options.adjustLeft){
                    this.flyoutWrapper.css({'left':(-60)+'px'});
                }
                this.flyoutWrapper.show();

                this.flyoutWrapper.attr({'aria-expanded':'true','aria-hidden':'false'}).focus();
                this.isHidden = false;
            }
        },
         /**
         * @desc hide flyout method
         * @public
         */
        hide: function(){
            var self = this;
            if(!self.isHidden){
                this.flyoutWrapper.hide();
                this.isHidden = true;
                this.flyoutWrapper.attr({'aria-expanded':'false','aria-hidden':'true'});
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
