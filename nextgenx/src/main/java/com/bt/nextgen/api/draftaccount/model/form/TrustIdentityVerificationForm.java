package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class TrustIdentityVerificationForm implements ITrustIdentityVerificationForm{
    private final Map<String, Object> trustDetails;

    public TrustIdentityVerificationForm(Map<String, Object> trustDetails) {
        this.trustDetails = trustDetails;
    }

    public IIdvDocument getIdvDocument() {
    	String documentType = (String) trustDetails.get("identitydocument");
		return new IdvDocument((Map<String, Object>) trustDetails.get(documentType), documentType);
	}
    
    static public class IdvDocument implements IIdvDocument {
    	private final Map<String, Object> documentDetails;
    	private final String documentType;
    	
    	public IdvDocument(Map<String, Object> documentDetails, String documentType) {
    		this.documentDetails = documentDetails;
    		this.documentType = documentType;
    	}
    	
    	public String getDocumentType() {
    		return documentType;
    	}
    	
    	public String getName() {
    		return (String)documentDetails.get("fullname");
    	}
    	
    	public XMLGregorianCalendar getDocumentDate() {
            String documentDate = (String) documentDetails.get("documentdate");
            return XMLGregorianCalendarUtil.getXMLGregorianCalendar(documentDate, "dd/MM/yyyy");
    	}

    	public String getDocumentNumber() {
       		return (String)documentDetails.get("documentnumber");
       	}

    	public String getVerifiedFrom() {
    		return (String)documentDetails.get("verifiedfrom");
    	}
    }
}
