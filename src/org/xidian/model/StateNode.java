package org.xidian.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * State状态节点
 * @author HanChun
 * @version 1.0 2016-5-18
 */
public class StateNode implements Cloneable{
	
	private boolean isChange =  false;  //到到死锁状态的距离,是否被计算过
	private int toDeadLength = 1;  //到到死锁状态的距离
	private int[] state;
	private int stateNo;  
	private boolean ifDeadlock = false;  //是否死锁状态
	private List<StateNode> childNodes;  //该点孩子节点
	private int depth;   //节点深度
	
	/**
	 * @param state 状态
	 * @param stateNo 状态编号
	 */
	public StateNode(int[] state, int stateNo) {
		this.state = state;
		this.stateNo = stateNo;
		childNodes = new LinkedList<StateNode>();
	}
	
	/**
	 * @param state 状态
	 * @param depth 状态深度
	 * @param stateNo 状态编号
	 */
	public StateNode(int[] state, int stateNo, int depth) {
		this.depth = depth;
		this.state = state;
		this.stateNo = stateNo;
		childNodes = new LinkedList<StateNode>();
	}

	public StateNode() {
		
	}
	
	@Override
	 public int hashCode(){
		return  Arrays.hashCode(state);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		 return super.clone();
    }

	@Override
	public String toString() {
		if(this.state == null || this.state.length == 0) {
			return null;
		}
		StringBuffer ab = new StringBuffer();
		for(int i = 0; i < this.state.length; i++) {
			ab.append(this.state[i] + " ");
		}
		return ab.toString();
	}
	
	public void addPathLength() {
		toDeadLength ++;
	}

	public int[] getState() {
		return state;
	}

	public void setState(int[] state) {
		this.state = state;
	}

	public int getStateNo() {
		return stateNo;
	}

	public void setStateNo(int stateNo) {
		this.stateNo = stateNo;
	}

	public boolean isIfDeadlock() {
		return ifDeadlock;
	}

	public void setIfDeadlock(boolean ifDeadlock) {
		this.ifDeadlock = ifDeadlock;
	}

	public List<StateNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<StateNode> childNodes) {
		this.childNodes = childNodes;
	}

	public int getToDeadLength() {
		return toDeadLength;
	}

	public void setToDeadLength(int toDeadLength) {
		this.toDeadLength = toDeadLength;
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
