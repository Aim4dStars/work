org.bt.modules.testPage = (function($,window,document) {

    var _initPlugins = function(){
        //sample validation engine
        $('form.jq-cashPaymentMakePaymentForm').validationEngine({
            ajaxSubmit:false,
            ajaxValidationUrl   :org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl		:org.bt.utils.serviceDirectory.addBpayPayee(),
            dataType            :'json',
            onValidationComplete:function($form,success){

            },
            onSubmitSuccess:function(d){

            },
            onSubmitError:function(){

            }
        });

        //sample select

        $('select#select').dropkick({
            change: function (value, label) {
                $(this).change();
                org.bt.utils.log.info('select change calling');
            },
            inputClasses:['smallInput']
        });

        //sample calendar
        $('#date').calendar();

        //password policy
        var passwordPolicy = $('#password').passwordPolicy({'userNameElement':'#username'});

        $('#anotherPassword').passwordPolicy({'userNameElement':'#anotherUsername'});

        $('#validatePassword').click(function(){
            var valid = $('#password').passwordPolicy('validate');
            console.log('password validation status',valid)
        });
        $('#validateAnotherPassword').click(function(){
            var valid = $('#anotherPassword').passwordPolicy('validate');
            console.log('another password validation status',valid)
        });

        //Accordion Table
        var $accordion = $('#demo-table');
        if($accordion.length > 0){
            $accordion.accordionTable();

            $('.jq-sampleSliderButton').click(function(e){
                var slider = $(this).parents('.jq-innerSlider').siblings('.jq-anotherSlider');
                $accordion.accordionTable('toggleInnerSliders',slider);
            });
        }

    };
    var _init = function(){
        _initPlugins();
    };
    return{
        init:function(){
            _init();
        }
    }
})(jQuery, window, document);
