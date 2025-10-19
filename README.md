SimpleLang Compiler

Description:
SimpleLang is a custom compiler front-end written in Java. It currently implements parsing, semantic analysis, and type checking for integer and boolean types. The compiler supports function definitions, arithmetic and comparison expressions, and maintains a symbol table to track variables and function scopes. Lexer and code generation stages are planned but not yet implemented.

Features Implemented

Parse Tree Construction

Parses a list of tokens into a hierarchical Parse Tree structure.

Supports nested statements and expressions.

Abstract Syntax Tree (AST) Generation

Converts the Parse Tree into an AST suitable for semantic analysis.

Handles arithmetic expressions with proper operator precedence (+ - * /) and parentheses.

Semantic Analysis

Checks variable declarations and usage.

Supports function definitions and verifies return types.

Ensures type consistency across expressions and assignments.

Symbol Table and Scope Management

Maintains symbol tables for each function scope.

Tracks variables, functions, and operators separately.

Ensures identifiers are declared before usage.

Expression Evaluation Preparation

Prepares nodes for potential code generation.

Handles binary operations and comparisons between variables and constants.

Planned Features

Lexer: Tokenization of source code into meaningful tokens.

Code Generation: Transform AST into executable code or bytecode.

Usage

Written entirely in Java and can be executed using standard Java commands.

Currently, the compiler front-end reads tokens, builds ASTs, and performs semantic checks.

Full compilation to executable code will be available after the lexer and code generation stages are implemented.

License

Private. Redistribution or modification is not allowed.
