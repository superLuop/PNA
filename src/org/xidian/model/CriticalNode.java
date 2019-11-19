package org.xidian.model;

/**
 * 状态分类
 * @author HanChun
 * @since 2016-6-6
 * @version 1.0
 */
public class CriticalNode {
	
	private Integer type;  //0:好状态，1：邻界状态，2：死锁状态，3：完全死锁状态
	private Integer nodeNum;  //状态编号
	
	public CriticalNode(Integer type, Integer nodeNum) {
		this.type = type;
		this.nodeNum = nodeNum;
	}
	
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getNodeNum() {
		return nodeNum;
	}
	
	public void setNodeNum(Integer nodeNum) {
		this.nodeNum = nodeNum;
	}

}
