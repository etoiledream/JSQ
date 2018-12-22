package parser;

import scanner.Token;

//所有语句构成的完整树
//该树的第 1 层孩子都是  AST_statement  对象
public class AST_stmt_list extends AST  
{
	public AST_stmt_list(Token t) {super(t);}  // 根结点存放被分析的文件名
	public double value() {return 0.0;}
	public AST_statement getChild(int idx)    // 返回第 idx+1 条语句
	{
		if( idx >= children.size() )
			return null;
		else
			return (AST_statement) children.get(idx);
	}
	public void addStmt(AST_statement stmt)// 增加一个语句(默认where是1的情况)
	{
		children.add(stmt);
	}
	//应该是1是插在队尾，其他数字插在队头
	public void addStmt(AST_statement stmt, int where)// 增加一个语句
	{children.add(0,stmt);}
}