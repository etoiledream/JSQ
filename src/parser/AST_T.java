package parser;

import scanner.Token;

public class AST_T extends AST_leaf  //����һ��ָ��һ��ֵ��ָ��
{
	private Tclass ptr;    //�����ptr��һ������
	public AST_T(Token t, Tclass p){super(t); ptr = p;}
	public double value(){return (ptr == null)?0:ptr.parameter;}  //��Java�����ò���ֵ��Ĭ��0�ģ�
	void setStorage(double p){ptr.parameter = p;}
};
