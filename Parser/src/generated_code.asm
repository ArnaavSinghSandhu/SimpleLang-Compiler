=== Function:  ===
add:
ADD R5 R3 R4
main:
MOV R6, 5
MOV R7, 10
MOV ARG0,R10
MOV ARG1,R11
CALL add
MOV R9, R0
MOV R12, 2
MUL R8 R9 R12

========================

=== Function: add ===
add:
ADD R5 R3 R4

========================

=== Function: main ===
main:
MOV R6, 5
MOV R7, 10
MOV ARG0,R10
MOV ARG1,R11
CALL add
MOV R9, R0
MOV R12, 2
MUL R8 R9 R12

========================

Symbol Table for function: add
  a -> R3
  b -> R4

Symbol Table for function: main
  x -> R10
  y -> R11

