<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script language="javascript" type="text/javascript"
	src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/siloStatus.js'/>"></script>
<body>
	<h1 class="pageHeaderItem">Silo Movement status</h1>
	<div id="key" class="noDisplay">${ key}</div>
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>"
		name="cssftoken" />
	<div id="siloMovementResponse">
		<table>
			<tr>
				<th><strong>Services</strong></th>
				<th><strong>Status</strong></th>
			</tr>
			<tr>
				<td>Service 258- Retrieve Details And Arrangement Relationships
					For Involved Parties</td>
				<td><div id="service258"></div></td>

			</tr>
			<tr>
				<td>Service 324- Retrieve IDV Details</td>
				<td><div id="service324"></div></td>

			</tr>
			<tr>
				<td>Service 336- Create Individual IP Relationships</td>
				<td><div id="service336"></div></td>

			</tr>
			<tr>
				<td>Service 325- Maintain IDV Details</td>
				<td><div id="service325"></div></td>
			</tr>
			<tr>
				<td>Service 256- Maintain Arrangement And IP Arrangement
					Relationships- Create Relationships</td>
				<td><div id="service256_CRT"></div></td>

			</tr>
			<tr>
				<td>Service 256- Maintain Arrangement And IP Arrangement
					Relationships- End Relationships</td>
				<td><div id="service256_END"></div></td>

			</tr>
		</table>
	</div>
</body>