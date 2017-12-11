<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


	<div class="messageType2Wrap">   
	    <div class="messageIconWrap">
	        <em class="iconwarning"><span class="screenReaderText">We are currently experiencing technical difficulties.</span></em>
	    </div>
	    <div class="messageBody">
	        <h2>We are currently experiencing technical difficulties.</h2>
	    </div>
	    <div class="messageText">Sorry for the inconvenience. Please try again later.</div>
	</div>
	
	<c:if test="${not empty soapException}">
		<br />
		<h1>Soap Exception:</h1>
		<br />
		<h3>
			<pre>${soapException}</pre>
		</h3>
	</c:if>
	<div style="display:none">
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
	</div>