/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */

package scanner;

//记号类
public class Token {

    private BasicToken basic_token;//基本记号信息
    private Position position;//记号位置

    public Token(){
        //默认构造函数
        basic_token = new BasicToken();
        position = new Position(0, 0);
        clear(false);

    }
    public Token(Position pos){
        //输入位置进行构造
        basic_token = new BasicToken();
        position = pos;
        clear(false);
    }
    public Token(BasicToken basic_token){
        //输入一个记号构造
        this.basic_token = basic_token;
        position = new Position(0, 0);
    }
    public void clear(boolean resetPosition){
        //初始化清空
        basic_token.clear();
        if(resetPosition)
            position.set(0, 0);
    }
    public void setBasic(BasicToken basic_token){this.basic_token = basic_token;}//设值基本记号信息
    public void setPosition(Position position){this.position = position;}//设值记号位置
    public void setType(TokenType type){basic_token.type = type;}//设值记号类型
    public void setValue(double value){
        //设值记号值
        if(basic_token.type == TokenType.CONST_ID)
            basic_token.value = value;
    }
    public void setText(String str){
        //设值记号文本
        if(!str.isEmpty())
            basic_token.lexeme = str;
    }
    public void appendText(char c){
        //将字符C追加到记号文本末尾
        basic_token.lexeme += String.valueOf(c);
    }
    public void appendText(String str){
        //将字符串str追加到记号文本末尾
        if(!str.isEmpty())
            basic_token.lexeme += str;
    }
    public void setChar(int c){
        // 将无效字符  c 填写为记号文本，对于可见字符，填写它。从 '!' 到 '~'
        if(32 < c && c <= 126)
            basic_token.lexeme = String.valueOf((char)c);
        else
            basic_token.lexeme = String.format("\\X%02X", (char)c);
    }
    public TokenType getType(){return basic_token.type;}//获取记号类型
    public String getLexeme(){return basic_token.lexeme;}//获取记号文本
    public double getValue(){return basic_token.value;}//获取记号值
    public Func getFunc(){return basic_token.func;}//获取函数引用
    public boolean isEmpty(){return basic_token.lexeme.isEmpty();}//判断记号文本是否为空:空-true，不空-false
    public Position getPosition(){return position;}//获取记号位置
}
