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
                symboltable s = new symboltable(f);
                executor e = new executor();
                s.symbolTable1(e,n);
                functiontype fn = existencechecker(n.data.value, vt.keySet());
                if(fn == null){
                    fn = new functiontype(n.data.value, valuetype.Type.DEFAULT);
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
                        String r = generatecodeexpression(cc,vt,f);
                        args.add(r);
                    }
                }
                for( i = 0 ; i < args.size();i++){
                    buf.append("MOV ARG" + i + "," + args.get(i) + "\n");
                }
                buf.append("CALL" + (f3 != null?f3.value:"unknown")+ "\n");
                buf.append("RET\n");
                
                
            }
            else if(c.data.t == Token.Type.EXPRESSION){
                String reg = generatecodeexpression(root.children.get(0), vt, f);
                buf.append("RET " + reg + "\n");
                
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
            
            Token potentialId = null;
            for (int i = 1; i < root.children.size(); i += 2) {
                TreeNode typeNode = root.children.get(i);
                TreeNode paramNode = root.children.get(i + 1);
                if (typeNode.data.t == Token.Type.ASSIGNMENTKEYWORDS) {
                    localSymbols.put(paramNode.data.value, newReg());
                }
            }
            for (TreeNode c : root.children) {
                if (c != null && c.data != null && c.data.t == Token.Type.IDENTIFIER) {
                    potentialId = c.data;
                    break;
                }
            }
            if (potentialId != null) {
                genertor1.append(potentialId.value + ":\n"); 
            }
            for(TreeNode c : root.children){
                if(c == null)continue;
                if(c.data == null)continue;
                if (c.data.t == Token.Type.ASSIGNMENT)
                    genertor1.append(generatecodeassignment(c, localSymbols, vt, f));
                else if (c.data.t == Token.Type.EXPRESSION)
                    genertor1.append(generatecodeexpression(c, vt, f));
                else if (c.data.t == Token.Type.RETURN)
                    genertor1.append(generatecodereturn(c, vt, f));
                else if(c.data.t == Token.Type.BODY){
                    genertor1.append(generatecodebody(c,vt,f));
                }
            }
        return genertor1;
    }
    public StringBuffer generatecodeassignment(TreeNode root,HashMap<String,String> symbolTableforFunc,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        if(root == null)return new StringBuffer();
        StringBuffer buf = map.computeIfAbsent(f, k->new StringBuffer());
        
        symbolTableforFunc.computeIfAbsent(root.data.value, k -> new String());
        String varReg = symbolTableforFunc.get(root.data.value);
        if (varReg == null) {
            varReg = newReg();
            symbolTableforFunc.put(root.data.value, varReg);
        }
        TreeNode rhs = null;
        for (TreeNode child : root.children) {
            if (child.data.t == Token.Type.EXPRESSION || child.data.t == Token.Type.NUMBER || child.data.t == Token.Type.IDENTIFIER) {
            rhs = child;
            break;
    }
}
        if (rhs != null)
            buf.append("MOV " + " " + varReg + ", " + generatecodeexpression(rhs, vt, f) + "\n");
            else{
                throw new IllegalArgumentException("Not Possible");
            }
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
    public String generatecodeexpression(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        if(root == null || root.data == null)throw new RuntimeException("Null expression node");
        StringBuffer s = map.computeIfAbsent(f, k -> new StringBuffer());
        if (root.data.t == Token.Type.NUMBER) {
            return generatorcodenumber(root, vt, f);
        } else if (root.data.t == Token.Type.IDENTIFIER) {
            return generatecodeidentifier(root, vt, f);
        }
        else if(root.data.t == Token.Type.OPERATOR || (root.children != null && root.children.size() == 2)){
            return genereatecodeoperator(root, vt, f, s);
        }
        else if(root.data.t == Token.Type.EXPRESSION){
            if (!root.children.isEmpty()) return generatecodeexpression(root.children.get(0), vt, f);
        }
        else {
            throw new RuntimeException("Unhandled expression node type: " + root.data.t);
        }
        return genereatecodeoperator(root.children.get(0), vt, f,s);
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
            buf.append(generatecodestatement(root,vt,f));
        }
        return buf;
    }
    public String genereatecodeoperator(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f,StringBuffer genertor1){
        String s  = newReg();
        
        String leftreg = new String();
        String rightreg = new String();
        switch(root.data.value){
            
            case "+":
            
            
                if(root.children.get(0).data.t == Token.Type.OPERATOR){
                    leftreg = genereatecodeoperator(root.children.get(0),vt,f,genertor1);
                }
                
                else if(root.children.get(0).data.t == Token.Type.NUMBER){
                    leftreg = generatorcodenumber(root.children.get(0),vt,f);
                }
                else{
                    leftreg = generatecodeidentifier(root.children.get(0), vt, f);
                }
                if(root.children.get(1).data.t == Token.Type.OPERATOR){
                    rightreg = genereatecodeoperator(root.children.get(1),vt,f,genertor1);
                }
                
                else if(root.children.get(1).data.t == Token.Type.NUMBER){
                    rightreg = generatorcodenumber(root.children.get(1),vt,f);
                }
                else{
                    rightreg = generatecodeidentifier(root.children.get(1), vt, f);
                }
                
            
            genertor1.append("ADD" + " " + s + " " + leftreg + " " + rightreg + "\n");
            break;
            case "-":
            
            
                if(root.children.get(0).data.t == Token.Type.OPERATOR){
                    leftreg = genereatecodeoperator(root.children.get(0),vt,f,genertor1);
                }
                
                else if(root.children.get(0).data.t == Token.Type.NUMBER){
                    leftreg = generatorcodenumber(root.children.get(0),vt,f);
                }
                else{
                    leftreg = generatecodeidentifier(root.children.get(0), vt, f);
                }
                if(root.children.get(1).data.t == Token.Type.OPERATOR){
                    rightreg = genereatecodeoperator(root.children.get(1),vt,f,genertor1);
                }
                
                else if(root.children.get(1).data.t == Token.Type.NUMBER){
                    rightreg = generatorcodenumber(root.children.get(1),vt,f);
                }
                else{
                    rightreg = generatecodeidentifier(root.children.get(1), vt, f);
                }
                
            
            genertor1.append("SUB" + " " + s + " " + leftreg + " " + rightreg +"\n");
            break;
            case "/":
            
            
                if(root.children.get(0).data.t == Token.Type.OPERATOR){
                    leftreg = genereatecodeoperator(root.children.get(0),vt,f,genertor1);
                }
                
                else if(root.children.get(0).data.t == Token.Type.NUMBER){
                    leftreg = generatorcodenumber(root.children.get(0),vt,f);
                }
                else{
                    leftreg = generatecodeidentifier(root.children.get(0), vt, f);
                }
                if(root.children.get(1).data.t == Token.Type.OPERATOR){
                    rightreg = genereatecodeoperator(root.children.get(1),vt,f,genertor1);
                }
                
                else if(root.children.get(1).data.t == Token.Type.NUMBER){
                    rightreg = generatorcodenumber(root.children.get(1),vt,f);
                }
                else{
                    rightreg = generatecodeidentifier(root.children.get(1), vt, f);
                }
                
            
            genertor1.append("DIV" + " " + s + " " + leftreg + " " + rightreg + "\n");
            break;
            case "*":
            
                if(root.children.get(0).data.t == Token.Type.OPERATOR){
                    leftreg = genereatecodeoperator(root.children.get(0),vt,f,genertor1);
                }
                
                else if(root.children.get(0).data.t == Token.Type.NUMBER){
                    leftreg = generatorcodenumber(root.children.get(0),vt,f);
                }
                else{
                    leftreg = generatecodeidentifier(root.children.get(0), vt, f);
                }
                if(root.children.get(1).data.t == Token.Type.OPERATOR){
                    rightreg = genereatecodeoperator(root.children.get(1),vt,f,genertor1);
                }
                
                else if(root.children.get(1).data.t == Token.Type.NUMBER){
                    rightreg = generatorcodenumber(root.children.get(1),vt,f);
                }
                else{
                    rightreg = generatecodeidentifier(root.children.get(1), vt, f);
                }
                
            
            genertor1.append("MUL" + " " + s + " " + leftreg + " " + rightreg + "\n");
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