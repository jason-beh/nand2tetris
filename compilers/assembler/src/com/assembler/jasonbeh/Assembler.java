package com.assembler.jasonbeh;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
    private final BufferedWriter hackCode;
    private final Code encoder;
    private final SymbolTable symbolTable;
    private Parser parser;
    private final String fileSource;

    public Assembler(String source, String target) throws IOException {
        symbolTable = new SymbolTable();
        encoder = new Code();
        hackCode = new BufferedWriter(new FileWriter(target));
        fileSource = source;
    }

    // Only process the labels in the first pass
    public void firstPass() {
        parser = new Parser(fileSource);
        while (parser.hasMoreLines()) {
            parser.advance();

            Parser.Instruction instructionType = parser.instructionType();

            if (instructionType.equals(Parser.Instruction.L_INSTRUCTION)) {
                symbolTable.addLabelEntry(parser.symbol());
            } else {
                symbolTable.incrementCurrentLineNumber();
            }

        }
        parser.close();
    }

    // Process variables and C instructions
    public void secondPass() throws IOException {
        parser = new Parser(fileSource);
        while (parser.hasMoreLines()) {
            parser.advance();

            Parser.Instruction instructionType = parser.instructionType();

            if (instructionType.equals(Parser.Instruction.C_INSTRUCTION)) {
                String comp = parser.comp();
                String dest = parser.dest();
                String jump = parser.jump();

                String code = "111" + encoder.comp(comp) + encoder.dest(dest) + encoder.jump(jump);
                hackCode.append(code);
                hackCode.newLine();
            } else if (instructionType.equals(Parser.Instruction.A_INSTRUCTION)) {
                String symbol = parser.symbol();
                boolean isSymbol = !Character.isDigit(symbol.charAt(0));

                String symbolAddress = symbol;

                // Add to symbol table if it's a symbol that doesn't exist
                if (isSymbol) {
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addVariableEntry(symbol);
                    }

                    symbolAddress = Integer.toString(symbolTable.getAddress(symbol));
                }

                // Convert decimal to binary
                int value = Integer.parseInt(symbolAddress);
                String binaryNumber = Integer.toBinaryString(value);
                String code = "0" + String.format("%15s", binaryNumber).replace(' ', '0');
                hackCode.append(code);
                hackCode.newLine();
            }
        }
        parser.close();
    }

    public void close() throws IOException {
        hackCode.close();
    }
}
