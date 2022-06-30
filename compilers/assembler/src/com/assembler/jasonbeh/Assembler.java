package com.assembler.jasonbeh;

import java.io.*;

public class Assembler {
    private final BufferedWriter hackCode;
    private final Code encoder;
    private final SymbolTable symbolTable;
    private Parser parser;
    private final String fileSource;

    public static void main(String[] args) {
        // No source file
        if (args.length == 0) {
            System.err.println("Usage: java Assembler [absolute-path-to-source-file]");
            System.exit(1);
        }

        // Invalid source file
        File sourceFile = new File(args[0].trim());
        if (!sourceFile.exists()) {
            System.err.println("The specified source file doesn't exist");
            System.exit(2);
        }

        // Get output file.
        String sourceAbsolutePath = sourceFile.getAbsolutePath();
        String fileName = sourceFile.getName();
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String sourceDirectory = sourceAbsolutePath.substring(0, sourceFile.getAbsolutePath().indexOf(sourceFile.getName()));

        // Get absolute path and add hack file
        String outputFileAbsolutePath = sourceDirectory + fileNameWithoutExtension + ".hack";
        File outputFile = new File(outputFileAbsolutePath);

        try {
            StringWriter message = new StringWriter();

            // Delete old file if exist
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    message.append("Deleted previous file at ").append(outputFileAbsolutePath);
                }
            }

            // Create new file
            if (outputFile.createNewFile()) {
                message.append("\nCreated new file at ").append(outputFileAbsolutePath);
            }

            long startTime = System.currentTimeMillis();

            // Translate source file.
            Assembler assembler = new Assembler(sourceAbsolutePath, outputFileAbsolutePath);
            assembler.firstPass();
            assembler.secondPass();
            assembler.close();

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            message.append("\nTranslated ASM to Hack file in ").append(Long.toString(elapsedTime)).append("ms.");

            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    public Assembler(String source, String target) throws IOException {
        symbolTable = new SymbolTable();
        encoder = new Code();
        hackCode = new BufferedWriter(new FileWriter(target));
        fileSource = source;
    }

    // Only process the labels in the first pass
    private void firstPass() {
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
    private void secondPass() throws IOException {
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
