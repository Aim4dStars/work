/**
 * @desc Global Elements Module
 * @namespace org.bt.modules.globalLeftNavigation
 */
org.bt.modules.globalLeftNavigation = (function($,window,document) {

    var _DOMElements = {
            docLibraryLeftNav  : '.jq-docLibraryLeftNav',
            gcmHomeLeftNav     : '.jq-gcmHomeLeftNav'
    };

	//bind all the DOM events - starts
    var _bindDOMEvents = function(){

    }; //bind all the DOM events - ends

    /**
     * Hide count bubble if the message count is zero
    **/
    var _showHideNavOptions = function(){
        var action = 'secure/page/serviceOps/user/role';
        org.bt.utils.communicate.ajax({
            url:action,
            type:'GET',
            data:null,
            async:false,
            onSuccess:function(res){
                if (res.data) {
                    //show or hide GCM Home
                    if (res.data.gcmHome) {
                         $(_DOMElements.gcmHomeLeftNav).removeClass('noDisplay');
                    } else {
                        $(_DOMElements.gcmHomeLeftNav).addClass('noDisplay');
                    }
                    //show or hide Document Library
                    if(res.data.docLibrary){
                        $(_DOMElements.docLibraryLeftNav).removeClass('noDisplay');
                    } else {
                        $(_DOMElements.docLibraryLeftNav).addClass('noDisplay');
                    }
                }
            },
            onError:function(){
                $(_DOMElements.docLibraryLeftNav).addClass('noDisplay');
            }
        });
    };

    var _init = function(){
        _bindDOMEvents();
        _showHideNavOptions();
    };

    return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document);