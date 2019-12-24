package org.xidian.alg;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.hamcrest.Condition.Step;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.ui.LocalGraphParametersListener;

/**
 * 含有可控变迁的预测
 * @author wss
 *
 */
public class ControlStepPredict {
	static List<String> bs = null;
	static List<String> es = null;
	static List<Integer> trans = null;
	static List<String> temp = null;
	static List<String> DeadState = null;
	static Set<String> Critical = null;
	static List<String> BadAndDeadState = null;
	static int stepsize = 1;//初始假设步长
	static int p = 0;
	//定义到底循环计算多少次
	static int total = 5000;

	//存放结果
	static StringBuffer sb = new StringBuffer();
	public static String calculateCritical() throws Exception{
		bs = new ArrayList<String>();
		es = new ArrayList<String>();
		trans = new ArrayList<Integer>();
		temp =new ArrayList<String>();
		DeadState = new ArrayList<String>();
		Critical = new HashSet<String>();
		BadAndDeadState = new ArrayList<String>();
			
//		new ReachabilityGraphAlgorithm().createReachabilityGraph(null, 0);
	/*	StepAlgorithm.analyse();
		int pathsize = StepAlgorithm.maxPath+1;*/
//		System.out.println(pathsize);
		
		ReachabilityGraphAlgorithm rga = new ReachabilityGraphAlgorithm();

		String initialMarking = BaseData.rootState.toString();
		String begin = initialMarking.replace(" ",",");
		begin = begin.substring(0, begin.length() - 1);

		
		int optimalStep = CalculateOptimalStep(rga, begin,stepsize);
		
		
		
		int a = optimalStep/2;//8
		int b = optimalStep;//16
		
		
		int middle = (a+b)/2;//12
		
		while(true){
//			System.out.println(middle);
			int BooleanResult = CalculateOptimalStep2(new ReachabilityGraphAlgorithm(),begin,middle);
			if(BooleanResult==-1){
				a = middle;
				middle = (a+b)/2;//14
			}else{
				b = middle;
				middle = (a+b)/2;//10
			}
		    
			if(middle%2!=0){
				CalculateOptimalStep2(new ReachabilityGraphAlgorithm(),begin,middle);
				
			/*	if(BResult==-1){
//					System.out.println("哈哈："+middle);
					sb.append("最接近最优步长的步长但是不可行："+middle+"\n");
					middle = middle + 1;
				}*/
				  middle = middle + 1;
				break;
			}
		}
		
		
		sb.append("可行步长: "+optimalStep+"\n");
		sb.append("最优步长: "+middle+"\n");
	/*	sb.append("可控最终的临界状态有："+Critical+"\n");
		sb.append("可控最终的临界状态的大小："+Critical.size());*/
	/*	System.out.println("@@@@@@@@@@@@@@@@@@@"+middle);
		System.out.println("最优步长: "+optimalStep);*/
//		return sb.toString();
		
		
		return sb.toString();
	}


