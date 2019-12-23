package org.xidian.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

//import org.jgrapht.DirectedGraph;
//import org.jgrapht.GraphPath;
//import org.jgrapht.alg.AllDirectedPaths;
//import org.jgrapht.alg.CycleDetector;
//import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.xidian.model.GraphModel;
import org.xidian.model.Marking;
import org.xidian.model.Matrix;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.model.Transition;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;

/**
 * 可达图（广义）数据模型
 *
 * @author HanChun
 * @version 2.0 2016-10-2 (1)自定义方式生成局部可达图;
 */
public class ReachabilityGraphAlgorithm extends BaseData {


    public static List<List<Integer>> adjlist = null;
    public static Map<Integer, StateNode> resu = null;

    //存放死锁状态
    public static List<String> deadstate = null;
    public static List<StateNode> deadlockStates = null;

    public static Map<Integer, StateNode> preStatesMap; //已发现状态集合
    public static int statesAmout; //记录状态总数

    /**
     * 自定义方式生成可达图 (可局部)
     *
     * @param initNode 自定义初始marking
     * @param step     步长, 为0表示不限制步长，其他表示一次分析最远步长
     * @throws CloneNotSupportedException
     */
    public static String createReachabilityGraph(StateNode initNode, int step)
            throws CloneNotSupportedException {

        adjlist = new ArrayList<List<Integer>>();
        resu = new HashMap<Integer, StateNode>();
        deadstate = new ArrayList<String>();
        deadlockStates = new LinkedList<StateNode>();


        if (initNode == null) {
            initNode = rootState;
        }
        String str = "Initial State [" + initNode.toString().trim()
                + "], Path=" + step + "\n";
        if (step == 0) {
            step = Integer.MAX_VALUE - 2;
            str = "Initial State [" + initNode.toString().trim() + "]\n";
        }
        //初始化200个状态
        preStatesMap = new HashMap<Integer, StateNode>(200);
        StringBuffer resultStr = new StringBuffer(str);
        StringBuffer deadStateStr = new StringBuffer("\n");
        int stateCount = 1;
        int deadStateCount = 0;
        //当前状态下，能够发射的变迁
        Stack<Integer> nextTrans = new Stack<Integer>();
        Queue<StateNode> stateQueue = new LinkedList<StateNode>();
        Marking temState = null;
        StateNode currentState = initNode;
        //过程中探索到的状态
        StateNode duringState = null;
        preStatesMap.put(currentState.hashCode(), currentState);
        //根状态为起始状态
        stateQueue.add(initNode);
        boolean[] canFire = null;


        while (!stateQueue.isEmpty()) {
            //超过步长,跳出循环
            if (currentState.getDepth() > (step + 1)) {
                break;
            }
            //每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
            currentState = stateQueue.poll();
            resultStr.append("\nState nr:" + currentState.getStateNo() + "\n"
                    + PrintUtil.printPlaces() + "\n" + "toks: "
                    + currentState + "\n");
//			System.out.println("------------------------"+currentState.getStateNo());
            canFire = getEnabledTrans(currentState);
            for (int i = 0; i < canFire.length; i++) {
                if (canFire[i]) {
                    nextTrans.push(i);
                }
            }

            resu.put(currentState.getStateNo(), currentState);
//			System.out.println(currentState);

            List<Integer> ls = new ArrayList<Integer>();

            //超过步长,不再计算可发射变迁
            if (currentState.getDepth() > step) {
                continue;
            }

            //死锁状态
            if (nextTrans.isEmpty()) {
                currentState.setIfDeadlock(true);
                resultStr.append("Deadlock States\n");
                deadStateStr.append(currentState.getStateNo() + ": "
                        + currentState + "\n");
                deadStateCount++;
                //得到死锁状态
                deadstate.add(currentState.toString().trim());
                deadlockStates.add(currentState);
            } else {
                while (!nextTrans.isEmpty()) {

                    ls.add(currentState.getStateNo());

                    if (currentState.getDepth() > (step + 1)) {
                    }
                    resultStr.append("==" + "t" + (nextTrans.peek() + 1));

//					System.out.println("++++++++++++"+(nextTrans.peek() + 1));
                    ls.add((nextTrans.peek() + 1));


                    temState = fire(currentState, nextTrans.pop());
                    //新状态
                    if (!ifOccured(temState)) {
                        stateCount++;
                        duringState = new StateNode(temState.marking, stateCount,
                                currentState.getDepth() + 1);
                        resultStr.append("==>" + "s" + duringState.getStateNo() + "\n");

//						System.out.println("****************"+duringState.getStateNo());
                        //	addEdge(currentState.getStateNo(),duringState.getStateNo(),(nextTrans.peek() + 1));
                        ls.add(duringState.getStateNo());

                        preStatesMap.put(temState.hashCode(), duringState);
                        stateQueue.add((StateNode) duringState.clone());
                        //旧状态
                    } else {
                        resultStr.append("==>" + "s"
                                + preStatesMap.get(temState.hashCode()).getStateNo() + "\n");
                        ls.add(preStatesMap.get(temState.hashCode()).getStateNo());
                    }

                }
                adjlist.add(ls);
            }
        }


//		System.out.println(adjlist);

        statesAmout = stateCount;
        resultStr.append("\nTotal states count: " + statesAmout + "\n");
        resultStr.append("\nTotal deadlock state counts: " + deadStateCount + "\n" + deadStateStr);
//		System.out.println(deadStateStr);
        //如果状态数低于10000，再初始化图数据结构，后期需要更改数据结构！矩阵的表示方法改成邻接表形式！
        if (statesAmout < 10000) {
            //resultStr.append(calculateDeadlockPath());
            initGrapht();
            //如果生成全局可达图，则再次生成图的数据模型，需要优化！！！
            if (step == Integer.MAX_VALUE - 2) {
                graphModel = traverseReachabilityGraph();
            }
        }

        return resultStr.toString();
        //System.out.println(resultStr.toString());	//for debug
    }

