package Parser;
import java.util.*;

import javax.print.attribute.HashAttributeSet;
class semanticanalysis{
    ArrayList<valuetype> val = new ArrayList<>();
    functiontype ft = new functiontype(null, null);
    HashMap<String,valuetype.Type> ae1 = new HashMap<>(Map.of("int",valuetype.Type.INT , "bool", valuetype.Type.BOOLEAN));
    int pos  = 0;
    HashMap<String, valuetype.Type> operatorMapper = new HashMap<>(Map.of(
    "+", valuetype.Type.INT,
    "-", valuetype.Type.INT,
    "*", valuetype.Type.INT,
    "/", valuetype.Type.INT
));
    HashMap<String, valuetype.Type> ae = new HashMap<>(Map.of("int", valuetype.Type.INT , "bool", valuetype.Type.BOOLEAN));
    public semanticanalysis(HashMap<String,valuetype> symbolTable){
        for(Map.Entry<String,valuetype> e: symbolTable.entrySet()){
            val.add(e.getValue());
        }
    }
    public valuetype peek(){
        return val.get(pos);
    }
    public valuetype consume(){
        return val.get(pos++);
    }
    public valuetype.Type contains(String name){
    if(name.equals("int") || name.equals("bool") || name.equals("var") || name.equals("let")){
        return null; 
    }
    for(valuetype v : val){
        if(name.equals(v.value)) return v.t;
    }
    return null;
}
    public valuetype.Type typechecker(TreeNode root,ArrayList<valuetype> val){
        
        valuetype.Type last;
        if(root.children == null || root.children.isEmpty()){
             return this.contains(root.data.value);
            
        }
        if(root != null && root.children.size() == 2){
            valuetype.Type a = this.typechecker(root.children.get(0), val);
            valuetype.Type b = this.typechecker(root.children.get(1), val);
            if(a == null || b == null) {
                throw new IllegalArgumentException("Type not found for operands of " + root.data.value);
            }
            last = ((a.equals(b)) ) ? a : null ;
            
        }
        else {
            return null;
        }
        return last;
    }
    public valuetype.Type complexoperationshandler(TreeNode root,ArrayList<valuetype> val,HashMap<String,valuetype> vt)throws IllegalArgumentException{
        valuetype v2 ;
        HashMap<valuetype.Type,ArrayList<valuetype.Type>> map = new HashMap<>();
        for(int i = 0 ; i < root.children.size();i++){
            TreeNode node = root.children.get(i);
            valuetype vtId = (new valuetype(node.data.value,operatorMapper.get(node.data.value)));
            v2 = vtId;
                if(node.data.t == Token.Type.OPERATOR){
                    map.putIfAbsent(vtId.t, new ArrayList<>());
                    valuetype.Type v = complexoperationshandler(node, val, vt);
                    map.get(vtId.t).add(v);
                    functiontype f = new functiontype(node.data.value, v);
                    vt.put(node.data.value,new valuetype(node.data.value,v));

                }
                else if(node.data.t != Token.Type.OPERATOR){
                    map.putIfAbsent(vtId.t, new ArrayList<>());
                    map.get(vtId.t).add(typechecker(node, val));
                }
            }
            
        for(int i =0;i < map.get(root.data.t).size()-1;i++){
            
            if(map.get(root.data.t).get(i) != map.get(root.data.t).get(i+1)){
                throw new IllegalArgumentException("Not possible");
            }
        }
        
        if(map.size() > 1) throw new IllegalArgumentException("Type mismatch in operation");

        if (!map.isEmpty()) {
            return map.keySet().iterator().next(); 
        } else {
            return null;
        }
    }
    public void scopeManagement(ArrayList<TreeNode> root,HashMap<functiontype,HashMap<String,valuetype>> vt,HashMap<String,valuetype> symbolTable){
        
        
            for(TreeNode i : root){
                Token value = i.data;
                if (value.t == Token.Type.ASSIGNMENTKEYWORDS ||
                    value.value.equals("int") || 
                    value.value.equals("bool") || 
                    value.value.equals("var") || 
                    value.value.equals("let")) {
                    continue;
                }
                if(value.t.equals(Token.Type.FUNCTIONAL)){
                    TreeNode funcNameNode = i.children.get(0);
                    String funcName = funcNameNode.data.value;
                    String s  = new String();
                    functiontype f = new functiontype(funcName,valuetype.Type.INT);

                    vt.putIfAbsent(f, new HashMap<>());
                    HashMap<String, valuetype> funcScope = vt.get(f);
                    funcScope.put(funcName,new valuetype(funcName,valuetype.Type.INT));

                    for (int j = 1; j < i.children.size() - 1; j += 2) {
                        TreeNode typeNode = i.children.get(j);
                        TreeNode paramNode = i.children.get(j + 1);
                        if(typeNode.data.t == Token.Type.BODY)break;
                        if (typeNode.data.t == Token.Type.ASSIGNMENTKEYWORDS && paramNode.data.t == Token.Type.IDENTIFIER) {
                            valuetype.Type paramType = ae.get(typeNode.data.value); 
                            funcScope.put(paramNode.data.value, new valuetype(paramNode.data.value, paramType));
                        }
                    }

    
                    TreeNode bodyNode = i.children.get(i.children.size() - 1);
                    this.scopeManagement(bodyNode.children, vt, funcScope);
                }
                else if(value.t.equals(Token.Type.ASSIGNMENT)){
                    this.scopeManagement(i.children, vt, symbolTable);

                }
                else if(value.t.equals(Token.Type.ASSIGNMENTKEYWORDS)){
                    continue;
                }else if(value.t.equals(Token.Type.IDENTIFIER)){
                    // Skip if this identifier is actually a type keyword
                    boolean b = false;
                    for(functiontype f : vt.keySet()){
                        for(String v : vt.get(f).keySet()){
                            if(v.equals(value.value))b = true;
                        }
                    }
                    if (b) continue; // already in table
                    if(value.value.equals("int") || value.value.equals("bool") || value.value.equals("var") || value.value.equals("let")){
                        continue; 
                    }
                    throw new IllegalArgumentException("Variable '" + value.value + "' used before declaration.");
                }
                else {
                        if (!i.children.isEmpty()) {
                    this.scopeManagement(i.children, vt, symbolTable);
                }
                }

        }
}

