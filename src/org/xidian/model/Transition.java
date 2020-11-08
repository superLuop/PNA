package org.xidian.model;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * 变迁类
 * @author luopeng
 * @version 1.0 2018-6-16
 */
@SuppressWarnings("serial")
public class Transition extends DefaultWeightedEdge{
	
	private int[] Transition;

	private int tranName;
	
	public Transition() {
		
	}

	public Transition(int[] transition) {
		Transition = transition;
	}

	public int[] getTransition() {
		return Transition;
	}

	public void setTransition(int[] transition) {
		Transition = transition;
	}

	public int getTranName() {
		return tranName;
	}

	public void setTranName(int tranName) {
		this.tranName = tranName;
	}

}
