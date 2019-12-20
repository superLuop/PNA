package org.xidian.utils;

//import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.xidian.model.PetriModel;
import org.xidian.model.StateNode;

public class PrintUtil {
	
	

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
	 * 输出集合，每行20个
	 * @param s
	 * @return
	 */
	public static String printSet(Set<Integer> s) {
		int flag = 1;
		StringBuffer tem = new StringBuffer();
		StringBuffer tem3 = new StringBuffer();
		tem.append("total " + s.size() + "\n");
		for(Integer i : s) {
			if(flag % 20 == 0) {
				tem.append("\n");
			}
//			tem.append(i+" ");
			tem3.append(i+" ");
			flag++;
		}
		return tem3.toString();
	}
	
	/**
	 * 输出列表，每行30个
	 * @param s
	 * @return
	 */
	public static String printList(List<StateNode> s) {
		int flag = 1;
//		StringBuffer tem = new StringBuffer();
		
		StringBuffer tem2 = new StringBuffer();
		
//		tem.append("total " + s.size() + "\n");
		if (s != null && (s.size() > 0)){
			for(StateNode el : s) {
//				if(flag % 30 == 0) {
//					tem.append("\n");
//				}
//				tem.append(el.getStateNo()+" ");
				tem2.append(el.getStateNo()+" ");
				flag++;
			}
		}
		return tem2.toString();
	}
	
}
