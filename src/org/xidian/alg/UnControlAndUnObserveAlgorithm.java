package org.xidian.alg;

import java.util.*;

import org.xidian.model.StateNode;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;

/**
 * @author LP;
 * @Description 含有不可控变迁和不可观变迁的状态分析算法
 * @CreateDate [2018-7-6]
 */
public class UnControlAndUnObserveAlgorithm {

    static int[][] StateShift;
    static List<Integer> badAnddeadState = null;
    static List<Integer> criticalState = null;
    static Map<Integer, String> ifobservable = null;
    static List<Integer> unObservableTra = null;
    static Map<Integer, String> ifcontrollable = null;
    static List<Integer> unControllableTra = null;
    static ReachabilityGraphAlgorithm rg = null;
    static Queue<Integer> que = null;


    //存储临界状态,输出结果
    static StringBuffer sb = null;
    static StringBuffer stateResult = null;

    public static String check() {
        sb = new StringBuffer();
        stateResult = new StringBuffer();
        unObservableTra = new ArrayList<Integer>();
        unControllableTra = new ArrayList<Integer>();
        try {
            rg = new ReachabilityGraphAlgorithm();
            rg.createReachabilityGraph(null, 0);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        UnobservableReachability.check();
        UnControllableReachabilityGraphAlgorithm.test();
//        StepAlgorithm.analyse();

        //状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
        List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;


        //死锁状态
//        String deadState1 = StepAlgorithm.deadState;
        List<StateNode> deadStates = ReachabilityGraphAlgorithm.deadStates;
        String deadState1 = PrintUtil.printList(deadStates);
        String[] deadState = deadState1.trim().split(" ");

        //变迁是否可观
        ifobservable = LoadModelUtil.ifobservable;
        int size = ifobservable.size() + 1;
        for (int h = 1; h < size; h++) {
            if ("N".equals(ifobservable.get(h))) {
                unObservableTra.add(h);
            }
        }


        //仅含不可控变迁时的坏状态和死锁状态
        badAnddeadState = UnControllableReachabilityGraphAlgorithm.badAnddeadState;
//     	System.out.println(badAnddeadState);
        if (!badAnddeadState.isEmpty() && (badAnddeadState != null)) {

            int totalsize = adj.size() + deadState.length;
            StateShift = new int[totalsize + 1][totalsize + 1];

            for (int i = 0; i < adj.size(); i++) {
                List<Integer> list = adj.get(i);
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {
                        addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                    }
                }
            }

            //把仅含不可控变迁时的坏状态和死锁状态进行排队
            que = new LinkedList<Integer>();
            for (int s : badAnddeadState) {
                que.add(s);
            }
            while (!que.isEmpty()) {
                int head = que.poll();
                for (int i = 1; i < StateShift.length; i++) {
                    for (int j = 0; j < unObservableTra.size(); j++) {
                        if (StateShift[i][head] == unObservableTra.get(j) && !badAnddeadState.contains(i) && badAnddeadState.contains(head)) {
                            que.add(i);
                            badAnddeadState.add(i);
                        }
                    }
                }
            }

            //stateList = new LinkedList<Integer>();  //存放状态
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
                                ss.add(g);
                            }
                        }
                        if (badAnddeadState.containsAll(ss)) {
                            badAnddeadState.add(s);
                        }
                        //	}
                    }
                }
            }

            //仅含不可控变迁时的临界状态
            criticalState = UnControllableReachabilityGraphAlgorithm.criticalState;
            for (int n : badAnddeadState) {
                for (int m = 0; m < StateShift.length; m++) {
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

            stateResult.append("The analysis of containing uncontrollable and unobservable transitions is as follows:\n");
            //所有状态数
            int totalstate = ReachabilityGraphAlgorithm.statesAmout;
            stateResult.append("Total number of states：" + totalstate + "\n\n\n");
            //输出好状态
            stateResult.append("The count of good states：" + (totalstate - criticalState.size() - badAnddeadState.size()) + "\n");
            stateResult.append("The good states are：");
            for (int i = 1; i <= totalstate; i++) {
                if (!criticalState.contains(i) && !badAnddeadState.contains(i)) {
                    stateResult.append(i + " ");
                }
            }
            //输出临界状态
            stateResult.append("\n\nThe count of critical States：" + criticalState.size() + "\n");
            stateResult.append("The critical States are：\n");
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet) {
                stateResult.append(key + "-->The transition that needs to be controlled in this state is : ");
                for (int value : map.get(key))
                    stateResult.append("t" + value + "  ");
                stateResult.append("\n");
            }

            //输出坏状态
            String badstate = badAnddeadState.subList(deadState.length, badAnddeadState.size()).toString();
            badstate = badstate.substring(1, badstate.length() - 1);
            String[] split = badstate.split(",");
            for (String ss : split) {
                sb.append(ss + " ");
            }
            stateResult.append("\n\nThe count of bad States：" + (badAnddeadState.size() - deadState.length) + "\n");
            stateResult.append("The bad States are：" + sb);

            //输出死锁状态
            stateResult.append("\n\nThe count of deadlock States：" + deadState.length + "\n");
            stateResult.append("The deadlock States are：" + deadState1);
/*    	
     	if(badAnddeadState.contains(1)){
     		stateResult.delete(0,stateResult.length());
     		
     		stateResult.append("含不可控变迁和不可观变迁的各状态数情况如下：\n\n");
    		stateResult.append("所有状态数："+totalstate+"\n\n\n");
    		stateResult.append("好状态数有：0\n");
    		stateResult.append("好状态分别是：");
    		stateResult.append("\n\n临界状态数有：0\n");
    		stateResult.append("临界状态分别是：");
    		LinkedList<Integer> que1 = new  LinkedList<>();
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
}