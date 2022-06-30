package com.jackcompiler.jasonbeh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class JackTokenizer {
    private Set<String> symbolSet;
    private Set<String> keywordSet;
    private Scanner reader;
    private List<String> tokens;
    private int currentTokenIdx;

    public JackTokenizer(File file) {
        try {
            initializeSymbolSet();
            initializeKeywordSet();
            reader = new Scanner(new FileReader(file));
            tokens = new ArrayList<>();
            currentTokenIdx = 0;

            tokenize();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public enum TokenType {
        TOKEN_KEYWORD,
        TOKEN_SYMBOL,
        TOKEN_IDENTIFIER,
        TOKEN_INT_CONST,
        TOKEN_STRING_CONST
    }

    private void initializeSymbolSet() {
        symbolSet = new HashSet<>();
        symbolSet.add("{");
        symbolSet.add("}");
        symbolSet.add("(");
        symbolSet.add(")");
        symbolSet.add("[");
        symbolSet.add("]");
        symbolSet.add(".");
        symbolSet.add(",");
        symbolSet.add(";");
        symbolSet.add("+");
        symbolSet.add("-");
        symbolSet.add("*");
        symbolSet.add("/");
        symbolSet.add("&");
        symbolSet.add("|");
        symbolSet.add("<");
        symbolSet.add(">");
        symbolSet.add("=");
        symbolSet.add("~");
    }

    private void initializeKeywordSet() {
        keywordSet = new HashSet<>();
        keywordSet.add("class");
        keywordSet.add("method");
        keywordSet.add("function");
        keywordSet.add("constructor");
        keywordSet.add("int");
        keywordSet.add("boolean");
        keywordSet.add("char");
        keywordSet.add("void");
        keywordSet.add("var");
        keywordSet.add("static");
        keywordSet.add("field");
        keywordSet.add("let");
        keywordSet.add("do");
        keywordSet.add("if");
        keywordSet.add("else");
        keywordSet.add("while");
        keywordSet.add("return");
        keywordSet.add("true");
        keywordSet.add("false");
        keywordSet.add("null");
        keywordSet.add("this");
    }

    public TokenType tokenType() {
        String token = tokens.get(currentTokenIdx);
        try {
            Integer.parseInt(token);
            return TokenType.TOKEN_INT_CONST;
        } catch (NumberFormatException ignored) {
        }

        if (token.length() > 1) {
            if (keywordSet.contains(token)) {
                return TokenType.TOKEN_KEYWORD;
            }

            if (token.startsWith("\"") && token.endsWith("\"")) {
                return TokenType.TOKEN_STRING_CONST;
            }
        } else {
            if (symbolSet.contains(token)) {
                return TokenType.TOKEN_SYMBOL;
            }
        }

        // If all else fail, it is an identifier
        return TokenType.TOKEN_IDENTIFIER;
    }

    public void tokenize() {
        tokens = new ArrayList<>();
        while (reader.hasNextLine()) {
            String nextLine = reader.nextLine();

            // Move to next line if it's empty or a comment
            if (isEmptyLine(nextLine) || isComment(nextLine)) {
                continue;
            }

            // Trim inline comment if exist
            int commentIdx = nextLine.indexOf("//");
            if (commentIdx != -1) {
                nextLine = nextLine.substring(0, commentIdx - 1);
            }

            // TODO: Add support for multiline comment

            // Begin parsing
            parse(nextLine);
        }
    }

    public void parse(String line) {
        if (line.length() == 0) {
            return;
        }

        String[] lineTokens = line.split("\\s");

        // Contains symbol: parse before symbol, parse symbol and parse after symbol
        if (lineTokens.length == 1) {
            int symbolIndex = -1;

            // Get the earliest symbol index possible
            for (String symbol : symbolSet) {
                if (line.contains(symbol)) {
                    if (symbolIndex == -1) {
                        symbolIndex = line.indexOf(symbol);
                    } else {
                        symbolIndex = Math.min(symbolIndex, line.indexOf(symbol));
                    }
                }
            }

            // If there is no symbol, add the whole line as token
            if (symbolIndex == -1) {
                tokens.add(line);
                return;
            }

            // Parse before
            if (symbolIndex > 0) {
                parse(line.substring(0, symbolIndex));
            }

            // Add symbol
            tokens.add(line.substring(symbolIndex, symbolIndex + 1));

            // Parse after
            parse(line.substring(symbolIndex + 1));

            return;
        }

        // Contains string: parse before string, parse string and parse after string
        if (line.indexOf('"') >= 0) {
            int openingQuoteIdx = line.indexOf('"');
            int closingQuoteIdx = line.indexOf('"', openingQuoteIdx + 1);

            // Before opening quote
            parse(line.substring(0, openingQuoteIdx).trim());

            // String
            tokens.add(line.substring(openingQuoteIdx, closingQuoteIdx + 1));

            // After opening quote
            parse(line.substring(closingQuoteIdx + 1).trim());

            return;
        }

        // Process each token in the string
        if (line.indexOf('"') < 0) {
            for (String lineToken : lineTokens) {
                parse(lineToken);
            }
            return;
        }

        // If all else fails, add the current line as token
        tokens.add(line);
    }

    private boolean isEmptyLine(String line) {
        return line.trim().isEmpty();
    }

    private boolean isComment(String line) {
        return line.trim().startsWith("//");
    }

    public boolean hasMoreTokens() {
        return tokens.size() - 1 > currentTokenIdx;
    }

    public void advance() {
        currentTokenIdx += 1;
    }

    public int intVal() {
        return Integer.parseInt(tokens.get(currentTokenIdx));
    }

    public String stringVal() {
        // Omit the quotes
        return tokens.get(currentTokenIdx).substring(1, tokens.get(currentTokenIdx).length() - 1);
    }

    public char symbol() {
        // Each symbol is one character
        return tokens.get(currentTokenIdx).charAt(0);
    }

    public String keyword() {
        return tokens.get(currentTokenIdx);
    }

    public String identifier() {
        return tokens.get(currentTokenIdx);
    }


}
