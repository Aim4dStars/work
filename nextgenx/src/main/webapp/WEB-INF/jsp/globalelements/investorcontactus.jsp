<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld"%>

<div class="footerCMSContentPanel" data-ng-key="cmsContentPanel">
	<h1>Contact us</h1>

    <h2 class="mod1">If you have any enquiries about your portfolio,<br/> please contact your financial adviser</h2>
	<h6 class="emphasis">${adviserModel.dealerGroupName}</h6>
	<ul class="noBullets">
	    <li data-ng-key="email">
				<c:forEach items="${adviserModel.emailIds}" var="emailList">
					<c:if test="${emailList.primary}">
						<a href="mailto:${emailList.email}" title="${emailList.email}" class="baseLink">${emailList.email}</a>
					</c:if>
				</c:forEach>
	    </li>
	    
	    <li data-ng-key="phone">
            <c:forEach items="${adviserModel.phoneNumbers}" var="mobileList">
                <c:if test="${mobileList.type=='Mobile'}">
                    <c:if test="${mobileList.primary}">${mobileList.phoneNumber}</c:if>
                </c:if>
            </c:forEach>
	    </li>
            <c:forEach items="${adviserModel.addresses}" var="addresseList">
                <c:if test="${addresseList.type=='Residential'}">
                    <li data-ng-key="address">${addresseList.addressLine1} ${addresseList.addressLine2}<br />
                        ${addresseList.city} ${addresseList.state} ${addresseList.pin} ${addresseList.country}
                    </li>
                    <li data-ng-key="googlelink"><a class="baseLink" target="_blank" href="https://maps.google.com/maps?q=${addresseList.addressLine1},+${addresseList.addressLine2},+${addresseList.city},+${addresseList.state},+${addresseList.country},+${addresseList.pin}">See on Google maps</a></li>
                </c:if>
            </c:forEach>
	</ul>

    <h2 class="mod1">For general Panorama<%--'Panorama' should ideally be a variable--%> enquiries</h2>
    <dl class="large">
        <dt class="emphasis">Phone</dt>
        <dd data-ng-key="btphone"><cms:content name="btphone" /></dd>
        
        <dt class="emphasis">Phone from overseas</dt>
	    <dd data-ng-key="btphoneoverseas"><cms:content name="btphoneoverseas" /></dd>

        <dt class="emphasis">Email</dt>
        <dd data-ng-key="email"><a class="baseLink" target="_blank" title="<cms:content name="btemail" />" href="mailto:<cms:content name="btemail" />"><cms:content name="btemail" /></dd>
    </dl>
    <div class="clear"></div>

    <br/>

	<div class="clear"></div>


</div>