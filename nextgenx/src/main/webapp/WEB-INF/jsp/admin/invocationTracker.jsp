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
Invocation tracker for server ${machineName}
</h3>

<c:choose>
    <c:when test="${empty report}">
        <p>Nothing to report - no service methods have been invoked yet!</p>
    </c:when>
    <c:otherwise>

<table>
<thead>
    <tr><th>Bean</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th><th>Method</th><th>&nbsp;&nbsp;&nbsp;</th><th>Last invoked</th><th>&nbsp;&nbsp;&nbsp;</th><th class="number">Duration</th><th>&nbsp;&nbsp;&nbsp;</th><th>Result</th></tr>
</thead>
<tbody>
    <c:forEach items="${report}" var="bean">
    <tr><td rowspan="${fn:length(bean.value)}">${bean.key}</td>
    
        <c:forEach items="${bean.value}" var="method" varStatus="status">
            <c:if test="${status.index gt 0}"><tr></c:if>
             <td>&nbsp;&nbsp;&nbsp;</td>
            <td>${method.key}</td>
              <td>&nbsp;&nbsp;&nbsp;</td>
            <td><fmt:formatDate value="${method.value.startDate}" pattern="yyyy-MM-dd HH:mm:ss.SSS" /></td>
            <td>&nbsp;&nbsp;&nbsp;</td>
            <td class="number"><fmt:formatNumber value="${method.value.duration}" pattern="#,##0"/> ms</td>
            <td>&nbsp;&nbsp;&nbsp;</td>
            <c:choose>
                <c:when test="${empty method.value.error}"><td>OK</td></c:when>
                <c:otherwise><td class="error">ERROR</td></c:otherwise>
            </c:choose>
            </tr>
        </c:forEach>
    </c:forEach>
</tbody>
</table>

    </c:otherwise>
</c:choose> 


