package org.xidian.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Background extends JPanel {
	
	public Background() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, 100,
				(int) screenSize.getHeight());
	}

	@Override	
	public void paint(Graphics g) {
		super.paint(g);		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, (int) screenSize.getWidth(),
				(int) screenSize.getHeight());		
		g.drawImage(new ImageIcon(this.getClass().getResource(
				"/images/background1.jpg")).getImage(), 0, 0, getWidth(), getHeight(), this);
		MainFrame.getInstance().repaint();
	}
	
}
