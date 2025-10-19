package Parser;
import java.util.*;
class valuetype{
    enum Type{INT,DOUBLE,FLOAT,BOOLEAN};
    public enum Type1{ADDITION,SUBTRACTION,MULITIPLICATION,DIVISION};
    public enum Type2{GREATER,LESSER,EQUAL};
    String value;
    Type t ;
    Type1 t1;
    Type2 t2;
    public valuetype(String value,Type t){
        this.t = t;
        this.value = value;
    }
    public valuetype(String value,Type1 t){
        this.t1 = t;
        this.value = value;
    }
    public valuetype(String value,Type2 t){
        this.t2 = t;
        this.value = value;
    }
    public valuetype(){

    }
}
class functiontype{
    enum Type{INT,DOUBLE,FLOAT,BOOLEAN,VOID,DEFAULT};
    String value;
    Type t;
    public functiontype(String value,Type t){
        this.t = t;
        this.value = value;
    }
}



