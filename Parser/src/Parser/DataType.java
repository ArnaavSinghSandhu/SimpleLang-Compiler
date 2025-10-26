package Parser;
import java.util.*;
class valuetype{
    enum Type{INT,DOUBLE,FLOAT,BOOLEAN,DEFAULT};
    public enum Type1{ADDITION,SUBTRACTION,MULITIPLICATION,DIVISION};
    public enum Type2{GREATER,LESSER,EQUAL};
    public enum Type3{EQUALTO};
    String value;
    Type t ;
    Type1 t1;
    Type2 t2;
    Type3 t3;
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
    public valuetype(String value,Type3 t){
        this.t3 = t;
        this.value = value;
    }
    public valuetype(){

    }
}
class functiontype{
    valuetype.Type t;
    String value;
    public functiontype(String value,valuetype.Type t){
        this.t = t;
        this.value = value;
    }
}



