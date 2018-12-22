package parser;

import scanner.Token;

public class AST_minus extends AST_binary
{
	public AST_minus(Token t,AST L,AST R){super(t,L,R);}
	public double value() {return leftValue() - rightValue();}
}