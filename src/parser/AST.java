package parser;

import java.util.ArrayList;
import java.util.Iterator;

import errlog.ErrLog;
import scanner.*;
// AST �Ǻ�����ͼ�������нṹ��Ƶĳ����﷨������㣩������
// ���� 3 ��ֱ�����ࣺ
//   AST_expression: ���б��ʽ��Ӧ���Ĺ�ͬ����
//   AST_statement : �������֮�������Ĺ�ͬ��
//   AST_stmt_list : Ϊȫ�����빹����﷨����һ������б�

abstract public class AST
{
	protected Token mToken;
	protected ArrayList<AST> children;
	
	AST(Token t){mToken = t;children = new ArrayList<AST>();}  //һ����������һ����ָ��...������
	
	//abstract ~AST();  //java����Ҫ��������
	
	public abstract double value(); // ����������Ӧ���ʽ��ֵ
	
	public AST getChild(int idx)  // ���ص� idx+1 ������
	{
		if(idx >= children.size())
			return null;
		else
			return children.get(idx);
	}
	
	public int children_count()// ����������
	{return children.size();}
	
	public double childValue(int idx)  // ���ص� idx+1 �����ӵ�ֵ
	{
		AST C = getChild(idx);
		return (C == null ?0.0:C.value());
	}
	
	public void print(int ident)  // ��ӡ ( �� ) ��
	{
		for(int i = 0;i < ident ; i++)
			ErrLog.printLog(" ");   // ����
		ErrLog.printLog(token().getLexeme() + "\n");
		Iterator<AST> itr = children.iterator();
		for(;itr.hasNext();)
			itr.next().print(ident + 2);
	}
	
	public Token token(){return mToken;}; // ��ý���еļǺ� 
	public TokenType kind() {return mToken.getType();}
	
}


abstract class AST_expression extends AST  //���ʽ������
{
	public AST_expression (Token t){super(t);}
}


abstract class AST_binary extends AST_expression //��Ԫ����������
{
	public AST_binary(Token t,AST L,AST R) 
	{
		super(t);
		children.add(L);  //push_back����˼���Ǹ�β����һ������
		children.add(R);
	}
	
	protected double leftValue(){return childValue(0);}
	protected double rightValue(){return childValue(1);}
}

class AST_plus extends AST_binary //��Ԫ��
{
	public AST_plus(Token t,AST L,AST R){super(t,L,R);}
	public double value() {return leftValue() + rightValue();}
}

// *AST_minus ��ȡ��Ϊһ��������

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


class AST_func extends AST_unary//�������ý�㣺����һ���������� һԪ����
{
	public AST_func(Token t,AST child) {super(t,child);}
	public double value() 
	{
		double temp = 0;  //�����д���
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

abstract class AST_leaf extends AST_expression  //Ҷ�ӽڵ������
{
	AST_leaf (Token t){super(t);}
}


// *AST_T ��ȡ��Ϊһ��������

// *AST_const ��ȡ��Ϊһ��������


// *AST_statement ��ȡ��Ϊһ��������


// *AST_stmt_list ��ȡ��Ϊһ��������
