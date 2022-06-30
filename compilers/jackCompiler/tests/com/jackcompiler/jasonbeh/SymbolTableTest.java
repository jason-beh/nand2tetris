package com.jackcompiler.jasonbeh;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class
SymbolTableTest {
    SymbolTable symbolTable;

    @Before
    public void init() {
        symbolTable = new SymbolTable();
    }

    @Test
    public void symbolTableIndexOf() throws IOException {
        symbolTable.define("argExample0", "int", SymbolTable.Kind.KIND_ARG);
        symbolTable.define("argExample1", "string", SymbolTable.Kind.KIND_ARG);
        symbolTable.define("argExample2", "boolean", SymbolTable.Kind.KIND_ARG);
        symbolTable.define("varExample0", "int", SymbolTable.Kind.KIND_VAR);
        assertEquals(0, symbolTable.indexOf("argExample0"));
        assertEquals(1, symbolTable.indexOf("argExample1"));
        assertEquals(2, symbolTable.indexOf("argExample2"));
        assertEquals(0, symbolTable.indexOf("varExample0"));
    }

    @Test
    public void symbolTableTypeOf() throws IOException {
        symbolTable.define("staticExample0", "int", SymbolTable.Kind.KIND_STATIC);
        symbolTable.define("varExample0", "string", SymbolTable.Kind.KIND_VAR);
        assertEquals("int", symbolTable.typeOf("staticExample0"));
        assertEquals("string", symbolTable.typeOf("varExample0"));
    }

    @Test
    public void symbolTableKindOf() throws IOException {
        symbolTable.define("fieldExample0", "int", SymbolTable.Kind.KIND_FIELD);
        symbolTable.define("argExample0", "string", SymbolTable.Kind.KIND_ARG);
        symbolTable.define("argExample1", "string", SymbolTable.Kind.KIND_ARG);
        assertEquals("field", symbolTable.kindOf("fieldExample0"));
        assertEquals("argument", symbolTable.kindOf("argExample0"));
        assertEquals("argument", symbolTable.kindOf("argExample1"));
    }

    @Test
    public void symbolTableAdjustMethodSymbolTable() throws IOException {
        symbolTable.adjustMethodSymbolTable();
        symbolTable.define("exampleVariable", "int", SymbolTable.Kind.KIND_ARG);
        assertEquals(1, symbolTable.indexOf("exampleVariable"));
    }

    @Test
    public void symbolTableVarCount() throws IOException {
        symbolTable.define("fieldExample0", "int", SymbolTable.Kind.KIND_FIELD);
        symbolTable.define("fieldExample1", "int", SymbolTable.Kind.KIND_FIELD);
        symbolTable.define("staticExample0", "int", SymbolTable.Kind.KIND_STATIC);
        symbolTable.define("varExample0", "int", SymbolTable.Kind.KIND_VAR);
        symbolTable.define("varExample1", "int", SymbolTable.Kind.KIND_VAR);
        symbolTable.define("varExample2", "int", SymbolTable.Kind.KIND_VAR);
        assertEquals(2, symbolTable.varCount(SymbolTable.Kind.KIND_FIELD));
        assertEquals(1, symbolTable.varCount(SymbolTable.Kind.KIND_STATIC));
        assertEquals(3, symbolTable.varCount(SymbolTable.Kind.KIND_VAR));
        assertEquals(0, symbolTable.varCount(SymbolTable.Kind.KIND_ARG));
    }
}