    /**
     * see createReachabilityGraph(),该方法在createReachabilityGraph()上面做了优化（之所以不在上面直接改，是为了保证基础版的足够简单）
     * 使用jgrapht开源包，实现遍历可达图，生成图数据结构，然后借助相应的办法
     *
     * @throws CloneNotSupportedException
     */
    public static GraphModel traverseReachabilityGraph() throws CloneNotSupportedException {
        preStatesMap = new HashMap<Integer, StateNode>(1000); //初始化1000个状态
        deadStates = new LinkedList<StateNode>();
        grapht = new DirectedPseudograph<StateNode, Transition>(Transition.class);
        int stateCount = 1;    //状态数
        Stack<Integer> nextTrans = new Stack<Integer>(); //当前状态下，能够发射的变迁
        Queue<StateNode> stateQueue = new LinkedList<StateNode>();  //状态队列
        Marking temState = null;
        Transition currentTran = null;
        int currentTranName = 0;
        StateNode currentState = rootState;
        StateNode duringState = null;  //过程中探索到的状态
        preStatesMap.put(currentState.hashCode(), currentState);//根节点加入
        stateQueue.add(rootState); //根状态为起始状态
        grapht.addVertex(rootState);
        boolean[] canFire = null;
        while (!stateQueue.isEmpty()) {
            currentState = stateQueue.poll();//每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
            canFire = getEnabledTrans(currentState);
            for (int i = 0; i < canFire.length; i++) {
                if (canFire[i]) nextTrans.push(i);
            }
            //死锁状态
            if (nextTrans.isEmpty()) {
                deadStates.add(preStatesMap.get(currentState.hashCode()));
                currentState.setIfDeadlock(true);
            } else {
                while (!nextTrans.isEmpty()) {
                    //构建图的数据结构
                    currentTranName = nextTrans.peek();
                    //transition 命名
                    currentTran = new Transition();
                    currentTran.setTranName(nextTrans.peek() + 1);
                    temState = fire(currentState, nextTrans.pop());
                    //新状态
                    if (!ifOccured(temState)) {
                        stateCount++;
                        //变迁信息
                        duringState = new StateNode(temState.marking, stateCount);//新节点，之前没有遇到过
                        grapht.addVertex(duringState);
                        currentState.getChildNodes().add(duringState);
                        currentTran = grapht.addEdge(preStatesMap.get(currentState.hashCode()), duringState);
                        currentTran.setTranName(currentTranName);

                        //下面转为graph model
                        graphModel.getCostMatrix().getMatrix()[preStatesMap.get(currentState.hashCode()).getStateNo() - 1][duringState.getStateNo() - 1] = 1 + currentTranName;
                        //graphModel.getArcMatrix().getMatrix()[preStatesMap.get(currentState.hashCode()).getStateNo()-1][duringState.getStateNo()-1] = currentTranName; //下标正常，1开始
                        preStatesMap.put(temState.hashCode(), duringState);
                        stateQueue.add((StateNode) duringState.clone());
                        //旧状态
                    } else {
                        currentState.getChildNodes().add(preStatesMap.get(temState.hashCode()));
                        currentTran = grapht.addEdge(preStatesMap.get(currentState.hashCode()), preStatesMap.get(temState.hashCode()));
                        currentTran.setTranName(currentTranName);
                        //下面转为graph model
                        graphModel.getCostMatrix().getMatrix()[preStatesMap.get(currentState.hashCode()).getStateNo() - 1][preStatesMap.get(temState.hashCode()).getStateNo() - 1] = 1 + currentTranName;
//						graphModel.getArcMatrix().getMatrix()[preStatesMap.get(currentState.hashCode()).getStateNo()-1][preStatesMap.get(temState.hashCode()).getStateNo()-1] = currentTranName;

                    }
                }
            }
        }
        //Matrix.printMatrix(graphModel.getCostMatrix().getMatrix());
        return graphModel;
    }

