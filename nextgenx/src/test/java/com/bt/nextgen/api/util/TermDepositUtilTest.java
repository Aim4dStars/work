package com.bt.nextgen.api.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositUtilTest {
    @Test
    public void testGetTermDepositMaturityInstruction() {
        String instr = null;
        String result = TermDepositUtil.getMaturityInstructionForDisplay(instr, "BT Cash", false);
        Assert.assertTrue(result == null);

        final String defaultInstr = "Deposit all money into BT Cash";
        result = TermDepositUtil.getMaturityInstructionForDisplay(instr, "BT Cash", true);
        Assert.assertEquals(result, defaultInstr);

        instr = "Test instruction";
        result = TermDepositUtil.getMaturityInstructionForDisplay(instr, "BT Cash", true);
        Assert.assertEquals(result, instr);

        // This instruction should be mapped to the default as specified by PO
        // (Def 9656).
        instr = "None (Close TD)";
        result = TermDepositUtil.getMaturityInstructionForDisplay(instr, "BT Cash", true);
        Assert.assertEquals(result, defaultInstr);
    }
}
