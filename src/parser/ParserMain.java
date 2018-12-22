package parser;

import errlog.ErrLog;
import scanner.Scanner;

public class ParserMain {
	
	void doParse(String file_name) //²âÊÔ´úÂë
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
			e.printStackTrace();
		}
		ErrLog.restart(false);
	}

	public static void main(String[] args) 
	{
		ParserMain test = new ParserMain();
		test.doParse("ScannerTest.txt");
	}

}
