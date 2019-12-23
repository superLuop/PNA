package org.xidian.ui;

import org.xidian.model.PetriModel;
import org.xidian.utils.FileUtil;
import org.xidian.utils.LoadModelUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 源文件导入导出监听类
 */
@SuppressWarnings("serial")
public class OptionListener extends JFrame implements ActionListener {

    private JButton importFileButton, exportFileButton, initButton, declareRationButton, exitButton;
    MainPanel mainPanel;
    FileDialog fileDialog;
    JFileChooser jFileChooser;

    public OptionListener(JButton importFileButton, JButton exportFileButton, JButton initButton, JButton declareRationButton, JButton exitButton) {
        this.importFileButton = importFileButton;
        this.exportFileButton = exportFileButton;
        this.initButton = initButton;
        this.declareRationButton = declareRationButton;
        this.exitButton = exitButton;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
//        ImportAndExportFileListener importExportFileListener = new ImportAndExportFileListener(importFileButton);
        if (importFileButton.equals(actionEvent.getSource())) {
//            importFileButton.addActionListener(importExportFileListener);
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
        } else if (exportFileButton.equals(actionEvent.getSource())) {
//            exportFileButton.addActionListener(importExportFileListener);
            fileDialog = new FileDialog(this, UIContants.UI_FILE_SAVE, FileDialog.SAVE);
            fileDialog.setVisible(true);
            Long startTime = FileUtil.getCurrentTime();
            File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
            String string = mainPanel.getText();
            //导出
            FileUtil.write(file.getAbsolutePath() + ".txt", string, false);
            JOptionPane.showMessageDialog(null, (UIContants.UI_EXPORT_SUCCESS + (System.currentTimeMillis() - startTime) + "ms"));
        } else if (initButton.equals(actionEvent.getSource())){
            int result = JOptionPane.showConfirmDialog(null, "Confirm init?",
                    "InitData",JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                MenuListener.mainPanel.setText(null);
                LoadModelUtil.ifcontrollable = null;
                LoadModelUtil.ifobservable = null;
                LoadModelUtil.ifreliable = null;
                LoadModelUtil.badTrans = null;
                LoadModelUtil.up = null;
                LoadModelUtil.rp = null;
                LoadModelUtil.preMatrix = null;
                LoadModelUtil.posMatrix = null;
                LoadModelUtil.Trans = null;
                LoadModelUtil.iniMarking = null;
                LoadModelUtil.trueMaxTran = 0;

                PetriModel.transCount = 0;
                PetriModel.placesCount = 0;
                LoadModelUtil.marking = null;
                LoadModelUtil.transition = null;
                LoadModelUtil.clearBaseData();

            }
        }else if (declareRationButton.equals(actionEvent.getSource())) {
			JOptionPane.showMessageDialog(null, UIContants.UI_SOFTWARE_COPYRIGHT,
                    "Copyright",JOptionPane.INFORMATION_MESSAGE);
        } else if (exitButton.equals(actionEvent.getSource())) {
//			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			int r = JOptionPane.showConfirmDialog(null,"Confirm quit?",
                    "ConfirmDialog",JOptionPane.YES_NO_OPTION);
			if(r==JOptionPane.YES_OPTION){
				System.exit(0);
			}
        }

    }

}
		
