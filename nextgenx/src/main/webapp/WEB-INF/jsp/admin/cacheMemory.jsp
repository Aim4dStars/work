<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<body style="padding-left: 10px">

<h3>
Cache Status for server ${machineName}
</h3>

<style>
table {
    border-style: solid;
    border-width: 1px 1px 1px 1px;
    border-spacing: 10px;
    border-collapse: collapse;
}

th,td {
    border-style: solid;
    margin: 0;
    padding: 8px;
    border-width: 1px 1px 0 0;
}

th {
	font-weight: bold;
}

.footnote {
	font-size: 85%;
	margin-top: 10px;
}
</style>

<script type="text/javascript">
</script>

<br/>
<form id="cacheMemoryList" class="jq-cacheClearForm" method="GET" action="cacheMemory">
	<input type="hidden" name="token" value="<c:out value="${token}"/>"/>
	<input type="hidden" name="cssftoken" value="<c:out value="${cssftoken}"/>"/>
	
	<table>
		<tr>
			<th>#</th>
			<th>Cache</th>
			<th>Object Size</th>
		</tr>
	
		<c:forEach var="cache" items="${cacheSizes}" varStatus="status">
		<c:set var="cacheNameItem" value="##${cache.key}##" />
		<tr>			
			<td align="right">${status.index + 1}</td>		
			<td>${cache.key}</td>
			<td align="left">${cache.value}</td>
		</tr>
		</c:forEach>
	</table>

	<div class="footnote">
	Note: Cache clearing only applies to non-static data.
	</div>
	
	<p/>
</form>


<form id="cacheLookup" class="jq-cacheLookupForm" action="cacheLookUp" method="POST">
	<input name="token" type="hidden" value="<c:out value="${token}"/>" />
	<input name="cssftoken" type="hidden" value="<c:out value="${cssftoken}"/>" />
	<input id="lookUpCacheName" name="name" type="hidden"value="" />
	<input id="lookUpCacheKey" name="key" type="hidden" value="" />
</form>

</body>