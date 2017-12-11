package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IContactValue {

    public boolean isPreferredContact();

    public boolean isNull();

    /**
     * Sould always have a 'value' key. Otherwise throws IllegalStateException when method is called
     *
     * @return
     */
    public String getValue();

    /**
     * It might not have a country code so return null
     *
     * @return
     */
    public String getCountryCode();

    /**
     * It might not have area code so return null
     * @return
     */
    public String getAreaCode();

    public enum ContactType {

        EMAIL("email"), SECONDARY_EMAIL("secondaryemail"),
        MOBILE("mobile"), SECONDARY_MOBILE("secondarymobile"),
        HOME_NUMBER("homenumber"), WORK_NUMBER("worknumber"), OTHER_NUMBER("othernumber");

        private final String jsonValue;

        ContactType(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static ContactType fromJson(String jsonValue) {
            for (ContactType type : values()) {
                if (type.jsonValue.equalsIgnoreCase(jsonValue)) {
                    return type;
                }
            }
            return null;
        }

        public String getJsonValue() {
            return jsonValue;
        }

    }

}
