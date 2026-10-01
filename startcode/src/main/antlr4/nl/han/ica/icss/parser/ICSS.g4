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




//--- PARSER: ---
propertyValues: PIXELSIZE|COLOR|PERCENTAGE;
operators:(PLUS|MIN|MUL);


value: (variableNames|propertyValues) (operators (SCALAR|variableNames|propertyValues))*;

keyword: LOWER_IDENT COLON;
property: keyword value SEMICOLON;
element: OPEN_BRACE (property)* CLOSE_BRACE;
selector: simpleSelector+ element;
simpleSelector : LOWER_IDENT| ID_IDENT | CLASS_IDENT;

varValue: PIXELSIZE|COLOR|PERCENTAGE|TRUE|FALSE;

variableNames: LOWER_IDENT|CAPITAL_IDENT;

var: variableNames ASSIGNMENT_OPERATOR varValue SEMICOLON;

stylesheet: var* selector* EOF;

