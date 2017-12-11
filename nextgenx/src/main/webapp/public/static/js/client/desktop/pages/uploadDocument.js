/**
 * @namespace org.bt.modules.uploadDocument
 */
org.bt.modules.uploadDocument =  (function($, window, document, sjcl) {

    var _DOMElements = {

        uploadDocumentType              :'.jq-uploadDocumentType',
        uploadFinancialYear             :'.jq-uploadFinancialYear',
        uploadDocumentAction            :'.jq-documentUploadAction',
        newVersionUploadAction          :'.jq-newDocVersionUpload',
        uploadDocumentPopupBlock        :'.jq-uploadDocumentPopupBlock',
        cancelUploadDocumentPopupBlock  :'.jq-cancelUploadDocumentPopupBlock',
        auditFlagInput                  :'.jq-auditFlagInput',
        uploadPrivacyFlag               :'.jq-uploadPrivacyFlag'

    };

     var _bindDOMEvents = function () {

        $("#fileInput").change (function (e) {
            e.preventDefault();
            var fileName = $(this).val().split('\\').pop();
            var file = document.getElementById('fileInput').files[0];
            if(file != undefined || file != null){
                $(".jq-SelectedDocumentSize").text(Math.round(file.size/1024)+'kb');
                $(".jq-SelectedDocumentName").text(fileName);
                $("#selectedDocument").val(fileName);
                $("#selectedDocumentInfo").removeClass("noDisplay");
                $(".jq-BrowseDocument").addClass("noDisplay");
                //display upload modal
                $('#disableLayer').fadeIn();
                $( _DOMElements.uploadDocumentPopupBlock).dialog( "open" );
            }
        });

        $(_DOMElements.uploadDocumentAction).click(function (e) {
            e.preventDefault();
            _hideUploadErrorMessage();
            org.bt.modules.docLibUtils.hideMessageBox();
            $("#fileInput").click();
//            org.bt.modules.docLibUtils.removeDropKick('documentSubType');
            if( $('#selectContainer_uploadDocumentType').length ){
                 if($("#selectContainer_uploadDocumentType").attr('class').indexOf('disable-dropdown') > -1) {
                    $("#selectContainer_uploadDocumentType").removeClass('disable-dropdown');
                 }
            }
            if($('#selectContainer_documentSubType').length) {
                 if($("#selectContainer_documentSubType").attr('class').indexOf('disable-dropdown') > -1) {
                    $("#selectContainer_documentSubType").removeClass('disable-dropdown');
                 }
            }
             if($('#selectContainer_documentTitleCodes').length) {
                 if($("#selectContainer_documentTitleCodes").attr('class').indexOf('disable-dropdown') > -1) {
                    $("#selectContainer_documentTitleCodes").removeClass('disable-dropdown');
                 }
            }
            $("#selectedDocumentId").val("");
            $("#abcOrderIdContainer").addClass('noDisplay');
            $("#externalIdContainer").addClass('noDisplay');
            $("#abcOrderIdContainer").val("");
            $("#externalIdContainer").val("");
            $(_DOMElements.auditFlagInput).attr('checked', false);
            $(_DOMElements.uploadPrivacyFlag).attr('checked', false);
            $('#documentSubTypeContainer').addClass('noDisplay');
            $('#docTitleCodesContainer').addClass('noDisplay');
            $('#docTitleCodesContainerForFA').addClass('noDisplay');
            $(_DOMElements.uploadDocumentType).dropkick('reset');
            $(_DOMElements.uploadFinancialYear).dropkick('reset');
            $("#documentSubSubTypeContainer").addClass('noDisplay');
            $("#documentSubSubType").dropkick('reset');

        $('.jsUploadDocumentType.inputStyleAlignOne input').keyup(function() {
        $('#uploadDocumentType').trigger('change');
        });
        $(_DOMElements.uploadDocumentType).change(function(value){
         _loadSubCategories($(this).val());
        });
        });

        $(_DOMElements.newVersionUploadAction).click(function (e) {
            e.preventDefault();
            _hideUploadErrorMessage();
            $("#fileInput").click();
            var selectedDocumentType = $(this).attr('data-info');
            //getting three values as data-info tag categories[0]-Document Type,
            //categories[1]-Document Sub Type and categories[2]-document Title Code
            var categories = selectedDocumentType.split('-');
            $(_DOMElements.uploadDocumentType).dropkick('setValue',categories[0]); //categories[0]-Document Type
            $("#selectedDocumentId").val($(this).attr('data-id'));
            $("#selectContainer_uploadDocumentType").addClass('disable-dropdown');
            if(!_.isEmpty(categories[1] )){
                org.bt.modules.docLibUtils.removeDropKick('documentSubType');
                org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentSubType',
                    [{text:categories[1], value:categories[1]}],
                    'inputStyleFive');// categories[1]-Document Sub Type if exist
                $("#selectContainer_documentSubType").addClass('disable-dropdown');
            }
            if (org.bt.modules.referenceData.STATEMENT === categories[0]) {
                //categories[2]-Document Title Code for statement only
                $('.jq-documentTitleCodes').dropkick('setValue',categories[2]);
                $("#selectContainer_documentTitleCodes").addClass('disable-dropdown');
            }
            $(_DOMElements.auditFlagInput).attr('checked', false);
            $(_DOMElements.uploadPrivacyFlag).attr('checked', false);
        });

        $('.jq-documentUploadBtn').click(function (e){
            e.preventDefault();
            _hideUploadErrorMessage();
            //file validation
            if(_validateFileMetaData()){
             //form data object
                if(!$('.jq-documentUploadBtn').hasClass('primaryButtonDisabled')) {
                    $('.jq-documentUploadBtn').addClass('primaryButtonDisabled');
                   _uploadDocument();
                }
            }
        });

     };

    var _uploadDocument = function () {
        var formData = new FormData();//document.querySelector("uploadForm")
        var filename = $('#selectedDocument').val();
        var selectedDocId = $('#selectedDocumentId').val();
        var file = document.getElementById('fileInput').files[0];
        var year = org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadFinancialYear');
        var size = file.size;
        $('.jq-cancelUploadDocumentPopupBlock').addClass('inactiveLink');
        $('.modalClose').addClass('inactiveLink');
        formData.append("status",($(_DOMElements.uploadPrivacyFlag ).is(":checked") ? 'Draft' : 'Final'));
        formData.append('audit', $(_DOMElements.auditFlagInput).is(":checked"));
        formData.append('documentName', filename);
        if(year !== undefined && null != year){
            formData.append("financialYear", org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadFinancialYear'));
        }
        formData.append('docupload',file);
        formData.append('key.accountId',$('#accountId').val());
        formData.append('relationshipType', $('.jq-relationshipTypeFilter').val())
        if(selectedDocId !== undefined && null != selectedDocId && selectedDocId !== ""){
            formData.append('key.documentId', selectedDocId);
        } else {
            formData.append('documentType', org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'));
            if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubType'))){
                formData.append('documentSubType',org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubType'));
            }
            if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'))){
                formData.append('documentTitleCode',org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'));
            }
            if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodesForFA'))){
                formData.append('documentTitleCode',org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodesForFA'));
            }
            if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubSubType'))){
                formData.append('documentSubType2',org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubSubType'));
            }
            if(!_.isEmpty($('.jq-uploadExternalIdInput').val())){
                formData.append("externalId", $('.jq-uploadExternalIdInput').val());
            }
            if(!_.isEmpty($('.jq-updateAbcOrderIdInput').val())){
                formData.append("abcOrderId", $('.jq-updateAbcOrderIdInput').val());
            }
        }
        var pathname = window.location.pathname; // Returns path only
        var path = pathname.split('/page');
        var action =path[0] + '/upload';
        //submission
        $.ajax({
          url: action,
          type: 'POST',
          beforeSend: function(request) {
                // generate random token
                var passKey = $('#accountId').val(),
                    data = formData || action,
                    key = sjcl.codec.utf8String.toBits(passKey),
                    out = (new sjcl.misc.hmac(key, sjcl.hash.sha256)).mac(data),
                    token = encodeURIComponent(sjcl.codec.hex.fromBits(out));
                $.cookie.raw = true;
                $.cookie('securityToken', token, {
                      path: '/',
                      secure: window.location.protocol === 'https:'
                });
                $.cookie.raw = false;
                request.setRequestHeader('securityToken', token);
          },
          data: formData,
          processData: false,  // tell jQuery not to process the data
          contentType: false,   // tell jQuery not to set contentType
          success :function(res){

               if(res.status === 1 && res.data.warnings.length === 0){
                    $('.jq-documentUploadBtn').removeClass('primaryButtonDisabled');
                    $('.jq-cancelUploadDocumentPopupBlock').removeClass('inactiveLink');
                    $('.modalClose').removeClass('inactiveLink');
                    $( _DOMElements.uploadDocumentPopupBlock).dialog( 'close' );
                    org.bt.modules.docLibUtils.showMessageBox('Document uploaded successfully.', 'successBox');
                    $("html, body").animate({scrollTop: 0}, 0);
                    $('.jq-formSubmit').trigger('click').delay(1000);
               } else {
                    $('.jq-documentUploadBtn').removeClass('primaryButtonDisabled');
                    $('.jq-cancelUploadDocumentPopupBlock').removeClass('inactiveLink');
                    $('.modalClose').removeClass('inactiveLink');
                    $( _DOMElements.uploadDocumentPopupBlock).dialog( 'close' );
                    if(res.data.warnings.length > 0 && res.data.warnings[0].message === 'Err.IP-0460') {
                        org.bt.modules.docLibUtils.showMessageBox('Filename already exists. Please choose a new filename or upload as a new version.', 'warningBox');
                    } else {
                        org.bt.modules.docLibUtils.showMessageBox('Could not upload document. Please try later.', 'warningBox');
                    }
                    $("html, body").animate({scrollTop: 0}, 0);
               }
            },
            error:function(){
                $('.jq-documentUploadBtn').removeClass('primaryButtonDisabled');
                $('.jq-cancelUploadDocumentPopupBlock').removeClass('inactiveLink');
                $('.modalClose').removeClass('inactiveLink');
                $( _DOMElements.uploadDocumentPopupBlock).dialog( 'close' );
                org.bt.modules.docLibUtils.showMessageBox('Could not upload document. Please try later.', 'warningBox');
                $("html, body").animate({scrollTop: 0}, 0);
            }
        });
     };


    var _validateFileMetaData = function () {
        var filename = $('#selectedDocument').val();
        var file = document.getElementById('fileInput').files[0];
        //fileSize should be greater then 20Mb
        if(file == undefined || file == null){
            _showUploadErrorMessage('Please select a document.');
            return false;
        } else {
            if (file.size > 20000000) {
                _showUploadErrorMessage('Selected document can\'t be uploaded (Exceeds file size limit 20 MB.');
                return false;
            }
            if (filename.length > 200) {
                _showUploadErrorMessage('Selected document can\'t be uploaded (File name too long).');
                return false;
            }
            if(_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'))) {
                _showUploadErrorMessage('Please select document type.');
                return false;
            }
            if(!$('#docTitleCodesContainer').hasClass('noDisplay')
                && _.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'))&&(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'))==='STM') {
                _showUploadErrorMessage('Please select document title code.');
                return false;
            }
             if(!$('#docTitleCodesContainer').hasClass('noDisplay')
                && _.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'))&&(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'))==='INV'
                &&(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubType'))==='Asset Transfers' ) {
                _showUploadErrorMessage('Please select document title code.');
                return false;
            }

            if(!$('#docTitleCodesContainer').hasClass('noDisplay')
                && _.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'))&&(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'))==='TAXSUPER') {
                _showUploadErrorMessage('Please select document title code.');
                return false;
            }

             if(!$('#docTitleCodesContainer').hasClass('noDisplay')
                           && _.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTitleCodes'))&&(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadDocumentType'))==='APPROVAL') {
                           _showUploadErrorMessage('Please select document title code.');
                           return false;
            }

            var fileExt = filename.substr(filename.lastIndexOf('.') + 1).toLowerCase();
            var allowedFiles = ['doc', 'docx', 'pdf', 'jpg', 'msg', 'png', 'ppt', 'pptx', 'tif', 'xls', 'xlsx', 'csv'];
            if (allowedFiles.indexOf(fileExt) === -1) {
                _showUploadErrorMessage('Selected document can\'t be uploaded (Invalid extension).');
                return false;
            }
        }

        return true;
    };


    var _showUploadErrorMessage = function (msg) {
        $('.jq-uploadMessageBox .jq-uploadMessage').text(msg);
        $('.jq-uploadMessageBox').removeClass("noDisplay");
    };

    var _hideUploadErrorMessage = function () {
        if($('.jq-uploadMessageBox').attr('class').indexOf('noDisplay') === -1) {
            $('.jq-uploadMessageBox').addClass("noDisplay");
        }
    };

    var _initUploadDocumentTypeDropdown = function(){
        $(_DOMElements.uploadDocumentType).dropkick({
            change: function (value, label) {
                $(this).change();
            },
            inputClasses: ["selectDropDown"]
        });
    };


    var _initUploadDocumentSubTypeDropdown = function(){
        $('.jq-documentSubType').dropkick({
            change: function (value, label) {
                $(this).change();
                _displayDependentFields(value);
            },
            inputClasses: ["inputStyleFive"]
        });
    };

    var _displayDependentFields = function (value) {

        if(value === 'SMSF Fund Establishment'){
            $('#abcOrderIdContainer').addClass('noDisplay');
            $('#updateExternalId').val("");
            $('#externalIdContainer').removeClass('noDisplay');
              $('#docTitleCodesContainer').removeClass('noDisplay');
              $('#docTitleCodesContainerForFA').addClass('noDisplay');
              $('#documentTitleCodes').dropkick('reset');
              //$('#documentTitleCodes').dropkick('reload',{inputClasses:['inputStyleFive'],container:org.bt.modules.referenceData.SMSF_DOCUMENT_TITLE_CODES});
              org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodes',
              org.bt.modules.referenceData.SMSF_FUND_ESTABLISHMENT_TITLE_CODES,
              'inputStyleFive');
               $('#documentSubSubTypeContainer').addClass('noDisplay');

        }
        else if(value==='SMSF Fund Administration')
        {
         $('#abcOrderIdContainer').addClass('noDisplay');
         $('#updateExternalId').val("");
         $('#externalIdContainer').removeClass('noDisplay');
          $('#docTitleCodesContainer').addClass('noDisplay');
         $('#docTitleCodesContainerForFA').removeClass('noDisplay');
         $('#documentTitleCodesForFA').dropkick('reset');
         //$('#documentTitleCodes').dropkick('reload',{inputClasses:['inputStyleFive'],container:org.bt.modules.referenceData.SMSF_DOCUMENT_TITLE_CODES});
                       org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodesForFA',
                       org.bt.modules.referenceData.SMSF_FUND_ADMINISTRATION_TITLE_CODES,
                       'inputStyleFive');
         $('#documentSubSubTypeContainer').removeClass('noDisplay');
           org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentSubSubType',
                       org.bt.modules.referenceData.SMSF_FADM_SUB_CATEGORIES,
                       'inputStyleFive');

        }
        else if(value==='Company'||value==='SMSF General'){
        $('#abcOrderIdContainer').addClass('noDisplay');
        $('#updateExternalId').val("");
        $('#externalIdContainer').removeClass('noDisplay');
        $('#docTitleCodesContainer').addClass('noDisplay');
        $('#docTitleCodesContainerForFA').addClass('noDisplay');
        $('#documentSubSubTypeContainer').addClass('noDisplay');
        }

        else if(value==='Asset Transfers'){
        $('#abcOrderIdContainer').addClass('noDisplay');
         $('#externalIdContainer').addClass('noDisplay');
         $('#docTitleCodesContainer').removeClass('noDisplay');
         $('#docTitleCodesContainerForFA').addClass('noDisplay');
         $('#documentSubSubTypeContainer').addClass('noDisplay');
          org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodes',
          org.bt.modules.referenceData.ASSET_TITLE_CODES,'inputStyleFive');
        }

        else {
            $('#abcOrderIdContainer').addClass('noDisplay');
            $('#externalIdContainer').addClass('noDisplay');
            $('#docTitleCodesContainer').addClass('noDisplay');
            $('#docTitleCodesContainerForFA').addClass('noDisplay');
            $('#documentSubSubTypeContainer').addClass('noDisplay');
        }
    };

    var _loadSubCategories = function (value) {

        org.bt.modules.docLibUtils.removeDropKick('documentSubType');
        org.bt.modules.docLibUtils.removeDropKick('documentTitleCodes');
        $('#documentSubTypeContainer').addClass('noDisplay');
        $('#docTitleCodesContainer').addClass('noDisplay');
        $('#abcOrderIdContainer').addClass('noDisplay');
        $('#externalIdContainer').addClass('noDisplay');
        $('#documentSubSubTypeContainer').addClass('noDisplay');

        if(value === org.bt.modules.referenceData.TAXSUPER){
            $('#docTitleCodesContainer').removeClass('noDisplay');
            org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodes',
                org.bt.modules.referenceData.TAXSUPER__TITLE_CODES,
                'inputStyleFive');
        }else
        if(value === org.bt.modules.referenceData.SMSF ){
           $('#documentSubTypeContainer').removeClass('noDisplay');
            org.bt.modules.docLibUtils.attachOptions('.jq-documentSubType',
            org.bt.modules.referenceData.SMSF_SUB_CATEGORIES);
            _initUploadDocumentSubTypeDropdown();

        } else if(value === org.bt.modules.referenceData.INVESTMENT ){

            $('#documentSubTypeContainer').removeClass('noDisplay');
            org.bt.modules.docLibUtils.attachOptions('.jq-documentSubType',
            org.bt.modules.referenceData.INVESTMENT_SUB_CATEGORIES);
            _initUploadDocumentSubTypeDropdown();

        } else if(value === org.bt.modules.referenceData.STATEMENT ){

              $('#docTitleCodesContainer').removeClass('noDisplay');
              org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodes',
              org.bt.modules.referenceData.STATEMENT_TITLE_CODES,
              'inputStyleFive');
        }
        else if(value === org.bt.modules.referenceData.APPROVAL){
        $('#docTitleCodesContainer').removeClass('noDisplay');
                      org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentTitleCodes',
                      org.bt.modules.referenceData.APPROVAL_TITLE_CODES,
                      'inputStyleFive');
        }

         $('.jsDocumentSubType.inputStyleAlignOne input').keyup(function(){
                  $('#documentSubType').trigger('change');
                });
                $('#documentSubType').change(function(value){
                   _displayDependentFields($(this).val());
                });
    }


    var _init = function () {
        _bindDOMEvents();
        org.bt.modules.docLibUtils.initFinancialYearDropdown(_DOMElements.uploadFinancialYear);
        if(!_.isNull($('.jq-relationshipTypeFilter').val()) && $('.jq-relationshipTypeFilter').val() == 'CUST' ){
            org.bt.modules.docLibUtils.attachOptions(_DOMElements.uploadDocumentType,
                org.bt.modules.referenceData.CUSTOMER_CATEGORIES);
        } else {
            org.bt.modules.docLibUtils.attachOptions(_DOMElements.uploadDocumentType,
                org.bt.modules.referenceData.DOCUMENT_CATEGORIES);
        }
        _initUploadDocumentTypeDropdown();
    };

 return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document, sjcl);