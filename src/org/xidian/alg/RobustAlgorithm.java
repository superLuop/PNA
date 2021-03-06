package org.xidian.alg;

import java.util.*;

import org.xidian.model.Matrix;
import org.xidian.model.RobustNode;
import org.xidian.model.StateNode;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;

/**
 * @author PLuo;
 * @Description 含有不可靠资源的状态分析算法（鲁棒性分析）
 * @CreateDate [2018-7-31]
 * @update [2020-11-7]
 */
public class RobustAlgorithm extends BaseData {
    static int[][] StateShift;
    static LinkedList<Integer> originalBadState = null;
    static LinkedList<Integer> badState = null;
    static Set<Integer> badTrans = null;
    static LinkedList<Integer> up = null;
    static ReachabilityGraphAlgorithm rg = null;
    static StringBuffer stateResult = null;

    static List<Integer> criticalState = null;
    static List<Integer> deadStates1 = null;
    static Set<Integer> badRobustStates = null;
    static Deque<Integer> extendBadRobust;
    static Set<Integer> badAnddeadStates = null;
    static Map<Integer, StateNode> allStatesMap = null; //所有状态集合
    static int step = 0;//稳健步长

    public static String check() {

        stateResult = new StringBuffer();
        try {
            rg = new ReachabilityGraphAlgorithm();
            rg.createReachabilityGraph(null, 0);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        allStatesMap = new HashMap<Integer, StateNode>();
        allStatesMap = ReachabilityGraphAlgorithm.resu;
//   	    for (Integer key : allStatesMap.keySet()) {
//   	    	stateResult.append("CurrentStateNo:"+key+"==>"+"Marking:"+allStatesMap.get(key)+"\n");//输出所有状态集合
//		}

        badTrans = LoadModelUtil.badTrans;
//		stateResult.append("The bad transitions are：");
        List<Integer> badTranList = new ArrayList<>(badTrans);
//	 	for(int m = 0;m<list.size();m++){  
//			stateResult.append(list.get(m)+" ");
//		}


        //含有不可控和不可观变迁时的坏死状态
        List<Integer> badAndDeadState = UnControlAndUnObserveAlgorithm.badAnddeadState;

        if (badAndDeadState != null && (badAndDeadState.size() > 0)) {

            originalBadState = new LinkedList<Integer>();
            badAnddeadStates = new HashSet<Integer>();
            for (int badanddeadstate : badAndDeadState) {
                badAnddeadStates.add(badanddeadstate);
                originalBadState.add(badanddeadstate);
            }

            //死锁状态
            List<StateNode> deadStates = ReachabilityGraphAlgorithm.deadStates;
            String deadState1 = PrintUtil.printList(deadStates);
            String[] deadState2 = deadState1.trim().split(" ");
            deadStates1 = new LinkedList<Integer>();
            for (String s : deadState2) {
                deadStates1.add(Integer.parseInt(s.trim()));
            }

            //状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
            List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;

            int totalsize = adj.size() + deadState2.length;
            StateShift = new int[totalsize + 1][totalsize + 1];

            for (List<Integer> list : adj) {
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {
                        addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                    }
                }
            }

            originalBadState.removeAll(deadStates1);

            RobustAlgorithm.robustPermission(badTranList);
//    	 	System.out.println(badAnddeadStates);

            //临界状态
            List<Integer> criticalState1 = UnControlAndUnObserveAlgorithm.criticalState;
            criticalState = new LinkedList<Integer>();
            criticalState.addAll(criticalState1);

            int totalstate = ReachabilityGraphAlgorithm.statesAmout;
            List<Integer> notBadState = new LinkedList<Integer>();
            for (int i = 1; i <= totalstate; i++) {
                notBadState.add(i);
            }
            notBadState.removeAll(badAnddeadStates);
            for (int n : badAnddeadStates) {
                for (int m : notBadState) {
                    if (StateShift[m][n] > 0 && badAnddeadStates.contains(n) && !badAnddeadStates.contains(m) && !criticalState.contains(m)) {
                        criticalState.add(m);
                    }
                }
            }

            criticalState.removeAll(badAnddeadStates);

            //临界状态下需要控制的变迁
            Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
            for (int c : criticalState) {
                map.put(c, new ArrayList<Integer>());
                for (int b : badAnddeadStates) {
                    if (StateShift[c][b] > 0) {
                        map.get(c).add(StateShift[c][b]);
//     			stateResult.append(c+"-->The transition that needs to be controlled in this state is : "+"t"+StateShift[c][b]+"\n");
                    }
                }
            }

            //不可靠资源
            stateResult.append("\nThe Result of Robust Analysis：");
            if (!badTrans.isEmpty()) {
                stateResult.append("\n\nThe places of unreliable resource are：");
                up = LoadModelUtil.up;
                for (Integer item : up) {
                    stateResult.append("P").append(item).append(" ");
                }
            }

            //所有状态数
            stateResult.append("\n\nTotal number of states：").append(totalstate);

            //输出最大许可行为
            stateResult.append("\n\nThe maximum permissive behaviors of the net are: (total ").append(notBadState.size()).append(" states)\n");
            for (Integer maxPermissive : notBadState) {
                stateResult.append(maxPermissive).append(" ");
            }

            //输出所有好状态
            stateResult.append("\n\nThe count of good states：").append(totalstate - badAnddeadStates.size() - criticalState.size()).append("\n");
            stateResult.append("The good states are：");
            for (int n = 1; n <= totalstate; n++) {
                if (!badAnddeadStates.contains(n) && !criticalState.contains(n)) {
                    stateResult.append(n).append(" ");
                }
            }

            //输出所有临界状态
            stateResult.append("\n\nThe count of critical states：").append(criticalState.size());
            stateResult.append("\nThe critical states are：\n");
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet) {
                stateResult.append(key).append("-->The transition that needs to be controlled in this state is : ");
                for (int value : map.get(key))
                    stateResult.append("t").append(value).append("  ");
                stateResult.append("\n");
            }


            //由不可靠资源导致的不稳健状态
            if (!badTrans.isEmpty()) {
                Set<Integer> tmpNotRobustSet = new HashSet<>(badRobustStates);
                tmpNotRobustSet.removeAll(badAndDeadState);
                stateResult.append("\n\nThe count of bad robust states：").append(tmpNotRobustSet.size());
                stateResult.append("\nThe bad robust states are：");
                stateResult.append(PrintUtil.printSet(tmpNotRobustSet));
                printBadRobust(tmpNotRobustSet);
            }

            //输出所有坏状态
            badState = new LinkedList<Integer>();
            badState.addAll(badAnddeadStates);
            badState.removeAll(deadStates1);
            badState.removeAll(badRobustStates);
            stateResult.append("\n\nThe count of bad states：").append(badState.size());
            stateResult.append("\nThe bad states are：");
            for (Integer value : badState) {
                stateResult.append(value).append(" ");
            }

            //输出所有死锁状态
            stateResult.append("\n\nThe count of deadlock states：").append(deadStates1.size());
            stateResult.append("\nThe deadlock states are：");
            for (Integer integer : deadStates1) {
                stateResult.append(integer).append(" ");
            }

            //输出稳健步长
//            if (step == -1)
//                stateResult.append("\n\nNo robust step!");
//            else
//                stateResult.append("\n\n\nThe robust step is : " + step);
        } else {
            //原来不含有坏死状态，即原网无死锁
            stateResult.append("\nThe original Petri net has no deadlock,but due to the existence of unreliable resources:");
            up = LoadModelUtil.up;
            for (Integer integer : up) {
                stateResult.append("P").append(integer).append(" ");
            }
            stateResult.append("\nThe system is not robust,the details are as follows:");

            originalBadState = new LinkedList<Integer>();
            badAnddeadStates = new HashSet<Integer>();

            RobustAlgorithm.robustPermission(badTranList);

            //状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
            List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;
            int totalSize = adj.size();
            StateShift = new int[totalSize + 1][totalSize + 1];

            for (List<Integer> list : adj) {
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {
                        addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                    }
                }
            }

