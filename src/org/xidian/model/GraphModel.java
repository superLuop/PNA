package org.xidian.model;

/**
 * petri reachability graph model转化为 graph model
 * @author luopeng
 * @since 2018-6-4
 * @version 1.0
 */
public class GraphModel {
	
//	private GraphModel graphtModel;
	private Matrix costMatrix; //邻接矩阵
	private Matrix arcMatrix;  //弧即变迁名称，如2:t2
	private int statesAmount;
	
	/**
	 * @param statesAmount 节点个数
	 */
	public GraphModel(int statesAmount) {
		this.statesAmount = statesAmount;
		costMatrix = new Matrix(statesAmount, statesAmount, "costMatrix");
		arcMatrix = new Matrix(statesAmount, statesAmount, "arcMatrix", true);
	}

	public Matrix getCostMatrix() {
		return costMatrix;
	}

	public void setCostMatrix(Matrix costMatrix) {
		this.costMatrix = costMatrix;
	}

	public Matrix getArcMatrix() {
		return arcMatrix;
	}

	public void setArcMatrix(Matrix arcMatrix) {
		this.arcMatrix = arcMatrix;
	}

	public int getStatesAmount() {
		return statesAmount;
	}

	public void setStatesAmount(int statesAmount) {
		this.statesAmount = statesAmount;
	}
	
}
