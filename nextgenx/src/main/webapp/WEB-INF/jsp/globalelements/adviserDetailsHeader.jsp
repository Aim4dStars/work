<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<ul class="globalContentWrapper jq-AdviserDetailsLink cursor" data-ng-key="adviserInfoHeader">
    <li class="sectionHeader">
        <span class="block emphasis">Your adviser</span>
		<span class="block topMenuItemTxtName" data-ng-key="adviserName">
			${person.advisor.firstName} ${person.advisor.lastName}
		</span>		
    	<c:forEach items="${person.advisor.phoneNumbers}" var="varMobileDetails">
            <c:if test="${(varMobileDetails.type=='Landline')}">
            	<span class="block topMenuItemPhone" data-ng-key="adviserPhoneNumber"> ${varMobileDetails.phoneNumber}</span>
            </c:if>
        </c:forEach>
    </li>
    <li class="sectionAction">
        <a href="#nogo" role="button" aria-describedby="jq-globalNavAdviserDetails"><em class="iconarrowfulldown cursor"><span class="screenReaderText" id="jq-globalNavAdviserDetails">Click here to view adviser details</span></em></a>
    </li>
</ul>

<div class="contentDialog noDisplay jq-AdviserDetailsWrapper jq-flyout" role="dialog" data-ng-key="adviserDetailHeader">

    <em class="expandArrow expandArrowMod2 jq-pointer"></em>
    <h5 data-ng-key="dealerGroup">${person.advisor.dealerGroupName}</h5>
    <dl class="definitionList">
        	    <dt class="emphasis">Email</dt>
        	    <dd>
        	        <c:forEach items="${person.advisor.emailIds}" var="varEmailDetails" varStatus="index">
                          <c:if test="${varEmailDetails.primary}">
                             <a href="mailto:${varEmailDetails.email}" class="primaryLink" data-ng-key="adviserEmail">${varEmailDetails.email}</a>
                          </c:if>
                    </c:forEach>
        	    </dd>

        	    <dt class="emphasis clearBoth">Address</dt>
        	    <dd>
        	        <ul class="listContent">
        	            <c:forEach items="${person.advisor.addresses}" var="varAddresDetails" varStatus="index">
                            <c:if test="${varAddresDetails.type=='Residential'}">
                               <li data-ng-key="adviserAddressLine1">${varAddresDetails.addressLine1},</li>
                               <c:if test="${(not empty varAddresDetails.addressLine2)}">
                               <li data-ng-key="adviserAddressLine2"> ${varAddresDetails.addressLine2},</li>
                               </c:if>
                                <li data-ng-key="adviserStatePin"> ${varAddresDetails.state} ${varAddresDetails.pin}<c:if test="${(not empty varAddresDetails.country)}">,</c:if></li>
                                <li data-ng-key="adviserCountry"> ${varAddresDetails.country}</li>
                                <li class="setTopGutter">
                                    <a target="_blank"  data-ng-key="adviserLocationMap" href="https://maps.google.com/maps?q=${varAddresDetails.addressLine1},+${varAddresDetails.addressLine2},+${varAddresDetails.city},+${varAddresDetails.state},+${varAddresDetails.pin},+${varAddresDetails.country}" class="primaryLink">
                                    <em class="iconlocationpin iconlocationpinMod1 jq-pointer"></em>
                                    <span class="text">Show me on Google maps</span></a>
                                </li>
                            </c:if>
                        </c:forEach>

                    </ul>
        	    </dd>
        	</dl>

</div>


