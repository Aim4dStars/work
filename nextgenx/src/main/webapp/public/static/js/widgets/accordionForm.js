
/**
 * @namespace $.ui.accordionForm
 */
;(function ( $, window, document ) {

    $.widget( 'ui.accordionForm' , {

        /**
         * Options to be used as defaults
         */
        options: {
            accordionWrapper: null,
            onSlideDown: function(){},
    		onClose	: function(){}
        },
        /**
         * @desc accordion form create method
         * @public
         */
        _create: function () {
            var self    = this,
                options = self.options;


            self.isHidden = true;
            
            self.accordionWrapper = $(options.accordionWrapper).addClass('jq-accordionWrapper').removeClass('noDisplay').hide().uniqueId();
            
            self.accordionClose 	  = self.accordionWrapper.find('.jq-advSearchFilterClose');
            
            self.accordionForm	  = self.accordionWrapper.find('.jq-accordionForm');

            self.accordionSource = self.element.addClass('jq-accordionSource').bind('click',function(event){
                event.preventDefault();
                self.toggle();
            });
            
            self.accordionClose.bind('click',function(event){
                event.preventDefault();
                self.hide();
            });
            
            self.accordionForm.find('.jq-date').bind('change', function(){
            	self.accordionForm.removeClass('jq-preventSubmit');
            });
            

            self.accordionSourceId = this.accordionSource.attr('id');
            self.accordionWrapper.attr({'aria-labelledby':self.accordionSourceId});
            self.accordionForm.addClass('jq-preventSubmit');
            self.accordionSource.attr({'aria-controls':this.id,'role':'tab'}).uniqueId();

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
         * @desc accordion toggle method
         * @public
         */
        toggle: function(){
            var self = this;
            if(self.isHidden){
                self.show();
            } else {
                self.hide();
            }
        },
        /**
         * @desc show accordion method
         * @public
         */

        show: function(){

            var self = this;
            if(self.isHidden){
            	
            	self.accordionForm.resetForm();
        		self._initDates();
        		self.accordionForm.addClass('jq-preventSubmit');
            	self.accordionWrapper.slideDown(400, function(){
            		self.options.onSlideDown();
                	self.accordionWrapper.find('.jq-focusDefault').focus();
                });
            	self.accordionWrapper.attr('style','');
            	
            	self.accordionWrapper.attr({'aria-expanded':'true','aria-hidden':'false'}).focus();
            	self.isHidden = false;
            }
        },
         /**
         * @desc hide accordion method
         * @public
         */
        hide: function(){
            var self = this;
            if(!self.isHidden){
            	self.options.onClose();
            	self.accordionForm.resetForm();
            	self._initDates();
            	self.accordionWrapper.hide();
            	self.isHidden = true;
            	self.accordionSource.focus();
            	self.accordionForm.addClass('jq-preventSubmit');
            	self.accordionWrapper.attr({'aria-expanded':'false','aria-hidden':'true'});
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
        },
        
        _initDates : function(){
        	var self 				= 	this, 
        		defaultDate, deaultDateFmtDate;
        	
        	self.accordionForm.find('.jq-date').each(function(){
        		deaultDate 			= 	new Date($(this).attr('data-default-value'));
            	deaultDateFmtDate 	= 	$.formatDate(deaultDate,'dd mmm yyyy');
            	$(this).parents('.jq-dateFormFieldContainer').find('.jq-dateCalendarPlaceHolder').datepicker('setDate', deaultDate);
            	$(this).val(deaultDateFmtDate);
        	});
        	
        }
    });

})( jQuery, window, document );
