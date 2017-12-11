/**
 * @namespace org.bt.modules.serviceOperator
 * @desc Service operator
 */
org.bt.modules.maintaiIpToIpRelationship = (function($, window, document) {

	var _DOMElements = {

		documentUploadLink : '.jq-newDocVersionUpload',
		fileInput : "#file",
		docSize : ".jq-SelectedDocumentSize",
		docName : ".jq-SelectedDocumentName",
		submitBtn : '.jq-formSubmit',
		fromSilo:"#fromSilo",
		toSilo:"#toSilo",
		personType:"#personType"
	}

	$(_DOMElements.documentUploadLink).click(function(event) {
		$(_DOMElements.fileInput).click();
	});
	$(_DOMElements.fileInput).change(function(e) {
		e.preventDefault();
		if ($(this).val()!= null && $(this).val() != '') {

			var fileName = $(this).val().split('\\').pop();
			var extention = fileName.split(".").pop();
			if (extention != "xlsx" && extention != "xls") {
				$(".fileUploadError").removeClass("noDisplay");
				return false;
			}
			var file = document.getElementById('file').files[0];
			$(_DOMElements.docSize).text(Math.round(file.size / 1024) + 'kb');
			$(_DOMElements.docName).text(fileName);
			$(_DOMElements.submitBtn).attr('disabled', false);
		}else{
			$(".fileUploadError").removeClass("noDisplay");
			return false;
		}
	});
	
	
	$( _DOMElements.fromSilo ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.toSilo ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
    $( _DOMElements.personType ).dropkick( {
        inputClasses : [
            'inputStyleEight'
        ],
        change : function ( value, label ) {

        }
    } );
	

})(jQuery, window, document);