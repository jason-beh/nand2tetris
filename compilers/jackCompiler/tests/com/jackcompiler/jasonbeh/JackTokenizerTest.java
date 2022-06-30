package com.jackcompiler.jasonbeh;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class JackTokenizerTest {
    private JackTokenizer tokenizer;
    private BufferedWriter writer;
    private File currentFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        currentFile = folder.newFile();
        writer = new BufferedWriter(new FileWriter(currentFile));
    }

//    @After
//    public void print() throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(currentFile.getAbsolutePath()));
//        String line = reader.readLine();
//        while (line != null) {
//            System.out.println(line);
//            line = reader.readLine();
//        }
//        reader.close();
//    }

    @Test
    public void hasNoMoreAvailableTokens() throws IOException {
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertFalse(tokenizer.hasMoreTokens());
    }

    @Test
    public void hasMoreAvailableTokens() throws IOException {
        writer.write("let x = 10;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertTrue(tokenizer.hasMoreTokens());
    }

    // TokenType - Keyword
    @Test
    public void tokenTypeKeywordClass() throws IOException {
        writer.write("class Main {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordMethod() throws IOException {
        writer.write("method randomMethod() {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordFunction() throws IOException {
        writer.write("function exampleFunction() {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordConstructor() throws IOException {
        writer.write("constructor Shape() {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordInt() throws IOException {
        writer.write("int time = 3;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordBoolean() throws IOException {
        writer.write("boolean isHappy = true;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordChar() throws IOException {
        writer.write("char x = '3';");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordVoid() throws IOException {
        writer.write("void myFunction() {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordVar() throws IOException {
        writer.write("var pig;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordStatic() throws IOException {
        writer.write("static int test;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordField() throws IOException {
        writer.write("field String wish;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordLet() throws IOException {
        writer.write("let car = \"Lamborghini\";");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordDo() throws IOException {
        writer.write("do Output.printInt(3);");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordIf() throws IOException {
        writer.write("if(true) {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordElse() throws IOException {
        writer.write("else {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordWhile() throws IOException {
        writer.write("while(num < 1) {");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordReturn() throws IOException {
        writer.write("return");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordTrue() throws IOException {
        writer.write("true");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordFalse() throws IOException {
        writer.write("false");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordNull() throws IOException {
        writer.write("null");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeKeywordThis() throws IOException {
        writer.write("this");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
    }

    // TokenType - Int
    @Test
    public void tokenTypeIntConst() throws IOException {
        writer.write("321341");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_INT_CONST, tokenizer.tokenType());
    }

    // TokenType - String
    @Test
    public void tokenTypeStringConst() throws IOException {
        writer.write("\"I am the king of the jungle\"");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_STRING_CONST, tokenizer.tokenType());
    }

    // TokenType - Symbol
    @Test
    public void tokenTypeSymbolOpenCurlyBracket() throws IOException {
        writer.write("{");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolCloseCurlyBracket() throws IOException {
        writer.write("}");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolOpenRoundBracket() throws IOException {
        writer.write("(");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolCloseRoundBracket() throws IOException {
        writer.write(")");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolOpenSquareBracket() throws IOException {
        writer.write("[");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolCloseSquareBracket() throws IOException {
        writer.write("]");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolPeriod() throws IOException {
        writer.write(".");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolComma() throws IOException {
        writer.write(",");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolSemiColon() throws IOException {
        writer.write(";");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolPlus() throws IOException {
        writer.write("+");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolMinus() throws IOException {
        writer.write("-");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolMultiply() throws IOException {
        writer.write("*");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolDivide() throws IOException {
        writer.write("/");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolAnd() throws IOException {
        writer.write("&");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolOr() throws IOException {
        writer.write("|");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolLeftAngleBracket() throws IOException {
        writer.write("<");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolRightAngleBracket() throws IOException {
        writer.write(">");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolEqual() throws IOException {
        writer.write("=");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    @Test
    public void tokenTypeSymbolNot() throws IOException {
        writer.write("~");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
    }

    // TokenType - Symbol
    @Test
    public void tokenTypeIdentifier() throws IOException {
        writer.write("lksjlkjsdflkjla");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_IDENTIFIER, tokenizer.tokenType());
    }

    // Tokenize
    @Test
    public void tokenizeTest() throws IOException {
        writer.write("let x = 59;");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);

        assertEquals("let", tokenizer.identifier());
        tokenizer.advance();
        assertEquals("x", tokenizer.identifier());
        tokenizer.advance();
        assertEquals("=", tokenizer.identifier());
        tokenizer.advance();
        assertEquals("59", tokenizer.identifier());
        tokenizer.advance();
        assertEquals(";", tokenizer.identifier());
    }

    // intVal
    @Test
    public void getIntVal() throws IOException {
        writer.write("100");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_INT_CONST, tokenizer.tokenType());
        assertEquals(100, tokenizer.intVal());
    }

    // stringVal
    @Test
    public void getStringVal() throws IOException {
        writer.write("\"Hello World\"");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_STRING_CONST, tokenizer.tokenType());
        assertEquals("Hello World", tokenizer.stringVal());
    }

    // symbol
    @Test
    public void getSymbol() throws IOException {
        writer.write("{}()");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_SYMBOL, tokenizer.tokenType());
        assertEquals('{', tokenizer.symbol());
    }

    // keyword
    @Test
    public void getKeyword() throws IOException {
        writer.write("function");
        writer.close();

        tokenizer = new JackTokenizer(currentFile);
        assertEquals(JackTokenizer.TokenType.TOKEN_KEYWORD, tokenizer.tokenType());
        assertEquals("function", tokenizer.keyword());
    }
}
