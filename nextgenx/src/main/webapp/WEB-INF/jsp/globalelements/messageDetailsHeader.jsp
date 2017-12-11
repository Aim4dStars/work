<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<security:authorize ifAnyGranted="ROLE_INVESTOR">
    <div class="contentDialog contentDialogMod1 noDisplay jq-messageDetailsWrapper" role="dialog" data-ng-key="investorMessages">
         <em class="expandArrow expandArrowMod3"></em>
         <ul class="globalNavList">
            <li class="globalNavListHeader">
                <span class="emphasis"><a class="baseLink baseLinkClear" href="<c:url value='/secure/page/investorMessages'/>" data-ng-key="messages">Messages</a></span>
            </li>
            <li>
                <span class="floatLeft">Unread</span>
                <span class="emphasis floatRight jq-investorUnread" data-remaining-count="${globalAlertNotification.unread}" data-ng-key="investorUnread">${globalAlertNotification.unread}</span>
            </li>
             <li class="clearBoth" >
                <span class="floatLeft">High</span>
                <span class="emphasis floatRight jq-investorHigh" data-ng-key="investorHigh" data-remaining-count="${globalAlertNotification.high}">${globalAlertNotification.high}</span>
            </li>

         </ul>
    </div>
</security:authorize>


<security:authorize ifAnyGranted="ROLE_ADVISER">
	
    <div class="contentDialog contentDialogMod1 noDisplay jq-messageDetailsWrapper" role="dialog"  data-ng-key="adviserMessages">
         <em class="expandArrow expandArrowMod3"></em>
         <ul class="globalNavList">
            <li class="globalNavListHeader">
                <span class="emphasis"><a class="baseLink baseLinkClear" href="<c:url value='/secure/page/adviserMessages#jq-clientMessages'/>" data-ng-key="clientmsgLink">Client messages</a></span>
            </li>
            <li>
                <span class="floatLeft">Unread</span>
                <span class="emphasis floatRight jq-adviserClientUnread" data-ng-key="adviserClientUnread" data-remaining-count="${clientGlobalAlertNotification.unread}">${clientGlobalAlertNotification.unread}</span>
            </li>
             <li>
                <span class="floatLeft">High</span>
                <span class="emphasis floatRight jq-adviserClientHigh"  data-ng-key="adviserClientHigh" data-remaining-count="${clientGlobalAlertNotification.high}">${clientGlobalAlertNotification.high}</span>
            </li>

            <li  class="globalNavListHeader">
                <span class="emphasis"><a class="baseLink baseLinkClear" href="<c:url value='/secure/page/adviserMessages#jq-myMessages'/>">Your messages</a></span>
            </li>
            <li>
                <span class="floatLeft">Unread</span>
                <span class="emphasis floatRight jq-adviserMessageUnread" data-ng-key="adviserMessageUnread" data-remaining-count="${adviserGlobalAlertNotification.unread}">${adviserGlobalAlertNotification.unread}</span>
            </li>
             <li class="clearBoth">
                <span class="floatLeft">High</span>
                <span class="emphasis floatRight jq-adviserMessageHigh" data-ng-key="adviserMessageHigh" data-remaining-count="${adviserGlobalAlertNotification.high}">${adviserGlobalAlertNotification.high}</span>
            </li>

            <%--<li  class="globalNavListHeader centerAlign noDisplay">
                <span class="actionWrapper actionWrapperMod2 actionWrapPrimary">
                    <a href="<c:url value='/secure/page/yourdetails'/>" class="actionButtonIcon accordionFormClear" data-ng-key="messagePreference">
                        <span>Preferences</span><em class="iconarrowright"></em>
                    </a>
                </span>
            </li>--%>

         </ul>
    </div>
</security:authorize>


