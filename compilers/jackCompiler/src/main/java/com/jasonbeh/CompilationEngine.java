package main.java.com.jasonbeh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilationEngine {
    private record Variable(String name, String type) {
        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    private int counter;
    private final SymbolTable symbolTable;
    private final VMWriter vmWriter;
    private final JackTokenizer jackTokenizer;

    private String className;

    public CompilationEngine(File sourceFile, File targetFile) throws IOException {
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(targetFile);
        jackTokenizer = new JackTokenizer(sourceFile);
        counter = 0;

        advanceTokenIfPossible();
        compileClass();
    }

    private void advanceTokenIfPossible() {
        if(jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
        }
    }

    private void compileClass() throws IOException {
        // e.g. class Person { statements }

        // class
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // className
        className = jackTokenizer.identifier();
        advanceTokenIfPossible();

        // {
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // class level variable declarations
        compileClassVarDec();

        // class level subroutines (methods, constructors, functions)
        while(!(isCurrentTokenSymbol('}'))) {
            compileSubroutine();
        }

        // }
        jackTokenizer.symbol();
        advanceTokenIfPossible();
    }

    private void compileClassVarDec() {
        // e.g. field int num;
        // e.g. field int num1, num2, num3;
        // e.g. static int classSize

        while(jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_KEYWORD) {
            // ensure it is static or field for class level variables
            if(!(jackTokenizer.keyword().equals("static") || jackTokenizer.keyword().equals("field"))) {
                continue;
            }

            // variable symbol table kind
            String varKind = jackTokenizer.keyword();
            advanceTokenIfPossible();

            // variable data type
            String varDataType = jackTokenizer.identifier();
            advanceTokenIfPossible();

            // get all variable names for multiple variable declaration
            List<String> varNames = getVarNames();

            // populate symbol table
            for(String varName : varNames) {
                symbolTable.define(varName, varDataType, symbolTable.stringToKind(varKind));
            }
        }
    }

    private void compileSubroutine() throws IOException {
        // e.g. method void customFunction(int num, boolean isTrue) { statements }
        // e.g. constructor Rectangle new(int width, int height) { statements }
        // e.g. function void compute(int num) { statements }

        // method, constructor or function
        String subroutineType = jackTokenizer.keyword();
        advanceTokenIfPossible();

        // return type - unused because VM doesn't care about return types
        jackTokenizer.identifier();
        advanceTokenIfPossible();

        // subroutine name
        String subroutineName = jackTokenizer.identifier();
        advanceTokenIfPossible();

        // create another symbol table for subroutine level
        symbolTable.initializeSubroutineScope();

        // adjust for method type of subroutine
        if(subroutineType.equals("method")) {
            symbolTable.adjustMethodSymbolTable();
        }

        // (
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Parameters List
        List<Variable> parameterList = compileParameterList();
        for(Variable param: parameterList) {
            symbolTable.define(param.getName(), param.getType(), SymbolTable.Kind.KIND_ARG);
        }

        // )
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // {
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Local Variables List
        List<Variable> localVariablesList = compileVarDec();
        for(Variable localVariable: localVariablesList) {
            symbolTable.define(localVariable.getName(), localVariable.getType(), SymbolTable.Kind.KIND_VAR);
        }

        // Write subroutine declaration
        String functionNameInVM = className + "." + subroutineName;
        vmWriter.writeFunction(functionNameInVM, symbolTable.varCount(SymbolTable.Kind.KIND_VAR));

        // After pushing all parameters and local variables, we can write actual VM code
        if(subroutineType.equals("method")) {
            vmWriter.writePush(VMWriter.Segment.SEG_ARGUMENT, 0);
            vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 0);
        } else if(subroutineType.equals("constructor")) {
            vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, symbolTable.varCount(SymbolTable.Kind.KIND_FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 0);
        }

        // Compile rest of statements after local variables declaration
        compileStatements();

        // }
        jackTokenizer.symbol();
        advanceTokenIfPossible();
    }

    private List<Variable> compileParameterList() {
        // e.g. method void customFunction(int num, boolean isTrue) { statements }

        List<Variable> parameters = new ArrayList<>();
        while(!isCurrentTokenSymbol(')')) {

            // process comma if we encounter it
            if(isCurrentTokenSymbol(',')) {
                jackTokenizer.symbol();
                advanceTokenIfPossible();
            }

            String varType = jackTokenizer.identifier();
            advanceTokenIfPossible();

            String varName = jackTokenizer.identifier();
            advanceTokenIfPossible();

            parameters.add(new Variable(varName, varType));
        }

        return parameters;
    }

    private List<Variable> compileVarDec() {
        // e.g. var int num;
        // e.g. var int num1, num2;
        List<Variable> variables = new ArrayList<>();

        while(isCurrentTokenKeyword("var")) {
            // var
            jackTokenizer.keyword();
            advanceTokenIfPossible();

            // variable data type
            String varType = jackTokenizer.identifier();

            // get all list of variable names
            List<String> varNames = getVarNames();

            for(String varName: varNames) {
                variables.add(new Variable(varName, varType));
            }
        }

        return variables;
    }

    private void compileStatements() throws IOException {
        // Controller for let, while, if, do or return statement
        while(!isCurrentTokenSymbol('}')) {
            if(isCurrentTokenKeyword("return")) {
                compileReturn();
            } else if(isCurrentTokenKeyword("let")) {
                compileLet();
            } else if(isCurrentTokenKeyword("while")) {
                compileWhile();
            } else if(isCurrentTokenKeyword("if")) {
                compileIf();
            } else if(isCurrentTokenKeyword("do")) {
                compileDo();
            }
        }
    }

    private void compileLet() throws IOException {
        // e.g. let test = 3;
        // e.g. let arr[3] = 10;

        // let
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // identifier
        String varName = jackTokenizer.identifier();
        advanceTokenIfPossible();

        // array indexing
        if(isCurrentTokenSymbol('[')) {
            // Push current varName to setup for subsequent calculation
            vmWriter.writePush(vmWriter.stringToSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));

            // [
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Expression
            compileExpression();

            // ]
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Add offset and pop into pointer 1
            vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_ADD);
            vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 1);

            // =
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Expression
            compileExpression();

            // ;
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Pop to that 0
            vmWriter.writePop(VMWriter.Segment.SEG_THAT, 0);

        } else if(isCurrentTokenSymbol('=')) {
            // Assignment only (e.g. let num = 3)

            // =
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Expression
            compileExpression();

            // ;
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Pop to the variable
            vmWriter.writePop(vmWriter.stringToSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
        }
    }

    private int compileExpressionList() {
        // e.g. var int x,y;

        int numExpressions = 0;
        numExpressions += compileExpression();

        while(isCurrentTokenSymbol(',')) {
            // ;
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            numExpressions += compileExpression();
        }
        return numExpressions;
    }

    private void compileWhile() throws IOException {
        // e.g. while(num < 3) { statements }

        // while
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // label loop
        vmWriter.writeLabel("WHILE_BEGIN" + counter);

        // (
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Expression
        compileExpression();

        // )
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // If we don't fulfill the condition jump to end
        vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NOT);
        vmWriter.writeIf("WHILE_END" + counter);

        // {
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // statements
        compileStatements();

        // }
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        vmWriter.writeGoto("WHILE_BEGIN" + counter);
        vmWriter.writeLabel("WHILE_END" + counter);

        counter++;
    }

    private void compileReturn() throws IOException {
        // e.g. return;
        // e.g. return 4;
        // e.g. return name;

        // return
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // if nothing is explicitly returned
        if(isCurrentTokenSymbol(';')) {
            // By default, we return 0, although there's no explicit return
            vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, 0);
        } else {
            // a variable was explicitly returned
            compileExpression();
        }

        // write return
        vmWriter.writeReturn();

        // ;
        jackTokenizer.symbol();
        advanceTokenIfPossible();
    }

    private void compileIf() throws IOException {
        // e.g. if(num > 1) { statements }

        // if
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // (
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Expression
        compileExpression();

        // )
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // {
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        vmWriter.writeIf("IF_TRUE" + counter);
        vmWriter.writeGoto("IF_FALSE" + counter);
        vmWriter.writeLabel("IF_TRUE" + counter);

        // statements
        compileStatements();

        // }
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        vmWriter.writeGoto("IF_END" + counter);
        vmWriter.writeLabel("IF_FALSE" + counter);

        // Parse else block if exist
        if(isCurrentTokenKeyword("else")) {
            // else
            jackTokenizer.keyword();
            advanceTokenIfPossible();

            // {
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // statements
            compileStatements();

            // }
            jackTokenizer.symbol();
            advanceTokenIfPossible();
        }

        vmWriter.writeLabel("IF_END" + counter);
        counter++;
    }

    private int compileExpression() {
        return -1;
    }

    private void compileTerm() {
    }

    private void compileDo() throws IOException {
        // E.g. do Output.println 3;

        // do
        jackTokenizer.keyword();
        advanceTokenIfPossible();

        // class.functionName
        compileTerm();

        // ;
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Reset by popping top of stack into temp 0
        vmWriter.writePop(VMWriter.Segment.SEG_TEMP, 0);
    }


    private List<String> getVarNames() {
        // e.g. var int x,y;
        List<String> varNames = new ArrayList<>();

        // varName
        varNames.add(jackTokenizer.identifier());
        advanceTokenIfPossible();

        while(!isCurrentTokenSymbol(';')) {
            // ,
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // varName
            varNames.add(jackTokenizer.identifier());
            advanceTokenIfPossible();
        }

        // ;
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        return varNames;
    }

    private boolean isCurrentTokenSymbol(char symbol) {
        return jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_SYMBOL && jackTokenizer.symbol() == symbol;
    }

    private boolean isCurrentTokenKeyword(String keyword) {
        return jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_KEYWORD && jackTokenizer.keyword().equals(keyword);
    }


    public void close() throws IOException {
        vmWriter.close();
    }
}
