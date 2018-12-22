package semantics;

/**
 * �������ģ��
 * @author WangJiaxiang 16130120131 752097910@qq.com
 *
 */

import errlog.*;	// ������
import parser.*;	// ʹ��AST�﷨���ṹ���乫�������ӿ�
import scanner.BasicToken;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;
import ui_main.WinGUI;		// ����UI��ͼ
import ui_main.WinGUI.t_color;		// ��ɫ

import java.math.*;


// ���������
// ���﷨���������� �﷨�� Ϊ���룬�������
// �����Ĺ����м��㡢��ͼ
public class SemanticAnalyzer 
{
	
	private AST_stmt_list theProgram;
	private Parser theParser;
	
	private t_color Color;
	private double Origin_x, Origin_y;
	private double Scale_x, Scale_y;
	private double rot_angle;
	private double myT_storage;
	
	// ���㱻���Ƶ������
	private void CalcCoord(AST x_tree, AST y_tree, double ptr_x_value, double ptr_y_value)
	{
		double x_val, x_temp, y_val;
		
		// ������ʽ��ֵ���õ����ԭʼ����
		x_val = (x_tree).value();
		y_val = (y_tree).value();
		
		// �����任
		x_val *= Scale_x;
		y_val *= Scale_y;
		
		// ��ת�任
		x_temp = x_val*Math.cos(rot_angle) + y_val*Math.sin(rot_angle);
		y_val = y_val*Math.cos(rot_angle) - x_val*Math.sin(rot_angle);
		x_val = x_temp;
		
		// ƽ�Ʊ任
		x_val += Origin_x;
		y_val += Origin_y;
		
		// ���ر仯��������
		// ��װ��Double�����ж�null
		if((Double)ptr_x_value != null)
			ptr_x_value = x_val;
		if((Double)ptr_y_value != null)
			ptr_y_value = y_val;
			
	}
	
	// ѭ�����Ƶ������
	private void Drawloop(
			AST start_tree, AST end_tree,
			AST step_tree, AST x_tree, AST y_tree)
	{
		double x_val = 0, y_val = 0;
		double start_val, end_val, step_val;	// ��ͼ��㡢�յ㡢����
		double p_T_value = theParser.getTmemory().parameter;	// parser manager it
		start_val = start_tree.value();	// ���������ʽ��ֵ
		end_val = end_tree.value();	// �����յ���ʽ��ֵ
		step_val = step_tree.value();	// ���㲽�����ʽ��ֵ
		
		for(p_T_value = start_val; p_T_value <= end_val; p_T_value += step_val)
		{
			
			CalcCoord(x_tree, y_tree, x_val, y_val);
			System.out.println("----------------------------");
			System.out.println("x: "+x_val+" y: "+y_val);
		//	WinGUI.DrawPixel(x_val, y_val, Color);
		}
	}
	
	public SemanticAnalyzer(Parser p)
	{
		theParser = p;
		theProgram = p.getStmts();
		theParser.setTmemory(myT_storage);
		myT_storage = 0;
		
//		Color.setValue(255, 0, 0);	// default color is RED
		Origin_x = 0; Origin_y = 0;
		Scale_x = 1; Scale_y = 1;
		rot_angle = 0;
	}
	
	public int run() 
	{
		// �������ļ���
		if((theProgram = theParser.getStmts()) == null)
			return -1;
		
		AST_statement stmt = null;
		Zorro();
		int n_stmt = theProgram.children_count();
		for(int i = 0; i < n_stmt; ++i) 
		{
			stmt = theProgram.getChild(i);
			switch(stmt.kind())
			{
			// childValue �������� int
			case ORIGIN:
				System.out.println("origin.");
				Origin_x = stmt.childValue(0);
				Origin_y = stmt.childValue(1);
				break;
			case SCALE:
				System.out.println("scale.");
				Scale_x = stmt.childValue(0);
				Scale_y = stmt.childValue(1);
				break;
			case ROT:
				System.out.println("rot.");
				rot_angle = stmt.childValue(0);
				break;
			case FOR:
				System.out.println("for.");
				Drawloop(stmt.getChild(0), stmt.getChild(1), stmt.getChild(2), stmt.getChild(3), stmt.getChild(4));
				break;
			case COLOR:
				System.out.println("color.");
				Color.setValue(
						(char)stmt.childValue(0),
						(char)stmt.childValue(1),
						(char)stmt.childValue(2));
				break;
				default:
					break;
			
			}
			
		}
		return 0;
	}
	
