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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.xidian.utils.FileUtil;
import org.xidian.utils.LoadModelUtil;

@SuppressWarnings("serial")
public class ImportAndExportFileListener extends JFrame implements ActionListener {
	
	private MainPanel mainPanel;
	private JButton importFileButton;
	FileDialog fileDialog;
	JFileChooser jFileChooser;

	public ImportAndExportFileListener(JButton importFileButton){
		mainPanel = MainPanel.getInstance();
		this.importFileButton = importFileButton;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(importFileButton.equals(actionEvent.getSource())){
			jFileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.pnt", "pnt");
			jFileChooser.setFileFilter(filter);
	        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = jFileChooser.showDialog(new JLabel(), UIContants.UI_CHOOSE);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = jFileChooser.getSelectedFile();
				if(file != null) {
					Long start = FileUtil.getCurrentTime();
					//初始化模型
					LoadModelUtil.loadResource(file.getAbsolutePath());
					//在界面顶端显示当前分析路径
					MainFrame.getInstance().setTitle(file.getAbsolutePath());
					JOptionPane.showMessageDialog(null, (UIContants.UI_IMPORT_SUCCESS + (System.currentTimeMillis() - start) + "ms"));
				}
			}

		} else {
			fileDialog = new FileDialog(this, UIContants.UI_FILE_SAVE, FileDialog.SAVE);
			fileDialog.setVisible(true);
			Long startTime = FileUtil.getCurrentTime();
		    File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
		    String string = mainPanel.getText();
		    //导出
		    FileUtil.write(file.getAbsolutePath() + ".txt", string, false);
			JOptionPane.showMessageDialog(null, (UIContants.UI_EXPORT_SUCCESS + (System.currentTimeMillis() - startTime) + "ms"));
		}
	}

}