            //临界状态
            int totalstate = ReachabilityGraphAlgorithm.statesAmout;
            List<Integer> notBadState = new LinkedList<Integer>();
            for (int i = 1; i <= totalstate; i++) {
                notBadState.add(i);
            }
            notBadState.removeAll(badAnddeadStates);

            criticalState = new LinkedList<Integer>();
            for (int n : badAnddeadStates) {
                for (int m : notBadState) {
                    if (StateShift[m][n] > 0 && badAnddeadStates.contains(n) && !badAnddeadStates.contains(m) && !criticalState.contains(m)) {
                        criticalState.add(m);
                    }
                }
            }

//	    criticalState.removeAll(badAnddeadStates);

            //临界状态下需要控制的变迁
            Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
            for (int c : criticalState) {
                map.put(c, new ArrayList<Integer>());
                for (int b : badAnddeadStates) {
                    if (StateShift[c][b] > 0) {
                        map.get(c).add(StateShift[c][b]);
//     			stateResult.append(c+"-->The transition that needs to be controlled in this state is : "+"t"+StateShift[c][b]+"\n");
                    }
                }
            }

            //所有状态数
            stateResult.append("\n\nTotal number of states：").append(totalstate);

            //输出最大许可行为
            stateResult.append("\n\nThe maximum permissive behaviors of the net are: (total ").append(notBadState.size()).append(" states)\n");
            for (Integer maxPermissive : notBadState) {
                stateResult.append(maxPermissive).append(" ");
            }

