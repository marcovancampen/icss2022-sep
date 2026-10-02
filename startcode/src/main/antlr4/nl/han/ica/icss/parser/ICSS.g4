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


//--- PARSER: ---
propertyValues: PIXELSIZE|COLOR|PERCENTAGE;
operators:(PLUS|MIN|MUL);
variableNames: LOWER_IDENT|CAPITAL_IDENT;
statement: property | ifStatement | var;
varValue: PIXELSIZE|COLOR|PERCENTAGE|TRUE|FALSE|SCALAR|variableNames;
expression: (variableNames (comparisonOperators (varValue))?);
condition: expression (logicalOperators|expression)*;


value: (variableNames|propertyValues) (operators (SCALAR|variableNames|propertyValues))*;
keyword: LOWER_IDENT COLON;
property: keyword value SEMICOLON;
element: OPEN_BRACE (statement)* CLOSE_BRACE;
selector: simpleSelector+ element;
simpleSelector : LOWER_IDENT| ID_IDENT | CLASS_IDENT;


var: variableNames ASSIGNMENT_OPERATOR varValue SEMICOLON;

ifStatement: IF BOX_BRACKET_OPEN condition BOX_BRACKET_CLOSE OPEN_BRACE statement* CLOSE_BRACE
             (ELSE OPEN_BRACE statement* CLOSE_BRACE)? ;

stylesheet: (var|selector)* EOF;

