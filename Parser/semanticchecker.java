package Parser;
import java.util.*;
class semanticanalysis{
    ArrayList<valuetype> val = new ArrayList<>();
    functiontype ft = new functiontype();
    HashMap<String,vt.Type> ae1 = Map.of("int",ft.Type.INT , "bool", ft.Type.BOOLEAN);
    int pos  = 0;
    HashMap<String, valuetype.Type1> operatorMapper = Map.of(
    "+", valuetype.Type.INT,
    "-", valuetype.Type.INT,
    "*", valuetype.Type.INT,
    "/", valuetype.Type.INT
);
    HashMap<String, valuetype.Type> ae = Map.of("int", valuetype.Type.INT , "bool", valuetype.Type.BOOLEAN);
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
    public valuetype.Type contains(String valuetype){
        for(int i = 0 ; i < val.size() ; i++){
            if(valuetype.equals(val.get(i).value)) return val.get(i).t; 
        }
        return null;
    }
    public void checker(TreeNode root,boolean b){
        valuetype.Type v = this.typechecker(root, this.val);
        if(v != null){
            this.labelchecker();
        }
    }
    public valuetype.Type typechecker(TreeNode root,ArrayList<valuetype> val){
        
        valuetype.Type last;
        if(root.children == null || root.children.isEmpty()){
            if(root.data.isIdentifier())
                return this.contains(root.data.value);
            else{
                return root.data.t;
            }
        }
        if(root != null && root.children.size() == 2){
            valuetype.Type a = this.typechecker(root.get(0), val);
            valuetype.Type b = this.typechecker(root.get(1), val);
            last = ((a != null && b != null && a.equals(b)) ) ? a : null ;
            
        }
        else {
            return null;
        }
        return last;
    }
    public valuetype.Type complexoperationshandler(TreeNode root,ArrayList<valuetype> val)throws IllegalArgumentException{
        valuetype.Type v2 = null;
        HashMap<valuetype.Type,ArrayList<valuetype.Type>> map = new HashMap<>();
        for(int i = 0 ; i < root.children.size();i++){
            TreeNode node = root.children.get(i);
            valuetype vtId = (new valuetype(node.data.value,operatorMapper.get(node.data.value)));
                if(node.data.t == Token.Type.OPERATOR){
                    map.putIfAbsent(vtId.t, new ArrayList<>());
                    map.get(vtId.t).add(complexoperationshandler(node,val));
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
        
        
        return v2;
    }
    public void scopeManagement(ArrayList<TreeNode> root,HashMap<functiontype,HashMap<String,valuetype>> vt,HashMap<String,valuetype> symbolTable){
        

            for(TreeNode i : root){
                Token value = i.data;
                if(value.t.equals(Token.Type.functional)){
                    String s  = new String();
                    for(TreeNode i1 : i.children){
                        Token val = i1.data;
                        if(val.t.equals(Token.Type.FUNC)){
                            s = val.value;
                            break;
                        }
                    }
                    functiontype f = new functiontype(s,ae1.get(s));
                    vt.putIfAbsent(f, new HashMap<>());
                    HashMap<String,valuetype> symboltable1 = vt.get(f);
                    this.scopeManagement(i.children, vt, symboltable1);
                }
                else if(value.t.equals(Token.Type.assignment)){
                    this.scopeManagement(i.children, vt, symbolTable);

                }
                else if(value.t.equals(Token.Type.IDENTIFIER)){
                    String varName = value.value;
                    if (!symbolTable.containsKey(varName)) {
                    System.err.println("Semantic Error: Variable '" + varName + "' used before declaration.");
                }
                }else {
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
                f = existencechecker(root.children.get(i).data.value, vt);
                functioncheck(root.children.get(i), vt,f);
                break;
                case Token.Type.IDENTIFIER:
                boolean b = false;
                boolean t = false;
                valuetype fCalled = new valuetype();
                String s = root.data.value;
                
                fCalled = existencechecker(s, vt.get(f).keySet());
                if(fCalled != null){
                    b = true;
                }
                
                if (b == false) {
                    throw new IllegalArgumentException("Function '" + s + "' not declared.");
                }
                if(f.t != functiontype.Type.DEFAULT){
                    HashMap<String,valuetype> e  = vt.get(f);
                        for(valuetype f4 : e.values()){
                            if(f4.equals(fCalled)){
                                t = true;
                                break;
                            }
                        }
                        if(!t){
                            HashMap<String, valuetype> currentScope = vt.get(f);
                            if (!currentScope.containsKey(s)) {
                                throw new IllegalArgumentException("Variable or function '" + s + "' not declared in current scope.");
                            }
                        }
                    }
                    
                break;
                case Token.Type.ASSIGNMENTKEYWORDS:
                
                String lhsName = root.children.get(i).value;
                
                HashMap<String, valuetype> currentScope = vt.get(f);
                if (!currentScope.containsKey(lhsName)) {
                    throw new IllegalArgumentException("Variable '" + lhsName + "' used before declaration.");
                }
                valuetype.Type lhsType = currentScope.get(lhsName).t;

                TreeNode rhs = root.children.get(i).children.get(0); 
                valuetype.Type rhsType = typechecker(rhs, val);
               
                if(lhsType != rhsType){
                    throw new IllegalArgumentException("Type mismatch in assignment to '" + lhsName + "'");
                }
                break;
                case Token.Type.EXPRESSION:
                
                    if(f.t != functiontype.Type.DEFAULT){
                        semanticanalysis s6 = new semanticanalysis(vt.get(f));
                        valuetype v = complexoperationshandler(root.children.get(i), s6.val);
                        if(v != null && v.t != f.t){
                            throw new IllegalArgumentException("Not possible");
                        }
                    }
                break;
                case Token.Type.NUMBER:
                if(poschecker(root.children.get(i), new ArrayList<valuetype>(vt.get(f).values()), i) != i){
                    throw new IllegalArgumentException("This is not valid");
                }
                break;
            }
        }
    }
        
    public functiontype existencechecker(String s,Set<functiontype> map){
        for(functiontype e : map){
            if(e.value.equals(s)){
                return e;
            }
        }
        return null;
    }
    public valuetype existencechecker(String s,Set<valuetype> map){
        for(valuetype e : map){
            if(e.value.equals(s)){
                return e;
            }
        }
        return null;
    }
    public functiontype existencechecker(valuetype.Type t,Set<functiontype> map){
        for(functiontype e : map){
            if(e.t == t){
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
