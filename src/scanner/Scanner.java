/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */

package scanner;

import java.io.PushbackInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

//词法分析器类
public class Scanner {

    protected PushbackInputStream in_file;//输入文件流
    protected DFA dfa;//有限状态自动机
    protected Position curr_pos;//当前文件位置（行号，列号）
    public String file_name;//文件名
    private BasicTokenCollection token_tab = new BasicTokenCollection();

    public Scanner(){
        //默认构造函数
        in_file = null;
        dfa = new DFA();
    }
    public int initScanner(String file_name){
        //初始化并点开词法分析器
        curr_pos = new Position(1, 0);
        this.file_name = file_name;
        try { in_file = new PushbackInputStream(new FileInputStream(file_name)); }
        catch (ArrayIndexOutOfBoundsException error){
            System.out.println(error.getMessage());
            error.getStackTrace();
            return 0;
        }
        catch (Exception error) {
            System.out.println(error.getMessage());
            error.printStackTrace();
            return 0;
        }
        return 1;
    }
    public void CloseScanner(){
        //关闭词法分析器
        if(in_file != null){
            try{ in_file.close(); }
            catch (IOException error)
            {
                System.out.println(error.getMessage());
                error.printStackTrace();
            }
            in_file = null;
        }
    }
    public Token getToken(){
        //获取一个记号
        int first_char;//记号开始的第 1 个字符
        int last_state = -1;// 识别记号结束时的状态
        Position where;//当前记号的起始位置
        Token theToken = new Token();//被识别到的记号对象
        int to_be_continue;
        do {
            theToken.clear(false);

            // 第1步：预处理，跳过空白字符
            first_char = preProcess();
            if (first_char == -1)//文件结束了
            {
                theToken.setPosition(curr_pos);
                theToken.setType(TokenType.NONTOKEN);
                return theToken;
            }
            where = curr_pos;
            theToken.setPosition(where);

            // 第2步：边扫描输入，边转移状态
            last_state = scanMove(theToken, first_char);

            // 第3步：后处理：根据终态所标记的记号种类信息，进行特殊处理
            to_be_continue = postProcess(theToken, last_state);
        }while(to_be_continue != 0);
//        theToken.setPosition(where);//修正记号位置
        return theToken;
    }
    private int getChar(){
        //从输入源程序中读入一个字符
        int result = -1;
        try{
            int next_char = in_file.read();
            if(next_char != -1)
            {
                if(next_char == '\n'){
                    curr_pos.nextline();
                    result = next_char;
                }
                else {
                    curr_pos.addCol();
                    result = Character.toUpperCase(next_char);
                }
            }
        }
        catch (IOException error)
        {
            System.out.println(error.getMessage());
            error.printStackTrace();
        }
        return result;
    }
    private void backChar(char next_char){
        //把预读的字符退回到输入源程序中
        if(next_char == ((char)-1) || next_char == '\n')
            return;
        try{
            in_file.unread(next_char);
            curr_pos.minusCol();
        }
        catch (IOException error)
        {
            System.out.println(error.getMessage());
            error.printStackTrace();
        }
    }
    private boolean isSpace(char c){
        //判断字符 c 是否为空白字符
        if(c < (char)0 || c > (char)0x7e)
            return false;
        return Character.isWhitespace(c);
    }
    private int preProcess() {
        //预处理
        int curr_char;//当前读到字符
        while(true){
            curr_char = getChar();
            if(curr_char == -1)
                return -1;
            if(!isSpace((char)curr_char))
                break;
        }
        return curr_char;
    }
    private int postProcess(Token tok, int last_state){
        //后处理
        int to_be_continue = 0;
        TokenType tk = dfa.stateIsFinal(last_state);
        tok.setType(tk);
        switch (tk){
            case ID: //查符号表，进一步计算记号信息
            {
                BasicToken id = JudgeKeyToken(tok.getLexeme());
                if(id == null)
                    tok.setType(TokenType.ERRTOKEN);
                else
                    tok.setType(id.type);
            }break;
            case CONST_ID: //转为数值
                tok.setValue(Double.valueOf(tok.getLexeme())); break;
            case COMMENT: //行注释：忽略直到行尾的文本 , 并读取下一个记号
            {
                int c;
                while(true) {
                    c = getChar();
                    if(c == '\n' || c == -1)
                        break;
                }
            }to_be_continue = 1; break;
            default: break;
        }
        return to_be_continue;
    }
    private int scanMove(Token tok, int first_char){
        //识别记号的核心操作：边扫描边进行状态转移
        int curr_state, next_state; //当前状态，下一状态
        int curr_char = first_char;//当前字符
        curr_state = dfa.getStartState();
        while(true){
            next_state = dfa.move(curr_state, (char)curr_char);
            if(next_state < 0)//没有转移了
            {
                //第一个字符就无效，则丢弃它 . 否则因为反复读到该字符而陷入死循环
                if(tok.isEmpty())
                    tok.setChar(curr_char);
                else
                    backChar((char)curr_char);
                break;
            }
            tok.appendText((char)curr_char);
            curr_state = next_state;
            curr_char = getChar();
            if(curr_char == -1)
                break;
        }
        return curr_state;
    }
    private BasicToken JudgeKeyToken(String str){
        //判断所给的字符串是否在符号表中
        for(BasicToken curr_tok : token_tab)
        {
            if(curr_tok.lexeme.equals(str))
                return curr_tok;
        }
        return null;
    }
    //内部静态类：记号表类
    public static class BasicTokenCollection extends ArrayList<BasicToken> {
		private static final long serialVersionUID = -7474649357028667723L;
		private BasicTokenCollection(){
            //默认构造函数
            add(new BasicToken(TokenType.CONST_ID, "PI", 3.1415926, null));//π
            add(new BasicToken(TokenType.CONST_ID, "E", 2.71828, null));//自然常数e
            add(new BasicToken(TokenType.T, "T", 0.0, null));//参数
            add(new BasicToken(TokenType.FUNC, "SIN", 0.0, Func.sin));//正弦函数
            add(new BasicToken(TokenType.FUNC, "COS", 0.0, Func.cos));//余弦函数
            add(new BasicToken(TokenType.FUNC, "TAN", 0.0, Func.tan));//正切函数
            add(new BasicToken(TokenType.FUNC, "LN", 0.0, Func.log));//对数函数
            add(new BasicToken(TokenType.FUNC, "EXP", 0.0, Func.exp));//指数函数
            add(new BasicToken(TokenType.FUNC, "SQRT", 0.0, Func.sqrt));//开平方根函数
            add(new BasicToken(TokenType.ORIGIN, "ORIGIN", 0.0, null));//保留关键字
            add(new BasicToken(TokenType.SCALE, "SCALE", 0.0, null));
            add(new BasicToken(TokenType.ROT, "ROT", 0.0, null));
            add(new BasicToken(TokenType.IS, "IS", 0.0, null));
            add(new BasicToken(TokenType.FOR, "FOR", 0.0, null));
            add(new BasicToken(TokenType.FROM, "FROM", 0.0, null));
            add(new BasicToken(TokenType.TO, "TO", 0.0, null));
            add(new BasicToken(TokenType.STEP, "STEP", 0.0, null));
            add(new BasicToken(TokenType.DRAW, "DRAW", 0.0, null));
            add(new BasicToken(TokenType.COLOR, "COLOR", 0.0, null));
        }
        public BasicTokenCollection getTokenTab(){ return this; } //获取记号表
    }
    //有限状态自动机类
    private static class DFA {

