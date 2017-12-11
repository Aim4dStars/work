
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<div
	class="utilBarItemHome utilBarItemBActive">
	<span class="screenReaderText">Home</span>
	<a href="<c:url value='/secure/page/home'/>" role="menuitem"
		class="linkIcon linkIconSide" title="Home" data-ng-key="dashboardNavLeft"> <em
		class="linkIconImage iconhome24"></em>
	</a>
</div>

<!-- Show it if the logged in user does not have DG Restricted ServiceOps Admin role  -->
<c:if test="${!isRestricted}">
    <div
        class="utilBarItemHome ${page == 'search'? 'utilBarItemBActive' : ''}">
        <span class="screenReaderText">Search for applications</span>
        <a href="<c:url value='/secure/page/serviceOps/searchApplication'/>" role="menuitem"
            class="linkIcon linkIconSide" title="Search for applications" data-ng-key="searchApplicationNavLeft"> <em
            class="linkIconImage iconServiceOps24"></em>
        </a>
    </div>
    <div
            class="utilBarItemTasks ${page == 'tasks'? 'utilBarItemBActive' : ''}">
        <span class="screenReaderText">Download unapproved applications</span>
        <a href="<c:url value='/secure/page/serviceOps/downloadApplication'/>" role="menuitem" class="linkIcon linkIconSide"
           title="Download unapproved applications" data-ng-key="mytasksNavLeft"> <em class="linkIconImage icontasks24"></em>
        </a>
    </div>

    <div  class="noDisplay jq-docLibraryLeftNav utilBarItemTasks ${page == 'tasks'? 'utilBarItemBActive' : ''}">
        <span class="screenReaderText">Download unapproved applications</span>
        <a href="<c:url value='/secure/page/serviceOps/accountSearch'/>" role="menuitem" class="linkIcon linkIconSide"
           title="Document Library" data-ng-key="doclibNavLeft"> <em class="linkIconImage iconlist"></em>
        </a>
    </div>
</c:if>
<div class="noDisplay jq-gcmHomeLeftNav utilBarItemHome ${page == 'tasks'? 'utilBarItemBActive' : ''}">
	<span class="screenReaderText">Home</span>
	<a href="<c:url value='/secure/page/serviceOps/gcmHome'/>" role="menuitem"
		class="linkIcon linkIconSide" title="Perform GCM Operations" data-ng-key="dashboardNavLeft"> <em
		class="linkIconImage iconclientsearch24"></em>
	</a>
</div>

<security:authorize ifAnyGranted="ROLE_ADVISER">
	<div
		class="utilBarItemClients ${page == 'Clients'? 'utilBarItemBActive' : ''} jq-ftueClientBubbleSourceWrap">
		<span class="screenReaderText">Clients</span>
		<a href="<c:url value='/secure/page/clients'/>" role="menuitem" class="linkIcon linkIconSide jq-ftueClientBubbleSource"
			title="Clients" data-ng-key="clientsNavLeft"> <em class="linkIconImage icongroup24"></em>
		</a>
	</div>
	<%--
	<div
		class="utilBarItemTasks ${page == 'Business'? 'utilBarItemBActive' : ''}">
		<span class="screenReaderText">My Business</span>
		<a href="<c:url value='/secure/page/business'/>" role="menuitem" class="linkIcon linkIconSide"
			title="My Business" data-ng-key="businessNavLeft"> <em class="linkIconImage iconcompany24"></em>
		</a>
	</div>
	--%>
	<div
		class="utilBarItemTasks ${page == 'tasks'? 'utilBarItemBActive' : ''}">
		<span class="screenReaderText">Your Tasks</span>
		<a href="<c:url value='/secure/page/tasks'/>" role="menuitem" class="linkIcon linkIconSide"
			title="Your tasks" data-ng-key="mytasksNavLeft"> <em class="linkIconImage icontasks24"></em>
		</a>
	</div>
    <div
		class="utilBarItemNews ${page == 'productsAndNews'? 'utilBarItemBActive' : ''}">
		<span class="screenReaderText">Products and news</span>
		<a href="<c:url value='/secure/page/productsandnews'/>" role="menuitem"
			class="linkIcon linkIconSide" title="Products and news" data-ng-key="productsandnewsNavLeft">
	        <em class="linkIconImage iconnewsProduct"></em>
		</a>
	</div>
</security:authorize>
<security:authorize ifAnyGranted="ROLE_INVESTOR">
	<div
		class="utilBarItemClients ${page == 'Clients'? 'utilBarItemBActive' : ''} jq-ftueClientBubbleSourceWrap">
		<!-- TODO: portfolio id and client id need to be clarified. -->
		<span class="screenReaderText">Accounts</span>
		<a href="<c:url value='/secure/page/${person.clientId}/${primaryPortfolioId}/overview'/>" role="menuitem"
			class="linkIcon linkIconSide jq-ftueClientBubbleSource" title="Accounts" data-ng-key="accountNavLeft"> <em
			class="linkIconImage iconindividual24"></em>
		</a>
	</div>
    <div
		class="utilBarItemNews ${page == 'productsAndNews'? 'utilBarItemBActive' : ''}">
		<span class="screenReaderText">Products and news</span>
		<a href="<c:url value='/secure/page/productsandnews'/>" role="menuitem"
			class="linkIcon linkIconSide" title="Products and news" data-ng-key="productsandnewsNavLeft">
	        <em class="linkIconImage iconnewsProduct"></em>
		</a>
	</div>
</security:authorize>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/globalLeftNavigation.js'/>"></script>
