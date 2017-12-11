package com.bt.nextgen.api.draftaccount;

public class FormDataConstantsForTests {

    public static String FORM_DATA_FOR_SUBMIT = "{\n" +
            "    \"accountType\": \"individual\",\n" +
            "    \"individualinvestordetails\": {\n" +
            "        \"title\": \"mr\",\n" +
            "        \"firstname\": \"John$##$\",\n" +
            "        \"middlename\": \"Doe\",\n" +
            "        \"lastname\": \"Smith\",\n" +
            "        \"preferredname\": \"Joe\",\n" +
            "        \"dateofbirth\": \"01/01/1980\",\n" +
            "        \"gender\": \"male\",\n" +
            "        \"taxcountry\": \"Ita\",\n" +
            "        \"taxoption\": \"Tax File Number provided\",\n" +
            "        \"tfn\": \"123456782\",\n" +
            "        \"resaddress\": {\n" +
            "            \"addressLine1\": \"60 Martin Pl\",\n" +
            "            \"addressLine2\": \"\",\n" +
            "            \"country\": \"Aus\",\n" +
            "            \"suburb\": \"SYDNEY\",\n" +
            "            \"postcode\": \"2000\",\n" +
            "            \"state\": \"NSW\",\n" +
            "            \"verified\": true,\n" +
            "            \"addressType\": \"residential\"\n" +
            "        },\n" +
            "        \"postaladdress\": {\n" +
            "            \"addressLine1\": \"60 Martin Pl\",\n" +
            "            \"addressLine2\": \"\",\n" +
            "            \"country\": \"Aus\",\n" +
            "            \"suburb\": \"SYDNEY\",\n" +
            "            \"postcode\": \"2000\",\n" +
            "            \"state\": \"NSW\",\n" +
            "            \"verified\": true,\n" +
            "            \"addressType\": \"residential\"\n" +
            "        },\n" +
            "        \"mobile\": {\n" +
            "            \"value\": \"0412345678\"\n" +
            "        },\n" +
            "        \"email\": {\n" +
            "            \"value\": \"john.doe@smith.com\"\n" +
            "        },\n" +
            "        \"nonphotodocuments\": {\n" +
            "            \"pensioncard\": {\n" +
            "                \"documentIssuer\": \"Centrelink\",\n" +
            "                \"issueDate\": \"07/08/2012\",\n" +
            "                \"expiryDate\": \"07/08/2015\",\n" +
            "                \"documentNumber\": \"121212121\",\n" +
            "                \"verificationSource\": \"original\",\n" +
            "                \"englishTranslation\": \"Not Applicable\"\n" +
            "            },\n" +
            "            \"utilitiesnotice\": {\n" +
            "                \"documentIssuer\": \"SOMEWHERE\",\n" +
            "                \"issueDate\": \"07/08/2012\",\n" +
            "                \"documentNumber\": \"101010101\",\n" +
            "                \"verificationSource\": \"certifiedcopy\",\n" +
            "                \"englishTranslation\": \"Not Applicable\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"preferredcontact\": \"secondaryEmail\"\n" +
            "    },\n" +
            "    \"accountsettings\": {\n" +
            "        \"investorAccountSettings\": [\n" +
            "           {\n" +
            "                \"paymentSetting\":\"allpayments\"\n" +
            "            }\n" +
            "       ]," +
            "        \"professionalspayment\": \"allpayments\"\n" +
            "    },\n" +
            "    \"linkedaccounts\": {\n" +
            "        \"primaryLinkedAccount\": {\n" +
            "            \"accountname\": \"JD SMITH WESTPAC 1\",\n" +
            "            \"bsb\": \"123456\",\n" +
            "            \"accountnumber\": \"987654321\",\n" +
            "            \"nickname\": \"JDS WESTPAC 1@#\",\n" +
            "            \"directdebitamount\": \"500.00\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"fees\": {\n" +
            "        \"estamount\": \"0.00\"\n" +
            "    }" +
            "}";
}
