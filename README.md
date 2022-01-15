# CS112-DataStruct-Project-2-Expression-Evaluation

Expressions

Here are some sample expressions of the kind your program will evaluate:

   Xyz
   
   3-4*5
   
   a-(b+A[B[2]])*d+3
   
   A[2*(a+b)]
   
   (varx + vary*varz[(vara+varb[(a+b)*33])])/55
   
The expressions will be restricted to the following components:

Integer constants

Simple (non-array) variables with integer values

Arrays of integers, indexed with a constant or a subexpression

Addition, subtraction, multiplication, and division operators, i.e. '+','-','*','/' Parenthesized subexpressions

Note the following:

Subexpressions (including indexes into arrays between '[' and ']') may be nested to any level

Multiplication and division have higher precedence than addition and subtraction

Variable names (either simple variables or arrays) will be made up of one or more letters ONLY (nothing but letters a-z and A-Z), are case sensitive (Xyz is different from xyz) and will be unique.

Integer constants may have multiple digits

There may any number of spaces or tabs between any pair of tokens in the expression. Tokens are variable names, constants, parentheses, square brackets, and operators.

MORE INFO:
Assignment 2 description.pdf
