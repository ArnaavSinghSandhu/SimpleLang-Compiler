package Parser;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private final int length;

    public Lexer(String input) {
        this.input = input;
        this.length = input.length();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < length) {
            char c = peek();

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                advance();
                continue;
            }

            // Identifiers or keywords
            if (Character.isLetter(c) || c == '_') {
                String word = readWord();
                switch (word) {
                    case "func":
                        tokens.add(new Token(Token.Type.FUNCTIONAL, word));
                        break;
                    case "return":
                        tokens.add(new Token(Token.Type.RETURN, word));
                        break;
                    case "body":
                        tokens.add(new Token(Token.Type.BODY, word));
                        break;
                    case "int":
                    case "bool":
                        tokens.add(new Token(Token.Type.ASSIGNMENTKEYWORDS, word));
                        break;
                    case "END":
                        tokens.add(new Token(Token.Type.END, word));
                        break;
                    default:
                        tokens.add(new Token(Token.Type.IDENTIFIER, word));
                        break;
                }
                continue;
            }

            // Numbers
            if (Character.isDigit(c)) {
                String num = readNumber();
                tokens.add(new Token(Token.Type.NUMBER, num));
                continue;
            }

            // Operators
            if ("+-*/".indexOf(c) != -1) {
                tokens.add(new Token(Token.Type.OPERATOR, String.valueOf(c)));
                advance();
                continue;
            }

            // Parentheses
            if (c == '(' || c == ')') {
                tokens.add(new Token(Token.Type.PAREN, String.valueOf(c)));
                advance();
                continue;
            }

            // Unknown characters
            throw new RuntimeException("Unknown character: " + c);
        }

        // Mark end
        tokens.add(new Token(Token.Type.END, "END"));
        return tokens;
    }

    private char peek() {
        return input.charAt(pos);
    }

    private void advance() {
        pos++;
    }

    private String readWord() {
        int start = pos;
        while (pos < length && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_'))
            pos++;
        return input.substring(start, pos);
    }

    private String readNumber() {
        int start = pos;
        while (pos < length && Character.isDigit(input.charAt(pos)))
            pos++;
        return input.substring(start, pos);
    }
}
