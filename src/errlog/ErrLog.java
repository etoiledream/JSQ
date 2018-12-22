/**
 *@author
 *LI_Yichen
 *ID: 16130120145
 *e-mail: niuqiao2010@163.com
 */

package errlog;

import java.io.*;

//错误日志类
public class ErrLog {

    final private static File errorFile = new File("src/errors.txt");
    final private static File logFile = new File("src/logs.txt");
    private static BufferedWriter ef_out = null;//错误文件写入流
    private static BufferedWriter lf_out = null;//日志文件写入流

    public static void open(){
        //开启写入流
        try
        {
            ef_out = new BufferedWriter(new FileWriter(errorFile));//开启错误文件写入流
            lf_out = new BufferedWriter(new FileWriter(logFile));//开启日志文件写入流
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public static void close(){
        //开启写入流
        try
        {
            //关闭错误文件写入流
        	if(ef_out != null)
        	{
                ef_out.flush();
                ef_out.close();
                //关闭日志文件写入流
                lf_out.flush();
                lf_out.close();
        	}
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public static void restart(boolean open_it){
        //重启写入流
        close();
        if(open_it)
            open();
    }
    public static void errorMessage(String msg){
        //错误信息
        try
        {
            ef_out.write(msg);
            ef_out.flush();
//            showMessage(msg);UI预留
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public static void errorMessage(int row, String description, String msg){
        //错误信息
        String info = String.format("Error at line %d: %s %s\n", row, description ,msg);
        try
        {
            ef_out.write(info);
            errorMessage(info);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public static void errorMessage(int row, int col, String description, String msg){
        //错误信息
        String info = String.format("Error at [%d, %d]: %s %s\n", row, col, description ,msg);
        try
        {
            ef_out.write(info);
            ef_out.flush();
            errorMessage(info);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public static void printLog(String ... msg){
        //打印日志信息
        try
        {
            for(String temp : msg)
            {
                lf_out.write(temp);
                lf_out.flush();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            errorMessage(e.getMessage());
        }
    }
}
