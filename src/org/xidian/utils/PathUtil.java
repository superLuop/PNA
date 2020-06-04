package org.xidian.utils;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * 导入模型窗口
 * @author luopeng
 * 
 */
public class PathUtil {
	static JFrame f  = new JFrame();
	static JButton resource = new JButton("导入模型");
	static String resourcePath;
	public static String destPath;
	
	public static void init() {
		resource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int returnVal = jfc.showOpenDialog(f);
				if(returnVal==JFileChooser.APPROVE_OPTION ){
					//解释下这里,弹出个对话框,可以选择要上传的文件,如果选择了,就把选择的文件的绝对路径打印出来,有了绝对路径,通过JTextField的setText就能设置进去了,那个我没写
					//System.out.println(jfc.getSelectedFile().getAbsolutePath());
					resourcePath = jfc.getSelectedFile().getAbsolutePath();
					destPath = jfc.getSelectedFile().getParentFile().getPath();
				}
			}
		});

		f.add(resource);
		f.setLayout(new FlowLayout());
		f.setSize(240, 180);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
//	public static void main(String[] args) {
//		init();
//		while(resourcePath != null) {
//			System.out.println(resourcePath);
//		}
//	}
}
 
