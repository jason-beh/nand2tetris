package main.java.com.jasonbeh;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CodeTest {

    private Code code;

    @Before
    public void init() {
        code = new Code();
    }

    @Test
    public void getDestThatExist() {
        assertEquals("001", code.dest("M"));
    }

    @Test
    public void getDestThatDoesNotExist() {
        assertEquals("000", code.dest("I_DON'T_EXIST"));
    }

    @Test
    public void getCompThatExist() {
        assertEquals("0110000", code.comp("A"));
    }

    @Test
    public void getCompThatDoesNotExist() {
        assertNull(code.comp("I_DON'T_EXIST"));
    }

    @Test
    public void getJumpThatExist() {
        assertEquals("100", code.jump("JLT"));
    }

    @Test
    public void getJumpThatDoesNotExist() {
        assertEquals("000", code.jump("I_DON'T_EXIST"));
    }
}