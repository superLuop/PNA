package org.xidian.alg;

import java.util.*;
//import java.lang.Iterable;

//import java.util.Set;
import org.xidian.model.StateNode;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;
//import java.util.HashSet;

/**
 * @author LP;
 * @Description 含有不可观变迁的状态分析算法
 * @CreateDate [2018-6-28]
 */
public class UnobservableReachability{
    static int[][] StateShift;
    static List<Integer> badAnddeadState = null;
    static List<Integer> badState = null;
    static List<Integer> criticalState = null;
    static Map<Integer, String> ifobservable = null;
    static List<Integer> unObservableTra = null;
    static ReachabilityGraphAlgorithm rg = null;
    static Queue<Integer> que = null;
    static List<Integer> que1 = null;

    //存储含有不可观变迁时临界状态,输出结果
    static StringBuffer sb = null;

    static StringBuffer stateResult = null;
    static LinkedList<Integer> stateList;

    public static String check() {
        sb = new StringBuffer();
        stateResult = new StringBuffer();
        unObservableTra = new ArrayList<Integer>();
        StepAlgorithm.analyse();

        //死锁状态
//        String deadState1 = StepAlgorithm.deadState;
        List<StateNode> deadStates = ReachabilityGraphAlgorithm.deadStates;
        String deadState1 = PrintUtil.printList(deadStates);
        String[] deadState = deadState1.trim().split(" ");
        //	 deadstate = new LinkedList<Integer>();
// 	 for(int c = 0;c<deadState.length;c++){
//		deadstate.add(Integer.parseInt(deadState[c].trim()));
// 	 }
        //状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
        List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;

        //变迁是否可观
        ifobservable = LoadModelUtil.ifobservable;
        int size = ifobservable.size() + 1;
        for (int h = 1; h < size; h++) {
            if ("N".equals(ifobservable.get(h))) {
                unObservableTra.add(h);
            }
        }
        //坏状态和死锁状态
        StringBuffer badAnddeadState1 = StepAlgorithm.badAnddeadState2;
        String[] strs = badAnddeadState1.toString().split(" ");
        badAnddeadState = new LinkedList<Integer>();
        if (!"".equals(badAnddeadState1.toString()) && (badAnddeadState1.toString() != null)) {
            for (String str : strs) {
                badAnddeadState.add(Integer.parseInt(str.trim()));
            }

            int totalsize = adj.size() + deadState.length;

            StateShift = new int[totalsize + 1][totalsize + 1];

            for (List<Integer> list : adj) {
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {
                        addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                    }
                }
            }

            //把原来的坏状态和死锁状态进行排队
            que = new LinkedList<Integer>();
            que.addAll(badAnddeadState);

            //原来的临界状态
            String criticalState1 = StepAlgorithm.Criticals;
            String[] criticalState2 = criticalState1.trim().split(" ");
            criticalState = new LinkedList<Integer>();
            if (criticalState2 != null && !"".equals(criticalState2)){
                for (String item : criticalState2) {
                    criticalState.add(Integer.parseInt(item.trim()));
                }
            }

            while (!que.isEmpty()) {
                int head = que.poll();
                for (int c : criticalState) {
                    for (int n = 0; n < unObservableTra.size(); n++) {
                        if (StateShift[c][head] == unObservableTra.get(n) && !badAnddeadState.contains(c) && badAnddeadState.contains(head)) {
                            que.add(c);
                            badAnddeadState.add(c);
                        }
                    }
                }
            }


            int totalstate = ReachabilityGraphAlgorithm.statesAmout;
            List<Integer> notBadState = new LinkedList<Integer>();
            for (int i = 1; i <= totalstate; i++) {
                notBadState.add(i);
            }
            notBadState.removeAll(badAnddeadState);

            for (int b = 0; b < badAnddeadState.size(); b++) {
                for (int s : notBadState) {
                    if (StateShift[s][badAnddeadState.get(b)] > 0 && !badAnddeadState.contains(s)) {
                        Set<Integer> ss = new HashSet<>();
                        for (int g : notBadState) {
                            if (StateShift[s][g] > 0) {
                                ss.add(g);
                            }
                        }
                        if (badAnddeadState.containsAll(ss)) {
                            badAnddeadState.add(s);
                        }
                    }
                }
            }


            //临界状态
            /*String criticalState1 = StepAlgorithm.Criticals;
            String[] criticalState2 = criticalState1.trim().split(" ");
            criticalState = new LinkedList<Integer>();
            for (int j = 0; j < criticalState2.length; j++) {
                criticalState.add(Integer.parseInt(criticalState2[j].trim()));
            }*/

            //Iterator<Integer> iter = stateList.iterator();
            notBadState.removeAll(badAnddeadState);

            for (int n : badAnddeadState) {
                for (int m : notBadState) {
                    if (StateShift[m][n] > 0 && badAnddeadState.contains(n) && !badAnddeadState.contains(m) && !criticalState.contains(m)) {
                        criticalState.add(m);
                    }
                }
            }

            criticalState.removeAll(badAnddeadState);

            //临界状态下需要控制的变迁
            Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
            for (int c : criticalState) {
                map.put(c, new ArrayList<Integer>());
                for (int m : badAnddeadState) {
                    if (StateShift[c][m] > 0) {
                        map.get(c).add(StateShift[c][m]);
//     			stateResult.append(c+"-->The transition that needs to be controlled in this state is : "+"t"+StateShift[c][m]+"\n");
                    }
                }
            }


            stateResult.append("The analysis of containing unobservable transitions is as follows:\n");
            //所有状态数
//            int totalstate = ReachabilityGraphAlgorithm.statesAmout;
            stateResult.append("Total number of states：" + totalstate + "\n\n\n");

            //输出最大许可行为
//            stateResult.append("The maximum permissive behaviors of the net are: (total "+notBadState.size()+" states)\n");
//            for (Integer maxPermissive : notBadState) {
//                stateResult.append(maxPermissive + " ");
//            }

            //输出好状态
            stateResult.append("Good states：" + (totalstate - criticalState.size() - badAnddeadState.size()) + "\n");
            stateResult.append("The good states are：");
            for (int i = 1; i <= totalstate; i++) {
                if (!criticalState.contains(i) && !badAnddeadState.contains(i)) {
                    stateResult.append(i + " ");
                }
            }
            //输出临界状态
            stateResult.append("\n\nCritical States：" + criticalState.size() + "\n");
            stateResult.append("The critical States are：\n");
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet) {
                stateResult.append(key + "-->The transition that needs to be controlled in this state is : ");
                for (int value : map.get(key))
                    stateResult.append("t" + value + "  ");
                stateResult.append("\n");
            }

