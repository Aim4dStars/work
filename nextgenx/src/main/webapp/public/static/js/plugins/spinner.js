/**
 * spinner Jquery function
 * @param options Object
 */
(function($) {
    $.fn.spinner = function(options) {
        var defaults = {
                input:'.jq-repeatNumberInput'
            },
            currentTimeoutLength = 200,
            timer                = null;
        options = $.extend(defaults, options);

        return this.each(function() {
            var $button     = $(this);

            $button.bind('mousedown',function(){onMouseDown.apply($button)});
            $button.bind('mouseup',function(){onMouseUp.apply($button)});
            $button.bind('mouseover',function(){onMouseOver.apply($button)});
            $button.bind('mouseout',function(){onMouseOut.apply($button)});
            $button.bind('click',function(){onClick.apply($button)});
        });
        function resetTimer(){
            if(timer !== null){
                clearInterval(timer);
                timer = null;
            }
        }
        function onMouseOver(){
            resetTimer();
        }
        function onMouseOut(){
            resetTimer();
        }
        function onMouseDown(){
            var self = this;
            resetTimer();
            timer = setInterval(function(){rotate.apply(self);},currentTimeoutLength)
        }
        function onMouseUp(){
            resetTimer()
        }
        function onClick(){
            resetTimer();
            rotate.apply(this);
        }
        function rotate(){
            var $element    = this,
                $input      = $(options['input']),
                value       = $input.val(),
                min         = parseInt($input.attr('data-min'),10),
                max         = parseInt($input.attr('data-max'),10),
                intValue    = parseInt(value,10),
                isNumber    = /^\d+$/.test(value),
                newValue    = 0,
                rel         = $element.attr('data-rel');

            if(isNumber){ //check user input is a number
                newValue = (rel === 'plus') ? intValue+1 : intValue -1;
                if(newValue >= min && newValue <= max){ //check new value is in the range
                    $input.val(newValue);
                }
            }
        }
    }
})(jQuery);
