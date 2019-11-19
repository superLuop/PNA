package org.xidian.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JTextField;

//import org.xidian.alg.ReachabilityGraphAlgorithm;
import org.xidian.model.PetriModel;
//import org.xidian.model.StateNode;

/**
 * 局部可达图参数对话框
 */
@SuppressWarnings("serial")
public class LocalGraphParametersDialog extends JFrame{

	private static LocalGraphParametersDialog localGraphParametersDialog;
	private JTextField originalState, path;

	public static LocalGraphParametersDialog getInstance() {
		if (localGraphParametersDialog == null)
			localGraphParametersDialog = new LocalGraphParametersDialog();
		return localGraphParametersDialog;
	}

	@SuppressWarnings("unused")
	private LocalGraphParametersDialog() {		
		setLayout(null);	
		setResizable(false);
		setSize(400, 250);
		JLabel originalStateLabel = new JLabel(UIContants.UI_GRAPH_PARAMETERS_ORIGINAL_STATE_NAME);
		JLabel pathLabel = new JLabel(UIContants.UI_GRAPH_PARAMETERS_PATH_NAME);
		originalState = new JTextField();
		path = new JTextField();
		JButton ensure = new JButton(UIContants.CONFIRM);
		JButton cancel = new JButton(UIContants.CANCEL);
		//设置中英文切换
		if (UIContants.IS_ENGLISH == 1){
			//设置控件大小和位置
			originalStateLabel.setBounds(50, 25, 90, 35);
			pathLabel.setBounds(50, 80, 90, 35);
			originalState.setBounds(150, 25, 190, 35);
			path.setBounds(150, 80, 190, 35);
			ensure.setBounds(95, 225, 90, 40);
			cancel.setBounds(205, 225, 90, 40);			
		}else{
			//设置控件大小和位置
			originalStateLabel.setBounds(20, 25, 120, 35);
			pathLabel.setBounds(20, 80, 120, 35);
			originalState.setBounds(180, 25, 190, 35);
			path.setBounds(180, 80, 190, 35);
			ensure.setBounds(100, 150, 90, 40);
			cancel.setBounds(210, 150, 90, 40);			
		}		
		//添加监听器
		LocalGraphParametersListener localGraphParametersListener = 
				new LocalGraphParametersListener(originalState, path, cancel);
		ensure.addActionListener(localGraphParametersListener);
		cancel.addActionListener(localGraphParametersListener);
		add(originalStateLabel);
		add(pathLabel);
		add(originalState);
		add(path);
		add(ensure);
		add(cancel);
		setLocationRelativeTo(null);
		//设置可见性
		setVisible(true);
	}

	public void open() {	
		originalState.setText(PetriModel.ininmarking.toString());
		
		path.setText("");
		setVisible(true);
		return;
	}
	
}
