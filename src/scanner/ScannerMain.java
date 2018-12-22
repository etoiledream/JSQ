/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */

package scanner;

//词法分析器测试程序
public class ScannerMain {

    public static void main(String[] args){

        String file_name = "ScannerTest.txt";
        Scanner lexer = new Scanner();
        if(lexer.initScanner(file_name) == 0){
            System.out.println("Open Source File Error ! \n");
            return;
        }
        System.out.println("\nTokenType    Lexeme        Value       Func           Location");
        System.out.println("_________________________________________________________________\n");
        Token token;
        while (true){
            token = lexer.getToken();  //这里一次分析一个字符
            if(token.getType()!= TokenType.NONTOKEN){
                System.out.format("%-12s %-12s %-12.5f %-12s (%-5d,%-5d)\n", token.getType(), token.getLexeme(), token.getValue(),
                        token.getFunc(), token.getPosition().getRow(), token.getPosition().getCol());
            }
            else
                break;
        }
        System.out.println("_________________________________________________________________\n");
    }
}