    /**
     * 初始化graph model
     */
    public static void initGrapht() {
        graphModel = new GraphModel(statesAmout);
    }

    /**
     * 1.计算出起始节点到各个死锁节点的全部(或部分)路径
     * 2.计算出各个路径中的最短路径（路径长度）
     * 3.计算步长
     * 4.计算环个数
     */
//	public static String calculateDeadlockPath() {
//		StringBuffer resultStr = new StringBuffer("Complete Deadlock States Information：\n\n");
//		GraphPath<StateNode, Transition> deadShortPaths = null;
//		//计算出最短路径
//		for(StateNode el : deadStates) {
//			DijkstraShortestPath<StateNode, Transition> dpath = new DijkstraShortestPath<StateNode, Transition>(grapht,rootState, el);
//			resultStr.append("["+el.getStateNo()+"] "+el.toString()+"\n");
//			deadShortPaths = dpath.getPath();
//			for(Transition tran : deadShortPaths.getEdgeList()){
//				resultStr.append(tran.getTranName()+"=>");
//			}
//			resultStr.append("Complete Deadlock State["+deadShortPaths.getEndVertex().getStateNo()+"](Shortest Path)"+"\n");
//		}
//		resultStr.append("\nOther Deadlock Paths(if n > 40, Only Part of These Paths are Displayed)\n");
//		//显示更多的路径
//		for(StateNode el: deadStates) {
//			Integer calNum = null; //计算路径步长
//			if(grapht.vertexSet().size() > 40) calNum = PetriModel.transCount;
//			AllDirectedPaths<StateNode, Transition> allPath = new AllDirectedPaths<StateNode, Transition>((DirectedGraph<StateNode, Transition>) grapht);
//			List<GraphPath<StateNode, Transition>> paths = allPath.getAllPaths(rootState, el, true, calNum);
//			resultStr.append("\nComplete Deadlock State[" + el.getStateNo() +"]" + " Other Paths：\n");
//			for(GraphPath<StateNode, Transition> g : paths) {
//	    		for(Transition t : g.getEdgeList()) {
//	    			resultStr.append(t.getTranName() + "=>");
//	    		}
//	    		resultStr.append("Complete Deadlock State[" + el.getStateNo() +"]\n");
//	    	}
//		}

    //暂时考虑是：状态数低于100计算环情况
//		if(grapht.vertexSet().size() < 100) {
//			resultStr.append("\n全局可达图环路信息分析如下：\n");
//			//System.out.println("\n全局可达图环路信息分析如下：");
//			CycleDetector<StateNode, Transition> cdetector =  new CycleDetector<StateNode, Transition>(grapht);
//			System.out.println("共   "+cdetector.findCycles().size() + " 个环，其中经过节点（即可逆）的环共   " + cdetector.findCyclesContainingVertex(rootState).size() + " 个环");
//			resultStr.append("共   "+cdetector.findCycles().size() + " 个环，其中经过节点（即可逆）的环共   " + cdetector.findCyclesContainingVertex(rootState).size() + " 个环");
//		}
//      cyclePath = new CyclePath(graphModel);
//		return resultStr.toString();
//	}

