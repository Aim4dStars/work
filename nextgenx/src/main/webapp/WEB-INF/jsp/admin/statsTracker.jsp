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
Stats tracker for server ${machineName}
</h3>

<c:choose>
    <c:when test="${empty report}">
        <p>Nothing to report - no service methods have been invoked yet!</p>
    </c:when>
    <c:otherwise>

<table>
<thead>
    <tr>
    <th>Bean</th>
    <th>Method</th>
    <th>total invocations</th>
    <th>total successful</th>
    <th>total errors</th>
    <th class="number">sample size</th>
    <th>allowable error rate</th>
    <th>Successful</th>
    <th>Errored</th>
    <th>Current Error Rate</th>
    <th>Result</th>
    </tr>
</thead>
<tbody>
    <c:forEach items="${report}" var="bean">
    <tr><td rowspan="${fn:length(bean.value)}">${bean.key}</td>

        <c:forEach items="${bean.value}" var="method" varStatus="status">
            <c:if test="${status.index gt 0}"><tr></c:if>
            <td>${method.key}</td>
            <td>${method.value.count}</td>
            <td>${method.value.callHistorySize}</td>
            <td>${method.value.errors}</td>
            <td>${method.value.sampleSize}</td>
            <td>${method.value.allowableErrorRate}</td>
            <td>${method.value.successesInSampleSize}</td>
            <td>${method.value.errorsInSampleSize}</td>
            <td>${method.value.currentErrorRate}</td>
            <td>${method.value.currentStatus}</td>
            </tr>
        </c:forEach>
    </c:forEach>
</tbody>
</table>

    </c:otherwise>
</c:choose>


