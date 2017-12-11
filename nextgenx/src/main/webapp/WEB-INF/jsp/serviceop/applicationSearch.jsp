<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h1 class="pageHeaderItem">Search for applications</h1>

<div class="toggleMenuLineWrap toggleMenuLineWrapMod7">
    <ul class="toggleMenuLine toggleMenuLineMod3">
        <li class="toggleMenuLineItem">
            <a href="#jq-advisedInvestors" class="textLink" data-ng-key="advisedInvestorsTab">Advised Investors</a>
        </li>
        <li class="toggleMenuLineItem">
            <a href="#jq-directInvestors" class="textLink" data-ng-key="directInvestorsTab">Direct Investors</a>
        </li>
        <li class="toggleMenuLineItem">
            <a href="#jq-onBordingStatus" class="textLink" data-ng-key="onboardingStatusTab">On Boarding Status</a>
        </li>
    </ul>
    <div id="jq-advisedInvestors" class="jq-tabContainer">
        <form id="applicationSearch" method="GET" class="jq-applicationSearchForm" action="" data-ajax-submit-url="">
            <fieldset>
                <div class="formBlock clearFix">
        <span class="inputStyleAlignOne">
        <select id="clientApplication" class="jq-searchApplication" name="clientApplication">
            <option value="Approved Application" selected="selected">Approved Application</option>
            <option value="Failed Application">Failed Application</option>
        </select>
        </span>
                </div>
                <ul class="formBlockContainer formBlockContainerMod6">

                    <li class="formBlock formBlockMod3">
                <span class="inputWrapper iconWrapper">
                    <input
                            id="accountNumber"
                            data-validation="validate[required,custom[customFunction]]"
                            data-validation-unSignedInteger-error="Please use only numbers"
                            class="formTextInput inputStyleEight jq-accountNumber"
                            name="accountNumber"
                            data-ng-key="searchCriteria"
                            type="text"
                            value=""
                            autocomplete="off"
                            placeholder="Enter account number"
                            size="40">
                </span>
                    </li>

                    <li class="formBlock formBlockMod3">
        <span class="inputWrapper iconWrapper">
        <input
                id="intermediaryName"
                data-validation="validate[required,custom[customFunction]]"
                data-min-length="10"
                data-max-length="10"
                data-validation-required-error="Enter an Application reference in the format 'R999999999'"
                data-validation-customMinLength-error="A valid Application Reference ID has 9 digits"
                data-validation-customMaxLength-error="A valid Application Reference ID has 9 digits"
                data-validation-allZero-error="A valid Application Reference ID has at least one non-zero digit"
                data-validation-firstCharNotR-error="Enter an Application reference in the format 'R999999999'"
                data-validation-unSignedInteger-error="Enter an Application reference in the format 'R999999999'"
                class="formTextInput inputStyleEight jq-applicationId jq-skip noDisplay"
                disabled="disabled"
                name="applicationId"
                data-ng-key="searchCriteria"
                type="text"
                value=""
                autocomplete="off"
                placeholder="Enter Application reference ID"
                size="40">
        </span>
                    </li>


                    <li class="formBlock formBlockMod6">
                        <input value="Search" type="submit" id="jq-IntermediariesAndClientsSearchButton"
                               class="noDisplay"
                               tabindex="-1"/>
                        <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                            <span>Search</span>
                            <em class="iconsearch"></em>
                        </a>
                    </li>
                </ul>
            </fieldset>
        </form>
    </div>
    <div id="jq-directInvestors" class="jq-tabContainer">
        <form id="directApplicationSearch" method="GET" class="jq-directApplicationSearchForm" action=""
              data-ajax-submit-url="">
            <fieldset>
                <div class="formBlock clearFix">
        <span class="inputStyleAlignOne">
        <select id="directClientApplication" class="jq-directClientApplication" name="directClientApplication">
            <option value="Approved Application" selected="selected">Approved Application</option>
            <option value="Failed Application">Failed Application</option>
        </select>
        </span>
                </div>
                <ul class="formBlockContainer formBlockContainerMod6">

                    <li class="formBlock formBlockMod3">
        <span class="inputWrapper iconWrapper">
        <input
                id="cisKey"
                data-validation="validate[required,custom[customFunction]]"
                data-min-length="11"
                data-max-length="11"
                data-validation-required-error="Enter a valid CIS Key"
                data-validation-unSignedInteger-error="Please use only numbers"
                data-validation-customMinLength-error="A valid CIS Key has 11 digits"
                data-validation-customMaxLength-error="A valid CIS Key has 11 digits"
                data-validation-allZero-error="A valid CIS Key has at least one non-zero digit"
                class="formTextInput inputStyleEight jq-cisKey"
                name="cisKey"
                data-ng-key="searchCriteria"
                type="text"
                value=""
                autocomplete="off"
                placeholder="Enter CIS key"
                size="38">
        </span>
                    </li>
                    <li class="formBlock formBlockMod6">
                        <input value="Search" type="submit" id="jq-directApplicationSearchButton"
                               class="noDisplay"
                               tabindex="-1"/>
                        <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                            <span>Search</span>
                            <em class="iconsearch"></em>
                        </a>
                    </li>
                </ul>
            </fieldset>
        </form>
    </div>
    <div id="jq-onBordingStatus" class="tab">
            <div class="tableContainer jq-searchResultTableContainer">
                <table class="dataTable">
                    <caption class="caption" style="visibility:hidden;"></caption>
                    <caption class="caption">On Boarding Application Status</caption>
                    <thead>
                        <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                            <th class="dataTableHeader dataTableHeaderFirst alphaAlign" style="width:40%;">Service</th>
                            <th class="dataTableHeader alphaAlign" style="width:60%;">Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/OnBoardingStatus'/>" class="emphasis baseLink">On Boarding Application Status <span class="italicFont">-</span><br/></a>
                            </td>
                            <td class="dataTableCell nameWrap">
                            </td>
                        </tr>
                        
                    </tbody>
                </table>
            </div>
        </div>
