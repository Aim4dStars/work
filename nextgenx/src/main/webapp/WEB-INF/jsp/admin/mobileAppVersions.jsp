<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- List of existing platform versions-->
<div class="sectionContentItem jq-contactDetailsWrap">
  <h2 class="mainHeaderItem mainHeaderItemMod3">
    Existing Mobile platform - version
  </h2>
  <div class="formLabel">
    <table class="inputStyleFive">
      <c:forEach items="${mobileAppVersions}" var="appVersion">
        <tr>
          <td>${appVersion.key.id}</td>
          <td>${appVersion.minVersion}</td>
        </tr>
      </c:forEach>
    </table>
  </div>
</div>

<!-- Update the existing platform versions-->
<div class="sectionContentItem jq-contactDetailsWrap">
  <form action="" class="jq-updateVersionForm"  id="updateAppVersion" method="POST">
    <div class="formBlock clearFix">
      <h2 class="mainHeaderItem mainHeaderItemMod3">
        Update an existing platform - version
      </h2>
      <label for="platform">Platform</label>
      <select class="jq-updatePlatform" id="platformList" name="platform" onchange="onPlatformChange(${appVersion.key.id})">
        <c:forEach items="${mobileAppVersions}" var="appVersion">
          <option value="${appVersion.key.id}">${appVersion.key.id}</option>
        </c:forEach>
      </select>
    </div>
    <div class="inputWrapper iconWrapper">
      <label for="version">New version (in format X.Y.Z, Y,Z are optional)</label>
      <input class="inputStyleFive jq-updateVersion" data-validation="validate[required,custom[customFunction]]" name="version" type="text"/>
    </div>
    <input class="actionButtonIcon jq-formSubmit" id="jq-updateMobileAppVersion" style="display:block; margin-top: 20px;text-align: center;" type="submit" value="Update"/>
  </form>
</div>

<!-- Add the existing platform versions-->
<div class="sectionContentItem jq-contactDetailsWrap">
  <form action="" class="jq-addVersionForm"  id="addAppVersion" method="POST">
    <div class="formBlock clearFix">
      <h2 class="mainHeaderItem mainHeaderItemMod3">
        Add a new platform - version
      </h2>
      <div class="inputWrapper iconWrapper">
        <label for="version">New platform</label>
        <input class="inputStyleFive jq-addPlatform" name="platform" style="margin-bottom: 20px;" type="text"/>
        <label for="version">Version (in format X.Y.Z, Y,Z are optional)</label>
        <input class="inputStyleFive jq-addVersion" data-validation="validate[required,custom[customFunction]]" name="version" type="text"/>
      </div>
      <input class="actionButtonIcon jq-formSubmit" id="jq-addMobileAppVersion" style="display:block; margin-top: 20px;text-align: center;" type="submit" value="Add"/>
    </div>
  </form>
</div>

<script language="javascript" type="text/javascript" src="/ng/public/static/js/client/desktop/pages/mobileAppVersions.js"></script>
