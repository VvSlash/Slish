grammar Slish;

@header {
package pl.edu.pw.slish;
}

// Parser Rules
program: statement* EOF;

statement
    : declaration
    | assignment
    | pipelineExpr
    | conditionalStatement
    | whileLoop
    | forLoop
    | returnStmt
    | printStmt
    | COMMENT
    ;

declaration
    : '/' (type | UNDERSCORE) IDENTIFIER declarationBody?    # TypedDeclaration
    | '/' IDENTIFIER                                  # FunctionReference
    ;

declarationType
    : 'fun'                   # FunDecl
    | 'data'                  # DataDecl
    ;

declarationBody
    : '(' paramList? ')' funcBody?                   # ParameterDeclarationBody
    | '{' field* '}'                                 # DataTypeBody
    | '=' expression                                 # AssignmentBody
    ;

funcBody
    : '=' block
    | block
    ;

paramList
    : typedParam (',' typedParam)*
    ;

typedParam
    : '/' type IDENTIFIER
    ;

field
    : IDENTIFIER ':' type
    ;

pipelineExpr
    : pipeElement ('|' pipeElement)*
    ;

pipeElement
    : expression                                     # ExpressionPipeElement
    | typeCast                                       # TypeCastPipeElement
    | '/' (type | UNDERSCORE) IDENTIFIER '=' UNDERSCORE # VariableAssignPipeElement
    | '/if' '=' block                                # IfPipeElement
    | '/ret'                                         # ReturnPipeElement
    ;

typeCast
    : '/' type
    ;

assignment
    : declaration '=' expression
    | IDENTIFIER '=' expression
    ;

conditionalStatement
    : '/if' '(' expression ')' block elseBlock?
    ;

elseBlock
    : '/else' block
    | '/else' conditionalStatement
    ;

whileLoop
    : '/while' '(' expression ')' block
    ;

forLoop
    : '/for' '(' (declaration | assignment)? ';' expression? ';' (expression | assignment)? ')' block
    | '/for' '(' expression ')' block  // Dla iteracji po tablicy/kolekcji
    ;

returnStmt
    : '/ret' expression?
    ;

printStmt
    : '/print' '(' argumentList? ')'
    ;

block
    : '{' statement* '}'
    ;

// Updated expression rules with proper precedence (highest to lowest)
expression
    : 'not' expression                                          # NotExpr
    | expression op=('*' | '//' | '%') expression               # MulDivExpr
    | expression op=('+' | '-') expression                      # AddSubExpr
    | expression op=('>' | '<' | '>=' | '<=') expression        # RelationalExpr
    | expression op=('==' | '!=') expression                    # EqualityExpr
    | expression op='xor' expression                            # XorExpr
    | expression op='and' expression                            # AndExpr
    | expression op='or' expression                             # OrExpr
    | '(' expression ')'                                        # ParenExpr
    | literal                                                   # LiteralExpr
    | IDENTIFIER                                                # IdentifierExpr
    | declaration '(' argumentList? ')'                         # FunctionCallExpr
    | stringInterpolation                                       # StringInterpExpr
    | IDENTIFIER '[' expression ']'                             # ArrayAccessExpr
    | '/read'                                                   # ReadExpr
    | arrayLiteral                                              # ArrayLiteralExpr
    ;

arrayLiteral
    : '[' (expression (',' expression)*)? ']'
    ;

argumentList
    : expression (',' expression)*
    ;

stringInterpolation
    : STRING_START (interpolationPart STRING_MIDDLE)* STRING_END
    | STRING_WITH_INTERPOLATION
    ;

interpolationPart
    : '{' (expression | assignment) '}'
    ;

type
    : 'int'                                       # IntType
    | 'float'                                     # FloatType
    | 'string'                                    # StringType
    | 'bool'                                      # BoolType
    | 'void'                                      # VoidType
    | 'fun'                                       # FunctionType
    | type '[' ']'                                # ArrayType
    | IDENTIFIER                                  # CustomType
    ;

operator
    : '+'
    | '-'
    | '*'
    | '//'
    | 'and'
    | 'or'
    | 'xor'
    | '>'
    | '<'
    | '>='
    | '<='
    | '=='
    | '!='
    ;

literal
    : INTEGER
    | FLOAT
    | STRING
    | BOOL
    | 'null'
    ;

// Lexer Rules
UNDERSCORE: '_';

COMMENT: '#' ~[\r\n]* -> skip;
WS: [ \t\r\n]+ -> skip;

INTEGER: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]+;
BOOL: 'true' | 'false';
STRING: '"' (~["{}\r\n] | '\\"')* '"';
STRING_WITH_INTERPOLATION: '"' (~["{}\r\n] | '\\{' | '\\}' | '{' ~[}]* '}')* '"';
STRING_START: '"' (~["{}\r\n] | '\\{')* '{';
STRING_MIDDLE: '}' (~["{}\r\n] | '\\{')* '{';
STRING_END: '}' (~["{}\r\n] | '\\"')* '"';

IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;