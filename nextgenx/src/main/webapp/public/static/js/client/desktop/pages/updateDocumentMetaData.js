/**
 * @namespace org.bt.modules.updateDocumentMetaData
 */
org.bt.modules.updateDocumentMetaData =  (function($,window,document,moment) {
    var _DOMElements = {

        cancelDocumentMetadataPopupBlock:'.jq-cancelDocumentMetadataPopupBlock',
        editDocumentMetadataPopupBlock  :'.jq-editDocumentMetadataPopupBlock',
        editDocumentAction              :'.jq-editDocument',
        businessArea                    :'.jq-businessArea',
        documentCategory                :'.jq-documentCategory',
        addedByRole                     :'.jq-addedByRole',
        addedByName                     :'.jq-addedByName',
        addedById                       :'.jq-addedById',
        checkInDate                     :'.jq-checkInDate',
        relationshipIdInput             :'.jq-relationshipIdInput',
        documentNameInput               :'.jq-documentNameInput',
        relationshipTypeInput           :'.jq-relationshipType',
        documentTitleCodeInput          :'.jq-documentTitleCode',
        editFinancialYear               :'.jq-editFinancialYear',
        sourceIdInput                   :'.jq-sourceIdInput',
        abcOrderIdInput                 :'.jq-abcOrderIdInput',
        documentActivityInput           :'.jq-documentActivityInput',
        externalIdInput                 :'.jq-externalIdInput',
        batchIdInput                    :'.jq-batchIdInput',
        documentSubCategoryInput        :'.jq-editDocumentSubCategory',
        documentSubCategory2Input       :'.jq-editDocumentSubCategory2',
        expiryDateInput                 :'.jq-expiryDateInput',
        startDateInput                  :'.jq-startDateInput',
        endDateInput                    :'.jq-endDateInput',
        auditFlag                       :'.jq-auditFlag',
        changeToken                     :'.jq-changeToken',
        documentUpdateBtn               :'.jq-documentUpdateBtn',
        documentId                      :'.jq-documentId',
        attachedFileInfo                :'.jq-attachedFileInfo',
        accountId                       :'.jq-accountId',
        startDatePickerHolder           :'.jq-StartDateCalendarPlaceHolder',
        endDatePickerHolder             :'.jq-EndDateCalendarPlaceHolder',
        expiryDateCalendarPlaceHolder   :'.jq-expiryDateCalendarPlaceHolder',
        sourceIdDropdown                :'.jq-sourceIdDropdown',      // Class for source Id dropdown
        documentCategoryInput           :'.jq-documentCategoryInput',
        documentSoftDeleted             :'.jq-documentSoftDeleted',
        softDeleteInput                 :'.jq-softDeleteEdit',
        permanentEditInput              :'.jq-permanentEdit'
    };

    var dateFormat = 'dd mmm yyyy',
        form_clean;

    _bindDOMEvents = function () {

        _initMetaDataDatePickers();

       $( _DOMElements.editDocumentAction ).click(function(e) {
            e.preventDefault();
            var docId = $(this).attr('data-id');
            _hideValidationErrorMessage();
            _resetFieldsToDefault();
            _loadDocumentMetaData(docId);
       });

       $( _DOMElements.documentUpdateBtn ).click(function(e) {
            e.preventDefault();
            if(_isAnyChangeToSave() && _validateMandatoryFields()){
                if(!$('.jq-documentUploadBtn').hasClass('primaryButtonDisabled')) {
                $(_DOMElements.documentUpdateBtn).addClass('primaryButtonDisabled');
                    _updateDocument();
                }
            }
       });

       $( '.jq-documentMetadataTab' ).click(function(e) {
           e.preventDefault();
           if($('#tab1').hasClass('noDisplay')){
              $('#tab1').removeClass('noDisplay');
              $('#tab2').addClass('noDisplay');
              $('ul.tabs li').removeClass('active');
              $(this).addClass('active')
           }
      });

      $('ul.tabs li').on('click',function(e){
            e.preventDefault();
            $('ul.tabs li').removeClass('active');
            $(this).addClass('active');
            $('.block article').hide();
            var activeTab = $(this).find('a').attr('href');
            if(activeTab === '#tab1'){
              $('#tab1').removeClass('noDisplay');
               $('#tab2').addClass('noDisplay');
            } else {
              $('#tab2').removeClass('noDisplay');
               $('#tab1').addClass('noDisplay');
            }
            return false;
      });

        $( '.jq-documentAuditHistoryTab' ).click(function(e) {
           e.preventDefault();
           if($('#tab2').hasClass('noDisplay')){
              $('#tab2').removeClass('noDisplay');
               $('#tab1').addClass('noDisplay');
               $('ul.tabs li').removeClass('active');
               $(this).addClass('active')
           }
      });
    };

    var _isAnyChangeToSave = function () {
        var form_dirty = $('#documentMetaDataForm').serialize();
        if(form_clean != form_dirty){
            return true;
        }
        return false;
    }

    var _updateDocument = function () {
        var docId = $( _DOMElements.documentId ).val();
        var jsonData = _buildJsonData ();
        var pathname = window.location.pathname; // Returns path only
        var path = pathname.split('/page');
        var action = path[0] + '/api/v1_0/documents/'+docId+'/update';

        org.bt.utils.communicate.ajax({
            url:action,
            type:'POST',
            data:jsonData,
            onSuccess:function(res){
               if(res.status === 1){
                    $(_DOMElements.documentUpdateBtn).removeClass('primaryButtonDisabled');
                    $( _DOMElements.editDocumentMetadataPopupBlock).dialog( "close" );
                    org.bt.modules.docLibUtils.showMessageBox('Document updated successfully.', 'successBox');
                    $("html, body").animate({scrollTop: 0}, 0);
                    $('.jq-formSubmit').trigger('click').delay(1000);
               } else {
                    $(_DOMElements.documentUpdateBtn).removeClass('primaryButtonDisabled');
                    $( _DOMElements.editDocumentMetadataPopupBlock).dialog( "close" );
                     org.bt.modules.docLibUtils.showMessageBox('Could not update document. Please try later.', 'warningBox');
                    $("html, body").animate({scrollTop: 0}, 0);
               }
            },
            onError:function(){
                $(_DOMElements.documentUpdateBtn).removeClass('primaryButtonDisabled');
                $( _DOMElements.editDocumentMetadataPopupBlock).dialog( "close" );
                org.bt.modules.docLibUtils.showMessageBox('Could not update document. Please try later.', 'warningBox');
                $("html, body").animate({scrollTop: 0}, 0);
            }
        });
    };

    var _resetFieldsToDefault = function () {
        $( _DOMElements.sourceIdInput ).val('');
        $( _DOMElements.abcOrderIdInput ).val('');
        $( _DOMElements.documentActivityInput ).val('');
        $( _DOMElements.externalIdInput ).val('');
        $( _DOMElements.batchIdInput ).val('');
        $(_DOMElements.startDateInput).val('').attr('data-placeholder','');
        $(_DOMElements.endDateInput).val('').attr('data-placeholder','');
        $(_DOMElements.expiryDateInput).val('').attr('data-placeholder','');
        $('ul.tabs li').removeClass('active');
        $('ul.tabs li:first').addClass('active');
        var activeTab = $('ul.tabs li:first').find('a').attr('href');
        if(activeTab === '#tab1'){
          $('#tab1').removeClass('noDisplay');
           $('#tab2').addClass('noDisplay');
        }
    };

    /**
     *  Init Date pickers for Portfolio, failedapp Summary
     */
    var _initMetaDataDatePickers = function(){

		$(_DOMElements.startDateInput).removeAttr('readonly');
		$(_DOMElements.endDateInput).removeAttr('readonly');
		$(_DOMElements.expiryDateInput).removeAttr('readonly');

        //date field date picker - failedapp Summary From - starts
        $(_DOMElements.startDatePickerHolder).datepicker({
            changeMonth: true,
            changeYear: true,
            onSelect: function(dateText, inst) {
                var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
                $(_DOMElements.startDateInput)
                    .val($.formatDate(selectedDate,dateFormat))
                    .trigger('focusout');
                $(this).addClass('noDisplay');
            }
        }).addClass('noDisplay');
        $(_DOMElements.startDatePickerHolder).datepicker("setDate", new Date($(_DOMElements.startDateInput).val()));
        $('.jq-startDateCalendarIcon').click(_onDateCalendarISFIconClick);

        //date field date picker - failedapp Summary To - starts
        $(_DOMElements.endDatePickerHolder).datepicker({
            changeMonth: true,
            changeYear: true,
            onSelect: function(dateText, inst) {
                var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
                $(_DOMElements.endDateInput)
                    .val($.formatDate(selectedDate,dateFormat))
                    .trigger('focusout');
                $(this).addClass('noDisplay');
            }
        }).addClass('noDisplay');
        $(_DOMElements.endDatePickerHolder).datepicker("setDate", new Date($(_DOMElements.endDateInput).val()));
        $('.jq-endDateCalendarIcon').click(_onDateCalendarISTIconClick);

        //expiry date field
        $(_DOMElements.expiryDateCalendarPlaceHolder).datepicker({
            changeMonth: true,
            changeYear: true,
            onSelect: function(dateText, inst) {
                var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
                $(_DOMElements.expiryDateInput)
                    .val($.formatDate(selectedDate,dateFormat))
                    .trigger('focusout');
                $(this).addClass('noDisplay');
            }
        }).addClass('noDisplay');
        $(_DOMElements.expiryDateCalendarPlaceHolder).datepicker("setDate", new Date($(_DOMElements.expiryDateInput).val()));
        $('.jq-expiryDateCalendarIcon').click(_onDateCalendarEXPIconClick);

        //hide date picker on window click
        $(document).click(function(e){
            var target              = $(e.target),
                $expiryDateCalendar = $(_DOMElements.expiryDateCalendarPlaceHolder),
                $dateCalendarFrom   = $(_DOMElements.startDatePickerHolder),
                $dateCalendarTo     = $(_DOMElements.endDatePickerHolder);
				


            if(!target.hasClass('jq-expiryDateCalendarIcon')){
                if(target.parents().filter('.jq-expiryDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $expiryDateCalendar.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-startDateCalendarIcon')){
                if(target.parents().filter('.jq-startDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarFrom.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-endDateCalendarIcon')){
                if(target.parents().filter('.jq-endDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarTo.datepicker().addClass('noDisplay');
                }
            }
        });
    };

   var _onDateCalendarISFIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.startDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };

    var _onDateCalendarISTIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.endDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };

    var _onDateCalendarEXPIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.expiryDateCalendarPlaceHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
			
			$('.ui-datepicker').css('left', '-39px');
    };

    var _buildJsonData = function () {
        var data = {
            'key.accountId' : $( _DOMElements.accountId ).val(),
            'key.documentId' : $( _DOMElements.documentId ).val(),
            relationshipId: $( _DOMElements.relationshipIdInput).val(),
            audit: $( _DOMElements.auditFlag ).is(":checked"),
            status:($('.jq-editPrivacyFlag' ).is(":checked") ? 'Draft' : 'Final'),
            changeToken:$( _DOMElements.changeToken ).val(),
            documentName:$( _DOMElements.documentNameInput ).val(),
            relationshipType:$( _DOMElements.relationshipTypeInput ).val(),
            softDeleted: $( _DOMElements.softDeleteInput ).is(":checked"),
            permanent:$( _DOMElements.permanentEditInput ).is(":checked")
        };

        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentSubCategoryInput) )) {
            $.extend(data,{documentSubType:(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentSubCategoryInput))});
        } else {
            $.extend(data,{documentSubType : ''});
        }

        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentSubCategory2Input) )) {
            $.extend(data,{documentSubType2:(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentSubCategory2Input))});
        } else {
            $.extend(data,{documentSubType2 : ''});
        }

        if(!_.isEmpty($( _DOMElements.externalIdInput ).val())) {
            $.extend(data,{externalId:($(_DOMElements.externalIdInput ).val())});
        } else {
            $.extend(data,{externalId : ''});
        }

        if(!_.isEmpty($( _DOMElements.batchIdInput ).val())) {
            $.extend(data,{batchId:($(_DOMElements.batchIdInput ).val())});
        } else {
            $.extend(data,{batchId : ''});
        }

        if(!_.isEmpty($( _DOMElements.abcOrderIdInput ).val())) {
            $.extend(data,{abcOrderId:($(_DOMElements.abcOrderIdInput ).val())});
        } else {
            $.extend(data,{abcOrderId : ''});
        }

        if(!_.isEmpty($( _DOMElements.expiryDateInput ).val())) {
            var xDate = new Date($(_DOMElements.expiryDateInput ).val());
            $.extend(data,{expiryDate : xDate.toISOString()});
        } else {
            $.extend(data,{expiryDate : ''});
        }

        if(!_.isEmpty($( _DOMElements.startDateInput ).val())) {
            var sDate = new Date($(_DOMElements.startDateInput ).val());
            $.extend(data,{startDate : sDate.toISOString()});
        }


        if(!_.isEmpty($( _DOMElements.endDateInput ).val())) {
            var eDate = new Date($(_DOMElements.endDateInput ).val());
            $.extend(data,{endDate : eDate.toISOString()});
        }

        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.editFinancialYear))) {
            $.extend(data,{financialYear:(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.editFinancialYear))});
        } else {
            $.extend(data,{financialYear: ''});
        }

        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentTitleCodeInput))) {
            $.extend(data,{documentTitleCode:(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.documentTitleCodeInput ))});
        } else {
            $.extend(data,{documentTitleCode: ''});
        }
       /* if(_isSourceIdFieldDropDown($( _DOMElements.documentCategoryInput ).val())) {
            if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue(_DOMElements.sourceIdDropdown) )) {
                $.extend(data,{sourceId:$(_DOMElements.sourceIdDropdown).val()});
            }else {
                $.extend(data,{sourceId:''});
            }
        } else {
            $.extend(data,{sourceId:$(_DOMElements.sourceIdInput).val()});
        }*/


        if(!_.isEmpty($( _DOMElements.sourceIdInput ).val())) {
               $.extend(data,{sourceId:($(_DOMElements.sourceIdInput ).val())});
        } else {
               $.extend(data,{sourceId : ''});
        }

        if(!_.isEmpty($(_DOMElements.documentSoftDeleted).val()) && $(_DOMElements.documentSoftDeleted).val() === 'true'){
            if(!$( _DOMElements.softDeleteInput ).is(":checked")){
                $.extend(data,{restoredDeleted:true});
            } else {
                $.extend(data,{restoredDeleted:false});
            }
        }

        return data;
    };

    var _isSourceIdFieldDropDown = function (category) {
        var isDropdown = false;
        switch(category) {
            case 'ADVICE':
            case 'TAX':
            case 'CORRO':
            case 'OTHER':
            case org.bt.modules.referenceData.INVESTMENT:
            case org.bt.modules.referenceData.SMSF:
            case org.bt.modules.referenceData.POBox:
                isDropdown = true;
                break;
            case org.bt.modules.referenceData.EMAIL:
            case 'FAX':
            case org.bt.modules.referenceData.SCANNED:
                isDropdown = false;
                break;
            default:
                isDropdown = false;
                break;
        }
        return isDropdown;
    };

    var _validateMandatoryFields = function () {
        _hideValidationErrorMessage();
        if(_.isEmpty($( _DOMElements.relationshipIdInput ).val())) {
            _showValidationErrorMessage('Relationship ID can\'t be empty.');
            return false;
        } else {
            var matched =/^\d+$/.test($( _DOMElements.relationshipIdInput ).val());
            if(!matched){
                _showValidationErrorMessage('Please enter valid Relationship ID.');
                return false;
            }
        }
        if (_.isEmpty($( _DOMElements.documentNameInput ).val())) {
            _showValidationErrorMessage('Display Name can\'t be empty.');
            return false;
        }

        if(!_.isEmpty($( _DOMElements.expiryDateInput ).val())) {
            var xDate = new Date($(_DOMElements.expiryDateInput ).val());
            if( !_validateDateString($(_DOMElements.expiryDateInput ).val()) || /Invalid|NaN/.test(xDate)){
                _showValidationErrorMessage('Please enter a valid expiry date.');
                return false;
            }
        }
        if(!_.isEmpty($( _DOMElements.startDateInput ).val())) {
            var sDate = new Date($(_DOMElements.startDateInput ).val());
            if(!_validateDateString($(_DOMElements.startDateInput ).val()) || /Invalid|NaN/.test(sDate)){
                _showValidationErrorMessage('Please enter a valid start date.');
                return false;
            }
        }
        if(!_.isEmpty($( _DOMElements.endDateInput ).val())) {
            var eDate = new Date($(_DOMElements.endDateInput ).val());
            if( !_validateDateString($(_DOMElements.endDateInput ).val()) ||  /Invalid|NaN/.test(eDate)){
                _showValidationErrorMessage('Please enter a valid end date.');
                return false;
            }
        }

        return true;
    };

    var _validateDateString = function (dateStr) {
        var regex = new RegExp(/\b\d{1,2}[\/\s]([a-zA-Z]{3}|\d{1,2})[\/\s]\d{2,4}\b/);
        return regex.test(dateStr)
    };

    var _showValidationErrorMessage = function (msg) {
        $('.jq-updateMessageBox .jq-updateMessage').text(msg);
        $('.jq-updateMessageBox').removeClass("noDisplay");
    };

    var _hideValidationErrorMessage = function () {
        if($('.jq-updateMessageBox').attr('class').indexOf('noDisplay') === -1) {
            $('.jq-updateMessageBox').addClass("noDisplay");
        }
    };

    var _loadDocumentMetaData = function (docId) {
        org.bt.modules.docLibUtils.hideMessageBox();
        var action = 'secure/page/serviceOps/document/'+docId+'/load';
        org.bt.utils.communicate.ajax({
            url:action,
            type:'GET',
            data:null,
            onSuccess:function(response){
                if(response.status === 1 && !_.isNull(response.data)){
                    $('#disableLayer').fadeIn();
                    $( _DOMElements.editDocumentMetadataPopupBlock).dialog( "open" );
                    _populateData(response);
                    form_clean = $('#documentMetaDataForm').serialize();
                } else {
                    org.bt.modules.docLibUtils.showMessageBox('Could not load document metadata. Please try later.', 'warningBox');
                    $("html, body").animate({scrollTop: 0}, 0);
                }
            },
            onError:function(){
                org.bt.modules.docLibUtils.showMessageBox('Could not load document metadata. Please try later.', 'warningBox');
                $("html, body").animate({scrollTop: 0}, 0);
            }
        });
    };

    var _populateAuditHistory = function (response) {
        $( '.jq-updatedByIdText' ).text(!_.isEmpty (response.data.updatedByID)? response.data.updatedByID : "");
        $( '.jq-updatedByRoleText' ).text(!_.isEmpty (response.data.updatedByRole) ? response.data.updatedByRole : "") ;
        $( '.jq-UpdateByNameText').text(!_.isEmpty (response.data.updatedByName ) ? response.data.updatedByName : "") ;
        $( '.jq-lastModifiedOn').text(_prepareLocalDateStr(response.data.lastModificationDate)) ;
        $( '.jq-deletedByIdText').text(!_.isEmpty (response.data.deletedByUserId)? response.data.deletedByUserId : "");
        $( '.jq-deletedByRoleText').text(!_.isEmpty (response.data.deletedByRole)? response.data.deletedByRole : "");
        $( '.jq-deletedByNameText').text(!_.isEmpty (response.data.deletedByName)? response.data.deletedByName : "");
        $( '.jq-deletedOn').text(_prepareLocalDateStr(response.data.deletedOn));
        $( '.jq-restoredByIdText').text(!_.isEmpty (response.data.restoredByUserId)? response.data.restoredByUserId : "");
        $( '.jq-restoredByNameText').text(!_.isEmpty (response.data.restoreByName)? response.data.restoreByName : "");
        $( '.jq-restoredByRoleText').text(!_.isEmpty (response.data.restoreByRole)? response.data.restoreByRole : "");
        $( '.jq-restoredOn').text(_prepareLocalDateStr(response.data.restoredOn));

    };

   var _prepareLocalDateStr = function(isoDateStr) {
        var str="";
        if(!_.isEmpty(isoDateStr)){
            var d = new Date(isoDateStr);
            str = $.formatDate(d,dateFormat) +' '+ d.toLocaleTimeString();
            return str;
        } else {
            return str;
        }
   };

   var _populateData = function (response) {

        _resetToDefault();
        _populateAuditHistory(response);
        $( _DOMElements.attachedFileInfo).text((_.isEmpty(response.data.fileName) ? '' : response.data.fileName+' - '+ Math.round(response.data.size/1024)+'kb')) ;

        $( _DOMElements.changeToken ).val(response.data.changeToken);
        $( _DOMElements.documentId ).val(response.data.key.documentId);
        $( _DOMElements.accountId ).val(response.data.key.accountId);
        $( _DOMElements.documentCategoryInput ).val(response.data.documentType);
        $(_DOMElements.documentSoftDeleted).val(response.data.softDeleted);

        $( _DOMElements.businessArea ).text(response.data.businessArea);
        $( _DOMElements.documentCategory ).text(org.bt.modules.docLibUtils.getLabel(response.data.documentType, org.bt.modules.referenceData.DOCUMENT_CATEGORIES));
        $( _DOMElements.addedByRole ).text(response.data.uploadedRole);
        if(!_.isEmpty(response.data.addedByName)){
            $( _DOMElements.addedByName ).text(response.data.addedByName);
        }
        if(!_.isEmpty(response.data.uploadedBy)){
            $( _DOMElements.addedById ).text(response.data.uploadedBy);
        }
        $( _DOMElements.relationshipIdInput ).val(response.data.relationshipId);
        $( _DOMElements.documentNameInput ).val(response.data.documentName);
        $( _DOMElements.sourceIdInput ).val(response.data.sourceId);
        /*_populateSourceIdField(response.data.documentType, response.data.sourceId);*/

        $( _DOMElements.abcOrderIdInput ).val(response.data.abcOrderId);
        $( _DOMElements.documentActivityInput ).val(org.bt.modules.docLibUtils.toTitleCase(response.data.activity));
        $( _DOMElements.externalIdInput ).val(response.data.externalId);
        $( _DOMElements.batchIdInput ).val(response.data.batchId);

        //date fields
        $( _DOMElements.checkInDate ).text(_prepareLocalDateStr(response.data.uploadedDate));
        if(!_.isEmpty(response.data.expiryDate)){
            $( _DOMElements.expiryDateInput ).val($.formatDate(new Date(response.data.expiryDate),dateFormat));
        }
        if(!_.isEmpty(response.data.startDate)){
            $( _DOMElements.startDateInput ).val($.formatDate(new Date(response.data.startDate),dateFormat));
        }
        if(!_.isEmpty(response.data.endDate)){
            $( _DOMElements.endDateInput ).val($.formatDate(new Date(response.data.endDate),dateFormat));
        }
        //dropdown fields
        $("#selectContainer_editDocumentSubCategory2").addClass('disable-dropdown');
        if(response.data.documentType === org.bt.modules.referenceData.SMSF ){

            org.bt.modules.docLibUtils.removeDropKick('editDocumentSubCategory');
            _initEditDocumentSubTypeDropdown();
            if(!_.isEmpty(response.data.documentSubType)){
                $( _DOMElements.documentSubCategoryInput ).dropkick('setValue',response.data.documentSubType);
            }
            if(response.data.documentSubType === 'SMSF Fund Administration'){
                org.bt.modules.docLibUtils.removeDropKick('editDocumentSubCategory2');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategory2Input,
                org.bt.modules.referenceData.SMSF_FADM_SUB_CATEGORIES,
                'selectDropDown');
                if(!_.isEmpty(response.data.documentSubType2)){
                    $( _DOMElements.documentSubCategory2Input ).dropkick('setValue',response.data.documentSubType2);
                }
            }
        } else if (response.data.documentType === org.bt.modules.referenceData.INVESTMENT ) {

            org.bt.modules.docLibUtils.removeDropKick('editDocumentSubCategory');
            org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategoryInput,
            org.bt.modules.referenceData.INVESTMENT_SUB_CATEGORIES,
            'selectDropDown');
            if(!_.isEmpty(response.data.documentSubType)){
                $( _DOMElements.documentSubCategoryInput ).dropkick('setValue',response.data.documentSubType);
            }
        } else {
            org.bt.modules.docLibUtils.removeDropKick('editDocumentSubCategory');
            org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategoryInput,
            [{text:'Any', value:'Any'}],
            'selectDropDown');
            $("#selectContainer_editDocumentSubCategory").addClass('disable-dropdown');
        }

         $( _DOMElements.relationshipTypeInput ).dropkick('setValue',response.data.relationshipType);
                _populateDocumentTitleField(response.data.documentType,response.data.documentSubType, response.data.documentTitleCode);

        $('.jq-editPrivacyFlag').attr("checked", (response.data.status == 'Draft' ? true : false));
        if(!_.isNull(response.data.financialYear)){
            $(_DOMElements.editFinancialYear ).dropkick('setValue',response.data.financialYear);
        }

        $(_DOMElements.auditFlag).attr("checked", response.data.audit);
        $(_DOMElements.softDeleteInput).attr("checked", response.data.softDeleted);
        $(_DOMElements.permanentEditInput).attr("checked", response.data.permanent);
        $("#selectContainer_documentVisibility").addClass('disable-dropdown');
        $( _DOMElements.documentActivityInput ).attr('disabled', true);

        $('.jsSubDocumentType.inputStyleAlignOne input').keyup(function() {
        $('#editDocumentSubCategory').trigger('change');
         });
        $(_DOMElements.documentSubCategoryInput).change(function(value){
            _populateSubCategory2Dropdown($(this).val());
        });

    };

    var _setSourceValue = function (c, v) {
        org.bt.modules.docLibUtils.removeDropKick('sourceIdDropdown');
        if(c === 'POBOX'){
            org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.sourceIdDropdown,
            org.bt.modules.referenceData.POBOX_SOURCES,
            'selectDropDown');

        } else {
            org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.sourceIdDropdown,
            org.bt.modules.referenceData.SOURCES,
            'selectDropDown');
        }
        if(!_.isEmpty(v) && v !== 'PanoramaUI' && v !== 'ServiceUI'){
            $( _DOMElements.sourceIdDropdown ).dropkick('setValue',v);
        } else {
            $( _DOMElements.sourceIdDropdown ).dropkick('setValue','Any');
        }
    };

    var _populateDocumentTitleField = function (category,subcategory, docTitleCode) {
        switch(category) {
            case org.bt.modules.referenceData.SMSF:
                if(!_.isEmpty(subcategory) && subcategory === 'SMSF Fund Administration'){
                        org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                        org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                        org.bt.modules.referenceData.SMSF_FUND_ADMINISTRATION_TITLE_CODES,
                        'selectDropDown');
                        if(!_.isEmpty(docTitleCode)) {
                            $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                        }
                        }
                else  if(!_.isEmpty(subcategory) && subcategory === 'SMSF Fund Establishment') {
                        org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                        org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                        org.bt.modules.referenceData.SMSF_FUND_ESTABLISHMENT_TITLE_CODES,
                        'selectDropDown');
                        if(!_.isEmpty(docTitleCode)) {
                             $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                        }
                  } else{
                        org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                        org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                        [{text:'Any', value:'Any'}],'selectDropDown');
                        $("#selectContainer_documentTitleCode").addClass('disable-dropdown');
                        }
                break;
            case org.bt.modules.referenceData.STATEMENT:
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                org.bt.modules.referenceData.STATEMENT_TITLE_CODES,
                'selectDropDown');
                if(!_.isEmpty(docTitleCode)) {
                    $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                }
                break;
            case org.bt.modules.referenceData.IMMODELRPT:
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                [{text:'Any', value:'Any'},{text:'IMMODELALL', value:'IMMODELALL'},{text:'IMMODEL', value:'IMMODEL'}],
                'selectDropDown');
                if(!_.isEmpty(docTitleCode)) {
                    $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                }
                break;
            case org.bt.modules.referenceData.INVESTMENT:
             if(!_.isEmpty(subcategory) && subcategory === 'Asset Transfers'){
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                org.bt.modules.referenceData.ASSET_TITLE_CODES,'selectDropDown');
                if(!_.isEmpty(docTitleCode)) {
                     $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                }
                }
                else{
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                [{text:'Any', value:'Any'}],'selectDropDown');
                $("#selectContainer_documentTitleCode").addClass('disable-dropdown');
                }
                break;

            case org.bt.modules.referenceData.APPROVAL:
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                org.bt.modules.referenceData.APPROVAL_TITLE_CODES,
                'selectDropDown');
                if(!_.isEmpty(docTitleCode)) {
                $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                }
                break;
            case org.bt.modules.referenceData.TAXSUPER:
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                org.bt.modules.referenceData.TAXSUPER__TITLE_CODES,
                'selectDropDown');
                if(!_.isEmpty(docTitleCode)) {
                    $( _DOMElements.documentTitleCodeInput ).dropkick('setValue',docTitleCode);
                }
                break;
            default:
                org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                [{text:'Any', value:'Any'}],
                'selectDropDown');
                $("#selectContainer_documentTitleCode").addClass('disable-dropdown');
                break;
            }
    };

    var _populateSourceIdField = function (category, sourceValue) {
        switch(category) {
            case 'ADVICE':
            case 'TAX':
            case 'CORRO':
            case 'OTHER':
            case org.bt.modules.referenceData.INVESTMENT:
            case org.bt.modules.referenceData.SMSF:
                $( _DOMElements.sourceIdInput ).addClass('noDisplay');           // hide the original source ID input
                $( '#selectContainer_sourceIdDropdown' ).removeClass('noDisplay');
                _setSourceValue(category, sourceValue); //Upload
                break;
            case 'EMAIL':
            case 'FAX':
            case 'SCANNED':
                $('#selectContainer_sourceIdDropdown').addClass('noDisplay');
                $( _DOMElements.sourceIdInput ).removeClass('noDisplay');
                $(_DOMElements.sourceIdInput ).val(sourceValue);
                break;
            case 'POBOX':
                $( _DOMElements.sourceIdInput ).addClass('noDisplay');           // hide the original source ID input
                $( '#selectContainer_sourceIdDropdown' ).removeClass('noDisplay');
                _setSourceValue(category, sourceValue);
                org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.sourceIdDropdown,org.bt.modules.referenceData.POBOX_SOURCES,'selectDropDown');
                break;
            default:
                $('#selectContainer_sourceIdDropdown').addClass('noDisplay');
                $( _DOMElements.sourceIdInput ).removeClass('noDisplay');
                $(_DOMElements.sourceIdInput ).val(sourceValue);
                $(_DOMElements.sourceIdInput ).attr('disabled', true);
                break;
        }

    };


    var _initEditDocumentSubTypeDropdown = function(){
    org.bt.modules.docLibUtils.attachOptions(_DOMElements.documentSubCategoryInput,
    org.bt.modules.referenceData.SMSF_SUB_CATEGORIES);
        $(_DOMElements.documentSubCategoryInput).dropkick({
            change: function (value, label) {
                $(this).change();
                _populateSubCategory2Dropdown(value);
            },
            inputClasses: ["selectDropDown"]
        });
    };

    var _populateSubCategory2Dropdown = function (value) {
        org.bt.modules.docLibUtils.removeDropKick('editDocumentSubCategory2');
        if(value === 'SMSF Fund Administration'){
            org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategory2Input,
            org.bt.modules.referenceData.SMSF_FADM_SUB_CATEGORIES,
            'selectDropDown');
              org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
              org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
              org.bt.modules.referenceData.SMSF_FUND_ADMINISTRATION_TITLE_CODES,
                            'selectDropDown');

        }
        else if(value === 'SMSF Fund Establishment')
        {
          org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                      org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                      org.bt.modules.referenceData.SMSF_FUND_ESTABLISHMENT_TITLE_CODES,
                                    'selectDropDown');

          org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategory2Input,
                      [{text:'Any', value:'Any'}],'selectDropDown');
                   $("#selectContainer_editDocumentSubCategory2").addClass('disable-dropdown');
        }
        else if(value === 'Asset Transfers')
        {
         org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                              org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                              org.bt.modules.referenceData.ASSET_TITLE_CODES,
                                            'selectDropDown');

                  org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategory2Input,
                              [{text:'Any', value:'Any'}],'selectDropDown');
                           $("#selectContainer_editDocumentSubCategory2").addClass('disable-dropdown');

        }
        else {

           org.bt.modules.docLibUtils.removeDropKick('documentTitleCode');
                         org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentTitleCodeInput,
                         [{text:'Any', value:'Any'}],'selectDropDown');
                         $("#selectContainer_documentTitleCode").addClass('disable-dropdown');
          org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.documentSubCategory2Input,
                [{text:'Any', value:'Any'}],
                'selectDropDown');
            $("#selectContainer_editDocumentSubCategory2").addClass('disable-dropdown');
        }
    };


    var _resetToDefault = function () {

        $( _DOMElements.abcOrderIdInput ).val('');
        $( _DOMElements.documentActivityInput ).val('');
        $( _DOMElements.externalIdInput ).val('');
        $( _DOMElements.batchIdInput ).val('');
        $( _DOMElements.sourceIdInput ).val('');
        $(_DOMElements.documentSubCategory2Input).dropkick('reset');
        $(_DOMElements.relationshipTypeInput).dropkick('reset');
        $(_DOMElements.documentTitleCodeInput).dropkick('reset');
        $(_DOMElements.auditFlag).attr('checked', false);
        $(_DOMElements.softDeleteInput).attr('checked', false);
        $(_DOMElements.permanentEditInput ).attr('checked', false);
    };


    var _init = function(){

        _bindDOMEvents();
        org.bt.modules.docLibUtils.initFinancialYearDropdown(_DOMElements.editFinancialYear);
        org.bt.modules.docLibUtils.initDropdownWithOptions(_DOMElements.relationshipTypeInput,
        org.bt.modules.referenceData.RELATIONSHIP_TYPES,
        'selectDropDown');
//        org.bt.modules.docLibUtils.initDropdown(_DOMElements.documentVisibilityInput, 'selectDropDown');
        org.bt.modules.docLibUtils.initDropdown(_DOMElements.documentSubCategoryInput, 'selectDropDown');
        org.bt.modules.docLibUtils.initDropdown(_DOMElements.documentSubCategory2Input, 'selectDropDown');
    };

 return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document, moment);