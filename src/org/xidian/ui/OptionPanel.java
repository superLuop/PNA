package org.xidian.ui;

import java.awt.*;
//import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 工具栏
 *
 */
@SuppressWarnings("serial")
public class OptionPanel extends JPanel {
	
	private static OptionPanel optionPanel;

	static public OptionPanel getInstance(){
		if(optionPanel == null)
			optionPanel = new OptionPanel();
		return optionPanel;
	}

	public OptionPanel() {		
		setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 20));			
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();	
		int width = (int) (screen.getWidth()*0.11);
		int height = (int) (screen.getHeight()*0.11);
	    setSize((int)(screen.width), (int)(screen.height*0.09));
		this.setOpaque(false);		
		//添加按钮背景图片
		ImageIcon icon1 = new ImageIcon(this.getClass().getResource(
				"/images/import.png"));
		ImageIcon icon2 = new ImageIcon(this.getClass().getResource(
				"/images/export.png"));
		ImageIcon icon3 = new ImageIcon(this.getClass().getResource(
				"/images/init.jpg"));
		ImageIcon icon4 = new ImageIcon(this.getClass().getResource(
				"/images/declaration.png"));
		ImageIcon icon5 = new ImageIcon(this.getClass().getResource(
				"/images/exit.jpg"));
		JButton importFileButton = new JButton(UIContants.UI_FILE_IMPORT, icon1);
		JButton exportFileButton = new JButton(UIContants.UI_FILE_EXPORT, icon2);
		JButton initButton = new JButton(UIContants.UI_INIT, icon3);
		JButton declareRationButton = new JButton(UIContants.UI_DECLARATION, icon4);
		JButton exitButton = new JButton(UIContants.UI_EXIT, icon5);
		//设置文字图片显示的相对位置
		importFileButton.setVerticalTextPosition(JButton.BOTTOM);
		importFileButton.setHorizontalTextPosition(JButton.CENTER);
		exportFileButton.setVerticalTextPosition(JButton.BOTTOM);
		exportFileButton.setHorizontalTextPosition(JButton.CENTER);
		initButton.setVerticalTextPosition(JButton.BOTTOM);
		initButton.setHorizontalTextPosition(JButton.CENTER);
		declareRationButton.setVerticalTextPosition(JButton.BOTTOM);
		declareRationButton.setHorizontalTextPosition(JButton.CENTER);
		exitButton.setVerticalTextPosition(JButton.BOTTOM);
		exitButton.setHorizontalTextPosition(JButton.CENTER);

		//设置按钮文本字体
		Font f = new Font(UIContants.UI_FONT_NAME,Font.HANGING_BASELINE,UIContants.UI_FONT_SIZE);
		importFileButton.setFont(f);
		exportFileButton.setFont(f);
		initButton.setFont(f);
		declareRationButton.setFont(f);
		exitButton.setFont(f);
		
		importFileButton.setPreferredSize(new Dimension(width, height));
		exportFileButton.setPreferredSize(new Dimension(width, height));
		initButton.setPreferredSize(new Dimension(width, height));
		declareRationButton.setPreferredSize(new Dimension(width, height));
		exitButton.setPreferredSize(new Dimension(width, height));
		//添加监听器
		OptionListener optionListener = new OptionListener(importFileButton, exportFileButton, initButton, declareRationButton, exitButton);
		importFileButton.addActionListener(optionListener);
		exportFileButton.addActionListener(optionListener);
		initButton.addActionListener(optionListener);
		declareRationButton.addActionListener(optionListener);
		exitButton.addActionListener(optionListener);
		//设置按钮边框不可见
		importFileButton.setContentAreaFilled(false);
		importFileButton.setBorderPainted(false);	
		exportFileButton.setContentAreaFilled(false);
		exportFileButton.setBorderPainted(false);
		initButton.setContentAreaFilled(false);
		initButton.setBorderPainted(false);
		declareRationButton.setContentAreaFilled(false);
		declareRationButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setBorderPainted(false);
		add(importFileButton);
		add(exportFileButton);
		add(initButton);
		add(declareRationButton);
		add(exitButton);
		setVisible(true);
	}

}
