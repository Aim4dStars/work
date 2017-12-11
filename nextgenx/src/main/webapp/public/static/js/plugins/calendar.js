/**
 * calendar Jquery function
 */
(function($){
    $.fn.calendar = function(options) {
        var defaults = {
            dateClass: 'jq-currentDate',
            title:'Date Picker',
            date:null,
            dateFormat:'mm/dd/yy',
            minDate: null,
            defaultDate: null,
            onSelect:function(){},
            blurOnSelect:true,
            autoHide:true
        };
        options = $.extend(defaults, options);

        return this.each(function() {
            var $element        = $(this),
                calendarRef     = 'calendar-'+new Date().getTime(),
                $calendarElement= null,
                date            = (options.date === null) ? $.formatDate(new Date(),'dd') : options.date,
                toggle          = null;

            //create calendar icon
            $element.after(
                [
                    '<a title="'+options.title+'" href="#" class="calendarIcon jq-appendErrorAfter jq-calendarIcon '+options.dateClass+'">',
                    '<span class="calendarHeader"></span>',
                    '<span class="calendarBody">',
                    '<p class="currentDate">'+date+'</p>',
                    '</span>',
                    '</a>',
                    '<span class="calendarPlaceHolder" id="'+calendarRef+'"></span>'
                ].join('')
            ).attr('data-calendar','#'+calendarRef);

            $calendarElement = $('#'+calendarRef);

            //init jQuery datepicker
            $calendarElement.datepicker({
                dateFormat:options.dateFormat,
                minDate: options.minDate,
                defaultDate: options.defaultDate,
                onSelect: function(dateText, inst) {
                    $element.val($.formatDate(new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay),'dd mmm yyyy'));
                    $(this).addClass('noDisplay');
                    if(options.blurOnSelect){
                        $element.trigger('focusout')
                    }
                    options.onSelect();
                }
            }).addClass('noDisplay');

            //toggle calendar
            $element.siblings('.jq-calendarIcon').click(function(e){
                e.preventDefault();
                toggle          = ($calendarElement.hasClass('noDisplay')) ? $calendarElement.removeClass('noDisplay') : $calendarElement.addClass('noDisplay')
            });
            //auto hide calendar
            if(options.autoHide){
                $(document).click(function(e){
                    var target              = $(e.target);

                    if(target.parents().filter('.calendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                        $calendarElement.datepicker().addClass('noDisplay');
                    }
                })
            }
        })
    }
})(jQuery);
