package main.java.com.jasonbeh;

public class Symbol {
    private final int index;
    private final SymbolTable.Kind kind;
    private final String type;

    public Symbol(SymbolTable.Kind kind, String type, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public SymbolTable.Kind getKind() {
        return kind;
    }
}
