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

    // Command types
    @Test
    public void identifyAdd() throws IOException {
        writer.write("add");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyNeg() throws IOException {
        writer.write("neg");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifySub() throws IOException {
        writer.write("sub");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyGt() throws IOException {
        writer.write("gt");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyLt() throws IOException {
        writer.write("lt");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyEq() throws IOException {
        writer.write("eq");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyAnd() throws IOException {
        writer.write("and");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyOr() throws IOException {
        writer.write("or");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyNot() throws IOException {
        writer.write("not");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_ARITHMETIC, parser.commandType());
    }

    @Test
    public void identifyFunction() throws IOException {
        writer.write("function testFunction 2");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_FUNCTION, parser.commandType());
    }

    @Test
    public void identifyPush() throws IOException {
        writer.write("push static 3");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_PUSH, parser.commandType());
    }

    @Test
    public void identifyPop() throws IOException {
        writer.write("pop local 2");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_POP, parser.commandType());
    }

    @Test
    public void identifyLabel() throws IOException {
        writer.write("label HELLO_WORLD");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_LABEL, parser.commandType());
    }

    @Test
    public void identifyGoto() throws IOException {
        writer.write("goto BYE_BYE");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_GOTO, parser.commandType());
    }

    @Test
    public void identifyIfGoto() throws IOException {
        writer.write("if-goto JUMP_AWAY");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_IF, parser.commandType());
    }

    @Test
    public void identifyCall() throws IOException {
        writer.write("call multiply 2");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_CALL, parser.commandType());
    }

    @Test
    public void identifyReturn() throws IOException {
        writer.write("return");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(Parser.Command.C_RETURN, parser.commandType());
    }

    // Arg1
    @Test
    public void obtainArg1Push() throws IOException {
        writer.write("push local 2");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("local", parser.arg1());
    }

    @Test
    public void obtainArg1Pop() throws IOException {
        writer.write("pop static 4");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("static", parser.arg1());
    }

    @Test
    public void obtainArg1Add() throws IOException {
        writer.write("add");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("add", parser.arg1());
    }

    @Test
    public void obtainArg1Function() throws IOException {
        writer.write("function addToN 1");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("addToN", parser.arg1());
    }

    @Test
    public void obtainArg1Label() throws IOException {
        writer.write("label WORMHOLE");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("WORMHOLE", parser.arg1());
    }

    @Test
    public void obtainArg1Goto() throws IOException {
        writer.write("goto PORTAL");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals("PORTAL", parser.arg1());
    }

    // Arg2
    @Test
    public void obtainArg2Push() throws IOException {
        writer.write("push constant 4");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(4, parser.arg2());
    }

    @Test
    public void obtainArg2Pop() throws IOException {
        writer.write("push that 6");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(6, parser.arg2());
    }

    @Test
    public void obtainArg2Call() throws IOException {
        writer.write("call MagicalCode 3");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertEquals(3, parser.arg2());
    }

    // hasMoreLines
    @Test
    public void isLastLine() throws IOException {
        writer.write("push constant 10");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertFalse(parser.hasMoreLines());
    }

    @Test
    public void isNotLastLine() throws IOException {
        writer.write("push constant 3\n");
        writer.write("push constant 12\n");
        writer.write("add\n");
        writer.close();

        parser = new Parser(currentFile.getAbsolutePath());
        parser.advance();
        assertTrue(parser.hasMoreLines());
    }
}