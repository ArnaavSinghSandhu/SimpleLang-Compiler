Custom Expression Compiler & x86 Code Generator 🖥️⚡

A lightweight compiler that parses a custom programming language, generates an abstract syntax tree (AST), and produces x86-style assembly code. Designed for learning compiler design and experimenting with recursive expression evaluation, function calls, and arithmetic operations.

Features

Supports function definitions and calls

Arithmetic expressions: +, -, *, /

Variable assignments and type tracking

Recursive AST traversal for code generation

Generates readable x86-like assembly for debugging and learning

Handles return statements and expression evaluation in functions

OUTPUT:-
<img width="1679" height="961" alt="Screenshot 2025-10-26 at 12 09 33 PM" src="https://github.com/user-attachments/assets/f0a2001a-3980-494f-acac-592fdf2e2b56" />


How It Works

Parsing: Converts your custom language into a tree of statements and expressions.

Symbol Table Management: Tracks variables and function scopes.

Recursive Code Generation: Converts the AST into x86-style instructions.

Function Calls: Supports arguments, return values, and nested expressions.

Getting Started

Clone the repo:

gh repo clone ArnaavSinghSandhu/SimpleLang-Compiler
cd Parser/src;


Compile the project:

javac Parser/*.java


Run the compiler with an cli interface:

java Parser.Main


Check generated_code.asm for your output assembly.

Future Improvements

Add loops and conditionals (if, while)

Optimize register allocation

Support more complex data types

Generate real x86 machine code

Why This Project?

Learn the fundamentals of compiler design

Understand AST traversal and code generation

Experiment with assembly-level thinking in a safe, educational environme
