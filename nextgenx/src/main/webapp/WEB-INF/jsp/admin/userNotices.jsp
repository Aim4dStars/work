<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<div class="back-button">
    <a href="home"> Back to Home</a>
</div>

<!-- Search notices available for user-->
<div class="sectionContentItem">

    <h2 class="mainHeaderItem mainHeaderItemMod3">Search notices available for user</h2>
    <div class="formLabel inputWrapper">

        <form action="userNotices" method="GET">
            <input id="userId" class="formTextInput inputStyleEight" name="userId" type="text" autocomplete="off" required>
            <input value="Search" type="submit" class="actionButtonIcon form-field"/>
        </form>

    </div>
    <c:if test="${userId != null}">
        <div>
            <table class="inputStyleFive">
                <tr>Available notices for user : ${userId} </tr>
                <c:if test="${fn:length(userNotices) gt 0}">
                   <c:forEach items="${userNotices}" var="userNotice">
                       <tr>
                           <td>${userNotice.noticeTypeName} (version: ${userNotice.key.version})</td>
                       </tr>
                   </c:forEach>
                </c:if>

            </table>
        </div>
    </c:if>

</div>

<!-- Latest notices for ALL users-->
<div class="sectionContentItem">
    <h2 class="mainHeaderItem mainHeaderItemMod3">Latest notices</h2>

    <c:if test="${modifiedNotice != null}">
        <h6 class="form-field positive">${modifiedNotice.noticesKey.noticeTypeId.displayText} has been updated.</h6>
    </c:if>

    <c:forEach items="${availableNotices}" var="availableNotice">
    <!--<c:set var="updatedDate" value="${availableNotice.lastUpdatedOn}"/>-->
        <div class="form-field">
            Name: ${availableNotice.noticesKey.noticeTypeId.displayText}<br/>
            Version: ${availableNotice.noticesKey.version}<br/>
            Last updated on: <joda:format value="${availableNotice.lastUpdatedOn}" pattern="MMM dd yyyy"/><br/>
            Description(500 chars): <input id="${availableNotice.noticesKey.noticeTypeId.id}_input" class="form-field" type="text" value="${availableNotice.description}"/>

            <a class="actionButtonIcon jq-noticesType submit-button" href="javascript:void(0);" data-update-value="${availableNotice.noticesKey.noticeTypeId.id}"
                data-version-value="${availableNotice.noticesKey.version}">
                Update
            </a>
        </div>
    </c:forEach>
</div>

<style>
    .submit-button, .back-button, .form-field {
        margin-top: 20px;
    }
</style>

<script language="javascript" type="text/javascript" src="/ng/public/static/js/client/desktop/pages/userNotices.js"></script>
