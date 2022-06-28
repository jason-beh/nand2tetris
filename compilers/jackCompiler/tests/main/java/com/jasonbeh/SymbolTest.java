package main.java.com.jasonbeh;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SymbolTest {
    @Test
    public void symbolGetIndex() throws IOException {
        Symbol symbol = new Symbol(SymbolTable.Kind.KIND_VAR, "int", 3);
        assertEquals(3, symbol.getIndex());
    }

    @Test
    public void symbolGetType() throws IOException {
        Symbol symbol = new Symbol(SymbolTable.Kind.KIND_ARG, "string", 5);
        assertEquals("string", symbol.getType());
    }

    @Test
    public void symbolGetKind() throws IOException {
        Symbol symbol = new Symbol(SymbolTable.Kind.KIND_STATIC, "boolean", 3);
        assertEquals(SymbolTable.Kind.KIND_STATIC, symbol.getKind());
    }
}
