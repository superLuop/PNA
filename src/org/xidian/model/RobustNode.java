package org.xidian.model;

 public class RobustNode{
	private int value;
	private int depth;
	
	public RobustNode(int value, int depth) {
		this.value = value;
		this.depth = depth;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}