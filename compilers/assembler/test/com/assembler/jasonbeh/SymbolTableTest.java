package com.assembler.jasonbeh;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableTest {

    private SymbolTable symbolTable;

    @Before
    public void init() {
        symbolTable = new SymbolTable();
    }

    @Test
    public void testIncrementLineNumber() {
        symbolTable.incrementCurrentLineNumber();
        assertEquals(1, symbolTable.getCurrentLineNumber());
    }

    @Test
    public void containsSPWithoutAdding() {
        assertTrue(symbolTable.contains("SP"));
    }

    @Test
    public void containsSPAfterAdding() {
        symbolTable.addVariableEntry("SP");
        assertTrue(symbolTable.contains("SP"));
    }

    @Test
    public void obtainSPAddress() {
        assertEquals(0, symbolTable.getAddress("SP"));
    }

    @Test
    public void obtainSPAddressAfterChanging() {
        symbolTable.addVariableEntry("SP");
        assertEquals(16, symbolTable.getAddress("SP"));
    }

    @Test
    public void obtainLabelAddressAfterAdding() {
        symbolTable.incrementCurrentLineNumber();
        symbolTable.incrementCurrentLineNumber();
        symbolTable.addLabelEntry("LOOP");
        assertEquals(2, symbolTable.getAddress("LOOP"));
    }
}