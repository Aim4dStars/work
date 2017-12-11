/**
 * @fileOverview validateDate jQuery plugin
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
    $.validateDate('Jan 1 13')
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.validateDate
 */

$.extend( {
    /**
     * @memberOf $.validateDate
     * @param {String} value date can be entered in any following formats
     * <ul>
     *     <li>1/1/12</li>
     *     <li>01/01/2012</li>
     *     <li>Jan 1 12</li>
     *     <li>Jan 1 2012</li>
     *     <li>1.1.12</li>
     *     <li>1-1-12</li>
     *     <li>1 Jan 12</li>
     *     <li>January 1 2012</li>
     *     <li>Jan 1</li>
     *     <li>1 Jan</li>
     *     <li>1st Jan</li>
     * </ul>
     * @returns {Object} Date Object or null
     */
    validateDate: function(value) {
        /*
         helper function
         */
        function _isValidDate(s){
            var bits = s.split('/');
            var y = bits[2], m  = bits[1], d = bits[0];
            // Assume not leap year by default (note zero index for Jan)
            var daysInMonth = [31,28,31,30,31,30,31,31,30,31,30,31];

            // If evenly divisible by 4 and not evenly divisible by 100,
            // or is evenly divisible by 400, then a leap year
            if ( (!(y % 4) && y % 100) || !(y % 400)) {
                daysInMonth[1] = 29;
            }
            return d <= daysInMonth[--m];
        }

        /*
            plugin
         */
        var matched 		= null,
            value			= value.replace(/ /g,"/"),
            format			= null,
            matchedformat	= null,
            monthIndex      = null,
            dateConfig		= {},
            date			= null,
            today           = new Date(),
            shortMonthNames = ['jan','feb','mar','apr','may','jun','jul','aug','sep','oct','nov','dec'],
            longMonthNames  = ['january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december'],
            formats = [
                {'regex':/^(\d{1,2})\/(\d{1,2})\/(\d{2})$/,'format':['day','month','year']},
                {'regex':/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/,'format':['day','month','year']},
                {'regex':/^(\w{3})\/(\d{1,2})\/(\d{2})$/,'format':['month','day','year'],'month':shortMonthNames},
                {'regex':/^(\w{3})\/(\d{1,2})\/(\d{4})$/,'format':['month','day','year'],'month':shortMonthNames},
                {'regex':/^(\d{1,2})\.(\d{1,2})\.(\d{2})$/,'format':['day','month','year']},
                {'regex':/^(\d{1,2})\-(\d{1,2})\-(\d{2})$/,'format':['day','month','year']},
                {'regex':/^(\d{1,2})\/(\w{3})\/(\d{2})$/,'format':['day','month','year'],'month':shortMonthNames},
                {'regex':/^(\w{3,9})\/(\d{1,2})\/(\d{4})$/,'format':['month','day','year'],'month':longMonthNames},
                {'regex':/^(\w{3})\/(\d{1,2})$/,'format':['month','day','year'],'month':shortMonthNames},
                {'regex':/^(\d{1,2})\/(\w{3})$/,'format':['day','month','year'],'month':shortMonthNames},
                {'regex':/^(\w{3})\/(\w{3})$/,'format':['day','month','year'],'month':shortMonthNames},

                {'regex':/^(\d{1,2})\/(\w{3})\/(\d{4})$/,'format':['day','month','year'],'month':shortMonthNames}
            ];

        /*
         1/1/12 - valid
         01/01/2012 - valid
         Jan 1 12 - valid
         Jan 1 2012 - valid
         1.1.12 - valid
         1-1-12 - valid
         1 Jan 12 - valid
         January 1 2012 - valid
         Jan 1 - valid
         1 Jan - valid
         1st Jan - valid

         1.1
         1-1
         1st Jan 12
         */
        $.each(formats,function(k,format){
            if(matched === null){
                matched = value.match(format['regex']);
                if(matched !== null){
                    matchedformat = k;
                }
            }
        });
        if(matchedformat !== null){
            matched = value.match(formats[matchedformat]['regex']);
            format = formats[matchedformat]['format'];
            dateConfig[format[0]] = matched[1];
            dateConfig[format[1]] = matched[2];
            dateConfig[format[2]] = matched[3] || today.getFullYear();

            if(isNaN(dateConfig.month)){
                //month is not a number. Therefore lookup from array
                monthIndex =  formats[matchedformat]['month'].indexOf(dateConfig.month.toLowerCase());
                if(monthIndex > -1){
                    dateConfig.month = monthIndex + 1;
                }
            }
            if(isNaN(dateConfig.day)){
                //date is not a number. Must be 1st format
                dateConfig.day = parseInt(dateConfig.day,10);
            }

            //validate month and the date is in the range
            if(_isValidDate(dateConfig.day+'/'+dateConfig.month+'/'+dateConfig.year)){
                org.bt.utils.log.info('entered a valid date',dateConfig);
                date = new Date(dateConfig.year, dateConfig.month-1, dateConfig.day);
                //check have a date before 1970
                if (date !== null && date.getFullYear() < 1970) {
                    //if user has entered year in two digit add another 100 years
                    if(dateConfig.year.length == 2){
                        date.setFullYear(date.getFullYear() + 100);
                    }
                }
            }
        }
        return date;
    }
});