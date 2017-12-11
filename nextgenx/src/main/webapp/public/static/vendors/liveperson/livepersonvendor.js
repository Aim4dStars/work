// TWIKI Page at http://dwgps0026/twiki/bin/view/NextGen/LivePersonClickToChat
(function (window) {
    var livePersonId;
    var logonChatEnabled = $('body').attr('data-liveperson');

    $.ajax({
        url: "/ng/public/api/v1_0/env",
        dataType: "json",
        type: "GET",
        success: function (data) {
            if(data.data.environment !== null || data.data.environment !== "" || data.data.environment !== undefined || data.data.environment !== "LOCALHOST") {
                livePersonId = data.data.livePersonId;
                init(data.data.environment.toLowerCase());
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            //do nothing
        }
    });
   
    function init(env) {
        var customerId = 'Public';
        var accountName = 'Public';
        var role = 'Public';
        var section = ["org-btfg","lob-btinvest","site-btinvest","public","env-"+env];

        if (logonChatEnabled) {
            section = ["org-btfg","lob-btpanorama","site-btpanorama-regosignon","public","env-"+env];
            createChatButtonContainer();
            connectLivePersonChannel(customerId, accountName, role, section);
        } else {
            $.ajax({
                url: "/ng/onboard/api/v1_0/clients/info?type=email,individual_details",
                dataType: "json",
                type: "GET",
                success: function (response) {
                    customerId = getValueByPath(response, 'data.individualDetails.userName');
                    accountName = getValueByPath(response, 'data.individualDetails.firstName') + ' ' + getValueByPath(response, 'data.individualDetails.lastName');
                    role = 'Prospective Investor';
                    section = ["org-btfg","lob-btinvest","site-btinvest","auth","env-"+env];
                }
            }).always(function() {
                createChatButtonContainer();
                connectLivePersonChannel(customerId, accountName, role, section);
            });
        }
    }
    function createChatButtonContainer() {
        var div = document.createElement('div');
        div.id = 'lpButtonDiv';
        document.body.appendChild(div);
    }
    function connectLivePersonChannel(customerId, accountName, role, section){
        lpTag.sdes.push({ type: 'ctmrinfo', info: { customerId: customerId, accountName: accountName, role: role } });
        lpTag.section = section;
        lpTag.site = livePersonId;
        lpTag.init();
    }

    function getValueByPath(obj, path) {
        var i, len;
        for (i = 0, path = path.split('.'), len = path.length; i < len; i++) {
            if (!obj || typeof obj !== 'object') {
                return undefined;
            }
            obj = obj[path[i]];
        }

        return obj;
    }

    window.lpGetAuthenticationToken = function(cb) {
        $.ajax({
            url: "/aac/oidc/endpoint/amapp-runtime-PanoramaOpenID/authorize",
            data: {
                scope: 'openid',
                response_type: 'id_token token',
                client_id: 'kUmezRAF2sGfvXiVTUqP',
                redirect_uri: 'https://apac.ts.liveperson.com/api/authenticate/stateredirect?v=1',
                nonce: 'na',
                response_mode: 'form_post',
            },
            type: "GET",
            success: function (data) {
                var token = data.match(/id_token"\svalue="([^"]+)/)[1];
                cb(token);
            }
        });
    }
})(window);