    public void functioncheck(TreeNode root,HashMap<functiontype,HashMap<String,valuetype>> vt,functiontype f) throws IllegalArgumentException{
        for(int i = 0 ; i < root.children.size();i++){
            switch(root.children.get(i).data.t){
                case Token.Type.RETURN:
                functioncheck(root.children.get(0), vt,f);
                break;
                case Token.Type.FUNCTIONAL:
                functiontype func = existencechecker1(root.children.get(i).children.get(0).data.value, vt.keySet());
                functioncheck(root.children.get(i), vt,func);
                break;
                case Token.Type.IDENTIFIER:
                boolean b = false;
                boolean t = false;
                valuetype fCalled = new valuetype();
                String s = root.children.get(i).data.value;
                vt.putIfAbsent(f, new HashMap<>());
                HashMap<String, valuetype> currentScope = vt.get(f);
                Set<valuetype> set = new HashSet<>(vt.get(f).values());
                fCalled = existencechecker2(s, set);
                boolean exists = false;
                for (Map<String, valuetype> scope : vt.values()) {
                    if (scope.containsKey(s)) {
                        exists = true;
                        fCalled = scope.get(s);
                        break;
                    }
                }
                if (exists == false) {
                    throw new IllegalArgumentException("Function '" + s + "' not declared.");
                }
                if(f.t != valuetype.Type.DEFAULT){
                    HashMap<String,valuetype> e  = vt.get(f);
                        for(valuetype f4 : e.values()){
                            if(f4.equals(fCalled)){
                                t = true;
                                break;
                            }
                        }
                        if(!t){
                            HashMap<String, valuetype> currentScope2 = vt.get(f);
                            if (!currentScope2.containsKey(s)) {
                                throw new IllegalArgumentException("Variable or function '" + s + "' not declared in current scope.");
                            }
                        }
                    }
                    
                break;
                case Token.Type.ASSIGNMENT:
                TreeNode varNode = root.children.get(1);
                if (varNode.data.t != Token.Type.IDENTIFIER) break;
                String lhsName = varNode.data.value;
                HashMap<String, valuetype> currentScope1 = vt.get(f);
                valuetype.Type lhsType = ae.get(root.children.get(i).data.value);

                if (!currentScope1.containsKey(lhsName)) {
                    currentScope1.put(lhsName, new valuetype(lhsName, lhsType));
                }
                

                    if (!varNode.children.isEmpty()) {
                TreeNode rhs = root.children.get(2);
                valuetype.Type rhsType = typechecker(rhs, val);
                if (lhsType != rhsType) {
                    throw new IllegalArgumentException("Type mismatch in assignment to '" + lhsName + "'");
                }
            }
                break;
                case Token.Type.EXPRESSION:
                
                    if(f.t != valuetype.Type.DEFAULT){
                        semanticanalysis s6 = new semanticanalysis(vt.get(f));
                        valuetype.Type v = complexoperationshandler(root.children.get(i), s6.val,vt.get(f));
                        if(v != null && !v.equals(f.t)){
                            throw new IllegalArgumentException("Not possible");
                        }
                    }
                break;
                case Token.Type.NUMBER:
                valuetype vtObj = vt.get(f).get(root.data.value);
                if (vtObj == null) {
                    throw new IllegalArgumentException("Variable '" + root.data.value + "' not declared.");
                }
                if(poschecker(vtObj.t, new ArrayList<valuetype>(vt.get(f).values()), i) != i){
                    throw new IllegalArgumentException("This is not valid");
                }
                break;
                default:
                    break;
            }
        }
    }
        
    public functiontype existencechecker1(String s,Set<functiontype> map){
        for(functiontype e : map){
            if(e.value.equals(s)){
                return e;
            }
        }
        return null;
    }
    public valuetype existencechecker2(String s,Set<valuetype> map){
        for(valuetype e : map){
            if(e.value.equals(s)){
                return e;
            }
        }
        return null;
    }
    public functiontype existencechecker3(valuetype.Type t,Set<functiontype> map){
        for(functiontype e : map){
            if(e.t.equals(t)){
                return e;
            }
        }
        return null;
    }
    public int poschecker(valuetype.Type t,ArrayList<valuetype> map,int i1){
        for(int i = 0 ; i < map.size();i++){
            if(map.get(i).t == t && i == i1){
                return i1;
            }
        }
        return -1;
    }

    
    
    
}
