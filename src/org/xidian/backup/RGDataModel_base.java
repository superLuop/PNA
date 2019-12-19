package org.xidian.backup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.xidian.model.Marking;
import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;
import org.xidian.utils.FileUtil;

/**
 * 可达图（广义）数据模型
 * @author HanChun
 * @version 1.0 version 2016-5-18
 */
public class RGDataModel_base {
	
	public String destPath; //输出路径
	public StateNode rootState; //初始状态
	public static Map<Integer,StateNode> preStatesMap = new HashMap<Integer,StateNode>(1000); //初始化1000个状态

	/**
	 *
	 * @param destPath
	 */
	public RGDataModel_base( String destPath) {
		this.destPath = destPath;
		this.rootState = new StateNode(PetriModel.ininmarking.getMarking(), 1); //初始初始状态
		try {
			createRG();
		} catch (CloneNotSupportedException e) {
			System.err.println("生成可达图失败！");
			e.printStackTrace();
		}
	}
	
//	/**  
//	 * 生成可达图(非递归方法), 输出到文件
//	 * @throws CloneNotSupportedException 
//	 */
//	public void createReachabilityGraph() throws CloneNotSupportedException{
//		preStatesMap = new HashMap<Integer,StateNode>(1000); //初始化1000个状态
//		StringBuffer resultStr = new StringBuffer("可达图分析\n");
//		int stateCount = 1;	//状态数
//		Stack<Integer> nextTrans = new Stack<Integer>(); //当前状态下，能够发射的变迁
//		Queue<StateNode> stateQueue = new LinkedList<StateNode>();  //状态队列
//		Marking temState = null;
//		StateNode currentState = rootState;
//		StateNode duringState = null;  //过程中探索到的状态
//		preStatesMap.put(currentState.hashCode(), currentState);
//		stateQueue.add(rootState); //根状态为起始状态
//		boolean[] canFire = null;
//		while(!stateQueue.isEmpty()) {
//			currentState = stateQueue.poll();	//每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
//			resultStr.append("\nState nr:" + currentState.getStateNo() + "\n"
//					+ printPlaces() + "\n" + "toks: " + currentState + "\n");
//			canFire = getEnabledTrans(currentState);
//			for(int i = 0; i < canFire.length; i++) {
//				if(canFire[i]) nextTrans.push(i);
//			}
//			//死锁状态
//			if(nextTrans.isEmpty()) {
//				currentState.setIfDeadlock(true);
//				resultStr.append("dead state\n");
//			}else{
//				while(!nextTrans.isEmpty()) {
//					resultStr.append("==" + "t" + (nextTrans.peek() + 1) + "==>");
//					temState = fire(currentState, nextTrans.pop());
//					//新状态
//					if(!ifOccured(temState)) {
//						stateCount++;
//						duringState = new StateNode(temState.marking, stateCount);
//						resultStr.append("s"+duringState.getStateNo()+"\n");
//						preStatesMap.put(temState.hashCode(), duringState);
//						stateQueue.add((StateNode) duringState.clone());
//					//旧状态
//					} else {
//						resultStr.append("s"+preStatesMap.get(temState.hashCode()).getStateNo()+"\n");
//					}
//				}
//			}
//		}
//		statesAmout = stateCount;
//		resultStr.append("\n--end--");
//		//System.out.println(resultStr.toString());	//for debug
//		FileUtil.write(destPath, resultStr.toString(), false);
//		initGrapht();
//	}

