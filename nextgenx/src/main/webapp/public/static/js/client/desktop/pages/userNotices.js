org.bt.modules.userNotices = (function ($) {
    var _DOMElements = {
        userUpdatesType: '.jq-noticesType'
    };

    return {
        init: function () {
            $(_DOMElements.userUpdatesType).on('click', function (e) {
                e.preventDefault();
                var dataSet = e.currentTarget.dataset;
                var description = $('#'+ dataSet.updateValue + '_input').val();
                var data = [{ name: 'noticeId', value: dataSet.updateValue },
                            { name: 'version', value: dataSet.versionValue } ,
                            { name: 'description', value: description }];
                org.bt.utils.communicate.post({ action: '', data:  data});
            });
        }
    }
})(jQuery);
