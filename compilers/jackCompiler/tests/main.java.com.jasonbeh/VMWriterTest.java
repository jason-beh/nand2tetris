package main.java.com.jasonbeh;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import static org.junit.Assert.*;

public class VMWriterTest {
    private BufferedWriter writer;
    private File currentFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        currentFile = folder.newFile();
    }

    // Test Arithmetic Commands

    @Test
    public void testWriteAdd() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_ADD);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("add", line);
    }

    @Test
    public void testWriteSub() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_SUB);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("sub", line);
    }

    @Test
    public void testWriteNeg() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NEG);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("neg", line);
    }

    @Test
    public void testWriteEq() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_EQ);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("eq", line);
    }

    @Test
    public void testWriteGt() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_GT);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("gt", line);
    }

    @Test
    public void testWriteLt() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_LT);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("lt", line);
    }

    @Test
    public void testWriteAnd() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_AND);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("and", line);
    }

    @Test
    public void testWriteOr() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_OR);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("or", line);
    }

    @Test
    public void testWriteNot() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NOT);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("not", line);
    }

    // Test VM Commands

    @Test
    public void testWritePush() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_CONSTANT, 3);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push constant 3", line);
    }

    @Test
    public void testWritePop() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePop(VMWriter.Segment.SEG_ARGUMENT, 5);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("pop argument 5", line);
    }

    @Test
    public void testWriteLabel() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeLabel("JUMP_LABEL_HERE");
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("label JUMP_LABEL_HERE", line);
    }

    @Test
    public void testWriteGoto() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeGoto("GOTO_LABEL");
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("goto GOTO_LABEL", line);
    }

    @Test
    public void testWriteIfGoto() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeIf("IF_GOTO_LABEL");
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("if-goto IF_GOTO_LABEL", line);
    }

    @Test
    public void testWriteCall() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeCall("customFunction", 2);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("call customFunction 2", line);
    }

    @Test
    public void testWriteFunction() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeFunction("anotherCustomFunction", 3);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("function anotherCustomFunction 3", line);
    }

    @Test
    public void testWriteReturn() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writeReturn();
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("return", line);
    }

    // Test Segments
    @Test
    public void testWritePushConstant() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_CONSTANT, 20);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push constant 20", line);
    }

    @Test
    public void testWritePushArgument() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_ARGUMENT, 3);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push argument 3", line);
    }

    @Test
    public void testWritePushLocal() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_LOCAL, 5);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push local 5", line);
    }

    @Test
    public void testWritePushStatic() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_STATIC, 0);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push static 0", line);
    }

    @Test
    public void testWritePushThis() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_THIS, 1);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push this 1", line);
    }

    @Test
    public void testWritePushThat() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_THAT, 2);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push that 2", line);
    }

    @Test
    public void testWritePushPointer() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_POINTER, 1);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push pointer 1", line);
    }

    @Test
    public void testWritePushTemp() throws IOException {
        VMWriter codeWriter = new VMWriter(currentFile);
        codeWriter.writePush(VMWriter.Segment.SEG_TEMP, 4);
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("push temp 4", line);
    }
}
