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
function clearCache(cacheName)
{
	var form = document.getElementById('cacheList');
	
    form.action = 'cacheMaintenance/' + cacheName + '/clearCache';
    form.submit();
}

function clearAllCaches()
{
	var form = document.getElementById('cacheList');
	
	form.action = 'cacheMaintenance/clearAllCaches';
	form.submit();
}

function lookUpCache(cacheName, id)
{
	var form = document.getElementById('cacheLookup');
	var cacheKey = document.getElementById(id).value;

	document.getElementById('lookUpCacheName').value = cacheName;
	document.getElementById('lookUpCacheKey').value = cacheKey;
	form.submit();
}
</script>

<br/>
<form id="cacheList" class="jq-cacheClearForm" method="POST" action="cacheMaintenance">
	<input type="hidden" name="token" value="<c:out value="${token}"/>"/>
	<input type="hidden" name="cssftoken" value="<c:out value="${cssftoken}"/>"/>
	
	<table>
		<tr>
			<th>#</th>
			<th>Cache</th>
			<th>Object count</th>
			<th>Cache Hits</th>
			<th>Cache Misses</th>
			<th>Average Get Time</th>
			<th colspan="2">Cache Actions</th>
		</tr>
	
		<c:forEach var="cache" items="${caches}" varStatus="status">
		<c:set var="cacheNameItem" value="##${cache.name}##" />
		<tr>			
			<td align="right">${status.index + 1}</td>		
			<td>${cache.name}</td>
			<td align="right">${cache.statistics.objectCount}</td>
			<td align="right">${cache.statistics.cacheHits}</td>
			<td align="right">${cache.statistics.cacheMisses}</td>
			<td align="right">${cache.statistics.averageGetTime}</td>
			<td align="center">
				<c:if test="${!fn:contains(staticDataCacheNames, cacheNameItem)}">
					<a href="#" onClick="clearCache('${cache.name}')">Clear</a>
				</c:if>
	   		</td>
			<td align="left">
				<input id="lookUp_${status.index}" size="10" maxsize="10" value="<c:out value="${cacheName == cache.name ? cacheKey : ''}" />" />
				&nbsp;
				<a href="#" onClick="lookUpCache('${cache.name}', 'lookUp_${status.index}')">Look up</a>
				<c:if test="${cacheName != null && cacheName == cache.name}">
					<br/>
					<br/>
					<c:out value="result: ${cacheValue == null ? '(none)' : cacheValue}" />
				</c:if>
	   		</td>
		</tr>
		</c:forEach>
	</table>

	<div class="footnote">
	Note: Cache clearing only applies to non-static data.
	</div>
	
	<p/>
	
	<br/>
	<a href="#" style="visibility:hidden" onClick="clearAllCaches()" class="primaryButton"><span>Clear all caches</span></a>
	&nbsp;<a href="<c:url value='/secure/page/home'/>" class="primaryButton"><span>Home</span></a>
</form>


<form id="cacheLookup" class="jq-cacheLookupForm" action="cacheLookUp" method="POST">
	<input name="token" type="hidden" value="<c:out value="${token}"/>" />
	<input name="cssftoken" type="hidden" value="<c:out value="${cssftoken}"/>" />
	<input id="lookUpCacheName" name="name" type="hidden"value="" />
	<input id="lookUpCacheKey" name="key" type="hidden" value="" />
</form>

</body>