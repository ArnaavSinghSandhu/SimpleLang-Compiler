package Parser;

import java.util.*;

/*
 * x86 assembly conversion
 */

class generator{
    memorymanagement manager = new memorymanagement();
    int regCounter = 3;
    public String newReg() {
        return "R" + (regCounter++);
    }
    HashMap<String,String> returner = new HashMap<>();
    HashMap<functiontype,StringBuffer> map = new HashMap<>();
    HashMap<functiontype,HashMap<String,String>> symbolTable = new HashMap<>();
    HashMap<String, valuetype.Type1> operatorMapper = new HashMap<>(Map.of(
    "+", valuetype.Type1.ADDITION,
    "-", valuetype.Type1.SUBTRACTION,
    "*", valuetype.Type1.MULITIPLICATION,
    "/", valuetype.Type1.DIVISION
));
    public StringBuffer generatecodestatement(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
       StringBuffer buf = map.computeIfAbsent(f, k -> new StringBuffer());
        if(root == null){
            return buf;
        }
        for(TreeNode n : root.children){
            if(n == null || n.data == null)continue;
            switch(n.data.t){
                case Token.Type.FUNCTIONAL:
                if (n.data.value == null || n.data.value.isEmpty()) {
                    break;
                }
                
                functiontype fn = existencechecker(n.children.get(0).data.value, vt.keySet());
                if(fn == null){
                    fn = new functiontype(n.children.get(0).data.value, valuetype.Type.DEFAULT);
                    vt.put(fn, new HashMap<>()); // initialize
                }
                symbolTable.computeIfAbsent(fn, k -> new HashMap<>());
                buf.append(generatecodefunctional(n, vt, fn));
                
                //manager.push(s,generatecodefunctional(n, s.symboltableorg, fn));
                break;
                case Token.Type.ASSIGNMENT:
                buf.append(generatecodeassignment(n,symbolTable.computeIfAbsent(f, k -> new HashMap<>()) ,vt,f));
                break;
                case Token.Type.RETURN:
                buf.append(generatecodereturn(n,vt,f));
                break;
                default:
                if(n.data.t == Token.Type.EXPRESSION){
                    String s5 = generatecodeexpression(n, vt, f,buf);
                    returner.put(f.value, s5);
                } else if(n.data.t == Token.Type.BODY){
                    buf.append(generatecodebody(n, vt, f));
                }
                break;
            }
        }
        return buf;
    }
    public StringBuffer generatecodereturn(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        StringBuffer buf = map.computeIfAbsent(f, k->new StringBuffer());
        if(root == null)return new StringBuffer();
        
        for(TreeNode c: root.children){
            if(c == null || c.data == null)continue;
            if(c.data.t == Token.Type.FUNCTIONAL){
                int i = 0;
                ArrayList<String> args = new ArrayList<>();
                functiontype f3 = new functiontype("",valuetype.Type.DEFAULT);
                for(TreeNode cc: c.children){
                    if(cc == null || cc.data == null)return buf;
                    if(cc.data.t == Token.Type.IDENTIFIER){
                        for(functiontype f4 : vt.keySet()){
                            if(cc.data.value.equals(f4.value)){
                                f3 = f4;
                                break;
                            }
                        }
                    }else if(cc.data.t.equals(Token.Type.NUMBER) || cc.data.t.equals(Token.Type.EXPRESSION) || cc.data.t.equals(Token.Type.IDENTIFIER)){
                        String r = generatecodeexpression(cc,vt,f,buf);
                        args.add(r);
                    }
                }
                for( i = 0 ; i < args.size();i++){
                    buf.append("MOV ARG" + i + "," + args.get(i) + "\n");
                }
                buf.append("CALL" + (f3 != null?f3.value:"unknown")+ "\n");
                buf.append("RET\n");
                
                
            }
            else if(c.data.t == Token.Type.EXPRESSION|| c.data.t == Token.Type.OPERATOR || c.data.t == Token.Type.IDENTIFIER || c.data.t == Token.Type.NUMBER){
                String reg = generatecodeexpression(c, vt, f,buf);
                buf.append("MOV R0, " + reg + "\n");
                buf.append("RET\n");
                
            }
        }
        return buf;
        
        
    }
    /*
     * functional : FUNC IDENTIFIER'(' ASSIGNMENTKEYWORDS IDENTIFIER (',' ASSIGNMENTKEYWORDS IDENTIFIER)*')' body;
     */
    public StringBuffer generatecodefunctional(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        
        if(root == null)return new StringBuffer();
            StringBuffer genertor1 = map.computeIfAbsent(f, k -> new StringBuffer());
            HashMap<String,String> localSymbols = symbolTable.computeIfAbsent(f, k->new HashMap<>());
            
            
            for (int i = 1; i + 1 < root.children.size(); i += 2) {
                TreeNode typeNode = root.children.get(i);
                TreeNode paramNode = root.children.get(i + 1);
                if (typeNode.data.t == Token.Type.ASSIGNMENTKEYWORDS) {
                    localSymbols.put(paramNode.data.value, newReg());
                }
            }
            if (root.data != null && root.data.value != null) {
                genertor1.append(root.children.get(0).data.value + ":\n");
            }
            for(TreeNode c : root.children){
                if(c == null)continue;
                if(c.data == null)continue;
                if (c.data.t == Token.Type.ASSIGNMENT)
                    genertor1.append(generatecodeassignment(c, localSymbols, vt, f));
                else if (c.data.t == Token.Type.EXPRESSION){
                    String s5 = generatecodeexpression(c, vt, f,genertor1);
                    genertor1.append(s5);
                    returner.put(f.value, s5);
                }
                else if (c.data.t == Token.Type.RETURN)
                    genertor1.append(generatecodereturn(c, vt, f));
                else if(c.data.t == Token.Type.BODY){
                    return (this.generatecodebody(c,vt,f));
                }
            }
        return genertor1;
    }
    public StringBuffer generatecodeassignment(TreeNode root,HashMap<String,String> symbolTableforFunc,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        if(root == null)return new StringBuffer();
        StringBuffer buf = map.computeIfAbsent(f, k->new StringBuffer());
        
        TreeNode varNode = null;
        TreeNode exprNode = null;

        for(TreeNode child : root.children){
            if(child.data.t == Token.Type.IDENTIFIER) varNode = child;
            else if(child.data.t == Token.Type.EXPRESSION || child.data.t == Token.Type.NUMBER || child.data.t == Token.Type.IDENTIFIER) exprNode = child;
        }

        if(varNode == null || exprNode == null){
            throw new RuntimeException("Assignment node missing variable or expression");
        }
        String varName = varNode.data.value;
        
        String varReg = symbolTableforFunc.get(varName);
        if (varReg == null) {
            varReg = newReg();
            symbolTableforFunc.put(varName, varReg);
        }
        
        buf.append("MOV " + varReg + ", " + generatecodeexpression(exprNode, vt, f,buf) + "\n");
        return buf;
    }
    public String generatorcodenumber(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        String reg = newReg();
        StringBuffer buf = map.computeIfAbsent(f, k -> new StringBuffer());
        buf.append("MOV " + reg + ", " + root.data.value + "\n");
        return reg;
    }
    public String generatecodeidentifier(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        symbolTable.computeIfAbsent(f,k->new HashMap<>());
        HashMap<String,String> table = symbolTable.get(f);
        String reg = table.get(root.data.value);
        if(reg == null){
            reg = newReg();
            symbolTable.get(f).put(root.data.value,reg);
        }
        return reg;
    }
    public String generatecodeexpression(TreeNode root, HashMap<functiontype, HashMap<String, valuetype>> vt, functiontype f, StringBuffer buf) {
    if (root == null || root.data == null) throw new RuntimeException("Null expression node");

    

    switch (root.data.t) {
        case Token.Type.NUMBER:
            return generatorcodenumber(root, vt, f);

        case Token.Type.IDENTIFIER:
            
            if (!root.children.isEmpty()) { 
                functiontype ftype = existencechecker(root.data.value, vt.keySet());
                String retReg = newReg();
                ArrayList<String> args = new ArrayList<>();

                for (TreeNode cc : root.children) {
                    if (cc == null || cc.data == null) continue;
                    args.add(generatecodeexpression(cc, vt, f,buf));
                }

                for (int i = 0; i < args.size(); i++) {
                    buf.append("MOV ARG" + i + "," + args.get(i) + "\n");
                }

                buf.append("CALL " + ftype.value + "\n");
                buf.append("MOV " + retReg + "," + returner.get(ftype.value) + "\n");
                return retReg;
            } else { 
                return generatecodeidentifier(root, vt, f);
            }

        case Token.Type.FUNCTIONAL: 
            functiontype ftype = existencechecker(root.data.value, vt.keySet());
            String retReg = newReg();
            ArrayList<String> args = new ArrayList<>();
            for (TreeNode cc : root.children) {
                if (cc == null || cc.data == null) continue;
                args.add(generatecodeexpression(cc, vt, f,buf));
            }
            for (int i = 0; i < args.size(); i++)
                buf.append("MOV ARG" + i + "," + args.get(i) + "\n");

            buf.append("CALL " + (ftype != null ? ftype.value : "unknown") + "\n");
            buf.append("MOV " + retReg + ", R0\n");
            return retReg;

        case Token.Type.OPERATOR:
            return genereatecodeoperator(root, vt, f, buf);

        case Token.Type.EXPRESSION:
            if (!root.children.isEmpty()) return generatecodeexpression(root.children.get(0), vt, f,buf);
            throw new RuntimeException("Empty expression node");

        default:
            throw new RuntimeException("Unsupported expression type: " + root.data.t);
    }
}

