package org.xidian.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

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
//				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				int result = JOptionPane.showConfirmDialog(null, "confirm quit?", "quit window",
							 JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if(result == JOptionPane.OK_OPTION){
					System.exit(0);
				}
		}

	}
	
}
		
