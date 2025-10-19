SimpleLang Compiler

Description:
SimpleLang is a custom language front-end compiler written in Java. It currently implements parsing, semantic analysis, and type checking for integer and boolean types. The compiler supports function definitions, expressions with operators (+ - * /), and maintains a symbol table for variables and functions. Lexer and code generation stages are yet to be implemented, making this project a work-in-progress.

Features Implemented:

Construction of Parse Trees and Abstract Syntax Trees (AST)

Expression parsing with operator precedence and parentheses

Semantic analysis and type checking for variables, functions, and expressions

Symbol table and scope management

Function handling with return type validation

Planned Features:

Lexer for tokenizing input source code

Code generation for converting AST to executable code

Usage:
Currently, the project runs as a Java program that parses tokens and checks types. Full compilation to executable code is under development.

License:
Private. Redistribution or modification is not allowed.
