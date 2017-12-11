<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<ul class="listWicon setBottomGutter">
    <li>
        <a href="javascript:history.go(-1)" class="baseLink baseLinkMod1" title="Back to previous page">
            <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Back to previous page</span>
        </a>
    </li>
</ul>
<div>

        <jsp:useBean id="total" class="java.util.LinkedHashMap"/>
        <c:set target="${total}" property="individual" value="Individual" />
        <c:set target="${total}" property="joint" value="Joint" />
        <c:set target="${total}" property="individualSMSF" value="Individual SMSF" />
        <c:set target="${total}" property="newIndividualSMSF" value="Individual SMSF" />
        <c:set target="${total}" property="corporateSMSF" value="Corporate SMSF" />
        <c:set target="${total}" property="newCorporateSMSF" value="Corporate SMSF" />
        <c:set target="${total}" property="corporateTrust" value="Corporate Trust" />
        <c:set target="${total}" property="individualTrust" value="Individual Trust" />
        <c:set target="${total}" property="company" value="Company" />
        <c:set target="${total}" property="superAccumulation" value="Super" />
        <c:set target="${total}" property="superPension" value="Pension" />

        <span><h1 class="mainHeaderItem">Application Summary - <c:out value="${total[clientApplication.investorAccountType]}"/> account</h1>
     </span>

    <c:if test="${clientApplication.asimProfile}">
        <h2 class="mainHeaderItem mainHeaderItemMod3">The client has permission to transact and manage their account themselves.</h2>
    </c:if>
    <span class="floatRight"><a title="Print" class="toolActionx iconSetBorder" href="javascript:window.print()">
        <em class="iconprint"><span>Print</span></em>
    </a></span>
</div>

<c:choose>
  <c:when test="${clientApplication.investorAccountType == 'individual' || clientApplication.investorAccountType == 'joint' ||
  clientApplication.investorAccountType == 'superAccumulation'}">
    <c:choose>
      <c:when test="${clientApplication.applicationOriginType == 'WestpacLive'}">
        <c:if test="${clientApplication.investorAccountType == 'superPension'}">
          <%@ include file="pensionEligibility.jsp" %>
        </c:if>
        <%@include file="directInvestorDetail.jsp"%>
        <c:if test="${clientApplication.investorAccountType == 'individual' || clientApplication.investorAccountType == 'superPension'}">
          <%@ include file="linkedAccount.jsp" %>
        </c:if>
        <c:if test="${clientApplication.investmentChoice != null}">
          <%@ include file="investmentChoice.jsp" %>
        </c:if>
      </c:when>
      <c:otherwise>
        <%@ include file="investorAppDetail.jsp" %>
        <%@ include file="accountSetting.jsp" %>
        <%@ include file="linkedAccount.jsp" %>
        <%@ include file="fees.jsp" %>
        <%@ include file="approvalDetail.jsp" %>
      </c:otherwise>
    </c:choose>
  </c:when>

    <c:when test="${clientApplication.investorAccountType == 'superPension'}">
        <%@ include file="pensionEligibility.jsp" %>
        <c:choose>
            <c:when test="${clientApplication.applicationOriginType == 'WestpacLive'}">
                <%@ include file="directInvestorDetail.jsp"%>
                <%@ include file="linkedAccount.jsp" %>
            </c:when>
            <c:otherwise>
                <%@ include file="investorAppDetail.jsp" %>
                <%@ include file="accountSetting.jsp" %>
                <%@ include file="linkedAccount.jsp" %>
                <%@ include file="fees.jsp" %>
            </c:otherwise>
        </c:choose>
        <%@ include file="approvalDetail.jsp" %>
    </c:when>

  <c:when test="${clientApplication.investorAccountType == 'individualSMSF'}">
    <%@  include file="smsfDetail.jsp" %>
    <%@  include file="trusteeDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.members}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.trustees}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>

  <c:when test="${clientApplication.investorAccountType == 'newIndividualSMSF'}">
    <%@  include file="smsfDetail.jsp" %>
    <%@  include file="trusteeDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.members}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.trustees}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
    </c:when>

  <c:when test="${clientApplication.investorAccountType == 'corporateSMSF'}">
   <c:set var="company" value ="${clientApplication.smsf.company}"/>
    <%@  include file="smsfDetail.jsp" %>
    <%@  include file="companyDetail.jsp" %>
    <%@  include file="directorDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.shareholdersAndMembers}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.directors}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>

  <c:when test="${clientApplication.investorAccountType == 'newCorporateSMSF'}">
   <c:set var="company" value ="${clientApplication.smsf.company}"/>
    <%@  include file="smsfDetail.jsp" %>
    <%@  include file="companyDetail.jsp" %>
    <%@  include file="directorDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.shareholdersAndMembers}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.directors}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>

  <c:when test="${clientApplication.investorAccountType == 'company'}">
    <c:set var="company" value ="${clientApplication.company}"/>
    <%@  include file="companyDetail.jsp" %>
    <%@ include file="investorAppDetail.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.shareholders}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.investors}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>

  <c:when test="${clientApplication.investorAccountType == 'individualTrust'}">
    <%@  include file="trustDetail.jsp" %>
    <%@  include file="trusteeDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.shareholdersAndMembers}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.trustees}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>

  <c:when test="${clientApplication.investorAccountType == 'corporateTrust'}">
    <%@  include file="trustDetail.jsp" %>
    <c:set var="company" value ="${clientApplication.trust.company}"/>
    <%@  include file="companyDetail.jsp" %>
    <%@  include file="directorDetails.jsp" %>
    <%@ include file="accountSetting.jsp" %>
    <c:set var="members" value ="${clientApplication.shareholdersAndMembers}"/>
    <c:set var="nominatedMembers" value ="${clientApplication.directors}"/>
    <%@ include file="memberDetails.jsp" %>
    <%@ include file="linkedAccount.jsp" %>
    <%@ include file="fees.jsp" %>
    <%@ include file="approvalDetail.jsp" %>
  </c:when>
  <c:otherwise>
    <h6 class="mainHeaderItem">No result was found. Please go back to previous page and try a different search.</h1>
  </c:otherwise>
</c:choose>

<ul class="listWicon">
    <li>
        <a href="javascript:history.go(-1)" class="baseLink baseLinkMod1" title="Back to previous page">
            <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Back to previous page</span>
        </a>
    </li>
</ul>



