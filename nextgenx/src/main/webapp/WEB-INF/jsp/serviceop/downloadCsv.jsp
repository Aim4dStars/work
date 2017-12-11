<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h1 class="pageHeaderItem">Download all unapproved applications report</h1>

<c:if test="${not empty ERROR}">
    <div data-ng-key="messageBox" class="jq-largeDataSetErrorMessage noticeBox warningBox">
        <ul class="noticeBoxWrapper">
            <li class="noticeBoxText" role="alert">
                <p>${ERROR}</p>
            </li>
        </ul>
    </div>
</c:if>

<form id="applicationDownload" method="GET" action="" class="jq-idpssnapshotReportfailedappSummary">
    <fieldset>
        <ul class="formBlockContainer formBlockContainerMod3 clearFix ">
            <li class="formBlock formBlockMod3" data-ng-key="failedappSummaryDateWidgetFrom">
                <label for="fromDate" class="formLabel">From</label>
                <span class="calendarPlaceHolder jq-failedappDownloadFromDateCalendarPlaceHolder"></span>
			          <span class="iconWrapper">

			           <input id="fromDate" name="fromDate" data-calendar=".jq-failedappDownloadFromDateCalendarPlaceHolder"
                              class="formTextInput jq-failedappDownloadFromDate inputStyleThree" value="" data-placeholder=""
                              data-validation="validate[date,custom[future],custom[past],custom[customFunction]]"
                              data-past="2006-01-01" data-future="2015-03-30"
                              data-validation-required-error="${errors.err00053}"
                              data-validation-date-error="${errors.err00060}"
                              data-validation-future-error="${errors.err00053}"
                              data-validation-customFunction-error="${errors.err00016}"/>


			          <a class="iconActionButton jq-failedappDownloadFromDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                          <em class="iconcalendar"><span>Select Date</span></em>
                          <em class="iconarrowfulldown"></em>
                      </a>
			         </span>

            </li>
            <li class="formBlock formBlockMod3" data-ng-key="failedappSummaryDateWidgetTo">
                <label for="toDate" class="formLabel">To</label>
                <span class="calendarPlaceHolder jq-failedappDownloadToDateCalendarPlaceHolder"></span>
			          <span class="iconWrapper">

			          <input id="toDate" name="toDate" data-calendar=".jq-failedappDownloadToDateCalendarPlaceHolder"
                             class="formTextInput jq-failedappDownloadToDate inputStyleThree" value="" data-placeholder=""
                             data-validation="validate[date,custom[future],custom[past],custom[customFunction]]"
                             data-past="2006-01-01" data-future="2015-03-30"
                             data-validation-required-error="${errors.err00053}"
                             data-validation-date-error="${errors.err00060}"
                             data-validation-future-error="${errors.err00053}"
                             data-validation-customFunction-error="${errors.err00132}"/>


			          <a class="jq-failedappDownloadToDateCalendarIcon jq-appendErrorAfter calendarIconLink" title="Date Picker" href="#nogo">
                          <em class="iconcalendar"><span>Select date</span></em>
                          <em class="iconarrowfulldown"></em>
                      </a>
			         </span>

            </li>
        </ul>
        <input type="submit" class="primaryButton jq-formSubmit" value="Download csv">

    </fieldset>
</form>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/downloadFailedApplication.js'/>"></script>