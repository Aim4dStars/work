<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>

<div class="footerCMSContentPanel" data-ng-key="cmsContentPanel">
	<h1>Contact us</h1>

    <h2 class="mod1">Panorama Support</h2>
	
	<dl class="large">
	    <dt class="emphasis">Phone</dt>
	    <dd data-ng-key="phone"><cms:content name="adviserrelationphone" /></dd>
	    
	    <dt class="emphasis">Phone from overseas</dt>
	    <dd data-ng-key="phoneoverseas"><cms:content name="adviserrelationphoneoverseas" /></dd>
	    
	    <dt class="emphasis">Email</dt>
	    <dd data-ng-key="email"><a class="baseLink" title="<cms:content name="email" />" href="mailto:<cms:content name="email" />"><cms:content name="email" /></a></dd>
	 
	</dl>
	<div class="clear"></div>
</div>