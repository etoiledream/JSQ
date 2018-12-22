package parser;
import errlog.ErrLog;
import scanner.*;
// 1. 该分析器要为全部输入生成一个完整的语法树 .
//    当语法分析器对象消亡时，此树不会自动销毁，即调用者必须销毁。
// 2. 其他程序可对该语法树进行多种处理。其中解释器所做的
//    就是计算表达式的值、绘图。
public class Parser
{
	
	// 下述函数用于跟踪调试，显示进入、退出哪个非终结符的函数。
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
	
	protected AST_stmt_list ASTroot = null;       // 整个输入的抽象语法树
	protected Tclass ptr_t_storage = new Tclass(0); // T 值的存放空间,是个指针(我这算是引用了)
	protected Tclass parameter = new Tclass(0);   // 每次将计算出来的变量存入T，然后循环
	protected Token curr_token;   // 当前记号
	protected Token lastToken; 	 // 当前记号之前，最后一个正确的记号
	
	void doParse(String file_name) //测试代码
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
	private int indent;    //  显示信息的缩进量
	private TokenType FetchToken() // 获取记号
	{
		TokenType tk = TokenType.ERRTOKEN;  //默认错误，防止意外情况
		while (true)
		{
			curr_token = theScanner.getToken();  //和词法分析器的交互
			if ((tk = curr_token.getType()) == TokenType.ERRTOKEN)
			{
				ErrLog.printLog("discard ERRtext \" " +curr_token.getLexeme() + "\" at [%d" + curr_token.getPosition().getRow() + "," + curr_token.getPosition().getCol() + "]!\n");
				SyntaxError(1); //一类错误
				continue;
			}
			break;
		}
		return tk;
	}
	private void MatchToken(TokenType expected) // 匹配记号
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
				SyntaxError(2);  //二类错误
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
	private void SyntaxError (int case_of)// 指出语法错误 ( 调用 error_msg)
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
	private AST_expression MakeExprNode(Token p_rt,AST_expression ... args)   // 为表达式构造语法树
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
	
	protected void program()  //program 的递归子程序
	{
		enter("program");
		while(curr_token.getType() != TokenType.NONTOKEN)
		{
			AST_statement stmt = statement();
			if( null != stmt)
			{
				// 因为检测到语法错误之后，会丢弃若干记号，直到
				//     看到了期望的记号才继续。
				// 所以可能直到文件末尾也匹配不上，那就要丢弃当前语句。
				if( curr_token.getType() == TokenType.NONTOKEN )
					stmt = null;  //原句是delete stmt;
				else
					ASTroot.addStmt(stmt);
			}
			MatchToken(TokenType.SEMICO);
		}
		back("program");
	}
	
	protected AST_statement statement() //statement 的递归子程序
	{
		 enter ("statement");
		 AST_statement root =null;  //根节点为空？
		 switch (curr_token.getType()) // LL(1)
		 {
		 case ORIGIN :  root = origin_statement();   break;
		 case SCALE  :  root = scale_statement();    break;
		 case ROT    :  root = rot_statement();      break; 
		 case FOR    :  root = for_statement();      break; 
		 case COLOR  :  root = color_statement();    break; 
		 default:  SyntaxError(2);System.out.println("第一个字有问题！");      break;
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
	
	protected AST_statement origin_statement()   //代码有点问题！
	{
		enter ("origin_statement");
		MatchToken(TokenType.ORIGIN);
		AST_statement root  = new AST_statement( lastToken );  // 生成 ORIGIN 语句的语法树根
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
		AST_statement root = new AST_statement( lastToken );  // 生成 ROT 语句的语法树根
		
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
		AST_statement root  = new AST_statement( lastToken );  // 生成 SCALE 语句的语法树根
		
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
	
	 // 语法分析器 无需重置的操作
	protected AST_expression expression() //expression 的递归子程序
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
			BasicToken tv = new BasicToken(TokenType.NONTOKEN,"0",0,null);  //一个空的根
			tv.lexeme = theScanner.file_name;		//根的信息为Scanner的文件名
			Position pos = new Position(0,0);		//文件的光标位置为(0,0)
			Token virtual_tk = new Token(tv);		//一个虚拟Token
			virtual_tk.setPosition(pos);			//位置初始化（实际上java里不需要这一步）
			ASTroot = new AST_stmt_list(virtual_tk);	//ASTroot为整个输入的抽象语法树
		}
		
		indent = 0;
		ptr_t_storage = parameter;
	//	parameter = 0;
	//	ptr_t_storage.parameter = parameter;  //实际是把空间传给了指针，每次parameter改变ptr就会变
		
		ErrLog.printLog("Analyse file:[" + theScanner.file_name + "]...\n");
	}
	//~Parser();  //不用实现
	
	// *获取文件名
	public String getFilename()
	{
		if(null == theScanner)
			return null;
		else 
			return theScanner.file_name;
	}

	public AST_stmt_list run() 	// 语法分析的入口操作。返回为整个输入构造的语法树（语句列表）
	{
		FetchToken(); // 获取第一个记号
		program(); // 递归下降分析
		
		tree_trace(ASTroot); // 输出完整输入的语法树
		
		// 输入中没有一个正确的语句 , 返回空树
		if( ASTroot.children_count() < 1)
		{
			//delete ASTroot;  不需要实现
			ASTroot = null;
		}
		return ASTroot;
	}
	
	// 返回被分析的文件名
	// 返回为整个输入构造的语法树（语句列表）
	public AST_stmt_list getStmts(){ return ASTroot ; }
	public Tclass getTmemory(){ return ptr_t_storage;}
	public void setTmemory(double t){ ptr_t_storage.parameter = t; }
	
	
}
