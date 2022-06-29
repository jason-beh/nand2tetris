package main.java.com.jasonbeh;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public enum Kind {
        KIND_STATIC,
        KIND_FIELD,
        KIND_ARG,
        KIND_VAR
    }

    private Map<Kind, Integer> kindCounterMapping;
    private Map<Kind, String> kindToStringMapping;
    private Map<String, Kind> stringToKindMapping;

    private final Map<String, Symbol> classSymbolTable;
    private final Map<String, Symbol> subroutineSymbolTable;

    public SymbolTable() {
        initializeCounters();
        initializeKindToStringMapping();
        initializeStringToKindMapping();

        classSymbolTable = new HashMap<>();

        subroutineSymbolTable = new HashMap<>();
        initializeSubroutineScope();
    }

    private void initializeCounters() {
        kindCounterMapping = new HashMap<>();
        kindCounterMapping.put(Kind.KIND_STATIC, 0);
        kindCounterMapping.put(Kind.KIND_FIELD, 0);
    }

    private void initializeStringToKindMapping() {
        stringToKindMapping = new HashMap<>();
        stringToKindMapping.put("static", Kind.KIND_STATIC);
        stringToKindMapping.put("field", Kind.KIND_FIELD);
        stringToKindMapping.put("argument", Kind.KIND_ARG);
        stringToKindMapping.put("var", Kind.KIND_VAR);
    }

    private void initializeKindToStringMapping() {
        kindToStringMapping = new HashMap<>();
        kindToStringMapping.put(Kind.KIND_STATIC, "static");
        kindToStringMapping.put(Kind.KIND_FIELD, "field");
        kindToStringMapping.put(Kind.KIND_ARG, "argument");
        kindToStringMapping.put(Kind.KIND_VAR, "var");
    }

    public void initializeSubroutineScope() {
        subroutineSymbolTable.clear();
        kindCounterMapping.put(Kind.KIND_ARG, 0);
        kindCounterMapping.put(Kind.KIND_VAR, 0);
    }

    public void adjustMethodSymbolTable() {
        kindCounterMapping.put(Kind.KIND_ARG, kindCounterMapping.get(Kind.KIND_ARG) + 1);
    }

    public int varCount(Kind kind) {
        if(kind == Kind.KIND_ARG || kind == Kind.KIND_FIELD || kind == Kind.KIND_STATIC || kind == Kind.KIND_VAR) {
            return kindCounterMapping.get(kind);
        }

        return -1;
    }

    private Symbol getSymbol(String name) {
        // Check for subroutine level first
        if(subroutineSymbolTable.containsKey(name)) {
            return subroutineSymbolTable.get(name);
        }

        // If it doesn't exist in subroutine level, check class
        if(classSymbolTable.containsKey(name)) {
            return classSymbolTable.get(name);
        }

        return null;
    }

    public String typeOf(String name) {
        Symbol symbol = getSymbol(name);
        if(symbol == null) {
            return null;
        }

        return symbol.getType();
    }

    public String kindOf(String name) {
        Symbol symbol = getSymbol(name);
        if(symbol == null) {
            return null;
        }

        return kindToStringMapping.get(symbol.getKind());
    }

    public int indexOf(String name) {
        Symbol symbol = getSymbol(name);
        if(symbol == null) {
            return -1;
        }

        return symbol.getIndex();
    }
    
    public void define(String name, String type, Kind kind) {
        // Create new symbol
        int counter = kindCounterMapping.get(kind);
        Symbol newSymbol = new Symbol(kind, type, counter);

        // Put symbol into symbolTable
        if(kind == Kind.KIND_FIELD || kind == Kind.KIND_STATIC) {
            classSymbolTable.put(name, newSymbol);
        } else if(kind == Kind.KIND_ARG || kind == Kind.KIND_VAR) {
            subroutineSymbolTable.put(name, newSymbol);
        }
        
        // Increment counter
        kindCounterMapping.put(kind, counter + 1);
    }

    public Kind stringToKind(String kindString) {
        return stringToKindMapping.get(kindString);
    }
}
