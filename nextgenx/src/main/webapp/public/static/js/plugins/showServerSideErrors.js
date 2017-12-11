/**
 * show server side errors Jquery function
 * $element jQuery object
 * response object
 * parent jQuery Element
 */
$.extend( {
    /**
     *
     * @param response
     * @param parent
     * @param {boolean} [noEscape=undefined] noEscape flag
     * @param {boolean} [modal=undefined] modal flag
     */
    showServerSideErrors: function(response,parent,noEscape, modal) {
        var noEscapeHTML 		= noEscape ? noEscape : false,
        	modalDialogError	= modal ? modal : false,
			multipleErrors		='',
			errorHeading
			;

        if($.isArray(response.data)){
			////to check if it's field level validation error or generic multiple errors.
			if(response.data[0].field){  
				$.each(response.data,function(k,v){
					//$element,rule,errorMessageClasses,errorFieldClass,genericErrorMessage
					var $element = parent.find('input[name="'+ v.field+'"]');
					$.promptMessage($element,'NULL',['jq-inputError','formFieldMessageError'],'textInputError', v.message);
					//hide success icon if present
					$element.siblings('.iconCheck').addClass('noDisplay');
				});
			} else {
					if(Object.keys(response.data).length < 2) {
						var messageClass = modalDialogError ? 'noticeBoxTextSmallBox' : 'noticeBoxTextSmallMod1';
						errorHeading =response.data[0].reason;
					} else {
								var messageClass = 'multipleErrorMessage';	
								errorHeading ='Sorry, but we encountered the following problems:';
								multipleErrors ='<ul class="iconLinkList">';
								$.each(response.data,function(k,v){
										if(v.reason){
											multipleErrors +='<li><em class="iconlink"></em><span class="iconLinkLabel emphasis">'+v.reason+'</span></li>';	
										}		
								});	
								multipleErrors +='</ul>';	
					}
					var sourceStart = ['<div class="ui-helper-hidden-accessible">{{message}}</div>',
										'<div class="noticeBox warningBox" data-error-message="error">',
											'<ul class="noticeBoxWrapper">',
												'<li>',
													'<span class="messageIcon">',
														'<em class="iconItem"></em>',
													'</span>',
												'</li>',
												'<li class="noticeBoxText {{messageClass}}">',
													'<p aria-hidden="true" class="visible">',
														'{{message}}',
													'</p>'],													
						sourceEnd =['</li>',
								'</ul>',
							'</div>'];
					var template    = Handlebars.compile(sourceStart.join('')+multipleErrors+sourceEnd.join(''),{noEscape: noEscapeHTML}),
						$errorWrap  = parent.find('.jq-FormErrorMessage');
					if($errorWrap.length == 0){
						//$errorWrap.html(template({'message':errorHeading,'messageClass':messageClass})).removeClass('noDisplay').focus();
						$('.jq-FormErrorMessage.customFormError').html(template({'message':errorHeading,'messageClass':messageClass})).removeClass('noDisplay').focus();
					} else {
						//display alert. Added for backward compatible support
						//$('.jq-FormErrorMessage.customFormError').html(template({'message':response.data,'messageClass':messageClass})).removeClass('noDisplay').focus();
						alert(response.data);
					}
			}
        } else {
        	var messageClass = modalDialogError ? 'noticeBoxTextSmallBox' : 'noticeBoxTextSmallMod1';
            var source = [                 
                     
                    '<div data-view-component="messagealert" data-directive-processed="true" class="view-messagealert_2 noticeBox warningBox" data-ng-key="messageBox">',
                    '<div class="response-message alert  message-alert-dynamic" role="alert">',
                	'<span class="icon-container-outer">',      
                       '<span class="icon-container default ">',
                            '<span class="icon icon-notification-fail"></span>',
                       '</span>',        
                	'</span>',
                	'<span class="message">',      
                		'{{message}}', 
                	'</span>',
            		'</div>', 
                    '</div>'],
            template    = Handlebars.compile(source.join(''),{noEscape: noEscapeHTML}),
            $errorWrap  = parent.find('.jq-FormErrorMessage');
            if($errorWrap.length == 1){               
              $('.jq-FormErrorMessage').html(template({'message':response.data,'messageClass':messageClass})).removeClass('noDisplay').attr('role','alert').attr('tabindex','-1').focus();
              $('.jq-FormErrorMessage.customFormError .view-messagealert_2').addClass('noDisplay');
               //$errorWrap.html(template({'message':response.data,'messageClass':messageClass})).removeClass('noDisplay').attr('role','alert').attr('tabindex','-1').focus();
              // $('.jq-FormErrorMessage.customFormError').html(template({'message':errorHeading,'messageClass':messageClass})).removeClass('noDisplay').focus();
            } else {
                //display alert. Added for backward compatible support
            	//$('.jq-FormErrorMessage').html(template({'message':response.data,'messageClass':messageClass})).removeClass('noDisplay').attr('role','alert').attr('tabindex','-1').focus();
                alert(response.data);
            	// $('.error').html(response.data);
            }
        }
    }
});