	/**
	 * 生成可达图(非递归方法), 输出到文件
	 * @throws CloneNotSupportedException 
	 */
	public void createRG() throws CloneNotSupportedException{
		StringBuffer resultStr = new StringBuffer("可达图分析结果如下：\n");
		int stateCount = 1;	//状态数
		Stack<Integer> nextTrans = new Stack<Integer>(); //当前状态下，能够发射的变迁
		Queue<StateNode> stateQueue = new LinkedList<StateNode>();  //状态队列
		Marking temState = null;
		StateNode currentState = rootState;
		StateNode duringState = null;  //过程中探索到的状态
		preStatesMap.put(currentState.hashCode(), currentState);
		stateQueue.add(rootState); //根状态为起始状态
		boolean[] canFire = null;
		while(!stateQueue.isEmpty()) {
			currentState = stateQueue.poll();	//每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
			resultStr.append("\nState nr:" + currentState.getStateNo() + "\n"
					+ printPlaces() + "\n" + "toks: " + currentState + "\n");
			
			//System.out.println( currentState.getStateNo()+":"+currentState);
			canFire = getEnabledTrans(currentState);
			for(int i = 0; i < canFire.length; i++) {
				if(canFire[i]) nextTrans.push(i);
			}
			//死锁状态
			if(nextTrans.isEmpty()) {
				currentState.setIfDeadlock(true);
				resultStr.append("dead state\n");
			}else{
				while(!nextTrans.isEmpty()) {
					resultStr.append("==" + "t" + (nextTrans.peek() + 1) + "==>");
					temState = fire(currentState, nextTrans.pop());
					//新状态
					if(!ifOccured(temState)) {
						stateCount++;
						duringState = new StateNode(temState.marking, stateCount);
						resultStr.append("s"+duringState.getStateNo()+"\n");
					/*	System.out.println(duringState.getStateNo());
						System.out.println("*******************************");*/
						preStatesMap.put(temState.hashCode(), duringState);
						stateQueue.add((StateNode) duringState.clone());
					//旧状态
					}else{
						resultStr.append(preStatesMap.get(temState.hashCode()).getStateNo()+"\n");
					}
				}
			}
		}
		resultStr.append("\n--end--");
		//System.out.println(resultStr.toString());	//for debug
		String path = destPath;
		FileUtil.write(path.substring(0, path.length()-3)+"path", resultStr.toString(), true);
	}
	
	/**
	 * @param transIndex 发射变迁编号
	 * @param currentState 待发射状态
	 * @return  
	 */
	public Marking fire(StateNode currentState, int transIndex) {
	    Marking newMarking = new Marking(PetriModel.placesCount);;  //发射之后的marking
	    for (int i = 0; i < currentState.getState().length; i++) {   
	    	newMarking.marking[i] = currentState.getState()[i] + PetriModel.preMatrix.getValue(i, transIndex) - 
	    			PetriModel.posMatrix.getValue(i, transIndex);	
	    }
	    return newMarking;
	}
		   
	 /**
	  * 得到当前状态下能够发射的变迁
	  * @return boolean[] 
	  */
	 public boolean[] getEnabledTrans(StateNode currentState) {
	      //记录变迁是否能发射结果
	      boolean[] result = new boolean[PetriModel.transCount];  
	      for(int i = 0; i < result.length; i++) {
	    	  result[i] = true;
	      }
	      for (int i = 0; i < PetriModel.transCount ;i++) {
	         for (int j = 0; j < PetriModel.placesCount; j++) {
	        	 //必须指定变迁全部满足
	            if ((currentState.getState()[j] < PetriModel.posMatrix.getValue(j, i))) {  
	               result[i] = false;
	               break;
	            }
	         }
	      }
	      return result;
	   }  
	
	/**
	 * 检查当前状态是否已经发生过
	 * @param node 待检查状态
	 * @return boolean true:发生过
	 */
	public boolean ifOccured(Marking node) {
		if(preStatesMap.containsKey(node.hashCode())){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * p.nr: 1 2 3 4 5 6 7 8
	 * @return
	 */
	public String printPlaces() {
		StringBuffer sb = new StringBuffer();
		sb.append("p.nr: ");
		for(int i = 0; i < PetriModel.placesCount; i++) {
			sb.append((i+1)+" ");
		}
		return sb.toString();
	}

}
