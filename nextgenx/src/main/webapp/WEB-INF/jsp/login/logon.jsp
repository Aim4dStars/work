<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>

<body class="bodyContainer flexContainer bg-blurred opacity-zero" data-liveperson="true">
	<!--<header></header>-->
    <spring:eval var="env" expression="@environment.getProperty('environment')" />
    <spring:eval var="buildNumber" expression="@environment.getProperty('nextgen.version')" />
    <spring:eval var="featureSimpleRegistration" expression="@environment.getProperty('feature.simpleRegistration')" />
    <div class="layoutContainer BlueContainer">
        <div class="noDisplay" id="TAM_OP">${tamOperation}</div>
				<c:if test="${not empty message}">
        <div class="globalErrorMessage">
    		<div data-view-component="messagealert" data-directive-processed="true" class="view-messagealert_2 noticeBox ${messageType == 'INFO' ? 'successBox' : 'warningBox'}" data-ng-key="messageBox">
    		<div class="response-message alert  message-alert-dynamic" role="alert">
    			<span class="icon-container-outer">        
            		<span class="icon-container default">
                		<span class="icon icon-notification-fail"></span>
            		</span>        
    			</span>
    			<span class="message">        
            		<p>${message}</p>  
    			</span>
			</div>
			</div>
        </div>			   
   		</c:if>
		<div class="outageNotificationWrapper noDisplay">
			<div class="outageHeading">
				<span class="icon icon-notification-fail"></span>
				<span class="headingText jq-outageMessageTitle"></span>
			</div>
			<div class="outageMessage jq-outageMessageText"><p></div>
		</div>
		<div class="registerOutageNotificationWrapper noDisplay">
			<div class="outageHeading">
				<span class="icon icon-notification-fail"></span>
				<span class="headingText jq-registerOutageMessageTitle"></span>
			</div>
			<div class="outageMessage jq-registerOutageMessageText"><p></div>
		</div>
        <div class="eamDownMessageWrapper">
   		<div data-view-component="messagealert" data-directive-processed="true" class="eamDownMessage  noDisplay view-messagealert_2 noticeBox ${messageType == 'INFO' ? 'successBox' : 'warningBox'}" data-ng-key="messageBox">
    		<div class="response-message alert  message-alert-dynamic" role="alert">
    			<span class="icon-container-outer">
            		<span class="icon-container default">
                		<span class="icon icon-notification-fail"></span>
            		</span>
    			</span>
    			<span class="message">
            		<p><cms:content name="err00077"/></p>
    			</span>
			</div>
		</div>
        </div>
		<div class="noDisplay jq-FormErrorMessage"></div>
		<div class="panoramaLogoWrapper">
        <div class="panoramaLogo">&nbsp;</div>
    </div>
   		<aside class="signinContainerWrap opacity-zero">
        	<div class="signinContainer">
            	<div class="jq-mainContent">	           
				 	<div class="noDisplay">
 						<ul>
              				<li class="noDisplay">
                             	<em class="iconlink"></em><a href="#jq-logon" title="Sign in" class="" data-ng-key="signin">Sign in</a>
                      		</li>
                        	<li>
                            	<em class="iconlink"></em><a href="#jq-register" title="Register" class="" data-ng-key="register">Register</a>
                        	</li>
							<li>
								<em class="iconlink"></em><a href="#jq-passwordReset1" title="Password Reset" class="" data-ng-key="signin">Password Reset</a>
							</li>						
                    	</ul>
                  	</div>
                	<!-- This hidden field is required by 'org.bt.utils.communicate.ajax' to identify when an ajax response is trying to show the logon page-->
                	<input id="logonPageDisplayed" type="hidden" class="jq-logonPageDisplayed"/>
                	<div id="jq-logon" data-ng-key="jq-logon">
                    	<form name="form" data-ajax="false" action="" method="POST" class="jq-logonForm" accept-charset="">
                        	<fieldset>
                        		<legend>Sign in</legend>
                       			<h3 class="header-statement heading-two">
                       				<span class="color-white">Sign in</span>
                       			</h3>
								<input type="hidden" name="deviceToken" value="" class="jq-deviceToken"/>
                        		<input type="hidden" name="${halgmFieldName}" value="" class="jq-halgmField"/>
                        		<input type="hidden" name="${brandFieldName}" value="${logonBrand}" class="jq-brandField"/>
                        		<input type="hidden" name="${passwordFieldName}" value="" class="jq-logonMaskPassword"/>
                        		<ul>
                            		<li class="margin-bottom-1"> 
                                 		<div data-view-component="inputtext" data-component-name="username" data-directive-processed="true" class="jq-appendErrorAfter">
                                 			<div class="validation-container">
                                  				<!--<span class="label">Username</span>-->
    											<input class="text-input jq-logonUsername" name="${usernameFieldName}" id="login_username" value="" type="text" 
    												data-validation="validate[required]"
    								 				data-validation-required-error="<cms:content name="Err.IP-0297"/>" data-ng-key="login_username" 
    												autocomplete="off" placeholder="Username" maxlength="250" data-stickit-id="stickit_23" data-view-value="" data-event="blur"/>
											</div>
										</div>
                            		</li>
                            		<li class="margin-bottom-1">                              
                                 		<div data-view-component="inputtext" data-component-name="password" data-directive-processed="true" class="jq-appendErrorAfter">
                                 			<div class="validation-container">
                                  				<!--<span class="label">Password<span class="deemphasis"> - case sensitive</span></span>-->                            		                            
    											<input class="text-input jq-logonPassword" name="login_entered_password" id="login_entered_password" value="" type="password" 
    												data-validation="validate[required,minLength]" data-validation-required-error="<cms:content name="Err.IP-0298"/>" data-ng-key="login_entered_password"
													data-min-length="6" data-validation-minlength-error="Password is not long enough" 
    								 				autocomplete="off" placeholder="Password" maxlength="250" data-stickit-id="stickit_23" data-view-value="" data-event="blur"/>
											</div>
										</div>
                            		</li>                            		
                            		<li class="sign-in-button">
                            			<div data-view-component="button" data-component-name="primaryaction" data-directive-processed="true" class="view-primaryaction">
    										<button type="submit" class="btn-action-primary jq-formSubmit" name="logon" data-ng-key="signinButton">
        										<span class="button-inner">
                            						<span class="label-content">Sign in</span>
                            						<span class="icon-wrapper"><span class="icon" aria-hidden="true"></span></span>       
        										</span>
    										</button>
										</div>
                            		</li>
                            		<li>
                            			<ul>
              								<li>
												<div data-view-component="button" data-component-name="terhref" data-directive-processed="true" class="view-terhref">
    												<a href="#jq-passwordReset1" class="btn-action-tertiary" title="Forgotten password" data-ng-key="signin">
        												<span class="button-inner"> 
                											<!--<span class="icon-wrapper">                								
                    							 				<span class="icon icon-cta-link"></span>
                											</span>-->
               							 					<span class="label-content">Forgotten password&#63;</span>
        												</span>
    												</a>
												</div>	
                      						</li>                        								
                    					</ul>                            	
                            		</li>
                        		</ul>
                        	</fieldset>
                        	<div class="forgotusername-contact">
                            	<div class="">Forgotten your username?</div>
                            	<span>Call <cms:content name="CONTACT_NUMBER"/><%--Phone# updated with US1199--%></span>  
                        	</div>
                    	</form>
						<div class="notificationPanel">
							<div class="registerBoxWrapper">
								<div class="registerBoxWrapper-title">
									New to Panorama?
								</div>
								<div class="registerBoxWrapper-text">
									Register to create your username and password.
								</div>
								<a href="#jq-register" class="btn-action-primary">
									<span class="button-inner">
										<span class="label-content">Register</span>
									</span>
								</a>
							</div>
						</div>
                    	<!--  <footer class="footerDisclaimerWrap">   
         	  				<c:if test="${env ne PROD and env ne UAT}">[build: ${buildNumber}]</c:if> 
		     				<div class="disclaimer jq-disclaimer footerDisclaimer">
                  				<%--<div class="emphasis">Disclaimer</div>--%>
		        				<div><cms:content name="loginDisclaimer"/></div>
		    				</div>	    	        
        				</footer>-->
                	</div>               
               
               		<c:import url="../registration/registration.jsp" />
                	<c:import url="../login/passwordReset.jsp" /> 

                	<c:if test="${systemEventMessageFound==true}">
                	<div data-view-component="messagealert" data-directive-processed="true" class="view-messagealert_2 noticeBox ${not empty message ? 'noteBoxWrapMod1' : ''} noteBoxWrapMod2" role="alert">
    					<div class="response-message alert  message-alert-dynamic" role="alert">
    						<span class="icon-container-outer">        
           						<span class="icon-container default">
                					<span class="icon icon-notification-fail" aria-hidden="true"></span>
            					</span>        
    						</span>
    						<span class="message">        
            					<p><cms:content name="systemEventMessage" /></p>  
    						</span>
						</div>
					</div>       
              		</c:if>	
            	</div>
       		</div><!-- signinContainer -->     		
        </aside> <!-- signinContainerWrap -->             
    </div><!-- layoutContainer -->     
    <!--<div class="jq-logonBusyDialog logonBusyDialog">
        <h3 class="heading-two">Signing you in... <em class="iconLoader"></em></h3>
    </div>-->
    <%--<div class="jq-registerBusyDialog logonBusyDialog">--%>
        <%--<em class="ajaxLine logonBusyDialogAjax"></em>--%>
    <%--</div>--%>
    <%-- <input type="hidden" class="jq-blocked" value="${param.acc}" /> --%>
   	<!--<div class="bottomFooter">
    	<span>Powered by BT Panorama</span>
 	</div> -->
</body>

<script language="javascript" type="text/javascript">
    org.bt.cryptoUrl = '${obfuscationUrl}';
</script>

<spring:eval var="simpleRegistration" expression="@environment.getProperty('feature.simpleRegistration')"/>

<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/pages/logon.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/le-mtagconfig.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/livepersonvendor.js'/>"></script>
<script language="javascript" type="text/javascript" src="../static/vendors/adobe/omniture/analytics_btpanorama.js"></script>
<script language="javascript" type="text/javascript">
    var pageDetails;
    pageDetails = {
        "pageName": "enter username",
        "pageType": "login",
        "formName": "sign in",
        "pageStep": "start"
    };
    if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1 && window.location.href.indexOf('#jq-passwordReset1') <= -1 && window.location.href.indexOf('#jq-register') <= -1) {
        window.wa && wa('page', pageDetails);
    }
</script>
<c:if test="${not empty logoutUrl}"><img src="${logoutUrl}" /></c:if>