package Parser;

import java.util.*;

class TreeNode {
    Token data;
    ArrayList<TreeNode> children;
    TreeNode parent;              

    public TreeNode(Token data) {
        this.data = data != null ? new Token(data.t, data.value) : null; 
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode node) {  
        if (node == null) return;      
        this.children.add(node);
        node.parent = this;
    }

    public void removeChild(TreeNode node) {
        if (node == null) return;     
        this.children.remove(node);
        node.parent = null;
    }
}

class parse_statement{
    TreeNode root = new TreeNode(new Token(Token.Type.STATEMENT,""));
    public int parsefull(List<Token> tokens,int start,TreeNode root){
        int index = start;
        if (root == null || tokens == null || tokens.isEmpty()) return index;
        while(index<tokens.size()){
        if (tokens.get(index) == null) { 
                index++;
                continue;
            }
        TreeNode statement1 = new TreeNode(tokens.get(index));
        //System.out.println(statement1.data.value);
        root.addChild(statement1);
        switch(tokens.get(index).t){
            case FUNCTIONAL:
            index = parsefull(tokens,index+1,statement1);
            break;
            case BODY:
           int j = index + 1;
            while (j < tokens.size() && tokens.get(j).t != Token.Type.END) {
                Token current = tokens.get(j);
                if (current.t == Token.Type.RETURN) {
                    TreeNode returnNode = new TreeNode(current); 
                    j++;

                    ArrayList<Token> exprTokens = new ArrayList<>();

                    while (j < tokens.size() && tokens.get(j).t != Token.Type.END) {
                        exprTokens.add(tokens.get(j));
                        j++;
                    }

                    TreeNode exprParent = new TreeNode(new Token(Token.Type.EXPRESSION, "expression"));

                    parse_expr_statement parserexp = new parse_expr_statement(exprTokens);
                    TreeNode exprTree = parserexp.parsexpr();

                    if (exprTree != null) exprParent.addChild(exprTree);
                    returnNode.addChild(exprParent); 
                    statement1.addChild(returnNode);
                    break;
                }

                j++;
            }

            index = j;
            break;
            case END:
            return index + 1;
            default:
            index++;
                break;
        }
    }
    return index;
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
            TreeNode right = parseterm();
            if (right != null) b.addChild(right);
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
            TreeNode right = parsefactor();
            if (right != null) b.addChild(right);
            left = b;
        }
        return left;
    }
    public TreeNode parsefactor(){
         if (peek() == null) return null;
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
    int pos  = 0;
    HashMap<String, valuetype.Type> operatorMapper = new HashMap<>(Map.of(
    "+", valuetype.Type.INT,
    "-", valuetype.Type.INT,
    "*", valuetype.Type.INT,
    "/", valuetype.Type.INT
));
    HashMap<String,valuetype.Type2> comparatorMapper = new HashMap<>(Map.of(">",valuetype.Type2.GREATER,"<",valuetype.Type2.LESSER,"==",valuetype.Type2.EQUAL));
    HashMap<String, valuetype.Type> ae = new HashMap<>(Map.of("int", valuetype.Type.INT , "bool", valuetype.Type.BOOLEAN));
    HashMap<String,valuetype.Type> ae1 = new HashMap<>(Map.of("int", valuetype.Type.INT, "bool", valuetype.Type.BOOLEAN));
    HashMap<String,String> op = new HashMap<>(Map.of("*","*","/","/")); 
    HashMap<functiontype,HashMap<String,valuetype>> symbolTableorg = new HashMap<>();
    HashMap<String,valuetype> operatorTable = new HashMap<>();
    String value1 = new String();
    public void functionalvisitor(ArrayList<TreeNode> root,HashMap<String,valuetype> symbolTable,HashMap<String,valuetype> operatorTable){
        functiontype ft1;
        String s = null;
        for(TreeNode next : root){
            Token value = next.data;
            //System.out.println(value.value);
            if(value.t.equals(Token.Type.FUNCTIONAL)){
                HashMap<String,valuetype> vt1 = new HashMap<>();
                TreeNode value1 = null;
                //System.out.println(next.children.get(0).data.value);
                valuetype v5 = new valuetype(next.children.get(0).data.value, valuetype.Type.INT);
                String funcName = next.children.get(0).data.value;
                String returnType = next.children.get(1).data.value;
                //System.out.println(returnType);
                ft1 = new functiontype(funcName, ae1.get(returnType));
                vt1.put(funcName,v5);
                for(int i = 1 ; i < next.children.size() ;i = i+2){
                    value1 = next.children.get(i);
                    TreeNode value2 = null;
                    //System.out.println(value1.data.value);
                    if(value1.data.t == Token.Type.BODY){
                        break;
                    }
                    if(i+1 < next.children.size()){
                    value2 = next.children.get(i+1);
                    //System.out.println(value2.data.value);
                    }
                    if(value1.data.t == Token.Type.ASSIGNMENTKEYWORDS && (value2 != null && value2.data.t == Token.Type.IDENTIFIER)){
                        valuetype.Type vtId = ae.get(value1.data.value);
                        //System.out.println(value2.data.value);
                        vt1.put(value2.data.value,new valuetype(value2.data.value,vtId));
                    }
                    else{
                        throw new RuntimeException("Not Possible");
                    }
               }
               
                if(value1.data.t.equals(Token.Type.BODY)){
                    this.functionalvisitor(value1.children, vt1, operatorTable);
                }
                /*for(valuetype v : vt1.values()){
                    System.out.println(v.t);
                }*/
                System.out.println(ft1.value);
                symbolTableorg.put(ft1,vt1);
            }
            else if(value.t.equals(Token.Type.RETURN)){
                functionalvisitor(next.children, symbolTable, operatorTable);
            }
            else if(value.t.equals(Token.Type.EXPRESSION)){
                valuetype vtID = new valuetype(value.value,valuetype.Type.INT);
                symbolTable.put(value.value,vtID);
                functionalvisitor(next.children, symbolTable, operatorTable);
            }
            /*else if(value.t.equals(Token.Type.ASSIGNMENTKEYWORDS)){
                valuetype.Type vtId = ae.get(value.value);

            }*/
            else if (value.t.equals(Token.Type.NUMBER)) {
                symbolTable.put(value.value,new valuetype(value.value,valuetype.Type.INT));
            }
                
            else if(value.t.equals(Token.Type.IDENTIFIER)){
                //System.out.println(value.value);
                symbolTable.put(value.value, new valuetype(value.value, valuetype.Type.INT));
                
            }
            else if(value.t.equals(Token.Type.OPERATOR)){
                if(!symbolTable.containsKey(value.value)){
                    symbolTable.put(value.value, new valuetype(value.value, operatorMapper.get(value.value)));
                }
                this.functionalvisitor(next.children, symbolTable, operatorTable);
            }
            else if(value.t.equals(Token.Type.COMPARATOR)){
                valuetype v = new valuetype(value.value, comparatorMapper.get(value.value));
                operatorTable.put(value.value,v);
                this.functionalvisitor(next.children, symbolTable,operatorTable);
            }
        }
    }
    public String returntypefinder(String s,TreeNode root) throws IllegalArgumentException{
        if (root == null || root.children == null || root.children.size() < 2)
            return "int";
        if(root.children.get(0).data.t == Token.Type.HIGHEROPERATOR){
            return root.children.get(0).data.value;
        }
        else if(root.children.get(1).data.t == Token.Type.HIGHEROPERATOR){
            return root.children.get(0).data.value;
        }
        else{
            return "int";
        }

    }
}

