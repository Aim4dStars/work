<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<body class="bodyContainer flexContainer bg-blurred">
	<!--<header></header>-->
    <!--<spring:eval var="env" expression="@environment.getProperty('environment')" />-->
    <!--<spring:eval var="buildNumber" expression="@environment.getProperty('nextgen.version')" />-->
    <div class="layoutContainer BlueContainer logoutDisplay">
        <div class="panoramaLogoWrapper">
            <div class="panoramaLogo">&nbsp;</div>
        </div> 
   		<aside class="signinContainerWrap">
        	<div class="signinContainer">
            	<div class="jq-mainContent"> 
                	<div id="jq-logout" data-ng-key="jq-logout">
                	    <div>
                	        <c:choose>
                                <c:when test="${not empty message}">
                                    <h2 class="header-statement heading-two">
                                        <span class="color-white">We seem to be having some technical difficulties.</span><br><br>
                                        <span class="color-white">Please sign in again or try again later.</span>

                                    </h2>
                                </c:when>
                                <c:otherwise>                                
                                    <h2 class="header-statement heading-two">
                                        <span class="color-white">You are no <br>longer signed in.</span>
                                    </h2>
                                </c:otherwise>
                            </c:choose>
    						<div data-view-component="button" data-directive-processed="true"
							class="view-button logoutSigninButton">
								<a href="../../public/page/logon?TAM_OP=login" title="Sign in" class="btn-action-primary">
									<span class="button-inner">	
										<span class="label-content">Sign in</span>
									</span>
								</a>
							</div>
                		</div>
                    	<!--  <footer class="footerDisclaimerWrap">
         	  				<c:if test="${env ne PROD and env ne UAT}">[build: ${buildNumber}]</c:if>       
		     				<div class="disclaimer jq-disclaimer footerDisclaimer">
                  			<div class="emphasis">Disclaimer</div>
		        			<div><cms:content name="loginDisclaimer"/></div>
		    				</div>	    	        
        				</footer> -->
                	</div>
            	</div>
       		</div><!-- signinContainer -->        		 
        </aside> <!-- signinContainerWrap -->       
    </div><!-- layoutContainer -->
    <!--<div class="bottomFooter">
    	<span>Powered by BT Panorama</span>
 	</div> -->
</body>
</body>
<script language="javascript" type="text/javascript">
    org.bt.cryptoUrl = '${obfuscationUrl}';
    $('.panoramaLogo').css('right', '25px');
</script>
<script language="javascript" type="text/javascript" src="../static/vendors/adobe/omniture/analytics_btpanorama.js"></script>
<script language="javascript" type="text/javascript">
    var pageDetails;
    pageDetails = {
        "pageName": "logout",
        "pageType": "logout",
        "formName": "sign in",
        "pageStep": ""
    };

    if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1 && window.location.href.indexOf('#jq-passwordReset1') <= -1 && window.location.href.indexOf('#jq-register') <= -1) {
        window.wa && wa('page', pageDetails);
    }
</script>

 