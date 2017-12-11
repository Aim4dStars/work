package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IIdvDocument;
import com.bt.nextgen.api.draftaccount.model.form.ITrustIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustIdv;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustVerification;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

import javax.xml.datatype.XMLGregorianCalendar;


class TrustIdentityVerificationForm implements ITrustIdentityVerificationForm{
    private final TrustVerification trustDetails;

    public TrustIdentityVerificationForm(TrustVerification trustDetails) {
        this.trustDetails = trustDetails;
    }

	@Override
    public IIdvDocument getIdvDocument() {
        TrustIdv trustIdv = trustDetails.getLetteridv()!=null? trustDetails.getLetteridv(): trustDetails.getTrustdeed();
		return new IdvDocument(trustIdv, trustDetails.getIdentitydocument().toString());
	}
    
    static public class IdvDocument implements IIdvDocument {
    	private final TrustIdv documentDetails;
        private final String documentType;

    	
    	public IdvDocument(TrustIdv documentDetails, String documentType) {
    		this.documentDetails = documentDetails;
    		this.documentType = documentType;
    	}

		@Override
    	public String getDocumentType() {
            return documentType;
    	}

		@Override
    	public String getName() {
    		return documentDetails.getFullname();
    	}

		@Override
    	public XMLGregorianCalendar getDocumentDate() {
            String documentDate = documentDetails.getDocumentdate();
            return XMLGregorianCalendarUtil.getXMLGregorianCalendar(documentDate, "dd/MM/yyyy");
    	}

		@Override
    	public String getDocumentNumber() {
       		return documentDetails.getDocumentnumber();
       	}

		@Override
    	public String getVerifiedFrom() {
    		return documentDetails.getVerifiedfrom().toString();
}
}
}
