package main.java.com.jasonbeh;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class SymbolTableTest {

    private SymbolTable symbolTable;

    @Before
    public void init() {
        symbolTable = new SymbolTable();
    }

    @Test
    public void containsSPWithoutAdding() {
        assertTrue(symbolTable.contains("SP"));
    }

    @Test
    public void containsSPAfterAdding() {
        symbolTable.addEntry("SP", 1000);
        assertTrue(symbolTable.contains("SP"));
    }

    @Test
    public void obtainSPAddress() {
        assertEquals(0, symbolTable.getAddress("SP"));
    }

    @Test
    public void obtainSPAddressAfterChanging() {
        symbolTable.addEntry("SP", 1000);
        assertEquals(1000, symbolTable.getAddress("SP"));
    }

    @Test
    public void addCustomSymbol() {
        symbolTable.addEntry("SCOOBYDOO", 1321);
        assertEquals(1321, symbolTable.getAddress("SCOOBYDOO"));
    }
}