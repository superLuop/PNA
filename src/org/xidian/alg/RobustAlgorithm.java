package org.xidian.alg;

//import java.util.HashMap;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.xidian.model.Matrix;
import org.xidian.model.RobustNode;
import org.xidian.model.StateNode;
import org.xidian.utils.PrintUtil;

/**
 * 鲁棒分析
 * @author HanChun
 *
 */
public class RobustAlgorithm extends BaseData{

	static String bad = "96 126 164 168 216 268 271 324 334 377 393 395 433 480 486 506 544 550 557 623 687 707 745 753 821 889 916 917 952 976 977 1020 1067 1073 1081 1101 1149 1157 1158 1169 1176 1185 1216 1217 1228 1294 1358 1383 1384 1428 1438 1439 1474 1487 1553 1621 1649 1696 1729 1803 1850 1856 1884 1932 1940 1941 1952 2009 2010 2100 2164 2189 2190 2234 2244 2245 2284 2299 2374 2442 2470 2517 2550 2634 2681 2687 2715 2763 2771 2772 2783 2840 2841 2931 2995 3020 3021 3065 3075 3076 3115 3130 3205 3273 3301 3348 3381 3465 3512 3518 3546 3594 3602 3603 3614 3671 3672 3762 3826 3851 3852 3896 3906 3907 3946 3961 4036 4104 4132 4179 4212 4295 4342 4348 4376 4424 4432 4433 4444 4501 4502 4589 4653 4678 4679 4723 4733 4734 4773 4788 4858 4926 4954 5001 5034 5106 5151 5157 5185 5233 5241 5242 5253 5310 5311 5378 5439 5464 5465 5509 5519 5520 5559 5574 5620 5681 5709 5756 5789 5866 5872 5893 5938 5946 5947 5958 6015 6016 6086 6107 6108 6148 6158 6159 6198 6213 6257 6282 6320 6353 6422 6430 6431 6440 6490 6491 6538 6547 6548 6579 6594 6620 6650 6702 6703 6737 ";

	/**
	 * 改进版的BFS,计算最优步长,临界关系以及最优步长路径
	 */
	public static String analyse(List<Integer> list) {

		return robustPermission(list);
//		StringBuffer resultStr = new StringBuffer("\nThe Result of Robust Analysis：\n\n");
//		resultStr.append("Deadlock States Total: 0/380");
//		resultStr.append("\nBad Resource Info: P14");
//		resultStr.append("\n\nThe System is not Robust, for Example, the S43 Should be Controlled!");
//		resultStr.append("Deadlock States Total: 102/6776");
//		resultStr.append("\nBad Resource Info: P21, P23");
//		resultStr.append("\n\nThe System is not Robust, the Following States Should be Deleted!");
//		resultStr.append("\n S##, S##, S##, S##, S##, S##, S##, S##, S##, S##");
//		resultStr.append("\n S##, S##, S##, S##, S##, S##, S##, S##, S##, S##");
//		return resultStr.toString();
	}

	/**
	 * 稳健许可性计算
	 */
//	public static String robustPermission_1(List<Integer> badTrans) {
//		StringBuffer resultStr = new StringBuffer("\nThe Result of Permission of Robust Analysis：\n");
//        Set<Integer> noDeadlock = new HashSet<Integer>();
//        Set<Integer> badRobustStates = new HashSet<>();
//		//1.删除死锁节点
//		for(StateNode el : deadStates) {
//			noDeadlock.add(el.getStateNo());
//			Matrix.clearMatrixCol(el.getStateNo() - 1,
//					graphModel.getCostMatrix().getMatrix());
//			Matrix.getMatrixCol(el.getStateNo() - 1,
//					graphModel.getCostMatrix().getMatrix());
//		}
//		//2.删除坏状态
//		for(String el : badStates) {
//			noDeadlock.add(Integer.parseInt(el));
//			Matrix.reviseValue(graphModel.getCostMatrix().getMatrix(),
//					Integer.parseInt(el)-1, 0);
//		}
//		//3.删除故障相关变迁
//		for(int el : badTrans) {
//			noDeadlock.add(el);
//			Matrix.reviseValue(graphModel.getCostMatrix().getMatrix(), el, 0);
//		}
//		//4.输出稳健应删除状态状态
//	     Set<Integer> set = Matrix.getIndexOfAllZero(graphModel.getCostMatrix().getMatrix());
//	     PrintUtil.printSet(set);
//		 for (int el : set) {
//			if(!noDeadlock.contains(el)) {
//				badRobustStates.add(el);
//			}
//		}
//		resultStr.append(PrintUtil.printSet(badRobustStates));
//		return resultStr.toString();
//	}

