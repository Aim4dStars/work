<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script>
    $(document).ready(function(){
        $(".tabbable").find(".tab").hide();
        $(".tabbable").find(".tab").first().show();
        $(".tabbable").find(".toggleMenuLine li").first().addClass("ui-tabs-active ui-state-active");
        $(".tabbable").find(".toggleMenuLine").find("li").click(function(){
            tab = $(this).find("a").attr("href");
            $(".tabbable").find(".tab").hide();
            $(".tabbable").find(".toggleMenuLine li").removeClass("ui-tabs-active ui-state-active");
            $(tab).show();
            $(this).addClass("ui-tabs-active ui-state-active");
            return false;
        });
    });
</script>

<spring:eval var="cmsUrl" expression="@environment.getProperty('aem.service.ops.url')" />

<h1 class="pageHeaderItem">Service List</h1>
<div class="toggleMenuLineWrap toggleMenuLineWrapMod7 tabbable">
    <ul class="toggleMenuLine toggleMenuLineMod3">
        <li class="toggleMenuLineItem">
            <a href="#jq-retrieveServices" class="textLink" data-ng-key="intermediariesTab">Retrieve Services (Read)</a>
        </li>
        <li class="toggleMenuLineItem">
            <a href="#jq-maintainServices" class="textLink" data-ng-key="clientsTab">Maintain Services (Update)</a>
        </li>
		<li class="toggleMenuLineItem">
            <a href="#jq-siloMovement" class="textLink" data-ng-key="clientsTab">GCM SILO movement</a>
        </li>
    </ul>

    <div class="tab-content">
        <div id="jq-retrieveServices" class="tab">
            <div class="tableContainer jq-searchResultTableContainer">
                <table class="dataTable">
                    <caption class="caption" style="visibility:hidden;"></caption>
                    <caption class="caption">RETRIEVE SERVICES LIST</caption>
                    <thead>
                        <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                            <th class="dataTableHeader dataTableHeaderFirst alphaAlign" style="width:40%;">Service</th>
                            <th class="dataTableHeader alphaAlign" style="width:60%;">Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/clientDetailsSerch'/>" class="emphasis baseLink">SVC0258 <span class="italicFont">(v11)</span></a>
                                <p class="italicFont">Retrieve Details And Arrangement Relationships For IPs</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service supports the retrieval of customer details.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0258.pdf" class="baseLink emphasis italicFont" target="_blank"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/retriveIpToIpRelationship'/>" class="emphasis baseLink">SVC0260 <span class="italicFont">(v4)</span><br/></a>
                                <p class="italicFont">Retrieve IP To IP Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service supports the retrieval of relationships between Involved Parties.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0260.pdf"  target="_blank" class="baseLink  emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/retrieveIDVDetails'/>" class="emphasis baseLink">SVC0324 <span class="italicFont">(v6)</span><br/></a>
                                <p class="italicFont">Retrieve IDV Details</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service supports the retrieval of IDV Details.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0324.pdf"  target="_blank"  class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/retrivePostalAddress'/>" class="emphasis baseLink">SVC0454 <span class="italicFont">(v1)</span><br/></a>
                                <p class="italicFont">Retrieve Postal Address</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service supports the retrieval of Standard, Non-Standard, Provider and All type of Addresses.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0454.pdf"  target="_blank" class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div id="jq-maintainServices" class="tab">
            <div class="tableContainer jq-searchResultTableContainer">
                <table class="dataTable">
                    <caption class="caption" style="visibility:hidden;"></caption>
                    <caption class="caption">MAINTAIN SERVICES LIST</caption>
                    <thead>
                        <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                            <th class="dataTableHeader dataTableHeaderFirst alphaAlign" style="width:40%;">Service</th>
                            <th class="dataTableHeader alphaAlign" style="width:60%;">Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/maintainArrangementAndrelationshipReq'/>" class="emphasis baseLink">SVC0256 <span class="italicFont">(v1)</span><br/></a>
                                <p class="italicFont">Maintain Arrangement And IP Arrangement Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to associate the account to an existing Organisation or the person. It will allow insert/update of the involved party roles associated to and arrangement.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0256.pdf" class="baseLink emphasis italicFont" target="_blank"
                                > here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/maintainIpToIpRelationshipReq'/>" class="emphasis baseLink">SVC0257 <span class="italicFont">(v1)</span><br/></a>
                                <p class="italicFont">Maintain IP To IP Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to maintain the relationships between IP to IP. It will allow insert/update of the relationship between two involved parties.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0257.pdf"  target="_blank" class="baseLink  emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/maintainIdvDetailsReq'/>" class="emphasis baseLink">SVC0325<span class="italicFont">(v5)</span><br/></a>
                                <p class="italicFont">Maintain IDV Details</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to maintain IDV details of individuals and organisations.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0325.pdf"  target="_blank"  class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/createIndividualIPReq'/>" class="emphasis baseLink">SVC0336<span class="italicFont">(v5)</span><br/></a>
                                <p class="italicFont">Create Individual IP Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to create individual involved party relationships.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0336.pdf"  target="_blank" class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/createOrganisationIPReq'/>" class="emphasis baseLink">SVC0337<span class="italicFont">(v5)</span><br/></a>
                                <p class="italicFont">Create Organisation IP Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to create organisation involved party relationships.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0337.pdf"  target="_blank" class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="#" class="emphasis baseLink">SVC0338<span class="italicFont">(-)</span><br/></a>
                                <p class="italicFont">Modify Individual Customer Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables customer to...
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0338.pdf"  target="_blank" class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="#" class="emphasis baseLink">SVC0339<span class="italicFont">(-)</span><br/></a>
                                <p class="italicFont">Modify Organisation Customer Relationships</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables customer to...
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0339.pdf"  target="_blank"  class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                         <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/maintainIpContactMethodReq'/>" class="emphasis baseLink">SVC0418<span class="italicFont">(v1)</span><br/></a>
                                <p class="italicFont">Maintain IP Contact Methods</p>
                            </td>
                            <td class="dataTableCell nameWrap">
                                This service enables user to maintain involved party contacts.
                                <p>For work instructions click <a href="${cmsUrl}/content/dam/secure/pdfs/work-instructions/WI_GESB_SVC0418.pdf"  target="_blank" class="baseLink emphasis italicFont"> here </a></p>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
		<div id="jq-siloMovement" class="tab">
            <div class="tableContainer jq-searchResultTableContainer">
                <table class="dataTable">
                    <caption class="caption" style="visibility:hidden;"></caption>
                    <caption class="caption">GCM SILO movement</caption>
                    <thead>
                        <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                            <th class="dataTableHeader dataTableHeaderFirst alphaAlign" style="width:40%;">Service</th>
                            <th class="dataTableHeader alphaAlign" style="width:60%;">Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/siloMovementReq'/>" class="emphasis baseLink">GCM SILO movement <span class="italicFont">-</span><br/></a>
                            </td>
                            <td class="dataTableCell nameWrap">
                            </td>
                        </tr>
                        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
                            <td class="dataTableCell nameWrap">
                                <a href="<c:url value='/secure/page/serviceOps/siloMovementTracking'/>" class="emphasis baseLink">GCM SILO movement Tracker<span class="italicFont">-</span><br/></a>
                            </td>
                            <td class="dataTableCell nameWrap">
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        	
    </div>

</div>