package com.bt.nextgen.service.json;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
@RunWith(MockitoJUnitRunner.class)
public class JsonStreamProcessorTest {

    private JsonStreamProcessor jsonStreamProcessor;

    @Before
    public void setup() {
        jsonStreamProcessor = new JsonStreamProcessor(new AccountJsonStreamStrategy());
    }

    @Test
    public void processJson() throws Exception {

        String jsonInputClient = " {\"position_id\": 101861, \"client_id\": 1188818, \"account_id\": 1188821, \"account_bsb\": \"262786\", \"account_name\": \"bp--1_4431\", \"account_number\": \"120132030\", \"account_open_date\": null, \"account_status\": \"\", \"account_structure_type\": \"SMSF\", \"account_type\": \"Customer\", \"adviser_id\": 29561, \"adviser_name\": \"N Craig Leibbrandt\", \"client_first_name\": \"person-121_10645\", \"client_full_name\": \"person-121_10645person-121_10645person-121_10645\", \"client_last_name\": \"person-121_10645\", \"client_middle_name\": \"person-121_10645\", \"object_hierarchy\": [{\"oe_id\": 99780, \"oetype_id\": 660307, \"oetype\": \"Issuer\"}, {\"oe_id\": 99797, \"oetype_id\": 660307, \"oetype\": \"Super Dealer Group\"}, {\"oe_id\": 99884, \"oetype_id\": 660307, \"oetype\": \"Dealer Group\"}, {\"oe_id\": 101861, \"oetype_id\": 660307, \"oetype\": \"Adviser Position\"}], \"product_id\": 108285, \"product_name\": \"BT Panorama Investments\", \"sequence_number\": 67416851, \"solr_query\": null}";
        String output = jsonStreamProcessor.processJson(jsonInputClient);
        System.out.println(output);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(output).getAsJsonObject();

        assertEquals("1188821", EncodedString.toPlainText(jsonObject.get("accountId").getAsString()));
        assertNotEquals("Expecting one value tobe replaced", jsonInputClient, output);

        String jsonInputAccount = " {\"position_id\": 101861, \"account_id\": 1188821, \"account_bsb\": \"262786\", \"account_name\": \"bp--1_4431\", \"account_number\": \"120132030\", \"account_open_date\": null, \"account_status\": \"\", \"account_structure_type\": \"SMSF\", \"account_type\": \"Customer\", \"adviser_id\": 29561, \"adviser_name\": \"N Craig Leibbrandt\", \"object_hierarchy\": [{\"oe_id\": 99780, \"oetype_id\": 660307, \"oetype\": \"Issuer\"}, {\"oe_id\": 99797, \"oetype_id\": 660307, \"oetype\": \"Super Dealer Group\"}, {\"oe_id\": 99884, \"oetype_id\": 660307, \"oetype\": \"Dealer Group\"}, {\"oe_id\": 101861, \"oetype_id\": 660307, \"oetype\": \"Adviser Position\"}], \"product_id\": 108285, \"product_name\": \"BT Panorama Investments\", \"sequence_number\": 67416851, \"solr_query\": null}";
        output = jsonStreamProcessor.processJson(jsonInputAccount);
        jsonObject = parser.parse(output).getAsJsonObject();

        assertEquals("1188821", EncodedString.toPlainText(jsonObject.get("accountId").getAsString()));
        assertNotEquals("Expecting account_id tobe replaced", jsonInputAccount, output);
    }
}