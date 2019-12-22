package org.xidian.ui;

import org.xidian.alg.BaseData;
import org.xidian.alg.ReachabilityGraphAlgorithm;
import org.xidian.utils.LoadModelUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * 源文件导入导出监听类
 */
@SuppressWarnings("serial")
public class OptionListener extends JFrame implements ActionListener {

    private JButton importFileButton, exportFileButton, initButton, declareRationButton, exitButton;
    static BaseData baseData;

    public OptionListener(JButton importFileButton, JButton exportFileButton, JButton initButton, JButton declareRationButton, JButton exitButton) {
        this.importFileButton = importFileButton;
        this.exportFileButton = exportFileButton;
        this.initButton = initButton;
        this.declareRationButton = declareRationButton;
        this.exitButton = exitButton;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ImportAndExportFileListener importExportFileListener = new ImportAndExportFileListener(importFileButton);
        if (importFileButton.equals(actionEvent.getSource())) {
            importFileButton.addActionListener(importExportFileListener);
            return;
        } else if (exportFileButton.equals(actionEvent.getSource())) {
            exportFileButton.addActionListener(importExportFileListener);
            return;
        } else if (initButton.equals(actionEvent.getSource())){
            int result = JOptionPane.showConfirmDialog(null, "Confirm init?","InitData",JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                MenuListener.mainPanel.setText(null);
                if (baseData != null){
                    LoadModelUtil.clearBaseData();
                    BaseData.rootState = null;
                }
            }
        }else if (declareRationButton.equals(actionEvent.getSource())) {
			JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_COPYRIGHT,"Copyright",JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (exitButton.equals(actionEvent.getSource())) {
//			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			int r = JOptionPane.showConfirmDialog(null,"Confirm quit?","ConfirmDialog",JOptionPane.YES_NO_OPTION);
			if(r==JOptionPane.YES_OPTION){
				System.exit(0);
			}
        }

    }

}
		
