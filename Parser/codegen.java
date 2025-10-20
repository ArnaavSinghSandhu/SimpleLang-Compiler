package Parser;

import java.util.*;

/*
 * x86 assembly conversion
 */

class generator{
    StringBuffer genertor = new StringBuffer();
    Deque<HashMap<functiontype,StringBuffer>> stack = new ArrayDeque<>();
    int regCounter = 3;
    public String newReg() {
        return "R" + (regCounter++);
    }
    HashMap<functiontype,StringBuffer> map = new HashMap<>();
    HashMap<functiontype,HashMap<String,String>> symbolTable = new HashMap<>();
    HashMap<String, valuetype.Type1> operatorMapper = Map.of(
    "+", valuetype.Type1.ADDITION,
    "-", valuetype.Type1.SUBTRACTION,
    "*", valuetype.Type1.MULITIPLICATION,
    "/", valuetype.Type1.DIVISION
);
    public void generatecodestatement(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        switch(root.children.get(0).data.t){
            case Token.Type.FUNCTIONAL:
            symbolTable.put(f,new HashMap<>());
            HashMap<functiontype,StringBuffer> map1 = new HashMap<>();
            map1.put(existencechecker(root.children.get(0).data.value,vt.keySet()),generatecodefunctional(root.children.get(0), vt, existencechecker(root.children.get(0).data.value,vt.keySet())));
            stack.push(map1);
            break;
            case Token.Type.ASSIGNMENT:
            generatecodeassignment(root.children.get(0),symbolTable ,vt,f);
            break;
            case Token.Type.RETURN:
            generatecodereturn(root.children.get(0),vt,f);
            break;
        }
        
    }
    public void generatecodereturn(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        if(root.children.get(0).data.t == Token.Type.FUNCTIONAL){
            int i = 0;
            functiontype f3 = new functiontype();
            LinkedHashMap<String, valuetype> map1 = new LinkedHashMap<>();
            while(i<root.children.get(0).children.size()){
                if(i == 2 && root.children.get(0).children.get(i).data.t == Token.Type.IDENTIFIER){
                    for(functiontype f4 : vt.keySet()){
                        if(root.children.get(0).children.get(i).data.value.equals(f4.value)){
                            map1 = vt.get(f4);
                            f3 = f4;
                            break;
                        }
                    }
                }
                else if(root.children.get(0).children.get(i).data.t == Token.Type.NUMBER){
                    ArrayList<String> list1 = new ArrayList<>(map1.keySet());
                    int j = 0;
                    map.get(f).append("MOV" + " " + symbolTable.get(f3).get(list1.get(i)) + "," +  root.children.get(0).children.get(i).data.value);  
                }
                i++;
            }
            map.get(f).append("CALL " + f3.value + "\n");
            
        }
        else if(root.children.get(0).data.t == Token.Type.EXPRESSION){
            generatecodeexpression(root.children.get(0), vt, f);
        }
        
    }
    /*
     * functional : FUNC IDENTIFIER'(' ASSIGNMENTKEYWORDS IDENTIFIER (',' ASSIGNMENTKEYWORDS IDENTIFIER)*')' body;
     */
    public StringBuffer generatecodefunctional(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        int i = 0;
        StringBuffer genertor1 = new StringBuffer();
        while(i<root.children.size()){
            if(i == 2 && root.children.get(i).data.t == Token.Type.IDENTIFIER){
                genertor1.append(root.children.get(i).data.value + " " + ":");
            }
            else if(root.children.get(i).data.t == Token.Type.IDENTIFIER){
               generatecodeidentifier(root.children.get(i).data,vt,f);
            }
            else if(root.children.get(i).data.t == Token.Type.BODY){
                generatecodebody(root.children.get(i),vt,f);
            }
            i++;
        }
        return stack.pop().get(f);
    }
    public StringBuffer generatecodeassignment(TreeNode root,HashMap<String,String> symbolTable,HashMap<functiontype,HashMap<String,valuetype>> vt,Hashfunctiontype f){
        String varReg = symbolTable.get(f).get(root.data.value());
        if (varReg == null) {
            varReg = newReg();
            symbolTable.get(f).put(root.data.value(), varReg);
        }
        TreeNode rhs = null;
        for (TreeNode child : root.children) {
            if (child.data.t == Token.Type.EXPRESSION || child.data.t == Token.Type.NUMBER || child.data.t == Token.Type.IDENTIFIER) {
            rhs = child;
            break;
    }
}
        if (rhs != null)
            genertor.append("MOV " + " " + varReg + ", " + generatecodeexpression(rhs, vt, f) + "\n");
        return genertor;
    }
    public String generatorcodenumber(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        String reg = newReg();
        genertor.append("MOV " + " " + reg + ", " + root.data.value + "\n");
        return reg;
    }
    public String generatecodeidentifier(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        String reg = symbolTable.get(f).get(root.data.value());
        if(reg == null){
            reg = newReg();
            symbolTable.get(f).put(root.data.value,reg);
        }
        return reg;
    }
    public String generatecodeexpression(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        StringBuffer s = map.get(f);
        return genereatecodeoperator(root.children.get(0), vt, f,s);
    }
    public void generatecodebody(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f){
        generatecodestatement(root.children.get(0),vt,f);
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