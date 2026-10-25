grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';
EQUALS : '==';
NOTEQUALS: '!=';
SMALLER: '<';
BIGGER: '>';
EQUALSSMALLER: '<=';
EQUALSBIGGER: '>=';
AND: '&&';
OR: '||';



comparisonOperators: SMALLER|BIGGER|EQUALS|NOTEQUALS|EQUALSBIGGER|EQUALSSMALLER;
logicalOperators: AND|OR;


//--- PARSER: ---;
operators1:MUL #multiplyOperation;
operators2:PLUS #addOperation
|MIN #subtractOperation ;

varValue: literals
| boolLiteral
| scalarLiteral
| variableReference;
scalarLiteral: SCALAR;
boolLiteral: TRUE|FALSE;

expression: (LOWER_IDENT|CAPITAL_IDENT(comparisonOperators (varValue))?);
calculation: calculation operators1 calculation #multiplyCalculation
| calculation operators2 calculation #addSubtractCalculation
| literals #literalCalculation
| variableReference #variableCalculation
| scalarLiteral #scalarCalculation;
//
value: calculation;

propertyName: LOWER_IDENT;

literals: PIXELSIZE #pixelLiteral
| COLOR #colorLiteral
| PERCENTAGE #percentageLiteral
;


property: propertyName COLON (value) SEMICOLON;
declaration: property;
stylerule: selector OPEN_BRACE (declaration|ifStatement|variableAssignment)* CLOSE_BRACE;
selector: LOWER_IDENT #tagSelector
| ID_IDENT #idSelector
| CLASS_IDENT #classSelector;

ifCondition: BOX_BRACKET_OPEN (variableReference|boolLiteral) BOX_BRACKET_CLOSE;
ifElseBlock:OPEN_BRACE (declaration | ifStatement|variableAssignment)* CLOSE_BRACE
             (elseBlock)?;
elseBlock: ELSE OPEN_BRACE (declaration | ifStatement|variableAssignment)* CLOSE_BRACE;

variableReference: CAPITAL_IDENT;


variableAssignment: variableReference ASSIGNMENT_OPERATOR varValue SEMICOLON ;

ifStatement: IF ifCondition ifElseBlock;

stylesheet: (variableAssignment|stylerule)* EOF;

