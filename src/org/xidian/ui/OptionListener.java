package org.xidian.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *  源文件导入导出监听类
 */
@SuppressWarnings("serial")
public class OptionListener extends JFrame implements ActionListener {
	
	private JButton importFileButton, exportFileButton;

	public OptionListener(JButton importFileButton, JButton exportFileButton) {
		this.importFileButton = importFileButton;
		this.exportFileButton = exportFileButton;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		ImportAndExportFileListener importExportFileListener = new ImportAndExportFileListener(importFileButton);
		if (actionEvent.getSource() == importFileButton) {			
			importFileButton.addActionListener(importExportFileListener);
			return;
		} else if (actionEvent.getSource() == exportFileButton){		
			exportFileButton.addActionListener(importExportFileListener);
			return;
		} 
		//JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_NAME1);
		JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_COPYRIGHT);
	}
	
}
		