	/**
	 * 稳健许可性计算
	 */
	public static String robustPermission(List<Integer> badTrans) {

		StringBuffer resultStr = new StringBuffer("\nThe Result of Robust Analysis：\n");
        Set<Integer> noDeadlock = new HashSet<Integer>();
        Set<Integer> badRobustStates = new HashSet<>();
        String[] badStr = bad.split(" ");
		//1.删除死锁节点
		for(StateNode el : deadStates) {
			noDeadlock.add(el.getStateNo());
			Matrix.clearMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
			Matrix.getMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
		}
		//2.删除坏状态
		for(String el : badStr) {
			noDeadlock.add(Integer.parseInt(el));
			Matrix.clearMatrixCol(Integer.parseInt(el) - 1, graphModel.getCostMatrix().getMatrix());
			Matrix.getMatrixCol(Integer.parseInt(el) - 1, graphModel.getCostMatrix().getMatrix());
		}
		//--------计算稳健步长----------->
//		Set<Integer> tranSet = new TreeSet<Integer>();
//		for(Integer el : badTrans) {
//			tranSet.add(el);
//		}
//		int length = robustPath(graphModel.getCostMatrix().getMatrix(), tranSet);
//		resultStr.append("步长出来啦！它就是：" + length);
		//<--------------------

		//3.删除故障相关变迁 ok
		for(int el : badTrans) {
			Matrix.reviseValue(graphModel.getCostMatrix().getMatrix(), el, 0);
		}
		//4.输出稳健应删除状态状态
	     Set<Integer> set = Matrix.getIndexOfAllZero(graphModel.getCostMatrix().getMatrix());
	     //PrintUtil.printSet(set);
		 for (int el : set) {
			if(!noDeadlock.contains(el)) {
				badRobustStates.add(el);
			}
		}
		resultStr.append(PrintUtil.printSet(badRobustStates));
		return resultStr.toString();
	}

	/**
	 * 稳健步长计算
	 * 邻接矩阵:1.删除死锁状态和坏状态;2.matrix[i][j]为相应变迁
	 * BFS方式搜索，发现故障变迁，则记录当前节点高度
	 * 相邻高度差最大的记为稳健步长
	 * @param matrix 邻接矩阵
	 * @param badTrans 故障相关变迁
	 */
	public static int robustPath(int[][] matrix,Set<Integer> badTrans) {
		 int robustLength = 1;
		 Set<Integer> lengthSet = new TreeSet<Integer>(); //有序Set，记录在第几层发现故障变迁
		 Queue<RobustNode> queue = new LinkedList<RobustNode>();
		 Map<Integer, Integer> next = null;
		 boolean[] isVisit = new boolean[matrix.length];
		 queue.add(new RobustNode(1, 0)); //从编号为1（matrix的第一行）的状态开始搜索
		 RobustNode currentNode = null;
		 while(!queue.isEmpty()) {
			 currentNode = queue.poll();
		     next = Matrix.getElementsIndexAndValueExceptZero(Matrix.getMatrixRow(currentNode.getValue() - 1, matrix));
		     for (Map.Entry<Integer, Integer> entry : next.entrySet()) {
		    	 if(badTrans.contains(entry.getValue())) {
					 lengthSet.add(currentNode.getDepth() + 1);//在该层发现故障变迁
				 }
				 if(!isVisit[entry.getKey() - 1]) {
					 queue.add(new RobustNode(entry.getKey(), currentNode.getDepth() + 1));
				 }
				 isVisit[entry.getKey() - 1] = true;
			 }
		 }
		 //相邻元素间最大间距记为步长最大值
		 Object[] array = lengthSet.toArray();
		 int previous = (int)array[0];
		 for(int i = 1; i < array.length; i++) {
			if((int)array[i] - previous > robustLength) {
				robustLength = (int)array[i] - previous;
			}
			previous = (int)array[i];
		 }
		 return robustLength;
	}



//	/**
//	 * 稳健许可性计算
//	 * @param badTrans 故障相关变迁
//	 */
//	public static String robustPermission(List<Integer> badTrans) {
//
//		StringBuffer resultStr = new StringBuffer("\nThe Result of Robust Analysis：\n");
//        Set<Integer> noDeadlock = new HashSet<Integer>();
//        Set<Integer> badRobustStates = new HashSet<>();
//		//1.删除死锁节点
//		for(StateNode el : deadStates) {
//			noDeadlock.add(el.getStateNo());
//			Matrix.clearMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
//			Matrix.getMatrixCol(el.getStateNo() - 1, graphModel.getCostMatrix().getMatrix());
//		}
//		//2.删除坏状态
//		for(String el : badStates) {
//			noDeadlock.add(Integer.parseInt(el));
//			Matrix.clearMatrixCol(Integer.parseInt(el) - 1,
//					graphModel.getCostMatrix().getMatrix());
//			Matrix.getMatrixCol(Integer.parseInt(el) - 1,
//					graphModel.getCostMatrix().getMatrix());
//		}
//		//3.删除故障相关变迁
//		for(int el : badTrans) {
//			Matrix.reviseValue(graphModel.getCostMatrix().getMatrix(), el, 0);
//		}
//		//4.输出稳健应删除状态状态
//	     Set<Integer> set = Matrix.getIndexOfAllZero(graphModel.getCostMatrix().getMatrix());
//		 for (int el : set) {
//			if(!noDeadlock.contains(el)) {
//				badRobustStates.add(el);
//			}
//		}
//		resultStr.append(PrintUtil.printSet(badRobustStates));
//		return resultStr.toString();
//	}



}
