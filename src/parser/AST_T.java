package parser;

import scanner.Token;

public class AST_T extends AST_leaf  //这是一个指向一个值的指针
{
	private Tclass ptr;    //这里的ptr是一个引用
	public AST_T(Token t, Tclass p){super(t); ptr = p;}
	public double value(){return (ptr == null)?0:ptr.parameter;}  //在Java里引用不赋值是默认0的？
	void setStorage(double p){ptr.parameter = p;}
};
