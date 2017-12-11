<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<body style="padding-left: 10px">

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script>
$(function() {
$( "#releaseList" ).accordion({
    heightStyle: "content",
    collapsible: true,
    active:false
    });
});
</script>

<style>
span {
    padding:4px;
    display:block;
    float: left;
}

div {
    display:block;

    }

div.change {
   clear:both;
   }
div.package {
    clear:both;
    }

</style>


<h3>Avaloq Version Information</h3>

<p><h4>Installed Avaloq Version is <c:out value="${avaloqVersion.installationUid}"/></h4></p>

<div id="releaseList">
<c:forEach var="releasePackage" items="${avaloqVersion.avaloqReleasePackages}">

    <h5>Release Package ${releasePackage.avaloqReleaseName} version ${releasePackage.avaloqPackageUid}</h5>
    <div class="package">
    <h6>List Of Changes:</h6>
    <c:forEach var="change" items="${releasePackage.avaloqChanges}">
        <div class="change">
            <span class="changeId">${change.id}</span><span class="installationTime">Installation Time ${change.installationTime}</span>
            <c:if test="${not empty change.name}">
                <span class="name">Name: ${change.name}</span>
                <c:forEach items="${change.authors}" var="author"><span class="author">author</span></c:forEach>
                <span class="taskId">${change.taskId}</span><span class="taskName">${change.taskName}</span>
            </c:if>
        </div>

    </c:forEach>
    </div>


</c:forEach>
</div>


<body>