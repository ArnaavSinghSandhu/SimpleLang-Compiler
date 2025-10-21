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
    public address addressgiver(){
        Random r = new Random();
        int n = r.nextInt();
        String hexdecimal = Integer.toHexString(n);
        return new address(hexdecimal);
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
    }
    public void symbolTable1(executor e,TreeNode root){
        e.functionalvisitor(root,e.symbolTableorg.get(this.f),e.operatorTable);
        if (this.symboltableorg == null)
        this.symboltableorg = new HashMap<>();
        symboltableorg.put(f,e.symbolTableorg.get(this.f));
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