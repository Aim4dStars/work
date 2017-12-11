<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
    xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
    xmlns:wst="http://docs.oasis-open.org/ws-sx/ws-trust/200512"
    xmlns:wsp="http://schemas.xmlsoap.org/ws/2004/09/policy" version="1.0">

    <xsl:template match="/">

        <xsl:variable name="destination" select="//*[local-name()='SubjectConfirmationData']/@Recipient"/>
        
        <samlp:Response
            xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
            xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
            xmlns:wsa="http://www.w3.org/2005/08/addressing"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion"
            xmlns:xs="http://www.w3.org/2001/XMLSchema"
            Destination="{$destination}" ID="FIMRSP_4b39cc9-0140-1a06-be4c-d6f54ea09f32" Version="2.0">
            
            <xsl:attribute name="IssueInstant">
                <xsl:value-of select="//*[local-name()='AuthnStatement']/@AuthnInstant"/>
            </xsl:attribute>
            <saml:Issuer Format="urn:oasis:names:tc:SAML:2.0:nameid-format:entity"><xsl:value-of select="//*[local-name()='Issuer']"/></saml:Issuer>
            <samlp:Status>
                <samlp:StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>
            </samlp:Status>

            <xsl:copy-of select="//*[local-name()='Assertion']"/>

        </samlp:Response>

    </xsl:template>
</xsl:stylesheet>
