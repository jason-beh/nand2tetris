package main.java.com.jasonbeh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    private Scanner reader;
    private String currentInstruction;
    private String nextInstruction;

    public enum Instruction
    {
        A_INSTRUCTION, // A instruction @xxx
        C_INSTRUCTION, // C instruction
        L_INSTRUCTION // Label (LOOP)
    }

    Parser(String filename) {
        try {
            reader = new Scanner(new FileReader(filename));
            advance();
            advance();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Instruction instructionType() {
        if(currentInstruction.startsWith("(") && currentInstruction.endsWith(")")) {
            return Instruction.L_INSTRUCTION;
        } else if(currentInstruction.startsWith("@")) {
            return Instruction.A_INSTRUCTION;
        }

        return Instruction.C_INSTRUCTION;
    }

    public String symbol() {
        Instruction currentType = instructionType();

        if(currentType.equals(Instruction.A_INSTRUCTION)) {
            return currentInstruction.substring(1); // Remove @
        } else if(currentType.equals(Instruction.L_INSTRUCTION)) {
            return currentInstruction.substring(1, currentInstruction.length() - 1); // Remove brackets
        }

        return null;
    }

    public String dest() {
        int delimiter = currentInstruction.indexOf("=");

        if(delimiter == -1) {
            return null;
        } else {
            return currentInstruction.substring(0, delimiter);
        }
    }

    public String jump() {
        int delimiter = currentInstruction.indexOf(";");

        if(delimiter == -1) {
            return null;
        } else {
            return currentInstruction.substring(delimiter + 1);
        }
    }

    public String comp() {
        int delimiter = currentInstruction.indexOf("=");
        if (delimiter != -1) {
            return currentInstruction.substring(delimiter + 1);
        }

        delimiter = currentInstruction.indexOf(";");
        return currentInstruction.substring(0, delimiter);
    }


    public boolean hasMoreLines() {
        return reader.hasNextLine();
    }

    public void advance() {
        currentInstruction = nextInstruction;

        // find the next valid line
        String tempNextInstruction;
        do {
            if(!hasMoreLines()) {
                return;
            }

            tempNextInstruction = reader.nextLine();

            if(tempNextInstruction == null) {
                return;
            }
        } while(isEmptyLine(tempNextInstruction) || isComment(tempNextInstruction));

        // trim inline comment if exist
        int commentIdx = tempNextInstruction.indexOf("//");
        if(commentIdx != -1) {
            tempNextInstruction = tempNextInstruction.substring(0, commentIdx - 1);
        }

        nextInstruction = tempNextInstruction.trim();
    }

    private boolean isEmptyLine(String line) {
        return line.trim().isEmpty();
    }

    private boolean isComment(String line) {
        return line.trim().startsWith("//");
    }
}
