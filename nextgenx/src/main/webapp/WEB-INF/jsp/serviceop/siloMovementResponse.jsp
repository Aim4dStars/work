<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script language="javascript" type="text/javascript"
	src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/siloMovementStatus.js'/>"></script>
<body>
	<h1 class="pageHeaderItem">Silo Movement request tracker</h1>
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>"
		name="cssftoken" />
	<!-- <div id="jsGrid">
	</div> -->
	<form id="siloMovementSearch" method="GET"
		class="jq-intermediariesAndClientsSearchForm"
		action="siloMovementTracking">
		<fieldset>
			<legend>Silo Movement search</legend>
			<ul class="formBlockContainer formBlockContainerMod6">
				<li class="formBlock formBlockMod3"><label
					for="intermediaryName" class="formLabel">Enter Old CIS Key</label> <span
					class="inputWrapper iconWrapper"> <input
						id="intermediaryName"
						data-validation="validate[required,custom[searchText],custom[minLength],custom[customFunction]]"
						data-min-length="2"
						data-validation-required-error="${errors.err00094}"
						data-validation-minLength-error="${errors.err00094}"
						data-validation-searchText-error="${errors.err00084}"
						class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
						name="oldCis" data-ng-key="oldCis" type="text"
						value="<c:out value='${oldCis}' />"
						autocomplete="off">
				</span></li>
				<li class="formBlock formBlockMod3"><label
					for="intermediaryName" class="formLabel">Start Date</label> <span class="calendarPlaceHolder jq-dateOfBirthPickerHolder"></span>
                <span class="iconWrapper">
                    <input id="partyRelStartDate" name="datetimeStart" data-calendar=".jq-dateOfBirthPickerHolder"
                      class="formTextInput jq-dateOfBirth inputStyleThree" value="<c:out value='${datetimeStart}' />" data-placeholder=""
                      data-validation=""
                      data-past="2006-01-01" data-future="2015-03-30"
                      data-validation-required-error="${errors.err00053}"
                      data-validation-date-error="${errors.err00060}"
                      data-validation-future-error="${errors.err00053}"
                      data-validation-customFunction-error="${errors.err00016}"/>
                    <a class="iconActionButton jq-birthDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                        <em class="iconcalendar"><span>Select Date</span></em>
                        <em class="iconarrowfulldown"></em>
                    </a>
             </span></li>
				<li class="formBlock formBlockMod3"><label
					for="intermediaryName" class="formLabel">End Date</label> <span class="calendarPlaceHolder jq-effectiveStartDateCalendarPlaceHolder"></span>
			    <span class="iconWrapper">
			        <input id="partyRelEndDate" name="datetimeEnd" data-calendar=".jq-effectiveStartDateCalendarPlaceHolder"
                          class="formTextInput jq-effectiveStartDate inputStyleThree" value="<c:out value='${datetimeEnd}' />" data-placeholder=""
                          data-validation=""
                          data-past="2006-01-01" data-future="2015-03-30"
                          data-validation-required-error="${errors.err00053}"
                          data-validation-date-error="${errors.err00060}"
                          data-validation-future-error="${errors.err00053}"
                          data-validation-customFunction-error="${errors.err00016}"/>
			        <a class="iconActionButton jq-effectiveStartDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                          <em class="iconcalendar"><span>Select Date</span></em>
                          <em class="iconarrowfulldown"></em>
                    </a>
			    </span></li>
				<li class="formBlock formBlockMod6"><label
					for="intermediaryName" class="formLabel "><br /></label> <span class="inputWrapper iconWrapper"> <input class="actionButtonIcon jq-formSubmit"
						type="Submit" value="Search"/>
						
				</span></li>
			</ul>
		</fieldset>
	</form>
	<div id="jq-siloMovementTracking" data-type="intermediaries"
		class="jq-tabContainer">
		<c:choose>
			<c:when test="${fn:length(siloMovementList) > 0}">
				<div class="tableContainer jq-searchResultTableContainer">
					<table class="dataTable" data-table="searchResult">
						<caption class="caption">Silo Movement Results For Last
							one Month</caption>
						<thead>
							<tr class="tableHeader tableHeaderFull tableHeaderMod2">
								<td colspan="4" class="toolHeader">
									<p class="textTransTable textTransTableMod2">Showing 1 -
										${fn:length(siloMovementList)} of
										${fn:length(siloMovementList)}</p>
								</td>
							</tr>
							<tr class="tableHeader tableHeaderFull tableHeaderMod6">
								<th class="dataTableHeader dataTableHeaderFirst alphaAlign">Application<br />
									Id
								</th>
								<th class="dataTableHeader alphaAlign ">Username</th>
								<th class="dataTableHeader alphaAlign">From <br />Silo
								</th>
								<th class="dataTableHeader alphaAlign">To <br /> Silo
								</th>
								<th class="dataTableHeader alphaAlign">Time<br /> Start
								</th>
								<th class="dataTableHeader alphaAlign">Time<br /> End
								</th>
								<th class="dataTableHeader alphaAlign">Old <br />Cis Key
								</th>
								<th class="dataTableHeader alphaAlign">New <br />Cis Key
								</th>
								<th class="dataTableHeader alphaAlign">Last<br /> Success
									<br />State
								</th>
								<th class="dataTableHeader alphaAlign">Error <br />State
								</th>
								<th class="dataTableHeader alphaAlign">Error<br /> Message
								</th>
							</tr>
						</thead>
						<tbody class="clientSearchResult jq-searchResult">
							<c:forEach items="${siloMovementList}" var="siloMovement">
								<tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg"
									data-type="siloMovementStatusModel">

									<td class="dataTableCell nameWrap">
										<div>${siloMovement.appId}</div>
									</td>
									<td class="dataTableCell dgWrap">
										<div>${siloMovement.userId}</div>
									</td>
									<td class="dataTableCell cdWrap">
										<div>${siloMovement.fromSilo}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.toSilo}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.datetimeStart}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.datetimeEnd}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.oldCis}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.newCis}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.lastSuccState}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.errState}</div>
									</td>
									<td class="dataTableCell loWrap">
										<div>${siloMovement.errMsg}</div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4"></td>
							</tr>
						</tfoot>
					</table>
				</div>
			</c:when>
			<c:when test="${fn:length(siloMovementList) == 0}">
				<p>Sorry, we can&rsquo;t find any silo movements. Please try
					again.</p>
				<p aria-live="assertive" aria-atomic="true"
					class="ui-helper-hidden-accessible jq-searchResultMessage"></p>
			</c:when>
		</c:choose>
	</div>
	<script language="javascript" type="text/javascript"
		src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/siloMovementCalender.js'/>"></script>
</body>