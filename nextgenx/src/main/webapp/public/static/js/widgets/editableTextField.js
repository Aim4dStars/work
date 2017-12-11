;(function ( $, window, document ) {

    $.widget( 'ui.editableTextField' , {
        //Options to be used as defaults
        options: {
            wrapperClass:'editableTextField',
            textFieldClass:'formTextInput formTextInputMod1',
            buttonWrapperClass:'actionWrapper actionWrapperMod2 actionWrapPrimary clearFix',
            labelText:'label',
            saveButtonClass:'actionButtonIcon',
            saveButtonDisabledClass:'actionButtonIconDisabled',
            saveButtonText:'Save',
            cancelButtonClass:'baseLink baseLinkMod1',
            cancelLinkText:'Cancel',
            submitType:'GET',
            submitUrl:'controller/method/submit.do',
            onSuccess:function(x){}
        },

        _create: function () {

            var self        = this,
                $element    = self.element,
                settings    = self.options;

            this.value = $element.val();

            $element.attr({'title':'Click to edit','readonly':'readonly'});

            this.wrap = $('<div>',{'class':settings.wrapperClass});
            $element.wrap(this.wrap);

            this.buttonWrap = $('<ul>',{'class':settings.buttonWrapperClass}).hide();
            $element.after(this.buttonWrap);

            this.saveButtonImage = '<em class="iconarrowright"></em>';
            this.saveButtonText = '<span>' + settings.saveButtonText + '</span>';
            this.saveBtn = $('<a>',{'href':'#nogo','class':settings.saveButtonClass,'role':'button'}).html( this.saveButtonText + this.saveButtonImage);
            this.buttonWrap.append(this.saveBtn);

            this.cancelButtonImage = '<em class="iconlink" title="Cancel"><span> Icon:Link</span></em>';
            this.cancelBtn = $('<a>',{'href':'#nogo','class':settings.cancelButtonClass,'role':'button'}).html(this.cancelButtonImage + settings.cancelLinkText);
            this.saveBtn.after(this.cancelBtn);

            this.saveBtn.wrap($('<li>'));
            this.cancelBtn.wrap($('<li>'));

            this.canSubmit = (typeof $element.attr('data-can-submit') !== "undefined") ? /true/i.test($element.attr('data-can-submit')): true;

            if(!this.canSubmit){
                this.saveBtn.addClass(settings.saveButtonDisabledClass);
            }

            this.element.bind('click.editable.click',function(){
                var readOnly= $element.is('[readonly]');

                if(readOnly){
                    $element.removeAttr('readonly');
                    self.buttonWrap.show();
                }
            });

            this.saveBtn.bind('click.editable.save', function(event) {
                event.preventDefault();

                var data            = {},
                    customDataAttr  = $element.attr('data-custom'),
                    customData;

                if(!self.canSubmit){
                    return false;
                }

                if($element.attr('placeholder') == $element.val()){
                    $element.val('');
                }

                data[$element.attr('name')] = $element.val();

                //assume that server will not throw an error so update the value
                self.value = $element.val();

                //if there is a custom attribute then set the value
                if(customDataAttr){
                    customData = customDataAttr.split('@');
                    data[customData[0]] = customData[1];
                }

                org.bt.utils.communicate.ajax.apply(self,[{
                    'type':settings.submitType,
                    'url':settings.submitUrl,
                    'data': data,
                    'onSuccess':function(d){self._onSuccess.call(self,d)},
                    'onError':self._readOnly.call(self)
                }]);
            });

            this.cancelBtn.bind('click.editable.cancel', function(event) {
                //reset value
                var val = self.value;
                self.element.val(val);

                self._readOnly.call(self);
            });

        },
        cancel: function(){
            this.element.val(this.value);
            this._readOnly.call(this);
        },
        _onSuccess: function(data){
            this.options.onSuccess(data);
            this._readOnly();
        },
        _readOnly:function(){
            this.element.attr('readonly','readonly');
            this.element.trigger('blur');
            this.buttonWrap.hide();
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

        // Respond to any changes the user makes to the
        // option method
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






