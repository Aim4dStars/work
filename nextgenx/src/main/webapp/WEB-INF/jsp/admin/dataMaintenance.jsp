<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<body style="padding-left: 10px">

<h3>
Static Data maintenance for server ${machineName}
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

h4 {
	margin-top: 40px;
}

select:not(.lp_select_field) {
	display: inline;
	margin-right: 20px;
}

select {
	display: inline;
	margin-right: 20px;
}

.note {
	font-size: 85%;
}

.searchField .note {
	display: inline;
}

.queryNote {
	margin: 5px 0 0 85px;
}

.inputLabel {
	display: inline-block;
	width: 80px;
}

.jq-cacheQueryForm {
	margin: 20px 0 20px 0;
}

.cacheSearchButton {
	margin-left: 20px;
}

.cacheSearchResults {
	margin-top: 30px;
}

.noSearchResult {
	color: #0095c8;
}
</style>

<script type="text/javascript">
function reload(dataLoaderName)
{
	var form = document.getElementById('dataLoader');
	
	form.action = 'dataMaintenance/' + dataLoaderName + '/reload';
	form.submit();
}
</script>
<br/>
<form id="dataLoader" class="jq-dataLoaderForm" method="POST" action="dataMaintenance">
	<input type="hidden" name="token" value="<c:out value="${token}"/>"/>
	<input type="hidden" name="cssftoken" value="<c:out value="${cssftoken}"/>"/>
	
	<table>
		<tr>
			<th>#</th>
			<th>Name</th>
			<th>Description</th>
			<th>Searchable Attributes</th>
			<th>Action</th>
		</tr>
		
		<c:forEach var="dataLoader" items="${dataLoaders}" varStatus="status">
		<c:set var="searchAttrs" value="${cacheSearchAttributes[dataLoader.cacheType]}" />
		<tr>
			<td>${status.index + 1}</td>
			<td>${dataLoader.name}</td>
			<td>${dataLoader.description}</td>
			<td>
				<c:forEach var="searchAttr" items="${searchAttrs}" varStatus="searchAttrStatus">
					<c:if test="${searchAttrStatus.index > 0}"><br/></c:if>
					<c:out value="${searchAttr}" />
				</c:forEach>
			</td>
			<td align="center">
				<a href="#" onClick="reload('${dataLoader.name}')">Reload</a>
	   		</td>
		</tr>
		</c:forEach>
	</table>
	
	<br/>
	<a href="<c:url value='/secure/page/home'/>" class="primaryButton"><span>Home</span></a>
</form>

<h4>Cache Search</h4>
<form class="jq-cacheQueryForm" method="POST" action="cacheQuery">
	<input type="hidden" name="token" value="<c:out value="${token}"/>"/>
	<input type="hidden" name="cssftoken" value="<c:out value="${cssftoken}"/>"/>
	
	<div class="searchField">
		<label class="inputLabel" for="cacheType">Static data:</label>
		<select name="name">
			<c:forEach var="dataLoader" items="${dataLoaders}">
				<c:if test="${not empty cacheSearchAttributes[dataLoader.cacheType]}">
					<option <c:if test="${dataLoader.name == staticDataName}">selected</c:if>><c:out value="${dataLoader.name}"/></option>
				</c:if>
			</c:forEach>
		</select>
		<div class="note">(Only searchable static data is available for selection)</div>
	</div>
	
	
	<div class="searchField">
		<label class="inputLabel" for="query">Query:</label>
		<input name="query" size="60" maxsize="120" value="<c:out value="${staticDataQuery}"/>" />
		<input name="search" class="cacheSearchButton" type="submit" value="Search" />
	</div>
	
	<div class="queryNote">
		<div class="note">
			Query examples using Lucene (for OE hierarchy):
			<ul>
				<li>brokerUser.jobKey:71819</li>
				<li>brokerUser.jobKey:7181*</li>
				<li>+(brokerUser.jobKey:7181*) +(brokerUser.firstName:Ric*)</li>
				<li>+brokerUser.lastName:P* +brokerUser.jobRole:ASSISTANT</li>
			</ul>
		</div>
	</div>
	
	<c:if test="${searchResults != null}">
		<div class="cacheSearchResults">
			<c:choose>
				<c:when test="${not empty searchResults}">
					<table>
						<tr>
							<th>#</th>
                            <th>Cache key</th>
							<th>Search attribute values</th>
                            <th>Search object</th>
						</tr>
						<c:forEach var="result" items="${searchResults}" varStatus="status">
						<tr>
							<td align="right"><c:out value="${status.index + 1}" /></td>
                            <td>
                                <c:out value="${result.cacheKey}" />
                            </td>
							<td>
								<c:out value="${result.searchAttributes}" />
							</td>
                            <td>
                                <c:out value="${result.cacheElement}" />
                            </td>
						</tr>
						</c:forEach>
					</table>
				</c:when>
				<c:otherwise>
					<div class="noSearchResult">(no search results)</div>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
	
	<c:if test="${searchError != null}">
		<div class="error">
			<c:out value="${searchError}" />
		</div>
	</c:if>
</form>

</body>