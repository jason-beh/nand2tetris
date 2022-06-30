package main.java.com.jasonbeh;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class CompilationEngineTest {
    private CompilationEngine compilationEngine;
    private BufferedWriter sourceFileWriter;
    private BufferedWriter comparisonFileWriter;
    private File sourceFile;
    private File outputFile;
    private File comparisonFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        sourceFile = folder.newFile();
        outputFile = folder.newFile();
        comparisonFile = folder.newFile();

        sourceFileWriter = new BufferedWriter(new FileWriter(sourceFile));
        comparisonFileWriter = new BufferedWriter(new FileWriter(comparisonFile));
    }

    @After
    public void printOutputFile() throws IOException {
        sourceFileWriter.close();
        comparisonFileWriter.close();

        compilationEngine = new CompilationEngine(sourceFile, outputFile);
        compilationEngine.close();

//        // Print output for debugging
//        BufferedReader reader = new BufferedReader(new FileReader(outputFile.getAbsolutePath()));
//        String line = "";
//        while (line != null) {
//            System.out.println(line);
//            line = reader.readLine();
//        }
//        reader.close();
//
//        reader = new BufferedReader(new FileReader(comparisonFile.getAbsolutePath()));
//        line = "";
//        while (line != null) {
//            System.out.println(line);
//            line = reader.readLine();
//        }
//        reader.close();

        assertEquals(-1, Files.mismatch(outputFile.toPath(), comparisonFile.toPath()));
    }

    @Test
    public void emptyClass() throws IOException {
        String jackCode = """
                    class Main {
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """ 
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void emptyMainMethod() throws IOException {
        String jackCode = """
                    class Main {
                        function void main() {
                            return;
                        }
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.main 0
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void singleSubroutineVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    function void main() {
                        var int num;
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.main 1
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void multipleSameSubroutineVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    function void main() {
                        var int num1, num2, num3;
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.main 3
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void multipleDifferentSubroutineVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    function void main() {
                        var int num1, num2, num3;
                        var String string1, string2;
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.main 5
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void singleClassVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    field int num;
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void multipleSameClassVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    field int num1, num2, num3;
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void multipleDifferentClassVarDeclaration() throws IOException {
        String jackCode = """
                class Main {
                    field int num1, num2, num3;
                    static String string1, string2;
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void emptyConstructor() throws IOException {
        String jackCode = """
                    class Main {
                        constructor Main new() {
                            return this;
                        }
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.new 0
                push constant 0
                call Memory.alloc 1
                pop pointer 0
                push pointer 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void intFieldAssignmentConstructor() throws IOException {
        String jackCode = """
                    class Main {
                        field int num;
                        constructor Main new(int newNum) {
                            let num = newNum;
                            return this;
                        }
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.new 0
                push constant 1
                call Memory.alloc 1
                pop pointer 0
                push argument 0
                pop this 0
                push pointer 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void staticAssignmentConstructor() throws IOException {
        String jackCode = """
                    class Main {
                        static int num;
                        constructor Main new(int newNum) {
                            let num = newNum;
                            return this;
                        }
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.new 0
                push constant 0
                call Memory.alloc 1
                pop pointer 0
                push argument 0
                pop static 0
                push pointer 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void stringFieldAssignmentConstructor() throws IOException {
        String jackCode = """
                    class Main {
                        field String name;
                        constructor Main new(String newName) {
                            let name = newName;
                            return this;
                        }
                    }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.new 0
                push constant 1
                call Memory.alloc 1
                pop pointer 0
                push argument 0
                pop this 0
                push pointer 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void returnIntMethod() throws IOException {
        String jackCode = """
                class Main {
                    field int counter;
                    method int incrementCounter() {
                        let counter = counter + 1;
                        return counter;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.incrementCounter 0
                push argument 0
                pop pointer 0
                push this 0
                push constant 1
                add
                pop this 0
                push this 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void varAssignmentMethod() throws IOException {
        String jackCode = """
                class Main {
                    field int counter;
                    method int setCounter(int newCounter) {
                        let counter = newCounter;
                        return counter;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.setCounter 0
                push argument 0
                pop pointer 0
                push argument 1
                pop this 0
                push this 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void isElseStatement() throws IOException {
        String jackCode = """
                class Main {
                    field int counter;
                   
                    method int isTrue() {
                        if(1 = 1) {
                            let counter = 1;
                        } else {
                            let counter = 0;
                        }
                        return counter;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.isTrue 0
                push argument 0
                pop pointer 0
                push constant 1
                push constant 1
                eq
                if-goto IF_TRUE0
                goto IF_FALSE0
                label IF_TRUE0
                push constant 1
                pop this 0
                goto IF_END0
                label IF_FALSE0
                push constant 0
                pop this 0
                label IF_END0
                push this 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void callFunctionWithoutAssignment() throws IOException {
        String jackCode = """
                class Main {
                    field int counter;
                   
                    method void print() {
                        do Output.printInt(counter);
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.print 0
                push argument 0
                pop pointer 0
                push this 0
                call Output.printInt 1
                pop temp 0
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void callFunctionWithAssignment() throws IOException {
        String jackCode = """
                class Main {
                    method int multiply() {
                        var int product;
                        let product = Math.multiply(2,3);
                        return product;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.multiply 1
                push argument 0
                pop pointer 0
                push constant 2
                push constant 3
                call Math.multiply 2
                pop local 0
                push local 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void whileLoop() throws IOException {
        String jackCode = """
                class Main {
                    method void printToN(int n) {
                        var int i;
                        let i = 0;
                        
                        while(~(i = n)) {
                            do Output.printInt(i);
                        }
                        
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.printToN 1
                push argument 0
                pop pointer 0
                push constant 0
                pop local 0
                label WHILE_EXP0
                push local 0
                push argument 1
                eq
                not
                not
                if-goto WHILE_END0
                push local 0
                call Output.printInt 1
                pop temp 0
                goto WHILE_EXP0
                label WHILE_END0
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void initializeArray() throws IOException {
        String jackCode = """
                class Main {
                    field Array arr;
                                
                    method void initializeArray(int n) {
                        let arr = Array.new(n);
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.initializeArray 0
                push argument 0
                pop pointer 0
                push argument 1
                call Array.new 1
                pop this 0
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }

    @Test
    public void setArrayIndex() throws IOException {
        String jackCode = """
                class Main {
                    field Array arr;
                                
                    method void setArrayIndex(int n) {
                        var int num;
                    
                        let arr = Array.new(n);
                        let num = arr[0];
                              
                        return;
                    }
                }
                """;
        sourceFileWriter.write(jackCode);

        String comparisonVMCode = """
                function Main.setArrayIndex 1
                push argument 0
                pop pointer 0
                push argument 1
                call Array.new 1
                pop this 0
                push constant 0
                push this 0
                add
                pop pointer 1
                push that 0
                pop local 0
                push constant 0
                return
                """;
        comparisonFileWriter.write(comparisonVMCode);
    }
}
