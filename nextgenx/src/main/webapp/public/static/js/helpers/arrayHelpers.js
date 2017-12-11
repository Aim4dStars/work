/**
 * @memberOf Array
 * @desc sortBy helper
 * @param property
 */
Array.prototype.sortBy = function(property) {
    return this.sort(function (a,b) {
        return (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
    });
};

/**
 * @memberOf Array
 * @desc remove helper
 * @param val
 */
Array.prototype.remove= function(val){
    for (var i = 0; i < this.length; i++) {
        var c = this[i];
        if (c == val || (val.equals && val.equals(c))) {
            this.splice(i, 1);
            break;
        }
    }
};

/**
 * @memberOf Array
 * @desc Array indexOf for IE
 */
if (!('indexOf' in Array.prototype)) {
    Array.prototype.indexOf= function(find, i /*opt*/) {
        if (i===undefined) i= 0;
        if (i<0) i+= this.length;
        if (i<0) i= 0;
        for (var n= this.length; i<n; i++)
            if (i in this && this[i]===find)
                return i;
        return -1;
    };
}
/**
 * @memberOf Array
 * @desc Array lastIndexOf for IE
 */
if (!('lastIndexOf' in Array.prototype)) {
    Array.prototype.lastIndexOf= function(find, i /*opt*/) {
        if (i===undefined) i= this.length-1;
        if (i<0) i+= this.length;
        if (i>this.length-1) i= this.length-1;
        for (i++; i-->0;) /* i++ because from-argument is sadly inclusive */
            if (i in this && this[i]===find)
                return i;
        return -1;
    };
}