</div>


<table class="jq-searchAppNoResults dataTable">
    <caption class="caption">Failed Applications</caption>
    <thead>
        <tr class="tableHeader tableHeaderFull">
            <td colspan="7"
                class="dataTableHeader dataTableHeaderFirst alphaAlign">
                <span
                    data-ng-key="emptyClientMessage">No result was found. Please check your criteria or try a different search.</span>
            </td>
        </tr>
    <thead>
</table>

<table class="jq-moveStatusFailedDraftSuccess dataTable">
            <caption class="caption">Failed Applications</caption>
            <thead>
            <tr class="tableHeader tableHeaderFull">
                <td colspan="7"
                    class="dataTableHeader dataTableHeaderFirst alphaAlign"><span class="jq-movedStatusAjaxResponse"
                        data-ng-key="emptyClientMessage">Status of application has been moved to Draft successfully.
            </span></td>
            </tr>
            <thead>
</table>

<table class="jq-searchAppTable dataTable dataTableMod4 dataTableGridServiceOpsSearchApp ${advisorDashboardModel.approvedRequests.size() == 0 ? 'noDataTable' : ''}
        "
        data-table="activeTasks" data-page="0" data-page-size="5"
        data-category="ACTIVE" data-remaining="<c:out
        value="${advisorDashboardModel.approvedRequests.size() > 5 ? advisorDashboardModel.approvedRequests.size()-5 : '0'}
        "/>"
        data-row-tmpl="#jq-taskViewActiveTemplate">
        <caption class="caption jq-searchAppTable-caption">Failed Applications <span class="textCount">${advisorDashboardModel.approvedRequests.size()}
        </span></caption>
        <colgroup>
        <col>
        <col>
        <col>
        <col>
        <col>
        <col>
    </colgroup>
    <thead>
        <tr class="tableHeader tableHeaderMod2 tableHeaderMod1">
        <th class="dataTableHeader dataCell2Col1 alphaAlign">Date Submitted</th>
        <th class="dataTableHeader dataCell2Col2 alphaAlign jq-columnHeaderAcctName">Account name / Application ref no.</th>
        <th class="dataTableHeader dataCell2Col3 alphaAlign">Status</th>
        <th class="dataTableHeader dataCell2Col4 alphaAlign">Adviser</th>
        <th class="dataTableHeader dataCell2Col5 alphaAlign">Last Updated by</th>
        <th class="dataTableHeader dataCell2Col6"></th>
        </tr>
    </thead>
    <tbody>
    </tbody>
 </table>

        <script id="jq-taskViewActiveTemplate" type="text/x-handlebars-template">
        {{#clients}}
        <tr data-id="{{messageId}}" class="dataTableRow dataTableRowBg cursor dataTableRowClearBorder">
        <td class="dataTableCell alphaAlign">
        <span class="emphasis">{{lastModified}}</span>
        <p class="daysAgo">{{lastModifiedTimeSpan}}</p>
        </td>
        <td class="dataTableCell alphaAlign">
        <span class="descriptionHeader emphasis">
        {{accountName}}
        </span>
        {{#if directInvestors}}
            <span class="inlineBlock">{{accountType}}.{{productName}}</span>
        {{else}}
            <span class="inlineBlock">{{referenceNumber}}.{{accountType}}.{{productName}}</span>
        {{/if}}
        </td>
        <td class="dataTableCell alphaAlign">
        <span class="inlineBlock">{{status}}</span>
        </td>
        <td class="dataTableCell alphaAlign"><span>{{adviserName}}</span></td>
        <td class="dataTableCell alphaAlign">
        <span class="inlineBlock">{{lastModifiedByName}}</span>
        </td>
        <td class="dataTableCell numericAlign">
        </td>
        </tr>

        <tr>
            <td class="dataTableHeaderFirst" colspan=7>
                <div class="setBottomGutter">
                    {{#unless directInvestors}}
                    <a data-id="" class="baseLink jq-moveFailedStatusDraft gutter-right-2" href="#nogo">Move Status to Draft</a><em class="iconLoader noDisplay jq-loaderIcon"></em>
                    {{/unless}}
                    <a data-id="" class="baseLink" href="{{viewLink}}">View</a>
                </div>
            </td>
        </tr>
        {{#if failureMessage}}
        <tr>
            <td class="dataTableHeaderFirst" colspan=7>
                <span class="emphasis">Reason for failure<br /><br /></span>
                <p>{{failureMessage}}</p>
            </td>
        </tr>
        {{/if}}
        {{/clients}}
        </script>
        <script language="javascript" type="text/javascript" src="<nextgen:hashurl
        src='/public/static/js/client/desktop/pages/searchApplication.js'/>"></script>