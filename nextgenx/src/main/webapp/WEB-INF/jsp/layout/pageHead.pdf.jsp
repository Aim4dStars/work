<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="description" content="" />
<meta name="keyword" content="" />

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0"/>

<!-- Original SASS generated PDF.CSS file location -->
<%-- <link rel="stylesheet" href="<ng:hashurl src="/public/static/css/pdf.css" pdf="true"/>" type="text/css" /> --%>

<!-- Modified static PDF.CSS file - Removed '../' from image path -->
<link rel="stylesheet" href="<ng:hashurl src="/public/static/pdf.css" pdf="true"/>" type="text/css" />

<!-- Defines brand Switch -->
<cms:content name="brand_css" var="brandCssUrl"/>
<link rel="stylesheet" href="<c:url value="${brandCssUrl}"/>" type="text/css" media="all"/>

