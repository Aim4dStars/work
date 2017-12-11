/**
 * @fileOverview formatDate jQuery function
 * @version 1.00
 * @author Samitha Fernando
 * @requires jquery
 * @example
 * $.formatDate(new Date(),'yyyy-mm-dd')
 */

/**
 * See <a href="http://jquery.com">http://jquery.com</a>.
 * @name $
 * @class
 * See the jQuery Library  (<a href="http://jquery.com">http://jquery.com</a>) for full details.  This just
 * documents the function and classes that are added to jQuery by this plug-in.
 */

/**
 * @namespace $.formatDate
 */

$.extend( {
    /**
     * @memberOf $.formatDate
     * @public
     * @param {Date} date date object
     * @param {String} format date format
     * @returns {String} formatted date
     */
    formatDate: function(date,format) {
        //check date is a valid date object
        if(typeof date.getMonth === 'function' && !isNaN(date.getMonth())){
            var monthNames	= ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
                dayNames		= ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
            format			= (typeof format === 'undefined') ? 'dd mmm yyyy':format; //set default format
            //format date
            return format.replace(/(yyyy|yy|mmmm|mmm|mm|dddd|ddd|dd|hh|nn|ss|a\/p)/gi,
                function (val) {
                    switch (val.toLowerCase()) {
                        case 'yy':
                            return date.getFullYear().substr(2);
                        case 'yyyy':
                            return date.getFullYear();
                        case 'mmmm':
                            return monthNames[date.getMonth()];
                        case 'mmm':
                            return monthNames[date.getMonth()].substr(0, 3);
                        case 'mm':
                            return (date.getMonth() + 1).leftZeroPad(2);
                        case 'dddd':
                            return dayNames[date.getDay()];
                        case 'ddd':
                            return dayNames[date.getDay()].substr(0, 3);
                        case 'dd':
                            return date.getDate().leftZeroPad(2);
                        case 'hh':
                            return ((h = date.getHours() % 12) ? h : 12).leftZeroPad(2);
                        case 'nn':
                            return date.getMinutes().leftZeroPad(2);
                        case 'ss':
                            return date.getSeconds().leftZeroPad(2);
                        case 'a/p':
                            return date.getHours() < 12 ? 'am' : 'pm';
                    }
                }
            );
        }
    }
});
