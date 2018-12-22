package parser;
import errlog.ErrLog;
import scanner.*;
// 1. �÷�����ҪΪȫ����������һ���������﷨�� .
//    ���﷨��������������ʱ�����������Զ����٣��������߱������١�
// 2. ��������ɶԸ��﷨�����ж��ִ������н�����������
//    ���Ǽ�����ʽ��ֵ����ͼ��
public class Parser
{
	
	// �����������ڸ��ٵ��ԣ���ʾ���롢�˳��ĸ����ս���ĺ�����
	private void enter(String x)
	{
		int i;
		for (i = 0; i < indent; ++i) ErrLog.printLog(" ");
			ErrLog.printLog("enter in " + x + "\n");
		indent += 2;
	}
	private void back (String x)
	{
		int i;
		indent -= 2;
		for (i = 0; i < indent; ++i) ErrLog.printLog(" ");
		ErrLog.printLog("exit from " + x + "\n");
	}
	private void tree_trace(AST x)
	{
		PrintSyntaxTree(x, 0);
	}
	
	protected AST_stmt_list ASTroot = null;       // ��������ĳ����﷨��
	protected Tclass ptr_t_storage = new Tclass(0); // T ֵ�Ĵ�ſռ�,�Ǹ�ָ��(��������������)
	protected Tclass parameter = new Tclass(0);   // ÿ�ν���������ı�������T��Ȼ��ѭ��
	protected Token curr_token;   // ��ǰ�Ǻ�
	protected Token lastToken; 	 // ��ǰ�Ǻ�֮ǰ�����һ����ȷ�ļǺ�
	
	void doParse(String file_name) //���Դ���
	{
		ErrLog.restart(true);
		Scanner theScanner = new Scanner();
		if (0 == theScanner.initScanner(file_name))
			return;
		Parser theParser = new Parser(theScanner);
		try
		{
			AST_stmt_list stmts = theParser.run();
			if (stmts != null) stmts = null;
		}
		catch (Exception e)
		{
		}
		ErrLog.restart(false);
	}
	
	private Scanner theScanner;
	private int indent;    //  ��ʾ��Ϣ��������
	private TokenType FetchToken() // ��ȡ�Ǻ�
	{
		TokenType tk = TokenType.ERRTOKEN;  //Ĭ�ϴ��󣬷�ֹ�������
		while (true)
		{
			curr_token = theScanner.getToken();  //�ʹʷ��������Ľ���
			if ((tk = curr_token.getType()) == TokenType.ERRTOKEN)
			{
				ErrLog.printLog("discard ERRtext \" " +curr_token.getLexeme() + "\" at [%d" + curr_token.getPosition().getRow() + "," + curr_token.getPosition().getCol() + "]!\n");
				SyntaxError(1); //һ�����
				continue;
			}
			break;
		}
		return tk;
	}
	private void MatchToken(TokenType expected) // ƥ��Ǻ�
	{
		lastToken = curr_token;
		if (curr_token.getType() == expected)
		{
			for (int i = 0; i < indent; ++i) ErrLog.printLog(" ");
				ErrLog.printLog("matchtoken "+ curr_token.getLexeme() +"\n");
			FetchToken();
		}
		else
		{
			while (true)
			{
				SyntaxError(2);  //�������
				ErrLog.printLog("discard mismatchtoken \"" +curr_token.getLexeme() +"\" at [" + curr_token.getPosition().getRow() + "," + curr_token.getPosition().getCol() + "]!\n");
				TokenType tk = FetchToken();
				if (tk == expected)
				{
					ErrLog.printLog("*** matchtoken " + curr_token.getLexeme() + " after discard~~~\n");
					lastToken = curr_token;
					FetchToken();
					break;
				}
				else if (tk == TokenType.NONTOKEN)
					break;
			}
		}
	}
	private void SyntaxError (int case_of)// ָ���﷨���� ( ���� error_msg)
	{
		switch (case_of)
		{
		case 1:
			ErrLog.errorMessage(curr_token.getPosition().getRow(),
				curr_token.getPosition().getCol(),
				" ",
				curr_token.getLexeme());
			break;
		case 2:
			ErrLog.errorMessage(curr_token.getPosition().getRow(),
				curr_token.getPosition().getCol(),
				curr_token.getLexeme(),
				" ");
			break;
		}
		//exit(1);
	}
	private void PrintSyntaxTree(AST root, int arg_indent)   
	{
		if (null == root) return;
		int L = arg_indent + indent;
		root.print(L);
		return;
	}
	@SuppressWarnings("incomplete-switch")
	private AST_expression MakeExprNode(Token p_rt,AST_expression ... args)   // Ϊ���ʽ�����﷨��
	{
		AST_expression root = null;
		Token rt = p_rt;
		switch (rt.getType())
		{
		case CONST_ID:
			root = new AST_const(rt);break;
		case T:
			root = new AST_T(rt, ptr_t_storage);break;
		case FUNC:
			root = new AST_func(rt, args[0]);break;
		default:
			AST_expression left = args[0];
			AST_expression right = args[1];
			switch (rt.getType())
			{
			case PLUS: root = new AST_plus(rt, left, right); break;
			case MINUS: root = new AST_minus(rt, left, right); break;
			case MUL: root = new AST_mutiple(rt, left, right); break;
			case DIV: root = new AST_div(rt, left, right); break;
			case POWER: root = new AST_power(rt, left, right); break;
			}
			break;
		}
		return root;
	}
	
