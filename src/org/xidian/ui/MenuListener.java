package org.xidian.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.xidian.alg.*;

/**
 * 菜单按钮监听类
 * @author HanChun wss LP
 */
public class MenuListener implements ActionListener{
	int i = 0;
	int j = 0;
	String UCtemp;
	String Ctemp;

	private JButton basicPropertyButton, reachabilityGraphButton, localReachabilityGraphButton,
			pathButton, siphonAnalysisButton, inequationButton,robustButton,uncontrollableandunobservableButton,unobservableReachabilityButton,unControllableReachabilityGraphButton,StepPredictButton,ControlStepPredictButton;

	protected static MainPanel mainPanel;

	public MenuListener(JButton basicPropertyButton, JButton reachabilityGraphButton,JButton localReachabilityGraphButton, JButton pathButton, JButton siphonAnalysisButton, JButton inequationButton,JButton unControllableReachabilityGraphButton,JButton unobservableReachabilityButton,JButton uncontrollableandunobservableButton,JButton robustButton,JButton StepPredictButton,JButton ControlStepPredictButton) {
		this.basicPropertyButton = basicPropertyButton;
		this.reachabilityGraphButton = reachabilityGraphButton;
		this.localReachabilityGraphButton = localReachabilityGraphButton;
		this.pathButton = pathButton;
		this.siphonAnalysisButton = siphonAnalysisButton;
		this.inequationButton = inequationButton;
		this.unControllableReachabilityGraphButton = unControllableReachabilityGraphButton;
		this.unobservableReachabilityButton = unobservableReachabilityButton;
		this.uncontrollableandunobservableButton = uncontrollableandunobservableButton;
		this.robustButton = robustButton;
		this.StepPredictButton = StepPredictButton;
		this.ControlStepPredictButton = ControlStepPredictButton;
		mainPanel = MainPanel.getInstance();
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		if (actionEvent.getSource() == basicPropertyButton) {
			//暂时只提供前置后置矩阵
			//TODO
			return;
		}else if (actionEvent.getSource() == reachabilityGraphButton) {
			try {
				String graphResult = new ReachabilityGraphAlgorithm().createReachabilityGraph(null, 0);
//				String str = StepAlgorithm.analyse();
				mainPanel.setText(graphResult);
//				mainPanel.setText(graphResult+"\n"+str.trim().subSequence("The Result of Step Analysis".length()+2, str.trim().indexOf("S(Critical)==>S(Bad)")));
			} catch (CloneNotSupportedException e) {
				JOptionPane.showMessageDialog(null,UIContants.UI_ANALYSIS_FAILURE);
				return;
			}
			return;
		}else if (actionEvent.getSource() == localReachabilityGraphButton) {
			LocalGraphParametersDialog.getInstance().open();
			return;
		}else if (actionEvent.getSource() == pathButton) {
			String result = StepAlgorithm.analyse();
			mainPanel.setText(result);
			return;
		}else if (actionEvent.getSource() == siphonAnalysisButton) {
			 String result = new SiphonAlgorithm().analyse();

			 mainPanel.setText(result);
			return;
		}else if (actionEvent.getSource() == inequationButton) {
			String result = new InequalityAlgorithm().analyse();
			mainPanel.setText(result);
			return;
		}else if(actionEvent.getSource() == robustButton){
			new RobustAlgorithm();
			String result = RobustAlgorithm.check();
			mainPanel.setText(result);
			return;
		}
		else if(actionEvent.getSource() == uncontrollableandunobservableButton){
			new UnControlAndUnObserveAlgorithm();
			String result = UnControlAndUnObserveAlgorithm.check();
			mainPanel.setText(result);
			return;
		}
		else if(actionEvent.getSource() == unobservableReachabilityButton){
			new UnobservableReachability();
			String result = UnobservableReachability.check();
			mainPanel.setText(result);
			return;
		}else if(actionEvent.getSource() == unControllableReachabilityGraphButton) {

			String result = UnControllableReachabilityGraphAlgorithm.test();
			mainPanel.setText(result);
			return;
		}else if(actionEvent.getSource() == StepPredictButton) {
			try {
				if(i==0){
					String result = UnControlStepPredict.calculateOptimalStep();
					mainPanel.setText(result);
					UCtemp = result;
					i++;
				}

				if(i>0){
					mainPanel.setText(UCtemp);
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,UIContants.UI_ANALYSIS_FAILURE);
				return;
			}
			return;
		}else if(actionEvent.getSource() == ControlStepPredictButton){
			try {
				if(j==0){
					String result = ControlStepPredict.calculateCritical();
					mainPanel.setText(result);
					Ctemp = result;
					j++;
				}
				if(j>0){
					mainPanel.setText(Ctemp);
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,UIContants.UI_ANALYSIS_FAILURE);
				return;
			}
			return;
		}else {
			JOptionPane.showMessageDialog(null, UIContants.UI_IN_DEVELOPMENT);
		}
//	    return;
	}

}

