package ui_main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.border.LineBorder;

import semantics.SemanticAnalyzer;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WinGUI extends JFrame 
{
	private static final long serialVersionUID = 5291359850874977375L;
	private JPanel contentPane;
	private JPanel panel;
	private JButton btnRe;
	
	public class t_color   //���Ƶ���ɫ
	{
		int red;
		int green;
		int blue;
		public t_color(){red = 255;green = 0;blue = 0;}
		public t_color(int r,int g,int b){red = r;green = g;blue = b;}
		public void setValue(int r,int g,int b){
			red = r;green = g; blue = b;
		}
		
	}
	
	private class collection  //����ArrayList����
	{
		double x; double y; t_color c;
		collection(double x2,double y2,t_color color)
		{
			x = x2; y = y2; c = color;
		}
	}
	
	private ArrayList<collection> drawInfo = new ArrayList<collection>();
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	private void setText()  //�ú���ʵ�ֽ��ļ��������һ��������GUI��
	{
		try {
			FileReader fr = new FileReader("ScannerTest.txt");
			java.util.Scanner input=new java.util.Scanner(fr);
			String informationInFile;
			while(input.hasNext() == true)
			{
				informationInFile = input.nextLine();
				textArea.append(informationInFile + "\r\n");
			}
			input.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,"��ȡԴ�ļ���Ϣʧ�ܣ�");}
	}

	//������ṩ�������ӿ�
	/////////////////////
	// @func: DrawPixel
	// @param: double x, double y, t_color color
	// 
	/////////////////////
	public void DrawPixel(double x,double y,t_color color)  //��ͼ�������һ����,ʵ���Ͻ����ǽ�����ֵ�����ҵ�������
	{
		drawInfo.add(new collection(x,y,color));
	}
	public void ShowMessage(int flag,String msg)  // ��������Ϣ =0 �� ��������Ϣ =1���ܼ򵥵�һ��Panel
	{
		JOptionPane.showMessageDialog(null,"msg");
	}
	
	
	
	public WinGUI() 
	{
		drawInfo.add(new collection(10,10,new t_color()));
		drawInfo.add(new collection(15,15,new t_color()));
		initCopmonents();	
		createEvents();
	}
	
	
	
	
	void initCopmonents()
	{
		setTitle("������ͼ����GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 486, 589);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel = new JPanel(); //�����г�ʼ��Ĭ���ļ�����е����ݹ��ɵİ���������
		
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 134, 400, 400);
		contentPane.add(panel);
		
		btnRe = new JButton("Go");
		btnRe.setFont(new Font("����", Font.PLAIN, 17));
		btnRe.setBounds(420, 504, 52, 48);
		contentPane.add(btnRe);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 452, 114);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		setText();
		scrollPane.setViewportView(textArea);
		
		
		
	}
	
	/*
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void paint(Graphics g) 
		{
			super.paint(g);  //���ø����paint����
			collection c = null;
			for(int i = 0 ; i < drawInfo.size() ;i++)
			{
				c = drawInfo.get(i);
				g.drawRect((int)c.x,(int)c.y,1,1);// *����Semantic��������ΪDouble, ֻ�ܽ��˴�����һ��ǿ��ת��
			}
		}
	};*/
//	void paintUI()
//	{
	//	panel.paint(g)
	//	{
			
	//	}
//	}
	
	void createEvents()
	{
		btnRe.addActionListener(new ActionListener() {    //���Go֮��Ĳ���
			public void actionPerformed(ActionEvent e) 
			{
				//���Ƚ��ļ����Scanner.txt��
				try {
					File file = new File("ScannerTest.txt");
					Writer out = new FileWriter(file);
					String info = textArea.getText();
					out.write(info);
					out.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,"��Ϣ����Դ�ļ�ʧ�ܣ�");
				}
				//�����������ǵ����﷨���������õ�һ��ArrayList�ļ��ϣ�Ȼ����Ƶ��������
				//doInterpret ("ScannerTest.txt");
				//drawInfo.add(new collection(20,20,new t_color()));
				//drawInfo.remove(1);	
				//panel.repaint();	//���ݿ��ܱ����ĵ��ļ����»���
			//	drawInfo.clear();	//������еĵ�׼���´λ�ͼ
				SemanticAnalyzer.doInterpret("ScannerTest.txt");
				
			}
		});
	}
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinGUI frame = new WinGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
