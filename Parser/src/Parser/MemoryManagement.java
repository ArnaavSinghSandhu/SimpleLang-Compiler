package Parser;

import java.util.*;
import java.io.*;

class memorymanagement{
    Deque<Frame> stackFrames;
    Deque<address> doublechecker = new ArrayDeque<>();
    public memorymanagement(){
        stackFrames = new ArrayDeque<>();
    }
    public void push(symboltable s,StringBuffer s1){
        address a = addressgiver();
        Frame f1 = new Frame(a, s, s1);
        doublechecker.push(a);
        stackFrames.push(f1);
    }
    public address addressgiver() {
    String hex = UUID.randomUUID().toString().replace("-", "");
    return new address(hex);
}
    public StringBuffer pop(functiontype f){
    address addr = doublechecker.pop();
    if (addr == null) throw new RuntimeException("No memory for function: " + f.value);
    Iterator<Frame> it = stackFrames.iterator();
    while(it.hasNext()){
        Frame f2 = it.next();
        if(f2.s.f.equals(f)){
            it.remove();
            return f2.s1;
        }
    }
    return null;
}
}
class symboltable{
    functiontype f;
    HashMap<functiontype,HashMap<String,valuetype>> symboltableorg;

    public symboltable(functiontype f){
        this.f = f;
        this.symboltableorg = new HashMap<>();
    }
    public void symbolTable1(executor e, TreeNode root) {
    System.out.println(f.value);

    HashMap<String, valuetype> tableForF = symboltableorg.computeIfAbsent(f, k -> new HashMap<>());

    for (int i = 0; i < root.children.size(); i++) {
        TreeNode child = root.children.get(i);
        if (child == null || child.data == null) continue;

        if (child.data.t == Token.Type.ASSIGNMENTKEYWORDS) {
            if (i + 1 < root.children.size()) {
                TreeNode next = root.children.get(i + 1);
                if (next != null && next.data != null && next.data.t == Token.Type.IDENTIFIER) {
                    tableForF.put(next.data.value, new valuetype(next.data.value,valuetype.Type.INT));
                    i++; 
                }
            }
        }
    }

            e.functionalvisitor(root.children, tableForF, e.operatorTable);
            semanticanalysis sa = new semanticanalysis(tableForF);
            sa.scopeManagement(root.children, symboltableorg, symboltableorg.get(f));
            sa.functioncheck(root, symboltableorg, f);

            symboltableorg.put(f, tableForF);
        }

}
class address{
    String add;
    public address(String add){
        this.add = add;
    }

}

class Frame{
    address a;
    symboltable s;
    StringBuffer s1;

    public Frame(address a,symboltable s,StringBuffer s1){
        this.a = a;
        this.s = s;
        this.s1 = new StringBuffer();
        this.s1.append(s1);
    }

}