/**
 * isMobile Jquery function
 */
$.extend( {
    isMobile: function() {
        return ( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) );
    }
});
