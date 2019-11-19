package org.xidian.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Set;

import org.xidian.model.Matrix;
import org.xidian.model.StateNode;
import org.xidian.utils.LoadModelUtil;
import org.xidian.utils.PrintUtil;
/**
 * @Description 含有不可靠资源的状态分析算法（鲁棒性分析）
 * @author PLuo;
 * @CreateDate [2018-7-31]
 */
public class UnreliableAlgorithm extends BaseData{
       static int[][] StateShift;
       //static Map<Integer,String> ifreliable = null;
       static LinkedList<Integer> originalBadState = null;
       static LinkedList<Integer> badState = null;
       static Set<Integer> badTrans = null;
       static LinkedList<Integer> up = null;
       //static LinkedList<Integer> robustStates = null;
       static ReachabilityGraphAlgorithm rg = null;
       //存储含有不可靠资源时的状态,输出结果
   	   static StringBuffer sb = null;
   	   static StringBuffer stateResult = null;
   	   
   	   static LinkedList<Integer> criticalState = null;
   	   static LinkedList<Integer> deadStates1 = null;
   	   static Set<Integer> badRobustStates = null;
       static Set<Integer> badAnddeadStates = null;
       static Map<Integer,StateNode> allStatesMap = null; //所有状态集合
       
   	
public static String check(){
   		//sb = new StringBuffer();
   		stateResult = new StringBuffer();
	    try {
	  		rg = new ReachabilityGraphAlgorithm();
	  		rg.createReachabilityGraph(null, 0);
	  	} catch (CloneNotSupportedException e) {
	  		e.printStackTrace();
	  	}
   		
   	    StepAlgorithm.analyse();
   	    allStatesMap = new HashMap<Integer,StateNode>();
   	    allStatesMap = rg.resu;
//   	    for (Integer key : allStatesMap.keySet()) {
//   	    	stateResult.append("CurrentStateNo:"+key+"==>"+"Marking:"+allStatesMap.get(key)+"\n");//输出所有状态集合
//		}
   	    
		badTrans = LoadModelUtil.badTrans;
//		stateResult.append("The bad transitions are：");
		List<Integer> badTranList = new ArrayList<>(badTrans);
//	 	for(int m = 0;m<list.size();m++){  
//			stateResult.append(list.get(m)+" ");
//		}
	 		
	 	
	 	
		//原来的坏死状态
	 	StringBuffer badAnddeadState1 = StepAlgorithm.badAnddeadState2;
	 	
	 	if(!"".equals(badAnddeadState1.toString())&&(badAnddeadState1.toString()!=null)){
	 		
	 		String[] badAnddeadState = badAnddeadState1.toString().split(" ");
	 		originalBadState = new LinkedList<Integer>();
	 		badAnddeadStates = new HashSet<Integer>();
	 		for(int g =0;g<badAnddeadState.length;g++){
	    	    badAnddeadStates.add(Integer.parseInt(badAnddeadState[g].trim()));
	    	    originalBadState.add(Integer.parseInt(badAnddeadState[g].trim()));
	 		}
	       
	 	//死锁状态
	  	String deadState1 = StepAlgorithm.deadState;
	  	String[] deadState2 = deadState1.trim().split(" ");
	  	deadStates1 = new LinkedList<Integer>();
		for(int d = 0;d<deadState2.length;d++){
			deadStates1.add(Integer.parseInt(deadState2[d].trim()));
		}
	 	
	    //状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
    	 	List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;
    	 	
    	 	int totalsize = adj.size()+deadState2.length;
    	 	StateShift = new int[totalsize+1][totalsize+1];
    		
    		for(int i = 0;i<adj.size();i++){
    			List<Integer> list = adj.get(i);
    			if(!list.isEmpty()){
    				for(int k=0;k<list.size();k=k+3){
    					addEdge(list.get(k),list.get(k+2),list.get(k+1));
    				}
    			}
    		}
    		
    		originalBadState.removeAll(deadStates1);
    		
    		UnreliableAlgorithm.robustPermission(badTranList);
//    	 	System.out.println(badAnddeadStates);
	 	
	 	//临界状态
	     String criticalState1 = StepAlgorithm.Criticals;
	     String[] criticalState2 = criticalState1.trim().split(" ");
	     criticalState = new LinkedList<Integer>();
	     for(int j =0;j<criticalState2.length;j++){
	     	 criticalState.add(Integer.parseInt(criticalState2[j].trim()));
	     		}
	     for(int n:badAnddeadStates){
	    	 for(int m = 0;m<StateShift.length;m++){
					if(StateShift[m][n]>0&&badAnddeadStates.contains(n)&&!badAnddeadStates.contains(m)&&!criticalState.contains(m)){
						criticalState.add(m);
					}
			 }
		}
	     
	    criticalState.removeAll(badAnddeadStates);
//	 	System.out.println(criticalState);

	    stateResult.append("\nThe Result of Robust Analysis：");
	    stateResult.append("\n\nThe places of unreliable resource are：");
	    up = LoadModelUtil.up;
		for(int i = 0;i < up.size();i++) {
			stateResult.append("P"+up.get(i)+" ");
		}
	    
	 	//所有状态数
        int totalstate = ReachabilityGraphAlgorithm.statesAmout;
        stateResult.append("\n\nTotal number of states："+totalstate);
        
       //List<Integer> states = new ArrayList<>();
 	 	//输出所有好状态
        stateResult.append("\n\nThe count of good states："+(totalstate-badAnddeadStates.size()-criticalState.size())+"\n");
    	 stateResult.append("The good states are：");
    	 for(int n= 1;n<=totalstate;n++){
    		if(!badAnddeadStates.contains(n) && !criticalState.contains(n)){
    			stateResult.append(n+" ");
    			}
    	}
    	 
      //输出所有临界状态
    	 stateResult.append("\n\nThe count of critical states："+criticalState.size());     
         stateResult.append("\nThe critical states are：");
         for(int t = 0;t<criticalState.size();t++){
        	 stateResult.append(criticalState.get(t)+" ");
         }  
    	 
      //输出所有坏状态 
        badState = new LinkedList<Integer>();
        badState.addAll(badAnddeadStates);  
	    badState.removeAll(deadStates1);
        stateResult.append("\n\nThe count of bad states："+badState.size());     
        stateResult.append("\nThe bad states are：");
        for(int t = 0;t<badState.size();t++){
       	 stateResult.append(badState.get(t)+" ");
        }

        
      //由不可靠资源导致的不稳健状态 
        stateResult.append("\n\nThe count of bad robust states："+badRobustStates.size());     
        stateResult.append("\nThe bad robust states are：");
        stateResult.append(PrintUtil.printSet(badRobustStates));
        printBadRobust(badRobustStates);
        
        //输出所有死锁状态
        stateResult.append("\n\nThe count of deadlock states："+deadStates1.size());     
        stateResult.append("\nThe deadlock states are：");
        for(int t = 0;t<deadStates1.size();t++){
       	 stateResult.append(deadStates1.get(t)+" ");
        }
	}else {
		//原来不含有坏死状态，即原网无死锁
		stateResult.append("\nThe original Petri net has no deadlock,but due to the existence of unreliable resources:");
		up = LoadModelUtil.up;
		for(int i = 0;i < up.size();i++) {
			stateResult.append("P"+up.get(i)+" ");
		}
		stateResult.append("\nThe system is not robust,the details are as follows:");

		originalBadState = new LinkedList<Integer>();
		badAnddeadStates = new HashSet<Integer>();
		UnreliableAlgorithm.robustPermission(badTranList);
//	 	System.out.println(badAnddeadStates);
		
		//状态的改变（一个状态在某一变迁的发射下到达另一状态的过程）
	 	List<List<Integer>> adj = ReachabilityGraphAlgorithm.adjlist;
	 	
	 	int totalsize = adj.size();
	 	StateShift = new int[totalsize+1][totalsize+1];
		
		for(int i = 0;i<adj.size();i++){
			List<Integer> list = adj.get(i);
			if(!list.isEmpty()){
				for(int k=0;k<list.size();k=k+3){
					addEdge(list.get(k),list.get(k+2),list.get(k+1));
				}
			}
		}
		
		//临界状态
	     criticalState = new LinkedList<Integer>();
	     for(int n:badAnddeadStates){
	    	 for(int m = 0;m<StateShift.length;m++){
					if(StateShift[m][n]>0&&badAnddeadStates.contains(n)&&!badAnddeadStates.contains(m)&&!criticalState.contains(m)){
						criticalState.add(m);
					}
			 }
		}
	     
//	    criticalState.removeAll(badAnddeadStates);
//	 	System.out.println(criticalState);

	 	//所有状态数
       int totalstate = ReachabilityGraphAlgorithm.statesAmout;
       stateResult.append("\n\nTotal number of states："+totalstate);
       
      //List<Integer> states = new ArrayList<>();
	 	//输出所有好状态
       stateResult.append("\n\nThe count of good states："+(totalstate-badAnddeadStates.size()-criticalState.size())+"\n");
   	 	stateResult.append("The good states are：");
   	 	for(int n= 1;n<=totalstate;n++){
   	 		if(!badAnddeadStates.contains(n) && !criticalState.contains(n)){
   	 			stateResult.append(n+" ");
   			}
   		}
   	 
     //输出所有临界状态
   	 stateResult.append("\n\nThe count of critical states："+criticalState.size());     
        stateResult.append("\nThe critical states are：");
        for(int t = 0;t<criticalState.size();t++){
       	 stateResult.append(criticalState.get(t)+" ");
        }  

     //由不可靠资源导致的不稳健状态 
       stateResult.append("\n\nThe count of bad robust states："+badRobustStates.size());     
       stateResult.append("\nThe bad robust states are：");
       stateResult.append(PrintUtil.printSet(badRobustStates));
       printBadRobust(badRobustStates);
	}
        
   		return stateResult.toString();	    
}
private static void printBadRobust(Set<Integer> badRobustStates) {
	for (Integer br : badRobustStates) {
		for (Integer key : allStatesMap.keySet()) {
	    	if (key.equals(br)) {
	    		stateResult.append("\nStateNo:"+key+"==>"+"Marking:"+allStatesMap.get(key)+"\n");
			}
		}
	}
}	  	
      /**
        * 稳健许可性计算
        */
      public static String robustPermission(List<Integer> badTrans) {
              
              Set<Integer> noDeadlock = new HashSet<Integer>();
              badRobustStates = new HashSet<>();

               
      	    if((!originalBadState.isEmpty() && originalBadState != null) && !deadStates.isEmpty()) {
      	    	
//                System.out.println(originalBadState);
//                System.out.println(deadStates);
                
          		
                  //1.删除死锁状态
                  for(StateNode el : deadStates) {
    	               noDeadlock.add(el.getStateNo());
    	               Matrix.clearMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
    	               Matrix.getMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
                  }
                 //2.删除坏状态
                 for(int el : originalBadState) {
    	              noDeadlock.add(el);
    	              Matrix.clearMatrixCol(el - 1, graphModel.getCostMatrix().getMatrix());
    	              Matrix.getMatrixCol(el - 1, graphModel.getCostMatrix().getMatrix());
                 }
      	    }

             //3.删除故障相关变迁 
             for(int el : badTrans) {
	              Matrix.reviseValue(graphModel.getCostMatrix().getMatrix(), el, 0);
             }
            //4.不稳健的状态
             Set<Integer> set = Matrix.getIndexOfAllZero(graphModel.getCostMatrix().getMatrix());
                  for (int el : set) {
	                 if(!badAnddeadStates.contains(el)) {
		                  badRobustStates.add(el);
	                  }
                  }
              badAnddeadStates.addAll(badRobustStates);
              
             return stateResult.toString();
 }
      
	public static void addEdge(int start, int end, int weight) {
	
		StateShift[start][end] = weight;
	
	}
	
}
