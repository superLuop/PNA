package org.xidian.alg;

import java.util.*;

import org.xidian.model.Marking;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.ui.LocalGraphParametersListener;
import org.xidian.utils.LoadModelUtil;

/**
 * 含有不可控变迁的步长预测
 *
 * @author wss
 */
public class UnControlStepPredict {
    static Queue<String> q = null;
    static List<String> bs = null;
    static List<String> es = null;
    static List<Integer> trans = null;
    static List<String> temp = null;
    static List<String> TotalDeadState = null;
    static Set<String> Critical = null;
    static List<String> BadAndDeadState = null;
    static Map<Integer, String> ifcontrollable = null;
    static int stepsize = 1;//初始假设步长
    static int p = 0;
    static Set<String> UncontrolCritical = null;
    //可控的坏死和完全死锁状态
    static Set<String> DeadState = null;

    //定义到底循环计算多少次
    static int total = 1000;

    //存放结果
    static StringBuffer sb = new StringBuffer();

    public static String calculateOptimalStep() throws Exception {

        bs = new ArrayList<String>();
        es = new ArrayList<String>();
        trans = new ArrayList<Integer>();
        temp = new ArrayList<String>();
        TotalDeadState = new ArrayList<String>();
        Critical = new HashSet<String>();
        DeadState = new HashSet<String>();
        BadAndDeadState = new ArrayList<String>();
//		new ReachabilityGraphAlgorithm().createReachabilityGraph(null, 0);
	/*	StepAlgorithm.analyse();
		int pathsize = StepAlgorithm.maxPath;*/
//		System.out.println(pathsize);

        ReachabilityGraphAlgorithm rga = new ReachabilityGraphAlgorithm();


        //得到不可控变迁
        ifcontrollable = LoadModelUtil.ifcontrollable;

//        String initialMarking = BaseData.rootState.toString();
//        String begin = initialMarking.replace(" ",",");
//        begin = begin.substring(0, begin.length() - 1);
        String begin = PetriModel.ininmarking.toString();


        int optimalStep = CalculateOptimalStep(rga, begin, stepsize);


        int a = optimalStep / 2;//8
        int b = optimalStep;//16


        int middle = (a + b) / 2;//12

        while (true) {
//			System.out.println(middle);
            int BooleanResult = CalculateOptimalStep2(new ReachabilityGraphAlgorithm(), begin, middle);
            if (BooleanResult == -1) {
                a = middle;
                middle = (a + b) / 2;//14
            } else {
                b = middle;
                middle = (a + b) / 2;//10
            }

            if (middle % 2 != 0) {
                int BResult = CalculateOptimalStep2(new ReachabilityGraphAlgorithm(), begin, middle);

                if (BResult == -1) {
                    sb.append("最接近最优步长的步长但是不可行：" + middle + "\n");
//					System.out.println("最接近最优步长的步长但是不可行："+middle);
                    middle = middle + 1;
                }
//				middle = middle + 1;
                break;
            }
        }

        sb.append("可行步长: " + optimalStep + "\n");
        sb.append("最优步长: " + middle + "\n");
        sb.append("不可控最终的临界状态有：" + UncontrolCritical + "\n");
        sb.append("不可控最终的临界状态的大小：" + UncontrolCritical.size());
//		System.out.println("可行步长: "+optimalStep);
//		System.out.println("最优步长: "+middle);
        return sb.toString();
    }


