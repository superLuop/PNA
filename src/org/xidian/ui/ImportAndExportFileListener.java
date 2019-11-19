package org.xidian.ui;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.xidian.utils.FileUtil;
import org.xidian.utils.LoadModelUtil;

@SuppressWarnings("serial")
public class ImportAndExportFileListener extends JFrame implements ActionListener {
	
	private MainPanel mainPanel;
	private JButton importFileButton;
	FileDialog fileDialog;
	
	public ImportAndExportFileListener(JButton importFileButton){
		mainPanel = MainPanel.getInstance();
		this.importFileButton = importFileButton;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(actionEvent.getSource() == importFileButton){
			JFileChooser jFileChooser = new JFileChooser();  
	        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
	        jFileChooser.showDialog(new JLabel(), UIContants.UI_CHOOSE);  
	        File file = jFileChooser.getSelectedFile();  
	        if(file != null && file.getAbsolutePath() != null) {
	        	Long start = FileUtil.getCurrentTime();
	        	//初始化模型
	        	LoadModelUtil.loadResource(file.getAbsolutePath());
	        	//在界面顶端显示当前分析路径
	        	MainFrame.getInstance().setTitle(file.getAbsolutePath());
	        	JOptionPane.showMessageDialog(null, (UIContants.UI_IMPORT_SUCCESS + (System.currentTimeMillis() - start) + "ms"));
	        }
		} else {
			fileDialog = new FileDialog(this, UIContants.UI_FILE_SAVE, FileDialog.SAVE);		
			fileDialog.setVisible(true);
		    File file = new File(fileDialog.getDirectory(), fileDialog.getFile());	 
		    String string = mainPanel.getText();
		    //导出
		    FileUtil.write(file.getAbsolutePath() + ".txt", string, false);	   
		}			
	}

}