            //输出坏状态
            StringBuffer sb2 = new StringBuffer();
            String badstate = badAnddeadState.subList(deadState.length, badAnddeadState.size()).toString();
            badstate = badstate.substring(1, badstate.length() - 1);
            String[] split2 = badstate.split(",");
            for (String ss : split2) {
                sb2.append(ss + " ");
            }
            stateResult.append("\n\nBad States：" + (badAnddeadState.size() - deadState.length) + "\n");
            stateResult.append("The bad States are：" + sb2);

            //输出死锁状态
            stateResult.append("\n\nDeadlock States：" + deadState.length + "\n");
            stateResult.append("The deadlock States are：" + deadState1);
 /*	
 	   //int[] initial = PetriModel.ininmarking.getMarking();
 	 if(badAnddeadState.contains(1)){
 		stateResult.delete(0,stateResult.length());
 		
 		stateResult.append("含不可观变迁的各状态数情况如下：\n\n");
		stateResult.append("所有状态数："+totalstate+"个\n\n\n");
		stateResult.append("好状态数有：0\n");
		stateResult.append("好状态分别是：");
		stateResult.append("\n\n临界状态数有：0\n");
		stateResult.append("临界状态分别是：");
		que1 = new  LinkedList<Integer>();
		for(int c = 1;c<StateShift.length;c++){
			que1.add(c);
		}
		LinkedList<Integer> list = new LinkedList<>();
		for(int d = 0;d<deadState.length;d++){
			list.add(Integer.parseInt(deadState[d].trim()));
		}
		que1.removeAll(list);
		stateResult.append("\n\n坏状态数有："+que1.size()+"\n");
		stateResult.append("坏状态分别是：");
		for(int m = 0;m<que1.size();m++){
			stateResult.append(que1.get(m)+" ");
		}
	 	//输出死锁状态
	 	stateResult.append("\n\n死锁状态数有："+deadState.length+"\n");
	 	stateResult.append("死锁状态分别是："+deadState1);
			    
		}
 	 */
            return stateResult.toString();
        } else {
            return "The petri network is not deadlocked.";
        }
    }

    public static void addEdge(int start, int end, int weight) {

        StateShift[start][end] = weight;

    }

    public static void BFS() {

        Set<Integer> set = new HashSet<Integer>();
        for (int s = 0; s < StateShift.length; s++) {
            set.add(s);
        }
        for (int t = 0; t < badAnddeadState.size(); t++) {
            for (int s = 0; s < StateShift.length; s++) {
                if (StateShift[s][badAnddeadState.get(t)] > 0 && !badAnddeadState.contains(s)) {
                    //stateList.addFirst(s);
                    //while(!stateList.isEmpty()){
                    //	int index = stateList.getFirst();
                    Set<Integer> ss = new HashSet<>();
                    for (int g : set) {
                        if (StateShift[s][g] > 0) {
                            ss.add(s);
                        }
                    }
                    if (badAnddeadState.containsAll(ss)) {
                        badAnddeadState.add(s);
                    }
                    //	}

                }
            }
        }

    }

}
