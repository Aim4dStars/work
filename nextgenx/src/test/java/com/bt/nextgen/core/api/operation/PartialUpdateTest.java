package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.PartialUpdateDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PartialUpdateTest {
    @Test
    public void testUpdate_whenOperationInvokedWithIncompleteKey_thenBadRequestException() {
        final TestDto dto = new TestDto(new TestKey("k1", null), "a", "1");
        PartialUpdateDtoService<TestKey, TestDto> service = new PartialUpdateDtoService<TestKey, TestDto>() {
            @Override
            public TestDto partialUpdate(TestKey key, Map<String, ? extends Object> partialUpdates, ServiceErrors serviceErrors) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        try {
            new PartialUpdate<TestKey, TestDto>("vTest", service, null, null, TestDto.class, null).performOperation();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("", BadRequestException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate_whenOperationInvokedWithCompleteKeyButUnknownUpdateFiled_thenBadRequestException() {
        final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
        PartialUpdateDtoService<TestKey, TestDto> service = new PartialUpdateDtoService<TestKey, TestDto>() {
            @Override
            public TestDto partialUpdate(TestKey key, Map<String, ? extends Object> partialUpdates, ServiceErrors serviceErrors) {
                // TODO Auto-generated method stub
                return null;
            }
        };

        try {
            HashMap<String, String> updates = new HashMap<>();
            updates.put("attr3", "blue");
            new PartialUpdate<TestKey, TestDto>("vTest", service, new TestKey("k1", "k2"), updates, TestDto.class, null)
                    .performOperation();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("", BadRequestException.class, e.getClass());
        }
    }

    @Test
    public void testUpdate_whenOperationInvoked_thenKeyedDtoIsReturned() {
        final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
        PartialUpdateDtoService<TestKey, TestDto> service = new PartialUpdateDtoService<TestKey, TestDto>() {
            @Override
            public TestDto partialUpdate(TestKey key, Map<String, ? extends Object> partialUpdates, ServiceErrors serviceErrors) {
                // TODO Auto-generated method stub
                return dto;
            }
        };

        KeyedApiResponse<TestKey> response = new PartialUpdate<TestKey, TestDto>("vTest", service, new TestKey("k1", "k2"),
                new HashMap<String, String>(), TestDto.class, null).performOperation();
        Dto d = response.getData();
        TestKey key = response.getId();
        Assert.assertEquals("k1", key.getAttr1());
        Assert.assertEquals("k2", key.getAttr2());
        Assert.assertEquals(d, dto);
    }
}
