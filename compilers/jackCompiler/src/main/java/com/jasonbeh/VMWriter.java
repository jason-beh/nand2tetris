package main.java.com.jasonbeh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VMWriter {
    private BufferedWriter vmCode;

    public enum Segment {
        SEG_CONSTANT,
        SEG_ARGUMENT,
        SEG_LOCAL,
        SEG_STATIC,
        SEG_THIS,
        SEG_THAT,
        SEG_POINTER,
        SEG_TEMP
    }

    public enum ArithmeticCmd {
        ARITHMETIC_ADD,
        ARITHMETIC_SUB,
        ARITHMETIC_NEG,
        ARITHMETIC_EQ,
        ARITHMETIC_GT,
        ARITHMETIC_LT,
        ARITHMETIC_AND,
        ARITHMETIC_OR,
        ARITHMETIC_NOT
    }

    public enum VmCmd {
        VM_PUSH,
        VM_POP,
        VM_LABEL,
        VM_GOTO,
        VM_IF_GOTO,
        VM_CALL,
        VM_FUNCTION,
        VM_RETURN
    }

    private Map<Segment, String> segmentMapping;
    private Map<ArithmeticCmd, String> arithmeticCmdMapping;
    private Map<VmCmd, String> vmCmdMapping;

    public VMWriter(File output) {
        try {
            initializeSegmentMapping();
            initializeArithmeticCmdMapping();
            initializeVMCmdMapping();
            vmCode = new BufferedWriter(new FileWriter(output));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSegmentMapping() {
        segmentMapping = new HashMap<>();
        segmentMapping.put(Segment.SEG_CONSTANT, "constant");
        segmentMapping.put(Segment.SEG_ARGUMENT, "argument");
        segmentMapping.put(Segment.SEG_LOCAL, "local");
        segmentMapping.put(Segment.SEG_STATIC, "static");
        segmentMapping.put(Segment.SEG_THIS, "this");
        segmentMapping.put(Segment.SEG_THAT, "that");
        segmentMapping.put(Segment.SEG_POINTER, "pointer");
        segmentMapping.put(Segment.SEG_TEMP, "temp");
    }

    private void initializeArithmeticCmdMapping() {
        arithmeticCmdMapping = new HashMap<>();
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_ADD, "add");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_SUB, "sub");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_NEG, "neg");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_EQ, "eq");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_GT, "gt");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_LT, "lt");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_AND, "and");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_OR, "or");
        arithmeticCmdMapping.put(ArithmeticCmd.ARITHMETIC_NOT, "not");
    }

    private void initializeVMCmdMapping() {
        vmCmdMapping = new HashMap<>();
        vmCmdMapping.put(VmCmd.VM_PUSH, "push");
        vmCmdMapping.put(VmCmd.VM_POP, "pop");
        vmCmdMapping.put(VmCmd.VM_LABEL, "label");
        vmCmdMapping.put(VmCmd.VM_GOTO, "goto");
        vmCmdMapping.put(VmCmd.VM_IF_GOTO, "if-goto");
        vmCmdMapping.put(VmCmd.VM_CALL, "call");
        vmCmdMapping.put(VmCmd.VM_FUNCTION, "function");
        vmCmdMapping.put(VmCmd.VM_RETURN, "return");
    }

    private void writeVMLine(String line) throws IOException {
        vmCode.append(line).append("\n");
    }

    public void writePush(Segment segment, int index) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_PUSH) + " " + segmentMapping.get(segment) + " " + index);
    }

    public void writePop(Segment segment, int index) throws IOException {
        String line = vmCmdMapping.get(VmCmd.VM_POP) + " " + segmentMapping.get(segment) + " " + index;

        if(segment == Segment.SEG_CONSTANT) {
            writeVMLine("======== ERROR: " + line + " ========");
            return;
        }

        writeVMLine(line);
    }

    public void writeArithmetic(ArithmeticCmd arithmeticCmd) throws IOException {
        writeVMLine(arithmeticCmdMapping.get(arithmeticCmd));
    }

    public void writeLabel(String label) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_LABEL) + " " + label);
    }

    public void writeGoto(String label) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_GOTO) + " " + label);
    }

    public void writeIf(String label) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_IF_GOTO) + " " + label);
    }

    public void writeCall(String functionName, int nArgs) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_CALL) + " " + functionName + " " + nArgs);
    }

    public void writeFunction(String functionName, int nVars) throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_FUNCTION) + " " + functionName + " " + nVars);
    }

    public void writeReturn() throws IOException {
        writeVMLine(vmCmdMapping.get(VmCmd.VM_RETURN));
    }

    public void close() throws IOException {
        vmCode.close();
    }

}
