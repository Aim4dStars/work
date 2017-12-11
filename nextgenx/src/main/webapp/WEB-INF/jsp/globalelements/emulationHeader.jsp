<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="emuHeaderWrap">
  <div class="emuHeaderItem emuHeaderItemFirst">
    <a href="#nogo" target="" title="Change user" data-ng-key="" class="iconSetBorder iconSeticonSetBorderDrop jq-emuChangeUserLink">
      <em class="iconindividual"><span>Download</span></em>
      <span class="iconarrowfulldown"></span>
    </a>
    <div class="contentDialog contentDialogMod2 noDisplay jq-emuChangeUserWrapper" role="dialog" data-ng-key="" id="ui-id-1" aria-labelledby=""  aria-expanded="" aria-hidden="">
         <em class="expandArrow expandArrowMod6"></em>
         <ul class="globalNavList">
            <li>
                <a class="baseLink" title="Change user" href="<c:url value='/secure/page/serviceOps/home'/>">Change user</a>
            </li>
             <li>
             <!-- This fixes the service operator issue of returning back to the same user which was being emulated on the search detail screen -->
             <c:choose>
                <c:when test="${person.clientId != person.encodedPersonId}">
                  <a class="baseLink" title="Actions for this user" href="<c:url value='/secure/page/serviceOps/${person.encodedPersonId}/detail'/>" >Actions for this user</a>
                </c:when>
                <c:otherwise>
                    <a class="baseLink" title="Actions for this user" href="<c:url value='/secure/page/serviceOps/${person.clientId}/detail'/>" >Actions for this user</a>
                </c:otherwise>
              </c:choose>
            </li>
         </ul>
    </div>
  </div>
  <div class="emuHeaderItem emuHeaderItemLast">
    <ul class="emuHeaderItemText">
      <li class="large">Emulating <span>${person.firstName} ${person.lastName}</span></li> 
      <li class="small">${dealerGroup}</li>
    </ul>
  </div>
</div><!-- emuHeaderWrap -->