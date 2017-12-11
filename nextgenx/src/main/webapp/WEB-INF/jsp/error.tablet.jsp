<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<body style="padding-left: 10px">

	<br />
	<h1>An unhandled exception has been caught:</h1>
	<br />
	<h3>
		<code>${exception}</code>
	</h3>

	<c:if test="${not empty soapException}">
		<br />
		<h1>Soap Exception:</h1>
		<br />
		<h3>
			<pre>${soapException}</pre>
		</h3>
	</c:if>

	<c:if test="${not empty exceptions}">
		<br />
		<h1>StackTrace:</h1>
		<br />

		<c:forEach var="exception" items="${exceptions}" varStatus="counter">
			<h3>
				<code><c:if test="${counter.count > 1}">Caused by: </c:if><u>${exception['class'].name}</u>: ${exception}</code>
				<br />
				<br />
				<c:forEach items="${exception.stackTrace}" var="element">
					<pre style="padding-left: 30px">${element}</pre>
				</c:forEach>
			</h3>
			<br />
		</c:forEach>
	</c:if>

</body>