    private static int CalculateOptimalStep(ReachabilityGraphAlgorithm rga, String begin, int stepsize)
            throws CloneNotSupportedException {
        //存放不可控的临界状态
        UncontrolCritical = new HashSet<String>();
        int p = 0;
        while (p < total) {
            DeadState.clear();
            BadAndDeadState.clear();
            Critical.clear();
            UncontrolCritical.clear();

            rga.createReachabilityGraph(new StateNode(new LocalGraphParametersListener().parseToArray(begin), 1, 1), stepsize);

            List<String> deadState1 = rga.deadstate;


            if (deadState1 != null) {
                for (String ds : deadState1) {
                    if (!TotalDeadState.contains(ds))
                        TotalDeadState.add(ds);
                }
            }


//		System.out.println("死锁："+DeadState);

            BadAndDeadState.addAll(TotalDeadState);
            if (TotalDeadState != null) {
                for (int u = 0; u < BadAndDeadState.size(); u++) {
                    String DS = BadAndDeadState.get(u);
                    //找坏死状态和临界状态
                    for (int g = 0; g < es.size(); g++) {
                        if (DS.equals(es.get(g).trim())) {//76

                            List<String> ls = new ArrayList<String>();
                            int c;
                            for (c = 0; c < bs.size(); c++) {//bs.get(g) //63 64
                                if (bs.get(g).trim().equals(bs.get(c).trim())) {
                                    ls.add(es.get(c).trim());
                                }
                            }

                            if (BadAndDeadState.containsAll(ls)) {
                                BadAndDeadState.add(bs.get(g).trim());
                            } else {

                                Critical.add(bs.get(g).trim());
                            }
                        }
                    }
                }
            }
//		System.out.println("临界状态："+Critical);

            DeadState.addAll(BadAndDeadState);

            q = new LinkedList<String>();
            if (Critical.size() != 0) {
                for (String ct : Critical) {
                    q.add(ct);
                }
            }

            //得出含有不可控变迁的临界状态
            while (!q.isEmpty()) {
                String remove = q.remove();
                for (int y = 0; y < bs.size(); y++) {
                    if (remove.trim().equals(bs.get(y).trim())) {//18
                        String YN = ifcontrollable.get(trans.get(y));//7
                        if ("N".equals(YN) && BadAndDeadState.contains(es.get(y).trim())) {
                            BadAndDeadState.add(remove.trim());
//						q.remove(remove.trim());
                            for (int t = 0; t < es.size(); t++) {
                                if (remove.trim().equals(es.get(t).trim())) {
                                    q.add(bs.get(t).trim());
                                }
                            }
                        }

                        if (BadAndDeadState.contains(remove.trim())) {
                            UncontrolCritical.remove(remove.trim());
                        } else {
                            UncontrolCritical.add(remove.trim());
                        }

                    }
                }

            }


//		System.out.println("临界状态："+UncontrolCritical);
//		System.out.println("临界状态的大小："+UncontrolCritical.toString().split(",").length);


            Map<Integer, StateNode> resu = rga.resu;

            List<List<Integer>> adjlist = ReachabilityGraphAlgorithm.adjlist;


            for (int i = 0; i < adjlist.size(); i++) {
                List<Integer> list = adjlist.get(i);
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {

                        if (!(temp.contains(resu.get(list.get(k)).toString()))) {
                            bs.add(resu.get(list.get(k)).toString());
                            es.add(resu.get(list.get(k + 2)).toString());
                            trans.add(list.get(k + 1));
                        }
                    }
                }
            }

            Set<String> ss = new HashSet<String>();
            ss.addAll(BadAndDeadState);
		
	/*	System.out.println("坏死"+ss);
		System.out.println("坏死"+ss.size());*/
		
		/*System.out.println("BadAndDeadState:"+ss);
		System.out.println("BadAndDeadState的大小:"+ss.size());*/
            temp.clear();

            temp.addAll(bs);


            List<String> nextbegin = new ArrayList<String>();

            for (int i = 0; i < bs.size(); i++) {

                String temp = bs.get(i).trim();

                if (temp.equals(begin.replace(",", " ")) && !ss.contains(es.get(i).trim())) {
				/*if("N".equals(ifcontrollable.get(trans.get(i)))){
					nextbegin.clear();
					nextbegin.add(es.get(i)); 
					break;
				}*/
                    nextbegin.add(es.get(i));


                }

            }

            int x = nextbegin.size();
            if (x == 0 && DeadState.contains(begin.replace(",", " ").trim())) {
                stepsize = stepsize * 2;
                begin = PetriModel.ininmarking.toString();
                continue;
            } else if (x == 0) {
                begin = PetriModel.ininmarking.toString();
                continue;
            }
            //在这里判断是否会走到死锁状态，如果走到死锁状态会报异常
            String s = nextbegin.get((int) Math.floor(Math.random() * x));
            String str = s.replace(" ", ",");
            begin = str.substring(0, str.length() - 1);

            p++;
        }


