package Parser;

import java.util.*;

import Parser.TreePrinter;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringBuilder codeInput = new StringBuilder();

        
        String line;
        while(!(line = sc.nextLine()).equals("END_INPUT")) {
            codeInput.append(line).append("\n");
        }

        Lexer lexer = new Lexer(codeInput.toString());
        List<Token> tokens;
        try {
            tokens = lexer.tokenize();
        } catch (RuntimeException e) {
            System.err.println("Lexer error: " + e.getMessage());
            return;
        }

        if (tokens.isEmpty()) {
            System.out.println("No tokens found.");
            return;
        }
        

        TreeNode root = new TreeNode(new Token(Token.Type.STATEMENT, ""));
        parse_statement parser = new parse_statement();
        try {
            parser.parsefull(tokens, 0, root);
            TreePrinter.printTree(root);
        } catch (RuntimeException e) {
            System.err.println("Parser error: " + e.getMessage());
            return;
        }

        executor exec = new executor();
        functiontype mainFunc = new functiontype("", valuetype.Type.DEFAULT);

        symboltable symTable = new symboltable(mainFunc);

        try {
            symTable.symbolTable1(exec, root);
        } catch (IllegalArgumentException e) {
            System.err.println("Semantic error: " + e.getMessage());
            return;
        }

        generator gen = new generator();
        StringBuffer finalCode;
        
        try {
            functiontype f2 = new functiontype("", valuetype.Type.INT);
            for(functiontype f: exec.symbolTableorg.keySet()){
                System.out.println(f.t);
                f2 = f;
            }
            finalCode = gen.generatecodestatement(root, exec.symbolTableorg,f2);
        } catch (RuntimeException e) {
            System.err.println("Code generation error: " + e.getMessage());
            return;
        }
        
        GeneratorToFile.writeGeneratedCodeToFile(gen, "generated_code.asm");

        System.out.println("\n===== Generated Code =====\n");
        System.out.println(finalCode.toString());
    }

    public static functiontype getFunctionTypeByName(Set<functiontype> functions, String name) {
    if (functions == null || name == null) return null;
    for (functiontype f : functions) {
        if (f.value.equals(name)) {
            return f;
        }
    }
    return null; // Not found
}
}
