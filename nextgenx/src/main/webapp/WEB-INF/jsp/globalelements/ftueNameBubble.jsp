<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div class="messageBubble jq-ftueMessageBubble jq-ftueNameBubble noDisplay">
        <ul class="messageBubbleTools clearFix">

            <li class="jq-dismissMessageBubble cursor">
                <a href="#" class="baseLink baseLinkClear">
                    <em class="iconviewdisabled"></em>  <span>Dismiss all</span>
                </a>
            </li>

            <li class="arrowTop jq-arrowPointer"></li>
        </ul>



     <ul class="messageBubbleContent">
        <li class="infoIcon"><em class="iconhelp iconhelpMod1"><span>Help</span></em></li>
        <li class="messageText">
            <security:authorize ifAnyGranted="ROLE_ADVISER">
                <cms:content name="adviserNameInfoText"/>
            </security:authorize>
            <security:authorize ifAnyGranted="ROLE_INVESTOR">
                <cms:content name="investorAdviserNameInfoText" />
            </security:authorize>
        </li>
    </ul>

</div>