	// ��ȡ�ļ���
	public String getFilename() 
	{
		if(theParser == null)
			return null;
		else
			return theParser.getFilename();
	}
	
	// *** ����
	private void Zorro() 
	{
		AST_stmt_list stmts = theProgram;
		if(null == stmts)
			return;
		
		int x = 0, y = 1;
		int sz = 12;
		BasicToken tv = new BasicToken(TokenType.FOR, "FOR", 0, null);
		Token tk = new Token(tv);
		AST_statement stmt = new AST_statement(tk);
		tv.type = TokenType.CONST_ID;	tv.value = x;
		tk.setBasic(tv); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(x+sz); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(1); 	stmt.addExpression(new AST_const(tk));
		Tclass referTemp = new Tclass(myT_storage);		// ����
		tk.setType(TokenType.T);	stmt.addExpression(new AST_T(tk, referTemp));
		tk.setType(TokenType.CONST_ID); 	stmt.addExpression(new AST_const(tk));
		stmts.addStmt(stmt, 0);
		
		tk.setType(TokenType.FOR); 	stmt = new AST_statement(tk);
		tv.type = TokenType.CONST_ID; 	tv.value = x+sz/4;
		tk.setBasic(tv); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(x+sz-sz/4); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(1); 	stmt.addExpression(new AST_const(tk));
		tk.setType(TokenType.T); 	stmt.addExpression(new AST_T(tk, referTemp));
		tk.setType(TokenType.CONST_ID); 	tk.setValue(y+sz/2); 	stmt.addExpression(new AST_const(tk));
		stmts.addStmt(stmt, 0);
		
		tk.setType(TokenType.FOR);  	stmt = new AST_statement(tk);
		tv.type = TokenType.CONST_ID; 	tv.value = x;
		tk.setBasic(tv); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(x+sz); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(1); 	stmt.addExpression(new AST_const(tk));
		tk.setType(TokenType.T); 	stmt.addExpression(new AST_T(tk, referTemp));
		tk.setType(TokenType.CONST_ID); 	tk.setValue(y+sz); 		stmt.addExpression(new AST_const(tk));
		stmts.addStmt(stmt, 0);
		
		tk.setType(TokenType.FOR); 	stmt = new AST_statement(tk);
		tv.type = TokenType.CONST_ID; 	tv.value = x;
		tk.setBasic(tv); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(x+sz); 	stmt.addExpression(new AST_const(tk));
		tk.setValue(1); 	stmt.addExpression(new AST_const(tk));
		tk.setType(TokenType.T);	stmt.addExpression(new AST_T(tk, referTemp));
		tk.setType(TokenType.CONST_ID);		tk.setValue(y+sz);
		AST l = new AST_const(tk);
		tk.setType(TokenType.T);
		AST r = new AST_T(tk, referTemp);
		tk.setType(TokenType.MINUS); 	stmt.addExpression(new AST_minus(tk, l, r));
		stmts.addStmt(stmt, 0);
		
		
		
	}
	
	// �������������
	public static void doInterpret(String file_name)
	{
		AST_stmt_list stmts = null;
		
		ErrLog.restart(true);
		
		Scanner theScanner = new Scanner();
		if( theScanner.initScanner(file_name) == 0 )
			return;
		
		Parser theParser = new Parser(theScanner);	// ������ָ��һ���ʷ�������
		SemanticAnalyzer treeWalker = new SemanticAnalyzer(theParser);
		
		try
		{
			stmts = theParser.run();
			if(stmts != null)
			{
				treeWalker.run();
				// delete stmts;
			}
		}
		catch(Exception e)
		{			
		}
		
		ErrLog.restart(false);
	}

	
	public static void main(String[] args) 
	{
		SemanticAnalyzer.doInterpret("ScannerTest.txt");
	}
}


