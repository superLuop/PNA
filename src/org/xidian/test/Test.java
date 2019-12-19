package org.xidian.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.xidian.model.PetriModel;

//import org.xidian.utils.PrintUtil;

public class Test {

	static int[][] preMatrix = {{0,1,0,0,0},{0,0,1,1,0},{1,0,0,0,1}};
	static int[][] posMatrix = {{1,0,0,1,0},{0,1,0,0,1},{0,0,1,0,0}};
	static int[] initMarking = {2,0,0,2,2}; 
	static int transCount = 3;
	static int placesCount = 5;
	
	public static void main(String[] args) throws CloneNotSupportedException {
		
		Map<Integer,StateNode> preStatesMap = new HashMap<Integer,StateNode>(200); //初始化200个状态
		StringBuffer resultStr = new StringBuffer();
		int stateCount = 1;	//状态数
		Stack<Integer> nextTrans = new Stack<Integer>(); //当前状态下，能够发射的变迁
		Queue<StateNode> stateQueue = new LinkedList<StateNode>();  //状态队列
		Marking temState = null;
		StateNode currentState = new StateNode(initMarking, 1);
		StateNode duringState = null;  //过程中探索到的状态
		preStatesMap.put(currentState.hashCode(), currentState);
		stateQueue.add(currentState); //根状态为起始状态
		boolean[] canFire = null;
		while(!stateQueue.isEmpty())  {
			currentState = stateQueue.poll(); //每次取出队列中最前面的状态作为当前状态（注意该状态可能不是新状态）
			resultStr.append("\nState nr:" + currentState.getStateNo() + "\n"
					+ printPlaces() + "\n" + "toks: " + currentState + "\n");
			canFire = getEnabledTrans(currentState);
			for(int i = 0; i < canFire.length; i++) {
				if(canFire[i]) {
					nextTrans.push(i);
				}
			}
			//死锁状态
			if(nextTrans.isEmpty()) {
				resultStr.append("Complete Deadlock State\n");
			} else {
				while(!nextTrans.isEmpty()) {
					resultStr.append("==" + "t" + (nextTrans.peek() + 1));
					temState = fire(currentState, nextTrans.pop());
					//新状态
					if(!preStatesMap.containsKey(temState.hashCode())) {
						stateCount++;
						duringState = new StateNode(temState.marking, stateCount);
						resultStr.append("==>" + "s" + duringState.getStateNo() + "\n");
						preStatesMap.put(temState.hashCode(), duringState);
						stateQueue.add((StateNode) duringState.clone());
					//旧状态
					}else{
						resultStr.append("==>" + "s" + ((StateNode) preStatesMap.get(temState.hashCode())).getStateNo() + "\n");
					}
				}
			}
		}
		System.out.println(resultStr.toString());
	}
	
	/**
	 * p.nr: 1 2 3 4 5 6 7 8
	 * @return
	 */
	public static String printPlaces() {
		StringBuffer sb = new StringBuffer();
		sb.append("p.nr: ");
		for(int i = 0; i < PetriModel.placesCount; i++) {
			sb.append((i+1)+" ");
		}
		return sb.toString().trim();
	}
	
	/**
	 * @param transIndex 发射变迁编号
	 * @param currentState 待发射状态
	 * @return  
	 */
	public static Marking fire(StateNode currentState, int transIndex) {
	    Marking newMarking = new Marking(placesCount);  //发射之后的marking
	    for (int i = 0; i < currentState.getState().length; i++) {
	    	newMarking.marking[i] = currentState.getState()[i] + preMatrix[transIndex][i]  
	    			- posMatrix[transIndex][i];	
	    }
	    return newMarking;
	}
		   
	 /**
	  * 得到当前状态下能够发射的变迁
	  * @return boolean[] 
	  */
	 public static boolean[] getEnabledTrans(StateNode currentState) {
	      //记录变迁是否能发射结果
	      boolean[] result = new boolean[transCount];  
	      for(int i = 0; i < result.length; i++) {
	    	  result[i] = true;
	      }
	      for (int i = 0; i < transCount ;i++) {
	         for (int j = 0; j < placesCount; j++) {
	        	 //必须指定变迁全部满足
	            if ((currentState.getState()[j] < posMatrix[i][j])) {  
	               result[i] = false;
	               break;
	            }
	         }
	      }
	      return result;
	   } 
}

class StateNode implements Cloneable{
	
	private int[] state; 
	private int stateNo;  
	
	/**
	 * @param state 状态
	 * @param stateNo 状态编号
	 */
	public StateNode(int[] state, int stateNo) {
		this.state = state;
		this.stateNo = stateNo;
	}
	
	@Override
	 public int hashCode(){
		return  Arrays.hashCode(state);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		 return super.clone();
    }

	@Override
	public String toString() {
		if(this.state == null || this.state.length == 0) {
			return null;
		}
		StringBuffer ab = new StringBuffer();
		for(int i = 0; i < this.state.length; i++) {
			ab.append(this.state[i] + " ");
		}
		return ab.toString();
	}

	public int getStateNo() {
		return stateNo;
	}

	public int[] getState() {
		return state;
	}
}

class Marking {
	
	public int[] marking;
	
	public Marking(int n) {
		marking = new int[n];
	}
	
	public Marking(int[] marking) {
		this.marking = marking;
	}
	
	public int[] getMarking() {
		return marking;
	}

	/**
	  * hashCode()
	  * 计算hashcode，用来比较两个对象是否相等
	  */
	@Override
	 public int hashCode(){
		return  Arrays.hashCode(marking);
	}

	@Override
	public String toString() {
		String result = "";
		for(int i = 0; i < marking.length; i++) {
			if(i != marking.length - 1) {
				result += (marking[i]+",");
			} else {
				result += marking[i];
			}
		}
		return result;
	}

}
