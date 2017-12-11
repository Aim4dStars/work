<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:eval var="env" expression="@environment.getProperty('environment')" />
<spring:eval var="buildNumber" expression="@environment.getProperty('version')" />
<spring:eval var="cmsUrl" expression="@environment.getProperty('aem.service.ops.url')" />

<div class="layoutFooterWrp layoutFooterWrpMod1">
    <em class="utilCopyright">
        <span class="noDisplay">BT Powered by NextGen</span>
    </em>
    <ul class="layoutFooterNav">       
        <li><a class="primaryLink"  href="${cmsUrl}/content/secure/help-and-support/bt/en/adviser/useful-information/accessibility.html" target="_blank">Accessibility</a></li>
        <li><a class="primaryLink"  href="${cmsUrl}/content/secure/help-and-support/bt/en/adviser/home/welcome/contact-us.html" target="_blank">Contact us</a></li>
        <li><a class="primaryLink"  href="${cmsUrl}/content/secure/help-and-support/bt/en/adviser/home/welcome/terms-of-use.html" target="_blank">Terms of use</a></li>
        <li><a class="primaryLink"  href="${cmsUrl}/content/secure/help-and-support/bt/en/adviser/home/welcome/privacy-statement.html" target="_blank">Privacy statement</a></li>
    </ul>
</div> <!-- layoutFooterWrp -->
