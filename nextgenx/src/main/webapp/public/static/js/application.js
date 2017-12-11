$(document).ready(function() {
    //init logging
    org.bt.utils.log.init();

    //init all the modules
    $.each(org.bt.modules,function(k,v){
        var init    = (typeof(v.init) === 'function');

        if(init){
            v.init();
        }
    });
    //hide cloak!. all js has been loaded
    $(document).find('head').append('<style type="text/css">@charset "UTF-8";.ng-cloak{display:none;}</style>');
});