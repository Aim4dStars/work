<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
	$(document).ready(function() {
		var jsonResponse = $("#jsonData").html();	
		$("#json-view").jJsonViewer(jsonResponse);
	});
</script>
<script>
function back() {
    window.history.back();
}
</script>
<body>
    <h1 class="pageHeaderItem">Response</h1>

    <div class="setBottomGutter">
        <a href="javascript:back();" class="baseLink baseLinkClear">
            <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back</span> </a>
    </div>
	<p id="json-view" class="">
		<textarea readonly class="jq-jsonDisplay" id="jsonData">
	        ${serviceOpsModel[0].rawResponse}
	    </textarea>
    </p>
    
    <div class="setBottomGutter">
        <a href="javascript:back();" class="baseLink baseLinkClear">
            <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back</span> </a>
    </div>
</body>

















