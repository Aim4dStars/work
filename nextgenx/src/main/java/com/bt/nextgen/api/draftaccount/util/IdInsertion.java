package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.api.draftaccount.FormDataConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IdInsertion {
    public static final String[] FIELDS = new String[]{
            "resaddress",
            "postaladdress",
            "mobile",
            "email",
            "secondaryEmail",
            "secondaryMobile",
            "homeNumber",
            "workNumber"
    };

    static class MonotonicCorrelationInserter {
        int correlationId = 1;

       private void populateIdsFor(Map<String, Object> formData, String... personAttributes) {
           final String personAttribute = personAttributes[0];
           if (formData.get(personAttribute) != null) {
                if (personAttributes.length == 1) {
                    for (Map<String, Object> investor : (List<Map<String, Object>>) formData.get(personAttribute)) {
                        investor.put(FormDataConstants.FIELD_CORRELATION_ID, correlationId++);

                        for (String field : FIELDS) {
                            insertId(investor, field);
                        }
                    }
                } else {
                    populateIdsFor((Map<String, Object>) formData.get(personAttribute), tail(personAttributes));
                }
            }
        }

        private void insertId(Map<String, Object> mappedValues, String... keys) {
            Map<String, Object> stringObjectMap = (Map<String, Object>) mappedValues.get(keys[0]);
            if (stringObjectMap != null) {
                if (keys.length == 1) {
                    stringObjectMap.put(FormDataConstants.FIELD_CORRELATION_ID, correlationId++);
                } else {
                    insertId(stringObjectMap, tail(keys));
                }
            }
        }

        private String[] tail(String[] strings) {
            return Arrays.copyOfRange(strings, 1, strings.length);
        }
    }

    /**
     * To be removed when we remove the Map<> forms.
     *
     * @param formData
     */
    @Deprecated
    public static void mergeIds(Map<String, Object> formData) {
        MonotonicCorrelationInserter inserter = new MonotonicCorrelationInserter();


        inserter.populateIdsFor(formData, "investors");
        inserter.populateIdsFor(formData, "directors");
        inserter.populateIdsFor(formData, "trustees");
        inserter.populateIdsFor(formData, "shareholderandmembers", "additionalShareHoldersAndMembers");

        inserter.insertId(formData, "companytrustee");
        inserter.insertId(formData, "companytrustee","companyoffice");
        inserter.insertId(formData, "companytrustee","placeofbusiness");
        inserter.insertId(formData, "companydetails");
        inserter.insertId(formData, "companydetails","companyoffice");
        inserter.insertId(formData, "companydetails","placeofbusiness");
        inserter.insertId(formData, "smsfdetails");
        inserter.insertId(formData, "smsfdetails", "smsfaddress");
        inserter.insertId(formData, "trustdetails");
        inserter.insertId(formData, "trustdetails", "address");
    }

}
