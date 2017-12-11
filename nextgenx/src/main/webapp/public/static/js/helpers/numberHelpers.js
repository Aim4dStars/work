/**
 * leftZeroPad helper
 * @param numZeros
 * @returns {String}
 */
Number.prototype.leftZeroPad = function(numZeros) {
    var n = Math.abs(this);
    var zeros = Math.max(0, numZeros - Math.floor(n).toString().length );
    var zeroString = Math.pow(10,zeros).toString().substr(1);
    if( this < 0 ) {
        zeroString = '-' + zeroString;
    }

    return zeroString+n;
};

/**
 * substr helper
 * @param num Number
 * @returns {String}
 */
Number.prototype.substr = function (num) {
    return this.toString().substr(num);
};