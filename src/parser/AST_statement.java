package parser;

import scanner.Token;

public class AST_statement extends AST
{
	public AST_statement(Token t) {super(t);}
	public double value(){return 0.0;}
	public void addExpression(AST expTree){children.add(expTree);}
}