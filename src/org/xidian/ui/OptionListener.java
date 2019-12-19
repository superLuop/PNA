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
	
	private JButton importFileButton, exportFileButton, declareRationButton, exitButton;

	public OptionListener(JButton importFileButton, JButton exportFileButton, JButton declareRationButton, JButton exitButton) {
		this.importFileButton = importFileButton;
		this.exportFileButton = exportFileButton;
		this.declareRationButton = declareRationButton;
		this.exitButton = exitButton;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		ImportAndExportFileListener importExportFileListener = new ImportAndExportFileListener(importFileButton);
		if (importFileButton.equals(actionEvent.getSource())) {
			importFileButton.addActionListener(importExportFileListener);
			return;
		} else if (exportFileButton.equals(actionEvent.getSource())){
			exportFileButton.addActionListener(importExportFileListener);
			return;
		}else if (declareRationButton.equals(actionEvent.getSource())){
			JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_NAME1);
//			JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_COPYRIGHT);
			return;
		}else if (exitButton.equals(actionEvent.getSource())){
			JOptionPane.showMessageDialog(null, "The programming is about to quit...");
			System.exit(0);
		}

	}
	
}
		
