package parser;

import scanner.Token;

//������乹�ɵ�������
//�����ĵ� 1 �㺢�Ӷ���  AST_statement  ����
public class AST_stmt_list extends AST  
{
	public AST_stmt_list(Token t) {super(t);}  // ������ű��������ļ���
	public double value() {return 0.0;}
	public AST_statement getChild(int idx)    // ���ص� idx+1 �����
	{
		if( idx >= children.size() )
			return null;
		else
			return (AST_statement) children.get(idx);
	}
	public void addStmt(AST_statement stmt)// ����һ�����(Ĭ��where��1�����)
	{
		children.add(stmt);
	}
	//Ӧ����1�ǲ��ڶ�β���������ֲ��ڶ�ͷ
	public void addStmt(AST_statement stmt, int where)// ����һ�����
	{children.add(0,stmt);}
}