            //输出所有好状态
            stateResult.append("\n\nThe count of good states：").append(totalstate - badAnddeadStates.size() - criticalState.size()).append("\n");
            stateResult.append("The good states are：");
            for (int n = 1; n <= totalstate; n++) {
                if (!badAnddeadStates.contains(n) && !criticalState.contains(n)) {
                    stateResult.append(n).append(" ");
                }
            }

            //输出所有临界状态
            stateResult.append("\n\nThe count of critical states：").append(criticalState.size());
            stateResult.append("\nThe critical states are：\n");
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet) {
                stateResult.append(key).append("-->The transition that needs to be controlled in this state is : ");
                for (int value : map.get(key))
                    stateResult.append("t").append(value).append("  ");
                stateResult.append("\n");
            }

            //由不可靠资源导致的不稳健状态
            stateResult.append("\n\nThe count of bad robust states：").append(badRobustStates.size());
            stateResult.append("\nThe bad robust states are：");
            stateResult.append(PrintUtil.printSet(badRobustStates));
            printBadRobust(badRobustStates);

//            //输出稳健步长
//            if (step == -1)
//                stateResult.append("\n\nNo robust step!");
//            else
//                stateResult.append("\n\nThe robust step is : " + step);
        }

        return stateResult.toString();
    }

    private static void printBadRobust(Set<Integer> badRobustStates) {
        for (Integer br : badRobustStates) {
            for (Integer key : allStatesMap.keySet()) {
                if (key.equals(br)) {
                    stateResult.append("\nStateNo:").append(key).append("==>").append("Marking:").append(allStatesMap.get(key)).append("\n");
                }
            }
        }
    }

    /**
     * 稳健许可性计算
     */
    public static void robustPermission(List<Integer> badTrans) {

        badRobustStates = new HashSet<Integer>();
        List<Integer> goodStateList = new ArrayList<Integer>();
        int totalstate = ReachabilityGraphAlgorithm.statesAmout;
        for (int i = 1; i <= totalstate; i++) {
            goodStateList.add(i);
        }

        if (!originalBadState.isEmpty() && !deadStates.isEmpty()) {
            List<Integer> badAnddeadState = UnControlAndUnObserveAlgorithm.badAnddeadState;
            goodStateList.removeAll(badAnddeadState);
        }

        StateShift = new int[totalstate + 1][totalstate + 1];
        Map<Integer, StateNode> preStatesMap = ReachabilityGraphAlgorithm.preStatesMap;
        Map<Integer, List<Integer>> enableTran = new HashMap<Integer, List<Integer>>();//每个状态下使能的变迁集
        for (Map.Entry<Integer, StateNode> entry : preStatesMap.entrySet()) {
            StateNode curState = entry.getValue();
            boolean[] canFire = ReachabilityGraphAlgorithm.getEnabledTrans(curState);
            Set<Integer> nextTrans = new HashSet<Integer>(); //当前状态下，能够发射的变迁
            enableTran.put(curState.getStateNo(), new ArrayList<>());
            for (int i = 0; i < canFire.length; i++) {
                if (canFire[i]) {
                    nextTrans.add(i + 1);
                    enableTran.get(curState.getStateNo()).add(i + 1);
                }
            }
            if (badTrans.containsAll(nextTrans)) {
                badRobustStates.add(curState.getStateNo());
            }
        }

        List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;
        for (List<Integer> list : adj) {
            if (!list.isEmpty()) {
                for (int k = 0; k < list.size(); k = k + 3) {
                    addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                }
            }
        }
        extendBadRobust = new ArrayDeque<Integer>(badRobustStates);
        while (!extendBadRobust.isEmpty()) {
            Integer m = extendBadRobust.pollFirst();
            for (int s : goodStateList) {
                if (StateShift[s][m] > 0 && !badRobustStates.contains(s)) {
                    Set<Integer> allTran = new HashSet<Integer>();
                    for (int g : goodStateList) {
                        if (StateShift[s][g] > 0 && (m != g)) {
                            allTran.add(StateShift[s][g]);
                        }
                    }
                    if (badTrans.containsAll(allTran)) {
                        badRobustStates.add(s);
                        extendBadRobust.offerLast(s);
                    }
                }
            }
        }

        badAnddeadStates.addAll(badRobustStates);

    }

    /**
     * 稳健步长计算
     * 邻接矩阵:1.删除死锁状态和坏状态;2.matrix[i][j]为相应变迁
     * BFS方式搜索，发现故障变迁，则记录当前节点高度
     * 相邻高度差最大的记为稳健步长
     * @param matrix   邻接矩阵
     * @param badTrans 故障相关变迁
     */
    public static int robustPath(int[][] matrix, Set<Integer> badTrans) {
        int robustLength = 1;
        Set<Integer> lengthSet = new TreeSet<Integer>(); //有序Set，记录在第几层发现故障变迁
        Queue<RobustNode> queue = new LinkedList<RobustNode>();
        Map<Integer, Integer> next = null;
        boolean[] isVisit = new boolean[matrix.length];
        queue.add(new RobustNode(1, 0)); //从编号为1（matrix的第一行）的状态开始搜索
        RobustNode currentNode = null;
        while (!queue.isEmpty()) {
            currentNode = queue.poll();
            next = Matrix.getElementsIndexAndValueExceptZero(Matrix.getMatrixRow(currentNode.getValue() - 1, matrix));
            for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
                if (badTrans.contains(entry.getValue())) {
                    lengthSet.add(currentNode.getDepth() + 1);//在该层发现故障变迁
                }
                if (!isVisit[entry.getKey() - 1]) {
                    queue.add(new RobustNode(entry.getKey(), currentNode.getDepth() + 1));
                }
                isVisit[entry.getKey() - 1] = true;
            }
        }

//        System.out.println(lengthSet);
        //相邻元素间最大间距记为步长最大值
        Object[] array = lengthSet.toArray();
        int previous = (int) array[0];
        for (int i = 1; i < array.length; i++) {
            if ((int) array[i] - previous > robustLength) {
                robustLength = (int) array[i] - previous;
            }
            previous = (int) array[i];
        }
        return robustLength;
    }

    public static void addEdge(int start, int end, int weight) {

        StateShift[start][end] = weight;

    }

}
