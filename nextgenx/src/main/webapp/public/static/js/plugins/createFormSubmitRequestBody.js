/**
 * createFormSubmitRequestBody Jquery function
 * $form jQuery object
 * returns JSON Object
 */
$.extend( {
    createFormSubmitRequestBody: function($form,escapeHtml) {
		var _escapeHtml = _.isUndefined(escapeHtml) ? true : escapeHtml;
        //remove placeholder txt
        $form.find('[placeholder]').each(function() {
            var input = $(this);
            if (input.val() == input.attr('placeholder')) {
                input.val('');
            }
        });

        var data = $form.serializeArray();
        //search if there any fields requires custom values to be submitted
        $.each(data,function(k,v){
            var submitValue = $form.find('input[name="'+ v.name+'"]').attr('data-submit-value');
            if(typeof submitValue !== 'undefined'){
                v.value = submitValue; //update the value
            }
            if(_escapeHtml){
				v.value = v.value.escapeHtml(); //prevents xss attacks
			}
        });
        return data;
    }
});
