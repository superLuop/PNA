package org.xidian.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MainPanel extends JPanel{
	
	private JEditorPane editorPane;
	private static MainPanel mainPanel;
	private static JScrollPane scroller;
	 
	public static MainPanel getInstance() {
		if(mainPanel == null) {
			mainPanel = new MainPanel();
		}
		return mainPanel;
	}
	   
	public MainPanel() {
		setOpaque(false);
		setLayout(new BorderLayout());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();		
		setPreferredSize(new Dimension((int)(screen.getWidth()*0.5), (int)(screen.getHeight()*0.8)));
		setBorder(new EmptyBorder(0, 0, 10, 10));
		editorPane = new JEditorPane();
		//边框不可见
		editorPane.setOpaque(false);
		editorPane.setEditable(false);
		editorPane.setBorder(BorderFactory.createTitledBorder(null, UIContants.UI_ANALYSIS_RESULT, TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 16), new Color(51, 51, 51)));
		//添加滚动条
		scroller = new JScrollPane(editorPane);
		scroller.setBorder(null);
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);		
		add(scroller);
	} 
	
	@Override
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"/images/scrollpane.png"));
		Image img = icon.getImage();
		g.drawImage(img, scroller.getX(), scroller.getY(),
				scroller.getWidth(), scroller.getHeight(), this);
		super.paint(g);
	}
	
	 public void setText(String text) {	   
		 editorPane.setText(text);
		 editorPane.setCaretPosition(0); //从头开始显示运算结果
	 }
	 
	 public String getText() {		    
			return editorPane.getText();			
	 }		
	
}
