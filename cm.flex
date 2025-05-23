/*
  Created By: Fei Song
  File Name: tiny.flex
  To Build: jflex tiny.flex

  and then after the parser is created
    javac Lexer.java
*/
   
/* --------------------------Usercode Section------------------------ */
   
import java_cup.runtime.*;
      
%%
   
/* -----------------Options and Declarations Section----------------- */
   
/* 
   The name of the class JFlex will create will be Lexer.
   Will write the code to the file Lexer.java. 
*/
%class Lexer
%public
%{
   private String fileName;
   public boolean invalid_lex = false;

   public Lexer(java.io.Reader in, String fileName) {
      this(in);
      this.fileName = fileName;
   }
%}

%eofval{
  return null;
%eofval};

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column
    
/* 
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup
   
/*
  Declarations
   
  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.  
*/
%{   
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        // l=yyline
        // r=yycolumn   
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        // l=yyline
        // r=yycolumn   
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   

/*
  Macro Declarations
  
  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.  
*/
   
/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or form feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]
mutliline_comment = "/\*""\*/"
/* A literal integer is is a number beginning with a number between
   one and nine followed by zero or more numbers between zero and nine
   or just a zero.  */
digit = [0-9]
number = {digit}+
truth = "true" | "false"

/* A identifier integer is a word beginning a letter between A and
   Z, a and z, or an underscore followed by zero or more letters
   between A and Z, a and z, zero and nine, or an underscore. */
identifier = [_a-zA-Z][_a-zA-Z0-9]*
bad_identifier = [0-9][_a-zA-Z0-9]*

   
%%
/* ------------------------Lexical Rules Section---------------------- */
   
/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

"bool"                        { return symbol(sym.BOOL); }
"if"                          { return symbol(sym.IF); }
"else"                        { return symbol(sym.ELSE); }
"int"                         { return symbol(sym.INT); }
"return"                      { return symbol(sym.RETURN); }
"void"                        { return symbol(sym.VOID); }
"while"                       { return symbol(sym.WHILE); }
{truth}                       { return symbol(sym.TRUTH, yytext().equals("true")); }
"+"                           { return symbol(sym.ADD); }
"-"                           { return symbol(sym.SUB); }
"*"                           { return symbol(sym.MULT); }
"/"                           { return symbol(sym.DIV); }
"<"                           { return symbol(sym.LT); }
">"                           { return symbol(sym.GT); }
"<="                          { return symbol(sym.LTE); }
">="                          { return symbol(sym.GTE); }
"=="                          { return symbol(sym.EQ); }
"!="                          { return symbol(sym.NEQ); }
"~"                           { return symbol(sym.NOT); }
"&&"                          { return symbol(sym.AND); }
"||"                          { return symbol(sym.OR); }
"="                           { return symbol(sym.ASSIGN); }
";"                           { return symbol(sym.SEMI); }
"("                           { return symbol(sym.LPAREN); }
")"                           { return symbol(sym.RPAREN); }
"["                           { return symbol(sym.LBRACKET); }
"]"                           { return symbol(sym.RBRACKET); }
"{"                           { return symbol(sym.LBRACE); }
"}"                           { return symbol(sym.RBRACE); }
","                           { return symbol(sym.COMMA); }
{number}                      { return symbol(sym.NUM, Integer.parseInt(yytext())); }
{identifier}                  { return symbol(sym.ID, yytext()); }
{bad_identifier}              { return symbol(sym.ERROR_TOKEN, yytext()); }
{WhiteSpace}+                 { /* skip whitespace */ }   
"//".*                        { /* Skip single-line comments */ }
"/*" !([^]* "*/" [^]*) "*/"   { /* Skip multi-line comments */ }
"/*" !([^]* "*/" [^]*)        { // Catch any other invalid character
                                 invalid_lex = true;
                                 System.err.println(this.fileName + ":" + (yyline + 1) + ":" + (yycolumn + 1) + ": Unterminated comment");}
[^]                           { // Catch any other invalid character
                                 invalid_lex = true;
                                 System.err.println(this.fileName + ":" + (yyline + 1) + ":" + (yycolumn + 1) + ": Invalid character '" + yytext() + "'");}
