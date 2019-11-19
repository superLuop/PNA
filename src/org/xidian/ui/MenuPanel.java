package org.xidian.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
//import java.awt.Graphics;
import java.awt.Toolkit;

//import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MenuPanel extends JPanel {	
	   
	public MenuPanel() {		
		//设置布局
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));		    
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int)(screen.getWidth()*0.17), (int)(screen.getHeight()*0.8)));
		this.setOpaque(false);		
		//设置按钮样式
		ImageButton basicPropertyButton = new ImageButton( "menu",UIContants.UI_BISIC_PROPERTY,16);
		ImageButton reachabilityGraphButton = new ImageButton( "menu",UIContants.UI_REACHABILITY_GRAPH_BUTTON_NAME,16);
		ImageButton localReachabilityGraphButton = new ImageButton("menu",UIContants.UI_LOCAL_REACHABILITY_GRAPH_BUTTON_NAME,16);
		ImageButton pathButton = new ImageButton("menu",UIContants.UI_PATH_BUTTON_NAME,16);
		ImageButton siphonAnalysisButton = new ImageButton("menu",UIContants.UI_SIPHON_ANALYSIS,16);
		ImageButton inequationButton = new ImageButton("menu",UIContants.UI_INEQUATION,16);
		ImageButton dynamicPathButton = new ImageButton("menu",UIContants.UI_DYNAMIC_PATH_ANALYSIS,16);	
		ImageButton unControllableReachabilityGraphButton = new ImageButton("menu",UIContants.UI_UNCONTROLLABLE_ANALYSIS,16);
		ImageButton unobservableReachabilityButton = new ImageButton("menu",UIContants.UI_UNOBSERVABLE_ANALYSIS,16);
		ImageButton uncontrollableandunobservableButton = new ImageButton("menu",UIContants.UI_UNCONTROLLABLEANDUNOBSERVABLE_ANALYSIS,16);
		ImageButton unreliableButton = new ImageButton("menu",UIContants.UI_UNRELIABLE_ANALYSIS,16);
		ImageButton ControlStepPredictButton = new ImageButton("menu",UIContants.UI_CONTROLSTEPPREDICT_ANALYSIS,16);
		ImageButton StepPredictButton = new ImageButton("menu",UIContants.UI_STEPPREDICT_ANALYSIS,16);
		//设置按钮大小
		int width = (int) (screen.getWidth()*0.15);
		int height = (int) (screen.getHeight()*0.050);
		basicPropertyButton.setPreferredSize(new Dimension(width, height));
		reachabilityGraphButton.setPreferredSize(new Dimension(width, height));
		localReachabilityGraphButton.setPreferredSize(new Dimension(width, height));
		pathButton.setPreferredSize(new Dimension(width, height));
		siphonAnalysisButton.setPreferredSize(new Dimension(width, height));
		inequationButton.setPreferredSize(new Dimension(width, height));
		dynamicPathButton.setPreferredSize(new Dimension(width, height));
		unControllableReachabilityGraphButton.setPreferredSize(new Dimension(width, height));
		unobservableReachabilityButton.setPreferredSize(new Dimension(width,height));
		uncontrollableandunobservableButton.setPreferredSize(new Dimension(width,height));
		unreliableButton.setPreferredSize(new Dimension(width,height));
		ControlStepPredictButton.setPreferredSize(new Dimension(width, height));
		StepPredictButton.setPreferredSize(new Dimension(width, height));
		//添加监听器		
		MenuListener menuListener = new MenuListener(basicPropertyButton, reachabilityGraphButton, localReachabilityGraphButton, pathButton, siphonAnalysisButton, inequationButton,unControllableReachabilityGraphButton,unobservableReachabilityButton,uncontrollableandunobservableButton,unreliableButton,StepPredictButton,ControlStepPredictButton);
		basicPropertyButton.addActionListener(menuListener);
		reachabilityGraphButton.addActionListener(menuListener);
		localReachabilityGraphButton.addActionListener(menuListener);
		pathButton.addActionListener(menuListener);
		siphonAnalysisButton.addActionListener(menuListener);	
		inequationButton.addActionListener(menuListener);	
		dynamicPathButton.addActionListener(menuListener);	
		unControllableReachabilityGraphButton.addActionListener(menuListener);
		unobservableReachabilityButton.addActionListener(menuListener);
		uncontrollableandunobservableButton.addActionListener(menuListener);
		unreliableButton.addActionListener(menuListener);
		StepPredictButton.addActionListener(menuListener);
		ControlStepPredictButton.addActionListener(menuListener);
		//设置按钮默认背景边框可见
		basicPropertyButton.setBorderPainted(false);	
		basicPropertyButton.setContentAreaFilled(false);
		reachabilityGraphButton.setBorderPainted(false);	
		reachabilityGraphButton.setContentAreaFilled(false);
		localReachabilityGraphButton.setBorderPainted(false);	
		localReachabilityGraphButton.setContentAreaFilled(false);
		pathButton.setBorderPainted(false);	
		pathButton.setContentAreaFilled(false);
		siphonAnalysisButton.setBorderPainted(false);	
		siphonAnalysisButton.setContentAreaFilled(false);
		inequationButton.setBorderPainted(false);	
		inequationButton.setContentAreaFilled(false);
		dynamicPathButton.setBorderPainted(false);	
		dynamicPathButton.setContentAreaFilled(false);		
		unControllableReachabilityGraphButton.setBorderPainted(false);	
		unControllableReachabilityGraphButton.setContentAreaFilled(false);
		unobservableReachabilityButton.setBorderPainted(false);
		unobservableReachabilityButton.setContentAreaFilled(false);
		uncontrollableandunobservableButton.setBorderPainted(false);
		uncontrollableandunobservableButton.setContentAreaFilled(false);
		unreliableButton.setBorderPainted(false);
		unreliableButton.setContentAreaFilled(false);
		StepPredictButton.setBorderPainted(false);	
		StepPredictButton.setContentAreaFilled(false);
		ControlStepPredictButton.setBorderPainted(false);	
		ControlStepPredictButton.setContentAreaFilled(false);
		//添加按钮
		add(basicPropertyButton);
		add(reachabilityGraphButton);
		add(localReachabilityGraphButton);
		add(pathButton);
		add(siphonAnalysisButton); 
		add(inequationButton);
		add(dynamicPathButton);
	    add(unControllableReachabilityGraphButton);
	    add(unobservableReachabilityButton);
	    add(uncontrollableandunobservableButton);
	    add(unreliableButton);
		add(ControlStepPredictButton);
		add(StepPredictButton);
		//设置可见性		
		setVisible(true);
	}

}
