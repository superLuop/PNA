package org.xidian.alg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.xidian.model.GraphModel;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.model.Transition;

/**
 * PN基础数据模型，后续分析依赖这部分数据，
 * 每次更新输入文件后，需要重新计算这部分
 * @author luopeng
 *
 */
public class BaseData {

	public static GraphModel graphModel;  //图论模型
	public static List<StateNode> deadStates;	//死锁状态
	public static Set<Integer> badStates = new HashSet<Integer>(); //坏状态编号，在进行完状态分类后确定
	public static DirectedGraph<StateNode, Transition> grapht;	//有向图(借助JGraph,后期自实现，替换掉)
	public static StateNode rootState = new StateNode(PetriModel.ininmarking.getMarking(), 1, 1); //初始状态
	
}
