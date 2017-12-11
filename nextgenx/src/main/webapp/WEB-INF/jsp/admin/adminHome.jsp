<%@ page language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>


<h3>
Admin page for server ${machineName}
</h3>

<ul>
<li><a href="home">Here</a></li>
<li><a href="stats">Stats Tracker</a></li>
<li><a href="invocation">Invocation</a></li>
<li><a href="cacheMaintenance">Cache Info</a></li>
<li><a href="dataMaintenance">Static Data</a></li>
<li><a href="cacheMemory">Cache Memory Usage</a></li>
<li><a href="avaloqVersion">Avaloq Installation Information</a></li>
<li><a href="toggles">Feature Toggles</a></li>
<li><a href="mobileAppVersions">Mobile App versions</a></li>
<li><a href="userNotices">User notices</a></li>
</ul>


