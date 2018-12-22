/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */
package scanner;
//基本实现scanner.hpp
//基本记号类
public class BasicToken {

    public TokenType type;//记号类型
    public String lexeme;//记号文本
    public double value;//记号值
    public Func func;//记号函数引用

    public BasicToken(){
        //默认构造函数
        this.type = null;
        this.lexeme = null;
        this.value = 0;
        this.func = null;
    }
    public BasicToken(TokenType type, String lexeme, double value, Func func){
        //构造函数
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.func = func;
    }
    public void clear() {
        //初始化清空
        type = TokenType.NONTOKEN;
        lexeme = "";
        value = 0;
        func = null;
    }
}


