package parser;

public class Tclass   //由于java里基本类型是传值赋值，只能采取包裹变量的方式实现基本类型指针
{
	public double parameter;
	public Tclass(double x)
	{
		parameter = x;
	}
}