    public StringBuffer generatecodebody(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        if(root == null){
            return new StringBuffer();
        }
        StringBuffer buf = new StringBuffer();
        for(TreeNode node : root.children){
            if(node == null){
                continue;
            }
            buf = generatecodestatement(node,vt,f);
        }
        return buf;
    }
    public String genereatecodeoperator(TreeNode root, HashMap<functiontype, HashMap<String, valuetype>> vt, functiontype f, StringBuffer genertor1) {
    String s = newReg();
    String leftreg = generatecodeexpression(root.children.get(0), vt, f, genertor1);
    String rightreg = generatecodeexpression(root.children.get(1), vt, f, genertor1);

    switch (root.data.value) {
        case "+":
            genertor1.append("ADD " + s + " " + leftreg + " " + rightreg + "\n");
            break;
        case "-":
            genertor1.append("SUB " + s + " " + leftreg + " " + rightreg + "\n");
            break;
        case "*":
            genertor1.append("MUL " + s + " " + leftreg + " " + rightreg + "\n");
            break;
        case "/":
            genertor1.append("DIV " + s + " " + leftreg + " " + rightreg + "\n");
            break;
    }
    return s;
}
    public functiontype existencechecker(String s,Set<functiontype> map){
        for(functiontype e : map){
            if(e.value.equals(s)){
                return e;
            }
        }
        return null;
    }

}