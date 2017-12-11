<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<body style="padding-left: 10px">

	<br />
	<h1>Unable to process Avaloq Response</h1>
	<br />
	<p>Avaloq returned a response which the web UI was unable to process, more information about the type of exceptions can be found <a href="http://dwgps0026/twiki/bin/view/NextGen/TechnicalAvaloqErrorTypesAndHandling">here </a></p>
    <div class="xmlResponse">
        <h2>Avaloq Response which caused the exception:</h2>
        <p>
           <code><c:out value="${rootCauseException.xmlPayload}"/></code>
        </p>
    </div>

    <h2>The exception thrown was:</h2>
	<p>
		<code>${exception}</code>
	</p>

	<c:if test="${not empty rootCauseException}">
		<br />
		<h2>Full stack trace for the Unmarshalling Exception:</h2>

		<br />
		<div class="stackTrace">
            <p>
               <c:if test="${not empty exceptions}">
                <h2>StackTrace:</h2>
                <br />

                <c:forEach var="exception" items="${exceptions}" varStatus="counter">

                        <code><c:if test="${counter.count > 1}">Caused by: </c:if><u>${exception['class'].name}</u>: ${exception}</code>
                        <br />
                        <br />
                        <c:forEach items="${exception.stackTrace}" var="element">
                            <pre style="padding-left: 30px">${element}</pre>
                        </c:forEach>
                    <br />
                </c:forEach>
                </c:if>
            </p>
        </div>

	</c:if>

</body>