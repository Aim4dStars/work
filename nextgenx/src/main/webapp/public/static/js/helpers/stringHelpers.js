/**
 * trim helper
 * @return {String}
 */
if(typeof String.prototype.trim !== 'function') {
    String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, '');
    }
}
/**
 * isPrintable helper
 * @return {Boolean}
 */
String.prototype.isPrintable = function(){
    var re=/^[\040-\176]*$/;
    return re.test(this);
};

/**
 * escape html helper
 * @return {String}
 */
String.prototype.escapeHtml = function(){
    var entityMap = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': '&quot;',
        "'": '&#39;',
        "/": '&#x2F;'
    };
    return this.replace(/[&<>"'\/]/g, function (s) {
        return entityMap[s];
    });
};