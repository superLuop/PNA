package org.xidian.ui;

import java.awt.Graphics;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;

public class ImageButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 8479836334633485188L;
	private String name;
	private JLabel image;
	private JLabel textPanel;
	private Boolean pressed = false;
	private Boolean entered = false;
	private Boolean enabled = true;
	private String modal = "";

	
	public ImageButton(String name) {
		this.name = name;
		this.image = new JLabel();
		textPanel = new JLabel();
		addMouseListener(this);
	}

	public ImageButton(String btnname, String text) {
		this(btnname);
		textPanel.setText(text);
		textPanel.setVerticalAlignment(JLabel.CENTER);
		textPanel.setHorizontalAlignment(JLabel.CENTER);
	}

	public ImageButton(String btnname, String text, int size) {
		this(btnname, text);
		textPanel.setFont(new Font("����",Font.BOLD, size));
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		if (!enabled)
			return;
		entered = true;
		if (pressed) {
			setModal("pressed");
		} else {
			setModal("entered");
		}
	}

	public void mouseExited(MouseEvent e) {
		if (!enabled)
			return;
		entered = false;
		setModal("");
	}

	public void mousePressed(MouseEvent e) {
		if (!enabled)
			return;
		pressed = true;
		setModal("pressed");
	}

	public void mouseReleased(MouseEvent e) {
		if (!enabled)
			return;
		pressed = false;
		if (entered) {
			setModal("entered");
		} else {
			setModal("");
		}
	}

	public void setModal() {
		if (entered) {
			setModal("entered");
		} else {
			setModal("");
		}
	}

	public void setModal(String modal) {
		this.modal = modal;
		repaint();
	}

	public void setIBEnabled(Boolean isenabled) {
		enabled = isenabled;
		if (enabled) {
			setModal();
		} else {
			setModal("");
		}
	}

	public void setText(String text) {
		this.textPanel.setText(text);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		image.setSize(width, height);
		textPanel.setSize(width, height);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (textPanel != null) {
			textPanel.setFont(font);
			textPanel.setSize(textPanel.getPreferredSize().width,
					textPanel.getHeight());
		}
	}

	@Override
	public void paint(Graphics g) {
		try {
			ImageIcon img = null;
			if ("".equals(modal)) {
				img = new ImageIcon(this.getClass().getResource(
						"/images/" + name + ".png"));
			} else {
				img = new ImageIcon(this.getClass().getResource(
						"/images/" + name + "_" + modal + ".png"));
			}
			if (img.getImage() != null)
				g.drawImage(img.getImage(),
						(getWidth() - img.getIconWidth())*2 ,
						(getHeight() - img.getIconHeight()) / 2, image);
		} catch (NullPointerException e) {
		}
		if (textPanel != null) {
			textPanel.paint(g);
		}
	}
}

