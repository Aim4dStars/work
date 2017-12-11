
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div class="messageBubble jq-ftueMessageBubble jq-ftueClientsBubble  noDisplay">
        <ul class="messageBubbleTools clearFix">
            <li class="jq-closeMessageBubble cursor">
              <a href="#" class="baseLink baseLinkClear"> <em class="iconclose"><span>Close</span></em> </a>
            </li>
            <li class="divider">
               |
            </li>
            <li class="jq-dismissMessageBubble cursor">
                <a href="#" class="baseLink baseLinkClear">
                    <em class="iconviewdisabled"></em>  <span>Dismiss all</span>
                </a>
            </li>
            <li class="arrowLeft jq-arrowPointer"></li>

        </ul>


        <ul class="messageBubbleContent">
            <li class="infoIcon"><em class="iconhelp iconhelpMod1"><span>Help</span></em></li>
            <li class="messageText">
                <security:authorize ifAnyGranted="ROLE_ADVISER">
                    <cms:content name="adviserClientsTabText"/>
                </security:authorize>
                <security:authorize ifAnyGranted="ROLE_INVESTOR">
                    <cms:content name="investorHomeTabText" />
                </security:authorize>
            </li>
        </ul>
</div>