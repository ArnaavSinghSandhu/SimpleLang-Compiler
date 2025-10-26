=== Function: func ===
add:
ADD R5 R3 R4
R5
main:
MOV R6, 5
R6
MOV R7, 10
R7
CALL add
MOV R8, R0
R8

========================

=== Function:  ===
add:
ADD R5 R3 R4
R5
add:
ADD R5 R3 R4
R5
main:
MOV R6, 5
R6
MOV R7, 10
R7
CALL add
MOV R8, R0
R8

========================

Symbol Table for function: func
  a -> R3
  b -> R4

