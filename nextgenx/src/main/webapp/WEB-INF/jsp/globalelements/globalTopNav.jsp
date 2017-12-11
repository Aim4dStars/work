    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
        <%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
        <%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
        <%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>

        <spring:eval var="trusteeCaApprovalsUrl" expression="@environment.getProperty('trustee.caApprovals.url')" />

        <nav class="layoutHeaderWrap">
            <span class="logoWrap logoWrapPos">
                <a href="#">BT</a>
            </span>
        <!-- Top Navigation elements -->

        <ul class="topMenu topMenuPos">
            <li class="topMenuItem topMenuMenuItemSub topMenuItemMod2  noLeftBorder">
            <!-- Message Header Flyout Details pagte -->
            <jsp:include page="../globalelements/messageDetailsHeader.jsp"/>
         </li>

        <li class="topMenuItem topMenuMenuItemMain jq-ftueBubbleSourceWrap ">
                <div class="topMenuItemLink linkIconText">
                	<span class="iconDropWrapper iconDropWrapperTopNav hidden">
                		<em class="iconsettings"></em>
                	</span>
                	<span class="topMenuItemTxtName">
                    	<span><strong>Login ID:</strong> ${person.userName}</span>
                    </span>
                </div>

			<!--  START : User menu -->
            <div class="contentDialog noDisplay contentDialogMod1 contentDialogMod4 jq-userMenuWrapper" role="dialog"  data-ng-key="userMenu">
			<em class="expandArrow expandArrowMod3"></em>
	         <ul class="globalNavList globalNavListMod1">
	            <li>		                
	            </li>
	         </ul>
			</div>
	        <!--  END : User menu -->
	        
        </li>

        <c:if test="${trusteeApprovalAccess}">
            <li class="topMenuItem topMenuMenuItemSubWide">
                <div class="topMenuItemWrapper">
                    <a href="<ng:hashurl src="${trusteeCaApprovalsUrl}"/>" target="_blank" class="topMenuItemLink linkIcon" title="CA approvals">
                        <span class="iconDropWrapper iconDropWrapperTopNav">
                            <em class="icontasks linkIconImage"></em>
                            <span>CA approvals</span>
                        </span>
                    </a>
                </div>
            </li>
        </c:if>

        <c:if test="${irgApprovalAccess}">
            <li class="topMenuItem topMenuMenuItemSubWide">
                <div class="topMenuItemWrapper">
                    <a href="<ng:hashurl src="${trusteeCaApprovalsUrl}"/>" target="_blank" class="topMenuItemLink linkIcon" title="IRG approvals">
                        <span class="iconDropWrapper iconDropWrapperTopNav">
                            <em class="icontasks linkIconImage"></em>
                            <span>IRG approvals</span>
                        </span>
                    </a>
                </div>
            </li>
        </c:if>

        <!--<li class="topMenuItem">

        </li>-->
        <li class="topMenuItem topMenuMenuItemSub">
        <span class="topMenuItemWrapper">
            <a data-ng-key="logoutLink" class="topMenuItemLink linkIcon jq-logoutButton" title="Sign out" href="#">
                <span class="iconDropWrapper iconDropWrapperTopNav">  <em class="iconsignout linkIconImage"></em>
                <span>Sign out</span>
        </span>
        </a>
           </span>
        </li>
        </ul>


        </nav><!-- layoutHeaderWrap-->
    <div class="jq-logoutBusyDialog jq-cloak logonBusyDialog">
        <h1>Signing you out ... <em class="iconLoader"></em></h1>
    </div>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/shared/globalElements.js'/>"></script>