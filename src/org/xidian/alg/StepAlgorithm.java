package org.xidian.alg;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.alg.DijkstraShortestPath;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.model.Transition;
import org.xidian.utils.PrintUtil;

/**
 * 步长算法
 * 
 * @author HanChun
 * @version 1.0 2016-10-3
 */
public class StepAlgorithm extends BaseData{
	static int maxPath = 1;  //最优步长
	public static StringBuffer badAnddeadState2 = null;
	public static String Criticals;
	public static String deadState;
	/**
	 * 1.计算出起始节点到各个死锁节点的全部(或部分)路径
	 * 2.计算出各个路径中的最短路径（路径长度）
	 * 3.计算步长
	 * 4.计算环个数
	 */
	public static String calculateDeadlockPath() {
		StringBuffer resultStr = new StringBuffer("Full Permission ：" + grapht.vertexSet().size() 
				+ ", Deadlock States Total : "+deadStates.size()+"\n");
		GraphPath<StateNode, Transition> deadShortPaths = null;
		//计算出最短路径
		for(StateNode el : deadStates) {
			DijkstraShortestPath<StateNode, Transition> dpath = new DijkstraShortestPath<StateNode, Transition>(grapht,rootState, el);
			resultStr.append("["+el.getStateNo()+"] "+el.toString()+"\n");
			deadShortPaths = dpath.getPath();
			for(Transition tran : deadShortPaths.getEdgeList()){
				resultStr.append(tran.getTranName()+"=>");
			}
			resultStr.append("Dead State["+deadShortPaths.getEndVertex().getStateNo()+"](Shortest Length)"+"\n");
		}
		resultStr.append("\n The rest of the deadlock path reference (if the state number is greater than 40, only to show which part of the path)\n");
		//显示更多的路径
		for(StateNode el: deadStates) {
			Integer calNum = null; //计算路径步长
			if(grapht.vertexSet().size() > 40) calNum = PetriModel.transCount;
			AllDirectedPaths<StateNode, Transition> allPath = new AllDirectedPaths<StateNode, Transition>((DirectedGraph<StateNode, Transition>) grapht);
			List<GraphPath<StateNode, Transition>> paths = allPath.getAllPaths(rootState, el, true, calNum);
			resultStr.append("\ndead state[" + el.getStateNo() +"]" + "The rest of the deadlock path：\n");
			for(GraphPath<StateNode, Transition> g : paths) {
	    		for(Transition t : g.getEdgeList()) {
	    			resultStr.append(t.getTranName() + "=>");
	    		}
	    		resultStr.append("dead state[" + el.getStateNo() +"]\n");
	    	}
		}
		
		//暂时考虑是：状态数低于100计算环情况
//		if(grapht.vertexSet().size() < 100) {
//			resultStr.append("\n全局可达图环路信息分析如下：\n");
//			//System.out.println("\n全局可达图环路信息分析如下：");
//			CycleDetector<StateNode, Transition> cdetector =  new CycleDetector<StateNode, Transition>(grapht);
//			System.out.println("共   "+cdetector.findCycles().size() + " 个环，其中经过节点（即可逆）的环共   " + cdetector.findCyclesContainingVertex(rootState).size() + " 个环");
//			resultStr.append("共   "+cdetector.findCycles().size() + " 个环，其中经过节点（即可逆）的环共   " + cdetector.findCyclesContainingVertex(rootState).size() + " 个环");		
//		}
		//添加步长计算
		return resultStr.toString();
		//String path = destPath;
		//FileUtil.write(destPath.substring(0, destPath.length()-3) + "path", resultStr.toString(), true);
		//路径探索
		//cyclePath = new CyclePath(graphModel);
	}
	
	/**
	 * 改进版的BFS,计算最优步长,临界关系以及最优步长路径
	 */
	public static String analyse() {
		badAnddeadState2 = new StringBuffer();
		
		
		StringBuffer resultStr = new StringBuffer("\nThe Result of Step Analysis：\n\n");
		TreeSet<Integer> cdeadlockNodes =  new TreeSet<Integer>();
		TreeSet<Integer> criticalNodes = new TreeSet<Integer>(); //临界节点
		TreeSet<Integer> deadlockNodes =  new TreeSet<Integer>();
		Queue<Integer> stateQueue = new LinkedList<Integer>();  //状态队列
		Map<Integer, Integer> depthMap = new HashMap<Integer, Integer>(); //保存反推步长
		//下标0开始
		if(deadStates!=null)
		for(StateNode node : deadStates) {
			stateQueue.add(node.getStateNo());
			cdeadlockNodes.add(node.getStateNo());
			depthMap.put(node.getStateNo(), 1);
		}
		
		int currentNode =  -1;
		List<Integer> upNodes = null;
		while(!stateQueue.isEmpty()) {
			currentNode = stateQueue.poll();//每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
			upNodes = ReachabilityGraphAlgorithm.getDistanceNodes(currentNode, 1, true);
			if(upNodes != null && !upNodes.isEmpty()) {
				for(Integer el : upNodes) {
					//获得el具体path
					if(depthMap.get(el) == null) {
						depthMap.put(el, depthMap.get(currentNode) + 1);
					} else {
						//之前出现过，暂时不更新，[潜在算法缺陷]!
					}
					if(cdeadlockNodes.containsAll(ReachabilityGraphAlgorithm.getDistanceNodes(el, depthMap.get(el) - 1, false))) {
						deadlockNodes.add(el);
						stateQueue.add(el);
					} else {
						criticalNodes.add(el);
					}
				}
			}
		}
		//BaseData.badStates.addAll(deadlockNodes); //初始化坏状态
		resultStr.append("Critical States count：" +criticalNodes.size()+"\n"+PrintUtil.printSet(criticalNodes) + "\n");
	
		Criticals = PrintUtil.printSet(criticalNodes);
		
		resultStr.append("Deadlock States count: " +deadStates.size()+"\n"+ PrintUtil.printList(deadStates) + "\n");
		resultStr.append("Bad States count：" +deadlockNodes.size()+"\n"+ PrintUtil.printSet(deadlockNodes));
		resultStr.append("\n\nS(Critical)==>S(Bad)\n");
		
		//+
		deadState = PrintUtil.printList(deadStates);
		badAnddeadState2.append(PrintUtil.printList(deadStates)+PrintUtil.printSet(deadlockNodes));
		
		
		//下面开始输出临界关系
		List<Integer> tempDownNodes = null;
//		int maxPath = 1;  //最优步长
		for(Integer cri : criticalNodes) {
			tempDownNodes = ReachabilityGraphAlgorithm.getDistanceNodes(cri, 1, false);
			if(maxPath < (depthMap.get(cri) - 1)) {
				maxPath = (depthMap.get(cri) - 1);
			}
			resultStr.append("Critical State:" + cri +"'s the Shortest Distance from the Complete Deadlock is " + (depthMap.get(cri) - 1) + "\n");
			for(Integer i : tempDownNodes) {
				if(cdeadlockNodes.contains((Integer)i) || deadlockNodes.contains((Integer)i)) {
					resultStr.append("S" + cri + "==>" + "S" + i +"\n");
				}
			}
		}
		resultStr.append("\n\nOptimal Deadlock Step Value is " + maxPath + ".\n");
		return resultStr.toString();
	}
	
}
