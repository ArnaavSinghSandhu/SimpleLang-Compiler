parser grammer;

Program : statement + EOF;
statement
    : functional
    | assignment
    | conditional
    | loop
    | RETURN functional END
    | RETURN expression END
    ;
functional : FUNC IDENTIFIER'(' ASSIGNMENTKEYWORDS IDENTIFIER (',' ASSIGNMENTKEYWORDS IDENTIFIER)*')' body;
function : FUNC IDENTIFIER '(' NUMBER (',' NUMBER)*')';
body : '{ statement*'}';
assignment : ASSIGNMENTKEYWORDS IDENTIFIER '=' expression END ;
loop : LOOP conditional body ;
conditional : CONDITION '(' expression (LOGICAL expression)*')' body(CONDITON body)* ;
expression : term (HIGHEROPERATOR term)* | term (OPERATOR term)* | term COMPARATOR term ;
term : NUMBER | IDENTIFIER | '( expression ')' ;


RETURN : 'return';
LOGICAL : '&&'|'||';
HIGHEROPERATOR : '*'|'/'|'%';
OPERATOR : '+'|'-';
LOOP : 'while'|'for';
FUNC : 'fun';
CONDITION : 'if'|'else';
IDENTIFIER  : [a-zA-Z_][a-zA-Z0-9_]* ;
ASSIGNMENTKEYWORDS : 'int'|'bool'|'var'|'let';
NUMBER : [0-9]+ ;
WS  : [ \t\r\n]+ -> skip ;
END : ';';