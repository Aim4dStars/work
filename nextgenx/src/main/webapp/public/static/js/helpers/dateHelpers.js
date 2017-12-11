/**
 * isLeapYear helper
 * @param year
 * @return {Boolean}
 */
Date.isLeapYear = function (year) {
    return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
};

/**
 * getDaysInMonth helper
 * @param year
 * @param month
 * @return {*}
 */
Date.getDaysInMonth = function (year, month) {
    return [31, (Date.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
};

/**
 * isLeapYear helper
 * @return {Boolean}
 */
Date.prototype.isLeapYear = function () {
    var y = this.getFullYear();
    return (((y % 4 === 0) && (y % 100 !== 0)) || (y % 400 === 0));
};

/**
 * getDaysInMonth helper
 * @return {*}
 */
Date.prototype.getDaysInMonth = function () {
    return Date.getDaysInMonth(this.getFullYear(), this.getMonth());
};

/**
 * addMonths helper
 * @param value
 * @return {*}
 */
Date.prototype.addMonths = function (value) {
    var n = this.getDate();
    this.setDate(1);
    this.setMonth(this.getMonth() + value);
    this.setDate(Math.min(n, this.getDaysInMonth()));
    return this;
};