	private static int CalculateOptimalStep(ReachabilityGraphAlgorithm rga, String begin,int stepsize)
			throws CloneNotSupportedException {
		while(p<total){
			
		BadAndDeadState.clear();
		Critical.clear();
		
		
		rga.createReachabilityGraph(new StateNode(new LocalGraphParametersListener().parseToArray(begin),1,1),stepsize);

		List<String> deadState1 = rga.deadstate;
		
		
		
		if(deadState1!=null){
			for(String ds : deadState1){
				if(!DeadState.contains(ds))
				DeadState.add(ds);
			}
		}
		
		
		BadAndDeadState.addAll(DeadState);
		if(DeadState!=null){
			for(int u=0;u<BadAndDeadState.size();u++){
				String DS = BadAndDeadState.get(u);
				//找坏死状态和临界状态
				for(int g =0;g<es.size();g++){
					if(DS.equals(es.get(g).trim())){//76
						
						List<String> ls = new ArrayList<String>();
						int c ;
						for(c = 0;c<bs.size();c++){//bs.get(g) //63 64
							if(bs.get(g).trim().equals(bs.get(c).trim())){
								ls.add(es.get(c).trim());
							}
						}
					
						if(BadAndDeadState.containsAll(ls)){
							BadAndDeadState.add(bs.get(g).trim());
						}else{
							Critical.add(bs.get(g).trim());
						}
						
						
					}
					
					
				}
			}
		}
		
		
		Map<Integer, StateNode> resu = rga.resu;
		
		List<List<Integer>> adjlist = ReachabilityGraphAlgorithm.adjlist;
		
		
		
		for(int i = 0;i<adjlist.size();i++){
			List<Integer> list = adjlist.get(i);
			if(!list.isEmpty()){
				for(int k=0;k<list.size();k=k+3){
			
					if(!(temp.contains(resu.get(list.get(k)).toString()))){
						bs.add(resu.get(list.get(k)).toString());
						es.add(resu.get(list.get(k+2)).toString());
						trans.add(list.get(k+1));
					}
				}
			}
		}
		
		
		temp.clear();
		
		temp.addAll(bs);
		
		
		List<String> nextbegin = new ArrayList<String>();
		
		for(int i =0;i<bs.size();i++){
			
			String temp = bs.get(i).trim();
			
			if(temp.equals(begin.replace(","," "))&&!BadAndDeadState.contains(es.get(i).trim())){
				nextbegin.add(es.get(i)); 
			}
		}
		
		int x  = nextbegin.size();
		if(x==0){
//			System.out.println(begin.replace(",", " ").trim());
			stepsize = stepsize*2;
			begin = PetriModel.ininmarking.toString();
			continue;
		}
		
		/*if(stepsize == 4){
			System.out.println(begin.replace(",", " ").trim());
		}*/
		
		
		//在这里判断是否会走到死锁状态，如果走到死锁状态会报异常
		String s =nextbegin.get((int) Math.floor(Math.random()*x));
		   
		
		String str = s.replace(" ", ",");
		
		begin = str.substring(0, str.length()-1);
		p++;
		}
		
		BadAndDeadState.clear();
		
		return stepsize;
	}
	
	
	static List<String> bs1 = new ArrayList<String>();
	static List<String> es1 = new ArrayList<String>();
	static List<Integer> trans1 = new ArrayList<Integer>();
	static List<String> temp1 =new ArrayList<String>();
	static List<String> DeadState1 = new ArrayList<String>();
	static Set<String> Critical1 = new HashSet<String>();
	static List<String> BadAndDeadState1 = new ArrayList<String>();
	private static int CalculateOptimalStep2(ReachabilityGraphAlgorithm rga, String begin,int stepsize)
			throws CloneNotSupportedException {
		
		int p =0;
		int total = 10000;
		while(p<total){
			
		BadAndDeadState1.clear();
		Critical1.clear();
		
		
		rga.createReachabilityGraph(new StateNode(new LocalGraphParametersListener().parseToArray(begin),1,1),stepsize);

		List<String> deadState1 = rga.deadstate;
		
		if(deadState1!=null){
			for(String ds : deadState1){
				if(!DeadState1.contains(ds))
				DeadState1.add(ds);
			}
		}
//		System.out.println("死锁状态是："+DeadState);
		
		
		BadAndDeadState1.addAll(DeadState1);
		if(DeadState1!=null){
			for(int u=0;u<BadAndDeadState1.size();u++){
				String DS = BadAndDeadState1.get(u);
				//找坏死状态和临界状态
				for(int g =0;g<es1.size();g++){
					if(DS.equals(es1.get(g).trim())){//76
						
						List<String> ls = new ArrayList<String>();
						int c ;
						for(c = 0;c<bs1.size();c++){//bs.get(g) //63 64
							if(bs1.get(g).trim().equals(bs1.get(c).trim())){
								ls.add(es1.get(c).trim());
							}
						}
						
						if(BadAndDeadState1.containsAll(ls)){
							BadAndDeadState1.add(bs1.get(g).trim());
						}else{
							Critical1.add(bs1.get(g).trim());
						}
						
						
					}
					
					
				}
			}
		}
		
		Map<Integer, StateNode> resu = rga.resu;
		
		List<List<Integer>> adjlist = ReachabilityGraphAlgorithm.adjlist;
		
		
		for(int i = 0;i<adjlist.size();i++){
			List<Integer> list = adjlist.get(i);
			if(!list.isEmpty()){
				for(int k=0;k<list.size();k=k+3){
			
					if(!(temp1.contains(resu.get(list.get(k)).toString()))){
						bs1.add(resu.get(list.get(k)).toString());
						es1.add(resu.get(list.get(k+2)).toString());
						trans1.add(list.get(k+1));
					}
				}
			}
		}
		
		
		temp1.clear();
		
		temp1.addAll(bs1);
		
		
		List<String> nextbegin = new ArrayList<String>();
		for(int i =0;i<bs1.size();i++){
			String temp1 = bs1.get(i).trim();
			if(temp1.equals(begin.replace(","," "))&&!BadAndDeadState1.contains(es1.get(i).trim())){
				nextbegin.add(es1.get(i)); 
			}
		}
		
		
		
		int x  = nextbegin.size();
		
		
		
		if(x==0){
			return -1;
		}
		
		
		
		//在这里判断是否会走到死锁状态，如果走到死锁状态会报异常
		String s =nextbegin.get((int) Math.floor(Math.random()*x));
		
		
		String str = s.replace(" ", ",");
		
		begin = str.substring(0, str.length()-1);
		
		p++;
		}
		
		BadAndDeadState1.clear();
		Critical1.clear();
		return stepsize;
}
}
