package org.xidian.alg;


import java.util.*;

//import org.xidian.model.PetriModel;
//import org.xidian.model.StateNode;
import org.xidian.model.StateNode;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;

/**
 * @Description 含有不可控变迁的状态分析算法
 * @author luopeng
 */
public class UnControllableReachabilityGraphAlgorithm {

    static int[][] adjedge;

    static List<Integer> criticalState = null;
    static List<Integer> badAnddeadState = null;
    static Map<Integer, String> ifcontrollable = null;
    static List<Integer> unControllableTra = null;
    static ReachabilityGraphAlgorithm rg = null;
    static Queue<Integer> q = null;
    static List<String> removelist = null;


    //装不可控的临界状态
    static StringBuffer sb = null;//这里面有重复的状态
    static StringBuffer sb2 = null;//这个是除去上面的重复的状态
    static StringBuffer sbResult = null;//结果

    public static String test() {
        sb2 = new StringBuffer();
        sb = new StringBuffer();
        sbResult = new StringBuffer();
        unControllableTra = new ArrayList<Integer>();
        try {
            rg = new ReachabilityGraphAlgorithm();
            rg.createReachabilityGraph(null, 0);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        StepAlgorithm.analyse();

        //死锁状态
//        String deadState1 = StepAlgorithm.deadState;
        List<StateNode> deadStates = ReachabilityGraphAlgorithm.deadStates;
        String deadState1 = PrintUtil.printList(deadStates);
        String[] deadState = deadState1.trim().split(" ");


        //可达图的边
        List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;


        //变迁是否可控
        ifcontrollable = LoadModelUtil.ifcontrollable;
        int size = ifcontrollable.size() + 1;
        for (int h = 1; h < size; h++) {
            if ("N".equals(ifcontrollable.get(h))) {
                unControllableTra.add(h);
            }
        }

//		System.out.println("unControllableTra "+unControllableTra);

        //坏状态和死锁状态
        StringBuffer badAnddeadState1 = StepAlgorithm.badAnddeadState2;
        String[] strs = badAnddeadState1.toString().split(" ");
        badAnddeadState = new LinkedList<Integer>();

        if (!"".equals(badAnddeadState1.toString()) && (badAnddeadState1.toString() != null)) {
            for (int g = 0; g < strs.length; g++) {

                badAnddeadState.add(Integer.parseInt(strs[g].trim()));
            }


            //临界状态
            String criticalState1 = StepAlgorithm.Criticals;
            String[] criticalState2 = criticalState1.trim().split(" ");
            criticalState = new ArrayList<Integer>();
            if (criticalState2 != null && criticalState2.length > 0){
                for (int j = 0; j < criticalState2.length; j++) {
                    criticalState.add(Integer.parseInt(criticalState2[j].trim()));
                }
            }

            int totalsize = adj.size() + deadState.length;
            adjedge = new int[totalsize + 1][totalsize + 1];


            for (int i = 0; i < adj.size(); i++) {
                List<Integer> list = adj.get(i);
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {
                        addEdge(list.get(k), list.get(k + 2), list.get(k + 1));
                    }
                }
            }


            for (int i = 0; i < criticalState.size(); i++) {

                for (int j = 0; j < adjedge.length; j++) {//比如22
                    if (adjedge[criticalState.get(i)][j] > 0 && badAnddeadState.contains(j)) {
                        String YN = ifcontrollable.get(adjedge[criticalState.get(i)][j]);
                        if ("Y".equals(YN)) {
                            sb.append(criticalState.get(i) + " ");
                        } else if ("N".equals(YN)) {

                            core(criticalState.get(i));
                        }
                    }
                }
            }

            String str = sb.toString();

            String ff = str.replace(" ", ",");

            String[] split = ff.split(",");
            Set<String> set = new HashSet<String>();
            for (String st : split) {
                set.add(st.trim());
            }


            sbResult.append("The number of states containing uncontrollable transitions is as follows:\n");


            for (String st : set) {
                for (int u = 0; u < adjedge.length; u++) {
                    for (int g = 0; g < unControllableTra.size(); g++) {
                        if ((adjedge[Integer.parseInt(st)][u] == unControllableTra.get(g) && badAnddeadState.contains(u))) {
                            sb2.append(st + " ");
                        }
                    }
                }
            }

            //将要移除的元素
            String[] split2 = sb2.toString().split(" ");


            //没有不可控的时候的坏死状态加到队列
            q = new LinkedList<Integer>();
            for (int s : badAnddeadState) {
                q.add(s);
            }

            removelist = new ArrayList<String>();
            //移除后的状态放到set集合中
            for (String string : split2) {
                if (set.contains(string)) {
                    set.remove(string);
                    removelist.add(string);
                }

            }


            //	int sz = badAnddeadState.size();
            while (!q.isEmpty()) {
                int remove = q.remove();
                for (int u = 1; u < adjedge.length; u++) {
                    for (int g = 0; g < unControllableTra.size(); g++) {
                        if ((adjedge[u][remove] > 0 && adjedge[u][remove] == unControllableTra.get(g) && !badAnddeadState.contains(u))) {
                            q.add(u);
                            badAnddeadState.add(u);
                        }
                    }
                }

            }

            //逐层向上搜索坏状态
            int totalstatecount = ReachabilityGraphAlgorithm.statesAmout;
            List<Integer> notBadState = new LinkedList<Integer>();
            for (int i = 1; i <= totalstatecount; i++) {
                notBadState.add(i);
            }
            notBadState.removeAll(badAnddeadState);
            for (int b = 0; b < badAnddeadState.size(); b++) {
                for (int s : notBadState) {
                    if (adjedge[s][badAnddeadState.get(b)] > 0 && !badAnddeadState.contains(s)) {
                        Set<Integer> ss = new HashSet<>();
                        for (int g : notBadState) {
                            if (adjedge[s][g] > 0) {
                                ss.add(g);
                            }
                        }
                        if (badAnddeadState.containsAll(ss)) {
                            badAnddeadState.add(s);
                        }
                    }
                }
            }

//            System.out.println("+++++++++"+badAnddeadState);
//            System.out.println("+++++++++"+badAnddeadState.size());

            Set<String> set2 = new HashSet<String>();
            for (int g = 0; g < badAnddeadState.size(); g++) {
                for (int u = 1; u < adjedge.length; u++) {
                    if ((adjedge[u][badAnddeadState.get(g)] > 0 && !badAnddeadState.contains(u))) {
                        set2.add(u + "=t" + adjedge[u][badAnddeadState.get(g)]);
                    }
                }
            }

//		System.out.println(set2);

            StringBuffer sb3 = new StringBuffer();
            Object[] array = set2.toArray();
            for (int p = 0; p < array.length; p++) {
                if (array[p] != null) {
                    String ss = String.valueOf(array[p]);
//			System.out.println(ss);
                    String[] sp = ss.split("=");
//			System.out.println(sp[0]+"\n该状态下需要控制的变迁为：  "+sp[1]);
                    sb3.append(sp[0] + "\n该状态下需要控制的变迁为：  " + sp[1]);
                    for (int y = p + 1; y < array.length; y++) {
                        String ss2 = String.valueOf(array[y]);
                        String[] sp2 = ss2.split("=");
                        if (sp[0].equals(sp2[0])) {
                            array[y] = null;
                            sb3.append("   " + sp2[1]);
                        }
                    }
                }
                sb3.append("-");
            }
            String[] results = sb3.toString().split("-");
//			System.out.println(sb3);
            //总的状态数
//            int totalstatecount = ReachabilityGraphAlgorithm.statesAmout;
            sbResult.append("Total number of states：" + totalstatecount + "\n\n\n");


            criticalState.clear();
            if (!badAnddeadState.contains(1)) {
                for (String s : results) {
                    if (s != null && !"".equals(s)) {
                        String[] split3 = s.split("\n");
                        Integer.parseInt(split3[0]);
                        //sbResult.append("Critical States:"+s.split("\n")[0]+"\n");
                        criticalState.add(Integer.parseInt(s.split("\n")[0]));
                        //sbResult.append(s.split("\n")[0]+" ");
                        //sbResult.append("toks:"+result.get(k)+"\n\n\n");
                        //System.out.println("toks:"+result.get(k)+"\n\n\n");
                    }
                }
            } else {
                sbResult.append("Sorry, there's no critical states");
            }

            //临界状态下需要控制的变迁
            Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
            for (int c : criticalState) {
                map.put(c, new ArrayList<Integer>());
                for (int m : badAnddeadState) {
                    if (adjedge[c][m] > 0) {
                        map.get(c).add(adjedge[c][m]);
//     			stateResult.append(c+"-->The transition that needs to be controlled in this state is : "+"t"+StateShift[c][m]+"\n");
                    }
                }
            }

            //输出最大许可行为
//            sbResult.append("The maximum permissive behaviors of the net are: (total ").append(notBadState.size()).append(" states)\n");
//            for (Integer maxPermissive : notBadState) {
//                sbResult.append(maxPermissive).append(" ");
//            }

            sbResult.append("Good states：").append(totalstatecount - criticalState.size() - badAnddeadState.size()).append("\n");
            sbResult.append("The good states are：");

            //得到好的状态
            for (int i = 1; i <= totalstatecount; i++) {
                if (!criticalState.contains(i) && !badAnddeadState.contains(i)) {
                    sbResult.append(i + " ");
                }
            }

            sbResult.append("\n\nCritical States：" + criticalState.size() + "\n");
            sbResult.append("The critical States are：\n");
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet) {
                sbResult.append(key + "-->The transition that needs to be controlled in this state is : ");
                for (int value : map.get(key))
                    sbResult.append("t" + value + "  ");
                sbResult.append("\n");
            }


            //badAnddeadState集合使用完成后，最后输出坏死状态
            StringBuffer sb8 = new StringBuffer();
            String badstate = badAnddeadState.subList(deadState.length, badAnddeadState.size()).toString();
            badstate = badstate.substring(1, badstate.length() - 1);
            String[] split4 = badstate.split(",");
            for (String ss : split4) {
                sb8.append(ss + " ");
            }
            //System.out.println(sb8);
            sbResult.append("\n\nBad States：" + (badAnddeadState.size() - deadState.length));
            sbResult.append("\nThe bad States are：" + sb8);

            //输出死锁状态
            sbResult.append("\n\nDeadlock States：" + deadState.length);
            sbResult.append("\nThe deadlock States are：" + deadState1);


            return sbResult.toString();

        } else {
            return "The petri network is not deadlocked！！！";
        }

    }

    public static void addEdge(int start, int end, int weight) {
        adjedge[start][end] = weight;
    }


    public static void core(int j) {
        for (int l = 0; l < adjedge.length; l++) {
            if (adjedge[l][j] > 0) {
                int Criticalprestate = adjedge[l][j];
                //判断22上面的11,14对应的变迁是否能控
                String YN1 = ifcontrollable.get(Criticalprestate);
                if ("Y".equals(YN1)) {
                    sb.append(l + " ");
                } else if ("N".equals(YN1)) {
                    core(l);
                }
            }
        }
    }


}
