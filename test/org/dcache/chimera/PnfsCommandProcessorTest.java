package org.dcache.chimera;

import org.junit.Test;
import static org.junit.Assert.*;

import static org.dcache.chimera.PnfsCommandProcessor.process;

public class PnfsCommandProcessorTest {


    @Test
    public void shouldReturnParcsedElements() {

        String[] cmd = process(".(arg1)(arg2)");
        assertEquals("Invalid number of arguments", 2, cmd.length);
    }

    @Test
    public void shouldReturnSameElementOnMissingBrases() {
        String[] cmd = process("arg1");
        assertEquals("Invalid number of arguments", 1, cmd.length);
    }


    @Test
    public void shouldIgnoreNastedBrases() {

        String[] cmd = process(".(arg1)(arg2(opt1))");
        assertEquals("Invalid number of arguments", 2, cmd.length);
    }

}
