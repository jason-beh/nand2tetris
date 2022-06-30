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
    private String className;
    private final SymbolTable symbolTable;
    private final VMWriter vmWriter;
    private final JackTokenizer jackTokenizer;
    private boolean isAssigningToArray;


    public CompilationEngine(File sourceFile, File targetFile) throws IOException {
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter(targetFile);
        jackTokenizer = new JackTokenizer(sourceFile);
        counter = 0;
        isAssigningToArray = false;

        compileClass();
    }

    private void advanceTokenIfPossible() {
        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
        }
    }

    public void compileClass() throws IOException {
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
        while (!(isCurrentTokenSymbol('}'))) {
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

        while (jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_KEYWORD) {
            // ensure it is static or field for class level variables
            if (!(isCurrentTokenKeyword("static") || isCurrentTokenKeyword("field"))) {
                break;
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
            for (String varName : varNames) {
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
        if (subroutineType.equals("method")) {
            symbolTable.adjustMethodSymbolTable();
        }

        // (
        jackTokenizer.symbol();
        advanceTokenIfPossible();

        // Parameters List
        List<Variable> parameterList = compileParameterList();
        for (Variable param : parameterList) {
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
        for (Variable localVariable : localVariablesList) {
            symbolTable.define(localVariable.getName(), localVariable.getType(), SymbolTable.Kind.KIND_VAR);
        }

        // Write subroutine declaration
        String functionNameInVM = className + "." + subroutineName;
        vmWriter.writeFunction(functionNameInVM, symbolTable.varCount(SymbolTable.Kind.KIND_VAR));

        // After pushing all parameters and local variables, we can write actual VM code
        if (subroutineType.equals("method")) {
            vmWriter.writePush(VMWriter.Segment.SEG_ARGUMENT, 0);
            vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 0);
        } else if (subroutineType.equals("constructor")) {
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
        while (!isCurrentTokenSymbol(')')) {

            // process comma if we encounter it
            if (isCurrentTokenSymbol(',')) {
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

        while (isCurrentTokenKeyword("var")) {
            // var
            jackTokenizer.keyword();
            advanceTokenIfPossible();

            // variable data type
            String varType = jackTokenizer.identifier();
            advanceTokenIfPossible();

            // get all list of variable names
            List<String> varNames = getVarNames();

            for (String varName : varNames) {
                variables.add(new Variable(varName, varType));
            }
        }

        return variables;
    }

    private void compileStatements() throws IOException {
        // Controller for let, while, if, do or return statement
        while (!isCurrentTokenSymbol('}')) {
            if (isCurrentTokenKeyword("return")) {
                compileReturn();
            } else if (isCurrentTokenKeyword("let")) {
                compileLet();
            } else if (isCurrentTokenKeyword("while")) {
                compileWhile();
            } else if (isCurrentTokenKeyword("if")) {
                compileIf();
            } else if (isCurrentTokenKeyword("do")) {
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
        if (isCurrentTokenSymbol('[')) {
            // [
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Expression
            compileExpression();

            // Push current varName to setup for subsequent calculation
            vmWriter.writePush(vmWriter.kindStringToSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));

            // ]
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // TODO: Add LL(2) support for arrays
            // e.g. let arr[0] = arr[1];

            // Add offset and pop into pointer 1
            vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_ADD);
            vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 1);

            // =
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Expression
            isAssigningToArray = true;
            compileExpression();

            // ;
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Pop to that 0
            vmWriter.writePop(VMWriter.Segment.SEG_THAT, 0);

        } else if (isCurrentTokenSymbol('=')) {
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
            vmWriter.writePop(vmWriter.kindStringToSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
        }
    }

    private int compileExpressionList() throws IOException {
        // e.g. var int x,y;

        int numExpressions = 0;
        numExpressions += compileExpression();

        while (isCurrentTokenSymbol(',')) {
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
        vmWriter.writeLabel("WHILE_EXP" + counter);

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

        vmWriter.writeGoto("WHILE_EXP" + counter);
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
        if (isCurrentTokenSymbol(';')) {
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
        if (isCurrentTokenKeyword("else")) {
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

        while (!isCurrentTokenSymbol(';')) {
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

    // We return 1 if we compiled, otherwise 0
    // The return exists to facilitate compileExpressionList
    private int compileExpression() throws IOException {
        // e.g. num1 + num2

        if (jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_INT_CONST
                || jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_STRING_CONST
                || isCurrentTokenSymbol('(') // e.g. (num1 + num2)
                || isCurrentTokenSymbol('-') // negative (unary)
                || isCurrentTokenSymbol('~') // not (unary)
                || isCurrentTokenKeyword("true") // true (keywordConstant)
                || isCurrentTokenKeyword("false") // false (keywordConstant)
                || isCurrentTokenKeyword("null") // null (keywordConstant)
                || isCurrentTokenKeyword("this") // this (keywordConstant)
                || jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_IDENTIFIER) {

            // Compile the current term
            compileTerm();

            while (isCurrentTokenSymbol('+')
                    || isCurrentTokenSymbol('-')
                    || isCurrentTokenSymbol('*')
                    || isCurrentTokenSymbol('/')
                    || isCurrentTokenSymbol('&')
                    || isCurrentTokenSymbol('|')
                    || isCurrentTokenSymbol('<')
                    || isCurrentTokenSymbol('>')
                    || isCurrentTokenSymbol('=')) {
                char operator = jackTokenizer.symbol();
                advanceTokenIfPossible();

                // Compile the next term
                compileTerm();

                // Write VM code based on operator
                switch (operator) {
                    case '+' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_ADD);
                    case '-' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_SUB);
                    case '&' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_AND);
                    case '|' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_OR);
                    case '<' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_LT);
                    case '>' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_GT);
                    case '=' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_EQ);

                    case '*' -> vmWriter.writeCall("Math.multiply", 2);
                    case '/' -> vmWriter.writeCall("Math.divide", 2);
                }
            }

            // We successfully compiled one expression
            return 1;
        }

        // We didn't compile anything
        return 0;
    }

    private void compileTerm() throws IOException {
        if (jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_INT_CONST) {
            int intValue = jackTokenizer.intVal();
            advanceTokenIfPossible();

            vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, intValue);
            return;
        }

        if (jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_IDENTIFIER) {
            String initialIdentifier = jackTokenizer.identifier();
            advanceTokenIfPossible();

            if (isCurrentTokenSymbol('[')) {
                System.out.println(isAssigningToArray);
                System.out.println(initialIdentifier);
                // [
                jackTokenizer.symbol();
                advanceTokenIfPossible();

                // Compile rest of expression in []
                compileExpression();

                // Push variable to top of stack
                vmWriter.writePush(vmWriter.kindStringToSegment(symbolTable.kindOf(initialIdentifier)), symbolTable.indexOf(initialIdentifier));

                // ]
                jackTokenizer.symbol();
                advanceTokenIfPossible();

                // Add them together
                vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_ADD);

                // Calibrate that segment
                vmWriter.writePop(VMWriter.Segment.SEG_POINTER, 1);
                vmWriter.writePush(VMWriter.Segment.SEG_THAT, 0);

                return;
            }

            // A method is called on current object
            if (isCurrentTokenSymbol('(')) {
                // Push "this" onto stack
                vmWriter.writePush(VMWriter.Segment.SEG_POINTER, 0);                                             // class object

                // (
                jackTokenizer.symbol();
                advanceTokenIfPossible();

                // Get number of arguments
                int nArgs = compileExpressionList();

                // )
                jackTokenizer.symbol();
                advanceTokenIfPossible();

                // We add one because we pushed "this" onto the stack, with the first statement
                // e.g. call Square.calculateArea 2
                vmWriter.writeCall(className + "." + initialIdentifier, nArgs + 1);

                return;
            }

            if (isCurrentTokenSymbol('.')) {
                // .
                jackTokenizer.symbol();
                advanceTokenIfPossible();

                String subsequentIdentifier = jackTokenizer.identifier();
                advanceTokenIfPossible();

                // If we can't find the initial identifier in the symbol table, it must belong to another class
                if (symbolTable.indexOf(initialIdentifier) < 0) {
                    // (
                    jackTokenizer.symbol();
                    advanceTokenIfPossible();

                    // Get number of arguments
                    int nArgs = compileExpressionList();

                    // )
                    jackTokenizer.symbol();
                    advanceTokenIfPossible();

                    // Call subroutine from another class
                    vmWriter.writeCall(initialIdentifier + "." + subsequentIdentifier, nArgs);
                } else {
                    vmWriter.writePush(vmWriter.kindStringToSegment(symbolTable.kindOf(initialIdentifier)), symbolTable.indexOf(initialIdentifier));

                    // (
                    jackTokenizer.symbol();
                    advanceTokenIfPossible();

                    // Get number of arguments
                    int nArgs = compileExpressionList();

                    // )
                    jackTokenizer.symbol();
                    advanceTokenIfPossible();

                    // e.g. call this.eat 3
                    vmWriter.writeCall(symbolTable.typeOf(initialIdentifier) + "." + subsequentIdentifier, nArgs + 1);
                }

                return;
            }

            // If it's not any of the above, just add the identifier to symbol table
            vmWriter.writePush(vmWriter.kindStringToSegment(symbolTable.kindOf(initialIdentifier)), symbolTable.indexOf(initialIdentifier));
        }

        if (jackTokenizer.tokenType() == JackTokenizer.TokenType.TOKEN_STRING_CONST) {
            String stringValue = jackTokenizer.stringVal();
            advanceTokenIfPossible();

            int stringLength = stringValue.length();

            // VM code to create a string by calling appendCar n times
            vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, stringLength);
            vmWriter.writeCall("String.new", 1);
            for (int c = 0; c < stringLength; c++) {
                vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, stringValue.charAt(c));
                vmWriter.writeCall("String.appendChar", 2);
            }

            return;
        }

        // Compile unary
        if (isCurrentTokenSymbol('-') || isCurrentTokenSymbol('~')) {
            // e.g. ~isSafeToEat

            char operator = jackTokenizer.symbol();
            advanceTokenIfPossible();

            // Compile the following term
            compileTerm();

            // Write VM Code
            switch (operator) {
                case '-' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NEG);
                case '~' -> vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NOT);
            }

            return;
        }

        if (isCurrentTokenSymbol('(')) {
            // (
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            // compile rest of expression
            compileExpression();

            // )
            jackTokenizer.symbol();
            advanceTokenIfPossible();

            return;
        }

        // Compile keywordConstant
        if (isCurrentTokenKeyword("true") || isCurrentTokenKeyword("false") || isCurrentTokenKeyword("null") || isCurrentTokenKeyword("this")) {
            String keyword = jackTokenizer.keyword();
            advanceTokenIfPossible();

            switch (keyword) {
                case "this" -> vmWriter.writePush(VMWriter.Segment.SEG_POINTER, 0);
                case "null", "false" -> vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, 0);
                case "true" -> {
                    // When we not 0, it becomes -1 (true)
                    vmWriter.writePush(VMWriter.Segment.SEG_CONSTANT, 0);
                    vmWriter.writeArithmetic(VMWriter.ArithmeticCmd.ARITHMETIC_NOT);
                }
            }

            return;
        }
    }

    public void close() throws IOException {
        vmWriter.close();
    }
}
