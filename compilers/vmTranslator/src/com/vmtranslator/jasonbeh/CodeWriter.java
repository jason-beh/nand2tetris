package com.vmtranslator.jasonbeh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeWriter {
    private BufferedWriter asmCode;
    private String filename;
    private int labelCounter;

    private Map<String, String> segmentMapping;

    public CodeWriter(File output) {
        try {
            initializeSegmentMapping();
            asmCode = new BufferedWriter(new FileWriter(output));
            labelCounter = 0;
            writeBootstrapCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSegmentMapping() {
        segmentMapping = new HashMap<>();
        segmentMapping.put("argument", "ARG");
        segmentMapping.put("local", "LCL");
        segmentMapping.put("this", "THIS");
        segmentMapping.put("that", "THAT");
        segmentMapping.put("temp", "R5");
        segmentMapping.put("static", "R16");
        segmentMapping.put("pointer", "pointer");
    }

    public void setFileName(String newFileName) {
        filename = newFileName;
    }

    private void writeAsmLine(String line) throws IOException {
        asmCode.append(line).append("\n");
    }

    public void writeBootstrapCode() throws IOException {
        // Set SP to 256 and call Sys.init
        writeAsmLine("@256");
        writeAsmLine("D=A");
        writeAsmLine("@SP");
        writeAsmLine("M=D");
        writeCall("Sys.init", 0);
    }

    private void writeStagingTwoAddressOperation() throws IOException {
        // Get top most value in the stack and move backwards for subsequent operation
        writeAsmLine("@SP");
        writeAsmLine("A=M-1");
        writeAsmLine("D=M");
        writeAsmLine("A=A-1");
    }

    private void writeDecrementSP() throws IOException {
        writeAsmLine("@SP");
        writeAsmLine("M=M-1");
    }

    private void writeIncrementSP() throws IOException {
        writeAsmLine("@SP");
        writeAsmLine("A=M");
        writeAsmLine("M=D");
        writeAsmLine("@SP");
        writeAsmLine("M=M+1");
    }

    private void writeAdd() throws IOException {
        writeStagingTwoAddressOperation();
        writeAsmLine("M=D+M");
        writeDecrementSP();
    }

    private void writeSub() throws IOException {
        writeStagingTwoAddressOperation();
        writeAsmLine("M=M-D");
        writeDecrementSP();
    }

    private void writeAnd() throws IOException {
        writeStagingTwoAddressOperation();
        writeAsmLine("M=D&M");
        writeDecrementSP();
    }

    private void writeOr() throws IOException {
        writeStagingTwoAddressOperation();
        writeAsmLine("M=D|M");
        writeDecrementSP();
    }

    private void writeNot() throws IOException {
        writeAsmLine("@SP");
        writeAsmLine("A=M-1");
        writeAsmLine("M=!M");
    }

    private void writeNeg() throws IOException {
        writeAsmLine("@SP");
        writeAsmLine("A=M-1");
        writeAsmLine("M=-M");
    }

    private void writeComparator(String line) throws IOException {
        // Common code between eq, lt and gt
        writeAsmLine("@SP");
        writeAsmLine("A=M-1");
        writeAsmLine("D=M");
        writeAsmLine("A=A-1");
        writeAsmLine("D=M-D");
        writeAsmLine("@SP");
        writeAsmLine("M=M-1");

        writeAsmLine("@LABEL" + labelCounter);
        writeAsmLine(line);

        writeAsmLine("@SP");
        writeAsmLine("AM=M-1");
        writeAsmLine("M=0");

        writeAsmLine("@NEXT" + labelCounter);
        writeAsmLine("0;JMP");

        writeAsmLine("(LABEL" + labelCounter + ")");
        writeAsmLine("@SP");
        writeAsmLine("AM=M-1");
        writeAsmLine("M=-1");

        writeAsmLine("(NEXT" + labelCounter + ")");
        writeAsmLine("@SP");
        writeAsmLine("M=M+1");

        labelCounter++;
    }

    private void writeEq() throws IOException {
        writeComparator("D;JEQ");
    }

    private void writeLt() throws IOException {
        writeComparator("D;JLT");
    }

    private void writeGt() throws IOException {
        writeComparator("D;JGT");
    }

    public void writeArithmetic(String line) throws IOException {
        switch (line) {
            case "add" -> writeAdd();
            case "sub" -> writeSub();
            case "and" -> writeAnd();
            case "or" -> writeOr();
            case "eq" -> writeEq();
            case "lt" -> writeLt();
            case "gt" -> writeGt();
            case "not" -> writeNot();
            case "neg" -> writeNeg();
        }
    }

    public void writePush(String segment, int index) throws IOException {
        // Handle constant
        if (segment.equals("constant")) {
            writeAsmLine("@" + index);
            writeAsmLine("D=A");
            writeIncrementSP();
            return;
        }

        if (segmentMapping.containsKey(segment)) {
            String segmentPointer = segmentMapping.get(segment);

            // Handle pointer
            if (segmentPointer.equals("pointer")) {
                if (index == 0) {
                    segment = "this";
                } else if (index == 1) {
                    segment = "that";
                }

                segmentPointer = segmentMapping.get(segment);

                writeAsmLine("@" + segmentPointer);
                writeAsmLine("D=M");
                writeIncrementSP();
                return;
            }

            // Get offset number
            writeAsmLine("@" + index);
            writeAsmLine("D=A");

            if (segment.equals("static")) {
                writeAsmLine("@" + filename + "." + index);
            } else {
                writeAsmLine("@" + segmentPointer);
            }

            if (!segment.equals("temp") && !segment.equals("static")) {
                writeAsmLine("A=M");
            }

            writeAsmLine("A=A+D");
            writeAsmLine("D=M");
            writeIncrementSP();
        }
    }

    public void writePop(String segment, int index) throws IOException {
        if (segmentMapping.containsKey(segment)) {
            String segmentPointer = segmentMapping.get(segment);

            // Handle pointer
            if (segmentPointer.equals("pointer")) {
                if (index == 0) {
                    segment = "this";
                } else if (index == 1) {
                    segment = "that";
                }

                segmentPointer = segmentMapping.get(segment);

                writeDecrementSP();
                writeAsmLine("A=M");
                writeAsmLine("D=M");

                writeAsmLine("@" + segmentPointer);
                writeAsmLine("M=D");
                return;
            }

            // Save target address into R13
            writeAsmLine("@" + index);
            writeAsmLine("D=A");

            if (segment.equals("static")) {
                writeAsmLine("@" + filename + "." + index);
            } else {
                writeAsmLine("@" + segmentPointer);
            }

            if (!segment.equals("temp") && !segment.equals("static")) {
                writeAsmLine("A=M");
            }

            writeAsmLine("A=A+D");
            writeAsmLine("D=A");

            writeAsmLine("R13");
            writeAsmLine("M=D");

            // Get stack value
            writeDecrementSP();
            writeAsmLine("A=M");
            writeAsmLine("D=M");

            // Access target memory using R13 and save it
            writeAsmLine("@R13");
            writeAsmLine("A=M");
            writeAsmLine("M=D");
        }
    }

    public void writePushPop(Parser.Command command, String segment, int index) throws IOException {
        if (command == Parser.Command.C_PUSH) {
            writePush(segment, index);
        } else if (command == Parser.Command.C_POP) {
            writePop(segment, index);
        }
    }

    public void writeLabel(String label) throws IOException {
        writeAsmLine("(" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        writeAsmLine("@" + label);
        writeAsmLine("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        writeAsmLine("@SP");
        writeAsmLine("A=M");
        writeAsmLine("M=M-1");
        writeAsmLine("D=M");
        writeAsmLine("@" + label);
        writeAsmLine("D;JNE");
    }

    public void writeFunction(String functionName, int numLocals) throws IOException {
        writeLabel(functionName);

        // Initialize local variables to 0
        for (int i = 0; i < numLocals; i++) {
            writePushPop(Parser.Command.C_PUSH, "constant", 0);
        }
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        // Create stack frame to remember calling function
        writeAsmLine("@return-address" + labelCounter);
        writeAsmLine("D=A");
        writeIncrementSP();

        writeAsmLine("@LCL");
        writeAsmLine("D=M");
        writeIncrementSP();

        writeAsmLine("@ARG");
        writeAsmLine("D=M");
        writeIncrementSP();

        writeAsmLine("@THIS");
        writeAsmLine("D=M");
        writeIncrementSP();

        writeAsmLine("@THAT");
        writeAsmLine("D=M");
        writeIncrementSP();

        // Reposition ARG
        writeAsmLine("@SP");
        writeAsmLine("D=M");
        writeAsmLine("@" + numArgs);
        writeAsmLine("D=D-A");
        writeAsmLine("@5");
        writeAsmLine("D=D-A");
        writeAsmLine("@ARG");
        writeAsmLine("M=D");

        // Reposition LCL
        writeAsmLine("@SP");
        writeAsmLine("D=M");
        writeAsmLine("@LCL");
        writeAsmLine("M=D");

        // Transfer control to callee
        writeGoto(functionName);

        // Declare return address label so that we can jump to the next immediate instruction after calling the function
        writeLabel("return-address" + labelCounter);
        labelCounter++;
    }

    private void writeRestore(int index) throws IOException {
        // Restore
        writeAsmLine("@FRAME");
        writeAsmLine("D=M");
        writeAsmLine("@" + index);
        writeAsmLine("A=D-A");
        writeAsmLine("D=M");
    }

    public void writeReturn() throws IOException {
        // FRAME = LCL
        writeAsmLine("@LCL");
        writeAsmLine("D=M");
        writeAsmLine("@FRAME");
        writeAsmLine("M=D");

        // RETURN_ADDRESS = FRAME - 5
        writeAsmLine("@5");
        writeAsmLine("A=D-A");
        writeAsmLine("D=M");
        writeAsmLine("@RETURN_ADDRESS");
        writeAsmLine("M=D");

        // ARG = pop()
        writeAsmLine("@SP");
        writeAsmLine("AM=M-1");
        writeAsmLine("D=M");
        writeAsmLine("@ARG");
        writeAsmLine("A=M");
        writeAsmLine("M=D");

        // Restore SP using stack frame
        writeAsmLine("@ARG");
        writeAsmLine("D=M+1");
        writeAsmLine("@SP");
        writeAsmLine("M=D");

        // Restore THAT using stack frame
        writeRestore(1);
        writeAsmLine("@THAT");
        writeAsmLine("M=D");

        // Restore THIS using stack frame
        writeRestore(2);
        writeAsmLine("@THIS");
        writeAsmLine("M=D");

        // Restore ARG using stack frame
        writeRestore(3);
        writeAsmLine("@ARG");
        writeAsmLine("M=D");

        // Restore LCL using stack frame
        writeRestore(4);
        writeAsmLine("M=D");

        // Jump to return address
        writeAsmLine("@RETURN_ADDRESS");
        writeAsmLine("A=M");
        writeAsmLine("0;JMP");
    }

    public void close() throws IOException {
        asmCode.close();
    }

}
