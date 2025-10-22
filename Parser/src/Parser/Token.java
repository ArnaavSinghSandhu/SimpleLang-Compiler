package Parser;

class Token {
    enum Type {
        FUNCTIONAL,
        BODY,
        STATEMENT,
        IDENTIFIER,
        NUMBER,
        OPERATOR,
        ASSIGNMENT,
        COMPARATOR,
        ASSIGNMENTKEYWORDS,
        EXPRESSION,
        RETURN,
        END,
        PAREN,
        COMMA,
        HIGHEROPERATOR 
    }

    Type t;
    String value;

    public Token(Type t, String value) {
        this.t = t;
        this.value = value;
    }

    public boolean isIdentifier() {
        return t == Type.IDENTIFIER;
    }

    public boolean isNumber() {
        return t == Type.NUMBER;
    }

    @Override
    public String toString() {
        return "Token{" + "t=" + t + ", value='" + value + '\'' + '}';
    }
}