        return stepsize;
    }


    private static int CalculateOptimalStep2(ReachabilityGraphAlgorithm rga, String begin, int stepsize)
            throws CloneNotSupportedException {
        Queue<String> q = new LinkedList<String>();
        Set<String> set = new HashSet<String>();
        List<String> bs = new ArrayList<String>();
        List<String> es = new ArrayList<String>();
        List<Integer> trans = new ArrayList<Integer>();
        List<String> temp = new ArrayList<String>();
        List<String> DeadState = new ArrayList<String>();
        Set<String> Critical = new HashSet<String>();
        List<String> BadAndDeadState = new ArrayList<String>();
        Map<Integer, String> ifcontrollable = LoadModelUtil.ifcontrollable;
        int p = 0;
        int total = 10000;
        while (p < total) {

            BadAndDeadState.clear();
            Critical.clear();


            rga.createReachabilityGraph(new StateNode(new LocalGraphParametersListener().parseToArray(begin), 1, 1), stepsize);

            List<String> deadState1 = rga.deadstate;

            if (deadState1 != null) {
                for (String ds : deadState1) {
                    if (!DeadState.contains(ds))
                        DeadState.add(ds);
                }
            }
//		System.out.println("死锁状态是："+DeadState);


            BadAndDeadState.addAll(DeadState);
            if (DeadState != null) {
                for (int u = 0; u < BadAndDeadState.size(); u++) {
                    String DS = BadAndDeadState.get(u);
                    //找坏死状态和临界状态
                    for (int g = 0; g < es.size(); g++) {
                        if (DS.equals(es.get(g).trim())) {//76

                            List<String> ls = new ArrayList<String>();
                            int c;
                            for (c = 0; c < bs.size(); c++) {//bs.get(g) //63 64
                                if (bs.get(g).trim().equals(bs.get(c).trim())) {
                                    ls.add(es.get(c).trim());
                                }
                            }

                            if (BadAndDeadState.containsAll(ls)) {
                                BadAndDeadState.add(bs.get(g).trim());
                            } else {
                                Critical.add(bs.get(g).trim());
                            }


                        }


                    }
                }
            }

            if (Critical.size() != 0) {
                for (String ct : Critical) {
                    q.add(ct);
                }
            }
            while (!q.isEmpty()) {
                String remove = q.remove();
                for (int y = 0; y < bs.size(); y++) {
                    if (remove.trim().equals(bs.get(y).trim())) {//18
                        String YN = ifcontrollable.get(trans.get(y));//7
                        if ("N".equals(YN) && BadAndDeadState.contains(es.get(y).trim())) {
                            BadAndDeadState.add(remove.trim());
//						q.remove(remove.trim());
                            for (int t = 0; t < es.size(); t++) {
                                if (remove.trim().equals(es.get(t).trim())) {
                                    q.add(bs.get(t).trim());
                                }
                            }
                        }

                        if (BadAndDeadState.contains(remove.trim())) {
                            set.remove(remove.trim());
                        } else {
                            set.add(remove.trim());
                        }

                    }
                }

            }
            Set<String> ss = new HashSet<String>();
            ss.addAll(BadAndDeadState);

            Map<Integer, StateNode> resu = rga.resu;

            List<List<Integer>> adjlist = ReachabilityGraphAlgorithm.adjlist;


            for (int i = 0; i < adjlist.size(); i++) {
                List<Integer> list = adjlist.get(i);
                if (!list.isEmpty()) {
                    for (int k = 0; k < list.size(); k = k + 3) {

                        if (!(temp.contains(resu.get(list.get(k)).toString()))) {
                            bs.add(resu.get(list.get(k)).toString());
                            es.add(resu.get(list.get(k + 2)).toString());
                            trans.add(list.get(k + 1));
                        }
                    }
                }
            }


            temp.clear();

            temp.addAll(bs);


            List<String> nextbegin = new ArrayList<String>();

            for (int i = 0; i < bs.size(); i++) {

                bs.get(i).trim();


                if (temp.equals(begin.replace(",", " ")) && !ss.contains(es.get(i).trim())) {
				/*if("N".equals(ifcontrollable.get(trans.get(i)))){
					nextbegin.clear();
					nextbegin.add(es.get(i)); 
					break;
				}*/
                    nextbegin.add(es.get(i));
                }
            }

            int x = nextbegin.size();
            if (x == 0) {
                return -1;
            }


            //在这里判断是否会走到死锁状态，如果走到死锁状态会报异常
            String s = nextbegin.get((int) Math.floor(Math.random() * x));


            String str = s.replace(" ", ",");

            begin = str.substring(0, str.length() - 1);

            p++;
        }

        return stepsize;
    }
}
