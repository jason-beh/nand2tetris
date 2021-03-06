package com.assembler.jasonbeh;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Integer> symbolTable;
    private int variableAddress;
    private int currentLineNumber;

    public SymbolTable() {
        symbolTable = new HashMap<>();
        variableAddress = 16;
        currentLineNumber = 0;

        // Predefined keys
        // Memory segments
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);

        // Registers
        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);

        // Input/Output
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
    }

    public void addVariableEntry(String symbol) {
        symbolTable.put(symbol, variableAddress++);
    }

    public void addLabelEntry(String symbol) {
        symbolTable.put(symbol, currentLineNumber);
    }

    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    public void incrementCurrentLineNumber() {
        currentLineNumber++;
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }
}
