package parser;

import java.util.ArrayList;
import java.util.Iterator;

import errlog.ErrLog;
import scanner.*;
// AST 是函数绘图语言所有结构设计的抽象语法树（结点）的类型
// 它有 3 个直接子类：
//   AST_expression: 所有表达式对应结点的共同基类
//   AST_statement : 所有语句之树根结点的共同类
//   AST_stmt_list : 为全部输入构造的语法树（一个语句列表）

abstract public class AST
{
	protected Token mToken;
	protected ArrayList<AST> children;
	
	AST(Token t){mToken = t;children = new ArrayList<AST>();}  //一个容器传给一个空指针...看不懂
	
	//abstract ~AST();  //java不需要析构函数
	
	public abstract double value(); // 计算子树对应表达式的值
	
	public AST getChild(int idx)  // 返回第 idx+1 个孩子
	{
		if(idx >= children.size())
			return null;
		else
			return children.get(idx);
	}
	
	public int children_count()// 子树的数量
	{return children.size();}
	
	public double childValue(int idx)  // 返回第 idx+1 个孩子的值
	{
		AST C = getChild(idx);
		return (C == null ?0.0:C.value());
	}
	
	public void print(int ident)  // 打印 ( 子 ) 树
	{
		for(int i = 0;i < ident ; i++)
			ErrLog.printLog(" ");   // 缩进
		ErrLog.printLog(token().getLexeme() + "\n");
		Iterator<AST> itr = children.iterator();
		for(;itr.hasNext();)
			itr.next().print(ident + 2);
	}
	
	public Token token(){return mToken;}; // 获得结点中的记号 
	public TokenType kind() {return mToken.getType();}
	
}


abstract class AST_expression extends AST  //表达式抽象类
{
	public AST_expression (Token t){super(t);}
}


abstract class AST_binary extends AST_expression //二元运算抽象基类
{
	public AST_binary(Token t,AST L,AST R) 
	{
		super(t);
		children.add(L);  //push_back的意思就是给尾部加一条数据
		children.add(R);
	}
	
	protected double leftValue(){return childValue(0);}
	protected double rightValue(){return childValue(1);}
}

class AST_plus extends AST_binary //二元加
{
	public AST_plus(Token t,AST L,AST R){super(t,L,R);}
	public double value() {return leftValue() + rightValue();}
}

// *AST_minus 提取成为一个公共类

class AST_mutiple extends AST_binary
{
	public AST_mutiple(Token t, AST L, AST R){super(t,L,R);}
	public double value() {return leftValue() * rightValue();}
}

class AST_div extends AST_binary
{
	public AST_div(Token t, AST L, AST R){super(t,L,R);}
	public double value() {return leftValue() / rightValue();}
}

class AST_power extends AST_binary
{
	public AST_power(Token t, AST L, AST R){super(t,L,R);}
	public double value(){return Math.pow(leftValue(), rightValue());}
}

abstract class AST_unary extends AST_expression
{
	public AST_unary(Token t,AST C) {super(t);children.add(C);}
	protected double operand() {return childValue(0);}
}


class AST_func extends AST_unary//函数调用结点：仅有一个参数，即 一元运算
{
	public AST_func(Token t,AST child) {super(t,child);}
	public double value() 
	{
		double temp = 0;  //可能有错误
		switch(mToken.getFunc())
		{
		case sin:temp = Math.sin(operand());break;
		case cos:temp = Math.cos(operand());break;
		case tan:temp = Math.tan(operand());break;
		case sqrt:temp = Math.sqrt(operand());break;
		case exp:temp = Math.exp(operand());break;
		case log:temp = Math.log(operand());break;
		}
		return temp;
	}
}

abstract class AST_leaf extends AST_expression  //叶子节点的类型
{
	AST_leaf (Token t){super(t);}
}


// *AST_T 提取成为一个公共类

// *AST_const 提取成为一个公共类


// *AST_statement 提取成为一个公共类


// *AST_stmt_list 提取成为一个公共类
