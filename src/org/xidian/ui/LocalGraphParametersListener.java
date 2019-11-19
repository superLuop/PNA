package org.xidian.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.xidian.alg.ReachabilityGraphAlgorithm;
import org.xidian.model.StateNode;

/**
 * 局部可达图分析按钮监听类
 */
public class LocalGraphParametersListener implements ActionListener{
	
	private JTextField originalState, path;
	private JButton cancel;
	private MainPanel mainPanel;

	public LocalGraphParametersListener(JTextField originalState, 
			JTextField path, JButton cancel) {
		this.originalState = originalState;
		this.path = path;
		this.cancel = cancel;
		mainPanel = MainPanel.getInstance();
	}
	
	
	public LocalGraphParametersListener() {
		super();
	}


	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == cancel) {
			LocalGraphParametersDialog.getInstance().dispose();
		} else{ 
			if (originalState.getText().equals("")) {
				JOptionPane.showMessageDialog(null, UIContants.UI_NO_ORIGINAL_STATE_ERROR, 
						UIContants.UI_HINT, JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (path.getText().equals("")) {
				JOptionPane.showMessageDialog(null, UIContants.UI_NO_IMPORT_PATH_ERROR, 
						UIContants.UI_HINT, JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				String graphResult = new ReachabilityGraphAlgorithm().createReachabilityGraph(new StateNode(parseToArray(originalState.getText()),1,1),Integer.parseInt(path.getText()));
				mainPanel.setText(graphResult);
	     	 } catch (CloneNotSupportedException e) {
	     		JOptionPane.showMessageDialog(null,UIContants.UI_ANALYSIS_FAILURE);
	     		return;
	     	 }  
			//分析成功提示
			JOptionPane.showMessageDialog(null,UIContants.UI_ANALYSIS_SUCCESS);
			//关闭对话框
			LocalGraphParametersDialog.getInstance().dispose();
		}
	}
	
	//解析成数组
	public int[] parseToArray(String str) {
		str.trim();
		String[] tem = str.split(",");
		int[] result = new int[tem.length];
		for(int i = 0; i < tem.length; i++) {
			result[i] = Integer.parseInt(tem[i].trim());
		}
		return result;
	}

}