	protected void program()  //program �ĵݹ��ӳ���
	{
		enter("program");
		while(curr_token.getType() != TokenType.NONTOKEN)
		{
			AST_statement stmt = statement();
			if( null != stmt)
			{
				// ��Ϊ��⵽�﷨����֮�󣬻ᶪ�����ɼǺţ�ֱ��
				//     �����������ļǺŲż�����
				// ���Կ���ֱ���ļ�ĩβҲƥ�䲻�ϣ��Ǿ�Ҫ������ǰ��䡣
				if( curr_token.getType() == TokenType.NONTOKEN )
					stmt = null;  //ԭ����delete stmt;
				else
					ASTroot.addStmt(stmt);
			}
			MatchToken(TokenType.SEMICO);
		}
		back("program");
	}
	
	protected AST_statement statement() //statement �ĵݹ��ӳ���
	{
		 enter ("statement");
		 AST_statement root =null;  //���ڵ�Ϊ�գ�
		 switch (curr_token.getType()) // LL(1)
		 {
		 case ORIGIN :  root = origin_statement();   break;
		 case SCALE  :  root = scale_statement();    break;
		 case ROT    :  root = rot_statement();      break; 
		 case FOR    :  root = for_statement();      break; 
		 case COLOR  :  root = color_statement();    break; 
		 default:  SyntaxError(2);System.out.println("��һ���������⣡");      break;
		 }
		 back ("statement");
		 return root;
	}
	
