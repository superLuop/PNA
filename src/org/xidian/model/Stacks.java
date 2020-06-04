package org.xidian.model;

/**
 * 图的邻接表表示形式
 * @author luopeng
 * @since 2018-6-14
 * @version 1.0
 */
public class Stacks {

	public Node top;
	
	public Stacks(Node node){
		top = null;
	}
	
	public void push(int a){
		if(top == null){
			top = new Node(a, null);
		}else{
			top = new Node(a, top);
		}
	}
	
	public void pop(){
		top = top.nextNode;
	}
}
