package com.bt.nextgen.service.json;

import com.btfin.panorama.core.security.encryption.EncodedString;

public class AccountJsonStreamStrategy extends DefaultStreamStrategy {

    private boolean isEncodingRequired;

    public boolean isEncodingRequired() {
		return isEncodingRequired;
	}

	public void setEncodingRequired(boolean isEncodingRequired) {
		this.isEncodingRequired = isEncodingRequired;
	}

	private static final String ACCOUNT_ID_FIELD_NAME = "account_id";

    @Override
    public String processName(String name) {
        isEncodingRequired = ACCOUNT_ID_FIELD_NAME.equalsIgnoreCase(name);
        return super.processName(name);
    }

    @Override
    public String processNumber(String number) {
        if (isEncodingRequired) {
            return EncodedString.fromPlainText(number).toString();
        }
        return number;
    }
}
