package Parser;

import java.beans.Expression;
import java.util.*;

class TreeNode {
    Token data;
    ArrayList<TreeNode> children;
    TreeNode parent;              

    public TreeNode(Token data) {
        this.data = new Token(data.t, data.value); 
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode node) {        
        this.children.add(node);
        node.parent = this;
    }

    public void removeChild(TreeNode node) {     
        this.children.remove(node);
        node.parent = null;
    }
}

class parse_statement{
    TreeNode root = new TreeNode(new Token(Token.Type.STATEMENT,""));
    public void parsefull(List<Token> tokens,int start,TreeNode root){
        while(start<tokens.size()){
        TreeNode statement1 = new TreeNode(tokens.get(start));
        root.addChild(statement1);
        switch(tokens.get(start).t){
            case FUNCTIONAL:
            parsefull(tokens,start,statement1);
            break;
            case BODY:
            parsefull(tokens,start,statement1);
            break;
            case EXPRESSION:
            ArrayList<Token> arr = new ArrayList<>();
            int j = start;
            while(j < tokens.size() && !tokens.get(j).t.equals(Token.Type.END))j++;
            for(int i = start;i < j;i++){
                arr.add(tokens.get(i));
            }
            parse_expr_statement parserexp = new parse_expr_statement(arr);
            statement1.addChild(parserexp.parsexpr());
            start= j;
            break;
            case END:
            return;
        }
        start += 1;
    }
    }
}
class parse_expr_statement{
    ArrayList<Token> arr1;
    int pos = 0;
    public parse_expr_statement(ArrayList<Token> arr){
        this.arr1 = new ArrayList<>(arr);
    }
    public Token peek(){
        return pos < arr1.size() ? arr1.get(pos) : null;
    }
    public Token consume(){
        return pos < arr1.size() ? arr1.get(pos++) : null;
    }
    public TreeNode parsexpr(){
        TreeNode left = parseterm();
        while(peek() != null && (peek().value.equals("+") || peek().value.equals("-"))){
            Token a = consume();
            TreeNode b = new TreeNode(a);
            b.addChild(left);
            b.addChild(parseterm());
            left = b;
        } 
        return left;
    }
    public TreeNode parseterm(){
        TreeNode left = parsefactor();
        while(peek() != null && (peek().value.equals("*") || peek().value.equals("/"))){
            Token a = consume();
            TreeNode b = new TreeNode(a);
            b.addChild(left);
            b.addChild(parsefactor());
            left = b;
        }
        return left;
    }
    public TreeNode parsefactor(){
        if (peek() != null && (peek().t == Token.Type.IDENTIFIER || peek().t == Token.Type.NUMBER))
{
            Token a = consume();
            TreeNode b = new TreeNode(a);
            return b;
        }
        else if(peek() != null && peek().value.equals("(")){
            consume();
            TreeNode a = parsexpr();
            if(peek() != null && peek().value.equals(")")){
                consume();
            }
            else{
                throw new RuntimeException("Expected closing parenthesis");
            }
            
            return a;
        }
        return null;
    }
}
class executor{
    Scanner sc = new Scanner(System.in);
    valuetype vt = new valuetype();
    functiontype ft = new functiontype();
    HashMap<String, valuetype.Type1> operatorMapper = Map.of(
    "+", valuetype.Type1.ADDITION,
    "-", valuetype.Type1.SUBTRACTION,
    "*", valuetype.Type1.MULITIPLICATION,
    "/", valuetype.Type1.DIVISION
);
    HashMap<String,valuetype.Type2> comparatorMapper = Map.of(">",valuetype.Type2.GREATER,"<",valuetype.Type2.LESSER,"==",valuetype.Type2.EQUAL);
    HashMap<String, valuetype.Type> ae = Map.of("int", valuetype.Type.INT , "bool", valuetype.Type.BOOLEAN);
    HashMap<String,functiontype.Type> ae1 = new HashMap<>(Map.of("int", functiontype.Type.INT, "bool", functiontype.Type.BOOLEAN));
    HashMap<String,String> op = Map.of("*","*","/","/"); 
    static HashMap<functiontype,HashMap<String,valuetype>> symbolTableorg = new HashMap<>();
    HashMap<String,valuetype> operatorTable;
    String value1;
    public void functionalvisitor(ArrayList<TreeNode> root,HashMap<String,valuetype> symbolTable,HashMap<String,valuetype> operatorTable){
        functiontype ft1;
        String s;
        for(TreeNode next : root){
            
            Token value = next.data;
            if(value.t.equals(Token.Type.FUNCTIONAL)){
                HashMap<String,valuetype> vt1 = new HashMap<>();
                this.functionalvisitor(next.children, vt1,operatorTable);
                if (value1 != null && ae1.containsKey(value1)) {
                ft1 = new functiontype(value1, ae1.get(value1));
                executor.symbolTableorg.put(ft1, vt1);
            }
            }
            else if(value.t.equals(Token.Type.EXPRESSION)){
                valuetype vtID = new valuetype(value.value,ae.get(returntypefinder(s, next)));
                symbolTable.put(value.value,vtID);
                functionalvisitor(next, symbolTable, operatorTable);
            }
            else if(value.t.equals(Token.Type.ASSIGNMENTKEYWORDS)){
                valuetype vtId = new valuetype(value.value,ae.get(value.value));
                symbolTable.put(value.value, vtId);
                s = value.value;
            }
            else if (value.t.equals(Token.Type.NUMBER)) {
                symbolTable.put(value.value,new valuetype(value.value,valuetype.Type.INT));
            }
                
            else if(value.t.equals(Token.Type.IDENTIFIER)){
                valuetype vtId = new valuetype();
                vtId.value = s;
                vtId.t = ae.get(s);
                symbolTable.put(value.value, vtId);
            }
            else if(value.t.equals(Token.Type.HIGHEROPERATOR)){
                valuetype v = new valuetype(value.value, operatorMapper.get(value.value));
                operatorTable.put(value.value, v);
                this.functionalvisitor(next.children, symbolTable,operatorTable);
            }
            else if(value.t.equals(Token.Type.COMPARATOR)){
                valuetype v = new valuetype(value.value, comparatorMapper.get(value.value));
                operatorTable.put(value.value,v);
                this.functionalvisitor(next.children, symbolTable,operatorTable);
            }
        }
    }
    public String returntypefinder(String s,TreeNode root) throws IllegalArgumentException{
        if(root.children.get(0).data.t == Token.Type.HIGHEROPERATOR){
            return root.children.get(0).data.value;
        }
        else if(root.children.get(1).data.t == Token.Type.HIGHEROPERATOR){
            return root.children.get(0).data.value;
        }
        else{
            throw new IllegalArgumentException("Not a valid expression");
        }

    }
}

