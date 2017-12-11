package com.bt.nextgen.service.group.customer.groupesb.address.v10;

public enum RegistrationNumberV10Type {

    ACN("ACN"), ABN("ABN"), ARBN("ARBN"), ARSN("ARSN"), FOREIGN("FOREIGN"), DRIVING_LICENCE("DRIVING_LICENCE"), UNKN("UNKN"), ASIC_ORG_ID(
            "ASIC_ORG_ID"), LICENCE("Licence"), CERTIFICATE("Certificate"), MEMBERSHIP("Membership"), TFN("TFN"), TIN("TIN"), PPSR(
            "PPSR"), CRN("CRN");

    private final String value;

    RegistrationNumberV10Type(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RegistrationNumberV10Type fromValue(String v) {
        for (RegistrationNumberV10Type c : RegistrationNumberV10Type.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
