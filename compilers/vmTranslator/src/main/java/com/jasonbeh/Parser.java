package main.java.com.jasonbeh;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {
    private Scanner reader;
    private String[] currentCommand;
    private String[] nextCommand;

    public Map<String, Command> commandMap;

    public enum Command {
        C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
    }

    private void initializeCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put("add", Command.C_ARITHMETIC);
        commandMap.put("neg", Command.C_ARITHMETIC);
        commandMap.put("sub", Command.C_ARITHMETIC);
        commandMap.put("gt", Command.C_ARITHMETIC);
        commandMap.put("lt", Command.C_ARITHMETIC);
        commandMap.put("eq", Command.C_ARITHMETIC);
        commandMap.put("and", Command.C_ARITHMETIC);
        commandMap.put("or", Command.C_ARITHMETIC);
        commandMap.put("not", Command.C_ARITHMETIC);

        commandMap.put("function", Command.C_FUNCTION);
        commandMap.put("push", Command.C_PUSH);
        commandMap.put("pop", Command.C_POP);
        commandMap.put("label", Command.C_LABEL);
        commandMap.put("goto", Command.C_GOTO);
        commandMap.put("if-goto", Command.C_IF);
        commandMap.put("call", Command.C_CALL);
        commandMap.put("return", Command.C_RETURN);
    }

    Parser(String filename) {
        initializeCommandMap();
        currentCommand = new String[]{};
        nextCommand = new String[]{};

        try {
            reader = new Scanner(new FileReader(filename));
            advance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Command commandType() {
        return commandMap.get(currentCommand[0]);
    }

    public String arg1() {
        if(commandType() == Command.C_ARITHMETIC) {
            return currentCommand[0];
        }

        return currentCommand[1];
    }

    public int arg2() {
        return Integer.parseInt(currentCommand[2]);
    }

    public boolean hasMoreLines() {
        return reader.hasNextLine() || nextCommand.length != 0;
    }

    public void advance() {
        currentCommand = nextCommand;

        for(int i = 0; i < currentCommand.length; i++) {
            currentCommand[i] = currentCommand[i].trim();
        }

        nextCommand = new String[]{};

        // Find the next valid line
        String tempNextCommand;
        do {
            if (!hasMoreLines()) {
                return;
            }

            tempNextCommand = reader.nextLine();

            if (tempNextCommand == null) {
                return;
            }
        } while ((isEmptyLine(tempNextCommand) || isComment(tempNextCommand)) && hasMoreLines());

        // Trim inline comment if exist
        int commentIdx = tempNextCommand.indexOf("//");
        if (commentIdx != -1) {
            tempNextCommand = tempNextCommand.substring(0, commentIdx - 1);
        }

        // Process it into an array
        tempNextCommand = tempNextCommand.trim();
        nextCommand = tempNextCommand.split("\\s");
    }

    private boolean isEmptyLine(String line) {
        return line.trim().isEmpty();
    }

    private boolean isComment(String line) {
        return line.trim().startsWith("//");
    }

    public void close() {
        reader.close();
    }
}
