package org.xidian.model;

/**
 * 计算环路径的另一种数据表示方法
 * @author luopeng
 * @since 2018-6-4
 * @version 1.0
 */
public class Node {

	public int nodeId;
	
	public Node nextNode;
	
	public Node(int a, Node b){
		nodeId = a;
		nextNode = b;
	}
	
}