	protected AST_statement for_statement()
	{
		enter("for_statement");
		
		MatchToken(TokenType.FOR);
		AST_statement root = new AST_statement(lastToken); 
		MatchToken(TokenType.T);
		MatchToken(TokenType.FROM);
			AST ptr = expression();
			root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.TO);
			ptr = expression();
			root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.STEP);
			ptr = expression();
			root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.DRAW);
		MatchToken(TokenType.L_BRACKET);
			ptr = expression();
			root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.COMMA);
			ptr = expression();
			root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.R_BRACKET);
		back("for_statement");
		return root;
	}
	
	protected AST_statement origin_statement()   //�����е����⣡
	{
		enter ("origin_statement");
		MatchToken(TokenType.ORIGIN);
		AST_statement root  = new AST_statement( lastToken );  // ���� ORIGIN �����﷨����
		MatchToken(TokenType.IS);
		MatchToken(TokenType.L_BRACKET);
			AST ptr = expression();
			root.addExpression ( ptr );
				tree_trace(ptr);   // print AST
		MatchToken  (TokenType.COMMA);
			ptr = expression();
			root.addExpression (ptr);
				tree_trace(ptr);   // print AST
		MatchToken  (TokenType.R_BRACKET);	
		back("origin_statement");
		return root;
	}
	
	protected AST_statement rot_statement()
	{
		enter ("rot_statement");
		MatchToken(TokenType.ROT);
		AST_statement root = new AST_statement( lastToken );  // ���� ROT �����﷨����
		
		MatchToken(TokenType.IS);
		AST  ptr  = expression();
			root.addExpression (ptr);
			tree_trace(ptr);   // print AST
		back("rot_statement");
		return root;
		
	}
	
	protected AST_statement scale_statement() 
	{
		enter("scale_statement");
		MatchToken(TokenType.SCALE);
		AST_statement root  = new AST_statement( lastToken );  // ���� SCALE �����﷨����
		
		MatchToken  (TokenType.IS);
		MatchToken  (TokenType.L_BRACKET);
		AST ptr = expression ();
			root.addExpression ( ptr );
				tree_trace(ptr);   // print AST
		MatchToken  (TokenType.COMMA);
			ptr = expression();
			root.addExpression ( ptr );
				tree_trace(ptr);   // print AST
		MatchToken  (TokenType.R_BRACKET);
		
		back("scale_statement");
		return root;
	}
	
	protected AST_statement color_statement()
	{
		enter("color_statement");
		MatchToken(TokenType.COLOR);
		AST_statement root = new AST_statement(lastToken);
		MatchToken(TokenType.IS);
		MatchToken(TokenType.L_BRACKET);
		AST ptr = expression();
		root.addExpression(ptr);
				tree_trace(ptr);
		MatchToken(TokenType.COMMA);
		ptr = expression();
		root.addExpression(ptr);
			tree_trace(ptr);
		MatchToken(TokenType.COMMA);
		ptr = expression();
		root.addExpression(ptr);
			tree_trace(ptr);
		MatchToken(TokenType.R_BRACKET);
		back("color_statement");
		return root;
	}
	
	 // �﷨������ �������õĲ���
	protected AST_expression expression() //expression �ĵݹ��ӳ���
	{
		AST_expression left, right;
		enter("expression");
		left = term();
		while (curr_token.getType() == TokenType.PLUS || curr_token.getType() == TokenType.MINUS)
		{
			Token t = curr_token;
			MatchToken(curr_token.getType());
			right = term();
			left = MakeExprNode(t, left, right);
		}
		back("expression");
		return left;
	}
	protected AST_expression term()
	{
		AST_expression left, right;
		left = factor();
		while (curr_token.getType() == TokenType.MUL || curr_token.getType() == TokenType.DIV)
		{
			Token t = curr_token;
			MatchToken(curr_token.getType());
			right = factor();
			left = MakeExprNode(t, left, right);
		}
		return left;
	}
	protected AST_expression factor()
	{
		AST_expression left, right;
		if (curr_token.getType() == TokenType.PLUS)
		{
			MatchToken(TokenType.PLUS);
			right = factor(); 
		}
		else if (curr_token.getType() == TokenType.MINUS)
		{
			Token t = curr_token;
			MatchToken(TokenType.MINUS);
			right = factor();
			BasicToken tv = new BasicToken(TokenType.CONST_ID, "0", 0, null);
			Position pos = new Position(0, 0);
			Token virtual_op = new Token(tv);
			virtual_op.setPosition(pos);
			left = MakeExprNode(virtual_op);
			right = MakeExprNode(t, left, right);
		}
		else
			right = component(); 
		return right;
	}
	protected AST_expression component()
	{
		AST_expression left, right;
		left = atom();
		if (curr_token.getType() == TokenType.POWER)
		{
			Token t = curr_token;
			MatchToken(TokenType.POWER);
			right = component(); 
			left = MakeExprNode(t, left, right);
		}
		return left;
	}
	protected AST_expression atom()
	{
		AST_expression root = null;
		switch (curr_token.getType())
		{
		case CONST_ID:
			MatchToken(TokenType.CONST_ID);
			root = MakeExprNode(lastToken);
			break;
		case T:
			MatchToken(TokenType.T);
			root = MakeExprNode(lastToken);
			break;
		case FUNC:
		{
			MatchToken(TokenType.FUNC); Token t = lastToken;
			MatchToken(TokenType.L_BRACKET);
			AST_expression tmp = expression(); 
			root = MakeExprNode(t, tmp);
			MatchToken(TokenType.R_BRACKET);
		}
		break;
		case L_BRACKET:
			MatchToken(TokenType.L_BRACKET);
			root = expression();
			MatchToken(TokenType.R_BRACKET);
			break;
		default:
			SyntaxError(2);
			break;
		}
		return root;
	}
	
	public Parser(Scanner needScanner)
	{
		theScanner = needScanner;
		{
			BasicToken tv = new BasicToken(TokenType.NONTOKEN,"0",0,null);  //һ���յĸ�
			tv.lexeme = theScanner.file_name;		//������ϢΪScanner���ļ���
			Position pos = new Position(0,0);		//�ļ��Ĺ��λ��Ϊ(0,0)
			Token virtual_tk = new Token(tv);		//һ������Token
			virtual_tk.setPosition(pos);			//λ�ó�ʼ����ʵ����java�ﲻ��Ҫ��һ����
			ASTroot = new AST_stmt_list(virtual_tk);	//ASTrootΪ��������ĳ����﷨��
		}
		
		indent = 0;
		ptr_t_storage = parameter;
	//	parameter = 0;
	//	ptr_t_storage.parameter = parameter;  //ʵ���ǰѿռ䴫����ָ�룬ÿ��parameter�ı�ptr�ͻ��
		
		ErrLog.printLog("Analyse file:[" + theScanner.file_name + "]...\n");
	}
	//~Parser();  //����ʵ��
	
	// *��ȡ�ļ���
	public String getFilename()
	{
		if(null == theScanner)
			return null;
		else 
			return theScanner.file_name;
	}

	public AST_stmt_list run() 	// �﷨��������ڲ���������Ϊ�������빹����﷨��������б�
	{
		FetchToken(); // ��ȡ��һ���Ǻ�
		program(); // �ݹ��½�����
		
		tree_trace(ASTroot); // �������������﷨��
		
		// ������û��һ����ȷ����� , ���ؿ���
		if( ASTroot.children_count() < 1)
		{
			//delete ASTroot;  ����Ҫʵ��
			ASTroot = null;
		}
		return ASTroot;
	}
	
	// ���ر��������ļ���
	// ����Ϊ�������빹����﷨��������б�
	public AST_stmt_list getStmts(){ return ASTroot ; }
	public Tclass getTmemory(){ return ptr_t_storage;}
	public void setTmemory(double t){ ptr_t_storage.parameter = t; }
	
	
}