    /**
     * 发射指定变迁
     *
     * @param transIndex   变迁编号
     * @param currentState 当前状态
     * @return 发射之后的新状态
     */
    public static Marking fire(StateNode currentState, int transIndex) {
        Marking newMarking = new Marking(PetriModel.placesCount);
        for (int i = 0; i < currentState.getState().length; i++) {
            newMarking.marking[i] = currentState.getState()[i]
                    + PetriModel.preMatrix.getValue(i, transIndex)
                    - PetriModel.posMatrix.getValue(i, transIndex);
        }
        return newMarking;
    }

    /**
     * 得到当前状态下能够发射的变迁
     *
     * @param currentState 当前状态
     * @return boolean[] true满足使能条件
     */
    public static boolean[] getEnabledTrans(StateNode currentState) {
        boolean[] result = new boolean[PetriModel.transCount];
        for (int i = 0; i < result.length; i++) {
            result[i] = true;
        }
        for (int i = 0; i < PetriModel.transCount; i++) {
            for (int j = 0; j < PetriModel.placesCount; j++) {
                if ((currentState.getState()[j]
                        < PetriModel.posMatrix.getValue(j, i))) {
                    result[i] = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 由GraphModel 得到上step层节点（后往前推）
     * map中key是返回的相应层状态
     * 对应就是邻接矩阵的列向量
     * 邻接矩阵某一列，相当于某节点的上一层状态
     */
    public static List<Integer> getDistanceNodes(int stateNo, int step, boolean ifFoward) {
        List<Integer> result = new ArrayList<>(getStates(stateNo, ifFoward));
        Set<Integer> occuredSet = new HashSet<Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        int current = -1;
        while (step > 1 && result != null && result.size() != 0) {
            queue.addAll(result);
            result.removeAll(result);
            while (!queue.isEmpty()) {
                current = queue.poll();
                for (Integer el : getStates(current, ifFoward)) {
                    if (!occuredSet.contains(el)) {
                        occuredSet.add(el);
                        result.add(el);
                    }
                }
            }
            step--;
        }
        return result;
    }

    /**
     * 得到上一层或者上一层非0元素
     * 0开始下标！！
     *
     * @param ifFoward
     * @return
     */
    public static List<Integer> getStates(int stateNo, boolean ifFoward) {
        return ifFoward ? Matrix.getElementsExceptZero(Matrix.getMatrixCol(stateNo - 1, graphModel.getCostMatrix().getMatrix())) :
                Matrix.getElementsExceptZero(Matrix.getMatrixRow(stateNo - 1, graphModel.getCostMatrix().getMatrix()));
    }

    /**
     * 由GraphModel 得到上一层节点（后往前推）
     * 对应就是邻接矩阵的列向量
     *
     * @return boolean[]
     */
    public List<Integer> getDownNodes(int currentNode) {
        //记录变迁是否能发射结果
        List<Integer> result = new LinkedList<Integer>();
        int[] temArray = Matrix.getMatrixRow(currentNode, graphModel.getCostMatrix().getMatrix());
        for (int i = 0; i < temArray.length; i++) {
            if (temArray[i] != 0) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * 检查当前状态是否已经在deadlock 状态里面
     *
     * @param set
     * @return boolean true:deadlock false:邻界节点
     */
    public static boolean ifOccuredInDeadlock(List<Integer> list, Set<Integer> set) {
        //只要有一个节点不在死锁节点集合里面，则为临界节点
        for (Integer el : list) {
            if (!set.contains(el)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前状态是否已经发生过
     *
     * @param node 待检查状态
     * @return boolean true:发生过
     */
    public static boolean ifOccured(Marking node) {
        return preStatesMap.containsKey(node.hashCode());
    }

    /**
     * 检查当前状态是否已经发生过
     *
     * @param node 待检查状态
     * @return boolean true:发生过
     */
    public boolean ifOccured2(StateNode node) {
        return preStatesMap.containsKey(node.hashCode());
    }
}
