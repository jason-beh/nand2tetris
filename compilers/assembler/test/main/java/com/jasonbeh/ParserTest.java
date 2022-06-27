package main.java.com.jasonbeh;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;

public class ParserTest {

    private Parser parser;
    private BufferedWriter writer;
    private File currentFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        currentFile = folder.newFile();
        writer = new BufferedWriter(new FileWriter(currentFile));
    }

    @After
    public void print() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(currentFile.getAbsolutePath()));
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
        reader.close();
    }

    // Instruction types
    @Test
    public void identifyAInstruction() throws IOException {
        writer.write("@13");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Instruction.A_INSTRUCTION, parser.instructionType());
    }

    @Test
    public void identifyCInstruction() throws IOException {
        writer.write("M=D");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Instruction.C_INSTRUCTION, parser.instructionType());
    }

    @Test
    public void identifyLInstruction() throws IOException {
        writer.write("(LOOP)");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Instruction.L_INSTRUCTION, parser.instructionType());
    }

    // Symbols
    @Test
    public void parseAInstructionSymbol() throws IOException {
        writer.write("@sum");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("sum", parser.symbol());
    }

    @Test
    public void parseLInstructionSymbol() throws IOException {
        writer.write("(INFINITE_LOOP)");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("INFINITE_LOOP", parser.symbol());
    }

    // Destination
    @Test
    public void parseDestD() throws IOException {
        writer.write("D=M");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("D", parser.dest());
    }

    @Test
    public void parseDestM() throws IOException {
        writer.write("M=0");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("M", parser.dest());
    }

    @Test
    public void parseDestAMD() throws IOException {
        writer.write("AMD=1");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("AMD", parser.dest());
    }

    // Computation
    @Test
    public void parseCompWithEqualsM() throws IOException {
        writer.write("M=D");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("D", parser.comp());
    }

    @Test
    public void parseCompWithEqualsNotM() throws IOException {
        writer.write("M=!M");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("!M", parser.comp());
    }

    @Test
    public void parseCompWithoutEqualsA() throws IOException {
        writer.write("A;JMP");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("A", parser.comp());
    }

    @Test
    public void parseCompWithoutEqualsM() throws IOException {
        writer.write("M;JLT");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("M", parser.comp());
    }

    // Jumps
    @Test
    public void parseJumpJLT() throws IOException {
        writer.write("D;JLT");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("JLT", parser.jump());
    }

    @Test
    public void parseJumpJMP() throws IOException {
        writer.write("0;JMP");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("JMP", parser.jump());
    }

    @Test
    public void isLastLine() throws IOException {
        writer.write("0;JMP");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertFalse(parser.hasMoreLines());
    }

    @Test
    public void isNotLastLine() throws IOException {
        writer.write("@11\n");
        writer.write("M=D\n");
        writer.write("M=M+1\n");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertTrue(parser.hasMoreLines());
    }
}