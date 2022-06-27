package main.java.com.jasonbeh;

import java.io.File;
import java.io.IOException;

public class VMTranslator {
    public void translate(File sourceFile, File outputFile) throws IOException {
        CodeWriter codeWriter = new CodeWriter(outputFile);
        Parser parser = new Parser(sourceFile.getAbsolutePath());

        codeWriter.setFileName(outputFile.getName());

        while(parser.hasMoreLines()) {
            parser.advance();
            switch(parser.commandType()) {
                case C_POP, C_PUSH -> codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                case C_ARITHMETIC -> codeWriter.writeArithmetic(parser.arg1());
            }
        }

        parser.close();
        codeWriter.close();
    }
}
