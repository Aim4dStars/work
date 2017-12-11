package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class DocumentDtoCompressorTest {


    DocumentDtoCompressor compressor = new DocumentDtoCompressor(Collections.EMPTY_LIST);

    @Test
    public void testGetCreateName() {
        String name = compressor.getCreateName("12345", "Account name");
        Assert.assertTrue(StringUtils.contains(name, "Download_Account name_12345_"));
    }
}