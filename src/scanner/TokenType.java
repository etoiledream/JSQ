package scanner;

//定义记号类型
public enum TokenType{
  COMMENT,//用于处理行注释
  ID,//用于进一步区分各保留字、常量名、函数名
  ORIGIN, SCALE, ROT, IS, TO, STEP, DRAW, FOR, FROM, COLOR,//保留字
  T,//参数
  SEMICO, L_BRACKET, R_BRACKET, COMMA,//分隔符号
  PLUS, MINUS, MUL, DIV, POWER,//运算符
  FUNC,//函数
  CONST_ID,//常数
  NONTOKEN,//空记号
  ERRTOKEN//出错记号
}