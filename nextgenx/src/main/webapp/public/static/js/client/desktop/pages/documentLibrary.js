/**
 * @namespace org.bt.modules.documentLibrary
 */
org.bt.modules.documentLibrary = (function($,window,document,moment) {

    var _DOMElements = {
            searchFromDatePickerHolder      :'.jq-SearchFromDateCalendarPlaceHolder',
            searchToDatePickerHolder        :'.jq-SearchToDateCalendarPlaceHolder',
            searchFromDate                  :'.jq-SearchFromDate',
            searchToDate                    :'.jq-SearchToDate',
            documentResultTable             :'.jq-documentResultTable',
            bglDocumentTypePeriod           :'.jq-documentTypeInput',
            searchFinancialYearPeriod       :'.jq-searchFinancialYear',
            bglUploadedByPeriod             :'.jq-uploadedBy',
            newVersionUploadAction          :'.jq-NewDocVersionUpload',
            uploadDocumentStatus            :'.jq-documentStatus',
            editSDocumentStatus             :'.jq-editSDocumentStatus',
            searchForm                      :'.jq-documentSearchForm',
            loaderIcon                      :'.jq-loaderIcon',
            cancelDocumentMetadataPopupBlock:'.jq-cancelDocumentMetadataPopupBlock',
            editDocumentMetadataPopupBlock  :'.jq-editDocumentMetadataPopupBlock',
            uploadDocumentPopupBlock        :'.jq-uploadDocumentPopupBlock',
            cancelUploadDocumentPopupBlock  :'.jq-cancelUploadDocumentPopupBlock',
            documentTypeFilter              :'.jq-documentTypeFilter',
            financialYearFilter             :'.jq-financialYearFilter',
            uploadByFilter                  :'.jq-uploadByFilter',
            dateFromFilter                  :'.jq-dateFromFilter',
            dateToFilter                    :'.jq-dateToFilter',
            cancelDeleteDocumentBtn         :'.jq-cancelDeleteDocument',
            confirmationPopup               :'.jq-confirmationPopup',
            documentStatusFilterInput       :'.jq-documentStatusFilterInput',
            documentStatusFilter            :'.jq-documentStatusFilter',
            auditFlagFilter                 :'.jq-isAuditFilter',
            auditFlag                       :'.jq-auditFilterFlag',
            softDeletedFilter               :'.jq-softDeletedFilter',
            documentSubTypeFilter           :'.jq-documentSubTypeFilter',
            documentSubSubTypeFilter        :'.jq-documentSubSubTypeFilter'
    };

    var dateFormat = 'dd mmm yyyy',
		numDocs = new Array();

    var _bindDOMEvents = function(){

        _initDatePickers();
        //submit form on enter event
        $(_DOMElements.searchForm).keypress(function (e) {
            var c = e.which ? e.which : e.keyCode;
            if (c === 13) {
                $('.jq-formSubmit').trigger('click');
                e.preventDefault();
            }
        });

        $('.jq-formClear').click(function (e) {
            e.preventDefault();
            _resetToDefault();
        });

        $('img#documentVersionsImg').on('click', function () {
            org.bt.modules.docLibUtils.hideMessageBox();
            _hideUploadErrorMessage();
            var imgTag  = ($(this));

            if (imgTag.attr('src').indexOf('icon-collapse-minus') === -1) {
                var tr = $(this).closest('tr');
                var index = tr.index();
                var url = ($(this).attr('href'));
                imgTag.attr('src',"/ng/public/static/images/icon-collapse-minus.gif");
                var documentId = (RegExp('docId' + '=' + '(.+?)(&|$)').exec(imgTag.parent().attr('href'))||[,null])[1];
                var action = 'secure/page/serviceOps/documents/versions/'+documentId+'';
				
                org.bt.utils.communicate.ajax({
                    url:action,
                    type:'GET',
                    data:null,
					async:false,
                    onSuccess:function(res){
						$('#documentResultTable > tbody > tr:eq(' + tr.index() + ')').after(_getVersionTable(res));
                        jsonData = res.data.resultList,
                        numDocs.push(jsonData.length);
                    },
                    onError:function(){
                        org.bt.modules.docLibUtils.showMessageBox('Could not load document versions. Please try later.', 'warningBox');
                        $("html, body").animate({scrollTop: 0}, 0);
                    }
                });
            } else {
                var currIndex = parseInt(imgTag.parent().parent().parent().index()) + 1;
				if ($('#documentResultTable > tbody > tr:eq(' + currIndex+ ')').hasClass("grayBack")) {
					if (numDocs[currIndex-1] > 1) {
                        for (var index = 1; index < numDocs[currIndex-1]; index++) {
                            $('#documentResultTable > tbody > tr:eq(' + currIndex+ ')').remove();
                        }
                    }
                    else {
                        $('#documentResultTable > tbody > tr:eq(' + currIndex+ ')').remove();
                    }
				}
				imgTag.attr('src',"/ng/public/static/images/icon_expand_plus.gif");
            }

        } );

        $('.jq-deleteDocument').click(function (e){
            e.preventDefault();
            var id = $(this).attr('data-id');
            $('#selectedDocumentId').val(id);
            $('.jq-confirmationPopup').dialog( 'open' );
        });

        $('.jq-confirmDeletion').click(function (e){
            e.preventDefault();
            $('.jq-confirmationPopup').dialog( 'close' );
            _deleteDocument($('#selectedDocumentId').val());
        });

        $(_DOMElements.searchForm).validationEngine({
            ajaxSubmit:true,
            ajaxSubmitType : 'GET',
            ajaxValidationUrl   :org.bt.utils.serviceDirectory.validate,
            ajaxSubmitUrl       :null,
            dataType            :'json',
            onValidationComplete:function(form,success){
            $('.jq-formSubmit').addClass('actionButtonIconDisabled');
                if(success){
                    var data = _buildSearchCriteria();
                    var action = 'secure/page/serviceOps/account/'+$('#accountId').val()+'/documents';
                    org.bt.utils.communicate.get({action:action,data:data});
                }
            },
            ajaxLoaderSettings 	:{
                showLoader:true,
                replaceClass:'iconsearch',
                iconLoaderClass:'iconLoader'
            },
            onSubmitSuccess     :function(response){
                $('.jq-formSubmit').removeClass('actionButtonIconDisabled');
             },
            onSubmitError       :function(response){
                $('.jq-formSubmit').removeClass('actionButtonIconDisabled');
            },
            customFunctions:{
                'accountNumber':function(rules,value){
                    matched = /^\d+$/.test(value);
                    rules.push({'name':'unsignedInteger','matched':matched});
                    return rules;
                }
            }
       });
    };

    var _deleteDocument = function (docId) {
        $('.jq-confirmDeletion').addClass('primaryButtonDisabled');
        org.bt.modules.docLibUtils.hideMessageBox();
        var action = 'secure/page/serviceOps/document/delete/'+docId;
        org.bt.utils.communicate.ajax({
            url:action,
            type:'GET',
            data:null,
            onSuccess:function(res){
                if(res.success){
                    $('.jq-confirmDeletion').removeClass('primaryButtonDisabled');
                    org.bt.modules.docLibUtils.showMessageBox('Document successfully deleted.', 'successBox');
                    $("html, body").animate({scrollTop: 0}, 0);
                    $('.jq-formSubmit').trigger('click').delay(1000);
                } else {
                    $('.jq-confirmDeletion').removeClass('primaryButtonDisabled');
                    org.bt.modules.docLibUtils.showMessageBox('Could not delete document. Please try later.', 'warningBox');
                    $("html, body").animate({scrollTop: 0}, 0);
                }
            },
            onError:function(){
                $('.jq-confirmDeletion').removeClass('primaryButtonDisabled');
                org.bt.modules.docLibUtils.showMessageBox('Could not delete document. Please try later.', 'warningBox');
                $("html, body").animate({scrollTop: 0}, 0);
            }
        });
    };
	/**
     *  Init Date pickers for Portfolio, failedapp Summary
     */
    var _initDatePickers = function(){
		//date field date picker - failedapp Summary From - starts
		$(_DOMElements.searchFromDatePickerHolder).datepicker({
			changeMonth: true,
	        changeYear: true,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $(_DOMElements.searchFromDate)
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');

		$(_DOMElements.searchFromDatePickerHolder).datepicker("setDate", new Date($(_DOMElements.searchFromDate).val()));

		$('.jq-failedappDownloadFromDateCalendarIcon').click(_onDateCalendarISFIconClick);
		

		//date field date picker - failedapp Summary To - starts
		$(_DOMElements.searchToDatePickerHolder).datepicker({
			changeMonth: true,
	        changeYear: true,
	        onSelect: function(dateText, inst) {
	            var selectedDate    = new Date(inst.selectedYear,inst.selectedMonth,inst.selectedDay);
	            $(_DOMElements.searchToDate)
	                .val($.formatDate(selectedDate,dateFormat))
	                .trigger('focusout');
	            $(this).addClass('noDisplay');
	        }
	    }).addClass('noDisplay');

		$(_DOMElements.searchToDatePickerHolder).datepicker("setDate", new Date($(_DOMElements.searchToDate).val()));

		$('.jq-failedappDownloadToDateCalendarIcon').click(_onDateCalendarISTIconClick);

        //hide date picker on window click
        $(document).click(function(e){
            var target              = $(e.target),
                $dateCalendar       = $(_DOMElements.portfolioDatePickerHolder),
            	$dateCalendarFrom   = $(_DOMElements.searchFromDatePickerHolder),
            	$dateCalendarTo     = $(_DOMElements.searchToDatePickerHolder);

            if(!target.hasClass('jq-failedappDownloadFromDateCalendarIcon')){
                if(target.parents().filter('.jq-failedappDownloadFromDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarFrom.datepicker().addClass('noDisplay');
                }
            }

            if(!target.hasClass('jq-failedappDownloadToDateCalendarIcon')){
                if(target.parents().filter('.jq-failedappDownloadToDateCalendarIcon, .date-picker, .calendar, .ui-datepicker-header').length === 0){
                    $dateCalendarTo.datepicker().addClass('noDisplay');
                }
            }
        });
    };

   var _onDateCalendarISFIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.searchFromDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };

    var _onDateCalendarISTIconClick = function(e){
        e.preventDefault();
        var $placeholder    = $(_DOMElements.searchToDatePickerHolder),
            toggle          = ($placeholder.hasClass('noDisplay')) ? $placeholder.removeClass('noDisplay').find('select:first').focus() : $placeholder.addClass('noDisplay');
    };

    var _hideUploadErrorMessage = function () {
        if($(".jq-uploadMessageBox").attr('class').indexOf('noDisplay') === -1) {
            $('.jq-uploadMessageBox').addClass("noDisplay");
        }
    };

    var _buildSearchCriteria = function () {

        var jsonData = [];
        var documentType = org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentTypeInput');
        var financialYear = org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-searchFinancialYear');
        var uploadedBy = org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-uploadedBy');
        var uploadedFromDate ,uploadedToDate;
        if($(_DOMElements.searchFromDate).val() !== ''){
            uploadedFromDate =new Date($(_DOMElements.searchFromDate).val());
        }
        if($(_DOMElements.searchToDate).val() !== ''){
            uploadedToDate =new Date($(_DOMElements.searchToDate).val());
            uploadedToDate.setDate(uploadedToDate.getDate() + 1);
        }
        if(!_.isEmpty(documentType)){
            jsonData.push({name:'documentType',value:documentType});
        }
        if(!_.isEmpty( financialYear )){
            jsonData.push({name:'financialYear',value:financialYear});
        }
        if(!_.isEmpty(uploadedBy)){
            jsonData.push({name:'uploadedBy',value:uploadedBy});
        }
        if(undefined != uploadedFromDate && null != uploadedFromDate){
            jsonData.push({name:'fromDate',value:uploadedFromDate.toISOString()});
        }
        if(undefined != uploadedToDate && null != uploadedToDate){
            jsonData.push({name:'toDate',value:uploadedToDate.toISOString()});
        }
        if(!_.isEmpty($('.jq-relationshipTypeFilter').val()) ){
            jsonData.push({name:'relationshipType',value:$('.jq-relationshipTypeFilter').val()});
        }
        if($('.jq-privacyFlag' ).is(":checked") ){
            jsonData.push({name:'documentStatus',value:'Draft'});
        }
        if($( '.jq-auditFilterFlag' ).is(":checked")){
            jsonData.push({name:'auditFlag',value:$( '.jq-auditFilterFlag' ).is(":checked")});
        }
        if(!_.isEmpty($('.jq-documentNameFilter').val())){
            jsonData.push({name:'nameSearchToken',value:$( '.jq-documentNameFilter' ).val()});
        }
        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubCategory'))){
            jsonData.push({name:'documentSubType',value:$( '.jq-documentSubCategory' ).val()});
        }
        if(!_.isEmpty(org.bt.modules.docLibUtils.checkSelectedDropdownValue('.jq-documentSubSubCategory'))){
            jsonData.push({name:'documentSubSubType',value:$( '.jq-documentSubSubCategory' ).val()});
        }
        if($( '.jq-softDeletedFlag' ).is(":checked")){
            jsonData.push({name:'softDeleted',value:$( '.jq-softDeletedFlag' ).is(":checked")});
        }

        jsonData.push({name:'name',value:$( '#nameFilter' ).val()});

        return jsonData;
    };

    /* Formatting function for row details - modify as you need */
    var _getVersionTable = function ( response ) {
        // `d` is the original data object for the row
        var dataHtml, trHtml, deleteLink, endTr;
        var emptyStr = '';
        var imgPath = _buildImgUrl('/ng/public/static/images/doc_document.png');
        if(response.status === 1 && !_.isNull(response.data)){
            dataHtml = '<tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg"><td class="dataTableCell nameWrap" colspan="4">'+
                               '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
            var jsonData = response.data.resultList;
            if(jsonData.length > 1){
                for (var i = 1 ; i < jsonData.length ; ++i) {
                     var formatted = $.datepicker.formatDate("M d, yy", new Date(jsonData[i].uploadedDate));
                     var downloadUrl = _buildDownloadUrl(jsonData[i].key.documentId);
                     trHtml = trHtml +   '<tr class="grayBack dataTableRow dataTableRowActiveMod2 dataTableRowBg">'+
                        '<td class="dataTableCell nameWrap" style="width:50%" >'+
                        '<img id="DocumentImg" src="'+imgPath+'" width="15" height="18">'+
                        '<strong> '+jsonData[i].documentName+'</strong>  '+
                        jsonData[i].status+ '</br>'+jsonData[i].documentTypeLabel +''+
                        (!_.isEmpty(jsonData[i].documentSubType) ? ' > '+jsonData[i].documentSubType : '' )+
                        ' - '+Math.round(jsonData[i].size/1024)+ 'kb</br>'+
                        (!_.isEmpty(jsonData[i].financialYear) ? jsonData[i].financialYear : emptyStr )
                        +'</td>'+
                        '<td class="dataTableCell nameWrap" style="width:10%" >'+formatted +'</td>'+
                        '<td class="dataTableCell nameWrap" style="width:20%" >'
                        +(_.isEmpty($.trim(jsonData[i].addedByName)) ? '' : jsonData[i].addedByName +' </br> ' )+
                        (!_.isEmpty(jsonData[i].uploadedBy) ? jsonData[i].uploadedBy + ' - ' : '') + jsonData[i].uploadedRole+'</td>'+
                        '<td class="dataTableCell nameWrap" style="width:15%" >'+
                        '<a href="'+downloadUrl+'">'+
                        '<em class="icon-document-download" title="Download document"></em></a>';

                    if(_isDeletableCategory(jsonData[i].documentType)) {
                         trHtml = trHtml + '<a href="#nogo" class="jq-deleteDocument" data-id="'+jsonData[i].key.documentId+'">'+
                              '<em class="icon-trash" title="Delete version"></em>'+
                              '</a>';
                     }
                     trHtml = trHtml + '</td></tr>';

                     $(document).on('click', "a.jq-deleteDocument", function() {
                        var id = $(this).attr("data-id");
                        $('#selectedDocumentId').val(id);
                        $('.jq-confirmationPopup').dialog( 'open' );
                     });
                }
            }else {
                return emptyStr;
            }
        }
        else {
             return emptyStr;
        }
        dataHtml = trHtml + '</table></td></tr>';
        return dataHtml;
    };

    var _buildDownloadUrl = function (docId ) {
        var pathname = window.location.pathname; // Returns path only
        var path = pathname.split('/page');
        return path[0] + '/page/serviceOps/documents/'+docId;
    };

    var _buildImgUrl = function (imgUrl ) {
        var pathname = window.location.pathname; // Returns path only
        var path = pathname.split('/ng');
        return path[0] + imgUrl;
    };

    var _isDeletableCategory = function (category) {
        var isDeletable = true;
        $.each(org.bt.modules.referenceData.CUSTOMER_CATEGORIES, function (i, item) {
            if(category === item.value) {
                isDeletable =  false;
            }
        });
        return isDeletable;
    };

    var _clearErrorMsgs = function(){
        //Remove errors if any
        $.removeMessage($('.jq-accountNumber'),['jq-inputError','formFieldMessageError'],'textInputError');
    };

    var _clearSelectedFileObject = function () {
        //clearing selected file entry
        var fileControl = $("#fileInput");
        fileControl.replaceWith( fileControl = fileControl.clone(true) );
    };


    var _initDocumentTypeDropdown = function(){
        $(_DOMElements.bglDocumentTypePeriod).dropkick({
            change: function (value, label) {
                $(this).change();
                 _loadSubCategories($(this).val());
            },
            inputClasses: ["inputStyleFive"]
        });
    };


    var _loadSubCategories = function (value) {

        org.bt.modules.docLibUtils.removeDropKick('documentSubCategory');
        $('#documentSubCategoryContainer').addClass('noDisplay');
        $('#documentSubSubCategoryContainer').addClass('noDisplay');
        if(value === org.bt.modules.referenceData.SMSF ){
           $('#documentSubCategoryContainer').removeClass('noDisplay');
           org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentSubCategory',
           org.bt.modules.referenceData.SMSF_SUB_CATEGORIES,
            'inputStyleFive');

        } else if(value === org.bt.modules.referenceData.INVESTMENT ){

            $('#documentSubCategoryContainer').removeClass('noDisplay');
            org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentSubCategory',
            org.bt.modules.referenceData.INVESTMENT_SUB_CATEGORIES,
            'inputStyleFive');

        }

         $('.jsSubDocumentType.inputStyleAlignOne input').keyup(function() {
         $('#documentSubCategory').trigger('change');
         });

         $('#documentSubCategory').change(function(value){
         _loadSubSubCategories($(this).val());
         });
    }

    var _loadSubSubCategories = function (value) {

             org.bt.modules.docLibUtils.removeDropKick('documentSubSubCategory');
             $('#documentSubSubCategoryContainer').addClass('noDisplay');
             if(value === org.bt.modules.referenceData.SMSF_FUND_ADMINISTRATION ){
             $('#documentSubSubCategoryContainer').removeClass('noDisplay');
             org.bt.modules.docLibUtils.initDropdownWithOptions('.jq-documentSubSubCategory',
             org.bt.modules.referenceData.SMSF_FADM_SUB_CATEGORIES,
             'inputStyleFive');
             }
             }

    /**
     * Init modal
     * @private
     */
    var _initModalDialog = function(dialogWindowElement, dialogCancelBtn, w){
        var $dialogWindowElement    = $(dialogWindowElement);

        $dialogWindowElement.dialog({
             modal: false,
             autoOpen: false,
             width:w,
             height:'auto',
             draggable:false,
             dialogClass: 'modalBox',
             resizable: false,
             zIndex: 1002,
             title:'',
             appendTo: '.layoutContent',
             close:function(){
                 _clearSelectedFileObject();
                 $('#disableLayer').fadeOut();
            	 $('.ui-widget-overlay').css('position', 'absolute');
             },
             open: function() {
                 $('#disableLayer').fadeIn();
                 $('.ui-widget-overlay').css('position', 'fixed');
             }
         }).end()
             .find('.ui-dialog-titlebar').addClass('modalTitleBar').end()
             .find('.ui-dialog-titlebar-close').addClass('modalClose');

         $(window).resize(function() {
             $dialogWindowElement.dialog('option', 'position', 'center');
         });

         if(dialogCancelBtn){
          $(dialogCancelBtn).click(function(e){
              e.preventDefault();
              $dialogWindowElement.dialog( "close" );
          });
      }
    };

    var _setFilters = function () {
        if(!_.isEmpty($(_DOMElements.documentTypeFilter).val())){
            $( _DOMElements.bglDocumentTypePeriod ).dropkick('setValue',$(_DOMElements.documentTypeFilter).val());
        }
        if(!_.isEmpty($(_DOMElements.financialYearFilter).val())){
            $( _DOMElements.searchFinancialYearPeriod ).dropkick('setValue',$(_DOMElements.financialYearFilter).val());
        }
        if(!_.isEmpty($(_DOMElements.uploadByFilter).val())){
            $( _DOMElements.bglUploadedByPeriod ).dropkick('setValue',$(_DOMElements.uploadByFilter).val());
        }
        if(!_.isEmpty($(_DOMElements.documentStatusFilter).val()) && $(_DOMElements.documentStatusFilter).val() === 'Draft'){
            $( '.jq-privacyFlag').attr('checked',true);
        } else {
            $( '.jq-privacyFlag').attr('checked',false);
        }
        $(_DOMElements.searchFromDate)
	    .val($.formatDate(new Date($(_DOMElements.dateFromFilter).val()),dateFormat))
	    .attr('data-placeholder',$.formatDate(new Date($(_DOMElements.dateFromFilter).val()),dateFormat));
        var searchDateToFilter = new Date($(_DOMElements.dateToFilter).val());
        searchDateToFilter.setDate(searchDateToFilter.getDate() -1);
        $(_DOMElements.searchToDate)
	    .val($.formatDate(new Date(searchDateToFilter),dateFormat))
	    .attr('data-placeholder',$.formatDate(new Date(searchDateToFilter),dateFormat));

	    if(!_.isEmpty($(_DOMElements.auditFlagFilter).val()) && $(_DOMElements.auditFlagFilter).val() === 'true'){
            $( _DOMElements.auditFlag).attr('checked', true);
        }
        if(!_.isEmpty($('#nameSearchFilter').val())){
            $( '.jq-documentNameFilter').val($('#nameSearchFilter').val());
        }
        if(!_.isEmpty($('.jq-documentSubTypeFilter').val())){
            _loadSubCategories($(_DOMElements.documentTypeFilter).val());
            $( '.jq-documentSubCategory' ).dropkick('setValue',$('.jq-documentSubTypeFilter').val());
        }
        if(!_.isEmpty($('.jq-documentSubSubTypeFilter').val())){
            _loadSubSubCategories($(_DOMElements.documentSubTypeFilter).val());
            $( '.jq-documentSubSubCategory' ).dropkick('setValue',$('.jq-documentSubSubTypeFilter').val());
        }
        if(!_.isEmpty($(_DOMElements.softDeletedFilter).val()) && $(_DOMElements.softDeletedFilter).val() === 'true'){
            $( '.jq-softDeletedFlag').attr('checked', true);
        }
    };

    var _init = function(){

        _bindDOMEvents();
        org.bt.modules.docLibUtils.attachOptions(_DOMElements.bglDocumentTypePeriod,
            org.bt.modules.referenceData.DOCUMENT_CATEGORIES);
        _initDocumentTypeDropdown();
        org.bt.modules.docLibUtils.initFinancialYearDropdown(_DOMElements.searchFinancialYearPeriod);
        org.bt.modules.docLibUtils.initDropdown(_DOMElements.bglUploadedByPeriod, 'selectDropDown');
        org.bt.modules.docLibUtils.initDropdown(_DOMElements.editSDocumentStatus, 'selectDropDown');
        org.bt.modules.docLibUtils.initDropdown(_DOMElements.uploadDocumentStatus, 'selectDropDown');

        $(_DOMElements.bglDocumentTypePeriod).dropkick('reset');
        $(_DOMElements.searchFinancialYearPeriod).dropkick('reset');
        $(_DOMElements.bglUploadedByPeriod).dropkick('reset');
        $(_DOMElements.uploadDocumentStatus).dropkick('reset');
        $(_DOMElements.editSDocumentStatus).dropkick('reset');

        _initModalDialog(_DOMElements.editDocumentMetadataPopupBlock,_DOMElements.cancelDocumentMetadataPopupBlock, 900);
        _initModalDialog(_DOMElements.uploadDocumentPopupBlock,_DOMElements.cancelUploadDocumentPopupBlock, 750);
        _initModalDialog(_DOMElements.confirmationPopup,_DOMElements.cancelDeleteDocumentBtn, 600);
        $(_DOMElements.uploadDocumentPopupBlock).css('display', 'block');
        $(_DOMElements.editDocumentMetadataPopupBlock).css('display', 'block');
        _setFilters ();

        $('.jsDocumentType.inputStyleAlignOne input').keyup(function() {
             $('#documentTypeInput').trigger('change');
         });
        $(_DOMElements.bglDocumentTypePeriod).change(function(value){
              _loadSubCategories($(this).val());
         });
        $('.jq-documentSubCategory').dropkick({
         change: function (value, label) {
        $(this).change();
        _loadSubSubCategories($(this).val());
         },
         inputClasses: ["inputStyleFive"]
         });
    };

    var _resetToDefault = function () {
        $(_DOMElements.bglDocumentTypePeriod).dropkick('reset');
        $(_DOMElements.searchFinancialYearPeriod).dropkick('reset');
        $(_DOMElements.bglUploadedByPeriod).dropkick('reset');
        $(_DOMElements.documentTypeFilter).val('');
        $(_DOMElements.financialYearFilter).val('');
        $(_DOMElements.uploadByFilter).val('');
        $(_DOMElements.dateFromFilter).val('');
        $(_DOMElements.dateToFilter).val('');
        $(_DOMElements.searchFromDate).val('').attr('data-placeholder','');
        $(_DOMElements.searchToDate).val('').attr('data-placeholder','');
        $(_DOMElements.auditFlag).attr('checked', false);
        $('.jq-privacyFlag').attr('checked', false);
        $('.jq-softDeletedFlag').attr('checked', false);
        $('.jq-documentNameFilter').val('');
        $('#nameSearchFilter').val('');
        $('jq-documentSubTypeFilter').val('');
        $('#documentSubCategoryContainer').addClass('noDisplay');
        $('#documentSubSubCategoryContainer').addClass('noDisplay')

    };

    return{
        init:function(){
            _init();
        }
    };
})(jQuery,window, document, moment);