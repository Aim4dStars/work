<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form id="documentsFilterSearch" method="GET" class="jq-documentsFilterSearchForm" action="">
  <fieldset>
    <legend>Selected Account '120002019' </legend>
      <ul class="formBlockContainer formBlockContainerMod6">
        <li class="formBlock formBlockMod3">
          <label for="intermediaryName" class="formLabel visuallyHidden">Document type</label>
          <span class="inputWrapper iconWrapper" >
           <select id="documentType" class="jq-documentType" name="documentType">
                   <option value="Any" selected="selected">Any</option>
                   <option value="Advice Documentation">Advice Documentation</option>
                   <option value="Correspondence">Correspondence</option>
                   <option value="Investments">Investments</option>
                   <option value="SMSF">SMSF</option>
                   <option value="Statements">Statements</option>
                   <option value="Tax returns">Tax returns</option>
                   <option value="Other">Other</option>
                   </select>
          </span>
        </li>
        <li class="formBlock formBlockMod3">
                  <label for="financialYear" class="formLabel visuallyHidden">Financial year</label>
                  <span class="inputWrapper iconWrapper" >
                   <select id="financialYear" class="jq-financialYear" name="financialYear">
                           <option value="Any" selected="selected">Any</option>
                           <option value="2010/2011">2010/2011</option>
                           <option value="2011/2012">2011/2012</option>
                           <option value="2012/2013">2012/2013</option>
                           <option value="2013/2014">2013/2014</option>
                           <option value="2014/2015">2014/2015</option>
                           <option value="2015/2016">2015/2016</option>
                           <option value="2016/2017">2016/2017</option>
                           <option value="2018/2019">2018/2019</option>
                           <option value="2019/2020">2019/2020</option>
                           </select>
                  </span>
                </li>
        </ul>
        <ul class="formBlockContainer formBlockContainerMod6">
                <li class="formBlock formBlockMod3">
                  <label for="Uploadedby" class="formLabel visuallyHidden">Uploaded by</label>
                  <span class="inputWrapper iconWrapper" >
                   <select id="Uploadedby" class="jq-Uploadedby" name="Uploadedby">
                           <option value="Any" selected="selected">Any</option>
                           <option value="Accountant">Accountant</option>
                           <option value="Adviser">Adviser</option>
                           <option value="Investor">Investor</option>
                           <option value="Panorama">Panorama</option>
                           </select>
                  </span>
                </li>
        </ul>
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


        <li class="formBlock formBlockMod6">
          <input value="Search" type="submit" id="jq-IntermediariesAndClientsSearchButton" class="noDisplay" tabindex="-1"/>
          <input value="" name="selection" type="hidden" class="jq-IntermediariesAndClientsSearchSelection"/>
          <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                <span>Search</span>
                <em class="iconsearch"></em>
          </a>
        </li>
 </fieldset>
</form>

 <script id="jq-autoCompleteTemplate" type="text/x-handlebars-template">
    <a>
    <dl class="accontSelectContainer accontSelectContainerMod1">
        <dt class="accountDef accountDefMod1 accountDefTitleItem">

            <div class="floatLeft">
                <p data-ng-key="name">{{lastName}}, {{firstName}}</p>
                <p class="normalWeight accountDefText" data-ng-key="userID">{{id}}</p>
            </div>
        </dt>
        <dd class="floatRight accountDefTextItem">
            <span data-ng-key="location">{{city}}<br/>{{state}}</span>
        </dd>
    </dl>
    </a>
</script>