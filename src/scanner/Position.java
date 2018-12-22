/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */

package scanner;
//scanner.hpp 里的position
//位置类
public class Position {

    private int row;//行号
    private int col;//列号

    public Position(int row, int col) {
        //初始化
        this.row = row;
        this.col = col;
    }
    public void set(int row, int col){
        //设值
        this.row = row;
        this.col = col;
    }
    public void nextline(){
        //读取下一行
        row++;
        col = 0;
    }
    public void addCol(){ col++; }//读取下一列
    public void minusCol(){
        //后退一列
        if(--col == Integer.MAX_VALUE)
            col = 0;
    }
    public int getRow(){return row;}//返回行
    public int getCol(){return col;}//返回列
}
