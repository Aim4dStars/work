<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<spring:eval var="buildNumber" expression="@environment.getProperty('nextgen.version')" />
<spring:eval var="logoutUrls" expression="@environment.getProperty('security.logout.url.adviser')" />

<security:authorize ifAnyGranted="ROLE_INVESTOR">
    <spring:eval var="logoutUrls" expression="@environment.getProperty('security.logout.url.investor')" />
</security:authorize>

<spring:eval var="logoutUrl" expression="@environment.getProperty('security.logout.url')" />
<%-- URL used to clear webseal session on registration when getting SMS code, in case its session still lingers on after nextgen session has timed out --%>
<spring:eval var="logoutSuccessUrl" expression="@environment.getProperty('security.logout.redirect.success')" />

<meta charset="utf-8" />

<meta name="description" content="BT Panorama is BT Financial Group’s (BTFG) new and innovative wealth operating system that supports the future architecture of BTFG’s products rolled out on a smart new platform.  It is designed to change, evolve and adapt to the changing needs of our customers, delivered through agile, intuitive technology to make business simpler, faster and more efficient." />
<meta name="version" content="${buildNumber}" />
<meta name="keywords" content="" />

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0"/>

<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/normalize.css"/>" type="text/css"/>
<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/screen.css"/>" type="text/css" id="stylesheet-2560"/>
<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/print.css"/>" type="text/css" media="print"/>
<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/screen-blessed1.css"/>" type="text/css"/>
<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/preauth.css"/>" type="text/css"/>

<link rel="stylesheet" href="<ng:hashurl src="/public/static/css/jjsonviewer.css"/>" type="text/css"/>

<!--[if IE 9]>
  <link rel="stylesheet" href="<ng:hashurl src="/public/static/css/ie9.css"/>" type="text/css" media="screen"/>
<![endif]-->

<!-- Defines brand Switch -->
<cms:content name="brand_css" var="brandCssUrl"/>
<link rel="stylesheet" href="<c:url value="${brandCssUrl}"/>" type="text/css" media="all"/>


    <%--JavaScript file structure--%>
<%--Config--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/config/namespace.js'/>"></script>
<%--/Config--%>
<script type="text/javascript">
    org.bt.token = "<c:out value="${cssftoken}"/>";
    org.bt.contextPath = "<ng:hashurl src=""/>/";
    org.bt.url = {
        logout : "${logoutUrls}",
        afterLogout : "${logoutUrl}",
        logoutSuccess: "${logoutSuccessUrl}"
    };
  var ie = (function(){
          var undef,
          v = 3,
          div = document.createElement('div'),
          all = div.getElementsByTagName('i');
          while (
          div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->',
          all[0]
          );
          return v > 4 ? v : document.documentMode;
       }());
  if(ie <=8) {
    window.location.href ="/ng/public/static/page/unsupportedbrowsers.html";
  }
</script>
<%--Libs--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/jQuery.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/jQueryUi.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/handlebars.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/underscore.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/sjcl.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/moment.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/lib/jjsonviewer.js'/>"></script>
<%--/Libs--%>

<%--Helpers--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/helpers/stringHelpers.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/helpers/numberHelpers.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/helpers/arrayHelpers.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/helpers/dateHelpers.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/helpers/utils.js'/>"></script>
<%--/Helpers--%>

<%--Plugins--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/isMobile.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/placeholder.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/passwordPolicy.js'/>"></script>
  <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/usernamePolicy.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/formatDate.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/formatNumber.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/validateDate.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/promptMessage.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/removeMessage.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/showServerSideErrors.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/validationEngine.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/createFormSubmitRequestBody.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/resetForm.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/calendar.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/spinner.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/modalBox.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/searchableDropDown.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/dropKick.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/accordionTable.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/tableSorter.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/jScrollPane.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/address.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/cookie.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/printPage.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/base64.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/smsCode.js'/>"></script>
  <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/equalHeights.js'/>"></script>

  <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/plugins/customTootip.js'/>"></script>
<%--/Plugins--%>

<%--Widgets--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/comboBox.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/selectMenu.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/tableSorter.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/editableTextField.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/accordionView.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/flyout.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/accordionForm.js'/>"></script>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/widgets/carousel.js'/>"></script>
<%--/Widgets--%>

<%--Shared--%>
  <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/shared/initDatepicker.js'/>"></script>
<%--Shared--%>

<%--Application--%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/application.js'/>"></script>
<%--/Application--%>

<%-- SAFI device finger print library --%>
    <script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/content/pm_fp.js'/>"></script>
<%--/--%>
<%--/JavaScript file structure--%>
