package org.xidian.ui;

import java.awt.BorderLayout;
import java.awt.Container;
//import java.awt.Cursor;
import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Panel;
//import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;

import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	
	public static MainFrame mainFrame;	
	public static MainFrame getInstance() {		
		if (mainFrame == null){
			mainFrame = new MainFrame();
		}
		return mainFrame;
	}
	
	public MainFrame() {
		super(UIContants.UI_SOFTWARE_NAME1);
		//super(UIContants.UI_SOFTWARE_NAME);
		init();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						int result = JOptionPane.showConfirmDialog(null, "confirm quit?", "quit window",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
						if(result == JOptionPane.OK_OPTION){
							System.exit(0);
						}
					}
				}
		);
	}


	public void init() {
		//默认启动界面大小
		final Dimension defaultDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(defaultDimension.getWidth()*0.9), (int)(defaultDimension.getHeight()*0.9));
	    setResizable(true); //Resizable 可变尺寸的。可调整大小的
	    setLocationRelativeTo(null);
		//默认最小界面大小
		setMinimumSize(new Dimension((int)(defaultDimension.getWidth()*0.4), (int)(defaultDimension.getHeight()*0.5)));
		Container container = getContentPane();
		//图标
		setIconImage(new ImageIcon(this.getClass().getResource("/images/icon.png")).getImage());
		//添加菜单、工具和主显示panel
		container.add(new MenuPanel(), BorderLayout.WEST);
		container.add(new OptionPanel(), BorderLayout.NORTH);
		container.add(MainPanel.getInstance(), BorderLayout.CENTER);
		//添加背景图片
		((JPanel) this.getContentPane()).setOpaque(false);
		Background background = new Background();
		getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
		setVisible(true);


	}

}