        private DFA(){}//默认构造函数
        private int getStartState(){return 0;}//获取DFA初态
        private int move(int state_src, char c){
            // 查询转移：在状态state_src下，遇到字符c的下一状态。若小于0，表示无转移
            switch (state_src){
                case 0:
                    if(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_') return 1;
                    else if('0' <= c && c <= '9') return 2;
                    else if(c == '*') return 4;
                    else if(c == '/') return 6;
                    else if(c == '-') return 7;
                    else if(c == '+') return 8;
                    else if(c == ',') return 9;
                    else if(c == ';') return 10 ;
                    else if(c == '(') return 11;
                    else if(c == ')') return 12;
                    break;
                case 1:
                    if(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_') return 1; break;
                case 2:
                    if(c == '.') return 3;
                    if('0' <= c && c <= '9' ) return 2;
                    break;
                case 3:
                    if('0' <= c && c <= '9') return 3; break;
                case 4:
                    if(c == '*') return 5; break;
                case 6:
                    if(c == '/') return 13; break;
                case 7:
                    if(c == '-') return 13; break;
            }
            return -1;
        }
        private TokenType stateIsFinal(int state){
            //判断指定状态是否为 DFA 的终态。若是则返回该终态对应的记号类别，否则返回ERRTOKEN.
            switch (state){
                case 1: return TokenType.ID;
                case 2: case 3: return TokenType.CONST_ID;
                case 4: return TokenType.MUL;
                case 5: return TokenType.POWER;
                case 6: return TokenType.DIV;
                case 7: return TokenType.MINUS;
                case 8: return TokenType.PLUS;
                case 9: return TokenType.COMMA;
                case 10: return TokenType.SEMICO;
                case 11: return TokenType.L_BRACKET;
                case 12: return TokenType.R_BRACKET;
                case 13: return TokenType.COMMENT;
                default: return TokenType.ERRTOKEN;
            }
        }
        @SuppressWarnings("unused")
		public static DFA getHardCodeDFA(){
            //获得直接编码型DFA对象
            return (new DFA());
        }
    }
}
